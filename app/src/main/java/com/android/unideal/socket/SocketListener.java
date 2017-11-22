package com.android.unideal.socket;

import com.android.unideal.data.socket.Message;

/**
 * Created by DSK02 on 12/10/2015.
 */
public interface SocketListener {

  void onSocketConnected();

  void onSocketError(Object... args);

  void onAuthSuccess(Object... args);

  void onMessageReceive(Message message);

  void onMessagesListReceived(Object... args);

  void onUpdateMessages(Object... args);

  void onHistoryReceive(Object... args);

  void onStatusReceive(Object... args);

  void onReceivedMessage(Object... args);

  void onReadMessage(Object... args);

  void onNewAgentMessages(Object... args);

  void onUnreadCount(Object... args);
}
