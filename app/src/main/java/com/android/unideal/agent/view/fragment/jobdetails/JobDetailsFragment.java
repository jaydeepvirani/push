package com.android.unideal.agent.view.fragment.jobdetails;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.android.unideal.ImageViewerActivity;
import com.android.unideal.R;
import com.android.unideal.agent.view.JobMessageActivity;
import com.android.unideal.agent.view.ProposalActivity;
import com.android.unideal.agent.view.QuestionerProfileActivity;
import com.android.unideal.agent.view.dialog.JobRatingDialog;
import com.android.unideal.agent.view.dialog.SimpleDialogFragment;
import com.android.unideal.agent.viewmodel.JobDetailsViewModel;
import com.android.unideal.data.JobDetail;
import com.android.unideal.data.Status;
import com.android.unideal.data.UserDetail;
import com.android.unideal.databinding.ActionSheetBinding;
import com.android.unideal.databinding.AgentJobDetailsBinding;
import com.android.unideal.enums.AppMode;
import com.android.unideal.notification.NotificationHelper;
import com.android.unideal.questioner.view.DisclaimerActivity;
import com.android.unideal.rest.RestFields;
import com.android.unideal.util.AppUtility;
import com.android.unideal.util.Communicator;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.SessionManager;
import com.jakewharton.rxbinding.view.RxView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import rx.functions.Action1;

/**
 * Created by bhavdip on 6/10/16.
 * This will manage the all kind of status's job details
 */
public class JobDetailsFragment extends Fragment
    implements JobDetailsViewModel.JobDetailsViewHandler {
  public static final String KEY_JOB_DETAILS = "JobDetail";
  private static final String TAG = "JobDetailsFragment";
  private static final String KEY_JOB_ID = "JobID";
  private static final int REQUEST_PROPOSAL = 310;
  private JobDetail mJobDetail;
  private AgentJobDetailsBinding mDetailsBinding;
  private BottomSheetDialog mBottomSheetDialog;
  private ActionSheetBinding mSheetBinding;
  private JobDetailsViewModel mDetailsViewModel;
  private int jobId = -1;
  // this field update when the proposal has been send and response is positive
  private boolean isJobApplied = false;

  public static Fragment getInstance(int jobId) {
    JobDetailsFragment fragment = new JobDetailsFragment();
    Bundle mBundle = new Bundle();
    mBundle.putInt(KEY_JOB_ID, jobId);
    fragment.setArguments(mBundle);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mDetailsViewModel = new JobDetailsViewModel(getActivity(), this);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mDetailsBinding = loadBinding(inflater, container);
    mDetailsBinding.setViewmodel(mDetailsViewModel);
    return mDetailsBinding.getRoot();
  }

  private AgentJobDetailsBinding loadBinding(LayoutInflater inflater, ViewGroup container) {
    return DataBindingUtil.inflate(inflater, R.layout.fragment_agent_jobs_details, container,
        false);
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mDetailsViewModel.onActivityCreated();
  }

  /**
   * 1. start Binding views
   */
  @Override public void startBindingViews() {
    handleBackButton();
    handleClickListeners();
  }

  /**
   * 1.1 Handle Back Button
   */
  private void handleBackButton() {
    mDetailsBinding.actionBar.imageViewBack.setVisibility(View.VISIBLE);
    RxView.clicks(mDetailsBinding.actionBar.imageViewBack).subscribe(new Action1<Void>() {
      @Override public void call(Void aVoid) {
        if (isJobApplied) {
          getActivity().setResult(Activity.RESULT_OK);
          notifyToAgentJobListHome();
          getActivity().finish();
        } else {
          getActivity().setResult(Activity.RESULT_CANCELED);
          notifyToAgentJobListHome();
          getActivity().finish();
        }
      }
    });
  }

  private void notifyToAgentJobListHome() {
    Communicator.getCommunicator().notifyAgentJob(Status.OPEN);
    Communicator.getCommunicator().notifyAgentJob(Status.IN_PROCESS);
    Communicator.getCommunicator().notifyAgentJob(Status.COMPLETED);
    Communicator.getCommunicator().notifyAgentJob(Status.PAUSED);
  }

  /**
   * 1.2 Handle Click Listener
   */
  private void handleClickListeners() {
    final UserDetail userDetail = SessionManager.get(getActivity()).getActiveUser();
    RxView.clicks(mDetailsBinding.btnApply).subscribe(new Action1<Void>() {
      @Override public void call(Void aVoid) {

        if (userDetail.getFromSocialNetwork() == 1 && TextUtils.isEmpty(
            userDetail.getEmailAddress())) {
          DialogUtils.showDialog(getActivity(), R.string.title_app_name,
              getString(R.string.text_email_no_available), R.string.text_continue,
              R.string.btn_cancel, new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                  //check credit card available or not
                  startProposalActivity();
                }
              });
        }else{
          startProposalActivity();
        }
      }
    });

    RxView.clicks(mDetailsBinding.btnJobCenter).subscribe(new Action1<Void>() {
      @Override public void call(Void aVoid) {
        // Job Center with full mode
        Intent intentMessages =
            JobMessageActivity.getActivity(getActivity(), AppMode.AGENT, mJobDetail, true);
        startActivity(intentMessages);
      }
    });

    RxView.clicks(mDetailsBinding.btnDisputeCenter).subscribe(new Action1<Void>() {
      @Override public void call(Void aVoid) {
        // Job Center with full mode
        Intent intentMessages =
            JobMessageActivity.getActivity(getActivity(), AppMode.AGENT, mJobDetail, true);
        startActivity(intentMessages);
      }
    });

    RxView.clicks(mDetailsBinding.btnMsgCenter).subscribe(new Action1<Void>() {
      @Override public void call(Void aVoid) {
        // only chatting mode
        // Job Center with full mode
        if (mJobDetail == null) {
          AppUtility.showToast(getActivity(), R.string.error_job_not_found);
          return;
        }
        if (TextUtils.isEmpty(mJobDetail.getMsg_thread_id())) {
          AppUtility.showToast(getActivity(), R.string.error_thread_id);
          return;
        }
        Intent intentMessages =
            JobMessageActivity.getActivity(getActivity(), AppMode.AGENT, mJobDetail, true);
        startActivity(intentMessages);
      }
    });

    RxView.clicks(mDetailsBinding.textViewClient).subscribe(new Action1<Void>() {
      @Override public void call(Void aVoid) {
        Intent intent = QuestionerProfileActivity.getInstance(getActivity(),
            String.valueOf(mJobDetail.getUser_id()));
        startActivity(intent);
      }
    });
    RxView.clicks(mDetailsBinding.btnSettings).subscribe(new Action1<Void>() {
      @Override public void call(Void aVoid) {
        showActionSheet();
      }
    });
  }

  private void startProposalActivity() {
    Intent sendProposalIntent =
        ProposalActivity.getDialogActivity(getActivity(), mJobDetail.getJob_id(),
            mJobDetail.getUser_id());
    startActivityForResult(sendProposalIntent, REQUEST_PROPOSAL);
  }

  /**
   * 2. This will load a job details
   */
  @Override public void loadJobDetails() {
    if (getArguments().containsKey(KEY_JOB_ID)) {
      jobId = getArguments().getInt(KEY_JOB_ID, -1);
      if (jobId == -1) {
        return;
      }
      int userId = SessionManager.get(getActivity()).getUserId();
      mDetailsBinding.actionBar.textViewTitle.setText(
          String.format(Locale.getDefault(), "#%d", jobId));
      mDetailsViewModel.loadJobDetails(userId, jobId, RestFields.USER_TYPE_AGENT);
      clearNotifications();
    }
  }

  private void clearNotifications() {
    Activity activity = getActivity();
    if (activity == null) {
      return;
    }
    NotificationHelper.clearNotification(activity, jobId);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_PROPOSAL) {
      if (resultCode == Activity.RESULT_OK) {
        JobDetail tempJobDetails = data.getParcelableExtra(KEY_JOB_DETAILS);
        //msg_user_name
        mJobDetail.setMsg_user_name(tempJobDetails.getMsg_user_name());
        //msg_thread_id
        mJobDetail.setMsg_thread_id(tempJobDetails.getMsg_thread_id());
        //msg_user_id
        mJobDetail.setMsg_user_id(tempJobDetails.getMsg_user_id());
        //msg_user_profile_url
        mJobDetail.setMsg_user_profile_url(tempJobDetails.getMsg_user_profile_url());

        showAppliedJobOption();
        getActivity().setResult(Activity.RESULT_OK);
        isJobApplied = true;
      }
    }
  }

  @Override public void onJobDetailRetrieved(JobDetail jobDetail) {
    mJobDetail = jobDetail;
    if (mJobDetail != null) {
      mDetailsViewModel.bindingModel(mJobDetail);
      showImages();
      refreshBottomMenuOption();
      setUpBottomSheetDialog();
    }
  }

  /**
   * Agent Job Details -> Bottom Menu options
   */
  private void refreshBottomMenuOption() {
    Status currentStatus = mJobDetail.getStatusEnum();
    if (currentStatus == Status.OPEN) {
      // open
      showOpenJobOption();
    } else if (currentStatus == Status.APPLIED) {
      // Applied
      showAppliedJobOption();
    } else if (currentStatus == Status.IN_PROCESS) {
      //In Process
      showInProcessJobOption();
    } else if (currentStatus == Status.COMPLETED) {
      showJobCompleteOption();
    } else if (currentStatus == Status.PAUSED) {
      // paused
      showJobPauseOption();
    } else if (currentStatus == Status.CANCELLED) {
      // canceled
      showJobCancelledOption();
    }
  }

  private void showAppliedJobOption() {
    mDetailsBinding.btnApply.setVisibility(View.VISIBLE);
    mDetailsBinding.btnApply.setText(getResources().getString(R.string.btn_text_applied));
    mDetailsBinding.btnApply.setAlpha(0.4f);
    mDetailsBinding.btnApply.setEnabled(false);
    mDetailsBinding.btnMsgCenter.setVisibility(View.VISIBLE);
    mDetailsBinding.msgBoard.setVisibility(View.GONE);
    mDetailsBinding.sectionRating.setVisibility(View.GONE);
  }

  private void showOpenJobOption() {
    mDetailsBinding.btnApply.setVisibility(View.VISIBLE);
    mDetailsBinding.msgBoard.setVisibility(View.GONE);
    mDetailsBinding.btnMsgCenter.setVisibility(View.GONE);
    mDetailsBinding.sectionRating.setVisibility(View.GONE);
  }

  private void showInProcessJobOption() {
    mDetailsBinding.btnJobCenter.setVisibility(View.VISIBLE);
    mDetailsBinding.btnSettings.setVisibility(View.VISIBLE);
    mDetailsBinding.btnDisputeCenter.setVisibility(View.GONE);
    mDetailsBinding.msgBoard.setVisibility(View.GONE);
    mDetailsBinding.btnMsgCenter.setVisibility(View.GONE);
    if (mJobDetail != null && mJobDetail.getIs_agent_confirm() == 1) {
      mDetailsBinding.btnSettings.setAlpha(0.4f);
      mDetailsBinding.btnSettings.setEnabled(false);
    } else {
      mDetailsBinding.btnSettings.setAlpha(1f);
      mDetailsBinding.btnSettings.setEnabled(true);
    }
  }

  private void showJobCompleteOption() {
    // completed
    mDetailsBinding.msgCompleted.setVisibility(View.VISIBLE);
    mDetailsBinding.sectionRating.setVisibility(View.VISIBLE);
  }

  private void showJobPauseOption() {
    mDetailsBinding.btnSettings.setVisibility(View.VISIBLE);
    mDetailsBinding.msgBoard.setVisibility(View.GONE);
    mDetailsBinding.btnJobCenter.setVisibility(View.GONE);
    mDetailsBinding.btnMsgCenter.setVisibility(View.GONE);
    //Note: If job has been paused by questioner we would not allow to pause this job
    // For that we check before show the Settings option whether agent has been confirm
    // to do on pause mode.
    if (mJobDetail.getIs_dispute() == 0) {
      mDetailsBinding.msgBoard.setVisibility(View.VISIBLE);
      mDetailsBinding.msgPaused.setVisibility(View.VISIBLE);
      mDetailsBinding.btnJobCenter.setVisibility(View.VISIBLE);
    } else {
      mDetailsBinding.msgBoard.setVisibility(View.VISIBLE);
      mDetailsBinding.msgDispute.setVisibility(View.VISIBLE);
      mDetailsBinding.msgDispute.setVisibility(View.VISIBLE);
      mDetailsBinding.btnDisputeCenter.setVisibility(View.VISIBLE);
    }

    if (mJobDetail.getIs_agent_confirm() == 1) {
      mDetailsBinding.btnSettings.setAlpha(1f);
      mDetailsBinding.btnSettings.setEnabled(true);
    } else {
      mDetailsBinding.btnSettings.setAlpha(0.4f);
      mDetailsBinding.btnSettings.setEnabled(false);
    }
  }

  private void showJobCancelledOption() {
    mDetailsBinding.msgCancelled.setVisibility(View.VISIBLE);
    mDetailsBinding.btnJobCenter.setVisibility(View.VISIBLE);
  }

  private void setUpBottomSheetDialog() {
    if (mBottomSheetDialog == null) {
      if (getActivity() == null) {
        return;
      }
      mBottomSheetDialog = new BottomSheetDialog(getActivity());

      mSheetBinding = DataBindingUtil.inflate(getActivity().getLayoutInflater(),
          R.layout.bottom_sheet_job_action, null, false);
      mBottomSheetDialog.setContentView(mSheetBinding.getRoot());
    }

    // Pause
    RxView.clicks(mSheetBinding.btnJobPause).subscribe(new Action1<Void>() {
      @Override public void call(Void aVoid) {
        hideActionSheet();
        confirmPausedDialog();
      }
    });
    // Complete
    RxView.clicks(mSheetBinding.btnJobComplete).subscribe(new Action1<Void>() {
      @Override public void call(Void aVoid) {
        hideActionSheet();
        confirmJobCompleteDialog();
      }
    });
    // Resume
    RxView.clicks(mSheetBinding.btnJobResume).subscribe(new Action1<Void>() {
      @Override public void call(Void aVoid) {
        hideActionSheet();
        confirmResumeJobDialog();
      }
    });

    refreshBottomSheet();
  }

  private void refreshBottomSheet() {
    Status currentStatus = mJobDetail.getStatusEnum();
    if (currentStatus == Status.IN_PROCESS) {
      //Notes : In agent during in process mode,
      // we would not allow request to complete a job
      // :-) Feature changes : Now Agent can complete the job.
      if (mJobDetail.getIs_questioner_confirm() == 1) {
        mSheetBinding.btnJobPause.setAlpha(0.4f);
        mSheetBinding.btnJobPause.setEnabled(false);
      } else {
        mSheetBinding.btnJobPause.setAlpha(1f);
        mSheetBinding.btnJobPause.setEnabled(true);
      }
      mSheetBinding.PauseLayout.setVisibility(View.VISIBLE);
      mSheetBinding.CompleteLayout.setVisibility(View.VISIBLE);
      mSheetBinding.ResumeLayout.setVisibility(View.GONE);
    } else if (currentStatus == Status.PAUSED) {
      mSheetBinding.PauseLayout.setVisibility(View.GONE);
      mSheetBinding.CompleteLayout.setVisibility(View.GONE);
      mSheetBinding.ResumeLayout.setVisibility(View.VISIBLE);
    }
    if (currentStatus == Status.PAUSED) {
      LinearLayout.LayoutParams params =
          new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
              LinearLayout.LayoutParams.WRAP_CONTENT);
      params.setMargins(0, AppUtility.dpToPx(getActivity(), R.dimen.action_btn_padding), 0,
          AppUtility.dpToPx(getActivity(), R.dimen.action_btn_padding));
      mSheetBinding.btnJobResume.setLayoutParams(params);
    }
  }

  private void confirmJobCompleteDialog() {
    final SimpleDialogFragment simpleDialogFragment =
        SimpleDialogFragment.getDialogFragment(R.string.dialog_title_complete_job);
    simpleDialogFragment.setPositiveClickListener(new SimpleDialogFragment.OnClickListener() {
      @Override public void onPositiveClick() {
        simpleDialogFragment.dismiss();
        showRatingCompleteJob();
      }
    });
    simpleDialogFragment.show(getFragmentManager(), null);
  }

  private void showRatingCompleteJob() {
    final JobRatingDialog jobRatingDialog = JobRatingDialog.getRatingDialog(AppMode.AGENT);
    jobRatingDialog.setCancelable(false);
    jobRatingDialog.setOnJobRatingListener(new JobRatingDialog.OnClickListener() {
      @Override public void submitRating(String rating) {
        jobRatingDialog.dismiss();
        mDetailsViewModel.requestUpdateJobStatus(mJobDetail, RestFields.STATUS.COMPLETED, rating);
      }
    });
    jobRatingDialog.show(getFragmentManager(), null);
  }

  private void showActionSheet() {
    mBottomSheetDialog.show();
  }

  /**
   * 1. Pause Job Dialog
   */
  private void confirmPausedDialog() {
    final SimpleDialogFragment simpleDialogFragment =
        SimpleDialogFragment.getDialogFragment(R.string.dialog_title_pause_job);
    simpleDialogFragment.setPositiveClickListener(new SimpleDialogFragment.OnClickListener() {
      @Override public void onPositiveClick() {
        simpleDialogFragment.dismiss();
        mDetailsViewModel.requestUpdateJobStatus(mJobDetail, RestFields.STATUS.PAUSED);
      }
    });
    simpleDialogFragment.show(getFragmentManager(), null);
  }

  /**
   * *3. Re-Active Job
   */
  private void confirmResumeJobDialog() {
    final SimpleDialogFragment simpleDialogFragment =
        SimpleDialogFragment.getDialogFragment(R.string.dialog_title_re_activate);
    simpleDialogFragment.setPositiveClickListener(new SimpleDialogFragment.OnClickListener() {
      @Override public void onPositiveClick() {
        simpleDialogFragment.dismiss();
        mDetailsViewModel.requestUpdateJobStatus(mJobDetail, RestFields.STATUS.RESUMED);
      }
    });
    simpleDialogFragment.show(getFragmentManager(), null);
  }

  private void hideActionSheet() {
    mBottomSheetDialog.hide();
  }

  private void showImages() {
    if (mJobDetail != null && mJobDetail.getFiles() != null && mJobDetail.getFiles().size() > 0) {
      int imageWidth = getResources().getDimensionPixelSize(R.dimen.image_width);
      int imageHeight = getResources().getDimensionPixelSize(R.dimen.image_height);
      int imageMargin = getResources().getDimensionPixelOffset(R.dimen.job_image_margin);
      List<String> list = mJobDetail.getFiles();
      int size = list.size();
      for (int i = 0; i < size; i++) {
        String imageUrl = list.get(i);
        ImageView imageView = generateImage(i, imageUrl, imageWidth, imageHeight, imageMargin);
        mDetailsBinding.imageContent.addView(imageView);
      }
    }
  }

  private ImageView generateImage(int position, String imageUrl, int imageWidth, int imageHeight,
      int imageMargin) {
    ImageView imageView = new ImageView(getActivity());
    final LinearLayout.LayoutParams layoutParams =
        new LinearLayout.LayoutParams(imageWidth, imageHeight);
    layoutParams.setMargins(imageMargin, 0, imageMargin, 0);
    imageView.setLayoutParams(layoutParams);
    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    imageView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorOsloGray));
    imageView.setTag(position);
    imageView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (getActivity() == null) {
          return;
        }
        if (mJobDetail != null) {
          int position = (int) v.getTag();
          List<String> list = mJobDetail.getFiles();
          if (list != null && list.size() > 0) {
            Intent intent = ImageViewerActivity.getActivityIntent(getActivity(), position,
                new ArrayList<>(list), AppMode.AGENT);
            startActivity(intent);
          }
        }
      }
    });
    Picasso.with(getActivity()).load(imageUrl).into(imageView);
    return imageView;
  }

  @Override public void showProgressBar() {
    mDetailsBinding.progressBarLayout.setVisibility(View.VISIBLE);
    mDetailsBinding.contentLayout.setVisibility(View.GONE);
  }

  @Override public void hideProgressBar() {
    mDetailsBinding.progressBarLayout.setVisibility(View.GONE);
    mDetailsBinding.contentLayout.setVisibility(View.VISIBLE);
  }

  @Override public void showToast(String message) {
    AppUtility.showToast(getActivity(), message);
  }

  @Override public void onStatusUpdate(String message, int requestStatus) {
    showToast(message);
    switch (requestStatus) {
      case RestFields.STATUS.PAUSED: {
        mJobDetail.setJob_status(RestFields.STATUS.PAUSED);
        refreshBottomMenuOption();
        refreshBottomSheet();
        break;
      }
      case RestFields.STATUS.RESUMED: {
        mJobDetail.setJob_status(RestFields.STATUS.RESUMED);
        refreshBottomMenuOption();
        refreshBottomSheet();
        break;
      }
      case RestFields.STATUS.COMPLETED: {
        mJobDetail.setJob_status(RestFields.STATUS.COMPLETED);
        //Notes : This will notify into the My Job Fragment
        Communicator.getCommunicator().notifyQuestionerJob(Status.IN_PROCESS);
        getActivity().finish();
        break;
      }
    }
  }

  @Override public void onStatusUpdateFail(String message, int requestStatus) {
    showToast(message);
  }

  @Override public void onStatusUpdateException(String message, int requestStatus) {
    showToast(message);
  }
}
