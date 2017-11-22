package com.android.unideal.notification;

/**
 * Created by CS02 on 12/20/2016.
 */

public class MsgNotification {

  private int job_id;
  private int user_id;
  private String user_name;
  private String user_profile_url;
  private String msg_thread_id;
  private int msg_user_id;
  private String msg_user_name;
  private String msg_user_profile_url;
  private String text;
  private int user_type;
  private String attachment;
  private int consignment_size;
  private int is_offered;

  public int getJob_id() {
    return job_id;
  }

  public void setJob_id(int job_id) {
    this.job_id = job_id;
  }

  public int getUser_id() {
    return user_id;
  }

  public void setUser_id(int user_id) {
    this.user_id = user_id;
  }

  public String getUser_name() {
    return user_name;
  }

  public void setUser_name(String user_name) {
    this.user_name = user_name;
  }

  public String getUser_profile_url() {
    return user_profile_url;
  }

  public void setUser_profile_url(String user_profile_url) {
    this.user_profile_url = user_profile_url;
  }

  public String getMsg_thread_id() {
    return msg_thread_id;
  }

  public void setMsg_thread_id(String msg_thread_id) {
    this.msg_thread_id = msg_thread_id;
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

  public String getMsg_user_profile_url() {
    return msg_user_profile_url;
  }

  public void setMsg_user_profile_url(String msg_user_profile_url) {
    this.msg_user_profile_url = msg_user_profile_url;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getAttachment() {
    return attachment;
  }

  public void setAttachment(String attachment) {
    this.attachment = attachment;
  }

  public int getUser_type() {
    return user_type;
  }

  public void setUser_type(int user_type) {
    this.user_type = user_type;
  }

  public int getIs_offered() {
    return is_offered;
  }

  public void setIs_offered(int is_offered) {
    this.is_offered = is_offered;
  }

  public int getConsignment_size() {
    return consignment_size;
  }

  public void setConsignment_size(int consignment_size) {
    this.consignment_size = consignment_size;
  }
}
