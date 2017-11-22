package com.android.unideal.enums;

/**
 * Created by bhavdip on 10/18/16.
 */

public enum AppMode {
  QUESTIONER(1), AGENT(2), ADMIN(3);
  private int appMode = 1;

  AppMode(int value) {
    appMode = value;
  }

  public int getValue() {
    return appMode;
  }
}
