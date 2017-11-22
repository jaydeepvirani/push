package com.android.unideal.rest.response;

import com.android.unideal.util.Consts;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bhavdip on 14/11/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Category {

  @JsonProperty("category_id")
  private Integer categoryId;
  @JsonProperty("category_name")
  private String categoryName;
  @JsonProperty("sub_category_id")
  private String subCategoryId;
  @JsonProperty("sub_category_name")
  private String subCategoryName;
  @JsonProperty("category_name_ch")
  private String categoryNameCh;

  @JsonIgnore
  private String appMode = Consts.ENGLISH_MODE;

  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  @JsonProperty("sub_category_id")
  public String getSubCategoryId() {
    return subCategoryId;
  }

  @JsonProperty("sub_category_id")
  public void setSubCategoryId(String subCategoryId) {
    this.subCategoryId = subCategoryId;
  }

  @JsonProperty("sub_category_name")
  public String getSubCategoryName() {
    return subCategoryName;
  }

  @JsonProperty("sub_category_name")
  public void setSubCategoryName(String subCategoryName) {
    this.subCategoryName = subCategoryName;
  }

  @JsonProperty("category_name_ch")
  public String getCategoryNameCh() {
    return categoryNameCh;
  }

  @JsonProperty("category_name_ch")
  public void setCategoryNameCh(String categoryNameCh) {
    this.categoryNameCh = categoryNameCh;
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
   * @return The categoryName
   */
  @JsonProperty("category_name")
  public String getCategoryName() {
    return categoryName;
  }

  /**
   * @param categoryName The category_name
   */
  @JsonProperty("category_name")
  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }

  public String getAppMode() {
    return appMode;
  }

  public void setAppMode(String appMode) {
    this.appMode = appMode;
  }

  @Override
  public String toString() {
    if (appMode.equals(Consts.ENGLISH_MODE)) {
      return categoryName;
    } else if (appMode.equals(Consts.CHINESE_MODE)) {
      return categoryNameCh;
    } else {
      return categoryName;
    }
  }
}