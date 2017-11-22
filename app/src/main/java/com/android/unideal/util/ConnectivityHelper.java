package com.android.unideal.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.android.unideal.UniDealApplication;

public class ConnectivityHelper {

  private static ConnectivityHelper networkManager = new ConnectivityHelper();

  private ConnectivityHelper() {
  }

  public static ConnectivityHelper getInstance() {
    return networkManager;
  }

  /**
   * Get the network info
   *
   * @param context application context
   * @return NetworkInfo
   */
  private static NetworkInfo getNetworkInfo(Context context) {
    ConnectivityManager cm =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    return cm.getActiveNetworkInfo();
  }

  /**
   * Check if there is any connectivity
   *
   * @return boolean is connected or not
   */
  public boolean isConnected() {
    boolean retVal;
    NetworkInfo info = getNetworkInfo(UniDealApplication.getApplication());
    retVal = (info != null && info.isConnectedOrConnecting());
    return retVal;
  }
}

