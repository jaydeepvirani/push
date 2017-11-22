package com.android.unideal.agent.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import com.android.unideal.R;
import com.android.unideal.agent.adapter.CategoryAdapter;
import com.android.unideal.agent.viewmodel.AgentProfileViewModel;
import com.android.unideal.data.UserDetail;
import com.android.unideal.databinding.ProfileAgentBinding;
import com.android.unideal.rest.response.Category;
import com.android.unideal.util.AppUtility;
import com.android.unideal.util.CategoryList;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.FileUtils;
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

public class AgentProfileActivity extends AppCompatActivity
    implements AgentProfileViewModel.AgentProfileListener,
    RunTimePermissionManager.callbackRunTimePermission {
  public final static int REQ_PROFILE_IMAGE = 569;
  public final static int REQ_PERMISSION_PROFILE_IMAGE_QUESTIONER = 509;
  public final static int REQ_UPDATE_PROFILE_CODE = 111;
  private static final String TAG = "AgentProfileActivity";
  // request code for runtime permission
  private static final int PICK_FILE_RESULT_CODE = 1;
  private static String AGENT_OFFER_PRICE = "agent_offer";
  private ProfileAgentBinding mBinding;
  private AgentProfileViewModel mViewModel;
  private List<String> genderArrayList = new ArrayList<>();
  private ImagePicker picker;
  private int genderType, categoryType;
  private String profileImagePath;
  ImagePicker.ImageListener imageListener = new ImagePicker.ImageListener() {
    @Override public void onImagePick(final int imageReq, final String path) {
      runOnUiThread(new Runnable() {
        @Override public void run() {
          if (imageReq == REQ_PROFILE_IMAGE) {
            profileImagePath = path;
            Picasso picasso = Picasso.with(AgentProfileActivity.this);
            picasso.load(new File(profileImagePath))
                .placeholder(R.drawable.drw_show_img_progress)
                .into(mBinding.profilePhoto);
          }
        }
      });
    }

    @Override public void onError(String s) {
      Log.d(TAG, "onError: ");
    }
  };
  private int chooseFlag;
  private String uploadFilePath;
  private RunTimePermissionManager mRunTimePermission;

  public static Intent startAgentProfileActivity(Activity activity) {
    Intent mIntent = new Intent(activity, AgentProfileActivity.class);
    return mIntent;
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile_agent);
    mViewModel = new AgentProfileViewModel(this, this);
    UserDetail userDetail = SessionManager.get(this).getActiveUser();
    if (userDetail.getFromSocialNetwork() == 1 && TextUtils.isEmpty(userDetail.getEmailAddress())) {
      mBinding.email.setEnabled(true);
    }
    setProfileInfo();
    genderArrayList.add(getResources().getString(R.string.txt_male));
    genderArrayList.add(getResources().getString(R.string.txt_female));
    CategoryAdapter<String> spinnerAdapter = new CategoryAdapter<>(this, genderArrayList);
    mBinding.genderSpinner.setAdapter(spinnerAdapter);
    //set profile photo background

    // Get current date by calender

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
        //if (editCount == 1) {
        chooseFlag = 1;
        permissionRequestList();
        //}
      }
    });

    mBinding.submitButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        onClickUpdteProfile();
      }
    });

    mBinding.layoutDocument.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        chooseFlag = 2;
        permissionRequestList();
        //showFileChooser();
      }
    });
    mBinding.bio.setOnTouchListener(new View.OnTouchListener() {

      public boolean onTouch(View view, MotionEvent event) {
        // TODO Auto-generated method stub
        if (view.getId() == R.id.bio) {
          view.getParent().requestDisallowInterceptTouchEvent(true);
          switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
              view.getParent().requestDisallowInterceptTouchEvent(false);
              break;
          }
        }
        return false;
      }
    });
  }

  private void showFileChooser() {
    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    intent.setType("*/*");
    intent.addCategory(Intent.CATEGORY_OPENABLE);

    try {
      startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"),
          PICK_FILE_RESULT_CODE);
    } catch (android.content.ActivityNotFoundException ex) {
      Toast.makeText(getApplicationContext(), "Please install a File Manager.", Toast.LENGTH_SHORT)
          .show();
    }
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
    bindFavourableData();
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
    mBinding.bio.setText(userDetail.getBio());
    if (!TextUtils.isEmpty(userDetail.getProfilePic())) {
      Picasso.with(this)
          .load(userDetail.getProfilePic())
          .placeholder(R.drawable.drw_show_img_progress)
          .into(mBinding.profilePhoto);
    }
    if (!TextUtils.isEmpty(userDetail.getUser_doc())) {
      String fileName = userDetail.getUser_doc()
          .substring(userDetail.getUser_doc().lastIndexOf('/') + 1,
              userDetail.getUser_doc().length());
      mBinding.fileName.setText(fileName);
    } else {
      mBinding.fileName.setText("");
    }
  }

  /**
   * Entry point of asking the run time permission
   */
  private void bindFavourableData() {
    CategoryAdapter<Category> adapter =
        new CategoryAdapter<>(AgentProfileActivity.this, CategoryList.favourableCategory(this));
    mBinding.favourableSpinner.setAdapter(adapter);
    mBinding.favourableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Category category =
            CategoryList.favourableCategory(AgentProfileActivity.this).get(position);
        categoryType = category.getCategoryId();
      }

      @Override public void onNothingSelected(AdapterView<?> parent) {
      }
    });
  }

  private void permissionRequestList() {
    HashMap<String, String> permissionsMap = new HashMap<>();
    permissionsMap.put(Manifest.permission.WRITE_EXTERNAL_STORAGE,
        getResources().getString(R.string.message_storage_permission));
    mRunTimePermission.buildPermissionList(permissionsMap, REQ_PERMISSION_PROFILE_IMAGE_QUESTIONER);
  }

  private void onClickUpdteProfile() {
    String fullName = mBinding.fullName.getText().toString();
    String email = mBinding.email.getText().toString();
    String oldPassword = mBinding.oldPassword.getText().toString();
    String newPassword = mBinding.NewPassword.getText().toString();
    String confirmPassword = mBinding.ConfirmPassword.getText().toString();
    String number = mBinding.number.getText().toString();
    String gender = mBinding.genderSpinner.getSelectedItem().toString();
    String bio = mBinding.bio.getText().toString();
    if (gender.equals(getString(R.string.txt_male))) {
      genderType = 1;
    } else if (gender.equals(getString(R.string.txt_female))) {
      genderType = 2;
    } else {
      genderType = 1;
    }
    if (!newPassword.equals(confirmPassword)) {
      DialogUtils.showDialog(this, R.string.alert_password_match);
    } else {
      mViewModel.updateProfile(fullName, email, oldPassword, newPassword, number, profileImagePath,
          genderType, bio, categoryType, uploadFilePath);
    }
  }

  private void detectFieldChange() {
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
    //picker.openDialog(REQ_PROFILE_IMAGE_QUESTIONER);
    if (chooseFlag == 1) {
      picker.openDialog(REQ_PROFILE_IMAGE);
    } else if (chooseFlag == 2) {
      showFileChooser();
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.KITKAT) @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    picker.onActivityResult(requestCode, resultCode, data);
    mRunTimePermission.onActivityResult(REQ_PERMISSION_PROFILE_IMAGE_QUESTIONER, requestCode,
        requestCode, data);

    if (requestCode == PICK_FILE_RESULT_CODE) {
      if (resultCode == Activity.RESULT_OK) {
        Uri selectedFileURI = data.getData();
        File file = new File(FileUtils.getPath(this, selectedFileURI));
        Log.d(TAG, "File : " + file.getName());
        uploadFilePath = file.getAbsolutePath();
        mBinding.fileName.setText(file.getName());
      }
    }
  }

  @Override public void updateSuccessfully(String message) {
    DialogUtils.showToast(this, message);
    setResult(RESULT_OK);
    finish();
  }

  @Override public void onFailed(String message) {
    DialogUtils.showDialog(this, message);
  }
}