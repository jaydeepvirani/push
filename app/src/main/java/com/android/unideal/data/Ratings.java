package com.android.unideal.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ADMIN on 05-10-2016.
 */

public class Ratings implements Parcelable {
  public static final Parcelable.Creator<Ratings> CREATOR = new Parcelable.Creator<Ratings>() {
    @Override
    public Ratings createFromParcel(Parcel source) {
      return new Ratings(source);
    }

    @Override
    public Ratings[] newArray(int size) {
      return new Ratings[size];
    }
  };
  private float ratings;

  public Ratings() {
  }

  protected Ratings(Parcel in) {
    this.ratings = in.readFloat();
  }

  public float getRatings() {
    return ratings;
  }

  public Ratings setRatings(float ratings) {
    this.ratings = ratings;
    return this;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeFloat(this.ratings);
  }
}
