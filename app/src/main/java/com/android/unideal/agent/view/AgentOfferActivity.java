package com.android.unideal.agent.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.android.unideal.R;
import com.android.unideal.agent.view.fragment.AgentOfferFragment;
import com.android.unideal.agent.viewmodel.AgentOfferViewModel;
import com.android.unideal.data.AgentOfferData;
import com.android.unideal.databinding.AgentOfferBinding;
import com.android.unideal.rest.RestFields;
import com.android.unideal.util.AppUtility;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.converter.JacksonConverter;
import com.jakewharton.rxbinding.view.RxView;
import java.util.ArrayList;
import java.util.List;
import rx.functions.Action1;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by bhavdip on 12/27/16.
 */

public class AgentOfferActivity extends AppCompatActivity
    implements AgentOfferViewModel.AgentOfferHandler {
  private static final String TAG = "AgentOfferActivity";
  private static final String KEY_OFFERS_DATA = "offersList";
  private AgentOfferBinding mOfferBinding;
  private AgentOfferViewModel mOfferViewModel;
  private List<AgentOfferData> mOfferDataList;
  private OfferPagesAdapter mPagesAdapter;
  private OfferPageChangeHandler mPageChangeHandler;

  public static void startOfferActivity(Activity activity, String agentOfferDatas) {
    Intent offerIntent = new Intent(activity, AgentOfferActivity.class);
    offerIntent.putExtra(KEY_OFFERS_DATA, agentOfferDatas);
    activity.startActivity(offerIntent);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mOfferBinding = DataBindingUtil.setContentView(this, R.layout.activity_agent_offer);
    getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    mOfferViewModel = new AgentOfferViewModel(this);
    mOfferViewModel.onCreate();
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  @Override
  public void extractIntentData() {
    if (getIntent().hasExtra(KEY_OFFERS_DATA)) {
      String jsonArrayList = getIntent().getStringExtra(KEY_OFFERS_DATA);
      mOfferDataList = JacksonConverter.getListTypeFromJSON(jsonArrayList, AgentOfferData.class);
    }
  }

  @Override
  public void startBindingViews() {
    if (mOfferDataList != null && mOfferDataList.size() > 0) {
      mPageChangeHandler = new OfferPageChangeHandler();
      mOfferBinding.AgentOffersPages.addOnPageChangeListener(mPageChangeHandler);
      mPagesAdapter = new OfferPagesAdapter(getSupportFragmentManager(), mOfferDataList);
      mOfferBinding.AgentOffersPages.setAdapter(mPagesAdapter);
      //Inner child
      mOfferBinding.layoutBottomFirst.setVisibility(View.VISIBLE);
      //Parent of Inner child
      mOfferBinding.requesterOfferSection.setVisibility(View.VISIBLE);
      //default first position
      updateOfferTitle(0);
    }

    RxView.clicks(mOfferBinding.textViewAccept).subscribe(new Action1<Void>() {
      @Override
      public void call(Void aVoid) {
        performOnRequesterOffer(RestFields.ACTION_ACCEPT);
      }
    });

    RxView.clicks(mOfferBinding.textViewDecline).subscribe(new Action1<Void>() {
      @Override
      public void call(Void aVoid) {
        performOnRequesterOffer(RestFields.ACTION_REJECT);
      }
    });

    RxView.clicks(mOfferBinding.textViewCancel).subscribe(new Action1<Void>() {
      @Override
      public void call(Void aVoid) {
        finish();
      }
    });

    RxView.clicks(mOfferBinding.textViewEdit).subscribe(new Action1<Void>() {
      @Override
      public void call(Void aVoid) {
        preformAgentEditOffer();
      }
    });

    RxView.clicks(mOfferBinding.textViewModify).subscribe(new Action1<Void>() {
      @Override
      public void call(Void aVoid) {
        sendAgentOfferAction();
      }
    });

    RxView.clicks(mOfferBinding.textViewCancelModify).subscribe(new Action1<Void>() {
      @Override
      public void call(Void aVoid) {
        closeAgentEditOffer();
      }
    });
  }

  @Override
  public void showProgressBar() {
    DialogUtils.getInstance().showProgressDialog(this);
  }

  @Override
  public void hideProgressBar() {
    DialogUtils.getInstance().hideProgressDialog();
  }

  private void performOnRequesterOffer(int action) {
    int currentOffers = mOfferBinding.AgentOffersPages.getCurrentItem();
    AgentOfferData agentOfferData = mOfferDataList.get(currentOffers);
    mOfferViewModel.performActionOnOffer(action, agentOfferData);
  }

  /**
   * Send the counter consignment action to requester
   */
  private void sendAgentOfferAction() {
    int currentOffers = mOfferBinding.AgentOffersPages.getCurrentItem();
    AgentOfferData agentOfferData = mOfferDataList.get(currentOffers);
    float oldCSize = agentOfferData.getConsignment_size();
    String nwCSizeString = mOfferBinding.editTextNewSize.getText().toString();
    if (!TextUtils.isEmpty(nwCSizeString)) {
      int nwcSize = (int) Double.parseDouble(mOfferBinding.editTextNewSize.getText().toString());
      if (validateConsignmentSize(oldCSize, nwcSize)) {
        agentOfferData.setConsignment_size(nwcSize);
        //hide soft window keyboard
        closeSoftWindowKeyboard();
        //Make a call for do the action
        mOfferViewModel.performActionOnOffer(RestFields.ACTION_MODIFY, agentOfferData);
      } else {
        DialogUtils.showToast(getApplicationContext(), R.string.error_consignment_size_diff);
      }
    } else {
      DialogUtils.showToast(getApplicationContext(), R.string.error_consignment_size);
    }
  }

  private void preformAgentEditOffer() {
    //1. hide the all offer layout and action section
    mOfferBinding.requesterOfferSection.setVisibility(View.GONE);
    //2. show the agent offer section
    //We should bind the content before display
    prepareAgentOfferContent();
    mOfferBinding.agentOfferSection.setVisibility(View.VISIBLE);
  }

  private void closeAgentEditOffer() {
    //hide soft window keyboard
    closeSoftWindowKeyboard();
    //flush the entry of new offer size
    mOfferBinding.editTextNewSize.getText().clear();
    // hide the agent offer section completely
    mOfferBinding.agentOfferSection.setVisibility(View.GONE);
    //show the original next request offer to agent
    mOfferBinding.requesterOfferSection.setVisibility(View.VISIBLE);
  }

  private boolean validateConsignmentSize(float oldCSize, float newCSize) {
    if (oldCSize == newCSize) {
      return false;
    }
    return true;
  }

  private void prepareAgentOfferContent() {
    int currentOffers = mOfferBinding.AgentOffersPages.getCurrentItem();
    AgentOfferData agentOfferData = mOfferDataList.get(currentOffers);
    mOfferBinding.textViewOfferMessage.setText(agentOfferData.getMessage());
    mOfferBinding.editTextNewSize.setText(String.valueOf(agentOfferData.getConsignment_size()));
  }

  private void closeSoftWindowKeyboard() {
    AppUtility.hideSoftKeyBoard(this, getCurrentFocus());
  }

  @Override
  public void onActionResult(int responseCode, String resultMessage, int action) {
    Log.d(TAG, "onActionResult() called with: responseCode = ["
        + responseCode
        + "], resultMessage = ["
        + resultMessage
        + "], action = ["
        + action
        + "]");

    AppUtility.showToast(this, resultMessage);

    int currentOffers = mOfferBinding.AgentOffersPages.getCurrentItem();
    //remove from original source
    mOfferDataList.remove(currentOffers);
    //remove from adapter
    mPagesAdapter.removeAtPosition(currentOffers);
    mPagesAdapter.notifyDataSetChanged();
    if (action == RestFields.ACTION_MODIFY) {
      closeAgentEditOffer();
    }
    if (mOfferDataList.size() == 0) {
      finish();
    }else{
      updateOfferTitle(0);
    }
  }

  private void updateOfferTitle(int position) {
    if (mOfferDataList != null) {
      AgentOfferData agentOfferData = mOfferDataList.get(position);
      mOfferBinding.textViewOfferTitle.setText(
          getString(R.string.title_agent_offer, agentOfferData.getJob_id()));
      int totalPage = mPagesAdapter.getCount();
      mOfferBinding.textViewPageCount.setText(String.valueOf((position + 1) + "/" + totalPage));
    }
  }

  public class OfferPagesAdapter extends FragmentStatePagerAdapter {

    private List<AgentOfferData> offersList;

    public OfferPagesAdapter(FragmentManager fm, List<AgentOfferData> offerDataList) {
      super(fm);
      offersList = new ArrayList<>(offerDataList.size());
      offersList.addAll(offerDataList);
    }

    public void removeAtPosition(int position) {
      if (offersList != null && offersList.size() > 0) {
        offersList.remove(position);
      }
    }

    @Override
    public Fragment getItem(int position) {
      AgentOfferData offerContentMassage = offersList.get(position);
      return AgentOfferFragment.getOfferFragment(offerContentMassage);
    }

    @Override
    public int getCount() {
      return offersList.size();
    }
  }

  public class OfferPageChangeHandler implements ViewPager.OnPageChangeListener {
    public OfferPageChangeHandler() {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
      updateOfferTitle(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
  }
}
