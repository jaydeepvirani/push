package com.android.unideal.agent.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import com.android.unideal.data.UserDetail;
import com.android.unideal.rest.CallbackWrapper;
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
 * Created by ADMIN on 07-10-2016.
 */

public class ProfileFragmentViewModel extends BaseObservable {
  private Context context;
  private AgentProfileListener mListener;

  public ProfileFragmentViewModel(Context context, AgentProfileListener mListener) {
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
        mListener.setEditProfileInfo(response.getData());
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

  public Map<String, Object> getProfileParams() {
    HashMap<String, Object> params = new HashMap<>();
    params.put(RestFields.KEY_USER_ID, SessionManager.get(context).getActiveUser().getUserId());
    params.put(RestFields.KEY_USER_TYPE, RestFields.USER_TYPE_AGENT);
    return params;
  }

  public interface AgentProfileListener {
    void onError(String message);

    void setEditProfileInfo(UserDetail data);

    void showProgressBar();

    void hideProgressBar();
  }
}
