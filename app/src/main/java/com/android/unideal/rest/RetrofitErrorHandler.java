package com.android.unideal.rest;

import android.text.TextUtils;
import android.util.Log;
import com.android.unideal.R;
import com.android.unideal.UniDealApplication;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.util.ConnectivityHelper;
import java.net.ConnectException;
import retrofit2.Response;

public class RetrofitErrorHandler {

  private static final String TAG = "Unideal-Error";

  private static RetrofitErrorHandler errorHandler = new RetrofitErrorHandler();

  private RetrofitErrorHandler() {
  }

  public static RetrofitErrorHandler getErrorHandler() {
    return errorHandler;
  }

  public ErrorResponse buildError(Response response) {
    ErrorResponse errorResponse = new ErrorResponse();
    if (response != null && !TextUtils.isEmpty(response.message())) {
      errorResponse.setMessage(response.message());
    } else {
      errorResponse.setMessage(getString(R.string.alert_internal_exception));
    }
    return errorResponse;
  }

  public ErrorResponse lookUpThrowable(Throwable retrofitError) {
    if (!TextUtils.isEmpty(retrofitError.getMessage())) {
      Log.e(TAG, retrofitError.getMessage());
    }
    if (!ConnectivityHelper.getInstance().isConnected()) {
      return new ErrorResponse().setMessage(getString(R.string.alert_connectivity_problem));
    } else if (retrofitError instanceof ConnectException) {
      return new ErrorResponse().setMessage(getString(R.string.alert_connectivity_problem));
    } else if (retrofitError instanceof java.net.SocketTimeoutException) {
      return new ErrorResponse().setMessage(getString(R.string.alert_time_out));
    } else if (retrofitError instanceof java.net.UnknownHostException) {
      return new ErrorResponse().setMessage(getString(R.string.alert_unknown_exception));
    } else {
      return new ErrorResponse().setMessage(getString(R.string.alert_internal_exception));
    }
  }

  private String getString(int resourceId) {
    return UniDealApplication.getApplication().getString(resourceId);
  }
}
