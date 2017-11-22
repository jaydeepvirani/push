package com.android.unideal.questioner.viewmodel;

import android.content.Context;

/**
 * Created by ADMIN on 18-10-2016.
 */

public class QuestionerHelpFragmentViewModel {
  private Context context;
  private QuestionerHelpListener mListener;

  public QuestionerHelpFragmentViewModel(Context context, QuestionerHelpListener mListener) {
    this.context = context;
    this.mListener = mListener;
  }

  public interface QuestionerHelpListener {

  }
}
