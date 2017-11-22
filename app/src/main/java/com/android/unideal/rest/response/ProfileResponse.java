package com.android.unideal.rest.response;

import android.os.Parcel;
import android.os.Parcelable;
import com.android.unideal.data.JobDetail;
import com.android.unideal.data.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADMIN on 14-11-2016.
 */

public class ProfileResponse implements Parcelable {
  public static final Parcelable.Creator<ProfileResponse> CREATOR =
      new Parcelable.Creator<ProfileResponse>() {
        @Override
        public ProfileResponse createFromParcel(Parcel source) {
          return new ProfileResponse(source);
        }

        @Override
        public ProfileResponse[] newArray(int size) {
          return new ProfileResponse[size];
        }
      };
  @JsonProperty("user_profile")
  private User response;
  @JsonProperty("user_jobs")
  private List<JobDetail> userJobList = new ArrayList<>();

  public ProfileResponse() {
  }

  protected ProfileResponse(Parcel in) {
    this.response = in.readParcelable(User.class.getClassLoader());
    this.userJobList = in.createTypedArrayList(JobDetail.CREATOR);
  }

  public User getResponse() {
    return response;
  }

  public void setResponse(User response) {
    this.response = response;
  }

  public List<JobDetail> getUserJobList() {
    return userJobList;
  }

  public void setUserJobList(List<JobDetail> userJobList) {
    this.userJobList = userJobList;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(this.response, flags);
    dest.writeTypedList(userJobList);
  }
}
