package com.android.unideal.enums;

/**
 * Created by bhavdip on 18/11/16.
 */

public enum AuthMode {
  GOOGLE(1), FACEBOOK(2), CUSTOM(3);
  private int authMode;

  AuthMode(int authMode) {
    this.authMode = authMode;
  }

  public int getAuthMode() {
    return authMode;
  }

  public AuthMode setAuthMode(int authMode) {
    this.authMode = authMode;
    return this;
  }
}
