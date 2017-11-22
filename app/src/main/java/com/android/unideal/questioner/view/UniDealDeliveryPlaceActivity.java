package com.android.unideal.questioner.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import com.android.unideal.R;
import com.android.unideal.databinding.DeliveryPlaceBinding;
import com.android.unideal.enums.AppMode;
import com.android.unideal.util.AppUtility;
import com.android.unideal.util.SessionManager;

/**
 * Created by bhavdip on 19/1/17.
 */

public class UniDealDeliveryPlaceActivity extends AppCompatActivity {
  private static String KEY_APP_MODE = "appMode";
  private DeliveryPlaceBinding mDeliveryPlaceBinding;

  public static void startDeliveryPlaceActivity(Activity activity, AppMode appMode) {
    Intent deliveryPlace = new Intent(activity, UniDealDeliveryPlaceActivity.class);
    deliveryPlace.putExtra(KEY_APP_MODE, appMode.name());
    activity.startActivity(deliveryPlace);
  }

  private void changeStatusBarColor(int userType) {
    if (userType == 1) {
      int colorInt = ContextCompat.getColor(this, R.color.colorPersianGreen);
      AppUtility.changeStatusBarColor(this.getWindow(), colorInt);
    } else if (userType == 2) {
      int colorInt = ContextCompat.getColor(this, R.color.colorCuriousBlue);
      AppUtility.changeStatusBarColor(this.getWindow(), colorInt);
    }
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mDeliveryPlaceBinding =
        DataBindingUtil.setContentView(this, R.layout.activity_unidelivery_place);
    extractIntent();
    if (!TextUtils.isEmpty(SessionManager.get(this).getDeliveryPlaceURL())) {
      mDeliveryPlaceBinding.deliveryPlaceWebView.setWebViewClient(
          new UniDealDeliveryPlaceActivity.WebViewClient());
      mDeliveryPlaceBinding.deliveryPlaceWebView.loadUrl(
          SessionManager.get(this).getDeliveryPlaceURL());
    }
    mDeliveryPlaceBinding.imageViewBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
  }

  private void extractIntent() {
    if (getIntent().hasExtra(KEY_APP_MODE)) {
      String mode = getIntent().getStringExtra(KEY_APP_MODE);
      switch (AppMode.valueOf(mode)) {
        case AGENT: {
          mDeliveryPlaceBinding.deliveryPlaceToolbar.setBackgroundResource(R.color.colorCerulean);
          changeStatusBarColor(AppMode.AGENT.getValue());
          break;
        }
        case QUESTIONER: {
          mDeliveryPlaceBinding.deliveryPlaceToolbar.setBackgroundResource(
              R.color.colorPersianGreen);
          changeStatusBarColor(AppMode.QUESTIONER.getValue());
          break;
        }
      }
    }
  }

  private class WebViewClient extends android.webkit.WebViewClient {
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
      super.onPageStarted(view, url, favicon);
      mDeliveryPlaceBinding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
      super.onPageFinished(view, url);
      mDeliveryPlaceBinding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      // TODO Auto-generated method stub
      view.loadUrl(url);
      return true;
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
      view.loadUrl(request.getUrl().toString());
      return true;
    }
  }
}
