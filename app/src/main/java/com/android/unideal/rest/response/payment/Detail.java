package com.android.unideal.rest.response.payment;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bhavdip on 1/1/17.
 */

public class Detail {

  @JsonProperty("field")
  private String field;
  @JsonProperty("issue")
  private String issue;
  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  @JsonProperty("field")
  public String getField() {
    return field;
  }

  @JsonProperty("field")
  public void setField(String field) {
    this.field = field;
  }

  @JsonProperty("issue")
  public String getIssue() {
    return issue;
  }

  @JsonProperty("issue")
  public void setIssue(String issue) {
    this.issue = issue;
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
