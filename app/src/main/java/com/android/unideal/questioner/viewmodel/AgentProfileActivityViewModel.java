package com.android.unideal.questioner.viewmodel;

import android.content.Context;
import com.android.unideal.rest.CallbackWrapper;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import com.android.unideal.rest.response.ProfileResponse;
import java.util.HashMap;
import retrofit2.Call;

/**
 * Created by ADMIN on 18-10-2016.
 */

public class AgentProfileActivityViewModel {
  private AgentProfileQuestionerListener mListener;

  public AgentProfileActivityViewModel(AgentProfileQuestionerListener mListener) {
    this.mListener = mListener;
  }

  public void onCreateActivity() {
    this.mListener.initialization();
  }

  public void getProfileDetails(String userId) {
    mListener.showProgressBar();
    HashMap<String, String> params = new HashMap<>();
    params.put(RestFields.KEY_USER_ID, userId);
    params.put(RestFields.KEY_USER_TYPE, String.valueOf(RestFields.USER_TYPE_AGENT));
    Call<GenericResponse<ProfileResponse>> responseCall = RestClient.get().getProfileDetail(params);
    responseCall.enqueue(new CallbackWrapper<ProfileResponse>() {
      @Override
      public void onSuccess(GenericResponse<ProfileResponse> response) {
        mListener.setUserData(response.getData());
        mListener.hideProgressBar();
      }

      @Override
      public void onFailure(GenericResponse<ProfileResponse> response) {
        mListener.hideProgressBar();
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        mListener.hideProgressBar();
      }
    });
  }

  public interface AgentProfileQuestionerListener {
    void initialization();

    void setUserData(ProfileResponse detailJobResponse);

    void showProgressBar();

    void hideProgressBar();
  }
}
