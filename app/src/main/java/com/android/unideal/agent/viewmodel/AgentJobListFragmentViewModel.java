package com.android.unideal.agent.viewmodel;

import com.android.unideal.data.JobDetail;
import com.android.unideal.rest.CallbackWrapper;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;

/**
 * Created by ADMIN on 23-09-2016.
 */

public class AgentJobListFragmentViewModel {
  private AgentJobListener mListener;

  public AgentJobListFragmentViewModel(AgentJobListener mListener) {
    this.mListener = mListener;
  }

  public void onActivityCreated() {
    if (mListener != null) {
      mListener.startBindingViews();
      mListener.setupRecyclerView();
    }
  }

  public void getJobList(String userId) {
    HashMap<String, String> hashMap = new HashMap<>();
    hashMap.put(RestFields.KEY_USER_ID, String.valueOf(userId));
    hashMap.put(RestFields.KEY_JOB_STATUS, String.valueOf(RestFields.STATUS.OPEN));
    hashMap.put(RestFields.KEY_USER_TYPE, String.valueOf(RestFields.USER_TYPE_AGENT));
    retrieveJobList(hashMap);
  }

  public void getJobList(String userId, HashMap<String, String> filterMap) {
    HashMap<String, String> hashMap = new HashMap<>();
    hashMap.put(RestFields.KEY_USER_ID, String.valueOf(userId));
    hashMap.put(RestFields.KEY_JOB_STATUS, String.valueOf(RestFields.STATUS.OPEN));
    hashMap.put(RestFields.KEY_USER_TYPE, String.valueOf(RestFields.USER_TYPE_AGENT));
    hashMap.putAll(filterMap);
    retrieveJobList(hashMap);
  }

  public void getJobList(String userId, HashMap<String, String> filterMap, String search) {
    HashMap<String, String> hashMap = new HashMap<>();
    hashMap.put(RestFields.KEY_USER_ID, userId);
    hashMap.put(RestFields.KEY_JOB_STATUS, String.valueOf(RestFields.STATUS.OPEN));
    hashMap.put(RestFields.KEY_USER_TYPE, String.valueOf(RestFields.USER_TYPE_AGENT));
    hashMap.put(RestFields.KEY_JOB_SEARCH, search);
    hashMap.putAll(filterMap);
    retrieveJobList(hashMap);
  }

  private void retrieveJobList(HashMap<String, String> hashMap) {
    mListener.showProgressBar();
    Call<GenericResponse<List<JobDetail>>> call = RestClient.get().getJobList(hashMap);
    call.enqueue(new CallbackWrapper<List<JobDetail>>() {
      @Override
      public void onSuccess(GenericResponse<List<JobDetail>> response) {
        mListener.hideProgressBar();
        mListener.responseAgentJobList(response.getData(), response.getMessage());
      }

      @Override
      public void onFailure(GenericResponse<List<JobDetail>> response) {
        mListener.hideProgressBar();
        mListener.responseAgentJobList(Collections.<JobDetail>emptyList(), response.getMessage());
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        mListener.hideProgressBar();
        mListener.responseAgentJobList(Collections.<JobDetail>emptyList(), null);
      }
    });
  }

  public interface AgentJobListener {
    void showProgressBar();

    void hideProgressBar();

    void startBindingViews();

    void setupRecyclerView();

    void responseAgentJobList(List<JobDetail> jobList, String message);
  }
}
