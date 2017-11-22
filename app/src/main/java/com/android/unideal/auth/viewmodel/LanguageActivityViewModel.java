package com.android.unideal.auth.viewmodel;

import android.content.Context;
import android.view.View;

/**
 * Created by ADMIN on 19-09-2016.
 */

public class LanguageActivityViewModel {
  private Context context;
  private LanguageListener mListener;

  public LanguageActivityViewModel(Context context, LanguageListener mListener) {
    this.context = context;
    this.mListener = mListener;
  }

  public void moveToLoginScreen(View view) {
    mListener.moveToLogInScreen();
  }

  public interface LanguageListener {
    void moveToLogInScreen();
  }
}
