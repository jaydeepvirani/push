package com.android.unideal.data.socket;

import com.android.unideal.data.Status;
import com.android.unideal.rest.RestFields;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bhavdip on 6/12/16.
 * This model use for messages list
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Messages {
  @JsonProperty("thread_id")
  private String threadId;
  @JsonProperty("job_id")
  private int jobId;
  @JsonProperty("job_status")
  private int jobStatus;
  @JsonProperty("message_type")
  private int messageType;
  @JsonProperty("total_unread")
  private int totalUnread;
  @JsonProperty("agent_id")
  private int agentId;
  @JsonProperty("agent_name")
  private String agentName;
  @JsonProperty("agent_profile_pic")
  private String agentProfilePicture;
  @JsonProperty("created_date")
  private String createdDate;
  @JsonProperty("is_offered")
  private int isOffered;
  @JsonProperty("offer_price")
  private int offeredPrice;
  @JsonProperty("delivery_place")
  private String deliveryMethod;
  @JsonProperty("consignment_size")
  private int consignmentSize;
  @JsonProperty("data")
  private MediaData mediaData;

  /**
   * convert status enum to status type int
   *
   * @param status status enum
   * @return status type int or -1 if not found
   */
  public static int convertFromEnum(Status status) {
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

  public String getThreadId() {
    return threadId;
  }

  public Messages setThreadId(String threadId) {
    this.threadId = threadId;
    return this;
  }

  public int getJobId() {
    return jobId;
  }

  public Messages setJobId(int jobId) {
    this.jobId = jobId;
    return this;
  }

  public int getJobStatus() {
    return jobStatus;
  }

  public Messages setJobStatus(int jobStatus) {
    this.jobStatus = jobStatus;
    return this;
  }

  public int getMessageType() {
    return messageType;
  }

  public Messages setMessageType(int messageType) {
    this.messageType = messageType;
    return this;
  }

  public int getTotalUnread() {
    return totalUnread;
  }

  public Messages setTotalUnread(int totalUnread) {
    this.totalUnread = totalUnread;
    return this;
  }

  public int getIsOffered() {
    return isOffered;
  }

  public void setIsOffered(int isOffered) {
    this.isOffered = isOffered;
  }

  public int getConsignmentSize() {
    return consignmentSize;
  }

  public void setConsignmentSize(int consignmentSize) {
    this.consignmentSize = consignmentSize;
  }

  public int getAgentId() {
    return agentId;
  }

  public Messages setAgentId(int agentId) {
    this.agentId = agentId;
    return this;
  }

  public String getAgentName() {
    return agentName;
  }

  public Messages setAgentName(String agentName) {
    this.agentName = agentName;
    return this;
  }

  public String getAgentProfilePicture() {
    return agentProfilePicture;
  }

  public Messages setAgentProfilePicture(String agentProfilePicture) {
    this.agentProfilePicture = agentProfilePicture;
    return this;
  }

  public String getCreatedDate() {
    return createdDate;
  }

  public Messages setCreatedDate(String createdDate) {
    this.createdDate = createdDate;
    return this;
  }

  public MediaData getMediaData() {
    return mediaData;
  }

  public Messages setMediaData(MediaData mediaData) {
    this.mediaData = mediaData;
    return this;
  }

  public int getOfferedPrice() {
    return offeredPrice;
  }

  public void setOfferedPrice(int offeredPrice) {
    this.offeredPrice = offeredPrice;
  }

  public String getDeliveryMethod() {
    return deliveryMethod;
  }

  public void setDeliveryMethod(String deliveryMethod) {
    this.deliveryMethod = deliveryMethod;
  }

  /**
   * convert job status int to  job status enum
   *
   * @return job status enum or null
   */
  public Status getStatusEnum() {
    Status jobStatus = null;
    switch (this.jobStatus) {
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
}
