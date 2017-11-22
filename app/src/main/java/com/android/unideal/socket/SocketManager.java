package com.android.unideal.socket;

import android.text.TextUtils;
import android.util.Log;
import com.android.unideal.BuildConfig;
import com.android.unideal.UniDealApplication;
import com.android.unideal.chatting.viewmodel.ChatPresenter;
import com.android.unideal.data.socket.Message;
import com.android.unideal.notification.MsgNotification;
import com.android.unideal.notification.NotificationHelper;
import com.android.unideal.util.AppBuildConfig;
import com.android.unideal.util.SessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SocketManager {

  private static final String TAG = "UniDeal#SocketManager";
  private static SocketManager mSocketManager;
  private static boolean isConnected;
  private static boolean isAuthSuccess;

  private Socket mSocket;
  private Map<String, SocketListener> listenerMap = new HashMap<>();
  private int userId;
  private Emitter.Listener onConnect = new Emitter.Listener() {
    @Override
    public void call(Object... args) {
      // socket connection listener
      consoleLog("SocketService#onConnect");
      for (SocketListener listener : listenerMap.values()) {
        listener.onSocketConnected();
      }
      isConnected = true;
    }
  };
  private Emitter.Listener onConnectionTimeOut = new Emitter.Listener() {
    @Override
    public void call(Object... args) {
      // socket connection listener
      isConnected = false;
      isAuthSuccess = false;
      consoleLog("SocketService#onConnectionTimeOut");
    }
  };
  private Emitter.Listener onConnectionError = new Emitter.Listener() {
    @Override
    public void call(Object... args) {
      // socket connection listener
      isConnected = false;
      isAuthSuccess = false;
      consoleLog(String.format("SocketService#onConnectionError#%s", args));
      for (SocketListener listener : listenerMap.values()) {
        listener.onSocketError(args);
      }
    }
  };
  private Emitter.Listener onDisconnect = new Emitter.Listener() {
    @Override
    public void call(Object... args) {
      // Socket connection error listener
      isConnected = false;
      isAuthSuccess = false;
      consoleLog(String.format("SocketService#onDisconnect#%s", args));
      for (SocketListener listener : listenerMap.values()) {
        listener.onSocketError(args);
      }
    }
  };

  private Emitter.Listener onUnreadCount = new Emitter.Listener() {
    @Override
    public void call(Object... args) {
      consoleLog(String.format("SocketService#onUnreadCount%s ", Arrays.toString(args)));
      for (SocketListener listener : listenerMap.values()) {
        listener.onUnreadCount(args);
      }
    }
  };
  private Emitter.Listener onReconnect = new Emitter.Listener() {
    @Override
    public void call(Object... args) {
      // Socket connection error listener
      isConnected = false;
      isAuthSuccess = false;
      consoleLog(String.format("SocketService#onReconnect#%s", args));
    }
  };
  private Emitter.Listener onReconnectError = new Emitter.Listener() {
    @Override
    public void call(Object... args) {
      // Socket connection error listener
      isConnected = false;
      isAuthSuccess = false;
      consoleLog(String.format("SocketService#onReconnect#Error%s ", args));
    }
  };
  private Emitter.Listener onEventError = new Emitter.Listener() {
    @Override
    public void call(Object... args) {
      // Socket connection error listener
      isConnected = false;
      isAuthSuccess = false;
      consoleLog(String.format("SocketService#onEventError%s ", args));
      for (SocketListener listener : listenerMap.values()) {
        listener.onSocketError(args);
      }
    }
  };
  /**
   * It's listen the list of messages between agent and requester
   */
  private Emitter.Listener onMessagesListReceived = new Emitter.Listener() {

    @Override
    public void call(Object... args) {
      consoleLog(String.format("SocketService#onMessagesListReceived%s ", args));
      for (SocketListener listener : listenerMap.values()) {
        listener.onMessagesListReceived(args);
      }
    }
  };

  /**
   * It's listen the list of messages between agent and requester
   */
  private Emitter.Listener onUpdateMessages = new Emitter.Listener() {

    @Override
    public void call(Object... args) {
      consoleLog(String.format("SocketService#onUpdateMessages%s ", Arrays.toString(args)));
      for (SocketListener listener : listenerMap.values()) {
        listener.onUpdateMessages(args);
      }
    }
  };

  private Emitter.Listener onStatusReceived = new Emitter.Listener() {

    @Override
    public void call(Object... args) {
      consoleLog(String.format("SocketService#onStatusReceived%s ", Arrays.toString(args)));
      for (SocketListener listener : listenerMap.values()) {
        listener.onStatusReceive(args);
      }
    }
  };

  private Emitter.Listener onReceivedMessage = new Emitter.Listener() {

    @Override
    public void call(Object... args) {
      consoleLog(String.format("SocketService#onReceivedMessage%s ", Arrays.toString(args)));
      for (SocketListener listener : listenerMap.values()) {
        listener.onReceivedMessage(args);
      }
    }
  };
  private Emitter.Listener onReadMessage = new Emitter.Listener() {

    @Override
    public void call(Object... args) {
      consoleLog(String.format("SocketService#onReadMessage%s ", Arrays.toString(args)));
      for (SocketListener listener : listenerMap.values()) {
        listener.onReadMessage(args);
      }
    }
  };

  private Emitter.Listener onNewAgentMessages = new Emitter.Listener() {

    @Override
    public void call(Object... args) {
      consoleLog(String.format("SocketService#onNewAgentMessages%s ", Arrays.toString(args)));
      for (SocketListener listener : listenerMap.values()) {
        listener.onNewAgentMessages(args);
      }
    }
  };

  private Emitter.Listener onMsgReceive = new Emitter.Listener() {
    @Override
    public void call(Object... args) {
      consoleLog(String.format("SocketService#onMsgReceive%s ", args));
      ObjectMapper mapper = new ObjectMapper();
      Message message;
      try {
        message = mapper.readValue((String) args[0], Message.class);
        if (message != null) {
          int status = Message.STATUS_SEND;
          for (SocketListener listener : listenerMap.values()) {
            listener.onMessageReceive(message);
            if (status == Message.STATUS_SEND && listener instanceof ChatPresenter) {
              String threadId = ((ChatPresenter) listener).getThreadId();
              if (!TextUtils.isEmpty(threadId) && threadId.equals(message.getThread_id())) {
                status = Message.STATUS_SEEN;
              }
            }
          }
          if (message.getSender_id() != userId) {
            sendMessageStatus(message, status);
            if (status != Message.STATUS_SEEN) {
              showNotification(message);
            }
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  };
  private Emitter.Listener onHistoryReceive = new Emitter.Listener() {
    @Override
    public void call(Object... args) {
      consoleLog(String.format("SocketService#onHistoryReceive%s ", args));
      for (SocketListener listener : listenerMap.values()) {
        listener.onHistoryReceive(args);
      }
    }
  };
  private Emitter.Listener onAuthSuccess = new Emitter.Listener() {
    @Override
    public void call(Object... args) {
      consoleLog(String.format("SocketService#onAuthSuccess%s ", args));
      isAuthSuccess = true;
      for (SocketListener listener : listenerMap.values()) {
        listener.onAuthSuccess(args);
      }
    }
  };

  private SocketManager(int userId) {
    this.userId = userId;
  }

  public static SocketManager getInstance() {
    if (mSocketManager == null) {
      int userId = SessionManager.get(UniDealApplication.getApplication()).getUserId();
      mSocketManager = new SocketManager(userId);
    }
    return mSocketManager;
  }

  private void showNotification(Message message) {
    MsgNotification notification = new MsgNotification();
    notification.setText(message.getMediaData().getText());
    notification.setAttachment(message.getMediaData().getImageUrl());
    notification.setJob_id(message.getJob_id());
    notification.setMsg_thread_id(message.getThread_id());
    notification.setMsg_user_id(message.getReceiver_id());
    notification.setMsg_user_name(message.getName());
    notification.setMsg_user_profile_url(message.getProfile_pic());
    NotificationHelper.showNotification(UniDealApplication.getApplication(), notification);
  }

  private void sendMessageStatus(Message message, int status) {
    if (message == null) {
      return;
    }
    emitEvent(SocketConst.EVENT_SEND_STATUS, message.getSender_id(), message.getReceiver_id(),
        status, message.getMessage_id());
  }

  public void setSocketListener(SocketListener mSocketListener) {
    listenerMap.put(mSocketListener.getClass().getName(), mSocketListener);
  }

  public void removeListener(SocketListener mSocketListener) {
    listenerMap.remove(mSocketListener.getClass().getName());
  }

  private String rootHostURL() {
    StringBuilder hostBuilder = new StringBuilder(AppBuildConfig.getSocketHostURL());
    consoleLog(hostBuilder.toString());
    return hostBuilder.toString();
  }

  public void openSocket() {
    try {
      IO.Options options = new IO.Options();
      options.forceNew = true;
      mSocket = IO.socket(rootHostURL(), options);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public void listenEvents() {
    mSocket.on(Socket.EVENT_CONNECT, onConnect);
    mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectionTimeOut);
    mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectionError);
    mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
    mSocket.on(Socket.EVENT_RECONNECT, onReconnect);
    mSocket.on(Socket.EVENT_RECONNECT_ERROR, onReconnectError);
    mSocket.on(Socket.EVENT_ERROR, onEventError);

    //custom channel listen
    mSocket.on(SocketConst.EVENT_ON_MESSAGES_LIST_RECEIVED, onMessagesListReceived);
    mSocket.on(SocketConst.EVENT_ON_MSG_RECEIVED, onMsgReceive);
    mSocket.on(SocketConst.EVENT_ON_RECEIVE_HISTORY, onHistoryReceive);
    mSocket.on(SocketConst.EVENT_ON_AUTH_SUCCESS, onAuthSuccess);
    mSocket.on(SocketConst.EVENT_ON_UPDATE_MESSAGES, onUpdateMessages);
    mSocket.on(SocketConst.EVENT_RECEIVED_STATUS, onStatusReceived);

    mSocket.on(SocketConst.EVENT_ON_RECEIVED_MESSAGE, onReceivedMessage);
    mSocket.on(SocketConst.EVENT_ON_READ_MESSAGE, onReadMessage);
    mSocket.on(SocketConst.EVENT_ON_NEW_AGENT_MSG, onNewAgentMessages);

    mSocket.on(SocketConst.EVENT_ON_UNREAD_COUNT, onUnreadCount);
  }

  public SocketManager connect() {
    if (mSocket == null) {
      openSocket();
      mSocket.connect();
    } else {
      mSocket.connect();
    }
    return mSocketManager;
  }

  public boolean isConnected() {
    return mSocket != null && isConnected;
  }

  public boolean isAuthSuccess() {
    return mSocket != null && isAuthSuccess;
  }

  public void off() {
    mSocket.off();
  }

  public void disconnect() {
    isAuthSuccess = false;
    isConnected = false;
    mSocket.off();
    mSocket.close();
    mSocket.disconnect();
  }

  public SocketManager emitEvent(final String event, final Object[] args, final Ack ack) {
    mSocket.emit(event, args, ack);
    return mSocketManager;
  }

  public SocketManager emitEvent(final String event, final Object... args) {
    consoleLog(String.format("SocketManager#Event:[%s], Param:[%s]", event, Arrays.toString(args)));
    mSocket.emit(event, args);
    return mSocketManager;
  }

  private void consoleLog(String logMsg) {
    if (BuildConfig.DEBUG) Log.d(TAG, logMsg);
  }
}

