package com.android.unideal.agent.viewmodel;

import android.content.Context;
import com.android.unideal.data.AgentOfferData;
import com.android.unideal.data.UserDetail;
import com.android.unideal.rest.CallbackWrapper;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;

/**
 * Created by ADMIN on 23-09-2016.
 */

public class AgentHomeViewModel {

  private static final String TAG = "AgentHomeViewModel";
  private Context context;
  private AgentHomeListener mListener;

  public AgentHomeViewModel(Context context, AgentHomeListener mListener) {
    this.context = context;
    this.mListener = mListener;
  }

  public void onCreate() {
    mListener.setUpResideMenu();
    mListener.startBindingViews();
    mListener.updateProfileInfo();
  }

  /**
   * This will make a call and find any new offer from requester side.
   * The offers might be more then one or two we display into the
   * view pager as list
   */
  public void listenOnNewOffers(UserDetail userDetail) {
    Call<GenericResponse<List<AgentOfferData>>> requestGetOffersList =
        RestClient.get().getAgentOffers(getOfferParam(userDetail));
    requestGetOffersList.enqueue(new CallbackWrapper<List<AgentOfferData>>() {
      @Override
      public void onSuccess(GenericResponse<List<AgentOfferData>> response) {
        if (response.isSuccess() == 1) {
          mListener.onWatchOffers(response.getData(), response.getMessage());
        }
      }

      @Override
      public void onFailure(GenericResponse<List<AgentOfferData>> response) {
        mListener.onWatchOffers(null, response.getMessage());
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        mListener.onWatchOffers(null, errorResponse.getMessage());
      }
    });
  }

  private Map<String, Object> getOfferParam(UserDetail userDetail) {
    HashMap<String, Object> offerParameter = new HashMap<>();
    //user id
    offerParameter.put(RestFields.KEY_USER_ID, String.valueOf(userDetail.getUserId()));
    //user type
    offerParameter.put(RestFields.KEY_USER_TYPE, RestFields.USER_TYPE_AGENT);
    return offerParameter;
  }

  public interface AgentHomeListener {
    void setUpResideMenu();

    void startBindingViews();

    void updateProfileInfo();

    void showProgressDialog();

    void hideProgressDialog();

    void showToast(String message);

    void onWatchOffers(List<AgentOfferData> offerDataList, String extra);
  }
}
