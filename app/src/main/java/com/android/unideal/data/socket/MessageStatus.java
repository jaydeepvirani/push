package com.android.unideal.data.socket;

/**
 * Created by CS02 on 12/13/2016.
 */

public class MessageStatus {
  private int sender_id;
  private int receiver_id;
  private int status;
  private int message_id;

  public int getSender_id() {
    return sender_id;
  }

  public void setSender_id(int sender_id) {
    this.sender_id = sender_id;
  }

  public int getReceiver_id() {
    return receiver_id;
  }

  public void setReceiver_id(int receiver_id) {
    this.receiver_id = receiver_id;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public int getMessage_id() {
    return message_id;
  }

  public void setMessage_id(int message_id) {
    this.message_id = message_id;
  }
}
