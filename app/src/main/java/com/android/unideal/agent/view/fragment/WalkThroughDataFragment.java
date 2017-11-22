package com.android.unideal.agent.view.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.data.WalkThroughData;
import com.android.unideal.databinding.WalkThroghItemBinding;

/**
 * Created by ADMIN on 13-01-2017.
 */

public class WalkThroughDataFragment extends Fragment {
  public static String KEY_WALKTHROUGH = "walkthrough";
  private WalkThroghItemBinding walkThroghItemBinding;
  private WalkThroughData walkThroughData;

  public static WalkThroughDataFragment newInstance(WalkThroughData walkThroughData) {
    WalkThroughDataFragment walkThroughDataFragment = new WalkThroughDataFragment();
    Bundle bundle = new Bundle();
    bundle.putParcelable(KEY_WALKTHROUGH, walkThroughData);
    walkThroughDataFragment.setArguments(bundle);
    return walkThroughDataFragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    walkThroughData = getArguments().getParcelable(KEY_WALKTHROUGH);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    walkThroghItemBinding =
        DataBindingUtil.inflate(inflater, R.layout.fragment_walkthrough, container, false);
    walkThroghItemBinding.walkThroughImage.setImageResource(
        walkThroughData.getDrawableResourceId());
    walkThroghItemBinding.walthThroughText.setText(walkThroughData.getStringResorId());

    return walkThroghItemBinding.getRoot();
  }
}

