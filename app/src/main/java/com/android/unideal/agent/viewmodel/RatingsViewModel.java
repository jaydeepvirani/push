package com.android.unideal.agent.viewmodel;

import android.content.Context;

import com.android.unideal.rest.CallbackWrapper;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import com.android.unideal.rest.response.RattingResponse;
import com.android.unideal.util.SessionManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by ADMIN on 05-10-2016.
 */

public class RatingsViewModel {
  private RatingsListener mListener;
  private Context context;

  public RatingsViewModel(Context context, RatingsListener mListener) {
    this.context = context;
    this.mListener = mListener;
  }

  public void loadData() {
    mListener.showProgressBar();
    Call<GenericResponse<RattingResponse>> responseCall = RestClient.get().getRatting(getParams());
    responseCall.enqueue(new CallbackWrapper<RattingResponse>() {
      @Override
      public void onSuccess(GenericResponse<RattingResponse> response) {
        mListener.hideProgressBar();
        if(response.getData() != null){
          if(response.getData().getJobRattingsList().size() > 0){
            mListener.loadData(response.getData());
          }else{
            mListener.showEmptyView();
          }
        }
      }

      @Override
      public void onFailure(GenericResponse<RattingResponse> response) {
        mListener.hideProgressBar();
        mListener.onError(response.getMessage());
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        mListener.hideProgressBar();
        mListener.onError(errorResponse.getMessage());
      }
    });
  }

  public Map<String, Object> getParams() {
    HashMap<String, Object> paramas = new HashMap<>();
    paramas.put(RestFields.KEY_USER_ID, SessionManager.get(context).getUserId());
    paramas.put(RestFields.KEY_USER_TYPE, RestFields.USER_TYPE_AGENT);
    return paramas;
  }

  public interface RatingsListener {
    void showProgressBar();

    void loadData(RattingResponse agentJobListResponses);

    void hideProgressBar();

    void onError(String message);

    void showEmptyView();
  }
}
