package com.android.unideal.auth.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import com.android.unideal.R;
import com.android.unideal.auth.viewmodel.ForgetPasswordViewModel;
import com.android.unideal.databinding.ForgetPasswordBinding;
import com.android.unideal.util.AppUtility;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.SimpleTextWatcher;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by ADMIN on 03-10-2016.
 */

public class ForgetPasswordActivity extends AppCompatActivity
    implements ForgetPasswordViewModel.ForgetPasswordListener {
  private ForgetPasswordBinding mBinding;
  private ForgetPasswordViewModel mViewModel;

  public static Intent startForgetPasswordActivity(Activity activity) {
    return new Intent(activity, ForgetPasswordActivity.class);
  }

  private static void setWindowFlag(Activity activity, final int bits, boolean on) {
    Window win = activity.getWindow();
    WindowManager.LayoutParams winParams = win.getAttributes();
    if (on) {
      winParams.flags |= bits;
    } else {
      winParams.flags &= ~bits;
    }
    win.setAttributes(winParams);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mBinding = DataBindingUtil.setContentView(this, R.layout.activity_forget_password);
    // Set the padding to match the Status Bar height
    mViewModel = new ForgetPasswordViewModel(this, this);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      mBinding.forgetPasswordToolBar.getLayoutParams().height = dpToPx(48) + getStatusBarHeight();
      mBinding.forgetPasswordToolBar.setPadding(0, getStatusBarHeight(), 0, 0);
      mBinding.forgetPasswordToolBar.requestLayout();
    }

    if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
      setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
    }
    if (Build.VERSION.SDK_INT >= 19) {
      getWindow().getDecorView()
          .setSystemUiVisibility(
              View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
    if (Build.VERSION.SDK_INT >= 21) {
      setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
      getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    mBinding.imageViewBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
    mBinding.resetPassword.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        resetPassword();
      }
    });
    detectsFieldChanged();
  }

  private void detectsFieldChanged() {
    mBinding.emailLayout.addTextChangedListener(new SimpleTextWatcher() {
      @Override
      public void onTextChanged(String newValue) {
        AppUtility.setEditTextColor(mBinding.emailLayout, R.color.colorGallery);
      }
    });
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  // A method to find height of the status bar
  public int getStatusBarHeight() {
    int result = 0;
    int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      result = getResources().getDimensionPixelSize(resourceId);
    }
    return result;
  }

  public int dpToPx(int dp) {
    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
    int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    return px;
  }

  public void resetPassword() {
    if (!TextUtils.isEmpty(mBinding.emailLayout.getText().toString()) && isEditTextContainEmail(
        mBinding.emailLayout)) {
      mViewModel.setResetPassword(mBinding.emailLayout.getText().toString());
    } else {
      mBinding.emailLayout.getBackground()
          .setColorFilter(ContextCompat.getColor(this, R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
    }
  }

  public boolean isEditTextContainEmail(EditText argEditText) {
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
  public void onResetSuccessfully(String message) {
    DialogUtils.showDialog(this, message, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        finish();
      }
    });
  }

  @Override
  public void onFailPassword(String message) {
    DialogUtils.showDialog(this, message);
  }
}
