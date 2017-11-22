package com.android.unideal.agent.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;
import android.text.TextUtils;
import android.view.View;
import com.android.unideal.R;
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
import java.util.Map;
import retrofit2.Call;

/**
 * Created by bhavdip on 21/11/16.
 */

public class JobDetailsViewModel {

  public ObservableField<String> title = new ObservableField<>();
  public ObservableField<String> clientName = new ObservableField<>();
  public ObservableField<String> categoryName = new ObservableField<>();
  public ObservableField<String> totalApplicants = new ObservableField<>();
  public ObservableField<String> consignmentSize = new ObservableField<>();
  public ObservableField<String> jobEndsOnDate = new ObservableField<>();
  public ObservableField<String> jobDetails = new ObservableField<>();
  public ObservableField<Float> requesterReviews = new ObservableField<>(0.0f);
  public ObservableField<Float> agentReviews = new ObservableField<>(0.0f);
  public ObservableField<String> deliveryPlace = new ObservableField<>();
  public ObservableField<String> requireInvoice = new ObservableField<>();
  public ObservableField<Integer> showDeliveryPlace = new ObservableField<>(View.GONE);
  private Context mContext;
  private JobDetailsViewHandler mViewHandler;

  public JobDetailsViewModel(Context activityContext, JobDetailsViewHandler viewHandler) {
    this.mViewHandler = viewHandler;
    this.mContext = activityContext;
  }

  private Context getActivityContext() {
    return mContext;
  }

  public void onActivityCreated() {
    mViewHandler.startBindingViews();
    mViewHandler.loadJobDetails();
  }

  public void loadJobDetails(int userId, int jobId, int userType) {
    mViewHandler.showProgressBar();
    Call<GenericResponse<JobDetail>> call = RestClient.get().getJobDetail(userId, jobId, userType);
    call.enqueue(new CallbackWrapper<JobDetail>() {
      @Override
      public void onSuccess(GenericResponse<JobDetail> response) {
        mViewHandler.hideProgressBar();
        mViewHandler.onJobDetailRetrieved(response.getData());
      }

      @Override
      public void onFailure(GenericResponse<JobDetail> response) {
        mViewHandler.hideProgressBar();
        mViewHandler.showToast(response.getMessage());
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        mViewHandler.hideProgressBar();
        mViewHandler.showToast(errorResponse.getMessage());
      }
    });
  }

  public void bindingModel(JobDetail jobDetail) {
    title.set(jobDetail.getJob_title());

    if (jobDetail.getStatusEnum() == Status.OPEN || jobDetail.getStatusEnum() == Status.APPLIED) {
      clientName.set(jobDetail.getUser_name());
    } else {
      clientName.set(jobDetail.getMsg_user_name());
    }

    if (!TextUtils.isEmpty(jobDetail.getSub_category_name())) {
      categoryName.set(jobDetail.getCategory_name().trim()
          + "("
          + jobDetail.getSub_category_name().trim()
          + ")");
    } else {
      categoryName.set(jobDetail.getCategory_name().trim());
    }
    if (jobDetail.getStatusEnum() == Status.OPEN) {
      showDeliveryPlace.set(View.GONE);
    } else {
      if (!TextUtils.isEmpty(jobDetail.getDeliveryplace())) {
        showDeliveryPlace.set(View.VISIBLE);
        deliveryPlace.set(jobDetail.getDeliveryplace());
      } else {
        showDeliveryPlace.set(View.VISIBLE);
        deliveryPlace.set(getActivityContext().getString(R.string.title_unideal_delivery_place));
      }
    }

    if (jobDetail.getApplicant() > 1) {
      totalApplicants.set(
          getActivityContext().getString(R.string.total_applicants, jobDetail.getApplicant()));
    } else {
      totalApplicants.set(
          getActivityContext().getString(R.string.total_applicant, jobDetail.getApplicant()));
    }
    String consignment = String.valueOf(jobDetail.getConsignment_size());
    consignmentSize.set(
        getActivityContext().getString(R.string.consignment_size_price, consignment));
    jobEndsOnDate.set(DateTimeUtils.jobEndDateTime(mContext, jobDetail.getJob_end_on()));
    jobDetails.set(jobDetail.getJob_details());
    requesterReviews.set(jobDetail.getQuestionar_review());
    agentReviews.set(jobDetail.getAgent_review());
    requireInvoice.set(
        jobDetail.getIs_invoice() != 0 ? getActivityContext().getString(R.string.btn_yes)
            : getActivityContext().getString(R.string.btn_no));
  }

  public void requestUpdateJobStatus(JobDetail jobDetail, final int requestStatus,
      String reviewsPoints) {
    changeJobStatus(jobDetail, requestStatus, reviewsPoints);
  }

  public void requestUpdateJobStatus(JobDetail jobDetail, final int requestStatus) {
    changeJobStatus(jobDetail, requestStatus, null);
  }

  private void changeJobStatus(JobDetail jobDetail, final int requestStatus, String reviewsPoints) {
    mViewHandler.showProgressBar();
    Call<GenericResponse> requestUpdateStatus = RestClient.get()
        .updateJobStatus(getStatusParameter(jobDetail.getJob_id(), requestStatus, reviewsPoints));
    requestUpdateStatus.enqueue(new EmptyCallbackWrapper() {
      @Override
      public void onSuccess(GenericResponse response) {
        mViewHandler.hideProgressBar();
        mViewHandler.onStatusUpdate(response.getMessage(), requestStatus);
      }

      @Override
      public void onFailure(GenericResponse response) {
        mViewHandler.hideProgressBar();
        mViewHandler.onStatusUpdateFail(response.getMessage(), requestStatus);
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        mViewHandler.hideProgressBar();
        mViewHandler.onStatusUpdateException(errorResponse.getMessage(), requestStatus);
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
    updateJobParameter.put(RestFields.KEY_USER_TYPE, RestFields.USER_TYPE_AGENT);
    //Job id
    updateJobParameter.put(RestFields.KEY_JOB_ID, jobId);
    //new request status
    updateJobParameter.put(RestFields.KEY_UPDATE_JOB_STATUS, requestStatus);
    if (!TextUtils.isEmpty(reviewsPoint)) {
      updateJobParameter.put(RestFields.KEY_REVIEWS, reviewsPoint);
    }
    return updateJobParameter;
  }

  public interface JobDetailsViewHandler {
    void onJobDetailRetrieved(JobDetail jobDetail);

    void startBindingViews();

    void loadJobDetails();

    void showProgressBar();

    void hideProgressBar();

    void showToast(String message);

    void onStatusUpdate(String message, int requestStatus);

    void onStatusUpdateFail(String message, int requestStatus);

    void onStatusUpdateException(String message, int requestStatus);
  }
}
