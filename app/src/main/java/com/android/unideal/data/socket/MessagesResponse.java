package com.android.unideal.data.socket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Created by bhavdip on 6/12/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessagesResponse {
  @JsonProperty("success")
  private int success;
  @JsonProperty("status_code")
  private int status_code;
  @JsonProperty("has_more")
  private boolean hasMore;
  @JsonProperty("message")
  private String message;
  @JsonProperty("data")
  private List<Messages> messages;

  public int getSuccess() {
    return success;
  }

  public MessagesResponse setSuccess(int success) {
    this.success = success;
    return this;
  }

  public String getMessage() {
    return message;
  }

  public MessagesResponse setMessage(String message) {
    this.message = message;
    return this;
  }

  public int getStatus_code() {
    return status_code;
  }

  public MessagesResponse setStatus_code(int status_code) {
    this.status_code = status_code;
    return this;
  }

  public boolean isHasMore() {
    return hasMore;
  }

  public MessagesResponse setHasMore(boolean hasMore) {
    this.hasMore = hasMore;
    return this;
  }

  public List<Messages> getMessages() {
    return messages;
  }

  public MessagesResponse setMessages(List<Messages> messages) {
    this.messages = messages;
    return this;
  }
}
