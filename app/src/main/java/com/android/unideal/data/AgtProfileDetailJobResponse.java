package com.android.unideal.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ADMIN on 14-11-2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgtProfileDetailJobResponse implements Parcelable {
  public static final Parcelable.Creator<AgtProfileDetailJobResponse> CREATOR =
      new Parcelable.Creator<AgtProfileDetailJobResponse>() {
        @Override
        public AgtProfileDetailJobResponse createFromParcel(Parcel source) {
          return new AgtProfileDetailJobResponse(source);
        }

        @Override
        public AgtProfileDetailJobResponse[] newArray(int size) {
          return new AgtProfileDetailJobResponse[size];
        }
      };
  @JsonProperty("user_id")
  private int user_Id;
  @JsonProperty("job_id")
  private String jobId;
  @JsonProperty("jobIdField")
  private String jobIdField;
  @JsonProperty("applicant")
  private int jobTotalApplicants;
  @JsonProperty("clientName")
  private String clientName;
  @JsonProperty("job_title")
  private String jobTitle;
  @JsonProperty("jobPrice")
  private String jobPrice;
  @JsonProperty("jobEndDate")
  private String jobEndDate;
  @JsonProperty("jobRatingBar")
  private float jobRatingBar;
  @JsonProperty("category_id")
  private int joCategory;
  @JsonProperty("job_status")
  private int jobStatus;
  @JsonProperty("consinment_size")
  private int consignmentSize;
  @JsonProperty("isread")
  private boolean isRead;
  @JsonProperty("totalMessage")
  private int totalMessage;
  @JsonProperty("job_detail")
  private String jobDetail;
  @JsonProperty("agent_commision")
  private int commision;
  @JsonProperty("status")
  private int status;
  @JsonProperty("job_start_on")
  private String jobStartDate;
  @JsonProperty("service_fees_advanced")
  private int serviceFees;
  @JsonProperty("advanced_charge")
  private int advanceCharge;
  @JsonProperty("commision_charge")
  private int commisionCharge;
  @JsonProperty("sub_category")
  private String subCategory;
  @JsonProperty("quantity")
  private String quantity;

  public AgtProfileDetailJobResponse() {
  }

  protected AgtProfileDetailJobResponse(Parcel in) {
    this.user_Id = in.readInt();
    this.jobId = in.readString();
    this.jobIdField = in.readString();
    this.jobTotalApplicants = in.readInt();
    this.clientName = in.readString();
    this.jobTitle = in.readString();
    this.jobPrice = in.readString();
    this.jobEndDate = in.readString();
    this.jobRatingBar = in.readFloat();
    this.joCategory = in.readInt();
    this.jobStatus = in.readInt();
    this.consignmentSize = in.readInt();
    this.isRead = in.readByte() != 0;
    this.totalMessage = in.readInt();
    this.jobDetail = in.readString();
    this.commision = in.readInt();
    this.status = in.readInt();
    this.jobStartDate = in.readString();
    this.serviceFees = in.readInt();
    this.advanceCharge = in.readInt();
    this.commisionCharge = in.readInt();
    this.subCategory = in.readString();
    this.quantity = in.readString();
  }

  public int getUser_Id() {
    return user_Id;
  }

  public void setUser_Id(int user_Id) {
    this.user_Id = user_Id;
  }

  public String getJobId() {
    return jobId;
  }

  public void setJobId(String jobId) {
    this.jobId = jobId;
  }

  public String getJobIdField() {
    return jobIdField;
  }

  public void setJobIdField(String jobIdField) {
    this.jobIdField = jobIdField;
  }

  public int getJobTotalApplicants() {
    return jobTotalApplicants;
  }

  public void setJobTotalApplicants(int jobTotalApplicants) {
    this.jobTotalApplicants = jobTotalApplicants;
  }

  public String getClientName() {
    return clientName;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public String getJobTitle() {
    return jobTitle;
  }

  public void setJobTitle(String jobTitle) {
    this.jobTitle = jobTitle;
  }

  public String getJobPrice() {
    return jobPrice;
  }

  public void setJobPrice(String jobPrice) {
    this.jobPrice = jobPrice;
  }

  public String getJobEndDate() {
    return jobEndDate;
  }

  public void setJobEndDate(String jobEndDate) {
    this.jobEndDate = jobEndDate;
  }

  public float getJobRatingBar() {
    return jobRatingBar;
  }

  public void setJobRatingBar(float jobRatingBar) {
    this.jobRatingBar = jobRatingBar;
  }

  public int getJoCategory() {
    return joCategory;
  }

  public void setJoCategory(int joCategory) {
    this.joCategory = joCategory;
  }

  public int getJobStatus() {
    return jobStatus;
  }

  public void setJobStatus(int jobStatus) {
    this.jobStatus = jobStatus;
  }

  public int getConsignmentSize() {
    return consignmentSize;
  }

  public void setConsignmentSize(int consignmentSize) {
    this.consignmentSize = consignmentSize;
  }

  public boolean isRead() {
    return isRead;
  }

  public void setRead(boolean read) {
    isRead = read;
  }

  public int getTotalMessage() {
    return totalMessage;
  }

  public void setTotalMessage(int totalMessage) {
    this.totalMessage = totalMessage;
  }

  public String getJobDetail() {
    return jobDetail;
  }

  public void setJobDetail(String jobDetail) {
    this.jobDetail = jobDetail;
  }

  public int getCommision() {
    return commision;
  }

  public void setCommision(int commision) {
    this.commision = commision;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getJobStartDate() {
    return jobStartDate;
  }

  public void setJobStartDate(String jobStartDate) {
    this.jobStartDate = jobStartDate;
  }

  public int getServiceFees() {
    return serviceFees;
  }

  public void setServiceFees(int serviceFees) {
    this.serviceFees = serviceFees;
  }

  public int getAdvanceCharge() {
    return advanceCharge;
  }

  public void setAdvanceCharge(int advanceCharge) {
    this.advanceCharge = advanceCharge;
  }

  public int getCommisionCharge() {
    return commisionCharge;
  }

  public void setCommisionCharge(int commisionCharge) {
    this.commisionCharge = commisionCharge;
  }

  public String getSubCategory() {
    return subCategory;
  }

  public void setSubCategory(String subCategory) {
    this.subCategory = subCategory;
  }

  public String getQuantity() {
    return quantity;
  }

  public void setQuantity(String quantity) {
    this.quantity = quantity;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.user_Id);
    dest.writeString(this.jobId);
    dest.writeString(this.jobIdField);
    dest.writeInt(this.jobTotalApplicants);
    dest.writeString(this.clientName);
    dest.writeString(this.jobTitle);
    dest.writeString(this.jobPrice);
    dest.writeString(this.jobEndDate);
    dest.writeFloat(this.jobRatingBar);
    dest.writeInt(this.joCategory);
    dest.writeInt(this.jobStatus);
    dest.writeInt(this.consignmentSize);
    dest.writeByte(isRead ? (byte) 1 : (byte) 0);
    dest.writeInt(this.totalMessage);
    dest.writeString(this.jobDetail);
    dest.writeInt(this.commision);
    dest.writeInt(this.status);
    dest.writeString(this.jobStartDate);
    dest.writeInt(this.serviceFees);
    dest.writeInt(this.advanceCharge);
    dest.writeInt(this.commisionCharge);
    dest.writeString(this.subCategory);
    dest.writeString(this.quantity);
  }
}
