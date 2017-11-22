package com.android.unideal.fcm;

import android.text.TextUtils;
import android.util.Log;
import com.android.unideal.rest.EmptyCallbackWrapper;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import com.android.unideal.util.SessionManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;

/**
 * Created by Belal on 5/27/2016.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

  private static final String TAG = "MyFirebaseIIDService";

  @Override
  public void onTokenRefresh() {
    String refreshedToken = FirebaseInstanceId.getInstance().getToken();
    Log.d(TAG, "Refreshed token: " + refreshedToken);
    sendRegistrationToServer(refreshedToken);
  }

  private void sendRegistrationToServer(final String token) {
    int userId = SessionManager.get(getApplicationContext()).getUserId();
    String oldToken = SessionManager.get(getApplicationContext()).getDeviceToken();

    if (!TextUtils.isEmpty(token)) {
      if (!token.equals(SessionManager.get(getApplicationContext()).getDeviceToken())) {
        if (SessionManager.get(getApplicationContext()).getUserId() != -1) {
          Call<GenericResponse> responseCall =
              RestClient.get().updateToken(getParams(token, userId, oldToken));
          responseCall.enqueue(new EmptyCallbackWrapper() {
            @Override
            public void onSuccess(GenericResponse response) {
              SessionManager.get(getApplicationContext()).setDeviceToken(token);
            }

            @Override
            public void onFailure(GenericResponse response) {
              Log.d(TAG, "onFailure: " + response.getMessage());
            }

            @Override
            public void exception(ErrorResponse errorResponse) {
              Log.d(TAG, "onFailure: " + errorResponse.getMessage());
            }
          });
        }
      }
    }
  }

  private Map<String, Object> getParams(String newToken, int userId, String oldToken) {
    HashMap<String, Object> params = new HashMap<>();
    params.put(RestFields.KEY_USER_ID, userId);
    params.put(RestFields.KEY_NEW_DEVICE_TOKEN, newToken);
    if (!TextUtils.isEmpty(oldToken)) {
      params.put(RestFields.KEY_OLD_DEVICE_TOKEN, oldToken);
    } else {
      params.put(RestFields.KEY_OLD_DEVICE_TOKEN, "");
    }

    return params;
  }
}