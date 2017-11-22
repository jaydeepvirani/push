package com.android.unideal.agent.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.android.unideal.R;
import com.android.unideal.agent.view.fragment.jobdetails.JobDetailsFragment;
import com.android.unideal.agent.viewmodel.ProposalViewModel;
import com.android.unideal.data.JobDetail;
import com.android.unideal.databinding.ProposalDataBinding;
import com.android.unideal.enums.AppMode;
import com.android.unideal.flurry.FlurryManager;
import com.android.unideal.questioner.view.DisclaimerActivity;
import com.android.unideal.questioner.view.UniDealDeliveryPlaceActivity;
import com.android.unideal.util.AppUtility;
import com.android.unideal.util.DialogUtils;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxRadioGroup;
import com.jakewharton.rxbinding.widget.RxTextView;
import rx.functions.Action1;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Proposal Dialog Activity when agent try to apply on particular job
 * under the proposal details section.
 */
public class ProposalActivity extends AppCompatActivity
    implements ProposalViewModel.ProposalViewHandler {
  public static final String KEY_JOB_ID = "jobId";
  public static final String KEY_REQUESTER_ID = "requesterId";
  private static final String TAG = "ProposalActivity";
  private ProposalDataBinding mDataBinding;
  private ProposalViewModel mProposalViewModel;
  private int currentJobId = -1;
  private int jobRequesterId = -1;
  private boolean isRadioInit = false;
  private String TNC_TYPE = "agtApply";
  private int REQUEST_ACPT = 370;
  private boolean isCustomSelect;
  private boolean isUniDealSelect;

  /**
   * Open the Proposal Activity As Dialog for send the proposal on particular jon
   */
  public static Intent getDialogActivity(Activity activity, int jobId, int requesterId) {
    Intent proposalIntent = new Intent(activity, ProposalActivity.class);
    proposalIntent.putExtra(KEY_JOB_ID, jobId);
    proposalIntent.putExtra(KEY_REQUESTER_ID, requesterId);
    return proposalIntent;
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_proposal_dialog);
    getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    mProposalViewModel = new ProposalViewModel(this);
    mProposalViewModel.onCreate();
  }

  @Override protected void onResume() {
    super.onResume();
    isRadioInit = true;
  }

  @Override protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  @Override public void extractIntentData() {
    if (getIntent().hasExtra(KEY_JOB_ID)) {
      currentJobId = getIntent().getIntExtra(KEY_JOB_ID, -1);
      jobRequesterId = getIntent().getIntExtra(KEY_REQUESTER_ID, -1);
    }
  }

  @Override public void startBindingViews() {
    RxTextView.textChanges(mDataBinding.etSendProposal).subscribe(new Action1<CharSequence>() {
      @Override public void call(CharSequence charSequence) {
       getStatusOfSlectPlace();
        updateSendButton(charSequence.toString(),
            mDataBinding.editTextOfferPrice.getText().toString(),
            mDataBinding.editTextCustomDelivery.getText().toString());
      }
    });

    RxTextView.textChanges(mDataBinding.editTextOfferPrice).subscribe(new Action1<CharSequence>() {
      @Override public void call(CharSequence charSequence) {
        getStatusOfSlectPlace();
        updateSendButton(mDataBinding.etSendProposal.getText().toString(), charSequence.toString(),
            mDataBinding.editTextCustomDelivery.getText().toString());
      }
    });
    RxTextView.textChanges(mDataBinding.editTextCustomDelivery)
        .subscribe(new Action1<CharSequence>() {
          @Override public void call(CharSequence charSequence) {
            getStatusOfSlectPlace();
            updateSendButton(mDataBinding.etSendProposal.getText().toString(),
                mDataBinding.editTextOfferPrice.getText().toString(), charSequence.toString());
          }
        });
    RxView.clicks(mDataBinding.btnSendProposal).subscribe(new Action1<Void>() {
      @Override public void call(Void aVoid) {
        Intent sendProposalIntent =
            DisclaimerActivity.getDialogActivity(ProposalActivity.this, AppMode.AGENT, TNC_TYPE);
        startActivityForResult(sendProposalIntent, REQUEST_ACPT);
      }
    });

    RxView.clicks(mDataBinding.imageViewClose).subscribe(new Action1<Void>() {
      @Override public void call(Void aVoid) {
        setResult(RESULT_CANCELED);
        finish();
      }
    });

    RxRadioGroup.checkedChanges(mDataBinding.radioGroupDeliveryPlace)
        .subscribe(new Action1<Integer>() {
          @Override public void call(Integer integer) {
            Log.d(TAG, "call: " + integer);
            if (!isRadioInit) return;
            switch (integer) {
              case R.id.radioBtnCustom: {
                mDataBinding.editTextCustomDelivery.setVisibility(View.VISIBLE);
                isCustomSelect = true;
                isUniDealSelect = false;
              customSelect(mDataBinding.editTextCustomDelivery.getText().toString(),mDataBinding.editTextOfferPrice.getText().toString(),mDataBinding.etSendProposal.getText().toString());
                break;
              }
              case R.id.radioBtnUniDeal: {
                mDataBinding.editTextCustomDelivery.setVisibility(View.GONE);
                isCustomSelect = false;
                isUniDealSelect = true;
                uniDealSelect(mDataBinding.editTextOfferPrice.getText().toString(),mDataBinding.etSendProposal.getText().toString());
                UniDealDeliveryPlaceActivity.startDeliveryPlaceActivity(ProposalActivity.this,
                    AppMode.AGENT);

                break;
              }
            }
          }
        });
  }

  private void getStatusOfSlectPlace() {
    if(mDataBinding.radioBtnCustom.isChecked()){
      isCustomSelect=true;
      isUniDealSelect=false;
    }else if(mDataBinding.radioBtnUniDeal.isChecked()){
      isUniDealSelect=true;
      isCustomSelect=false;
    }else{
      isCustomSelect=true;
      isUniDealSelect=false;
    }
  }

  private void updateSendButton(final String text1, final String text2, final String text3) {
    if (isCustomSelect) {
      customSelect(text1, text2, text3);
    } else if (isUniDealSelect) {
      uniDealSelect(text1, text2);
    } else {
      customSelect(text1, text2, text3);
    }
  }

  public void customSelect(String text1, String text2, String text3) {
    if (text1.trim().length() > 0 && text2.trim().length() > 0 && text3.trim().length() > 0) {
      mDataBinding.btnSendProposal.setAlpha(1);
      mDataBinding.btnSendProposal.setEnabled(true);
    } else {
      mDataBinding.btnSendProposal.setAlpha(0.6f);
      mDataBinding.btnSendProposal.setEnabled(false);
    }
  }

  public void uniDealSelect(String text1, String text2) {
    if (text1.trim().length() > 0 && text2.trim().length() > 0) {
      mDataBinding.btnSendProposal.setAlpha(1);
      mDataBinding.btnSendProposal.setEnabled(true);
    } else {
      mDataBinding.btnSendProposal.setAlpha(0.6f);
      mDataBinding.btnSendProposal.setEnabled(false);
    }
  }

  private void sendProposal() {
    String details = mDataBinding.etSendProposal.getText().toString().trim();
    if (TextUtils.isEmpty(details)) {
      AppUtility.showToast(this, R.string.msg_proposal);
      return;
    }
    String offerPriceString = mDataBinding.editTextOfferPrice.getText().toString().trim();
    if (TextUtils.isEmpty(offerPriceString)) {
      return;
    }
    String deliveryPlace = null;
    if (mDataBinding.radioBtnCustom.isChecked()) {
      deliveryPlace = mDataBinding.editTextCustomDelivery.getText().toString().trim();
      if (TextUtils.isEmpty(deliveryPlace)) {
        return;
      }
    }
    try {
      float offerPrice = Float.parseFloat(offerPriceString);
      mProposalViewModel.startSendingProposal(currentJobId, details, offerPrice, deliveryPlace);
    } catch (NumberFormatException e) {
      e.printStackTrace();
      AppUtility.showToast(this, R.string.error_number_format);
    }
  }

  @Override public void showProgressBarView(boolean showProgress) {
    if (showProgress) {
      DialogUtils.getInstance().showProgressDialog(ProposalActivity.this);
    } else {
      DialogUtils.getInstance().hideProgressDialog();
    }
  }

  @Override public void sendProposalSuccessfully(String message, JobDetail jobDetail) {
    if (jobRequesterId != -1) {
      mProposalViewModel.sendingProposalOnSocket(jobRequesterId);
      Log.d(TAG, "sendProposalSuccessfully: " + jobRequesterId);
    }
    if (!TextUtils.isEmpty(message)) {
      AppUtility.showToast(ProposalActivity.this, message);
    }

    //Send Proposal
    FlurryManager.agentApplyOnJob(currentJobId, String.valueOf(jobDetail.getJob_id()));

    Intent mIntent = new Intent();
    mIntent.putExtra(JobDetailsFragment.KEY_JOB_DETAILS, jobDetail);
    setResult(RESULT_OK, mIntent);
    finish();
  }

  @Override public void failOnSendingProposal(String failureMessage) {
    DialogUtils.showExceptionDialog(ProposalActivity.this, failureMessage);
  }

  @Override public void exceptionSendingProposal(String exceptionMessage) {
    DialogUtils.showExceptionDialog(ProposalActivity.this, exceptionMessage);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_ACPT) {
      if (resultCode == Activity.RESULT_OK) {
        sendProposal();
      }
    }
  }
}
