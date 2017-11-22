package com.android.unideal.agent.view.fragment;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.agent.viewmodel.ReferCodeFragmentViewModel;
import com.android.unideal.data.UserDetail;
import com.android.unideal.databinding.ReferCodeBinding;
import com.android.unideal.flurry.FlurryManager;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.SessionManager;
import com.android.unideal.util.SocialSharingUtil;

/**
 * Created by MRUGESH on 10/8/2016.
 */

public class ReferCodeFragment extends Fragment
    implements ReferCodeFragmentViewModel.ReferCodeListener {
  private ReferCodeBinding mBinding;
  private ReferCodeFragmentViewModel mViewModel;
  private String referCode;
  private UserDetail mUserDetail;
  private SessionManager sessionManager;

  public static Fragment getInstance() {
    return new ReferCodeFragment();
  }

  private ReferCodeBinding loadBinding(LayoutInflater inflater, ViewGroup container) {
    return DataBindingUtil.inflate(inflater, R.layout.fragment_refer_code_agent, container, false);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mBinding = loadBinding(inflater, container);

    sessionManager = SessionManager.get(getContext());

    mUserDetail = sessionManager.getActiveUser();
    referCode = mUserDetail.getMyReferralCode();

    if (!TextUtils.isEmpty(referCode)) {
      mBinding.referCode.setText(referCode);

      mBinding.copyClipboard.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          //Flurry Session
          FlurryManager.shareReferralCode("copy", mUserDetail.getUserId());
          SocialSharingUtil.copyToClipboard(referCode, getContext());
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
          FlurryManager.shareReferralCode("whatsapp", mUserDetail.getUserId());
          SocialSharingUtil.shareWithWhatsApp(getShareContent(), getContext());
        }
      });
      mBinding.mail.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          FlurryManager.shareReferralCode("mail", mUserDetail.getUserId());
          SocialSharingUtil.shareWithMail(getShareContent(), getContext());
        }
      });
    } else {
      DialogUtils.showDialog(getContext(), R.string.title_app_name, R.string.no_refrcode_avalable,
          R.string.btn_ok, R.string.cancelled, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
            }
          });
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
