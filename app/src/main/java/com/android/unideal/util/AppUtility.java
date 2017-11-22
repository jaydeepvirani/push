package com.android.unideal.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by bhavdip on 10/1/16.
 */

public class AppUtility {

  public static void showToast(Context activityContext, String message) {
    if (!TextUtils.isEmpty(message)) {
      Toast.makeText(activityContext, message, Toast.LENGTH_SHORT).show();
    }
  }

  public static void showToast(Context activityContext, @StringRes int message) {
    if (message > -1) Toast.makeText(activityContext, message, Toast.LENGTH_SHORT).show();
  }

  /**
   * Always hide the window soft keyboard
   */
  public static void hideSoftKeyBoard(Activity activity, View view) {
    if (activity != null && view != null) {
      InputMethodManager imm =
          (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }

  /**
   * @param context Application context
   * @param currentFocus Current Focus EditText
   * @param windowInput True for show and False for hide if it's visible
   */
  public static void softInputWindow(Context context, View currentFocus, boolean windowInput) {
    InputMethodManager imm =
        (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    if (!windowInput) {
      if (currentFocus != null && currentFocus instanceof EditText) {
        imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
      }
    } else {
      imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
  }

  public static int dpToPx(Context context, @DimenRes int dp) {
    return context.getResources().getDimensionPixelSize(dp);
  }

  public static boolean isNetworkConnected(Context context) {
    ConnectivityManager cm =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

    return cm.getActiveNetworkInfo() != null;
  }

  public static String findStringResource(Context activityContext, int resourceID) {
    return activityContext.getResources().getString(resourceID);
  }

  public static void loadSVGImage(ImageView mImageView, int iconResource, @ColorRes int id) {
    Drawable icon = ContextCompat.getDrawable(mImageView.getContext(), iconResource);
    DrawableCompat.setTint(icon.mutate(), ContextCompat.getColor(mImageView.getContext(), id));
    mImageView.setImageDrawable(icon);
  }

  public static boolean isValidEmail(CharSequence target) {
    return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target)
        .matches();
  }

  public static void hideSoftKeyBoard(Window window) {
    if (window == null) {
      return;
    }
    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
  }

  public static void setEditTextColor(EditText editText, @ColorRes int colorRes) {
    if (editText == null) {
      return;
    }
    int color = ContextCompat.getColor(editText.getContext(), colorRes);
    editText.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
  }

  public static void changeStatusBarColor(Window window, @ColorInt int colorInt) {
    if (window == null) {
      return;
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(colorInt);
    }
  }

  public static String convertMD5(String s) {
    try {
      // Create MD5 Hash
      MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
      digest.update(s.getBytes());
      byte messageDigest[] = digest.digest();

      // Create Hex String
      StringBuffer hexString = new StringBuffer();
      for (int i = 0; i < messageDigest.length; i++)
        hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
      return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return "";
  }

}
