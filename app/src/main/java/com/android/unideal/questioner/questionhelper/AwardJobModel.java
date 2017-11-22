package com.android.unideal.questioner.questionhelper;

import com.android.unideal.data.Applicant;
import com.android.unideal.questioner.viewmodel.BaseViewModel;
import com.android.unideal.rest.EmptyCallbackWrapper;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import java.util.HashMap;
import retrofit2.Call;

/**
 * Created by CS02 on 12/28/2016.
 */

public class AwardJobModel {
  private AwardListener mListener;

  public AwardJobModel(AwardListener listener) {
    this.mListener = listener;
  }

  public void acceptApplicant(int userId, String jobId, int applicantId, Applicant applicant,
      int isConsignmentEdit, float consignmentOffer) {
    performAction(userId, jobId, applicantId, applicant, RestFields.ACTION_ACCEPT,
        isConsignmentEdit, consignmentOffer);
  }

  public void rejectApplicant(int userId, String jobId, int applicantId, Applicant applicant) {
    performAction(userId, jobId, applicantId, applicant, RestFields.ACTION_REJECT, 0, 0);
  }

  private void performAction(int userId, String jobId, int applicantId, final Applicant applicant,
      final int action, final int isConsignmentEdit, final float consignmentOffer) {
    HashMap<String, Object> params = new HashMap<>();
    params.put(RestFields.KEY_USER_ID, userId);
    params.put(RestFields.KEY_JOB_STATUS, RestFields.STATUS.OPEN);
    params.put(RestFields.KEY_JOB_ID, jobId);
    params.put(RestFields.KEY_APPLICANT_ID, applicantId);
    params.put(RestFields.KEY_APPLICANT_ACTION, action);
    params.put(RestFields.KEY_IS_CONSIGNMENT_EDIT, isConsignmentEdit);
    params.put(RestFields.KEY_CONSIGNMENT_SIZE, consignmentOffer);
    mListener.showProgressDialog();
    Call<GenericResponse> responseCall = RestClient.get().applicantAction(params);
    responseCall.enqueue(new EmptyCallbackWrapper() {
      @Override
      public void onSuccess(GenericResponse response) {
        mListener.hideProgressDialog();
        if (action == RestFields.ACTION_ACCEPT) {
          mListener.onAcceptSuccess(applicant);
          mListener.showToast(response.getMessage());
        } else if (action == RestFields.ACTION_REJECT) {
          mListener.onRejectSuccess(applicant);
          mListener.showToast(response.getMessage());
        }
      }

      @Override
      public void onFailure(GenericResponse response) {
        mListener.hideProgressDialog();
        mListener.showToast(response.getMessage());
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        mListener.hideProgressDialog();
        mListener.showToast(errorResponse.getMessage());
      }
    });
  }

  public interface AwardListener extends BaseViewModel {
    void onAcceptSuccess(Applicant applicant);

    void onRejectSuccess(Applicant applicant);
  }
}
