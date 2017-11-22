package com.android.unideal.rest;

import com.android.unideal.rest.response.payment.VaultError;
import com.android.unideal.util.converter.JacksonConverter;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by bhavdip on 1/1/17.
 */

public abstract class PaymentCallbackWrapper<T> implements Callback<T> {

  public abstract void onSuccess(T cardData);

  public abstract void onFailure(VaultError response);

  public abstract void exception(VaultError errorResponse);

  @Override
  public void onResponse(Call call, Response response) {
    if (response.code() >= 400 && response.code() < 599) {
      if (response.errorBody() != null) {
        VaultError vaultError = new VaultError();
        try {
          vaultError =
              JacksonConverter.getObjectFromJSON(response.errorBody().string(), VaultError.class);
        } catch (IOException e) {
          vaultError.setMessage(e.getMessage());
          exception(vaultError);
          e.printStackTrace();
        }
        onFailure((vaultError));
      }
    } else if (response.code() >= 201) {
      onSuccess((T) response.body());
    } else {
      onSuccess((T) response.body());
    }
  }

  @Override
  public void onFailure(Call call, Throwable t) {
    VaultError vaultFailure = new VaultError();
    if (t != null) {
      vaultFailure.setMessage(t.getMessage());
      if (t.getCause() != null) {
        vaultFailure.setName(t.getCause().getMessage());
      }
    }
    onFailure(vaultFailure);
  }
}
