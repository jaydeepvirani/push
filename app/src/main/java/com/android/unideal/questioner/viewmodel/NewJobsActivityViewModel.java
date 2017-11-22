package com.android.unideal.questioner.viewmodel;

import android.content.Context;
import android.text.TextUtils;
import com.android.unideal.R;
import com.android.unideal.rest.CallbackWrapper;
import com.android.unideal.rest.EmptyCallbackWrapper;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import com.android.unideal.rest.response.NewJobResponse;
import com.android.unideal.util.session.UploadManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import retrofit2.Call;

/**
 * Created by ADMIN on 14-10-2016.
 */
public class NewJobsActivityViewModel {
  private Context mContext;
  private ArrayList<String> attachedFilesPath = new ArrayList<>();
  private NewJobsListener mListener;
  private boolean shouldRetry;

  public NewJobsActivityViewModel(Context context, NewJobsListener mListener) {
    this.mContext = context;
    this.mListener = mListener;
  }

  private Context getContext() {
    return mContext;
  }

  public void onCreate() {
    mListener.onInitialization();
    mListener.startBindingViews();
  }

  public void createNewJob(final HashMap<String, Object> requestMapDataSet) {
    mListener.showProgressDialog(true);
    Call<GenericResponse<NewJobResponse>> responseCreateJobCall =
        RestClient.get().createNewJob(requestMapDataSet);
    responseCreateJobCall.enqueue(new CallbackWrapper<NewJobResponse>() {
      @Override
      public void onSuccess(GenericResponse<NewJobResponse> response) {
        mListener.jobCreatedSuccessful(response);
        mListener.showProgressDialog(false);
      }

      @Override
      public void onFailure(GenericResponse<NewJobResponse> response) {
        mListener.showProgressDialog(false);
        if (!shouldRetry) {
          mListener.retryRequestCall(response.getMessage());
          shouldRetry = true;
        } else {
          mListener.failToCreateJob(response.getMessage());
        }
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        mListener.showProgressDialog(false);
        if (!shouldRetry) {
          mListener.retryRequestCall(errorResponse.getMessage());
          shouldRetry = true;
        } else {
          mListener.failToCreateJob(errorResponse.getMessage());
        }
      }
    });
  }

  public void addFileForUploading(String filePath) {
    attachedFilesPath.add(filePath);
  }

  public void removeAllUploadingFile() {
    attachedFilesPath.clear();
  }

  public void removeConfigFile(int position) {
    attachedFilesPath.remove(position);
  }

  public void startUploadingAttachedFiles(NewJobResponse newJobResponse) {
    if (newJobResponse != null) {
      if (attachedFilesPath.size() > 0) {
        String jobId = String.valueOf(newJobResponse.getJobId());
        //send the upload file set along with Job Id to the upload manager
        UploadManager uploadManager = UploadManager.get(getContext());
        //convert the list<String> into HashSet<String>
        uploadManager.addUploadFile(jobId, new HashSet<>(attachedFilesPath));
      }
    }
    mListener.startedUploadingJob();
  }

  public void verifyPromotionalCode(int userId, String coupon_code) {
    //mListener.showProgressDialog(true);
    Call<GenericResponse> promotionalCall =
        RestClient.get().promocodeProgram(userId, RestFields.REQUEST_PROMOTIONAL_CODE, coupon_code);
    promotionalCall.enqueue(new EmptyCallbackWrapper() {
      @Override
      public void onSuccess(GenericResponse response) {
        mListener.showProgressDialog(false);
        if (!TextUtils.isEmpty(response.getMessage())) {
          mListener.onPromotionalCode(response.getMessage());
        }
      }

      @Override
      public void onFailure(GenericResponse response) {
        // mListener.showProgressDialog(false);
        if (!TextUtils.isEmpty(response.getMessage())) {
          mListener.failPromotionalCode(response.getMessage());
        }
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        // mListener.showProgressDialog(false);
        if (!TextUtils.isEmpty(errorResponse.getMessage())) {
          mListener.failPromotionalCode(getContext().getString(R.string.alert_internal_exception));
        }
      }
    });
  }

  public interface NewJobsListener {
    void onInitialization();

    void startBindingViews();

    void showProgressDialog(boolean show);

    void jobCreatedSuccessful(GenericResponse<NewJobResponse> newJobResponse);

    void startedUploadingJob();

    void failToCreateJob(String errorMessage);

    void retryRequestCall(String errorMessage);

    void onPromotionalCode(String message);

    void onRemovePromotionalCode();

    void failPromotionalCode(String errorMessage);
  }
}
