package com.android.unideal.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ADMIN on 22-02-2017.
 */

public class ShowCasePreference {
  private static final String PREF_NAME = "user_showcase";
  private static final String PREF_SHOW_CASE_LAYOUT_MSG = "showcase_layout_msg";
  private static final String PREF_SHOW_CASE_LAYOUT_PROFILE = "showcase_layout_prof";
  private static final String PREF_SHOW_CASE_LAYOUT_SWITCHAC = "showcase_layout_switch";
  private static final String PREF_SHOW_CASE_LAYOUT_REFER_CODE = "showcase_layout_refer_code";
  private static ShowCasePreference SINGLETON;
  private Context mContext;
  private SharedPreferences pref;
  private SharedPreferences.Editor editor;
  private int PRIVATE_MODE = 0;

  private ShowCasePreference(Context context) {
    this.mContext = context;
    pref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    editor = pref.edit();
  }

  public static ShowCasePreference get(Activity activity) {
    return get(activity.getApplicationContext());
  }

  public static ShowCasePreference get(Context context) {
    if (SINGLETON == null) {
      SINGLETON = new ShowCasePreference(context);
    }
    return SINGLETON;
  }

  public boolean getShowCaseLayoutMsg() {
    return pref.getBoolean(PREF_SHOW_CASE_LAYOUT_MSG, true);
  }

  public void setShowCaseLayoutMsg(boolean checkFlag) {
    editor.putBoolean(PREF_SHOW_CASE_LAYOUT_MSG, checkFlag);
    editor.commit();
  }

  public boolean getShowCaseLayoutProf() {
    return pref.getBoolean(PREF_SHOW_CASE_LAYOUT_PROFILE, true);
  }

  public void setShowCaseLayoutProf(boolean checkFlag) {
    editor.putBoolean(PREF_SHOW_CASE_LAYOUT_PROFILE, checkFlag);
    editor.commit();
  }

  public boolean getShowCaseLayoutSwitch() {
    return pref.getBoolean(PREF_SHOW_CASE_LAYOUT_SWITCHAC, true);
  }

  public void setShowCaseSwitch(boolean checkFlag) {
    editor.putBoolean(PREF_SHOW_CASE_LAYOUT_SWITCHAC, checkFlag);
    editor.commit();
  }

  public boolean getShowCaseLayoutReferCode() {
    return pref.getBoolean(PREF_SHOW_CASE_LAYOUT_REFER_CODE, true);
  }

  public void setShowCaseReferCode(boolean checkFlag) {
    editor.putBoolean(PREF_SHOW_CASE_LAYOUT_REFER_CODE, checkFlag);
    editor.commit();
  }

  public void clearShowCase() {
    setShowCaseLayoutMsg(true);
    setShowCaseLayoutProf(true);
    setShowCaseSwitch(true);
    setShowCaseReferCode(true);
  }
}
