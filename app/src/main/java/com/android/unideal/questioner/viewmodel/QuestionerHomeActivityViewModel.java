package com.android.unideal.questioner.viewmodel;

import android.content.Context;

/**
 * Created by ADMIN on 11-10-2016.
 */

public class QuestionerHomeActivityViewModel {
  private Context context;
  private QuestionerListener mListener;

  public QuestionerHomeActivityViewModel(Context context, QuestionerListener mListener) {
    this.context = context;
    this.mListener = mListener;
  }

  public void onCreate() {
    mListener.setUpResideMenu();
    mListener.updateProfileInfo();
    mListener.startBindingViews();
    mListener.startUploadingService();
  }

  public interface QuestionerListener {
    void setUpResideMenu();

    void startBindingViews();

    void updateProfileInfo();

    void startUploadingService();
  }
}
