package com.android.unideal.agent.view.fragment;

import android.annotation.TargetApi;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import com.android.unideal.R;
import com.android.unideal.agent.viewmodel.HelpFragmentViewModel;
import com.android.unideal.databinding.AgentHelpBinding;
import com.android.unideal.util.AppUtility;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.SessionManager;

/**
 * Created by ADMIN on 19-10-2016.
 */

public class HelpFragment extends Fragment implements HelpFragmentViewModel.AgentHelpListener {

  private AgentHelpBinding mBinding;
  private HelpFragmentViewModel mViewModel;

  public static Fragment getInstance() {
    return new HelpFragment();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_help_agent, container, false);
    mViewModel = new HelpFragmentViewModel(getActivity(), this);
    if (AppUtility.isNetworkConnected(getContext())) {
      if (!TextUtils.isEmpty(SessionManager.get(getContext()).getHelpUrl())) {
        String languageMode = SessionManager.get(getActivity()).getLanguageMode();
        mBinding.webView.setWebViewClient(new WebViewClient());
        mBinding.webView.loadUrl(SessionManager.get(getContext()).getHelpUrl()+"?language="+languageMode);
      }
    } else {
      DialogUtils.showDialog(getContext(), R.string.no_internet_available);
    }
    return mBinding.getRoot();
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
