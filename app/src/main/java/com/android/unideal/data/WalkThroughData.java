package com.android.unideal.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ADMIN on 13-01-2017.
 */

public class WalkThroughData implements Parcelable {

  public static final Parcelable.Creator<WalkThroughData> CREATOR =
      new Parcelable.Creator<WalkThroughData>() {
        @Override
        public WalkThroughData createFromParcel(Parcel source) {
          return new WalkThroughData(source);
        }

        @Override
        public WalkThroughData[] newArray(int size) {
          return new WalkThroughData[size];
        }
      };
  int drawableResourceId;
  int stringResorId;

  public WalkThroughData() {
  }

  protected WalkThroughData(Parcel in) {
    this.drawableResourceId = in.readInt();
    this.stringResorId = in.readInt();
  }

  public int getDrawableResourceId() {
    return drawableResourceId;
  }

  public void setDrawableResourceId(int drawableResourceId) {
    this.drawableResourceId = drawableResourceId;
  }

  public int getStringResorId() {
    return stringResorId;
  }

  public void setStringResorId(int stringResorId) {
    this.stringResorId = stringResorId;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.drawableResourceId);
    dest.writeInt(this.stringResorId);
  }
}
