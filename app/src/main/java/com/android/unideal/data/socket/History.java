package com.android.unideal.data.socket;

import java.util.List;

/**
 * Created by CS02 on 12/12/2016.
 */

public class History {

  private int success;
  private int status_code;
  private boolean has_more;
  private String message;
  private List<Message> data;

  public int getSuccess() {
    return success;
  }

  public void setSuccess(int success) {
    this.success = success;
  }

  public int getStatus_code() {
    return status_code;
  }

  public void setStatus_code(int status_code) {
    this.status_code = status_code;
  }

  public boolean isHas_more() {
    return has_more;
  }

  public void setHas_more(boolean has_more) {
    this.has_more = has_more;
  }

  public List<Message> getData() {
    return data;
  }

  public void setData(List<Message> data) {
    this.data = data;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
