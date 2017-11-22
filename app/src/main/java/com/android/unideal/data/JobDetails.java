package com.android.unideal.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bhavdip on 10/2/16.
 */

public class JobDetails implements Parcelable {
  public static final Parcelable.Creator<JobDetails> CREATOR =
      new Parcelable.Creator<JobDetails>() {
        @Override
        public JobDetails createFromParcel(Parcel source) {
          return new JobDetails(source);
        }

        @Override
        public JobDetails[] newArray(int size) {
          return new JobDetails[size];
        }
      };
  private String jobTitle;
  private String description;
  private int consignmentSize;
  private int agentPrice;

  public JobDetails() {
  }

  protected JobDetails(Parcel in) {
    this.jobTitle = in.readString();
    this.description = in.readString();
    this.consignmentSize = in.readInt();
    this.agentPrice = in.readInt();
  }

  public String getJobTitle() {
    return jobTitle;
  }

  public JobDetails setJobTitle(String jobTitle) {
    this.jobTitle = jobTitle;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public JobDetails setDescription(String description) {
    this.description = description;
    return this;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  public int getAgentPrice() {
    return agentPrice;
  }

  public JobDetails setAgentPrice(int agentPrice) {
    this.agentPrice = agentPrice;
    return this;
  }

  public int getConsignmentSize() {
    return consignmentSize;
  }

  public JobDetails setConsignmentSize(int consignmentSize) {
    this.consignmentSize = consignmentSize;
    return this;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.jobTitle);
    dest.writeString(this.description);
    dest.writeInt(this.consignmentSize);
    dest.writeInt(this.agentPrice);
  }
}
