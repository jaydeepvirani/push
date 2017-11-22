package com.android.unideal;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;
import com.android.unideal.flurry.FlurryManager;
import com.android.unideal.util.AppBuildConfig;
import com.social.fb.FaceBookHelper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by ADMIN on 19-09-2016.
 */

public class UniDealApplication extends Application {
  private static UniDealApplication uniDealApplication;

  static {
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
  }

  public static UniDealApplication getApplication() {
    return uniDealApplication;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    uniDealApplication = this;
    configureFontStyle();
    FaceBookHelper.init(this, AppBuildConfig.FB_APP_ID);
    FlurryManager.loadManager().initManager(getApplicationContext());
  }

  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(this);
  }

  private void configureFontStyle() {
    CalligraphyConfig.initDefault(
        new CalligraphyConfig.Builder().setDefaultFontPath(getString(R.string.font_Avenir_Book))
            .setFontAttrId(R.attr.fontPath)
            .build());
  }
}
