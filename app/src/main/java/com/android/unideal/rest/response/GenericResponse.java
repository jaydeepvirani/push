package com.android.unideal.rest.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GenericResponse<T> {

  @JsonProperty("message")
  private String message;

  @JsonProperty("success")
  private int success;

  @JsonProperty("status_code")
  private int status_code;

  @JsonProperty("data")
  private T data;

  public T getData() {
    return data;
  }

  public String getMessage() {
    return message;
  }

  public int isSuccess() {
    return success;
  }

  public int getStatus_code() {
    return status_code;
  }
}
