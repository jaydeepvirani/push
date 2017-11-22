package com.android.unideal.rest.response;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ADMIN on 22-11-2016.
 */

public class JobRatting implements Parcelable {
  public static final Parcelable.Creator<JobRatting> CREATOR =
      new Parcelable.Creator<JobRatting>() {
        @Override
        public JobRatting createFromParcel(Parcel source) {
          return new JobRatting(source);
        }

        @Override
        public JobRatting[] newArray(int size) {
          return new JobRatting[size];
        }
      };
  @JsonProperty("job_title")
  private String jobTitle;
  @JsonProperty("job_id")
  private String jobId;
  @JsonProperty("reviews")
  private float reviews;
  @JsonProperty("job_end_on")
  private String jobEndDate;
  @JsonProperty("user_name")
  private String userName;

  public JobRatting() {
  }

  private JobRatting(Parcel in) {
    this.jobTitle = in.readString();
    this.jobId = in.readString();
    this.reviews = in.readFloat();
    this.jobEndDate = in.readString();
    this.userName = in.readString();
  }

  public String getJobTitle() {
    return jobTitle;
  }

  public void setJobTitle(String jobTitle) {
    this.jobTitle = jobTitle;
  }

  public String getJobId() {
    return jobId;
  }

  public void setJobId(String jobId) {
    this.jobId = jobId;
  }

  public float getReviews() {
    return reviews;
  }

  public void setReviews(int reviews) {
    this.reviews = reviews;
  }

  public String getJobEndDate() {
    return jobEndDate;
  }

  public void setJobEndDate(String jobEndDate) {
    this.jobEndDate = jobEndDate;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.jobTitle);
    dest.writeString(this.jobId);
    dest.writeFloat(this.reviews);
    dest.writeString(this.jobEndDate);
    dest.writeString(this.userName);
  }
}
