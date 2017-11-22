package com.android.unideal.util.google;

/**
 * Created by CS02 on 11/9/2016.
 */

public interface GoogleLoginListener {
  void onLogin(String id, String name, String email, String image);

  void onError(String error);
}
