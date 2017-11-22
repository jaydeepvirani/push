package com.android.unideal.questioner.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ADMIN on 19-10-2016.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BankAccountData implements Parcelable {

  public static final Parcelable.Creator<BankAccountData> CREATOR =
      new Parcelable.Creator<BankAccountData>() {
        @Override
        public BankAccountData createFromParcel(Parcel source) {
          return new BankAccountData(source);
        }

        @Override
        public BankAccountData[] newArray(int size) {
          return new BankAccountData[size];
        }
      };
  @JsonProperty("bank_details_id")
  private String bank_id;
  @JsonProperty("bank_name")
  private String bank_name;
  @JsonProperty("account_number")
  private String bank_account_number;
  @JsonProperty("swift_code")
  private String bank_swift_code;
  @JsonProperty("bank_created_on")
  private String bank_created_on;
  @JsonProperty("is_default")
  private int is_default;
  @JsonProperty("user_id")
  private int user_id;

  public BankAccountData() {
  }

  protected BankAccountData(Parcel in) {
    this.bank_id = in.readString();
    this.bank_name = in.readString();
    this.bank_account_number = in.readString();
    this.bank_swift_code = in.readString();
    this.bank_created_on = in.readString();
    this.is_default = in.readInt();
    this.user_id = in.readInt();
  }

  public String getBank_id() {
    return bank_id;
  }

  public void setBank_id(String bank_id) {
    this.bank_id = bank_id;
  }

  public String getBank_name() {
    return bank_name;
  }

  public void setBank_name(String bank_name) {
    this.bank_name = bank_name;
  }

  public String getBank_account_number() {
    return bank_account_number;
  }

  public void setBank_account_number(String bank_account_number) {
    this.bank_account_number = bank_account_number;
  }

  public String getBank_swift_code() {
    return bank_swift_code;
  }

  public void setBank_swift_code(String bank_swift_code) {
    this.bank_swift_code = bank_swift_code;
  }

  public String getBank_created_on() {
    return bank_created_on;
  }

  public void setBank_created_on(String bank_created_on) {
    this.bank_created_on = bank_created_on;
  }

  public int getIs_default() {
    return is_default;
  }

  public void setIs_default(int is_default) {
    this.is_default = is_default;
  }

  public int getUser_id() {
    return user_id;
  }

  public void setUser_id(int user_id) {
    this.user_id = user_id;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.bank_id);
    dest.writeString(this.bank_name);
    dest.writeString(this.bank_account_number);
    dest.writeString(this.bank_swift_code);
    dest.writeString(this.bank_created_on);
    dest.writeInt(this.is_default);
    dest.writeInt(this.user_id);
  }
}