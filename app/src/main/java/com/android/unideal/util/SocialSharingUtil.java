package com.android.unideal.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by ADMIN on 22-11-2016.
 */

public class SocialSharingUtil {
  public static void shareWithMail(String referCode, Context context) {
    try {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      Uri data = Uri.parse("mailto:?subject=" + "" + "&body=" + referCode);
      intent.setData(data);
      context.startActivity(intent);
    } catch (android.content.ActivityNotFoundException ex) {
      DialogUtils.showToast(context, "App not available");
    }
  }

  public static void shareWithMessage(String referCode, Context context) {
    try {
      Intent sendIntent = new Intent(Intent.ACTION_VIEW);
      sendIntent.putExtra("sms_body", referCode);
      sendIntent.setType("vnd.android-dir/mms-sms");
      context.startActivity(sendIntent);
    } catch (android.content.ActivityNotFoundException ex) {
      DialogUtils.showToast(context, "App not available");
    }
  }

  public static void shareWithWhatsApp(String referCode, Context context) {
    try {
      Intent sendIntent = new Intent();
      sendIntent.setAction(Intent.ACTION_SEND);
      sendIntent.putExtra(Intent.EXTRA_TEXT, referCode);
      sendIntent.setType("text/plain");
      sendIntent.setPackage("com.whatsapp");
      context.startActivity(sendIntent);
    } catch (android.content.ActivityNotFoundException ex) {
      DialogUtils.showToast(context, "Whatsapp not available");
    }
  }

  public static void shareWithAllApp(String referCode, Context context) {
    try {
      Intent intent = new Intent(Intent.ACTION_SEND);
      intent.setType("text/plain");
      intent.putExtra(Intent.EXTRA_TEXT, referCode);
      Intent mailer = Intent.createChooser(intent, null);
      context.startActivity(mailer);
    } catch (android.content.ActivityNotFoundException ex) {
      DialogUtils.showToast(context, "App not available");
    }
  }

  public static void copyToClipboard(String referCode, Context context) {
    ClipboardManager clipboard =
        (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    ClipData clip = ClipData.newPlainText("Copied Text", referCode);
    DialogUtils.showToast(context, "Copy to clipboard");
    clipboard.setPrimaryClip(clip);
  }
}
