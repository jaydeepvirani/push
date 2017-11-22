package com.android.unideal.agent.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.unideal.R;
import com.android.unideal.chatting.view.ChattingFragment;
import com.android.unideal.data.Applicant;
import com.android.unideal.data.JobDetail;
import com.android.unideal.data.Status;
import com.android.unideal.databinding.ChatMessageBinding;
import com.android.unideal.enums.AppMode;
import com.android.unideal.util.AppUtility;
import com.jakewharton.rxbinding.view.RxView;
import rx.functions.Action1;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * The chatting activity between the agent and questioner.
 * It's open under the job details screen with the set of
 * request parameter based on that the chat screen is being
 * initialize
 */

public class JobMessageActivity extends AppCompatActivity {
  private static final String KEY_JOB = "JobData";
  private static final String APP_MODE = "AppMode";
  private static final String IS_OFFERED = "is_offered";
  private static final String APPLICANTS = "applicants";
  private ChatMessageBinding mChatMessageBinding;
  private JobDetail mRawJobData;
  private Applicant mApplicant;
  private AppMode mCurrentAppMode;
  private boolean isOffered;

  public static Intent getActivity(Context context, AppMode appMode, JobDetail jobDetails,
      boolean isOffered) {
    Intent messageIntent = new Intent(context, JobMessageActivity.class);
    messageIntent.putExtra(KEY_JOB, jobDetails);
    messageIntent.putExtra(APP_MODE, appMode.name());
    messageIntent.putExtra(IS_OFFERED, isOffered);
    return messageIntent;
  }

  /**
   *
   * @param context
   * @param appMode
   * @param jobDetails
   * @param applicant
   * @param isOffered
   * @return
   */
  public static Intent getActivity(Context context, AppMode appMode, JobDetail jobDetails,
      Applicant applicant, boolean isOffered) {
    Intent messageIntent = new Intent(context, JobMessageActivity.class);
    messageIntent.putExtra(KEY_JOB, jobDetails);
    messageIntent.putExtra(APP_MODE, appMode.name());
    messageIntent.putExtra(IS_OFFERED, isOffered);
    messageIntent.putExtra(APPLICANTS, applicant);
    return messageIntent;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mChatMessageBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat_message);
    extract();
    bindViews();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  @Override
  public void onBackPressed() {
    handleBackPress();
  }

  private void extract() {
    if (getIntent().hasExtra(KEY_JOB)) {
      mRawJobData = getIntent().getParcelableExtra(KEY_JOB);
    }
    if (getIntent().hasExtra(APP_MODE)) {
      mCurrentAppMode = AppMode.valueOf(getIntent().getStringExtra(APP_MODE));
    }

    if (getIntent().hasExtra(APPLICANTS)) {
      mApplicant = getIntent().getParcelableExtra(APPLICANTS);
    }

    isOffered = getIntent().getBooleanExtra(IS_OFFERED, false);
  }

  /**
   * This will bind the layout components and load the chatting fragment
   */
  private void bindViews() {
    if (mRawJobData != null) {
      if (mCurrentAppMode == AppMode.AGENT) {
        setTitle(mChatMessageBinding.agentActionBar.textViewTitle);
        setBackButton(mChatMessageBinding.agentActionBar.imageViewBack);
        mChatMessageBinding.requesterActionBar.getRoot().setVisibility(View.GONE);
      } else {
        setTitle(mChatMessageBinding.requesterActionBar.textViewTitle);
        setBackButton(mChatMessageBinding.requesterActionBar.imageViewBack);
        mChatMessageBinding.agentActionBar.getRoot().setVisibility(View.GONE);
      }
      changeStatusBarColor(mCurrentAppMode.getValue());
      loadChattingFragment(mRawJobData);
    }
  }

  private void setTitle(TextView textView) {
    Status status = mRawJobData.getStatusEnum();
    if (status == Status.APPLIED) {
      textView.setText(getText(R.string.title_msg_center));
    } else if (status == Status.IN_PROCESS) {
      //Enable the media icon
      textView.setText(getText(R.string.title_job_center));
    } else if (status == Status.PAUSED) {
      //Enable the media icon
      textView.setText(getText(R.string.title_dispute_center));
    } else {
      textView.setText(getText(R.string.title_job_center));
    }
  }

  private void changeStatusBarColor(int userType) {
    if (userType == 1) {
      int colorInt = ContextCompat.getColor(this, R.color.colorPersianGreen);
      AppUtility.changeStatusBarColor(getWindow(), colorInt);
    } else if (userType == 2) {
      int colorInt = ContextCompat.getColor(this, R.color.colorCuriousBlue);
      AppUtility.changeStatusBarColor(getWindow(), colorInt);
    }
  }

  private void setBackButton(ImageView backButton) {
    backButton.setVisibility(View.VISIBLE);
    RxView.clicks(backButton).subscribe(new Action1<Void>() {
      @Override
      public void call(Void aVoid) {
        handleBackPress();
      }
    });
  }

  private void loadChattingFragment(JobDetail newJob) {
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.messageContainer,
            ChattingFragment.getFragment(mCurrentAppMode, newJob, mApplicant, isOffered))
        .commit();
  }

  private void handleBackPress() {
    setResult(RESULT_OK);
    finish();
  }
}
