package com.android.unideal.rest.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bhavdip on 16/11/16.
 */

public class NewJobResponse {

  @JsonProperty("user_id")
  private Integer userId;
  @JsonProperty("category_id")
  private Integer categoryId;
  @JsonProperty("job_title")
  private String jobTitle;
  @JsonProperty("job_details")
  private String jobDetails;
  @JsonProperty("agent_commision")
  private Integer agentCommision;
  @JsonProperty("consignment_size")
  private Integer consignmentSize;
  @JsonProperty("applicant")
  private Integer applicant;
  @JsonProperty("job_status")
  private Integer jobStatus;
  @JsonProperty("status")
  private Integer status;
  @JsonProperty("job_start_on")
  private String jobStartOn;
  @JsonProperty("job_end_on")
  private String jobEndOn;
  @JsonProperty("service_fees_advanced")
  private Integer serviceFeesAdvanced;
  @JsonProperty("advanced_charge")
  private Integer advancedCharge;
  @JsonProperty("commision_charge")
  private Integer commisionCharge;
  @JsonProperty("job_id")
  private Integer jobId;
  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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
   * @return The categoryId
   */
  @JsonProperty("category_id")
  public Integer getCategoryId() {
    return categoryId;
  }

  /**
   * @param categoryId The category_id
   */
  @JsonProperty("category_id")
  public void setCategoryId(Integer categoryId) {
    this.categoryId = categoryId;
  }

  /**
   * @return The jobTitle
   */
  @JsonProperty("job_title")
  public String getJobTitle() {
    return jobTitle;
  }

  /**
   * @param jobTitle The job_title
   */
  @JsonProperty("job_title")
  public void setJobTitle(String jobTitle) {
    this.jobTitle = jobTitle;
  }

  /**
   * @return The jobDetails
   */
  @JsonProperty("job_details")
  public String getJobDetails() {
    return jobDetails;
  }

  /**
   * @param jobDetails The job_details
   */
  @JsonProperty("job_details")
  public void setJobDetails(String jobDetails) {
    this.jobDetails = jobDetails;
  }

  /**
   * @return The agentCommision
   */
  @JsonProperty("agent_commision")
  public Integer getAgentCommision() {
    return agentCommision;
  }

  /**
   * @param agentCommision The agent_commision
   */
  @JsonProperty("agent_commision")
  public void setAgentCommision(Integer agentCommision) {
    this.agentCommision = agentCommision;
  }

  /**
   * @return The consignmentSize
   */
  @JsonProperty("consignment_size")
  public Integer getConsignmentSize() {
    return consignmentSize;
  }

  /**
   * @param consignmentSize The consignment_size
   */
  @JsonProperty("consignment_size")
  public void setConsignmentSize(Integer consignmentSize) {
    this.consignmentSize = consignmentSize;
  }

  /**
   * @return The applicant
   */
  @JsonProperty("applicant")
  public Integer getApplicant() {
    return applicant;
  }

  /**
   * @param applicant The applicant
   */
  @JsonProperty("applicant")
  public void setApplicant(Integer applicant) {
    this.applicant = applicant;
  }

  /**
   * @return The jobStatus
   */
  @JsonProperty("job_status")
  public Integer getJobStatus() {
    return jobStatus;
  }

  /**
   * @param jobStatus The job_status
   */
  @JsonProperty("job_status")
  public void setJobStatus(Integer jobStatus) {
    this.jobStatus = jobStatus;
  }

  /**
   * @return The status
   */
  @JsonProperty("status")
  public Integer getStatus() {
    return status;
  }

  /**
   * @param status The status
   */
  @JsonProperty("status")
  public void setStatus(Integer status) {
    this.status = status;
  }

  /**
   * @return The jobStartOn
   */
  @JsonProperty("job_start_on")
  public String getJobStartOn() {
    return jobStartOn;
  }

  /**
   * @param jobStartOn The job_start_on
   */
  @JsonProperty("job_start_on")
  public void setJobStartOn(String jobStartOn) {
    this.jobStartOn = jobStartOn;
  }

  /**
   * @return The jobEndOn
   */
  @JsonProperty("job_end_on")
  public String getJobEndOn() {
    return jobEndOn;
  }

  /**
   * @param jobEndOn The job_end_on
   */
  @JsonProperty("job_end_on")
  public void setJobEndOn(String jobEndOn) {
    this.jobEndOn = jobEndOn;
  }

  /**
   * @return The serviceFeesAdvanced
   */
  @JsonProperty("service_fees_advanced")
  public Integer getServiceFeesAdvanced() {
    return serviceFeesAdvanced;
  }

  /**
   * @param serviceFeesAdvanced The service_fees_advanced
   */
  @JsonProperty("service_fees_advanced")
  public void setServiceFeesAdvanced(Integer serviceFeesAdvanced) {
    this.serviceFeesAdvanced = serviceFeesAdvanced;
  }

  /**
   * @return The advancedCharge
   */
  @JsonProperty("advanced_charge")
  public Integer getAdvancedCharge() {
    return advancedCharge;
  }

  /**
   * @param advancedCharge The advanced_charge
   */
  @JsonProperty("advanced_charge")
  public void setAdvancedCharge(Integer advancedCharge) {
    this.advancedCharge = advancedCharge;
  }

  /**
   * @return The commisionCharge
   */
  @JsonProperty("commision_charge")
  public Integer getCommisionCharge() {
    return commisionCharge;
  }

  /**
   * @param commisionCharge The commision_charge
   */
  @JsonProperty("commision_charge")
  public void setCommisionCharge(Integer commisionCharge) {
    this.commisionCharge = commisionCharge;
  }

  /**
   * @return The jobId
   */
  @JsonProperty("job_id")
  public Integer getJobId() {
    return jobId;
  }

  /**
   * @param jobId The job_id
   */
  @JsonProperty("job_id")
  public void setJobId(Integer jobId) {
    this.jobId = jobId;
  }

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }
}
