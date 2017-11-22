package com.android.unideal.questioner.viewmodel;

import android.content.Context;
import android.util.Log;
import com.android.unideal.data.UserDetail;
import com.android.unideal.data.socket.Message;
import com.android.unideal.data.socket.Messages;
import com.android.unideal.data.socket.MessagesResponse;
import com.android.unideal.enums.AppMode;
import com.android.unideal.rest.response.GenericResponse;
import com.android.unideal.socket.SocketConst;
import com.android.unideal.socket.SocketListener;
import com.android.unideal.socket.SocketManager;
import com.android.unideal.util.DateTimeUtils;
import com.android.unideal.util.SessionManager;
import com.android.unideal.util.converter.JacksonConverter;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ADMIN on 12-10-2016.
 */

public class MessageFragmentViewModel implements SocketListener {
  private static final String TAG = "MessageFragmentViewMode";
  private Context mContext;
  private QuestionerMessageListener mListener;
  private SocketManager mSocketManger;
  private UserDetail mUserDetail;
  private int mPageIndex = 0;
  private String lastUpdateDate;

  public MessageFragmentViewModel(Context context, QuestionerMessageListener mListener) {
    this.mContext = context;
    this.mListener = mListener;
    mUserDetail = SessionManager.get(getContext()).getActiveUser();
  }

  private Context getContext() {
    return mContext;
  }

  public void onActivityCreated() {
    updateSynchronousTime();
    this.mListener.startBindingViews();
  }

  public void onResume() {
    initSocketManager();
  }

  public void onPause() {
    removeSocketListener();
  }

  private void initSocketManager() {
    Log.d(TAG, "initSocketManager: ");
    mSocketManger = SocketManager.getInstance();
    mSocketManger.setSocketListener(this);
    if (mSocketManger.isConnected()) {
      Log.d(TAG, "initSocketManager: Already Connected");
      onSocketConnected();
    } else {
      mSocketManger.openSocket();
      mSocketManger.listenEvents();
      mSocketManger.connect();
      Log.d(TAG, "initSocketManager: new connection");
    }
  }

  public void disconnectSocket() {
    mSocketManger.disconnect();
  }

  private void removeSocketListener() {
    mSocketManger.removeListener(this);
    Log.d(TAG, "removeSocketListener: ");
  }

  public void startAuthentication() {
    UserDetail activeUser = SessionManager.get(getContext()).getActiveUser();
    if (activeUser != null) {
      mSocketManger.emitEvent(SocketConst.EVENT_AUTH_REQ, activeUser.getUserId(),
          AppMode.QUESTIONER.getValue());
      Log.d(TAG, "startAuthentication: ");
    }
  }

  public int getCurrentIndex() {
    return mPageIndex;
  }

  /**
   * When request this function if the page index size is grater than
   * 0 we start show load more view once we receive data we remove it
   */
  public void getMessagesListData() {
    Log.d(TAG, "getMessagesListData: " + mPageIndex);
    mListener.showProgressBar(true);
    if (mPageIndex > 0) {
      mListener.showLoadMoreView(true);
    }
    mSocketManger.emitEvent(SocketConst.EVENT_ASK_MESSAGES_LIST, mUserDetail.getUserId(),
        AppMode.QUESTIONER.getValue(), mPageIndex);
  }

  public void refreshMessages() {
    mSocketManger.emitEvent(SocketConst.EVENT_ASK_UPDATE_MESSAGES, mUserDetail.getUserId(),
        AppMode.QUESTIONER.getValue(), lastUpdateDate);
  }

  private void updateSynchronousTime() {
    lastUpdateDate = DateTimeUtils.getCurrentTime();
  }

  @Override
  public void onSocketConnected() {
    this.mListener.onSocketConnected();
  }

  @Override
  public void onSocketError(Object... args) {
    mListener.showProgressBar(false);
  }

  @Override
  public void onAuthSuccess(Object... args) {
    Log.d(TAG, "onAuthSuccess: " + Arrays.toString(args));
    GenericResponse response =
        JacksonConverter.getObjectFromJSON((String) args[0], GenericResponse.class);
    if (response.isSuccess() > 200) {
      mListener.onAuthFailed();
    } else {
      mListener.onAuthSuccess();
    }
  }

  @Override
  public void onMessageReceive(Message message) {
    Log.d(TAG, "onMessageReceive: refreshMessages ");
    refreshMessages();
  }

  @Override
  public void onMessagesListReceived(Object... args) {
    Log.d(TAG, "onMessagesListReceived: " + Arrays.toString(args));
    MessagesResponse messagesResponse =
        JacksonConverter.getObjectFromJSON((String) args[0], MessagesResponse.class);
    if (mPageIndex > 0) {
      mListener.showLoadMoreView(false);
    }
    //once we add result message data into the adapter
    //then check the has more or not for load more enable disable
    if (messagesResponse.isHasMore()) {
      mListener.setMoreDataAvailable(true);
    } else {
      mListener.setMoreDataAvailable(false);
    }
    //we would allow to increase the page index
    mPageIndex++;
    List<Messages> messagesList = messagesResponse.getMessages();
    if (messagesList != null && messagesList.size() > 0) {
      mListener.showProgressBar(false);
      this.mListener.bindMessagesList(messagesList);
    } else {
      mListener.showProgressBar(false);
      this.mListener.bindEmptyView();
    }
  }

  @Override
  public void onUpdateMessages(Object... args) {
    Log.d(TAG, "onUpdateMessages: " + Arrays.toString(args));
    MessagesResponse updateMessagesResponse =
        JacksonConverter.getObjectFromJSON((String) args[0], MessagesResponse.class);
    if (updateMessagesResponse.getStatus_code() == 200) {
      updateSynchronousTime();
      mListener.onUpdateMessages(updateMessagesResponse.getMessages());
    } else {
      mListener.onUpdateMessages(null);
    }
  }

  @Override
  public void onHistoryReceive(Object... args) {
  }

  @Override
  public void onStatusReceive(Object... args) {
  }

  @Override
  public void onNewAgentMessages(Object... args) {
    Log.d(TAG, "onNewAgentMessages: " + Arrays.toString(args));
    mListener.onNewAgentMessages();
  }

  @Override
  public void onUnreadCount(Object... args) {

  }

  @Override
  public void onReceivedMessage(Object... args) {

  }

  @Override
  public void onReadMessage(Object... args) {

  }

  public interface QuestionerMessageListener {
    void onAuthSuccess();

    void onAuthFailed();

    void showLoadMoreView(boolean visibility);

    void setMoreDataAvailable(boolean available);

    void showProgressBar(boolean visibility);

    void bindMessagesList(List<Messages> messages);

    void onUpdateMessages(List<Messages> messages);

    void onNewAgentMessages();

    void bindEmptyView();

    void startBindingViews();

    void onSocketConnected();
  }
}
