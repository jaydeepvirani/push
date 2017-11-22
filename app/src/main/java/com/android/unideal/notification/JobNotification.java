package com.android.unideal.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by CS02 on 12/22/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobNotification {

  private int job_id;
  private int job_status;
  private int user_id;
  private int user_type;
  private int msg_user_id;
  private String msg_user_name;
  private String text;

  public int getJob_id() {
    return job_id;
  }

  public void setJob_id(int job_id) {
    this.job_id = job_id;
  }

  public int getJob_status() {
    return job_status;
  }

  public void setJob_status(int job_status) {
    this.job_status = job_status;
  }

  public int getUser_id() {
    return user_id;
  }

  public void setUser_id(int user_id) {
    this.user_id = user_id;
  }

  public int getUser_type() {
    return user_type;
  }

  public void setUser_type(int user_type) {
    this.user_type = user_type;
  }

  public int getMsg_user_id() {
    return msg_user_id;
  }

  public void setMsg_user_id(int msg_user_id) {
    this.msg_user_id = msg_user_id;
  }

  public String getMsg_user_name() {
    return msg_user_name;
  }

  public void setMsg_user_name(String msg_user_name) {
    this.msg_user_name = msg_user_name;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
