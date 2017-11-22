package com.android.unideal.auth.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import com.android.unideal.R;
import com.android.unideal.agent.view.WalkThroughActivity;
import com.android.unideal.auth.viewmodel.LanguageActivityViewModel;
import com.android.unideal.databinding.LanguageBinding;
import com.android.unideal.rest.RestClient;
import com.android.unideal.util.Consts;
import com.android.unideal.util.SessionManager;
import java.util.Locale;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by ADMIN on 19-09-2016.
 */

public class LanguageActivity extends AppCompatActivity
    implements LanguageActivityViewModel.LanguageListener {
  private static final String TAG = "LanguageActivity";
  private static final int REQUEST_LANGUAGE = 29;
  private static final int ANIMATE_TIME = 400;
  private LanguageBinding binding;
  private LanguageActivityViewModel viewModel;
  private boolean isEnglishSelected = false;
  private boolean isChineseSelected = false;

  public static Intent startLanguageActivity(Activity activity) {
    Intent intent = new Intent(activity, LanguageActivity.class);
    return intent;
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_LANGUAGE) {
      if (resultCode == Activity.RESULT_OK) {
        finish();
      }
    }
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_language);
    viewModel = new LanguageActivityViewModel(this, this);
    binding.setViewmodel(viewModel);
    String defaultLang = Locale.getDefault().getDisplayLanguage();
    if (defaultLang.equals(Consts.SYSTEM_CHINESE)) {
      binding.viewLayout.post(new Runnable() {
        @Override
        public void run() {
          animateEngToChinese(0);
          setChineseLanguage();
        }
      });
    } else {
      binding.viewLayout.post(new Runnable() {
        @Override
        public void run() {
          animateChineseToEng(0);
          setEnglishLanguage();
        }
      });
    }
    binding.hongKong.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        if (!isChineseSelected) {
          animateEngToChinese(ANIMATE_TIME);
          setChineseLanguage();
        }
      }
    });

    binding.english.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        if (!isEnglishSelected) {
          animateChineseToEng(ANIMATE_TIME);
          setEnglishLanguage();
        }
      }
    });
  }

  private void animateEngToChinese(int duration) {
    TranslateAnimation animate =
        new TranslateAnimation(0, binding.hongKong.getX() - binding.english.getX(), 0,
            binding.hongKong.getY() - binding.english.getY());
    animate.setFillAfter(true);
    animate.setDuration(duration);
    animate.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {
        Log.d(TAG, "onAnimationStart: ");
        binding.english.setTextColor(
            ContextCompat.getColor(LanguageActivity.this, R.color.colorPersianGreen));

        binding.hongKong.setTextColor(
            ContextCompat.getColor(LanguageActivity.this, R.color.colorWhite));
      }

      @Override
      public void onAnimationEnd(Animation animation) {
        Log.d(TAG,
            "onAnimationEnd: " + binding.viewLayout.getX() + "," + binding.viewLayout.getY());
        binding.viewLayout.setVisibility(View.VISIBLE);
        //binding.hongKong.setTextColor(
        //    ContextCompat.getColor(LanguageActivity.this, R.color.colorWhite));
      }

      @Override
      public void onAnimationRepeat(Animation animation) {
        Log.d(TAG, "onAnimationRepeat: ");
      }
    });
    binding.viewLayout.startAnimation(animate);
  }

  private void animateChineseToEng(int duration) {
    TranslateAnimation animate =
        new TranslateAnimation(binding.hongKong.getX() - binding.english.getX(), 0,
            binding.hongKong.getY() - binding.english.getY(), 0);
    animate.setFillAfter(true);
    animate.setDuration(duration);
    animate.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {
        Log.d(TAG, "onAnimationStart: ");
        binding.hongKong.setTextColor(
            ContextCompat.getColor(LanguageActivity.this, R.color.colorPersianGreen));

        binding.english.setTextColor(
            ContextCompat.getColor(LanguageActivity.this, R.color.colorWhite));
      }

      @Override
      public void onAnimationEnd(Animation animation) {
        Log.d(TAG,
            "onAnimationEnd: " + binding.viewLayout.getX() + "," + binding.viewLayout.getY());
        //binding.english.setTextColor(
        //    ContextCompat.getColor(LanguageActivity.this, R.color.colorWhite));
        binding.viewLayout.setVisibility(View.VISIBLE);
      }

      @Override
      public void onAnimationRepeat(Animation animation) {
        Log.d(TAG, "onAnimationRepeat: ");
      }
    });
    binding.viewLayout.startAnimation(animate);
  }

  public void setEnglishLanguage() {
    isEnglishSelected = true;
    isChineseSelected = false;
  }

  public void setChineseLanguage() {
    isChineseSelected = true;
    isEnglishSelected = false;
  }

  @Override
  public void moveToLogInScreen() {
    startLogInActivity();
  }

  private void startLogInActivity() {
    if (isChineseSelected) {
      SessionManager.get(LanguageActivity.this).setLanguageMode(Consts.CHINESE_MODE);
      RestClient.resetClient();
      changeLanguages(Consts.LOCALE_CHINESE);
    } else if (isEnglishSelected) {
      SessionManager.get(LanguageActivity.this).setLanguageMode(Consts.ENGLISH_MODE);
      RestClient.resetClient();
      changeLanguages(Consts.LOCALE_ENGLISH);
    } else {
      SessionManager.get(LanguageActivity.this).setLanguageMode(Consts.ENGLISH_MODE);
      RestClient.resetClient();
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
    this.getApplicationContext().getResources().updateConfiguration(conf, null);
    Intent intent = new Intent(this, WalkThroughActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivityForResult(intent, REQUEST_LANGUAGE);
  }
}
