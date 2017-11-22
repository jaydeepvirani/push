package com.android.unideal.questioner.viewmodel;

import android.content.Context;
import android.text.TextUtils;
import com.android.unideal.rest.EmptyCallbackWrapper;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.RestUtils;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import com.android.unideal.util.DialogUtils;
import java.io.File;
import java.util.HashMap;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * Created by ADMIN on 22-09-2016.
 */

public class QuestionerSignUpFragmentViewModel {
  private Context context;
  private QuestionerListener mListener;

  public QuestionerSignUpFragmentViewModel(Context context, QuestionerListener mListener) {
    this.mListener = mListener;
    this.context = context;
  }

  public void initalizeViews() {
    this.mListener.onInitialize();
  }

  public void setUpSignUpUser(String fullName, String email, String password, int gender,
      String refralcode, String number, String filePath) {
    {
      DialogUtils.getInstance().showProgressDialog(context);
      Call<GenericResponse> responseCall = RestClient.get()
          .register(
              getParamsAgentSignUp(fullName, email, password, String.valueOf(gender), refralcode,
                  number, filePath));
      responseCall.enqueue(new EmptyCallbackWrapper() {
        @Override
        public void onSuccess(GenericResponse response) {
          DialogUtils.getInstance().hideProgressDialog();
          mListener.onSignUpSuccessFully(response.getMessage());
        }

        @Override
        public void onFailure(GenericResponse response) {
          DialogUtils.getInstance().hideProgressDialog();
          mListener.onSignUpError(response.getMessage());
        }

        @Override
        public void exception(ErrorResponse errorResponse) {
          DialogUtils.getInstance().hideProgressDialog();
          mListener.onSignUpError(errorResponse.getMessage());
        }
      });
    }
  }

  private HashMap<String, RequestBody> getParamsAgentSignUp(String fullName, String email,
      String password, String gender, String refralcode, String number, String filePath) {
    HashMap<String, RequestBody> params = new HashMap<>();
    params.put(RestFields.KEY_NAME, RestUtils.TypedString(fullName));
    params.put(RestFields.KEY_EMAIL_ADDRESS, RestUtils.TypedString(email));
    params.put(RestFields.KEY_PASSWORD, RestUtils.TypedString(password));
    if (TextUtils.isEmpty(number)) {
      params.put(RestFields.KEY_NUMBER, RestUtils.TypedString(""));
    } else {
      params.put(RestFields.KEY_NUMBER, RestUtils.TypedString(number));
    }
    if (TextUtils.isEmpty(filePath)) {
      params.put(RestFields.KEY_PROFILE_PIC, RestUtils.TypedString(""));
    } else {
      File profilePicture = new File(filePath);
      params.put(RestFields.KEY_PROFILE_PIC + "\"; filename=\"" + profilePicture.getName() + "\"",
          RestUtils.TypedImageFile(profilePicture));
    }
    params.put(RestFields.KEY_GENDER, RestUtils.TypedString(gender));

    if (TextUtils.isEmpty(refralcode)) {
      params.put(RestFields.KEY_REFERRAL_CODE, RestUtils.TypedString(""));
    } else {
      params.put(RestFields.KEY_REFERRAL_CODE, RestUtils.TypedString(refralcode));
    }

    params.put(RestFields.KEY_USER_TYPE, RestUtils.TypedString(RestFields.USER_TYPE_REQUESTER));
    return params;
  }

  public void applyReferralCode(String inputReferralCode) {
    if (!TextUtils.isEmpty(inputReferralCode)) {
      Call<GenericResponse> applyReferralCode =
          RestClient.get().referralProgram(RestFields.REQUEST_REFFERAL_CODE, inputReferralCode);
      applyReferralCode.enqueue(new EmptyCallbackWrapper() {
        @Override
        public void onSuccess(GenericResponse response) {
          if (!TextUtils.isEmpty(response.getMessage())) {
            mListener.onPromotionalCode(response.getMessage());
          }
        }

        @Override
        public void onFailure(GenericResponse response) {
          if (!TextUtils.isEmpty(response.getMessage())) {
            mListener.failPromotionalCode(response.getMessage());
          }
        }

        @Override
        public void exception(ErrorResponse errorResponse) {
          if (!TextUtils.isEmpty(errorResponse.getMessage())) {
            mListener.failPromotionalCode(errorResponse.getMessage());
          }
        }
      });
    }
  }

  public interface QuestionerListener {
    void onInitialize();

    void onSignUpSuccessFully(String message);

    void onSignUpError(String message);

    void onPromotionalCode(String message);

    void onRemovePromotionalCode();

    void failPromotionalCode(String errorMessage);
  }
}
