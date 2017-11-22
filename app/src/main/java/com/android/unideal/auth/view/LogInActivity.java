package com.android.unideal.auth.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import com.android.unideal.R;
import com.android.unideal.agent.view.AgentHomeActivity;
import com.android.unideal.auth.viewmodel.LogInActivityViewModel;
import com.android.unideal.data.UserDetail;
import com.android.unideal.databinding.LoginActivityBinding;
import com.android.unideal.enums.AppMode;
import com.android.unideal.enums.AuthMode;
import com.android.unideal.flurry.FlurryManager;
import com.android.unideal.questioner.view.DisclaimerActivity;
import com.android.unideal.questioner.view.QuestionerHomeActivity;
import com.android.unideal.util.AppUtility;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.SessionManager;
import com.android.unideal.util.SimpleTextWatcher;
import com.android.unideal.util.google.GoogleLoginHelper;
import com.android.unideal.util.google.GoogleLoginListener;
import com.social.fb.FaceBookHelper;
import com.social.fb.FacebookLoginListener;
import com.social.fb.models.User;
import java.util.Arrays;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LogInActivity extends AppCompatActivity
    implements LogInActivityViewModel.LogInListener {

  private final static int REQ_DISCLAIMER_GOOGLE = 346;
  private final static int REQ_DISCLAIMER_FB = 3478;
  private static final int REQUEST_LOGIN = 23;
  private LoginActivityBinding mBinding;
  private LogInActivityViewModel mViewModel;

  private FaceBookHelper faceBookHelper;
  private GoogleLoginHelper googleLoginHelper;
  private String id, name, email, image;
  private String TNC_TYPE = "userTnc";

  //TextViews
  public static Intent startLogInActivity(Activity activity) {
    return new Intent(activity, LogInActivity.class);
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
    mViewModel = new LogInActivityViewModel(this, this);
    initGoogleApi();
    initFbApi();
    //setLayout();
    //hide soft keyboard when activity opens
    AppUtility.hideSoftKeyBoard(getWindow());
    setClickListeners();
    detectFieldChange();
  }

  private void initGoogleApi() {
    googleLoginHelper = new GoogleLoginHelper(this);
    googleLoginHelper.init(this, new GoogleLoginListener() {
      @Override
      public void onLogin(String id, String name, String email, String image) {
        mViewModel.checkForGoogleId(id, name, email, image);
      }

      @Override
      public void onError(String error) {
        AppUtility.showToast(LogInActivity.this, error);
      }
    });
  }

  private void initFbApi() {
    faceBookHelper = new FaceBookHelper(this);
    faceBookHelper.setListener(new FacebookLoginListener() {
      @Override
      public void onLogin(User user) {
        mViewModel.checkForFbId(user.getId(), user.getName(), user.getEmail(),
            user.getProfileImage());
      }

      @Override
      public void onError(String error) {
        AppUtility.showToast(LogInActivity.this, error);
      }
    });
  }

  private void setClickListeners() {
    //onClick of GooglePlus
    mBinding.googlePlusLogo.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        googleLoginHelper.loginWithGooglePlus();
      }
    });
    //when sign up is click load sign up activity
    mBinding.signUpClick.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        launchSignUpScreen();
      }
    });
    mBinding.signUpClickAgain.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        launchSignUpScreen();
      }
    });
    mBinding.faceBookLogo.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        loginWithFb();
      }
    });
    mBinding.forgetPassword.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        launchForgetPasswordActivity();
      }
    });
    mBinding.loginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        loginUser();
      }
    });
  }

  private void loginWithFb() {
    faceBookHelper.login(Arrays.asList("email", "public_profile"));
  }

  private void launchForgetPasswordActivity() {
    startActivity(ForgetPasswordActivity.startForgetPasswordActivity(this));
  }

  private void launchSignUpScreen() {
    Intent intent = SignUpActivity.startSignUpActivity(this);
    startActivity(intent);
  }

  //this method if all the field are valid will let the user log in in the app
  private void loginUser() {
    if (!checkEmptyFields()) {
      String emailAdd = mBinding.emailEditText.getText().toString().trim();
      String password = mBinding.inputPassword.getText().toString();
      mViewModel.customLogin(emailAdd, password);
    }
  }

  private boolean checkEmptyFields() {
    String emailAdd = mBinding.emailEditText.getText().toString().trim();
    String password = mBinding.inputPassword.getText().toString();
    if (!AppUtility.isValidEmail(emailAdd)) {
      setEditTextColor(mBinding.emailEditText, R.color.colorRed);
      return true;
    } else if (TextUtils.isEmpty(password) || password.length() < 6) {
      setEditTextColor(mBinding.inputPassword, R.color.colorRed);
      return true;
    }
    return false;
  }

  private void setEditTextColor(EditText editText, @ColorRes int colorRes) {
    int color = ContextCompat.getColor(this, colorRes);
    editText.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
  }

  private void detectFieldChange() {
    mBinding.emailEditText.addTextChangedListener(new SimpleTextWatcher() {
      @Override
      public void onTextChanged(String newValue) {
        setEditTextColor(mBinding.emailEditText, R.color.colorGallery);
      }
    });
    mBinding.inputPassword.addTextChangedListener(new SimpleTextWatcher() {
      @Override
      public void onTextChanged(String newValue) {
        setEditTextColor(mBinding.inputPassword, R.color.colorGallery);
      }
    });
  }

  @Override
  public void onLoginSuccess(UserDetail userDetail) {
    mViewModel.updateDeviceToken();
    FlurryManager.setUserId(userDetail.getUserId());
    FlurryManager.startEventLogIn(userDetail);
    updateRememberMe(userDetail);
    if (userDetail.getUserType() == AppMode.QUESTIONER.getValue()) {
      startActivityForResult(QuestionerHomeActivity.getActivityIntent(this), REQUEST_LOGIN);
      finish();
    } else {
      startActivityForResult(AgentHomeActivity.getActivityIntent(this), REQUEST_LOGIN);
      finish();
    }
  }

  private void updateRememberMe(UserDetail userDetail) {
    if (userDetail.getFromSocialNetwork() != null && userDetail.getFromSocialNetwork() == 3) {

      if (mBinding.rememberMe.isChecked()) {
        SessionManager.get(this).setRememberMe(true);
      } else {
        SessionManager.get(this).setRememberMe(false);
      }
    } else {
      SessionManager.get(this).setRememberMe(true);
    }
  }

  @Override
  public void onLoginFailed(String message) {
    DialogUtils.showDialog(this, message);
  }

  @Override
  public void onResendActivation(String message, final String emailAddress) {
    DialogUtils.showDialog(this, R.string.title_app_name, message, R.string.btn_resend,
        R.string.btn_ok, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            mViewModel.resendActivation(emailAddress);
          }
        });
  }

  @Override
  public void showAlertDialog(String message) {
    DialogUtils.showDialog(this, message);
  }

  @Override
  public void showTermsConditionDialog(AuthMode authMode, String id, String name, String email,
      String image) {
    switch (authMode) {
      case GOOGLE: {
        saveData(id, name, email, image);
        startActivityForResult(DisclaimerActivity.getDialogActivity(this, AppMode.AGENT, TNC_TYPE),
            REQ_DISCLAIMER_GOOGLE);
        break;
      }
      case FACEBOOK: {
        saveData(id, name, email, image);
        startActivityForResult(DisclaimerActivity.getDialogActivity(this, AppMode.AGENT, TNC_TYPE),
            REQ_DISCLAIMER_FB);
        break;
      }
    }
  }

  private void saveData(String id, String name, String email, String image) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.image = image;
  }

  private void clearData() {
    this.id = null;
    this.name = null;
    this.email = null;
    this.image = null;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_LOGIN) {
      if (resultCode == Activity.RESULT_OK) {
        setResult(RESULT_OK);
        finish();
      }
    }
    if (resultCode == Activity.RESULT_OK) {
      if (requestCode == REQ_DISCLAIMER_GOOGLE) {
        mViewModel.loginWithGoogle(id, name, email, image);
        clearData();
      } else if (requestCode == REQ_DISCLAIMER_FB) {
        mViewModel.loginWithFb(id, name, email, image);
        clearData();
      }
    }
    if (googleLoginHelper != null) {
      googleLoginHelper.onActivityResult(requestCode, resultCode, data);
    }
    if (faceBookHelper != null) {
      faceBookHelper.onActivityResult(requestCode, resultCode, data);
    }
  }
}
