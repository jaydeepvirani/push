package com.android.unideal.rest.response;

import com.android.unideal.data.Admin;
import com.android.unideal.data.Payment;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bhavdip on 14/11/16.
 */
public class AppConfigData {

  @JsonProperty("promocode_status")
  private Integer promocodeStatus;
  @JsonProperty("terms_and_condition")
  private String termsAndCondition;
  @JsonProperty("help_screen_url")
  private String helpScreenUrl;
  @JsonProperty("jobcategory_data")
  private List<Category> category = new ArrayList<Category>();
  @JsonProperty("job_sub_category_data")
  private List<SubCategory> subCategory = new ArrayList<>();
  @JsonProperty("min_range_value")
  private Integer minRangeValue;
  @JsonProperty("max_range_value")
  private Integer maxRangeValue;
  @JsonProperty("admin")
  private Admin admin;
  @JsonProperty("payment")
  private Payment payment;
  @JsonProperty("delivery_place_url")
  private String delivery_place_url;
  @JsonProperty("disclaimer")
  private String disclaimer_url;
  @JsonProperty("agent_terms_condition")
  private String agt_terms_url;

  @JsonProperty("play_store_link")
  private String play_store_link;

  @JsonProperty("app_store_link")
  private String app_store_link;

  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  /**
   * @return The promocodeStatus
   */
  @JsonProperty("promocode_status")
  public Integer getPromocodeStatus() {
    return promocodeStatus;
  }

  /**
   * @param promocodeStatus The promocode_status
   */
  @JsonProperty("promocode_status")
  public void setPromocodeStatus(Integer promocodeStatus) {
    this.promocodeStatus = promocodeStatus;
  }

  @JsonProperty("disclaimer")
  public String getDisclaimer_url() {
    return disclaimer_url;
  }

  @JsonProperty("disclaimer")
  public void setDisclaimer_url(String disclaimer_url) {
    this.disclaimer_url = disclaimer_url;
  }

  @JsonProperty("agent_terms_condition")
  public String getAgt_terms_url() {
    return agt_terms_url;
  }

  @JsonProperty("agent_terms_condition")
  public void setAgt_terms_url(String agt_terms_url) {
    this.agt_terms_url = agt_terms_url;
  }

  @JsonProperty("admin")
  public Admin getAdmin() {
    return admin;
  }

  @JsonProperty("admin")
  public void setAdminEmail(Admin admin) {
    this.admin = admin;
  }

  /**
   * @return The termsAndCondition
   */
  @JsonProperty("terms_and_condition")
  public String getTermsAndCondition() {
    return termsAndCondition;
  }

  /**
   * @param termsAndCondition The terms_and_condition
   */
  @JsonProperty("terms_and_condition")
  public void setTermsAndCondition(String termsAndCondition) {
    this.termsAndCondition = termsAndCondition;
  }

  @JsonProperty("job_sub_category_data")
  public List<SubCategory> getJobSubCategory() {
    return subCategory;
  }

  @JsonProperty("job_sub_category_data")
  public void setJobSubCategory(List<SubCategory> subCategory) {
    this.subCategory = subCategory;
  }

  /**
   * @return The helpScreenUrl
   */
  @JsonProperty("help_screen_url")
  public String getHelpScreenUrl() {
    return helpScreenUrl;
  }

  /**
   * @param helpScreenUrl The help_screen_url
   */
  @JsonProperty("help_screen_url")
  public void setHelpScreenUrl(String helpScreenUrl) {
    this.helpScreenUrl = helpScreenUrl;
  }

  /**
   * @return The category
   */
  @JsonProperty("category")
  public List<Category> getCategory() {
    return category;
  }

  /**
   * @param category The category
   */
  @JsonProperty("category")
  public void setCategory(List<Category> category) {
    this.category = category;
  }

  /**
   * @return The minRangeValue
   */
  @JsonProperty("min_range_value")
  public Integer getMinRangeValue() {
    return minRangeValue;
  }

  /**
   * @param minRangeValue The min_range_value
   */
  @JsonProperty("min_range_value")
  public void setMinRangeValue(Integer minRangeValue) {
    this.minRangeValue = minRangeValue;
  }

  /**
   * @return The maxRangeValue
   */
  @JsonProperty("max_range_value")
  public Integer getMaxRangeValue() {
    return maxRangeValue;
  }

  /**
   * @param maxRangeValue The max_range_value
   */
  @JsonProperty("max_range_value")
  public void setMaxRangeValue(Integer maxRangeValue) {
    this.maxRangeValue = maxRangeValue;
  }

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }

  public Payment getPayment() {
    return payment;
  }

  public AppConfigData setPayment(Payment payment) {
    this.payment = payment;
    return this;
  }

  public String getDelivery_place_url() {
    return delivery_place_url;
  }

  public AppConfigData setDelivery_place_url(String delivery_place_url) {
    this.delivery_place_url = delivery_place_url;
    return this;
  }

  public String getPlay_store_link() {
    return play_store_link;
  }

  public AppConfigData setPlay_store_link(String play_store_link) {
    this.play_store_link = play_store_link;
    return this;
  }

  public String getApp_store_link() {
    return app_store_link;
  }

  public AppConfigData setApp_store_link(String app_store_link) {
    this.app_store_link = app_store_link;
    return this;
  }
}