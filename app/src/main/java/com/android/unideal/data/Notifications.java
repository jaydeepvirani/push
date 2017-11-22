package com.android.unideal.data;

/**
 * Created by bhavdip on 5/10/16.
 */

public class Notifications {

  private int notification_id;
  private int job_id;
  private int notification_type;
  private int user_id;
  private int msg_user_id;
  private String msg_user_name;
  private String message;
  private String created_date;
  private int is_read;
  private int is_awared;
  private int job_status;

  public int getNotification_id() {
    return notification_id;
  }

  public void setNotification_id(int notification_id) {
    this.notification_id = notification_id;
  }

  public int getJob_id() {
    return job_id;
  }

  public void setJob_id(int job_id) {
    this.job_id = job_id;
  }

  public int getNotification_type() {
    return notification_type;
  }

  public void setNotification_type(int notification_type) {
    this.notification_type = notification_type;
  }

  public int getUser_id() {
    return user_id;
  }

  public void setUser_id(int user_id) {
    this.user_id = user_id;
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

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getCreated_date() {
    return created_date;
  }

  public void setCreated_date(String created_date) {
    this.created_date = created_date;
  }

  public int getIs_read() {
    return is_read;
  }

  public void setIs_read(int is_read) {
    this.is_read = is_read;
  }

  public int getIs_awared() {
    return is_awared;
  }

  public void setIs_awared(int is_awared) {
    this.is_awared = is_awared;
  }

  public int getJob_status() {
    return job_status;
  }

  public void setJob_status(int job_status) {
    this.job_status = job_status;
  }
}