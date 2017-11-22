package com.android.unideal.data.socket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bhavdip on 6/12/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageResponse {
  @JsonProperty("success")
  private int success;
  @JsonProperty("status_code")
  private int status_code;
  @JsonProperty("has_more")
  private boolean hasMore;
  @JsonProperty("data")
  private Message message;

  public int getSuccess() {
    return success;
  }

  public MessageResponse setSuccess(int success) {
    this.success = success;
    return this;
  }

  public int getStatus_code() {
    return status_code;
  }

  public MessageResponse setStatus_code(int status_code) {
    this.status_code = status_code;
    return this;
  }

  public boolean isHasMore() {
    return hasMore;
  }

  public MessageResponse setHasMore(boolean hasMore) {
    this.hasMore = hasMore;
    return this;
  }

  public Message getMessage() {
    return message;
  }

  public MessageResponse setMessage(Message message) {
    this.message = message;
    return this;
  }
}
