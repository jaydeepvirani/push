package com.android.unideal.rest.response;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADMIN on 14-11-2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RattingResponse implements Parcelable {

  public static final Parcelable.Creator<RattingResponse> CREATOR =
      new Parcelable.Creator<RattingResponse>() {
        @Override
        public RattingResponse createFromParcel(Parcel source) {
          return new RattingResponse(source);
        }

        @Override
        public RattingResponse[] newArray(int size) {
          return new RattingResponse[size];
        }
      };
  @JsonProperty("average_ratings")
  private float avg_ratting;
  @JsonProperty("job_ratings")
  private List<JobRatting> jobRattingsList = new ArrayList<>();

  public RattingResponse() {
  }

  protected RattingResponse(Parcel in) {
    this.avg_ratting = in.readFloat();
    this.jobRattingsList = new ArrayList<JobRatting>();
    in.readList(this.jobRattingsList, JobRatting.class.getClassLoader());
  }

  public float getAvg_ratting() {
    return avg_ratting;
  }

  public void setAvg_ratting(float avg_ratting) {
    this.avg_ratting = avg_ratting;
  }

  public List<JobRatting> getJobRattingsList() {
    return jobRattingsList;
  }

  public void setJobRattingsList(List<JobRatting> jobRattingsList) {
    this.jobRattingsList = jobRattingsList;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeFloat(this.avg_ratting);
    dest.writeList(this.jobRattingsList);
  }
}
