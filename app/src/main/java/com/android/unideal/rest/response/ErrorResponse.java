package com.android.unideal.rest.response;

import android.text.TextUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse {

  @JsonProperty("message")
  private String message;

  public String getMessage() {
    return message;
  }

  public ErrorResponse setMessage(String message) {
    this.message = message;
    return this;
  }

  public boolean hasMessage() {
    return (!TextUtils.isEmpty(getMessage()));
  }
}
