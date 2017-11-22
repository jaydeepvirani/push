package com.android.unideal.chatting.viewmodel;

import android.text.TextUtils;
import com.android.unideal.data.socket.History;
import com.android.unideal.data.socket.MediaData;
import com.android.unideal.data.socket.Message;
import com.android.unideal.data.socket.MessageStatus;
import com.android.unideal.data.socket.StatusResponse;
import com.android.unideal.rest.CallbackWrapper;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.RestUtils;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import com.android.unideal.socket.SocketConst;
import com.android.unideal.socket.SocketListener;
import com.android.unideal.socket.SocketManager;
import com.android.unideal.util.converter.JacksonConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import okhttp3.RequestBody;
import retrofit2.Call;

/** Created by CS02 on 12/7/2016. */
public class ChatPresenter implements SocketListener {
  private IChatView view;
  private SocketManager socketManager;
  private String threadId;
  private int userId;
  private int user_type;

  public ChatPresenter(IChatView view, String threadId, int userId, int user_type) {
    this.view = view;
    this.userId = userId;
    this.user_type = user_type;
    this.threadId = threadId;
  }

  public void initSocket() {
    socketManager = SocketManager.getInstance();
    socketManager.setSocketListener(this);
    if (socketManager.isConnected()) {
      if (socketManager.isAuthSuccess()) {
        view.onAuthSuccess();
      } else {
        onSocketConnected();
      }
    } else {
      socketManager.openSocket();
      socketManager.listenEvents();
      socketManager.connect();
    }
  }

  public void removeListener() {
    //        socketManager.off();
    socketManager.removeListener(this);
  }

  public String getThreadId() {
    return threadId;
  }

  public void disconnectSocket() {
    socketManager.disconnect();
  }

  public void sendMessage(Message message) {
    message.setThread_id(threadId);
    socketManager.emitEvent(SocketConst.EVENT_MSG_SEND,
        JacksonConverter.getStringFromObject(message));
  }

  public void uploadAttachment(String filePath) {
    HashMap<String, RequestBody> params = new HashMap<>();
    if (TextUtils.isEmpty(filePath)) {
      params.put(RestFields.KEY_FILE_PATH, RestUtils.TypedString(""));
    } else {
      File profilePicture = new File(filePath);
      params.put(RestFields.KEY_FILE_PATH + "\"; filename=\"" + profilePicture.getName() + "\"",
          RestUtils.TypedImageFile(profilePicture));
    }
    view.showProgressBar(true);
    Call<GenericResponse<MediaData>> call = RestClient.get().uploadAttachment(params);
    call.enqueue(new CallbackWrapper<MediaData>() {
      @Override
      public void onSuccess(GenericResponse<MediaData> response) {
        view.hideProgressBar();
        view.onImageUploaded(response.getData());
      }

      @Override
      public void onFailure(GenericResponse<MediaData> response) {
        view.hideProgressBar();
        view.showToast(response.getMessage());
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        view.hideProgressBar();
        view.showToast(errorResponse.getMessage());
      }
    });
  }

  public void getMessageHistory(int offset, int receiverId) {
    socketManager.emitEvent(SocketConst.EVENT_ASK_HISTORY, userId, user_type, threadId, offset,
        receiverId);
  }

  //public void sendMessageStatus(int msgId, String toUser, int chat_type) {
  //  socketManager.emitEvent(SocketConst.EVENT_SEND_MESSAGE_STATUS, msgId, toUser, chat_type);
  //}

  @Override
  public void onSocketConnected() {
    view.onConnected();
    socketManager.emitEvent(SocketConst.EVENT_AUTH_REQ, userId, user_type);
  }

  @Override
  public void onSocketError(Object... args) {

  }

  @Override
  public void onAuthSuccess(Object... args) {
    view.onAuthSuccess();
  }

  @Override
  public void onMessageReceive(Message message) {
    if (!TextUtils.isEmpty(threadId) && message.getThread_id().equals(threadId)) {
      view.onMessageReceive(message);
    }
  }

  @Override
  public void onMessagesListReceived(Object... args) {
  }

  @Override
  public void onUpdateMessages(Object... args) {
  }

  @Override
  public void onHistoryReceive(Object... args) {
    //ObjectMapper mapper = new ObjectMapper();
    //List<Message> messageList =
    //    mapper.readValue((String) args[0], new TypeReference<List<Message>>() {
    //    });
    ObjectMapper mapper = new ObjectMapper();
    History history = null;
    try {
      history = mapper.readValue((String) args[0], History.class);
      if (history != null && history.getSuccess() == 1) {
        view.onMessageReceive(history.getData(), history.isHas_more());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onStatusReceive(Object... args) {
    ObjectMapper mapper = new ObjectMapper();
    MessageStatus messageStatus = null;
    try {
      messageStatus = mapper.readValue((String) args[0], MessageStatus.class);
      if (messageStatus != null) {
        view.onStatusReceived(messageStatus);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onNewAgentMessages(Object... args) {
  }

  @Override
  public void onUnreadCount(Object... args) {

  }

  @Override
  public void onReceivedMessage(Object... args) {
    ObjectMapper mapper = new ObjectMapper();
    StatusResponse messageStatus = null;
    try {
      messageStatus = mapper.readValue((String) args[0], StatusResponse.class);
      if (messageStatus != null) {
        if (messageStatus.getThread_id().equals(threadId)) {
          view.onAllMessageDelivered();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onReadMessage(Object... args) {
    ObjectMapper mapper = new ObjectMapper();
    StatusResponse messageStatus = null;
    try {
      messageStatus = mapper.readValue((String) args[0], StatusResponse.class);
      if (messageStatus != null) {
        if (messageStatus.getThread_id().equals(threadId)) {
          view.onAllMessageRead();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
