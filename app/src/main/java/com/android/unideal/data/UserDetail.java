package com.android.unideal.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by CS02 on 11/8/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDetail {
  @JsonProperty("name")
  private String name;
  @JsonProperty("email_address")
  private String emailAddress;
  @JsonProperty("gender")
  private String gender;
  @JsonProperty("os_type")
  private String osType;
  @JsonProperty("my_referral_code")
  private String myReferralCode;
  @JsonProperty("from_social_network")
  private Integer fromSocialNetwork;
  @JsonProperty("facebook_id")
  private String facebookId;
  @JsonProperty("googleplus_id")
  private String googleplusId;
  @JsonProperty("language")
  private String language;
  @JsonProperty("jobstatus_notification")
  private int jobstatusNotification;
  @JsonProperty("transaction_notification")
  private int transactionNotification;
  @JsonProperty("newjob_notification")
  private int newjobNotification;
  @JsonProperty("app_tour_guide")
  private int app_tour_guide;
  @JsonProperty("created_on")
  private String createdOn;
  @JsonProperty("user_type")
  private int userType;
  @JsonProperty("profile_pic")
  private String profilePic;
  @JsonProperty("phone_number")
  private String phoneNumber;
  @JsonProperty("referral_code")
  private String referralCode;
  @JsonProperty("user_id")
  private Integer userId;
  @JsonProperty("birth_date")
  private String birthDate;
  @JsonProperty("device_token")
  private String deviceToken;
  @JsonProperty("status")
  private int status;
  @JsonProperty("total_earnings")
  private int total_earnings;
  @JsonProperty("default_card")
  private String default_card;
  @JsonProperty("user_expertise")
  private int user_expertise;
  @JsonProperty("user_doc")
  private String user_doc;
  @JsonProperty("bio")
  private String bio;

  @JsonProperty("device_token")
  public String getDeviceToken() {
    return deviceToken;
  }

  @JsonProperty("device_token")
  public void setDeviceToken(String deviceToken) {
    this.deviceToken = deviceToken;
  }

  @JsonProperty("bio")
  public String getBio() {
    return bio;
  }

  @JsonProperty("bio")
  public void setBio(String bio) {
    this.bio = bio;
  }

  /**
   * @return The name
   */
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  /**
   * @param name The name
   */
  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return The emailAddress
   */
  @JsonProperty("email_address")
  public String getEmailAddress() {
    return emailAddress;
  }

  /**
   * @param emailAddress The email_address
   */
  @JsonProperty("email_address")
  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  /**
   * @return The gender
   */
  @JsonProperty("gender")
  public String getGender() {
    return gender;
  }

  /**
   * @param gender The gender
   */
  @JsonProperty("gender")
  public void setGender(String gender) {
    this.gender = gender;
  }

  @JsonProperty("status")
  public int getStatus() {
    return status;
  }

  @JsonProperty("status")
  public void setStatus(int status) {
    this.status = status;
  }

  @JsonProperty("total_earnings")
  public int getTotal_earnings() {
    return total_earnings;
  }

  @JsonProperty("total_earnings")
  public void setTotal_earnings(int total_earnings) {
    this.total_earnings = total_earnings;
  }

  @JsonProperty("default_card")
  public String getDefault_card() {
    return default_card;
  }

  @JsonProperty("default_card")
  public void setDefault_card(String default_card) {
    this.default_card = default_card;
  }

  @JsonProperty("user_expertise")
  public int getUser_expertise() {
    return user_expertise;
  }

  @JsonProperty("user_expertise")
  public void setUser_expertise(int user_expertise) {
    this.user_expertise = user_expertise;
  }

  @JsonProperty("user_doc")
  public String getUser_doc() {
    return user_doc;
  }

  @JsonProperty("user_doc")
  public void setUser_doc(String user_doc) {
    this.user_doc = user_doc;
  }

  /**
   * @return The osType
   */
  @JsonProperty("os_type")
  public String getOsType() {
    return osType;
  }

  /**
   * @param osType The os_type
   */
  @JsonProperty("os_type")
  public void setOsType(String osType) {
    this.osType = osType;
  }

  /**
   * @return The myReferralCode
   */
  @JsonProperty("my_referral_code")
  public String getMyReferralCode() {
    return myReferralCode;
  }

  /**
   * @param myReferralCode The my_referral_code
   */
  @JsonProperty("my_referral_code")
  public void setMyReferralCode(String myReferralCode) {
    this.myReferralCode = myReferralCode;
  }

  /**
   * @return The fromSocialNetwork
   */
  @JsonProperty("from_social_network")
  public Integer getFromSocialNetwork() {
    return fromSocialNetwork;
  }

  /**
   * @param fromSocialNetwork The from_social_network
   */
  @JsonProperty("from_social_network")
  public void setFromSocialNetwork(Integer fromSocialNetwork) {
    this.fromSocialNetwork = fromSocialNetwork;
  }

  /**
   * @return The facebookId
   */
  @JsonProperty("facebook_id")
  public String getFacebookId() {
    return facebookId;
  }

  /**
   * @param facebookId The facebook_id
   */
  @JsonProperty("facebook_id")
  public void setFacebookId(String facebookId) {
    this.facebookId = facebookId;
  }

  /**
   * @return The googleplusId
   */
  @JsonProperty("googleplus_id")
  public String getGoogleplusId() {
    return googleplusId;
  }

  /**
   * @param googleplusId The googleplus_id
   */
  @JsonProperty("googleplus_id")
  public void setGoogleplusId(String googleplusId) {
    this.googleplusId = googleplusId;
  }

  /**
   * @return The language
   */
  @JsonProperty("language")
  public String getLanguage() {
    return language;
  }

  /**
   * @param language The language
   */
  @JsonProperty("language")
  public void setLanguage(String language) {
    this.language = language;
  }

  /**
   * @return The createdOn
   */
  @JsonProperty("created_on")
  public String getCreatedOn() {
    return createdOn;
  }

  /**
   * @param createdOn The created_on
   */
  @JsonProperty("created_on")
  public void setCreatedOn(String createdOn) {
    this.createdOn = createdOn;
  }

  /**
   * @return The userType
   */
  @JsonProperty("user_type")
  public int getUserType() {
    return userType;
  }

  /**
   * @param userType The user_type
   */
  @JsonProperty("user_type")
  public void setUserType(int userType) {
    this.userType = userType;
  }

  /**
   * @return The profilePic
   */
  @JsonProperty("profile_pic")
  public String getProfilePic() {
    return profilePic;
  }

  /**
   * @param profilePic The profile_pic
   */
  @JsonProperty("profile_pic")
  public void setProfilePic(String profilePic) {
    this.profilePic = profilePic;
  }

  /**
   * @return The phoneNumber
   */
  @JsonProperty("phone_number")
  public String getPhoneNumber() {
    return phoneNumber;
  }

  /**
   * @param phoneNumber The phone_number
   */
  @JsonProperty("phone_number")
  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  /**
   * @return The referralCode
   */
  @JsonProperty("referral_code")
  public String getReferralCode() {
    return referralCode;
  }

  /**
   * @param referralCode The referral_code
   */
  @JsonProperty("referral_code")
  public void setReferralCode(String referralCode) {
    this.referralCode = referralCode;
  }

  /**
   * @return The userId
   */
  @JsonProperty("user_id")
  public Integer getUserId() {
    return userId;
  }

  /**
   * @param userId The user_id
   */
  @JsonProperty("user_id")
  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  /**
   * @return The birthDate
   */
  @JsonProperty("birth_date")
  public String getBirthDate() {
    return birthDate;
  }

  /**
   * @param birthDate The birth_date
   */
  @JsonProperty("birth_date")
  public void setBirthDate(String birthDate) {
    this.birthDate = birthDate;
  }

  public int getJobstatusNotification() {
    return jobstatusNotification;
  }

  public void setJobstatusNotification(int jobstatusNotification) {
    this.jobstatusNotification = jobstatusNotification;
  }

  public int getTransactionNotification() {
    return transactionNotification;
  }

  public void setTransactionNotification(int transactionNotification) {
    this.transactionNotification = transactionNotification;
  }

  public int getNewjobNotification() {
    return newjobNotification;
  }

  public void setNewjobNotification(int newjobNotification) {
    this.newjobNotification = newjobNotification;
  }

  public int getApp_tour_guide() {
    return app_tour_guide;
  }

  public void setApp_tour_guide(int app_tour_guide) {
    this.app_tour_guide = app_tour_guide;
  }
}
