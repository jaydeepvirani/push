package com.android.unideal.agent.viewmodel;

import android.content.Context;
import android.text.TextUtils;
import com.android.unideal.data.UserDetail;
import com.android.unideal.rest.CallbackWrapper;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.RestUtils;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.SessionManager;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * Created by ADMIN on 20-10-2016.
 */

public class AgentProfileViewModel {
  private Context context;
  private AgentProfileListener mListener;

  public AgentProfileViewModel(Context context, AgentProfileListener mListener) {
    this.context = context;
    this.mListener = mListener;
  }

  public void updateProfile(String fullName, String email, String oldPassword, String newPassword,
      String number, String filePath, int genderType, String bio, int expertise,
      String uploadFileName) {
    DialogUtils.getInstance().showProgressDialog(context);
    Call<GenericResponse<UserDetail>> responseCall = RestClient.get()
        .updateProfile(
            getEditParams(fullName, email, oldPassword, newPassword, number, filePath, genderType,
                bio, expertise, uploadFileName));
    responseCall.enqueue(new CallbackWrapper<UserDetail>() {
      @Override
      public void onSuccess(GenericResponse<UserDetail> response) {
        DialogUtils.getInstance().hideProgressDialog();
        mListener.updateSuccessfully(response.getMessage());
        SessionManager.get(context).saveUserDetail(response.getData());
      }

      @Override
      public void onFailure(GenericResponse<UserDetail> response) {
        DialogUtils.getInstance().hideProgressDialog();
        mListener.onFailed(response.getMessage());
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        DialogUtils.getInstance().hideProgressDialog();
        mListener.onFailed(errorResponse.getMessage());
      }
    });
  }

  public Map<String, RequestBody> getEditParams(String fullName, String email, String oldPassword,
      String newPassword, String number, String filePath, int genderType, String bio, int expertise,
      String uploadFileName) {
    HashMap<String, RequestBody> params = new HashMap<>();
    UserDetail userDetail = SessionManager.get(context).getActiveUser();
    if (TextUtils.isEmpty(email)) {
      params.put(RestFields.KEY_EMAIL_ADDRESS, RestUtils.TypedString(""));
    } else {
      params.put(RestFields.KEY_EMAIL_ADDRESS, RestUtils.TypedString(email));
    }
    if (TextUtils.isEmpty(oldPassword)) {
      params.put(RestFields.KEY_OLD_PASSWORD, RestUtils.TypedString(""));
    } else {
      params.put(RestFields.KEY_OLD_PASSWORD, RestUtils.TypedString(oldPassword));
    }
    if (TextUtils.isEmpty(newPassword)) {
      params.put(RestFields.KEY_NEW_PASSWORD, RestUtils.TypedString(""));
    } else {
      params.put(RestFields.KEY_NEW_PASSWORD, RestUtils.TypedString(newPassword));
    }
    if (TextUtils.isEmpty(filePath)) {
      String imageUrl = SessionManager.get(context).getActiveUser().getProfilePic();
      if (!TextUtils.isEmpty(imageUrl)) {
        params.put(RestFields.KEY_PROFILE_PIC, RestUtils.TypedString(imageUrl));
      } else {
        params.put(RestFields.KEY_PROFILE_PIC, RestUtils.TypedString(""));
      }
    } else {
      File file = new File(filePath);
      params.put(RestFields.KEY_PROFILE_PIC + "\"; filename=\"" + file.getName() + "\"",
          RestUtils.TypedImageFile(file));
    }
    if (TextUtils.isEmpty(fullName)) {
      params.put(RestFields.KEY_NAME, RestUtils.TypedString(""));
    } else {
      params.put(RestFields.KEY_NAME, RestUtils.TypedString(fullName));
    }
    if (TextUtils.isEmpty(number)) {
      params.put(RestFields.KEY_NUMBER, RestUtils.TypedString(""));
    } else {
      params.put(RestFields.KEY_NUMBER, RestUtils.TypedString(number));
    }
    if (TextUtils.isEmpty(bio)) {
      params.put(RestFields.KEY_BIO, RestUtils.TypedString(""));
    } else {
      params.put(RestFields.KEY_BIO, RestUtils.TypedString(bio));
    }
    params.put(RestFields.KEY_EXPERTISE, RestUtils.TypedString(expertise));
    if (TextUtils.isEmpty(uploadFileName)) {
      params.put(RestFields.KEY_DOCUMENTS, RestUtils.TypedString(""));
    } else {
      File fileDocument = new File(uploadFileName);
      params.put(RestFields.KEY_DOCUMENTS + "\"; filename=\"" + fileDocument.getName() + "\"",
          RestUtils.TypedDocumentFile(fileDocument));
    }
    params.put(RestFields.KEY_FROM_SOCIAL_NETWORK,
        RestUtils.TypedString(userDetail.getFromSocialNetwork()));
    params.put(RestFields.KEY_USER_ID,
        RestUtils.TypedString(SessionManager.get(context).getActiveUser().getUserId()));
    params.put(RestFields.KEY_GENDER, RestUtils.TypedString(genderType));
    params.put(RestFields.KEY_USER_TYPE, RestUtils.TypedString(RestFields.USER_TYPE_AGENT));
    return params;
  }

  public interface AgentProfileListener {
    void updateSuccessfully(String message);

    void onFailed(String message);
  }
}
