package com.android.unideal.util;

import android.os.Environment;

/**
 * Created by ADMIN on 23-09-2016.
 */

public class Consts {
  public static final int SPLASH_DELAY_LENGTH = 4000;
  public static final String KEY_LANGUAGE_MODE = "language_mode";
  public static final int NOTI_TYPE_JOB = 1;
  public static final int NOTI_TYPE_MESSAGING = 2;
  public static final int NOTI_TYPE_ANNOUNCEMENT = 3;
  public static String ENGLISH_MODE = "en"; // Web Service API Locale Language
  public static String CHINESE_MODE = "ch";// Web Service API Locale Language
  public static String LOCALE_ENGLISH = "en"; //Application locale language code
  public static String LOCALE_CHINESE = "zh";//Application locale language code
  public static int daysSelect = 28;
  public static String SYSTEM_CHINESE = "中文";
  public static String SYSTEM_ENGLISH = "English";
  public static String genderTypeMale = "1";
  public static String genderTypeFeMale = "2";
  public static int MIN_PROMOTIONAL_LENGTH = 4;
  public static int MIN_REFERRAL_LENGTH = 5;
  public static boolean dismissOnTouch = false;
  public static int SHOWCASE_GUIDE_SHOW = 1;
  private static String DIR_HOME = "UniDeal";
  private static String DIR_IMAGE = "images";
  private static String APPLICATION_PATH = Environment.getExternalStorageDirectory().getPath();

  private static String getApplicationPath() {
    return (APPLICATION_PATH + "/");
  }

  /**
   * Absolute application home directory
   * <p/>
   * <br>
   * For example '/mnt/sd card/Consumer' or /storage/storage0/Consumer/
   *
   * @return return the path in string
   */

  public static String getRootPath() {
    return (getApplicationPath() + DIR_HOME + "/");
  }

  /**
   * Absolute application image directory
   * <p/>
   * <br>
   * For example '/mnt/sd card/Consumer/images' or /storage/storage0/Consumer/images
   *
   * @return return the path in string
   */

  public static String getImageDir() {
    return getRootPath() + DIR_IMAGE + "/";
  }
}
