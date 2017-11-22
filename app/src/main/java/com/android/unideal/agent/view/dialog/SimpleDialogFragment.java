package com.android.unideal.agent.view.dialog;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.databinding.SimpleContentBinding;

/**
 * Created by ADMIN on 10-10-2016.
 */

public class SimpleDialogFragment extends DialogFragment {
  private final static String KEY_STR_TITLE = "str_title";
  private final static String KEY_INT_TITLE = "int_title";
  private SimpleContentBinding mBinding;
  private OnClickListener mListener;
  private String titleContent;

  public static SimpleDialogFragment getDialogFragment(String title) {
    SimpleDialogFragment dialogFragment = new SimpleDialogFragment();
    Bundle mBundle = new Bundle();
    mBundle.putString(KEY_STR_TITLE, title);
    dialogFragment.setArguments(mBundle);
    return dialogFragment;
  }

  public static SimpleDialogFragment getDialogFragment(@StringRes int resourceId) {
    SimpleDialogFragment dialogFragment = new SimpleDialogFragment();
    Bundle mBundle = new Bundle();
    mBundle.putInt(KEY_INT_TITLE, resourceId);
    dialogFragment.setArguments(mBundle);
    return dialogFragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
    extractBundleData();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    mBinding = loadCompleteBinding(inflater, container);
    if (!TextUtils.isEmpty(titleContent)) {
      mBinding.textViewContentTitle.setText(titleContent);
    }
    mBinding.yesButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mListener.onPositiveClick();
      }
    });
    mBinding.noButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        cancelDialog();
      }
    });
    return mBinding.getRoot();
  }

  private SimpleContentBinding loadCompleteBinding(LayoutInflater inflater, ViewGroup container) {
    return DataBindingUtil.inflate(inflater, R.layout.dialog_simple_fragment, container, false);
  }

  private void cancelDialog() {
    dismiss();
  }

  public void setPositiveClickListener(OnClickListener mListener) {
    this.mListener = mListener;
  }

  private void extractBundleData() {
    if (getArguments().containsKey(KEY_STR_TITLE)) {
      titleContent = getArguments().getString(KEY_STR_TITLE);
    }
    if (getArguments().containsKey(KEY_INT_TITLE)) {
      titleContent = getResources().getString(getArguments().getInt(KEY_INT_TITLE));
    }
  }

  public interface OnClickListener {
    void onPositiveClick();
  }
}
