package com.android.unideal.rest.response;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ADMIN on 14-11-2016.
 */

public class UserRatting implements Parcelable {
  public static final Parcelable.Creator<UserRatting> CREATOR =
      new Parcelable.Creator<UserRatting>() {
        @Override
        public UserRatting createFromParcel(Parcel source) {
          return new UserRatting(source);
        }

        @Override
        public UserRatting[] newArray(int size) {
          return new UserRatting[size];
        }
      };
  @JsonProperty("ratting")
  private float ratting;
  @JsonProperty("title")
  private String title;
  @JsonProperty("client_name")
  private String client_name;
  @JsonProperty("user_id")
  private String user_id;
  @JsonProperty("user_type")
  private String user_type;

  public UserRatting() {
  }

  protected UserRatting(Parcel in) {
    this.ratting = in.readFloat();
    this.title = in.readString();
    this.client_name = in.readString();
    this.user_id = in.readString();
    this.user_type = in.readString();
  }

  public float getRatting() {
    return ratting;
  }

  public void setRatting(float ratting) {
    this.ratting = ratting;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getClient_name() {
    return client_name;
  }

  public void setClient_name(String client_name) {
    this.client_name = client_name;
  }

  public String getUser_id() {
    return user_id;
  }

  public void setUser_id(String user_id) {
    this.user_id = user_id;
  }

  public String getUser_type() {
    return user_type;
  }

  public void setUser_type(String user_type) {
    this.user_type = user_type;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeFloat(this.ratting);
    dest.writeString(this.title);
    dest.writeString(this.client_name);
    dest.writeString(this.user_id);
    dest.writeString(this.user_type);
  }
}
