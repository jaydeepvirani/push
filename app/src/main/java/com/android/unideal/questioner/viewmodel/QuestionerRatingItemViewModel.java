package com.android.unideal.questioner.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.text.TextUtils;

import com.android.unideal.rest.response.JobRatting;

/**
 * Created by MRUGESH on 10/15/2016.
 */

public class QuestionerRatingItemViewModel extends BaseObservable {
    private JobRatting agentJobListResponse;
    private Context context;

    public QuestionerRatingItemViewModel(Context context, JobRatting agentJobListResponse) {
        this.context = context;
        this.agentJobListResponse = agentJobListResponse;
    }

    public void setRatingResponse(JobRatting agentJobListResponse) {
        this.agentJobListResponse = agentJobListResponse;
        notifyChange();
    }

    public float getRating() {
        if (agentJobListResponse != null) {
            return agentJobListResponse.getReviews();
        } else {
            return 0;
        }
    }

    public String getTitle() {
        if (agentJobListResponse != null && !TextUtils.isEmpty(agentJobListResponse.getJobTitle())) {
            return agentJobListResponse.getJobTitle();
        }
        return "";
    }

    public String getClientName() {
        if (agentJobListResponse != null && !TextUtils.isEmpty(agentJobListResponse.getUserName())) {
            return agentJobListResponse.getUserName();
        }
        return "";
    }

    public String getDate() {
        if (agentJobListResponse != null && !TextUtils.isEmpty(agentJobListResponse.getJobEndDate())) {
            return agentJobListResponse.getJobEndDate();
        }
        return "";
    }
}
