package com.android.unideal.questioner.viewmodel;

import android.content.Context;

/**
 * Created by ADMIN on 12-10-2016.
 */

public class SettingsFragmentViewModel {
  private Context context;
  private QuestionerSettingsListener mListener;

  public SettingsFragmentViewModel(Context context,QuestionerSettingsListener mListener)
  {
    this.context = context;
    this.mListener = mListener;
  }


  public interface QuestionerSettingsListener
  {

  }
}

