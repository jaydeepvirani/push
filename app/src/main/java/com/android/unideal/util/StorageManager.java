package com.android.unideal.util;

import android.net.Uri;
import com.orhanobut.logger.Logger;
import java.io.File;

/**
 * Created by ADMIN on 14-10-2016.
 */

public class StorageManager {

  private static final String TAG = "StorageManager";

  public static boolean verifyAppRootDir() {
    File rootPath = new File(Consts.getRootPath());
    if (!rootPath.exists()) {
      return rootPath.mkdir();
    }
    return false;
  }

  /**
   * If image directory not available then create it
   */
  public static boolean verifyImagePath() {
    File imageDirectory = new File(Consts.getImageDir());
    if (!imageDirectory.exists()) {
      return imageDirectory.mkdir();
    }
    return false;
  }

  private static String getEmptyImageDirectory() {
    // if already exist at this location delete it first
    File dirImages = new File(Consts.getImageDir());
    final boolean isDeletedDirectory = deleteDirectory(dirImages);
    Logger.t(TAG).d("delete the images directory");
    verifyImagePath();
    File imagePath = getNewFilePath();
    return imagePath.getAbsolutePath();
  }

  public static boolean deleteImageDirectory() {
    // if already exist at this location delete it first
    File dirImages = new File(Consts.getImageDir());
    final boolean isDeletedDirectory = deleteDirectory(dirImages);
    return isDeletedDirectory;
  }
  private static File getNewFilePath() {
    return new File(Consts.getImageDir() + System.currentTimeMillis() + ".jpeg"); // /1123123123.jpg
  }

  /**
   * This will clear the image directory and give the new image name
   */
  public static Uri getDefaultProfileImageURI() {
    return Uri.fromFile(new File(StorageManager.getEmptyImageDirectory()));
  }

  public static boolean deleteDirectory(File path) {
    if (path.exists()) {
      File[] files = path.listFiles();
      if (files == null) {
        return true;
      }
      for (int i = 0; i < files.length; i++) {
        if (files[i].isDirectory()) {
          deleteDirectory(files[i]);
        } else {
          files[i].delete();
        }
      }
    }
    return (path.delete());
  }
}

