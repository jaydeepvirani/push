package com.android.unideal.data.socket;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bhavdip on 6/12/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MediaData {
  @JsonProperty("text")
  private String text;
  @JsonProperty("image_url")
  private String imageUrl;
  @JsonProperty("height")
  private int height;
  @JsonProperty("width")
  private int width;
  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  /**
   * @return The text
   */
  @JsonProperty("text")
  public String getText() {
    return text;
  }

  /**
   * @param text The text
   */
  @JsonProperty("text")
  public void setText(String text) {
    this.text = text;
  }

  /**
   * @return The imageUrl
   */
  @JsonProperty("image_url")
  public String getImageUrl() {
    return imageUrl;
  }

  /**
   * @param imageUrl The image_url
   */
  @JsonProperty("image_url")
  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  /**
   * @return The height
   */
  @JsonProperty("height")
  public int getHeight() {
    return height;
  }

  /**
   * @param height The height
   */
  @JsonProperty("height")
  public void setHeight(int height) {
    this.height = height;
  }

  /**
   * @return The width
   */
  @JsonProperty("width")
  public int getWidth() {
    return width;
  }

  /**
   * @param width The width
   */
  @JsonProperty("width")
  public void setWidth(int width) {
    this.width = width;
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
