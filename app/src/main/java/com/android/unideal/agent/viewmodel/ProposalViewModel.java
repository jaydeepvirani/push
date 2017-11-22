package com.android.unideal.agent.viewmodel;

import android.content.Context;
import android.text.TextUtils;
import com.android.unideal.data.JobDetail;
import com.android.unideal.data.UserDetail;
import com.android.unideal.rest.CallbackWrapper;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import com.android.unideal.socket.SocketConst;
import com.android.unideal.socket.SocketManager;
import com.android.unideal.util.SessionManager;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;

/**
 * Created by bhavdip on 21/11/16.
 */

public class ProposalViewModel {
  private ProposalViewHandler mViewHandler;
  private Context mContext;
  private SocketManager mSocketManager;

  public ProposalViewModel(Context context) {
    this.mContext = context;
    if (context instanceof ProposalViewHandler) {
      this.mViewHandler = (ProposalViewHandler) context;
    }
  }

  private Context getContext() {
    return mContext;
  }

  public void onCreate() {
    initSocket();
    this.mViewHandler.extractIntentData();
    this.mViewHandler.startBindingViews();
  }

  public void startSendingProposal(int jobId, String details, float offerPriceString,
      String deliveryPlace) {
    mViewHandler.showProgressBarView(true);
    Call<GenericResponse<JobDetail>> sendProposalCall = RestClient.get()
        .sendProposal(getProposalParameter(jobId, details, offerPriceString, deliveryPlace));
    sendProposalCall.enqueue(new CallbackWrapper<JobDetail>() {
      @Override
      public void onSuccess(GenericResponse<JobDetail> response) {
        mViewHandler.showProgressBarView(false);
        mViewHandler.sendProposalSuccessfully(response.getMessage(), response.getData());
      }

      @Override
      public void onFailure(GenericResponse<JobDetail> response) {
        mViewHandler.showProgressBarView(false);
        mViewHandler.failOnSendingProposal(response.getMessage());
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        mViewHandler.showProgressBarView(false);
        mViewHandler.exceptionSendingProposal(errorResponse.getMessage());
      }
    });
  }

  private Map<String, Object> getProposalParameter(int jobId, String proposalDetails,
      float offerPriceString, String deliveryAddress) {
    HashMap<String, Object> sendProposalParams = new HashMap<>();
    //1 User Type
    sendProposalParams.put(RestFields.KEY_USER_TYPE, String.valueOf(RestFields.USER_TYPE_AGENT));
    UserDetail userDetail = SessionManager.get(getContext()).getActiveUser();
    if (userDetail != null) {
      //2 User ID
      sendProposalParams.put(RestFields.KEY_USER_ID, String.valueOf(userDetail.getUserId()));
    }
    //3 Job ID
    sendProposalParams.put(RestFields.KEY_JOB_ID, String.valueOf(jobId));
    //4 Proposal Details
    if (!TextUtils.isEmpty(proposalDetails)) {
      sendProposalParams.put(RestFields.KEY_PROPOSAL_DETAILS, proposalDetails);
    }
    //5 offer price
    sendProposalParams.put(RestFields.KEY_OFFER, offerPriceString);

    // delivery address
    if (!TextUtils.isEmpty(deliveryAddress)) {
      sendProposalParams.put(RestFields.KEY_DELIVERY, deliveryAddress);
    }
    return sendProposalParams;
  }

  public void sendingProposalOnSocket(int jobRequesterId) {
    if (mSocketManager != null && mSocketManager.isConnected()) {
      mSocketManager.emitEvent(SocketConst.EVENT_REQ_SEND_PROPOSAL, jobRequesterId);
    }
  }

  public void initSocket() {
    mSocketManager = SocketManager.getInstance();
    if (!mSocketManager.isConnected()) {
      mSocketManager.openSocket();
      mSocketManager.listenEvents();
      mSocketManager.connect();
    }
  }

  public interface ProposalViewHandler {
    void extractIntentData();

    void startBindingViews();

    void showProgressBarView(boolean showProgress);

    void sendProposalSuccessfully(String message, JobDetail jobDetail);

    void failOnSendingProposal(String failureMessage);

    void exceptionSendingProposal(String exceptionMessage);
  }
}
