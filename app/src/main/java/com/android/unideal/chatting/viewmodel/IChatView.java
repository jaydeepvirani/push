package com.android.unideal.chatting.viewmodel;

import com.android.unideal.data.socket.MediaData;
import com.android.unideal.data.socket.Message;
import com.android.unideal.data.socket.MessageStatus;
import java.util.List;

/** Created by CS02 on 12/7/2016. */
public interface IChatView {
  void onConnected();

  void onAuthSuccess();

  void onMessageReceive(Message message);

  void onMessageReceive(List<Message> messageList, boolean hasMore);

  void showToast(String message);

  void onImageUploaded(MediaData mediaData);

  void showProgressBar(boolean isCancelable);

  void onStatusReceived(MessageStatus status);

  void hideProgressBar();

  void onAllMessageRead();

  void onAllMessageDelivered();
}
