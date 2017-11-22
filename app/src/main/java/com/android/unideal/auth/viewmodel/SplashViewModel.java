package com.android.unideal.auth.viewmodel;

import android.content.Context;
import android.os.Handler;
import com.android.unideal.rest.CallbackWrapper;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.response.AppConfigData;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import retrofit2.Call;

/**
 * Created by bhavdip on 14/11/16.
 */

public class SplashViewModel {

  private final int splash_delay_length_ms;
  private final SplashViewModelListener mViewModelListener;
  private final Context activityContext;
  private boolean isConfigurationComplete, isSplashTimeOut;

  public SplashViewModel(Context activityContext, SplashViewModelListener mViewModelListener,
      int splash_delay_length_ms) {
    this.activityContext = activityContext;
    this.mViewModelListener = mViewModelListener;
    this.splash_delay_length_ms = splash_delay_length_ms;
  }

  public void onCreateActivity() {
    mViewModelListener.startBindingViews();
    startNormalSplash();
  }

  public void startSplashScreen() {
    // by default configuration flag is false
    isConfigurationComplete = false;
    mViewModelListener.showProgressBar(true);
    Call<GenericResponse<AppConfigData>> responseCall = RestClient.get().appConfiguration();
    responseCall.enqueue(new CallbackWrapper<AppConfigData>() {
      @Override
      public void onSuccess(GenericResponse<AppConfigData> response) {
        isConfigurationComplete = true;
        mViewModelListener.onReceivedConfigData(response.getData());
      }

      @Override
      public void onFailure(GenericResponse<AppConfigData> response) {
        isConfigurationComplete = false;
        mViewModelListener.onFailConfigData(response.getMessage());
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        isConfigurationComplete = false;
        mViewModelListener.onConfigurationException(errorResponse.getMessage());
      }
    });
  }

  public boolean isSplashCompleted() {
    return (isConfigurationComplete && isSplashTimeOut);
  }

  /**
   * Normal splash bind with handler
   * parallel we make configuration call.
   * Once both finish then only splash
   * should finish
   */
  private void startNormalSplash() {
    mViewModelListener.showProgressBar(true);
    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        isSplashTimeOut = true;
        mViewModelListener.onSplashCompleted();
      }
    }, splash_delay_length_ms);

    startSplashScreen();
  }

  public interface SplashViewModelListener {
    void onReceivedConfigData(AppConfigData appConfigData);

    void onFailConfigData(String errorMessage);

    void onConfigurationException(String exceptionMessage);

    void onSplashCompleted();

    void showProgressBar(boolean value);

    void startBindingViews();
  }
}
