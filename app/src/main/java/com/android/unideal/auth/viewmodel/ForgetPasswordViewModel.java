package com.android.unideal.auth.viewmodel;

import android.content.Context;
import com.android.unideal.rest.EmptyCallbackWrapper;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import com.android.unideal.util.DialogUtils;
import java.util.HashMap;
import retrofit2.Call;

/**
 * Created by ADMIN on 03-10-2016.
 */

public class ForgetPasswordViewModel {
  private ForgetPasswordListener mListener;
  private Context context;

  public ForgetPasswordViewModel(Context context, ForgetPasswordListener mListener) {
    this.context = context;
    this.mListener = mListener;
  }

  public void setResetPassword(String email) {
    DialogUtils.getInstance().showProgressDialog(context);
    HashMap<String, Object> params = new HashMap<>();
    params.put(RestFields.KEY_EMAIL_ADDRESS, email);
    Call<GenericResponse> responseCall = RestClient.get().resetPassword(params);
    responseCall.enqueue(new EmptyCallbackWrapper() {
      @Override
      public void onSuccess(GenericResponse response) {
        DialogUtils.getInstance().hideProgressDialog();
        mListener.onResetSuccessfully(response.getMessage());
      }

      @Override
      public void onFailure(GenericResponse response) {
        DialogUtils.getInstance().hideProgressDialog();
        mListener.onFailPassword(response.getMessage());
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        DialogUtils.getInstance().hideProgressDialog();
        mListener.onFailPassword(errorResponse.getMessage());
      }
    });
  }

  public interface ForgetPasswordListener {
    void onResetSuccessfully(String message);

    void onFailPassword(String message);
  }
}
