package com.android.unideal.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ADMIN on 21-11-2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Applicant implements Parcelable {
  public static final Creator<Applicant> CREATOR = new Creator<Applicant>() {
    @Override
    public Applicant createFromParcel(Parcel source) {
      return new Applicant(source);
    }

    @Override
    public Applicant[] newArray(int size) {
      return new Applicant[size];
    }
  };
  @JsonProperty("job_id")
  private int jobId;
  @JsonProperty("user_name")
  private String userName;
  @JsonProperty("user_id")
  private int userId;
  @JsonProperty("applicant_status")
  private int applicantStatus;
  @JsonProperty("reviews")
  private int reviews;
  @JsonProperty("profile_pic")
  private String profilePic;
  @JsonProperty("applicant_id")
  private int applicantId;
  @JsonProperty("offer")
  private float offer;
  @JsonProperty("delivery_place")
  private String delivery_place;
  @JsonProperty("is_offered")
  private int is_offered;

  public Applicant() {
  }

  public int getJobId() {
    return jobId;
  }

  public void setJobId(int jobId) {
    this.jobId = jobId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public int getApplicantStatus() {
    return applicantStatus;
  }

  public void setApplicantStatus(int applicantStatus) {
    this.applicantStatus = applicantStatus;
  }

  public int getReviews() {
    return reviews;
  }

  public void setReviews(int reviews) {
    this.reviews = reviews;
  }

  public String getProfilePic() {
    return profilePic;
  }

  public void setProfilePic(String profilePic) {
    this.profilePic = profilePic;
  }

  public int getApplicantId() {
    return applicantId;
  }

  public void setApplicantId(int applicantId) {
    this.applicantId = applicantId;
  }

  public float getOffer() {
    return offer;
  }

  public void setOffer(float offer) {
    this.offer = offer;
  }

  public String getDelivery_place() {
    return delivery_place;
  }

  public void setDelivery_place(String delivery_place) {
    this.delivery_place = delivery_place;
  }

  public int getIs_offered() {
    return is_offered;
  }

  public Applicant setIs_offered(int is_offered) {
    this.is_offered = is_offered;
    return this;
  }

  protected Applicant(Parcel in) {
    this.jobId = in.readInt();
    this.userName = in.readString();
    this.userId = in.readInt();
    this.applicantStatus = in.readInt();
    this.reviews = in.readInt();
    this.profilePic = in.readString();
    this.applicantId = in.readInt();
    this.is_offered = in.readInt();
    this.delivery_place = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.jobId);
    dest.writeString(this.userName);
    dest.writeInt(this.userId);
    dest.writeInt(this.applicantStatus);
    dest.writeInt(this.reviews);
    dest.writeString(this.profilePic);
    dest.writeInt(this.applicantId);
    dest.writeInt(this.is_offered);
    dest.writeString(this.delivery_place);
  }
}
