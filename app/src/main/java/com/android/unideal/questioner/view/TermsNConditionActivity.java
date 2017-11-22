package com.android.unideal.questioner.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import com.android.unideal.R;
import com.android.unideal.databinding.TermsConditionBinding;
import com.android.unideal.enums.AppMode;
import com.android.unideal.util.AppUtility;
import com.android.unideal.util.SessionManager;

public class TermsNConditionActivity extends AppCompatActivity {
  private static String KEY_APP_MODE = "appMode";
  private TermsConditionBinding binding;

  public static Intent startTermsNConditionActivity(Activity activity, AppMode appMode) {
    Intent tNcIntent = new Intent(activity, TermsNConditionActivity.class);
    tNcIntent.putExtra(KEY_APP_MODE, appMode.name());
    return tNcIntent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_terms_condition);
    extractIntent();
    if (!TextUtils.isEmpty(SessionManager.get(this).getTermsNConditionURL())) {
      binding.termsWebView.setWebViewClient(new WebViewClient());
      binding.termsWebView.loadUrl(SessionManager.get(this).getTermsNConditionURL());
    }
    binding.imageViewBack.setOnClickListener(new View.OnClickListener() {
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
          binding.termsToolbar.setBackgroundResource(R.color.colorCerulean);
          changeStatusBarColor(AppMode.AGENT.getValue());
          break;
        }
        case QUESTIONER: {
          binding.termsToolbar.setBackgroundResource(R.color.colorPersianGreen);
          changeStatusBarColor(AppMode.QUESTIONER.getValue());
          break;
        }
      }
    }
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

  private class WebViewClient extends android.webkit.WebViewClient {
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
      super.onPageStarted(view, url, favicon);
      binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
      super.onPageFinished(view, url);
      binding.progressBar.setVisibility(View.GONE);
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
