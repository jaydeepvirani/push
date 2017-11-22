package com.android.unideal.questioner.view.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.data.UserDetail;
import com.android.unideal.databinding.ReferCodeQuestionerBinding;
import com.android.unideal.flurry.FlurryManager;
import com.android.unideal.questioner.viewmodel.ReferCodeFragmentViewModel;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.SessionManager;
import com.android.unideal.util.SocialSharingUtil;

/**
 * Created by ADMIN on 11-10-2016.
 */

public class ReferCodeFragment extends Fragment
    implements ReferCodeFragmentViewModel.ReferCodeListener {
  private ReferCodeQuestionerBinding mBinding;
  private ReferCodeFragmentViewModel mViewModel;
  private String referCode;
  private UserDetail mUserDetail;
  private SessionManager sessionManager;

  public static Fragment getInstance() {
    return new ReferCodeFragment();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_refer_code_questioner, container,
        false);
    sessionManager = SessionManager.get(getContext());
    mUserDetail = sessionManager.getActiveUser();
    referCode = mUserDetail.getMyReferralCode();

    if (!TextUtils.isEmpty(referCode)) {
      mBinding.referCode.setText(referCode);

      mBinding.copyClipboard.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          SocialSharingUtil.copyToClipboard(referCode, getContext());
          //Flurry Session
          FlurryManager.shareReferralCode("copy", mUserDetail.getUserId());
        }
      });
      mBinding.message.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          //Flurry Session
          FlurryManager.shareReferralCode("message", mUserDetail.getUserId());
          SocialSharingUtil.shareWithMessage(getShareContent(), getContext());
        }
      });

      mBinding.shareButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          SocialSharingUtil.shareWithAllApp(getShareContent(), getContext());
        }
      });
      mBinding.whatsApp.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          //Flurry Session
          FlurryManager.shareReferralCode("whatsapp", mUserDetail.getUserId());
          SocialSharingUtil.shareWithWhatsApp(getShareContent(), getContext());
        }
      });
      mBinding.mail.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          //Flurry Session
          FlurryManager.shareReferralCode("mail", mUserDetail.getUserId());
          SocialSharingUtil.shareWithMail(getShareContent(), getContext());
        }
      });
    } else {
      DialogUtils.showDialog(getContext(), R.string.no_refrcode_avalable);
    }
    mViewModel = new ReferCodeFragmentViewModel(getActivity(), this);
    return mBinding.getRoot();
  }

  private String getShareContent() {
    String googleLink = sessionManager.getPlayStoreLink();
    String appleLink = sessionManager.getAppleStoreLink();
    return String.format(getString(R.string.refer_code_share_text), referCode, googleLink,
        appleLink);
  }
}
