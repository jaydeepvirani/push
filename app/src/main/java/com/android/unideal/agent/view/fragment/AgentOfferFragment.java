package com.android.unideal.agent.view.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.data.AgentOfferData;
import com.android.unideal.databinding.OfferContentBinding;

/**
 * Created by bhavdip on 12/28/16.
 */

public class AgentOfferFragment extends Fragment {

  public static final String KEY_OFFER_MSG = "offerMsg";
  private AgentOfferData mAgentOfferData;
  private OfferContentBinding contentBinding;

  public static Fragment getOfferFragment(AgentOfferData offerData) {
    AgentOfferFragment fragment = new AgentOfferFragment();
    Bundle argumentBundle = new Bundle();
    argumentBundle.putParcelable(KEY_OFFER_MSG, offerData);
    fragment.setArguments(argumentBundle);
    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments().containsKey(KEY_OFFER_MSG)) {
      mAgentOfferData = getArguments().getParcelable(KEY_OFFER_MSG);
    }
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    contentBinding = loadOfferBinding(inflater,container);
    return contentBinding.getRoot();
  }

  private OfferContentBinding loadOfferBinding(LayoutInflater inflater, @Nullable ViewGroup container){
    return DataBindingUtil.inflate(inflater, R.layout.fragment_agent_offer, container, false);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (mAgentOfferData != null) {
      contentBinding.textViewOfferMessage.setText(mAgentOfferData.getMessage());
    }
  }
}
