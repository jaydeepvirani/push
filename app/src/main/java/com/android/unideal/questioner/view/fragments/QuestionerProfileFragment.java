package com.android.unideal.questioner.view.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.data.UserDetail;
import com.android.unideal.databinding.QuestionerProfileBinding;
import com.android.unideal.questioner.view.QuestionerHomeActivity;
import com.android.unideal.questioner.view.QuestionerProfileActivity;
import com.android.unideal.questioner.viewmodel.ProfileFragmentViewModel;
import com.android.unideal.util.Consts;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.ImagePicker;
import com.android.unideal.util.RunTimePermissionManager;
import com.squareup.picasso.Picasso;
import java.io.File;

/**
 * Created by ADMIN on 11-10-2016.
 */

public class QuestionerProfileFragment extends Fragment
    implements ProfileFragmentViewModel.ProfileFragmentListener {
  private final static int REQ_PROFILE_IMAGE_QUESTIONER = 989;
  // request code for runtime permission
  private final static int REQ_PERMISSION_PROFILE_IMAGE_QUESTIONER = 829;
  private static final String TAG = "QuestionerFragment";
  private QuestionerProfileBinding mBinding;
  private ProfileFragmentViewModel mViewModel;
  private ImagePicker picker;
  private String genderType;
  private String filePathQuestioner;
  private ImagePicker.ImageListener imageListener = new ImagePicker.ImageListener() {
    @Override
    public void onImagePick(final int imageReq, final String path) {
      getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          if (imageReq == REQ_PROFILE_IMAGE_QUESTIONER) {
            filePathQuestioner = path;
            Picasso picasso = Picasso.with(getActivity());
            picasso.load(new File(filePathQuestioner))
                .placeholder(R.drawable.drw_show_img_progress)
                .into(mBinding.profilePhoto);
          }
        }
      });
    }

    @Override
    public void onError(String s) {
      Log.d(TAG, "onError: ");
    }
  };
  private RunTimePermissionManager mRunTimePermission;

  public static Fragment getInstance() {
    return new QuestionerProfileFragment();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mBinding =
        DataBindingUtil.inflate(inflater, R.layout.fragment_profile_questioner, container, false);
    mViewModel = new ProfileFragmentViewModel(getActivity(), this);
    //set profile photo background
    mBinding.editProfileButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivityForResult(
            QuestionerProfileActivity.startQuestionerProfileActivity(getActivity()),
            QuestionerProfileActivity.REQ_UPDATE_PROFILT);
      }
    });
    mViewModel.getUserProfile();
    picker = new ImagePicker(this, imageListener);
    return mBinding.getRoot();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    picker.onSaveInstanceState(outState);
    Log.d(TAG, "onSaveInstanceState: " + outState.toString());
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    picker.onRestoreInstanceState(savedInstanceState);
    super.onViewStateRestored(savedInstanceState);
  }

  /**
   * Handle the result of request for ask the permission
   */
  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    mRunTimePermission.onRequestPermissionsResult(REQ_PERMISSION_PROFILE_IMAGE_QUESTIONER,
        permissions, grantResults);
  }

  @Override
  public void onError(String message) {
    DialogUtils.showDialog(getContext(), R.string.title_app_name, message, R.string.btn_ok,
        R.string.btn_cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });
  }

  public void setProfileInfo(UserDetail data) {
    if (!TextUtils.isEmpty(data.getName())) {
      mBinding.fullNameLayout.setVisibility(View.VISIBLE);
      mBinding.fullName.setText(data.getName());
    } else {
      mBinding.fullNameLayout.setVisibility(View.GONE);
    }
    if (!TextUtils.isEmpty(data.getEmailAddress())) {
      mBinding.emailLayout.setVisibility(View.VISIBLE);
      mBinding.email.setText(data.getEmailAddress());
    } else {
      mBinding.emailLayout.setVisibility(View.GONE);
    }
    if (!TextUtils.isEmpty(data.getPhoneNumber())) {
      mBinding.numberLayout.setVisibility(View.VISIBLE);
      mBinding.number.setText(data.getPhoneNumber());
    } else {
      mBinding.numberLayout.setVisibility(View.GONE);
    }
    if (!data.getGender().equals("0")) {
      mBinding.genderLayout.setVisibility(View.VISIBLE);
      if (data.getGender().equals(Consts.genderTypeMale)) {
        genderType = getString(R.string.txt_male);
      } else if (data.getGender().equals(Consts.genderTypeFeMale)) {
        genderType = getString(R.string.txt_female);
      } else {
        genderType = getString(R.string.txt_male);
      }
      mBinding.genderType.setText(genderType);
    }

    if (!TextUtils.isEmpty(data.getProfilePic())) {
      Picasso.with(getActivity())
          .load(data.getProfilePic())
          .placeholder(R.drawable.drw_show_img_progress)
          .into(mBinding.profilePhoto);
    }
  }

  @Override
  public void showProgressBar() {
    mBinding.progressBar.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideProgressBar() {
    mBinding.progressBar.setVisibility(View.GONE);
  }

  /**
   * Entry point of asking the run time permission
   */

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == QuestionerProfileActivity.REQ_UPDATE_PROFILT) {
      mViewModel.getUserProfile();
      ((QuestionerHomeActivity) getActivity()).updateProfileInfo();
    } else {
      picker.onActivityResult(requestCode, resultCode, data);
      mRunTimePermission.onActivityResult(REQ_PERMISSION_PROFILE_IMAGE_QUESTIONER, requestCode,
          requestCode, data);
    }
  }
}
