package com.android.unideal.rest.response;

import com.android.unideal.data.Notifications;
import java.util.List;

/**
 * Created by CS02 on 12/22/2016.
 */

public class NotificationResponse {

  private int success;
  private int status_code;
  private String message;
  private boolean has_more;
  private int total_unread;
  private List<Notifications> data;

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

  public String getMessage() {
    return message;
  }

  public void setMessage(String messages) {
    this.message = messages;
  }

  public boolean isHas_more() {
    return has_more;
  }

  public void setHas_more(boolean has_more) {
    this.has_more = has_more;
  }

  public List<Notifications> getData() {
    return data;
  }

  public void setData(List<Notifications> data) {
    this.data = data;
  }

  public int getTotal_unread() {
    return total_unread;
  }

  public void setTotal_unread(int total_unread) {
    this.total_unread = total_unread;
  }
}
