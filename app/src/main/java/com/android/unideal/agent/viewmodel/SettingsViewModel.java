package com.android.unideal.agent.viewmodel;

import android.content.Context;
import android.util.Log;
import com.android.unideal.data.UserDetail;
import com.android.unideal.rest.CallbackWrapper;
import com.android.unideal.rest.EmptyCallbackWrapper;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.SessionManager;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;

/**
 * Created by ADMIN on 06-10-2016.
 */

public class SettingsViewModel {
  private Context context;
  private SettingsListener mListener;

  public SettingsViewModel(Context context, SettingsListener mListener) {
    this.context = context;
    this.mListener = mListener;
  }

  public void onActivityCreated() {
    mListener.startBindingViews();
  }

  public void logoutUser(int userId, String deviceToken) {
    DialogUtils.getInstance().showProgressDialog(context);
    HashMap<String, Object> params = new HashMap<>();
    params.put(RestFields.KEY_USER_ID, userId);
    if (deviceToken == null) {
      params.put(RestFields.KEY_DEVICE_TOKEN, "");
    } else {
      params.put(RestFields.KEY_DEVICE_TOKEN, deviceToken);
    }

    Call<GenericResponse> responseCall = RestClient.get().logout(params);
    responseCall.enqueue(new EmptyCallbackWrapper() {
      @Override
      public void onSuccess(GenericResponse response) {
        DialogUtils.getInstance().hideProgressDialog();
        mListener.onLogoutSuccess();
      }

      @Override
      public void onFailure(GenericResponse response) {
        DialogUtils.getInstance().hideProgressDialog();
        mListener.showAlertDialog(response.getMessage());
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        DialogUtils.getInstance().hideProgressDialog();
        mListener.showAlertDialog(errorResponse.getMessage());
      }
    });
  }

  public void deleteUser(int userId) {
    DialogUtils.getInstance().showProgressDialog(context);
    HashMap<String, Object> params = new HashMap<>();
    params.put(RestFields.KEY_USER_ID, userId);
    Call<GenericResponse> responseCall = RestClient.get().deleteUser(params);
    responseCall.enqueue(new EmptyCallbackWrapper() {
      @Override
      public void onSuccess(GenericResponse response) {
        DialogUtils.getInstance().hideProgressDialog();
        mListener.onDeleteSuccess();
      }

      @Override
      public void onFailure(GenericResponse response) {
        DialogUtils.getInstance().hideProgressDialog();
        mListener.showAlertDialog(response.getMessage());
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        DialogUtils.getInstance().hideProgressDialog();
        mListener.showAlertDialog(errorResponse.getMessage());
      }
    });
  }

  public void updateSetting(final int newPost, final int jobStatus, final int transactionStatus,
      final int tourGuideStatus) {
    DialogUtils.getInstance().showProgressDialog(context);
    Call<GenericResponse<UserDetail>> responseCall = RestClient.get()
        .updateUserSetting(
            getSettingParams(newPost, jobStatus, transactionStatus, tourGuideStatus));
    responseCall.enqueue(new CallbackWrapper<UserDetail>() {
      @Override
      public void onSuccess(GenericResponse<UserDetail> response) {
        DialogUtils.getInstance().hideProgressDialog();
        SessionManager.get(context).setNewJobNotification(newPost);
        SessionManager.get(context).setJobStatusNotification(jobStatus);
        SessionManager.get(context).setTransactionNotification(transactionStatus);
        SessionManager.get(context).setShowTourGuide(tourGuideStatus);
      }

      @Override
      public void onFailure(GenericResponse<UserDetail> response) {
        DialogUtils.getInstance().hideProgressDialog();
        mListener.showAlertDialog(response.getMessage());
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        DialogUtils.getInstance().hideProgressDialog();
        mListener.showAlertDialog(errorResponse.getMessage());
      }
    });
  }

  public Map<String, Object> getSettingParams(int newPost, int jobStatus, int transactionStatus,
      int tourGuideStatus) {
    HashMap<String, Object> params = new HashMap<>();
    params.put(RestFields.KEY_USER_ID, SessionManager.get(context).getActiveUser().getUserId());
    params.put(RestFields.KEY_USER_TYPE, RestFields.USER_TYPE_REQUESTER);
    params.put(RestFields.KEY_NEW_JOB_NOTIFICATION, newPost);
    params.put(RestFields.KEY_JOB_STATUS_NOTIFICATION, jobStatus);
    params.put(RestFields.KEY_TRANSACTION_NOTIFICATION, transactionStatus);
    params.put(RestFields.KEY_APP_TOUR_GUIDE, tourGuideStatus);
    return params;
  }

  public interface SettingsListener {
    void onLogoutSuccess();

    void startBindingViews();

    void onDeleteSuccess();

    void showAlertDialog(String message);
  }
}
