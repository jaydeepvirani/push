package com.android.unideal.socket;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public abstract class SocketBaseActivity extends AppCompatActivity {
  private static final String TAG = "SocketBaseActivity";
  private boolean isActiveSocket = false;

  @Override
  public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
    super.onCreate(savedInstanceState, persistentState);
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (!isActiveSocket) {
      Log.d(TAG, "App is open");
      onOpenApp();
      isActiveSocket = true;
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (isActiveSocket) {
      Log.d(TAG, "App is close");
      onCloseApp();
    }
  }

  protected abstract void onOpenApp();

  protected abstract void onCloseApp();
}
