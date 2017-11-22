package com.android.unideal.questioner.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import com.android.unideal.R;
import com.android.unideal.databinding.DisclaimerDataBinding;
import com.android.unideal.enums.AppMode;
import com.android.unideal.util.SessionManager;
import com.jakewharton.rxbinding.view.RxView;
import rx.functions.Action1;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by ADMIN on 24-10-2016.
 */

public class DisclaimerActivity extends AppCompatActivity {
  public static String TNC_TYPE = "termsUrl";
  private static String KEY_APP_MODE = "appMode";
  private DisclaimerDataBinding mBinding;
  private String tncUserType = "userTnc";
  private String tncAgtApply = "agtApply";
  private String tncQueCreate = "createJob";

  public static Intent getDialogActivity(Activity activity, AppMode appMode, String tncType) {
    Intent proposalIntent = new Intent(activity, DisclaimerActivity.class);
    proposalIntent.putExtra(KEY_APP_MODE, appMode.name());
    proposalIntent.putExtra(TNC_TYPE, tncType);
    return proposalIntent;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mBinding = DataBindingUtil.setContentView(this, R.layout.activity_disclaimer);
    getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

    if (getIntent().hasExtra(TNC_TYPE)) {
      extractTncType();
    }
  }

  private void extractTncType() {
    if (getIntent().hasExtra(TNC_TYPE)) {
      String type = getIntent().getStringExtra(TNC_TYPE);
      if (type.equals(tncUserType)) {
        loadTncUrl(SessionManager.get(this).getTermsNConditionURL());
      } else if (type.equals(tncAgtApply)) {
        loadTncUrl(SessionManager.get(this).getAgtTermsUrl());
      } else if (type.equals(tncQueCreate)) {
        loadTncUrl(SessionManager.get(this).getQueCrJobUrl());
      } else {
        loadTncUrl(SessionManager.get(this).getTermsNConditionURL());
      }
    }
  }

  private void loadTncUrl(String tncUrl) {
    changeAcptButtonColor();

    if (!TextUtils.isEmpty(tncUrl)) {
      mBinding.webView.setWebViewClient(new WebViewClient());
      mBinding.webView.loadUrl(tncUrl);
    }
    mBinding.close.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
    mBinding.decline.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
    bindViews();
  }

  private void changeAcptButtonColor() {
    if (getIntent().hasExtra(KEY_APP_MODE)) {
      String mode = getIntent().getStringExtra(KEY_APP_MODE);
      Log.d("DescActivitty", "changeAcptButtonColor: " + mode);
      switch (AppMode.valueOf(mode)) {
        case AGENT: {
          mBinding.accept.getBackground()
              .setColorFilter(ContextCompat.getColor(this, R.color.colorCerulean),
                  PorterDuff.Mode.SRC_ATOP);
          mBinding.close.setBackgroundResource(R.drawable.ic_close_desclaimer_agent);
          break;
        }
        case QUESTIONER: {
          mBinding.accept.getBackground()
              .setColorFilter(ContextCompat.getColor(this, R.color.colorPersianGreen),
                  PorterDuff.Mode.SRC_ATOP);
          mBinding.close.setBackgroundResource(R.drawable.ic_close_desclaimer_que);
          break;
        }
      }
    }
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  private void bindViews() {
    RxView.clicks(mBinding.accept).subscribe(new Action1<Void>() {
      @Override
      public void call(Void aVoid) {
        setResult(DisclaimerActivity.RESULT_OK);
        finish();
      }
    });
  }

  public class WebViewClient extends android.webkit.WebViewClient {
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
      super.onPageStarted(view, url, favicon);
      mBinding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
      super.onPageFinished(view, url);
      mBinding.progressBar.setVisibility(View.GONE);
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
