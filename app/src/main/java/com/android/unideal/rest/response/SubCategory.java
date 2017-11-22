package com.android.unideal.rest.response;

import android.os.Parcel;
import android.os.Parcelable;
import com.android.unideal.util.Consts;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by ADMIN on 28-11-2016.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class SubCategory implements Parcelable {
  public static final Parcelable.Creator<SubCategory> CREATOR =
      new Parcelable.Creator<SubCategory>() {
        @Override
        public SubCategory createFromParcel(Parcel source) {
          return new SubCategory(source);
        }

        @Override
        public SubCategory[] newArray(int size) {
          return new SubCategory[size];
        }
      };
  private int category_id;
  private int sub_category_id;
  private String sub_category_name;
  private String appMode = Consts.ENGLISH_MODE;
  private String sub_category_name_ch;
  public SubCategory() {
  }

  protected SubCategory(Parcel in) {
    this.category_id = in.readInt();
    this.sub_category_id = in.readInt();
    this.sub_category_name = in.readString();
    this.sub_category_name_ch = in.readString();
  }

  public String getSub_category_name_ch() {
    return sub_category_name_ch;
  }

  public void setSub_category_name_ch(String sub_category_name_ch) {
    this.sub_category_name_ch = sub_category_name_ch;
  }

  public String getAppMode() {
    return appMode;
  }

  public void setAppMode(String appMode) {
    this.appMode = appMode;
  }

  public int getCategory_id() {
    return category_id;
  }

  public SubCategory setCategory_id(int category_id) {
    this.category_id = category_id;
    return this;
  }

  public int getSub_category_id() {
    return sub_category_id;
  }

  public SubCategory setSub_category_id(int sub_category_id) {
    this.sub_category_id = sub_category_id;
    return this;
  }

  public String getSub_category_name() {
    return sub_category_name;
  }

  public SubCategory setSub_category_name(String sub_category_name) {
    this.sub_category_name = sub_category_name;
    return this;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public String toString() {
    if (appMode.equals(Consts.ENGLISH_MODE)) {
      return sub_category_name;
    } else if (appMode.equals(Consts.CHINESE_MODE)) {
      return sub_category_name_ch;
    } else {
      return sub_category_name;
    }
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.category_id);
    dest.writeInt(this.sub_category_id);
    dest.writeString(this.sub_category_name);
  }
}
