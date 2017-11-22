package com.android.unideal.questioner.viewmodel;

import android.content.Context;
import com.android.unideal.data.UserDetail;
import com.android.unideal.rest.CallbackWrapper;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import com.android.unideal.util.SessionManager;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;

/**
 * Created by ADMIN on 11-10-2016.
 */

public class ProfileFragmentViewModel {
  private Context context;
  private ProfileFragmentListener mListener;

  public ProfileFragmentViewModel(Context context, ProfileFragmentListener mListener) {
    this.context = context;
    this.mListener = mListener;
  }

  public void getUserProfile() {
    mListener.showProgressBar();
    Call<GenericResponse<UserDetail>> responseCall =
        RestClient.get().getUserProfile(getProfileParams());
    responseCall.enqueue(new CallbackWrapper<UserDetail>() {
      @Override
      public void onSuccess(GenericResponse<UserDetail> response) {
        mListener.hideProgressBar();
        mListener.setProfileInfo(response.getData());
      }

      @Override
      public void onFailure(GenericResponse<UserDetail> response) {
        mListener.hideProgressBar();
        mListener.onError(response.getMessage());
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        mListener.hideProgressBar();
        mListener.onError(errorResponse.getMessage());
      }
    });
  }

  private Map<String, Object> getProfileParams() {
    HashMap<String, Object> params = new HashMap<>();
    params.put(RestFields.KEY_USER_ID, SessionManager.get(context).getActiveUser().getUserId());
    params.put(RestFields.KEY_USER_TYPE, RestFields.USER_TYPE_REQUESTER);
    return params;
  }

  public interface ProfileFragmentListener {
    void onError(String message);

    void setProfileInfo(UserDetail data);

    void showProgressBar();

    void hideProgressBar();
  }
}
