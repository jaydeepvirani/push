package com.android.unideal.rest;

import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class CallbackWrapper<T> implements Callback<GenericResponse<T>> {

  public abstract void onSuccess(GenericResponse<T> response);

  public abstract void onFailure(GenericResponse<T> response);

  public abstract void exception(ErrorResponse errorResponse);

  @Override
  public void onResponse(Call<GenericResponse<T>> call, Response<GenericResponse<T>> response) {
    if (response.isSuccessful()) {
      if (response.body().isSuccess() == 1) {
        if (response.body().getStatus_code() == RestFields.STATUS_SUCCESS) {
          onSuccess(response.body());
        } else {
          onFailure(response.body());
        }
      } else {
        onFailure(response.body());
      }
    } else {
      ErrorResponse errorResponse = RetrofitErrorHandler.getErrorHandler().buildError(response);
      exception(errorResponse);
    }
  }

  @Override
  public void onFailure(Call<GenericResponse<T>> call, Throwable t) {
    exception(RetrofitErrorHandler.getErrorHandler().lookUpThrowable(t));
  }
}
