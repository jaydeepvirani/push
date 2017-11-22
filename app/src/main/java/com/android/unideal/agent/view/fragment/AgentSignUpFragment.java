package com.android.unideal.agent.view.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.agent.adapter.CategoryAdapter;
import com.android.unideal.agent.viewmodel.AgentSignUpFragmentViewModel;
import com.android.unideal.databinding.AgentBinding;
import com.android.unideal.enums.AppMode;
import com.android.unideal.questioner.view.DisclaimerActivity;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.response.GenericResponse;
import com.android.unideal.util.AppUtility;
import com.android.unideal.util.Consts;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.ImagePicker;
import com.android.unideal.util.RunTimePermissionManager;
import com.android.unideal.util.SimpleTextWatcher;
import com.android.unideal.util.StorageManager;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by ADMIN on 22-09-2016.
 */

public class AgentSignUpFragment extends Fragment
    implements AgentSignUpFragmentViewModel.AgentListener,
    RunTimePermissionManager.callbackRunTimePermission {
  // request code for image picker
  public final static int REQ_PROFILE_IMAGE = 111;
  // request code for runtime permission
  public final static int REQ_PERMISSION_PROFILE_IMAGE = 229;
  private static final String TAG = "AgentSignUpFragment";
  private final static int REQ_DISCLAIMER = 345;
  private AgentBinding mBinding;
  private RunTimePermissionManager mRunTimePermission;
  private AgentSignUpFragmentViewModel mViewModel;
  private List<String> genderList = new ArrayList<>();
  private ImagePicker imagePicker;
  private String filePath;
  ImagePicker.ImageListener imageListener = new ImagePicker.ImageListener() {
    @Override
    public void onImagePick(final int imageReq, final String path) {
      getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          if (imageReq == REQ_PROFILE_IMAGE) {
            filePath = path;
            Picasso picasso = Picasso.with(getActivity());
            picasso.setLoggingEnabled(true);
            picasso.load(new File(filePath)).into(mBinding.profilePhoto);
          }
        }
      });
    }

    @Override
    public void onError(String s) {
      Log.d(TAG, "onError: ");
    }
  };
  private String TNC_TYPE = "userTnc";

  public static AgentSignUpFragment getInstance() {
    return new AgentSignUpFragment();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    imagePicker.onActivityResult(requestCode, resultCode, data);
    mRunTimePermission.onActivityResult(REQ_PERMISSION_PROFILE_IMAGE, requestCode, requestCode,
        data);
    //temrs  and accept by user on acceptclick
    if (requestCode == REQ_DISCLAIMER) {
      if (resultCode == Activity.RESULT_OK) {
        mBinding.termsAndCondition.setChecked(true);
      } else {
        mBinding.termsAndCondition.setChecked(false);
      }
    }
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_agent_sign_up, container, false);
    mViewModel = new AgentSignUpFragmentViewModel(getActivity(), this);
    mViewModel.initalizeViews();
    return mBinding.getRoot();
  }

  @Override
  public void onInitialize() {
    genderList.add(getString(R.string.txt_male));
    genderList.add(getString(R.string.txt_female));
    //set profile photo background
    mBinding.profilePhoto.setBackgroundResource(R.drawable.ic_camera_agent);
    CategoryAdapter<String> spinnerAdapter = new CategoryAdapter<>(getActivity(), genderList);
    mBinding.genderSpinner.setAdapter(spinnerAdapter);
    configureRunTimePermission();
    detectFieldChange();

    // Get current date by calender
    mBinding.termsAndCondition.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mBinding.termsAndCondition.isChecked()) {
          Intent dcmIntent =
              DisclaimerActivity.getDialogActivity(getActivity(), AppMode.AGENT, TNC_TYPE);
          startActivityForResult(dcmIntent, REQ_DISCLAIMER);
        } else {
          mBinding.termsAndCondition.setChecked(false);
        }
      }
    });

    mBinding.signUpButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        signUpUser();
      }
    });
    imagePicker = new ImagePicker(this, imageListener);

    mBinding.profilePhoto.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        permissionRequestList();
      }
    });

    RxTextView.afterTextChangeEvents(mBinding.editTextReferral)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<TextViewAfterTextChangeEvent>() {
          @Override
          public void call(TextViewAfterTextChangeEvent textViewAfterTextChangeEvent) {
            if (textViewAfterTextChangeEvent.editable().length() >= Consts.MIN_REFERRAL_LENGTH) {
              mBinding.btnVerify.setVisibility(View.VISIBLE);
            } else {
              mBinding.btnVerify.setVisibility(View.GONE);
            }
          }
        });

    RxView.clicks(mBinding.btnVerify)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<Void>() {
          @Override
          public void call(Void aVoid) {
            //hide soft keyboard
            AppUtility.hideSoftKeyBoard(getActivity(), getActivity().getCurrentFocus());
            mViewModel.applyReferralCode(mBinding.editTextReferral.getText().toString().trim());
          }
        });
    RxView.clicks(mBinding.btnRemove)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<Void>() {
          @Override
          public void call(Void aVoid) {
            //hide soft keyboard
            AppUtility.hideSoftKeyBoard(getActivity(), getActivity().getCurrentFocus());
            onRemovePromotionalCode();
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

  private void configureRunTimePermission() {
    mRunTimePermission = new RunTimePermissionManager(getActivity());
    mRunTimePermission.registerCallback(this);
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

  private void permissionRequestList() {
    HashMap<String, String> permissionsMap = new HashMap<>();
    permissionsMap.put(Manifest.permission.WRITE_EXTERNAL_STORAGE,
        getResources().getString(R.string.message_storage_permission));
    mRunTimePermission.buildPermissionList(permissionsMap, REQ_PERMISSION_PROFILE_IMAGE);
  }

  private void signUpUser() {
    String fullName = mBinding.fullName.getText().toString().trim();
    String email = mBinding.email.getText().toString().trim();
    String password = mBinding.password.getText().toString();
    String conPassword = mBinding.confirmPassword.getText().toString();
    int gender =
        mBinding.genderSpinner.getSelectedItemPosition() == 0 ? RestFields.MALE : RestFields.FEMALE;
    String referCode = mBinding.editTextReferral.getText().toString().trim();
    String number = mBinding.number.getText().toString().trim();
    if (!checkEmptyFields(fullName, email, password, conPassword)) {
      mViewModel.setSignUpUser(fullName, email, password, gender, referCode, number, filePath);
    }
  }

  private boolean checkEmptyFields(String fullName, String email, String password,
      String conPassword) {
    //user can enter signup check null value of signup form
    if (TextUtils.isEmpty(fullName)) {
      AppUtility.setEditTextColor(mBinding.fullName, R.color.colorRed);
      return true;
    } else if (!AppUtility.isValidEmail(email)) {
      AppUtility.setEditTextColor(mBinding.email, R.color.colorRed);
      return true;
    } else if (TextUtils.isEmpty(password) || password.length() < 6) {
      AppUtility.setEditTextColor(mBinding.password, R.color.colorRed);
      return true;
    } else if (mBinding.number.getText().toString().length() < 8) {
      AppUtility.setEditTextColor(mBinding.number, R.color.colorRed);
      return true;
    } else if (TextUtils.isEmpty(conPassword) || conPassword.length() < 6) {
      AppUtility.setEditTextColor(mBinding.confirmPassword, R.color.colorRed);
      return true;
    } else if (!conPassword.equals(password)) {
      AppUtility.setEditTextColor(mBinding.password, R.color.colorRed);
      AppUtility.setEditTextColor(mBinding.confirmPassword, R.color.colorRed);
      return true;
    } else if (!mBinding.termsAndCondition.isChecked()) {
      DialogUtils.showDialog(getContext(), R.string.accept_terms_condition);
      return true;
    }
    return false;
  }

  private void detectFieldChange() {
    //user can enter field can defualt color using textatcher
    mBinding.fullName.addTextChangedListener(new SimpleTextWatcher() {
      @Override
      public void onTextChanged(String newValue) {
        AppUtility.setEditTextColor(mBinding.fullName, R.color.colorGallery);
      }
    });
    mBinding.email.addTextChangedListener(new SimpleTextWatcher() {
      @Override
      public void onTextChanged(String newValue) {
        AppUtility.setEditTextColor(mBinding.email, R.color.colorGallery);
      }
    });

    mBinding.password.addTextChangedListener(new SimpleTextWatcher() {
      @Override
      public void onTextChanged(String newValue) {
        AppUtility.setEditTextColor(mBinding.password, R.color.colorGallery);
      }
    });
    mBinding.number.addTextChangedListener(new SimpleTextWatcher() {
      @Override
      public void onTextChanged(String newValue) {
        AppUtility.setEditTextColor(mBinding.number, R.color.colorGallery);
      }
    });
  }

  @Override
  public void showRationalDialog(String message, final int code) {
    DialogUtils.showDialog(getContext(), R.string.title_app_name, message, R.string.btn_ok,
        R.string.btn_cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            mRunTimePermission.askRunTimePermissions(code);
          }
        });
  }

  @Override
  public void deniedPermission(String deniedPermission, final int code) {
    // if user denied we again force to open the rational dialog
  }

  @Override
  public void requestAllPermissionGranted(int requestCode) {
    //check phot image path is null
    StorageManager.verifyAppRootDir();
    StorageManager.verifyImagePath();
    imagePicker.openDialog(REQ_PROFILE_IMAGE);
  }

  @Override
  public void onSignUpSuccessFully(String message) {
    DialogUtils.showDialog(getContext(), message, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        getActivity().finish();
      }
    });
  }

  @Override
  public void onSignUpError(GenericResponse response) {
    if (response.getStatus_code() == (RestFields.RESEND_STATUS_CODE)) {
      DialogUtils.showDialog(getContext(), R.string.title_app_name, response.getMessage(),
          R.string.btn_ok, R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
              getActivity().finish();
            }
          });
    } else {
      DialogUtils.showDialog(getContext(), R.string.title_app_name, response.getMessage(),
          R.string.btn_ok, R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
            }
          });
    }
  }

  @Override
  public void onSignUpException(String message) {
    DialogUtils.showDialog(getContext(), R.string.title_app_name, message, R.string.btn_ok,
        R.string.btn_cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });
  }

  @Override
  public void onPromotionalCode(String message) {
    //disable edit text
    mBinding.editTextReferral.setEnabled(false);
    //Hide verify button
    mBinding.btnVerify.setVisibility(View.GONE);
    //show remove promo code button
    mBinding.btnRemove.setVisibility(View.VISIBLE);
    // if message is available then show it
    if (!TextUtils.isEmpty(message)) {
      mBinding.editTextMessage.setText(message);
    }
    mBinding.editTextMessage.setTextColor(
        ContextCompat.getColor(getActivity(), R.color.colorPersianGreen));
    mBinding.editTextMessage.setVisibility(View.VISIBLE);
  }

  @Override
  public void onRemovePromotionalCode() {
    //enable edit text
    mBinding.editTextReferral.setEnabled(true);
    //hide both button
    mBinding.btnVerify.setVisibility(View.GONE);
    mBinding.btnRemove.setVisibility(View.GONE);
    //clear edit text and message
    mBinding.editTextMessage.setVisibility(View.GONE);
    mBinding.editTextReferral.getText().clear();
    mBinding.editTextReferral.getBackground()
        .mutate()
        .setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorSantasGray),
            PorterDuff.Mode.SRC_ATOP);
  }

  @Override
  public void failPromotionalCode(String errorMessage) {
    //Enable edit text
    mBinding.editTextReferral.setEnabled(false);
    // hide verify button
    mBinding.btnVerify.setVisibility(View.GONE);
    mBinding.btnRemove.setVisibility(View.VISIBLE);
    // if error message is present then show it
    if (!TextUtils.isEmpty(errorMessage)) {
      mBinding.editTextMessage.setText(errorMessage);
    }
    mBinding.editTextMessage.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorRed));
    mBinding.editTextReferral.getBackground()
        .mutate()
        .setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorRed),
            PorterDuff.Mode.SRC_ATOP);
    mBinding.editTextMessage.setVisibility(View.VISIBLE);
  }
}
