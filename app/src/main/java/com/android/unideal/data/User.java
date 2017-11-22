package com.android.unideal.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bhavdip on 10/1/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Parcelable {
  public static final Creator<User> CREATOR = new Creator<User>() {
    @Override
    public User createFromParcel(Parcel source) {
      return new User(source);
    }

    @Override
    public User[] newArray(int size) {
      return new User[size];
    }
  };
  @JsonProperty("name")
  private String name;
  @JsonProperty("agent_ratings")
  private float agentRatting;
  @JsonProperty("profile_pic")
  private String profilePic;
  @JsonProperty("bio")
  private String bio;
  @JsonProperty("user_doc")
  private String userDoc;
  @JsonProperty("reviews")
  private String reviews;
  @JsonProperty("expertise_id")
  private String expertiseId;
  @JsonProperty("category_id")
  private String categoryId;
  @JsonProperty("completed_jobs")
  private int completedJobs;

  public User() {
  }

  protected User(Parcel in) {
    this.name = in.readString();
    this.agentRatting = in.readFloat();
    this.profilePic = in.readString();
    this.bio = in.readString();
    this.userDoc = in.readString();
    this.reviews = in.readString();
    this.expertiseId = in.readString();
    this.categoryId = in.readString();
    this.completedJobs = in.readInt();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public float getAgentRatting() {
    return agentRatting;
  }

  public void setAgentRatting(float agentRatting) {
    this.agentRatting = agentRatting;
  }

  public String getProfilePic() {
    return profilePic;
  }

  public void setProfilePic(String profilePic) {
    this.profilePic = profilePic;
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public String getUserDoc() {
    return userDoc;
  }

  public void setUserDoc(String userDoc) {
    this.userDoc = userDoc;
  }

  public String getReviews() {
    return reviews;
  }

  public void setReviews(String reviews) {
    this.reviews = reviews;
  }

  public String getExpertiseId() {
    return expertiseId;
  }

  public void setExpertiseId(String expertiseId) {
    this.expertiseId = expertiseId;
  }

  public String getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(String categoryId) {
    this.categoryId = categoryId;
  }

  public int getCompletedJobs() {
    return completedJobs;
  }

  public void setCompletedJobs(int completedJobs) {
    this.completedJobs = completedJobs;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.name);
    dest.writeFloat(this.agentRatting);
    dest.writeString(this.profilePic);
    dest.writeString(this.bio);
    dest.writeString(this.userDoc);
    dest.writeString(this.reviews);
    dest.writeString(this.expertiseId);
    dest.writeString(this.categoryId);
    dest.writeInt(this.completedJobs);
  }
}
