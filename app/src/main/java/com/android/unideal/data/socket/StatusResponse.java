package com.android.unideal.data.socket;

/**
 * Created by CS02 on 12/16/2016.
 */

public class StatusResponse {

  private String thread_id;
  private int receiver_id;

  public String getThread_id() {
    return thread_id;
  }

  public void setThread_id(String thread_id) {
    this.thread_id = thread_id;
  }

  public int getReceiver_id() {
    return receiver_id;
  }

  public void setReceiver_id(int receiver_id) {
    this.receiver_id = receiver_id;
  }
}
