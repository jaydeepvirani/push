package com.android.unideal.questioner.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.android.unideal.R;
import com.android.unideal.agent.adapter.CategoryAdapter;
import com.android.unideal.data.UserDetail;
import com.android.unideal.databinding.ProfileQuestionerBinding;
import com.android.unideal.questioner.viewmodel.QuestionerProfileViewModel;
import com.android.unideal.util.AppUtility;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.ImagePicker;
import com.android.unideal.util.RunTimePermissionManager;
import com.android.unideal.util.SessionManager;
import com.android.unideal.util.SimpleTextWatcher;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by ADMIN on 20-10-2016.
 */

public class QuestionerProfileActivity extends AppCompatActivity
    implements QuestionerProfileViewModel.QuestionerProfileListener,
    RunTimePermissionManager.callbackRunTimePermission {
  public final static int REQ_PROFILE_IMAGE_QUESTIONER = 959;
  // request code for runtime permission
  public final static int REQ_PERMISSION_PROFILE_IMAGE_QUESTIONER = 809;
  public final static int REQ_UPDATE_PROFILT = 222;
  private static final String TAG = QuestionerProfileActivity.class.getName();
  private int genderType;
  private RunTimePermissionManager mRunTimePermission;
  private ProfileQuestionerBinding mBinding;
  private QuestionerProfileViewModel mViewModel;
  private List<String> genderArrayList = new ArrayList<>();
  private ImagePicker picker;
  private String filePathQuestioner;
  ImagePicker.ImageListener imageListener = new ImagePicker.ImageListener() {
    @Override public void onImagePick(final int imageReq, final String path) {
      runOnUiThread(new Runnable() {
        @Override public void run() {
          if (imageReq == REQ_PROFILE_IMAGE_QUESTIONER) {
            filePathQuestioner = path;
            Picasso picasso = Picasso.with(QuestionerProfileActivity.this);
            picasso.load(new File(filePathQuestioner))
                .placeholder(R.drawable.drw_show_img_progress)
                .error(R.drawable.drw_show_img_progress)
                .into(mBinding.profilePhoto);
          }
        }
      });
    }

    @Override public void onError(String s) {
      Log.d(TAG, "onError: ");
    }
  };

  public static Intent startQuestionerProfileActivity(Activity activity) {
    return new Intent(activity, QuestionerProfileActivity.class);
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mBinding = DataBindingUtil.setContentView(this, R.layout.acivity_profile_questioner);
    mViewModel = new QuestionerProfileViewModel(this, this);
    UserDetail userDetail = SessionManager.get(this).getActiveUser();
    if (userDetail.getFromSocialNetwork() == 1 && TextUtils.isEmpty(userDetail.getEmailAddress())) {
      mBinding.email.setEnabled(true);
    }
    setProfileInfo();
    genderArrayList.add(getString(R.string.txt_male));
    genderArrayList.add(getString(R.string.txt_female));
    CategoryAdapter<String> spinnerAdapter =
        new CategoryAdapter<>(QuestionerProfileActivity.this, genderArrayList);
    mBinding.genderSpinner.setAdapter(spinnerAdapter);
    //set profile photo background
    configureRunTimePermission();

    mBinding.btnBack.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        finish();
      }
    });

    detectFieldChange();

    picker = new ImagePicker(this, imageListener, true);

    mBinding.profilePhoto.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        permissionRequestList();
      }
    });

    mBinding.submitButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        submitProfileInfo();
      }
    });
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    picker.onSaveInstanceState(outState);
    Log.d(TAG, "onSaveInstanceState: " + outState.toString());
    super.onSaveInstanceState(outState);
  }

  @Override protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  private void configureRunTimePermission() {
    mRunTimePermission = new RunTimePermissionManager(this);
    mRunTimePermission.registerCallback(this);
  }

  /**
   * Handle the result of request for ask the permission
   */
  @Override public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    mRunTimePermission.onRequestPermissionsResult(REQ_PERMISSION_PROFILE_IMAGE_QUESTIONER,
        permissions, grantResults);
  }

  private void setProfileInfo() {
    UserDetail userDetail = SessionManager.get(this).getActiveUser();
    if (!TextUtils.isEmpty(userDetail.getName())) {
      mBinding.fullName.setText(userDetail.getName());
    } else {
      mBinding.fullName.setText("");
    }
    if (!TextUtils.isEmpty(userDetail.getEmailAddress())) {
      mBinding.email.setText(userDetail.getEmailAddress());
    } else {
      mBinding.email.setText("");
    }
    if (!TextUtils.isEmpty(userDetail.getEmailAddress())) {
      mBinding.email.setText(userDetail.getEmailAddress());
    } else {
      mBinding.email.setText("");
    }

    if (!TextUtils.isEmpty(userDetail.getPhoneNumber())) {
      mBinding.number.setText(userDetail.getPhoneNumber());
    } else {
      mBinding.number.setText("");
    }
    if (!TextUtils.isEmpty(userDetail.getProfilePic())) {
      Picasso.with(this)
          .load(userDetail.getProfilePic())
          .placeholder(R.drawable.drw_show_img_progress)
          .into(mBinding.profilePhoto);
    }
  }

  /**
   * Entry point of asking the run time permission
   */

  private void permissionRequestList() {
    HashMap<String, String> permissionsMap = new HashMap<>();
    permissionsMap.put(Manifest.permission.WRITE_EXTERNAL_STORAGE,
        getResources().getString(R.string.message_storage_permission));
    mRunTimePermission.buildPermissionList(permissionsMap, REQ_PERMISSION_PROFILE_IMAGE_QUESTIONER);
  }

  private void submitProfileInfo() {
    String fullName = mBinding.fullName.getText().toString();
    String email = mBinding.email.getText().toString();
    String oldPassword = mBinding.oldPassword.getText().toString();
    String newPassword = mBinding.NewPassword.getText().toString();
    String confirmPassword = mBinding.ConfirmPassword.getText().toString();
    String number = mBinding.number.getText().toString();
    String gender = mBinding.genderSpinner.getSelectedItem().toString();
    if (gender.equals(getResources().getString(R.string.txt_male))) {
      genderType = 1;
    } else if (gender.equals(getResources().getString(R.string.txt_female))) {
      genderType = 2;
    } else {
      genderType = 1;
    }
    if (!newPassword.equals(confirmPassword)) {
      DialogUtils.showDialog(this, R.string.alert_password_match);
    } else {
      mViewModel.updateProfile(fullName, email, oldPassword, newPassword, number,
          filePathQuestioner, genderType);
    }
  }

  public void detectFieldChange() {

    mBinding.fullName.addTextChangedListener(new SimpleTextWatcher() {
      @Override public void onTextChanged(String newValue) {
        AppUtility.setEditTextColor(mBinding.fullName, R.color.colorGallery);
      }
    });

    mBinding.email.addTextChangedListener(new SimpleTextWatcher() {
      @Override public void onTextChanged(String newValue) {
        AppUtility.setEditTextColor(mBinding.email, R.color.colorGallery);
      }
    });

    mBinding.number.addTextChangedListener(new SimpleTextWatcher() {
      @Override public void onTextChanged(String newValue) {
        AppUtility.setEditTextColor(mBinding.number, R.color.colorGallery);
      }
    });
  }

  @Override public void showRationalDialog(String message, final int code) {
    DialogUtils.showDialog(this, R.string.title_app_name, message, R.string.btn_ok,
        R.string.btn_cancel, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            mRunTimePermission.askRunTimePermissions(code);
          }
        });
  }

  @Override public void deniedPermission(String deniedPermission, int code) {
    // if user denied we again force to open the rational dialog
  }

  @Override public void requestAllPermissionGranted(int requestCode) {
    picker.openDialog(REQ_PROFILE_IMAGE_QUESTIONER);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    picker.onActivityResult(requestCode, resultCode, data);
    mRunTimePermission.onActivityResult(REQ_PERMISSION_PROFILE_IMAGE_QUESTIONER, requestCode,
        requestCode, data);
  }

  @Override public void updateSuccessFully(String message) {
    DialogUtils.showToast(this, message);
    setResult(RESULT_OK);
    finish();
  }

  @Override public void onFailled(String message) {
    DialogUtils.showDialog(this, R.string.title_app_name, message, R.string.btn_ok,
        R.string.btn_cancel, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });
  }
}
