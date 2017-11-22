package com.android.unideal.auth.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import com.android.unideal.R;
import com.android.unideal.agent.view.AgentHomeActivity;
import com.android.unideal.auth.viewmodel.SplashViewModel;
import com.android.unideal.data.UserDetail;
import com.android.unideal.databinding.SplashBinding;
import com.android.unideal.enums.AppMode;
import com.android.unideal.flurry.FlurryManager;
import com.android.unideal.questioner.view.QuestionerHomeActivity;
import com.android.unideal.rest.response.AppConfigData;
import com.android.unideal.util.Consts;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.SessionManager;
import com.jakewharton.rxbinding.view.RxView;
import java.util.Locale;
import rx.functions.Action1;

/**
 * Created by ADMIN on 19-09-2016.
 */

public class SplashActivity extends AppCompatActivity
    implements SplashViewModel.SplashViewModelListener {
  private static String TAG = "SplashActivity";
  private SplashViewModel mSplashViewModel;
  private SplashBinding mSplashBinding;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mSplashBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
    mSplashViewModel = new SplashViewModel(SplashActivity.this, this, Consts.SPLASH_DELAY_LENGTH);
    mSplashViewModel.onCreateActivity();
  }

  @Override
  public void startBindingViews() {
    RxView.clicks(mSplashBinding.textViewRefresh).subscribe(new Action1<Void>() {
      @Override
      public void call(Void aVoid) {
        mSplashBinding.textViewRefresh.setVisibility(View.INVISIBLE);
        mSplashViewModel.startSplashScreen();
      }
    });
  }

  private void openLoginActivity() {
    Intent intent = LanguageActivity.startLanguageActivity(this);
    startActivity(intent);
    finish();
  }

  private void openAgentActivity() {
    startActivity(AgentHomeActivity.getActivityIntent(SplashActivity.this));
    finish();
  }

  private void openRequesterActivity() {
    startActivity(QuestionerHomeActivity.getActivityIntent(SplashActivity.this));
    finish();
  }

  /**
   * return user detail from session if user is remembered
   *
   * @return user detail obj
   */
  private UserDetail getUserDetail() {
    if (SessionManager.get(this) != null) {
      if (SessionManager.get(this).isRememberMe()) {
        return SessionManager.get(this).getActiveUser();
      }
    }
    return null;
  }

  private void redirectInApplication() {
    UserDetail userDetail = getUserDetail();
    if (userDetail != null) {
      FlurryManager.setUserId(userDetail.getUserId());

      if (userDetail.getUserType() == AppMode.QUESTIONER.getValue()) {
        checkAppLanguage();
        openRequesterActivity();
      } else {
        checkAppLanguage();
        openAgentActivity();
      }
    } else {
      openLoginActivity();
    }
  }

  private void checkAppLanguage() {
    String cacheLanguage = SessionManager.get(this).getLanguageMode();
    if (cacheLanguage.equals(Consts.CHINESE_MODE)) {
      changeLanguages(Consts.LOCALE_CHINESE);
    } else {
      changeLanguages(Consts.LOCALE_ENGLISH);
    }
  }

  private void changeLanguages(String localeLanguage) {
    Locale myLocale = new Locale(localeLanguage);
    Resources res = getResources();
    DisplayMetrics dm = res.getDisplayMetrics();
    Configuration conf = res.getConfiguration();
    conf.locale = myLocale;
    res.updateConfiguration(conf, dm);
  }

  @Override
  public void onReceivedConfigData(AppConfigData appConfigData) {
    //save all configuration settings into a session manager
    SessionManager.get(getApplicationContext()).saveAppConfiguration(appConfigData);
    //save client id and secret key
    if (appConfigData.getPayment() != null) {
      SessionManager.get(getApplicationContext())
          .saveCredentials(appConfigData.getPayment().getClientid(),
              appConfigData.getPayment().getSecret());
    }
    if (mSplashViewModel.isSplashCompleted()) {
      showProgressBar(false);//stop progressbar
      redirectInApplication();
    }
  }

  @Override
  public void onFailConfigData(String errorMessage) {
    //call execute but fail will handle into this function
    //start show the retry option
    showProgressBar(false);//stop progressbar
    mSplashBinding.textViewRefresh.setVisibility(View.VISIBLE);
  }

  @Override
  public void onConfigurationException(String exceptionMessage) {
    showProgressBar(false);//stop progressbar
    // if call fail locally we handle inside this function
    DialogUtils.showDialog(SplashActivity.this, exceptionMessage,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            finish();
          }
        });
  }

  @Override
  public void onSplashCompleted() {
    if (mSplashViewModel.isSplashCompleted()) {
      showProgressBar(false);//stop progressbar
      redirectInApplication();
    }
  }

  @Override
  public void showProgressBar(boolean value) {
    mSplashBinding.refreshProgressBar.setVisibility(value ? View.VISIBLE : View.GONE);
  }
}
