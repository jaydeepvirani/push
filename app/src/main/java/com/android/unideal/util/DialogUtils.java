package com.android.unideal.util;

/**
 * Created by MRUGESH on 10/4/2016.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.widget.Toast;
import com.android.unideal.R;

public class DialogUtils {

  private static DialogUtils dialogUtils = new DialogUtils();
  private ProgressDialog mProgressDialog;

  private DialogUtils() {
  }

  public static DialogUtils getInstance() {
    return dialogUtils;
  }

  public static void showToast(Context activityContext, String message) {
    if (activityContext != null) {
      if (!TextUtils.isEmpty(message)) {
        Toast.makeText(activityContext, message, Toast.LENGTH_LONG).show();
      }
    }
  }

  public static void showToast(Context activityContext, int message) {
    if (activityContext != null) {
      Toast.makeText(activityContext, activityContext.getResources().getString(message),
          Toast.LENGTH_LONG).show();
    }
  }

  public static void showDialog(Context context, int message, int positiveButton) {
    showDialog(context, R.string.title_app_name, message, positiveButton, -1, null);
  }

  public static void showDialog(Context context, int message) {
    showDialog(context, R.string.title_app_name, message, -1, -1, null);
  }

  public static void showDialog(Context context, String message) {
    showDialog(context, R.string.title_app_name, message, -1, -1, null);
  }

  public static void showDialog(Context context, String message,
      DialogInterface.OnClickListener onClickListener) {
    showDialog(context, R.string.title_app_name, message, -1, -1, onClickListener);
  }

  public static void showDialog(Context context, int title, int message, int positivebutton,
      int nagativebutton, DialogInterface.OnClickListener onClickListener) {
    showDialog(context, title, context.getString(message), positivebutton, nagativebutton,
        onClickListener);
  }

  public static void showDialog(Context context, String message, int positivebutton,
      int nagativebutton, DialogInterface.OnClickListener onClickListener) {
    showDialog(context, R.string.title_app_name, message, positivebutton, nagativebutton,
        onClickListener);
  }

  public static void showDialog(Context context, int title, String message, int positivebutton,
      int nagativebutton, DialogInterface.OnClickListener onClickListener) {
    AlertDialog.Builder dialog =
        new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));
    if (title != -1) {
      dialog.setTitle(title);
    }
    dialog.setMessage(message);
    if (positivebutton > -1) {
      dialog.setPositiveButton(positivebutton, onClickListener);
    } else {
      dialog.setPositiveButton(R.string.btn_ok, onClickListener);
    }

    if (nagativebutton > -1) {
      dialog.setNegativeButton(nagativebutton, null);
    }
    dialog.show();
  }

  /**
   * This is a helper method for show exception while making a API request
   */
  public static void showExceptionDialog(Context activityContext, String exceptionMessage) {
    showDialog(activityContext, R.string.unideal_app_name, exceptionMessage, R.string.btn_ok, -1,
        null);
  }

  /**
   * This is the single progress dialog use to show the please wait indicator
   *
   * @param activityContext must pass the activity context not the application context
   */
  public synchronized void showProgressDialog(Context activityContext) {
    if (activityContext == null) return;
    if (mProgressDialog != null) {
      hideProgressDialog();
    }
    mProgressDialog = new ProgressDialog(activityContext);
    mProgressDialog.setMessage(activityContext.getString(R.string.msg_please_wait));
    mProgressDialog.setCancelable(false);
    mProgressDialog.show();
  }

  /**
   * Hide the single progress dialog
   */
  public synchronized void hideProgressDialog() {
    if (mProgressDialog != null) {
      mProgressDialog.dismiss();
    }
  }
}