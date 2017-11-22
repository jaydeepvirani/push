package com.android.unideal.auth.viewmodel;

import android.content.Context;

/**
 * Created by ADMIN on 21-09-2016.
 */

public class SignUpViewModel {
  private static final String TAG = "SignUpViewModel";
  private SignUpListener mListener;
  private Context context;

  public SignUpViewModel(SignUpListener mListener, Context context) {
    this.mListener = mListener;
    this.context = context;
  }

  public interface SignUpListener {

  }
}
