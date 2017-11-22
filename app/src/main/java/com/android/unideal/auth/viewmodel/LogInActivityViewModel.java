package com.android.unideal.auth.viewmodel;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.android.unideal.data.UserDetail;
import com.android.unideal.enums.AuthMode;
import com.android.unideal.rest.CallbackWrapper;
import com.android.unideal.rest.EmptyCallbackWrapper;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.SessionManager;
import com.google.firebase.iid.FirebaseInstanceId;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;

/**
 * Created by ADMIN on 20-09-2016.
 */

public class LogInActivityViewModel {
  private Context context;
  private LogInListener logInListener;

  public LogInActivityViewModel(Context context, LogInListener logInListener) {
    this.context = context;
    this.logInListener = logInListener;
  }

  public void loginWithFb(String id, String name, String email, String image) {
    DialogUtils.getInstance().showProgressDialog(context);
    HashMap<String, String> hashMap = getParams(name, email, image);
    hashMap.put(RestFields.KEY_FACEBOOK_ID, id);
    Call<GenericResponse<UserDetail>> responseCall = RestClient.get().loginWithFaceBook(hashMap);
    responseCall.enqueue(new CallbackWrapper<UserDetail>() {
      @Override
      public void onSuccess(GenericResponse<UserDetail> response) {
        DialogUtils.getInstance().hideProgressDialog();
        SessionManager.get(context).saveUserDetail(response.getData());
        //save users settings
        SessionManager.get(context).saveUsersSettings(response.getData());
        logInListener.onLoginSuccess(response.getData());
      }

      @Override
      public void onFailure(GenericResponse<UserDetail> response) {
        DialogUtils.getInstance().hideProgressDialog();
        logInListener.onLoginFailed(response.getMessage());
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        DialogUtils.getInstance().hideProgressDialog();
        logInListener.onLoginFailed(errorResponse.getMessage());
      }
    });
  }

  //check for google id
  public void checkForGoogleId(final String id, final String name, final String email,
      final String image) {
    DialogUtils.getInstance().showProgressDialog(context);
    Call<GenericResponse<UserDetail>> call = RestClient.get().checkForGoogleId(id);
    call.enqueue(new CallbackWrapper<UserDetail>() {
      @Override
      public void onSuccess(GenericResponse<UserDetail> response) {
        DialogUtils.getInstance().hideProgressDialog();
        SessionManager.get(context).saveUserDetail(response.getData());
        //save users settings
        SessionManager.get(context).saveUsersSettings(response.getData());
        logInListener.onLoginSuccess(response.getData());
      }

      @Override
      public void onFailure(GenericResponse<UserDetail> response) {
        DialogUtils.getInstance().hideProgressDialog();
        logInListener.showTermsConditionDialog(AuthMode.GOOGLE, id, name, email, image);
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        DialogUtils.getInstance().hideProgressDialog();
        logInListener.onLoginFailed(errorResponse.getMessage());
      }
    });
  }

  //check for fb id
  public void checkForFbId(final String id, final String name, final String email,
      final String image) {
    DialogUtils.getInstance().showProgressDialog(context);
    Call<GenericResponse<UserDetail>> call = RestClient.get().checkForFbId(id);
    call.enqueue(new CallbackWrapper<UserDetail>() {
      @Override
      public void onSuccess(GenericResponse<UserDetail> response) {
        DialogUtils.getInstance().hideProgressDialog();
        SessionManager.get(context).saveUserDetail(response.getData());
        //save users settings
        SessionManager.get(context).saveUsersSettings(response.getData());
        logInListener.onLoginSuccess(response.getData());
      }

      @Override
      public void onFailure(GenericResponse<UserDetail> response) {
        DialogUtils.getInstance().hideProgressDialog();
        logInListener.showTermsConditionDialog(AuthMode.FACEBOOK, id, name, email, image);
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        DialogUtils.getInstance().hideProgressDialog();
        logInListener.onLoginFailed(errorResponse.getMessage());
      }
    });
  }

  public void loginWithGoogle(String id, String name, String email, String image) {
    DialogUtils.getInstance().showProgressDialog(context);
    HashMap<String, String> hashMap = getParams(name, email, image);
    hashMap.put(RestFields.KEY_GOOGLE_PLUS_ID, id);
    Call<GenericResponse<UserDetail>> responseCall = RestClient.get().loginWithGoogle(hashMap);
    responseCall.enqueue(new CallbackWrapper<UserDetail>() {
      @Override
      public void onSuccess(GenericResponse<UserDetail> response) {
        DialogUtils.getInstance().hideProgressDialog();
        SessionManager.get(context).saveUserDetail(response.getData());
        //save users settings
        SessionManager.get(context).saveUsersSettings(response.getData());
        logInListener.onLoginSuccess(response.getData());
      }

      @Override
      public void onFailure(GenericResponse<UserDetail> response) {
        DialogUtils.getInstance().hideProgressDialog();
        logInListener.onLoginFailed(response.getMessage());
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        DialogUtils.getInstance().hideProgressDialog();
        logInListener.onLoginFailed(errorResponse.getMessage());
      }
    });
  }

  private HashMap<String, String> getParams(String name, String email, String image) {
    HashMap<String, String> params = new HashMap<>();
    params.put(RestFields.KEY_NAME, name);
    params.put(RestFields.KEY_DEVICE_TOKEN, "");
    if (!TextUtils.isEmpty(email)) {
      params.put(RestFields.KEY_EMAIL_ADDRESS, email);
    } else {
      params.put(RestFields.KEY_EMAIL_ADDRESS, "");
    }
    if (!TextUtils.isEmpty(image)) {
      params.put(RestFields.KEY_PROFILE_PIC, image);
    } else {
      params.put(RestFields.KEY_PROFILE_PIC, "");
    }
    params.put(RestFields.KEY_GENDER, "");
    return params;
  }

  public void customLogin(final String email, final String password) {
    DialogUtils.getInstance().showProgressDialog(context);
    Call<GenericResponse<UserDetail>> responseCall =
        RestClient.get().loginWithCustom(getParamsWithCustomLogin(email, password));
    responseCall.enqueue(new CallbackWrapper<UserDetail>() {
      @Override
      public void onSuccess(GenericResponse<UserDetail> response) {
        DialogUtils.getInstance().hideProgressDialog();
        SessionManager.get(context).saveUserDetail(response.getData());
        //save users settings
        SessionManager.get(context).saveUsersSettings(response.getData());
        logInListener.onLoginSuccess(response.getData());
      }

      @Override
      public void onFailure(GenericResponse<UserDetail> response) {
        DialogUtils.getInstance().hideProgressDialog();
        if (response.getStatus_code() == (RestFields.RESEND_STATUS_CODE)) {
          logInListener.onResendActivation(response.getMessage(), email);
        } else {
          logInListener.onLoginFailed(response.getMessage());
        }
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        DialogUtils.getInstance().hideProgressDialog();
        logInListener.onLoginFailed(errorResponse.getMessage());
      }
    });
  }

  private HashMap<String, Object> getParamsWithCustomLogin(String email, String password) {
    HashMap<String, Object> params = new HashMap<>();
    params.put(RestFields.KEY_EMAIL_ADDRESS, email);
    params.put(RestFields.KEY_PASSWORD, password);
    params.put(RestFields.KEY_DEVICE_TOKEN, "");
    params.put(RestFields.KEY_USER_TYPE, SessionManager.get(context).getUserMode());
    return params;
  }

  public void resendActivation(String email) {
    DialogUtils.getInstance().showProgressDialog(context);
    Call<GenericResponse> responseCall =
        RestClient.get().resendActivationCode(getParamsResend(email));
    responseCall.enqueue(new EmptyCallbackWrapper() {
      @Override
      public void onSuccess(GenericResponse response) {
        DialogUtils.getInstance().hideProgressDialog();
        logInListener.showAlertDialog(response.getMessage());
      }

      @Override
      public void onFailure(GenericResponse response) {
        DialogUtils.getInstance().hideProgressDialog();
        logInListener.showAlertDialog(response.getMessage());
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        DialogUtils.getInstance().hideProgressDialog();
        logInListener.showAlertDialog(errorResponse.getMessage());
      }
    });
  }

  private Map<String, Object> getParamsResend(String email) {
    HashMap<String, Object> params = new HashMap<>();
    params.put(RestFields.KEY_EMAIL_ADDRESS, email);
    return params;
  }

  public void updateDeviceToken() {
    final String newToken = FirebaseInstanceId.getInstance().getToken();
    final String oldToken = SessionManager.get(context).getDeviceToken();
    final int userId = SessionManager.get(context).getUserId();
    if (userId != -1) {
      if (!TextUtils.isEmpty(newToken)) {
        if (!newToken.equals(oldToken)) {
          Call<GenericResponse> responseCall =
              RestClient.get().updateToken(getParams(newToken, oldToken));
          responseCall.enqueue(new EmptyCallbackWrapper() {
            @Override
            public void onSuccess(GenericResponse response) {
              SessionManager.get(context).setDeviceToken(newToken);
            }

            @Override
            public void onFailure(GenericResponse response) {
              Log.d("LoginViewModel", "onFailure: " + response.getMessage());
            }

            @Override
            public void exception(ErrorResponse errorResponse) {
              Log.d("LoginViewModel", "onFailure: " + errorResponse.getMessage());
            }
          });
        }
      }
    }
  }

  public Map<String, Object> getParams(String newToken, String oldToken) {
    HashMap<String, Object> params = new HashMap<>();
    params.put(RestFields.KEY_USER_ID, SessionManager.get(context).getActiveUser().getUserId());
    params.put(RestFields.KEY_NEW_DEVICE_TOKEN, newToken);
    if (!TextUtils.isEmpty(oldToken)) {
      params.put(RestFields.KEY_OLD_DEVICE_TOKEN, oldToken);
    } else {
      params.put(RestFields.KEY_OLD_DEVICE_TOKEN, "");
    }

    return params;
  }

  public interface LogInListener {
    void onLoginSuccess(UserDetail userDetail);

    void onLoginFailed(String message);

    void onResendActivation(String message, String emailAddress);

    void showAlertDialog(String message);

    void showTermsConditionDialog(AuthMode authMode, String id, String name, String email,
        String image);
  }
}
