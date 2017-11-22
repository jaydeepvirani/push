package com.android.unideal.util;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import com.android.unideal.UniDealApplication;

/**
 * Created by MRUGESH on 10/2/2016.
 */

public class AppConstants {

  /**
   * Default date format for the  application, This date
   * will show in notification and My job posting
   */
  public static final String DATE_FORMAT = "dd MMM yyyy";

  public static final String APP_ROOT_DIR = Environment.getExternalStorageDirectory() + "/UniDeal";

  public static String getVersionName() {
    String versionName = "N/A";
    try {
      PackageManager manager = UniDealApplication.getApplication().getPackageManager();
      PackageInfo info =
          manager.getPackageInfo(UniDealApplication.getApplication().getPackageName(), 0);
      versionName = info.versionName;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return versionName;
  }
}
