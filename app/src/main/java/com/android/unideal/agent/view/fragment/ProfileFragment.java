package com.android.unideal.agent.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.agent.view.AgentHomeActivity;
import com.android.unideal.agent.view.AgentProfileActivity;
import com.android.unideal.agent.viewmodel.ProfileFragmentViewModel;
import com.android.unideal.data.UserDetail;
import com.android.unideal.databinding.AgentProfileBinding;
import com.android.unideal.rest.response.Category;
import com.android.unideal.util.CategoryList;
import com.android.unideal.util.Consts;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.ImagePicker;
import com.android.unideal.util.RunTimePermissionManager;
import com.android.unideal.util.SessionManager;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADMIN on 07-10-2016.
 */

public class ProfileFragment extends Fragment
    implements ProfileFragmentViewModel.AgentProfileListener {
  // request code for image picker
  public final static int REQ_PROFILE_IMAGE = 999;
  // request code for runtime permission
  public final static int REQ_PERMISSION_PROFILE_IMAGE = 929;
  private static final int PICKFILE_RESULT_CODE = 1;

  private static final String TAG = "QuestionerProfileFrag";
  private ProfileFragmentViewModel mViewModel;
  private AgentProfileBinding mBinding;
  private List<String> genderList = new ArrayList<>();
  private RunTimePermissionManager mRunTimePermission;
  private ImagePicker imagePicker;
  private String filePath;
  private String expertise = "";
  private String genderType;
  private String uploadedFileName;
  // value 1 will determine profile photo is chosen , value 2 will determine upload file is chosen
  private ImagePicker.ImageListener imageListener = new ImagePicker.ImageListener() {
    @Override
    public void onImagePick(final int imageReq, final String path) {
      getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          if (imageReq == REQ_PROFILE_IMAGE) {
            filePath = path;
            Picasso picasso = Picasso.with(getActivity());
            picasso.load(new File(filePath))
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

  public static Fragment getInstance() {
    return new ProfileFragment();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == AgentProfileActivity.REQ_UPDATE_PROFILE_CODE) {
      if (resultCode == Activity.RESULT_OK) {
        mViewModel.getUserProfile();
        ((AgentHomeActivity) getActivity()).updateProfileInfo();
      }
    } else {
      imagePicker.onActivityResult(requestCode, resultCode, data);
      mRunTimePermission.onActivityResult(REQ_PERMISSION_PROFILE_IMAGE, requestCode, requestCode,
          data);
      if (requestCode == PICKFILE_RESULT_CODE) {
        if (resultCode == Activity.RESULT_OK) {
          Uri selectedFileURI = data.getData();
          File file = new File(selectedFileURI.getPath().toString());
          Log.d("", "File : " + file.getName());
          uploadedFileName = file.getName().toString();
        }
      }
    }
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_agent, container, false);
    mViewModel = new ProfileFragmentViewModel(getActivity(), this);
    genderList.add(getString(R.string.txt_male));
    genderList.add(getString(R.string.txt_female));
    //image picker willtake the path of your choosen photo
    imagePicker = new ImagePicker(this, imageListener);
    mBinding.editProfileButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivityForResult(AgentProfileActivity.startAgentProfileActivity(getActivity()),
            AgentProfileActivity.REQ_UPDATE_PROFILE_CODE);
      }
    });

    mViewModel.getUserProfile();
    return mBinding.getRoot();
  }

  public void setEditProfileInfo(final UserDetail data) {
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
    String languageMode = SessionManager.get(getActivity()).getLanguageMode();
    for (Category categoryList : CategoryList.parentCategoryList(getActivity())) {
      if (categoryList.getCategoryId() != null
          && categoryList.getCategoryId() == data.getUser_expertise()) {
        if (languageMode.equals(Consts.ENGLISH_MODE)) {
          expertise = categoryList.getCategoryName();
        } else if (languageMode.equals(Consts.CHINESE_MODE)) {
          expertise = categoryList.getCategoryNameCh();
        } else {
          expertise = categoryList.getCategoryName();
        }

        break;
      }
    }
    if (!TextUtils.isEmpty(expertise) && !expertise.equals(getString(R.string.category_default))) {
      mBinding.favCatLayout.setVisibility(View.VISIBLE);
      mBinding.favourableCategory.setText(expertise);
    } else {
      mBinding.favCatLayout.setVisibility(View.GONE);
    }
    if (!TextUtils.isEmpty(data.getBio())) {
      mBinding.bioLayout.setVisibility(View.VISIBLE);
      mBinding.bio.setText(data.getBio());
    } else {
      mBinding.bioLayout.setVisibility(View.GONE);
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
        genderType = getResources().getString(R.string.txt_male);
      } else if (data.getGender().equals(Consts.genderTypeFeMale)) {
        genderType = getResources().getString(R.string.txt_female);
      }
      mBinding.genderSpinner.setText(genderType);
    }

    if (!TextUtils.isEmpty(data.getProfilePic())) {
      Picasso.with(getContext())
          .load(data.getProfilePic())
          .placeholder(R.drawable.drw_show_img_progress)
          .into(mBinding.profilePhoto);
    }

    if (!TextUtils.isEmpty(data.getUser_doc())) {
      mBinding.documentLayout.setVisibility(View.VISIBLE);
      String fileName = data.getUser_doc()
          .substring(data.getUser_doc().lastIndexOf('/') + 1, data.getUser_doc().length());
      mBinding.fileName.setText(fileName);
      onClickFile(data);
    } else {
      mBinding.documentLayout.setVisibility(View.GONE);
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

  private void onClickFile(final UserDetail data) {
    mBinding.fileName.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.getUser_doc()));
        startActivity(browserIntent);
      }
    });
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    imagePicker.onSaveInstanceState(outState);
    Log.d(TAG, "onSaveInstanceState: " + outState.toString());
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    imagePicker.onRestoreInstanceState(savedInstanceState);
    super.onViewStateRestored(savedInstanceState);
  }

  /**
   * Handle the result of request for ask the permission
   */
  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    mRunTimePermission.onRequestPermissionsResult(REQ_PERMISSION_PROFILE_IMAGE, permissions,
        grantResults);
  }

  /**
   * Entry point of asking the run time permission
   */

  @Override
  public void onError(String message) {
    DialogUtils.showDialog(getContext(), message);
  }
}
