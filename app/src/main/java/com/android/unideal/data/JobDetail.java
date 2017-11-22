package com.android.unideal.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.android.unideal.rest.RestFields;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * Created by CS02 on 11/21/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobDetail implements Parcelable {

  private int job_id;
  private int user_id;
  private String job_title;
  private int job_status;
  private float consignment_size;
  // Has agent request for complete,pause this job
  private int is_agent_confirm;
  // Has questioner request for complete,pause this job
  private int is_questioner_confirm;
  // Is this job is dispute 0 means false 1 means true
  private int is_dispute;
  private String job_end_on;
  private int applicant;
  private String category_name;
  private String user_name;
  private int messages;
  // applied
  private int category_id;
  private float reviews;
  private String cancelled_on;
  private float agent_review;
  private float questionar_review;
  private String job_details;
  private String job_start_on;
  private List<String> files;
  private String deliveryplace;
  private int offered_price;
  private String sub_category_name;
  private int is_invoice;
  private int msg_user_id;
  private String msg_thread_id;
  private String msg_user_name;
  private String msg_user_profile_url;
  private float agent_commision;
  public JobDetail() {
  }

  public float getAgent_commision() {
    return agent_commision;
  }

  public void setAgent_commision(float agent_commision) {
    this.agent_commision = agent_commision;
  }

  /**
   * convert status enum to status type int
   *
   * @param status status enum
   * @return status type int or -1 if not found
   */
  public static int convertFromEnam(Status status) {
    int jobStatus = -1;
    switch (status) {
      case OPEN:
        jobStatus = RestFields.STATUS.OPEN;
        break;
      case APPLIED:
        jobStatus = RestFields.STATUS.APPLIED;
        break;
      case CANCELLED:
        jobStatus = RestFields.STATUS.CANCELLED;
        break;
      case COMPLETED:
        jobStatus = RestFields.STATUS.COMPLETED;
        break;
      case IN_PROCESS:
        jobStatus = RestFields.STATUS.IN_PROCESS;
        break;
      case PAUSED:
        jobStatus = RestFields.STATUS.PAUSED;
        break;
    }
    return jobStatus;
  }

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

  public String getJob_title() {
    return job_title;
  }

  public void setJob_title(String job_title) {
    this.job_title = job_title;
  }

  public int getJob_status() {
    return job_status;
  }

  public void setJob_status(int job_status) {
    this.job_status = job_status;
  }

  public String getJob_end_on() {
    return job_end_on;
  }

  public void setJob_end_on(String job_end_on) {
    this.job_end_on = job_end_on;
  }

  public int getApplicant() {
    return applicant;
  }

  public void setApplicant(int applicant) {
    this.applicant = applicant;
  }

  public String getCategory_name() {
    return category_name;
  }

  public void setCategory_name(String category_name) {
    this.category_name = category_name;
  }

  public String getUser_name() {
    return user_name;
  }

  public void setUser_name(String user_name) {
    this.user_name = user_name;
  }

  public int getMessages() {
    return messages;
  }

  public void setMessages(int messages) {
    this.messages = messages;
  }

  public int getCategory_id() {
    return category_id;
  }

  public void setCategory_id(int category_id) {
    this.category_id = category_id;
  }

  public float getReviews() {
    return reviews;
  }

  public void setReviews(float reviews) {
    this.reviews = reviews;
  }

  public String getCancelled_on() {
    return cancelled_on;
  }

  public void setCancelled_on(String cancelled_on) {
    this.cancelled_on = cancelled_on;
  }

  public float getConsignment_size() {
    return consignment_size;
  }

  public void setConsignment_size(float consignment_size) {
    this.consignment_size = consignment_size;
  }

  public int getIs_agent_confirm() {
    return is_agent_confirm;
  }

  public void setIs_agent_confirm(int is_agent_confirm) {
    this.is_agent_confirm = is_agent_confirm;
  }

  public int getIs_questioner_confirm() {
    return is_questioner_confirm;
  }

  public void setIs_questioner_confirm(int is_questioner_confirm) {
    this.is_questioner_confirm = is_questioner_confirm;
  }

  public float getAgent_review() {
    return agent_review;
  }

  public void setAgent_review(float agent_review) {
    this.agent_review = agent_review;
  }

  public float getQuestionar_review() {
    return questionar_review;
  }

  public void setQuestionar_review(float questionar_review) {
    this.questionar_review = questionar_review;
  }

  public String getJob_details() {
    return job_details;
  }

  public void setJob_details(String job_details) {
    this.job_details = job_details;
  }

  public String getJob_start_on() {
    return job_start_on;
  }

  public void setJob_start_on(String job_start_on) {
    this.job_start_on = job_start_on;
  }

  public List<String> getFiles() {
    return files;
  }

  public void setFiles(List<String> files) {
    this.files = files;
  }

  public String getDeliveryplace() {
    return deliveryplace;
  }

  public JobDetail setDeliveryplace(String deliveryplace) {
    this.deliveryplace = deliveryplace;
    return this;
  }

  public String getSub_category_name() {
    return sub_category_name;
  }

  public JobDetail setSub_category_name(String sub_category_name) {
    this.sub_category_name = sub_category_name;
    return this;
  }

  public int getIs_dispute() {
    return is_dispute;
  }

  public JobDetail setIs_dispute(int is_dispute) {
    this.is_dispute = is_dispute;
    return this;
  }

  public int getMsg_user_id() {
    return msg_user_id;
  }

  public JobDetail setMsg_user_id(int msg_user_id) {
    this.msg_user_id = msg_user_id;
    return this;
  }

  public String getMsg_thread_id() {
    return msg_thread_id;
  }

  public JobDetail setMsg_thread_id(String msg_thread_id) {
    this.msg_thread_id = msg_thread_id;
    return this;
  }

  public String getMsg_user_name() {
    return msg_user_name;
  }

  public JobDetail setMsg_user_name(String msg_user_name) {
    this.msg_user_name = msg_user_name;
    return this;
  }

  public String getMsg_user_profile_url() {
    return msg_user_profile_url;
  }

  public JobDetail setMsg_user_profile_url(String msg_user_profile_url) {
    this.msg_user_profile_url = msg_user_profile_url;
    return this;
  }

  public int getIs_invoice() {
    return is_invoice;
  }

  public JobDetail setIs_invoice(int is_invoice) {
    this.is_invoice = is_invoice;
    return this;
  }

  public int getOffered_price() {
    return offered_price;
  }

  public void setOffered_price(int offered_price) {
    this.offered_price = offered_price;
  }

  /**
   * convert job status int to  job status enum
   *
   * @return job status enum or null
   */
  public Status getStatusEnum() {
    Status jobStatus = null;
    switch (job_status) {
      case RestFields.STATUS.OPEN:
        jobStatus = Status.OPEN;
        break;
      case RestFields.STATUS.APPLIED:
        jobStatus = Status.APPLIED;
        break;
      case RestFields.STATUS.CANCELLED:
        jobStatus = Status.CANCELLED;
        break;
      case RestFields.STATUS.COMPLETED:
        jobStatus = Status.COMPLETED;
        break;
      case RestFields.STATUS.IN_PROCESS:
        jobStatus = Status.IN_PROCESS;
        break;
      case RestFields.STATUS.RESUMED:
        jobStatus = Status.IN_PROCESS;
        break;
      case RestFields.STATUS.PAUSED:
        jobStatus = Status.PAUSED;
        break;
    }
    return jobStatus;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.job_id);
    dest.writeInt(this.user_id);
    dest.writeString(this.job_title);
    dest.writeInt(this.job_status);
    dest.writeFloat(this.consignment_size);
    dest.writeInt(this.is_agent_confirm);
    dest.writeInt(this.is_questioner_confirm);
    dest.writeInt(this.is_dispute);
    dest.writeString(this.job_end_on);
    dest.writeInt(this.applicant);
    dest.writeString(this.category_name);
    dest.writeString(this.user_name);
    dest.writeInt(this.messages);
    dest.writeInt(this.category_id);
    dest.writeFloat(this.reviews);
    dest.writeString(this.cancelled_on);
    dest.writeFloat(this.agent_review);
    dest.writeFloat(this.questionar_review);
    dest.writeString(this.job_details);
    dest.writeString(this.job_start_on);
    dest.writeStringList(this.files);
    dest.writeString(this.deliveryplace);
    dest.writeInt(this.offered_price);
    dest.writeString(this.sub_category_name);
    dest.writeInt(this.is_invoice);
    dest.writeInt(this.msg_user_id);
    dest.writeString(this.msg_thread_id);
    dest.writeString(this.msg_user_name);
    dest.writeString(this.msg_user_profile_url);
    dest.writeFloat(this.agent_commision);
  }

  protected JobDetail(Parcel in) {
    this.job_id = in.readInt();
    this.user_id = in.readInt();
    this.job_title = in.readString();
    this.job_status = in.readInt();
    this.consignment_size = in.readFloat();
    this.is_agent_confirm = in.readInt();
    this.is_questioner_confirm = in.readInt();
    this.is_dispute = in.readInt();
    this.job_end_on = in.readString();
    this.applicant = in.readInt();
    this.category_name = in.readString();
    this.user_name = in.readString();
    this.messages = in.readInt();
    this.category_id = in.readInt();
    this.reviews = in.readFloat();
    this.cancelled_on = in.readString();
    this.agent_review = in.readFloat();
    this.questionar_review = in.readFloat();
    this.job_details = in.readString();
    this.job_start_on = in.readString();
    this.files = in.createStringArrayList();
    this.deliveryplace = in.readString();
    this.offered_price = in.readInt();
    this.sub_category_name = in.readString();
    this.is_invoice = in.readInt();
    this.msg_user_id = in.readInt();
    this.msg_thread_id = in.readString();
    this.msg_user_name = in.readString();
    this.msg_user_profile_url = in.readString();
    this.agent_commision = in.readFloat();
  }

  public static final Creator<JobDetail> CREATOR = new Creator<JobDetail>() {
    @Override public JobDetail createFromParcel(Parcel source) {
      return new JobDetail(source);
    }

    @Override public JobDetail[] newArray(int size) {
      return new JobDetail[size];
    }
  };
}
