package com.android.unideal.questioner.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenData implements Parcelable {
  public static final Parcelable.Creator<TokenData> CREATOR = new Parcelable.Creator<TokenData>() {
    @Override
    public TokenData createFromParcel(Parcel source) {
      return new TokenData(source);
    }

    @Override
    public TokenData[] newArray(int size) {
      return new TokenData[size];
    }
  };
  private String scope;
  private String nonce;
  private String access_token;
  private String token_type;
  private String app_id;
  private String expires_in;

  public TokenData() {
  }

  protected TokenData(Parcel in) {
    this.scope = in.readString();
    this.nonce = in.readString();
    this.access_token = in.readString();
    this.token_type = in.readString();
    this.app_id = in.readString();
    this.expires_in = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.scope);
    dest.writeString(this.nonce);
    dest.writeString(this.access_token);
    dest.writeString(this.token_type);
    dest.writeString(this.app_id);
    dest.writeString(this.expires_in);
  }

  public String getScope() {
    return scope;
  }

  public TokenData setScope(String scope) {
    this.scope = scope;
    return this;
  }

  public String getNonce() {
    return nonce;
  }

  public TokenData setNonce(String nonce) {
    this.nonce = nonce;
    return this;
  }

  public String getAccess_token() {
    return access_token;
  }

  public TokenData setAccess_token(String access_token) {
    this.access_token = access_token;
    return this;
  }

  public String getToken_type() {
    return token_type;
  }

  public TokenData setToken_type(String token_type) {
    this.token_type = token_type;
    return this;
  }

  public String getApp_id() {
    return app_id;
  }

  public TokenData setApp_id(String app_id) {
    this.app_id = app_id;
    return this;
  }

  public String getExpires_in() {
    return expires_in;
  }

  public TokenData setExpires_in(String expires_in) {
    this.expires_in = expires_in;
    return this;
  }
}
