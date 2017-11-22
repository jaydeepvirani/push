package com.android.unideal.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bhavdip on 12/27/16.
 */

public class AgentOfferData implements Parcelable {

  public static final Parcelable.Creator<AgentOfferData> CREATOR =
      new Parcelable.Creator<AgentOfferData>() {
        @Override
        public AgentOfferData createFromParcel(Parcel source) {
          return new AgentOfferData(source);
        }

        @Override
        public AgentOfferData[] newArray(int size) {
          return new AgentOfferData[size];
        }
      };
  private int requester_id;
  private int agent_id;
  private int job_id;
  private float consignment_size;
  private String message;

  public AgentOfferData() {
  }

  protected AgentOfferData(Parcel in) {
    this.requester_id = in.readInt();
    this.agent_id = in.readInt();
    this.job_id = in.readInt();
    this.consignment_size = in.readFloat();
    this.message = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.requester_id);
    dest.writeInt(this.agent_id);
    dest.writeInt(this.job_id);
    dest.writeFloat(this.consignment_size);
    dest.writeString(this.message);
  }

  public int getRequester_id() {
    return requester_id;
  }

  public AgentOfferData setRequester_id(int requester_id) {
    this.requester_id = requester_id;
    return this;
  }

  public int getAgent_id() {
    return agent_id;
  }

  public AgentOfferData setAgent_id(int agent_id) {
    this.agent_id = agent_id;
    return this;
  }

  public int getJob_id() {
    return job_id;
  }

  public AgentOfferData setJob_id(int job_id) {
    this.job_id = job_id;
    return this;
  }

  public float getConsignment_size() {
    return consignment_size;
  }

  public AgentOfferData setConsignment_size(float consignment_size) {
    this.consignment_size = consignment_size;
    return this;
  }

  public String getMessage() {
    return message;
  }

  public AgentOfferData setMessage(String message) {
    this.message = message;
    return this;
  }
}
