package com.android.unideal.questioner.view.fragments;

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
import android.widget.EditText;
import com.android.unideal.R;
import com.android.unideal.agent.adapter.CategoryAdapter;
import com.android.unideal.databinding.QuestionerBinding;
import com.android.unideal.enums.AppMode;
import com.android.unideal.questioner.view.DisclaimerActivity;
import com.android.unideal.questioner.viewmodel.QuestionerSignUpFragmentViewModel;
import com.android.unideal.rest.RestFields;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by ADMIN on 22-09-2016.
 */

public class QuestionerSignUpFragment extends Fragment
    implements QuestionerSignUpFragmentViewModel.QuestionerListener,
    RunTimePermissionManager.callbackRunTimePermission {
  public final static int REQ_PROFILE_IMAGE = 222;
  // request code for runtime permission
  public final static int REQ_PERMISSION_PROFILE_IMAGE = 329;
  private final static int REQ_DISCLAIMER = 341;
  private static final String TAG = "QuestionerSignUpFragmen";
  private QuestionerBinding mBinding;
  private QuestionerSignUpFragmentViewModel mViewModel;
  private List<String> genderList = new ArrayList<>();
  private int selectedYear;
  private ImagePicker imagePicker;
  private int month;
  private String filePath, monthString;
  ImagePicker.ImageListener imageListener = new ImagePicker.ImageListener() {
    @Override
    public void onImagePick(final int imageReq, final String path) {
      getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          if (imageReq == REQ_PROFILE_IMAGE) {
            filePath = path;
            Picasso picasso = Picasso.with(getActivity());
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
  private RunTimePermissionManager mRunTimePermission;
  private int day;

  public static QuestionerSignUpFragment getInstance() {
    return new QuestionerSignUpFragment();
  }

  public static boolean isEditTextContainEmail(EditText argEditText) {
    try {
      Pattern pattern = Pattern.compile(
          "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
      Matcher matcher = pattern.matcher(argEditText.getText());
      return matcher.matches();
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    imagePicker.onActivityResult(requestCode, resultCode, data);
    mRunTimePermission.onActivityResult(REQ_PERMISSION_PROFILE_IMAGE, requestCode, requestCode,
        data);
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
    mBinding = loadBinding(inflater, container);
    mViewModel = new QuestionerSignUpFragmentViewModel(getActivity(), this);
    mViewModel.initalizeViews();
    return mBinding.getRoot();
  }

  public QuestionerBinding loadBinding(LayoutInflater inflater, @Nullable ViewGroup container) {
    return DataBindingUtil.inflate(inflater, R.layout.fragment_questioner_sign_up, container,
        false);
  }

  @Override
  public void onInitialize() {
    genderList.add(getString(R.string.txt_male));
    genderList.add(getString(R.string.txt_female));
    CategoryAdapter<String> spinnerAdapter = new CategoryAdapter<>(getActivity(), genderList);
    mBinding.genderSpinner.setAdapter(spinnerAdapter);
    //set profile photo background
    mBinding.profilePhoto.setBackgroundResource(R.drawable.ic_camera_questioner);

    // Get current date by calender
    final Calendar c = Calendar.getInstance();
    selectedYear = c.get(Calendar.YEAR);
    month = c.get(Calendar.MONTH);
    day = c.get(Calendar.DAY_OF_MONTH);
    configureRunTimePermission();
    setTermsConditionClick();
    mBinding.signUpButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        signUpUser();
      }
    });

    detectFieldChange();

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

  private void setTermsConditionClick() {
    mBinding.termsAndCondition.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mBinding.termsAndCondition.isChecked()) {
          Intent dcmIntent =
              DisclaimerActivity.getDialogActivity(getActivity(), AppMode.QUESTIONER, TNC_TYPE);
          startActivityForResult(dcmIntent, REQ_DISCLAIMER);
        } else {
          mBinding.termsAndCondition.setChecked(false);
        }
      }
    });
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
    String refralcode = mBinding.editTextReferral.getText().toString().trim();
    String number = mBinding.number.getText().toString().trim();
    if (!checkEmptyFields(fullName, email, password, conPassword)) {
      mViewModel.setUpSignUpUser(fullName, email, password, gender, refralcode, number, filePath);
    }
  }

  private boolean checkEmptyFields(String fullName, String email, String password,
      String conPassword) {
    if (TextUtils.isEmpty(fullName)) {
      AppUtility.setEditTextColor(mBinding.fullName, R.color.colorRed);
      return true;
    } else if (TextUtils.isEmpty(email) || !(isEditTextContainEmail(mBinding.email))) {
      AppUtility.setEditTextColor(mBinding.email, R.color.colorRed);
      return true;
    } else if (TextUtils.isEmpty(password) || password.length() < 6) {
      AppUtility.setEditTextColor(mBinding.password, R.color.colorRed);
      return true;
    } else if (TextUtils.isEmpty(conPassword) || conPassword.length() < 6) {
      AppUtility.setEditTextColor(mBinding.confirmPassword, R.color.colorRed);
      return true;
    } else if (mBinding.number.getText().toString().length() < 8) {
      AppUtility.setEditTextColor(mBinding.number, R.color.colorRed);
      return true;
    } else if (!conPassword.equals(password)) {
      AppUtility.setEditTextColor(mBinding.password, R.color.colorRed);
      AppUtility.setEditTextColor(mBinding.confirmPassword, R.color.colorRed);
      return true;
    } else if (!mBinding.termsAndCondition.isChecked()) {
      DialogUtils.showDialog(getContext(), R.string.accept_terms_condition);
      return true;
    }
    //else if (TextUtils.isEmpty(filePath)) {
    //  Toast.makeText(getActivity().getApplicationContext(), "Please Choose Photo",
    //      Toast.LENGTH_SHORT).show();
    //  return true;
    //}
    return false;
  }

  public int getFieldColor(int colorId) {
    return ContextCompat.getColor(getActivity(), colorId);
  }

  public void detectFieldChange() {
    //Textwatcher whe user enter information into edittext then get default color
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
  public void deniedPermission(String deniedPermission, int code) {
    // if user denied we again force to open the rational dialog
  }

  @Override
  public void requestAllPermissionGranted(int requestCode) {
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
  public void onSignUpError(String message) {
    DialogUtils.showDialog(getContext(), message);
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
