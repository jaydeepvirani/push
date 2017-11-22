package com.android.unideal.rest.response.payment;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bhavdip on 1/1/17.
 */

public class VaultError {

  @JsonProperty("name")
  private String name;
  @JsonProperty("debug_id")
  private String debugId;
  @JsonProperty("message")
  private String message;
  @JsonProperty("information_link")
  private String informationLink;
  @JsonProperty("details")
  private List<Detail> details = null;
  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("debug_id")
  public String getDebugId() {
    return debugId;
  }

  @JsonProperty("debug_id")
  public void setDebugId(String debugId) {
    this.debugId = debugId;
  }

  @JsonProperty("message")
  public String getMessage() {
    return message;
  }

  @JsonProperty("message")
  public void setMessage(String message) {
    this.message = message;
  }

  @JsonProperty("information_link")
  public String getInformationLink() {
    return informationLink;
  }

  @JsonProperty("information_link")
  public void setInformationLink(String informationLink) {
    this.informationLink = informationLink;
  }

  @JsonProperty("details")
  public List<Detail> getDetails() {
    return details;
  }

  @JsonProperty("details")
  public void setDetails(List<Detail> details) {
    this.details = details;
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
