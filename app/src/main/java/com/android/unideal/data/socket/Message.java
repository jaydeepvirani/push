package com.android.unideal.data.socket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Created by bhavdip on 6/12/16. * This model use for chatting conversation list */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {

  public static final int TYPE_TEXT = 1;
  public static final int TYPE_IMAGE = 2;

  public static final int STATUS_NONE = 0;
  public static final int STATUS_SEND = 1;
  public static final int STATUS_SEEN = 2;

  @JsonProperty("message_id")
  private int message_id;

  @JsonProperty("job_id")
  private int job_id;

  @JsonProperty("thread_id")
  private String thread_id;

  @JsonProperty("sender_id")
  private int sender_id;

  @JsonProperty("receiver_id")
  private int receiver_id;

  @JsonProperty("user_type")
  private int user_type;

  @JsonProperty("status")
  private int status;

  @JsonProperty("message_type")
  private int message_type;

  @JsonProperty("send_time")
  private String send_time;

  @JsonProperty("data")
  private MediaData mediaData;

  @JsonProperty("profile_pic")
  private String profile_pic;

  @JsonProperty("name")
  private String name;

  public int getMessage_id() {
    return message_id;
  }

  public Message setMessage_id(int message_id) {
    this.message_id = message_id;
    return this;
  }

  public int getJob_id() {
    return job_id;
  }

  public Message setJob_id(int job_id) {
    this.job_id = job_id;
    return this;
  }

  public String getThread_id() {
    return thread_id;
  }

  public Message setThread_id(String thread_id) {
    this.thread_id = thread_id;
    return this;
  }

  public int getSender_id() {
    return sender_id;
  }

  public Message setSender_id(int sender_id) {
    this.sender_id = sender_id;
    return this;
  }

  public int getReceiver_id() {
    return receiver_id;
  }

  public Message setReceiver_id(int receiver_id) {
    this.receiver_id = receiver_id;
    return this;
  }

  public int getUser_type() {
    return user_type;
  }

  public Message setUser_type(int user_type) {
    this.user_type = user_type;
    return this;
  }

  public int getStatus() {
    return status;
  }

  public Message setStatus(int status) {
    this.status = status;
    return this;
  }

  public int getMessage_type() {
    return message_type;
  }

  public Message setMessage_type(int message_type) {
    this.message_type = message_type;
    return this;
  }

  public MediaData getMediaData() {
    return mediaData;
  }

  public Message setMediaData(MediaData mediaData) {
    this.mediaData = mediaData;
    return this;
  }

  public String getSend_time() {
    return send_time;
  }

  public void setSend_time(String send_time) {
    this.send_time = send_time;
  }

  public String getProfile_pic() {
    return profile_pic;
  }

  public void setProfile_pic(String profile_pic) {
    this.profile_pic = profile_pic;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
