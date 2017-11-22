package com.android.unideal.rtp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import com.android.unideal.BuildConfig;
import com.android.unideal.R;
import com.android.unideal.util.StorageManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunTimePermissionManager {
  private static final int REQUEST_PERMISSION_SETTING = 123;
  private int mRequestCode = -1;
  private List<String> mPermissionKey = new ArrayList<>();
  private HashMap<String, String> originalPermissionList = new HashMap<>();

  private callbackRunTimePermission callbackRunTimePermission;

  private Activity mActivity;

  public RunTimePermissionManager(Activity activity) {
    this.mActivity = activity;
  }

  public RunTimePermissionManager registerCallback(
      RunTimePermissionManager.callbackRunTimePermission callbackRunTimePermission) {
    this.callbackRunTimePermission = callbackRunTimePermission;
    return this;
  }

  private boolean hasCallbackAvailable() {
    return callbackRunTimePermission != null;
  }

  private Activity getActivity() {
    return mActivity;
  }

  /**
   * Make sure the list of permissions should declare in project android Manifest file.
   *
   * @param permissionsMap key is the android manifest permission name and the value is relevant
   * display name
   */
  public void buildPermissionList(HashMap<String, String> permissionsMap) {

    if (this.mActivity == null) return;
    originalPermissionList = permissionsMap;
    // this is list of permission that are not granted yet.
    List<String> permissionsNeededName = new ArrayList<>();
    // android manifest permission unique package name
    List<String> manifestPermissionKey = new ArrayList<>(permissionsMap.keySet());
    List<String> manifestPermissionName = new ArrayList<>(permissionsMap.values());

    if (!isArrayListEmpty(manifestPermissionKey)) {
      for (String key : manifestPermissionKey) {
        if (!checkSelfPermission(key)) {
          permissionsNeededName.add(permissionsMap.get(key));
        }
      }
      // permission not granted array list is not empty then start show the rational dialog
      if (!isArrayListEmpty(permissionsNeededName)) {
        String buildMessage = mActivity.getString(R.string.grant_access_text) + manifestPermissionName.get(0);
        for (int i = 1; i < permissionsNeededName.size(); i++) {
          buildMessage = buildMessage + ", " + permissionsNeededName.get(i);
        }
        // this will display the UI to the user
        if (hasCallbackAvailable()) callbackRunTimePermission.showRationalDialog(buildMessage);
      } else {
        // permission granted array list is empty that means all permission is granted to this application
        if (hasCallbackAvailable()) callbackRunTimePermission.requestAllPermissionGranted();
      }
    }
  }

  /**
   * It start query to the system for prompt the permission dialog to
   * the user
   *
   * @param requestCode Request code must between 0 to 255
   */
  public void askRunTimePermissions(int requestCode) {
    mRequestCode = requestCode;
    if (!isArrayListEmpty(mPermissionKey)) {
      String[] arrayOfKey = mPermissionKey.toArray(new String[mPermissionKey.size()]);
      ActivityCompat.requestPermissions(getActivity(), arrayOfKey, mRequestCode);
    }
  }

  public void onRequestPermissionsResult(int requestCode, @NonNull String[] responsePermission,
      @NonNull int[] grantResults) {
    if (mRequestCode == requestCode) {
      Map<String, Integer> responseResultSet = new HashMap<>();
      for (int i = 0; i < responsePermission.length; i++) {
        responseResultSet.put(responsePermission[i], grantResults[i]);
      }
      // default all permission granted
      boolean isAllPermissionGranted = true;
      boolean isNeverAsk = false;
      List<String> permissionDeniedName = new ArrayList<>();
      for (String key : mPermissionKey) {
        if (responseResultSet.get(key) != PackageManager.PERMISSION_GRANTED) {
          boolean showRationale =
              ActivityCompat.shouldShowRequestPermissionRationale(mActivity, key);
          if (!showRationale) {
            // user denied flagging NEVER ASK AGAIN
            // you can either enable some fall back,
            // disable features of your app
            // or open another dialog explaining
            // again the permission and directing to
            // the app setting
            isNeverAsk = true;
          }
          isAllPermissionGranted = false;
          permissionDeniedName.add(originalPermissionList.get(key));
        }
      }
      if (hasCallbackAvailable()) {
        if (isAllPermissionGranted) {
          callbackRunTimePermission.requestAllPermissionGranted();
        } else {
          if (isNeverAsk) {
            openSettingScreen();
          } else {
            String buildMessage = mActivity.getString(R.string.grant_access_text) + permissionDeniedName.get(0);
            for (int i = 1; i < permissionDeniedName.size(); i++) {
              buildMessage = buildMessage + ", " + permissionDeniedName.get(i);
            }
            callbackRunTimePermission.deniedPermission(buildMessage);
          }
        }
      }
      mRequestCode = -1;
    }
  }

  private void openSettingScreen() {
    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
    intent.setData(uri);
    mActivity.startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
  }

  public void onActivityResult(int permissionAskedCode, int requestCode, int resultCode,
      Intent data) {
    if (requestCode == REQUEST_PERMISSION_SETTING && permissionAskedCode == mRequestCode) {
      if (hasPermissions(mPermissionKey)) {
        // all permission granted
        StorageManager.verifyAppRootDir();
        StorageManager.verifyImagePath();
        callbackRunTimePermission.requestAllPermissionGranted();
      }
    }
  }

  private boolean hasPermissions(@NonNull List<String> permissions) {
    for (String permission : permissions)
      if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(mActivity,
          permission)) {
        return false;
      }
    return true;
  }

  private boolean isArrayListEmpty(List<String> arrayList) {
    return !((arrayList != null) && (arrayList.size() > 0));
  }

  /**
   * It can check that whether the given permission has been granted. If not granted then
   * we should prompt the rational dialog or guide the user why we need to use this resource if
   * user allow us then only we can request the permission.
   *
   * @param permissionName check the self permission
   * @return true if we need to show the rational dialog to the user for better guide his or her
   */
  private boolean checkSelfPermission(String permissionName) {
    boolean result = true;
    if (ContextCompat.checkSelfPermission(getActivity(), permissionName)
        != PackageManager.PERMISSION_GRANTED) {
      if (!mPermissionKey.contains(permissionName)) {
        mPermissionKey.add(permissionName);
      }
      result = false;
      if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissionName)) {
        result = false;
      }
    }
    return result;
  }

  public interface callbackRunTimePermission {
    void showRationalDialog(String message);

    void deniedPermission(String deniedPermission);

    void requestAllPermissionGranted();
  }
}
