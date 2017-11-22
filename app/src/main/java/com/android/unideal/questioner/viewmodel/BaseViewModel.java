package com.android.unideal.questioner.viewmodel;

/**
 * Created by CS02 on 12/28/2016.
 */

public interface BaseViewModel {
  void showProgressDialog();

  void hideProgressDialog();

  void showToast(String message);
}
