package com.android.unideal.agent.view.dialog;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.databinding.CompleteJobRatingBinding;
import com.android.unideal.enums.AppMode;

/**
 * Created by ADMIN on 10-10-2016.
 */

public class JobRatingDialog extends DialogFragment {
  private static final String KEY_MODE = "mode";
  private static final String KEY_TITLE = "title";
  private CompleteJobRatingBinding mBinding;
  private OnClickListener mListener;
  private String jobRating;
  private AppMode ratingMode;
  private String title;

  public static JobRatingDialog getRatingDialog(AppMode appMode) {
    return getRatingDialog(appMode, null);
  }

  public static JobRatingDialog getRatingDialog(AppMode appMode, String title) {
    JobRatingDialog jobRatingDialog = new JobRatingDialog();
    Bundle mBundle = new Bundle();
    mBundle.putInt(KEY_MODE, appMode.getValue());
    if (!TextUtils.isEmpty(title)) {
      mBundle.putString(KEY_TITLE, title);
    }
    jobRatingDialog.setArguments(mBundle);
    return jobRatingDialog;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
    extractMode(getArguments());
  }

  private void extractMode(Bundle bundle) {
    if (bundle.containsKey(KEY_MODE)) {
      int mode = bundle.getInt(KEY_MODE);
      if (AppMode.AGENT.getValue() == mode) {
        ratingMode = AppMode.AGENT;
      } else if (AppMode.QUESTIONER.getValue() == mode) {
        ratingMode = AppMode.QUESTIONER;
      }
    } else {
      ratingMode = AppMode.AGENT;
    }
    if (bundle.containsKey(KEY_TITLE)) {
      title = bundle.getString(KEY_TITLE);
    }
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    mBinding = loadJobBinding(inflater, container);
    return mBinding.getRoot();
  }

  private CompleteJobRatingBinding loadJobBinding(LayoutInflater inflater, ViewGroup container) {
    return DataBindingUtil.inflate(inflater, R.layout.dialog_job_rating_agent, container, false);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (ratingMode == AppMode.AGENT) {
      if (TextUtils.isEmpty(title)) {
        mBinding.titleTextView.setText(R.string.title_rate_the_requester);
      } else {
        mBinding.titleTextView.setText(title);
      }
      mBinding.requesterRating.setVisibility(View.GONE);
      mBinding.agentRating.setVisibility(View.VISIBLE);
      mBinding.submitButtonAgent.setVisibility(View.VISIBLE);
      mBinding.submitButtonRequester.setVisibility(View.GONE);
      mBinding.submitButtonAgent.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          float rating = mBinding.agentRating.getRating();
          jobRating = String.valueOf(rating);
          mListener.submitRating(jobRating);
        }
      });
    } else if (ratingMode == AppMode.QUESTIONER) {
      if (TextUtils.isEmpty(title)) {
        mBinding.titleTextView.setText(R.string.title_rate_the_agent);
      } else {
        mBinding.titleTextView.setText(title);
      }
      mBinding.requesterRating.setVisibility(View.VISIBLE);
      mBinding.agentRating.setVisibility(View.GONE);
      mBinding.submitButtonRequester.setVisibility(View.VISIBLE);
      mBinding.submitButtonAgent.setVisibility(View.GONE);
      mBinding.submitButtonRequester.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          float rating = mBinding.requesterRating.getRating();
          jobRating = String.valueOf(rating);
          mListener.submitRating(jobRating);
        }
      });
    }
  }

  public void setOnJobRatingListener(OnClickListener mListener) {
    this.mListener = mListener;
  }

  public interface OnClickListener {
    void submitRating(String rating);
  }
}
