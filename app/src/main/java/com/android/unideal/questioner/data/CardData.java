package com.android.unideal.questioner.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by bhavdip on 26/12/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardData implements Parcelable {

  public static final Parcelable.Creator<CardData> CREATOR = new Parcelable.Creator<CardData>() {
    @Override
    public CardData createFromParcel(Parcel source) {
      return new CardData(source);
    }

    @Override
    public CardData[] newArray(int size) {
      return new CardData[size];
    }
  };
  private int expire_month;
  private int expire_year;
  private String payer_id;
  private String external_customer_id;
  private String merchant_id;
  private String type;
  private String id;
  private String number;
  private String state;
  private int cvv;
  private String first_name;
  private String last_name;

  public CardData() {
  }

  protected CardData(Parcel in) {
    this.expire_month = in.readInt();
    this.expire_year = in.readInt();
    this.payer_id = in.readString();
    this.type = in.readString();
    this.id = in.readString();
    this.number = in.readString();
    this.cvv = in.readInt();
    this.first_name = in.readString();
    this.last_name = in.readString();
    this.state = in.readString();
    this.external_customer_id = in.readString();
    this.merchant_id = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.expire_month);
    dest.writeInt(this.expire_year);
    dest.writeString(this.payer_id);
    dest.writeString(this.type);
    dest.writeString(this.id);
    dest.writeString(this.number);
    dest.writeInt(this.cvv);
    dest.writeString(this.first_name);
    dest.writeString(this.last_name);
    dest.writeString(this.state);
    dest.writeString(this.external_customer_id);
    dest.writeString(this.merchant_id);
  }

  public int getExpire_month() {
    return expire_month;
  }

  public CardData setExpire_month(int expire_month) {
    this.expire_month = expire_month;
    return this;
  }

  public int getExpire_year() {
    return expire_year;
  }

  public CardData setExpire_year(int expire_year) {
    this.expire_year = expire_year;
    return this;
  }

  public String getPayer_id() {
    return payer_id;
  }

  public CardData setPayer_id(String payer_id) {
    this.payer_id = payer_id;
    return this;
  }

  public String getType() {
    return type;
  }

  public CardData setType(String type) {
    this.type = type;
    return this;
  }

  public String getId() {
    return id;
  }

  public CardData setId(String id) {
    this.id = id;
    return this;
  }

  public String getNumber() {
    return number;
  }

  public CardData setNumber(String number) {
    this.number = number;
    return this;
  }

  public int getCvv() {
    return cvv;
  }

  public CardData setCvv(int cvv) {
    this.cvv = cvv;
    return this;
  }

  public String getFirst_name() {
    return first_name;
  }

  public CardData setFirst_name(String first_name) {
    this.first_name = first_name;
    return this;
  }

  public String getLast_name() {
    return last_name;
  }

  public CardData setLast_name(String last_name) {
    this.last_name = last_name;
    return this;
  }

  public String getState() {
    return state;
  }

  public CardData setState(String state) {
    this.state = state;
    return this;
  }

  public String getExternal_customer_id() {
    return external_customer_id;
  }

  public CardData setExternal_customer_id(String external_customer_id) {
    this.external_customer_id = external_customer_id;
    return this;
  }

  public String getMerchant_id() {
    return merchant_id;
  }

  public CardData setMerchant_id(String merchant_id) {
    this.merchant_id = merchant_id;
    return this;
  }
}
