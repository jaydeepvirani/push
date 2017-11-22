package com.android.unideal.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ADMIN on 09-02-2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnReadCount {
  @JsonProperty("success")
  private int success;

  @JsonProperty("status_code")
  private int statusCode;

  @JsonProperty("unread_count")
  private int unreadCount;

  public int getSuccess() {
    return success;
  }

  public void setSuccess(int success) {
    this.success = success;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  public int getUnreadCount() {
    return unreadCount;
  }

  public void setUnreadCount(int unreadCount) {
    this.unreadCount = unreadCount;
  }
}
