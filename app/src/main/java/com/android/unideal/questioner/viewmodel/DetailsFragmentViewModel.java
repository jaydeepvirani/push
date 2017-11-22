package com.android.unideal.questioner.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.View;
import com.android.unideal.R;
import com.android.unideal.data.Applicant;
import com.android.unideal.data.JobDetail;
import com.android.unideal.data.Status;
import com.android.unideal.data.UserDetail;
import com.android.unideal.rest.CallbackWrapper;
import com.android.unideal.rest.EmptyCallbackWrapper;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import com.android.unideal.util.DateTimeUtils;
import com.android.unideal.util.SessionManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;

/**
 * Created by ADMIN on 21-11-2016.
 */

public class DetailsFragmentViewModel {

  public ObservableField<Integer> applicantProgressbar = new ObservableField<>(View.GONE);
  public ObservableField<Integer> detailProgressBar = new ObservableField<>(View.GONE);
  public ObservableField<Integer> contentVisibility = new ObservableField<>(View.GONE);

  public ObservableField<String> jobIdField = new ObservableField<>();
  public ObservableField<String> jobTitle = new ObservableField<>();
  public ObservableField<String> jobDescriptions = new ObservableField<>();
  public ObservableField<String> jobTypeField = new ObservableField<>();//category
  public ObservableField<String> jobConsignmentSize = new ObservableField<>();
  public ObservableField<String> jobCreatedBy = new ObservableField<>();
  public ObservableField<String> totalApplicants = new ObservableField<>();
  public ObservableField<String> jobEndDate = new ObservableField<>();
  public ObservableField<Integer> ratingSection = new ObservableField<>(View.GONE);
  public ObservableField<Float> requesterRating = new ObservableField<>(0.0f);
  public ObservableField<Float> agentRating = new ObservableField<>(0.0f);
  public ObservableField<Integer> showDeliveryPlace = new ObservableField<>(View.GONE);
  public ObservableField<String> deliveryPlace = new ObservableField<>();

  private DetailsModelListener mListener;
  private Context mContext;
  private JobDetail mJobDetail;

  public DetailsFragmentViewModel(DetailsModelListener detailsModelListener, Context context) {
    this.mContext = context;
    this.mListener = detailsModelListener;
  }

  private void bindJobsDetails() {
    if (mJobDetail != null) {
      jobIdField.set(String.valueOf(mJobDetail.getJob_id()));
      jobTitle.set(mJobDetail.getJob_title());
      if (!TextUtils.isEmpty(mJobDetail.getSub_category_name())) {
        String cat = mJobDetail.getCategory_name().trim()
            + "("
            + mJobDetail.getSub_category_name().trim()
            + ")";
        jobTypeField.set(cat);
      } else {
        jobTypeField.set(mJobDetail.getCategory_name().trim());
      }
      if (mJobDetail.getStatusEnum() == Status.OPEN) {
        showDeliveryPlace.set(View.GONE);
      } else {
        if (!TextUtils.isEmpty(mJobDetail.getDeliveryplace())) {
          showDeliveryPlace.set(View.VISIBLE);
          deliveryPlace.set(mJobDetail.getDeliveryplace());
        } else {
          showDeliveryPlace.set(View.VISIBLE);
          deliveryPlace.set(getActivityContext().getString(R.string.title_unideal_delivery_place));
        }
      }
      if (mJobDetail.getStatusEnum() == Status.OPEN
          || mJobDetail.getStatusEnum() == Status.APPLIED) {
        jobCreatedBy.set(mJobDetail.getUser_name());
      } else {
        jobCreatedBy.set(mJobDetail.getMsg_user_name());
      }
      jobDescriptions.set(mJobDetail.getJob_details());
      String consignment = String.valueOf(mJobDetail.getConsignment_size());
      jobConsignmentSize.set(getString(R.string.consignment_size_price, consignment));
      if (mJobDetail.getApplicant() > 1) {
        totalApplicants.set(getString(R.string.total_applicants, mJobDetail.getApplicant()));
      } else {
        totalApplicants.set(getString(R.string.total_applicant, mJobDetail.getApplicant()));
      }
      jobEndDate.set(DateTimeUtils.jobEndDateTime(mContext, mJobDetail.getJob_end_on()));
    }
    bindJobRating();
  }

  private void bindJobRating() {
    if (mJobDetail.getStatusEnum() == Status.COMPLETED) {
      ratingSection.set(View.VISIBLE);
      requesterRating.set(mJobDetail.getQuestionar_review());
      agentRating.set(mJobDetail.getAgent_review());
    } else {
      ratingSection.set(View.GONE);
    }
  }

  private Context getActivityContext() {
    return mContext;
  }

  private String getString(@StringRes int resId, Object... formatArgs) {
    return getActivityContext().getString(resId, formatArgs);
  }

  public void onActivityCreated() {
    mListener.startViewBinding();
  }

  public void loadJobDetails(int userId, int jobId, int userType) {
    detailProgressBar.set(View.VISIBLE);
    contentVisibility.set(View.GONE);
    Call<GenericResponse<JobDetail>> call = RestClient.get().getJobDetail(userId, jobId, userType);
    call.enqueue(new CallbackWrapper<JobDetail>() {
      @Override
      public void onSuccess(GenericResponse<JobDetail> response) {
        detailProgressBar.set(View.GONE);
        contentVisibility.set(View.VISIBLE);
        mJobDetail = response.getData();
        bindJobsDetails();
        mListener.onJobDetailRetrieved(mJobDetail);
      }

      @Override
      public void onFailure(GenericResponse<JobDetail> response) {
        detailProgressBar.set(View.GONE);
        contentVisibility.set(View.VISIBLE);
        mListener.showToast(response.getMessage());
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        detailProgressBar.set(View.GONE);
        contentVisibility.set(View.VISIBLE);
        mListener.showToast(errorResponse.getMessage());
      }
    });
  }

  public void getInProgressApplicantList(String jobId) {
    applicantProgressbar.set(View.VISIBLE);
    Call<GenericResponse<List<Applicant>>> responseCall =
        RestClient.get().getApplicantList(getParams(jobId));
    responseCall.enqueue(new CallbackWrapper<List<Applicant>>() {
      @Override
      public void onSuccess(GenericResponse<List<Applicant>> response) {
        applicantProgressbar.set(View.GONE);
        mListener.onJobApplicantList(response.getData());
      }

      @Override
      public void onFailure(GenericResponse<List<Applicant>> response) {
        applicantProgressbar.set(View.GONE);
        mListener.showEmptyView(response.getMessage());
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        applicantProgressbar.set(View.GONE);
        mListener.showEmptyView(errorResponse.getMessage());
      }
    });
  }

  private Map<String, Object> getParams(String jobId) {
    HashMap<String, Object> params = new HashMap<>();
    params.put(RestFields.KEY_USER_ID, SessionManager.get(mContext).getActiveUser().getUserId());
    params.put(RestFields.KEY_USER_TYPE, RestFields.USER_TYPE_REQUESTER);
    params.put(RestFields.KEY_JOB_STATUS, RestFields.STATUS.IN_PROCESS);
    params.put(RestFields.KEY_JOB_ID, jobId);
    return params;
  }

  public void requestUpdateJobStatus(JobDetail jobDetail, final int requestStatus,
      String reviewsPoints) {
    changeJobStatus(jobDetail, requestStatus, reviewsPoints);
  }

  public void requestUpdateJobStatus(JobDetail jobDetail, final int requestStatus) {
    changeJobStatus(jobDetail, requestStatus, null);
  }

  private void changeJobStatus(JobDetail jobDetail, final int requestStatus, String reviewsPoints) {
    mListener.showProgressBar();
    Call<GenericResponse> requestUpdateStatus = RestClient.get()
        .updateJobStatus(getStatusParameter(jobDetail.getJob_id(), requestStatus, reviewsPoints));
    requestUpdateStatus.enqueue(new EmptyCallbackWrapper() {
      @Override
      public void onSuccess(GenericResponse response) {
        mListener.hideProgressBar();
        mListener.onStatusUpdate(response.getMessage(), requestStatus);
      }

      @Override
      public void onFailure(GenericResponse response) {
        mListener.hideProgressBar();
        mListener.onStatusUpdateFail(response.getMessage(), requestStatus);
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        mListener.hideProgressBar();
        mListener.onStatusUpdateException(errorResponse.getMessage(), requestStatus);
      }
    });
  }

  private Map<String, Object> getStatusParameter(int jobId, int requestStatus,
      String reviewsPoint) {
    HashMap<String, Object> updateJobParameter = new HashMap<>();
    UserDetail userDetail = SessionManager.get(getActivityContext()).getActiveUser();
    if (userDetail != null) {
      //user id
      updateJobParameter.put(RestFields.KEY_USER_ID, String.valueOf(userDetail.getUserId()));
    }
    //user type
    updateJobParameter.put(RestFields.KEY_USER_TYPE, RestFields.USER_TYPE_REQUESTER);
    //Job id
    updateJobParameter.put(RestFields.KEY_JOB_ID, jobId);
    //new request status
    updateJobParameter.put(RestFields.KEY_UPDATE_JOB_STATUS, requestStatus);

    if (!TextUtils.isEmpty(reviewsPoint)) {
      updateJobParameter.put(RestFields.KEY_REVIEWS, reviewsPoint);
    }

    return updateJobParameter;
  }

  public interface DetailsModelListener {
    void startViewBinding();

    void showProgressBar();

    void hideProgressBar();

    void onJobApplicantList(List<Applicant> data);

    void showEmptyView(String message);

    void onJobDetailRetrieved(JobDetail jobDetail);

    void onStatusUpdate(String message, int requestStatus);

    void onStatusUpdateFail(String message, int requestStatus);

    void onStatusUpdateException(String message, int requestStatus);

    void showAlert(String message);

    void showToast(String message);
  }
}
