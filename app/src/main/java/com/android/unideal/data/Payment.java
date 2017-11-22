package com.android.unideal.data;

/**
 * Created by bhavdip on 5/1/17.
 */

public class Payment {
  private String secret;
  private String clientid;
  private String url;

  public String getSecret() {
    return secret;
  }

  public Payment setSecret(String secret) {
    this.secret = secret;
    return this;
  }

  public String getClientid() {
    return clientid;
  }

  public Payment setClientid(String clientid) {
    this.clientid = clientid;
    return this;
  }

  public String getUrl() {
    return url;
  }

  public Payment setUrl(String url) {
    this.url = url;
    return this;
  }
}
