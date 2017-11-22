package com.android.unideal.questioner.viewmodel;

import android.content.Context;
import com.android.unideal.rest.CallbackWrapper;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import com.android.unideal.rest.response.RattingResponse;
import com.android.unideal.util.SessionManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;

/**
 * Created by MRUGESH on 10/15/2016.
 */

public class QuestionerRatingFragmentViewModel {
  private Context context;
  private QuestionerRatingListener mListener;

  public QuestionerRatingFragmentViewModel(Context context, QuestionerRatingListener mListener) {
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
        mListener.loadData(response.getData());
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
    paramas.put(RestFields.KEY_USER_TYPE, RestFields.USER_TYPE_REQUESTER);
    return paramas;
  }

  public interface QuestionerRatingListener {

    void showProgressBar();

    void loadData(RattingResponse agentJobListResponses);

    void hideProgressBar();

    void onError(String message);

    void setEmptyView();
  }
}
