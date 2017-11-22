package com.android.unideal.agent.viewmodel;

import com.android.unideal.data.AgentOfferData;
import com.android.unideal.rest.EmptyCallbackWrapper;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;

/**
 * Created by bhavdip on 12/27/16.
 */

public class AgentOfferViewModel {

  private AgentOfferHandler mHandler;

  public AgentOfferViewModel(AgentOfferHandler offerHandler) {
    this.mHandler = offerHandler;
  }

  public void onCreate() {
    mHandler.extractIntentData();
    mHandler.startBindingViews();
  }

  public void performActionOnOffer(final int offerAction, AgentOfferData offerData) {
    mHandler.showProgressBar();
    Call<GenericResponse> requestAcceptOffer =
        RestClient.get().agentofferaction(getOfferParam(offerData, offerAction));
    requestAcceptOffer.enqueue(new EmptyCallbackWrapper() {
      @Override
      public void onSuccess(GenericResponse response) {
        mHandler.hideProgressBar();
        if (response.isSuccess() == 1) {
          mHandler.onActionResult(response.getStatus_code(), response.getMessage(), offerAction);
        }
      }

      @Override
      public void onFailure(GenericResponse response) {
        mHandler.hideProgressBar();
        mHandler.onActionResult(response.getStatus_code(), response.getMessage(), offerAction);
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        mHandler.hideProgressBar();
      }
    });
  }

  /**
   * @param action 1:accept 2:decline 3: Modify
   */
  private Map<String, Object> getOfferParam(AgentOfferData offerData, int action) {
    HashMap<String, Object> offerParameter = new HashMap<>();
    if (offerData != null) {
      //user id
      offerParameter.put(RestFields.KEY_USER_ID, String.valueOf(offerData.getAgent_id()));
      //user type
      offerParameter.put(RestFields.KEY_USER_TYPE, RestFields.USER_TYPE_AGENT);
      //Job id
      offerParameter.put(RestFields.KEY_JOB_ID, offerData.getJob_id());
      //requester_id
      offerParameter.put(RestFields.KEY_REQUESTER_ID, offerData.getRequester_id());
      //consignment_size
      offerParameter.put(RestFields.KEY_CONSIGNMENT_SIZE, offerData.getConsignment_size());
      //action
      offerParameter.put(RestFields.KEY_ACTION, action);
      // If action is modify then it consider as edit
      if (action == 3) {
        //action
        offerParameter.put(RestFields.KEY_IS_CONSIGNMENT_EDIT, 1);
      }
    }
    return offerParameter;
  }

  public interface AgentOfferHandler {
    void extractIntentData();

    void startBindingViews();

    void showProgressBar();

    void hideProgressBar();

    void onActionResult(int responseCode, String resultMessage, int action);
  }
}
