package com.android.unideal.questioner.viewmodel;

import android.databinding.ObservableField;
import android.view.View;
import com.android.unideal.data.Applicant;
import com.android.unideal.rest.CallbackWrapper;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;

/**
 * Created by ADMIN on 21-11-2016.
 */

public class QueApplicantFragmentViewModel {
  public ObservableField<Integer> progressbarVisibility = new ObservableField<>();
  private QueApplicantListener mListener;

  public QueApplicantFragmentViewModel(QueApplicantListener queApplicantListener) {
    this.mListener = queApplicantListener;
  }

  public void getApplicantList(String userId, String jobId) {
    progressbarVisibility.set(View.VISIBLE);
    Call<GenericResponse<List<Applicant>>> responseCall =
        RestClient.get().getApplicantList(getParams(userId, jobId));
    responseCall.enqueue(new CallbackWrapper<List<Applicant>>() {
      @Override
      public void onSuccess(GenericResponse<List<Applicant>> response) {
        progressbarVisibility.set(View.GONE);
        mListener.loadApplicantList(response.getData(), response.getMessage());
      }

      @Override
      public void onFailure(GenericResponse<List<Applicant>> response) {
        progressbarVisibility.set(View.GONE);
        mListener.loadApplicantList(Collections.<Applicant>emptyList(), response.getMessage());
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        progressbarVisibility.set(View.GONE);
        mListener.loadApplicantList(Collections.<Applicant>emptyList(), null);
        mListener.showToast(errorResponse.getMessage());
      }
    });
  }

  private Map<String, Object> getParams(String userId, String jobId) {
    HashMap<String, Object> params = new HashMap<>();
    params.put(RestFields.KEY_USER_ID, userId);
    params.put(RestFields.KEY_USER_TYPE, RestFields.USER_TYPE_REQUESTER);
    params.put(RestFields.KEY_JOB_STATUS, RestFields.STATUS.OPEN);
    params.put(RestFields.KEY_JOB_ID, jobId);
    return params;
  }

  public interface QueApplicantListener extends BaseViewModel {

    void loadApplicantList(List<Applicant> data, String message);
  }
}
