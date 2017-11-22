package com.android.unideal.questioner.view.fragments.jobdetails;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.android.unideal.ImageViewerActivity;
import com.android.unideal.R;
import com.android.unideal.agent.view.dialog.JobRatingDialog;
import com.android.unideal.agent.view.dialog.SimpleDialogFragment;
import com.android.unideal.data.Applicant;
import com.android.unideal.data.JobDetail;
import com.android.unideal.data.Status;
import com.android.unideal.databinding.ActionSheetBinding;
import com.android.unideal.databinding.JobDetailsBinding;
import com.android.unideal.enums.AppMode;
import com.android.unideal.questioner.adapter.AwardedAgentListAdapter;
import com.android.unideal.questioner.view.UniDealDeliveryPlaceActivity;
import com.android.unideal.questioner.viewmodel.DetailsFragmentViewModel;
import com.android.unideal.rest.RestFields;
import com.android.unideal.util.AppUtility;
import com.android.unideal.util.Communicator;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.SessionManager;
import com.jakewharton.rxbinding.view.RxView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import rx.functions.Action1;

/**
 * Created by bhavdip on 17/10/16.
 */
public class DetailsFragment extends Fragment
    implements DetailsFragmentViewModel.DetailsModelListener,
    AwardedAgentListAdapter.ApplicantsListener {

  public static final String KEY_JOB_ID = "job_id";
  private JobDetailsBinding mJobDetailsBinding;
  private BottomSheetDialog mBottomSheetDialog;
  private ActionSheetBinding mSheetBinding;
  private DetailsFragmentViewModel mViewModel;
  private AwardedAgentListAdapter mAgentListAdapter;
  private int jobId;
  private JobDetail mJobDetail;
  private ApplicantCountListener mApplicantCountListener;

  public static Fragment getInstance(int jobId) {
    Bundle mBundle = new Bundle();
    mBundle.putInt(KEY_JOB_ID, jobId);
    DetailsFragment fragment = new DetailsFragment();
    fragment.setArguments(mBundle);
    return fragment;
  }

  @TargetApi(23)
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    onAttachToContext();
  }

  @SuppressWarnings("deprecation")
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      onAttachToContext();
    }
  }

  private void onAttachToContext() {
    if (getParentFragment() instanceof ApplicantCountListener) {
      mApplicantCountListener = (ApplicantCountListener) getParentFragment();
    } else {
      throw new RuntimeException("Parent fragment must implement ApplicantCountListener ");
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments().containsKey(KEY_JOB_ID)) {
      jobId = getArguments().getInt(KEY_JOB_ID);
    }
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mJobDetailsBinding = getJobDetailsBinding(inflater, container);
    mViewModel = new DetailsFragmentViewModel(this, getActivity());
    mJobDetailsBinding.setViewmodel(mViewModel);
    return mJobDetailsBinding.getRoot();
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mViewModel.onActivityCreated();
  }

  /**
   *
   * @param inflater
   * @param container
   * @return
   */
  private JobDetailsBinding getJobDetailsBinding(LayoutInflater inflater,
      @Nullable ViewGroup container) {
    return DataBindingUtil.inflate(inflater, R.layout.fragment_job_details, container, false);
  }

  @Override
  public void startViewBinding() {
    int userId = SessionManager.get(getActivity()).getUserId();
    mViewModel.loadJobDetails(userId, jobId, RestFields.USER_TYPE_REQUESTER);
  }

  public void sendUpdateJobDetails(JobDetail updateJobDetails) {
    if (getParentFragment() != null) {
      ((QuerJobDetailsFragment) getParentFragment()).updateJobDetails(updateJobDetails);
    }
  }

  @Override
  public void onJobApplicantList(List<Applicant> data) {
    mAgentListAdapter.addParticipant(data);
    mAgentListAdapter.notifyDataSetChanged();
  }

  @Override
  public void showEmptyView(String message) {
    mJobDetailsBinding.emptyView.setVisibility(View.VISIBLE);
    mJobDetailsBinding.emptyView.setText(message);
  }

  @Override
  public void showProgressBar() {
    DialogUtils.getInstance().showProgressDialog(getActivity());
  }

  @Override
  public void hideProgressBar() {
    DialogUtils.getInstance().hideProgressDialog();
  }

  @Override
  public void showToast(String message) {
    AppUtility.showToast(getActivity(), message);
  }

  @Override
  public void showAlert(String message) {
    DialogUtils.showDialog(getActivity(), message);
  }

  @Override
  public void onJobDetailRetrieved(JobDetail jobDetail) {
    mJobDetail = jobDetail;
    sendUpdateJobDetails(jobDetail);
    updateApplicantCount(jobDetail.getApplicant());
    showImages();
    Status status = mJobDetail.getStatusEnum();
    switch (status) {
      case IN_PROCESS:
        awardedApplicantsList();
        break;
    }
    loadJobDetailsSettings(status);
  }

  private void updateApplicantCount(int count) {
    if (mApplicantCountListener != null) {
      mApplicantCountListener.showApplicantCount(count);
    }
  }

  /**
   * Is the input job status in progress mode
   * we start showing the agent list along with
   * the selected agent as marked true
   */
  private void awardedApplicantsList() {
    mJobDetailsBinding.sectionApplicants.setVisibility(View.VISIBLE);
    mJobDetailsBinding.applicantList.setHasFixedSize(true);
    mJobDetailsBinding.applicantList.setNestedScrollingEnabled(false);
    mJobDetailsBinding.applicantList.setLayoutManager(new LinearLayoutManager(getContext()));
    mAgentListAdapter = new AwardedAgentListAdapter(this);
    mJobDetailsBinding.applicantList.setAdapter(mAgentListAdapter);

    mViewModel.getInProgressApplicantList(String.valueOf(jobId));
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
        mJobDetailsBinding.imageContent.addView(imageView);
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
      @Override
      public void onClick(View v) {
        if (getActivity() == null) {
          return;
        }
        if (mJobDetail != null) {
          int position = (int) v.getTag();
          List<String> list = mJobDetail.getFiles();
          if (list != null && list.size() > 0) {
            Intent intent = ImageViewerActivity.getActivityIntent(getActivity(), position,
                new ArrayList<>(list), AppMode.QUESTIONER);
            startActivity(intent);
          }
        }
      }
    });
    Picasso.with(getActivity()).load(imageUrl).into(imageView);
    return imageView;
  }

  private void showActionSheet() {
    mBottomSheetDialog.show();
  }

  private void hideActionSheet() {
    mBottomSheetDialog.hide();
  }

  private void loadJobDetailsSettings(Status status) {
    switch (status) {
      case IN_PROCESS: {
        if (mJobDetail != null && mJobDetail.getIs_questioner_confirm() == 1) {
          mJobDetailsBinding.btnJobSettings.setAlpha(0.4f);
          mJobDetailsBinding.btnJobSettings.setEnabled(false);
        } else {
          mJobDetailsBinding.btnJobSettings.setAlpha(1f);
          mJobDetailsBinding.btnJobSettings.setEnabled(true);
        }
        if (mJobDetail.getIs_dispute() == 0) {
          mJobDetailsBinding.btnJobSettings.setVisibility(View.VISIBLE);
        }
        break;
      }
      case PAUSED: {
        //Note: If job has been paused by questioner then we would allow to Resume this job
        // For that we check before show the Settings option whether agent/questioner has been confirm
        // to do on pause mode.
        mJobDetailsBinding.btnJobSettings.setVisibility(View.VISIBLE);
        if (mJobDetail.getIs_dispute() == 1) {
          //show the dispute message enable
          mJobDetailsBinding.msgDispute.setVisibility(View.VISIBLE);
        } else {
          mJobDetailsBinding.msgDispute.setVisibility(View.GONE);
        }
        if (mJobDetail != null && mJobDetail.getIs_questioner_confirm() == 1) {
          mJobDetailsBinding.btnJobSettings.setAlpha(1f);
          mJobDetailsBinding.btnJobSettings.setEnabled(true);
        } else {
          mJobDetailsBinding.btnJobSettings.setAlpha(0.4f);
          mJobDetailsBinding.btnJobSettings.setEnabled(false);
        }
        break;
      }
      case COMPLETED: {
        mJobDetailsBinding.btnJobSettings.setVisibility(View.GONE);
      }
    }
    configureBottomSheet(status);
    RxView.clicks(mJobDetailsBinding.btnJobSettings).subscribe(new Action1<Void>() {
      @Override
      public void call(Void aVoid) {
        showActionSheet();
      }
    });
  }

  private void configureBottomSheet(Status status) {
    if (mBottomSheetDialog == null) {
      mBottomSheetDialog = new BottomSheetDialog(getActivity());
      mSheetBinding = loadSheetBinding();
      mBottomSheetDialog.setContentView(mSheetBinding.getRoot());
    }
    if (status == Status.IN_PROCESS) {
      //In process mode

      if (mJobDetail.getIs_agent_confirm() == 1) {
        mSheetBinding.btnJobPause.setAlpha(0.4f);
        mSheetBinding.btnJobPause.setEnabled(false);
      } else {
        mSheetBinding.btnJobPause.setAlpha(1f);
        mSheetBinding.btnJobPause.setEnabled(true);
      }
      mSheetBinding.CompleteLayout.setVisibility(View.VISIBLE);
      mSheetBinding.PauseLayout.setVisibility(View.VISIBLE);
      mSheetBinding.ResumeLayout.setVisibility(View.GONE);
    } else if (status == Status.PAUSED) {
      mSheetBinding.ResumeLayout.setVisibility(View.VISIBLE);
      mSheetBinding.CompleteLayout.setVisibility(View.GONE);
      mSheetBinding.PauseLayout.setVisibility(View.GONE);
    }
    if (status == Status.IN_PROCESS) {
      RxView.clicks(mSheetBinding.btnJobPause).subscribe(new Action1<Void>() {
        @Override
        public void call(Void aVoid) {
          hideActionSheet();
          confirmJobPauseDialog();
        }
      });
      RxView.clicks(mSheetBinding.btnJobComplete).subscribe(new Action1<Void>() {
        @Override
        public void call(Void aVoid) {
          hideActionSheet();
          confirmJobCompletedDialog();
        }
      });
    }
    //2. Job in Paused Mode
    if (status == Status.PAUSED) {

      LinearLayout.LayoutParams params =
          new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
              LinearLayout.LayoutParams.WRAP_CONTENT);
      params.setMargins(0, AppUtility.dpToPx(getActivity(), R.dimen.action_btn_padding), 0,
          AppUtility.dpToPx(getActivity(), R.dimen.action_btn_padding));
      mSheetBinding.btnJobResume.setLayoutParams(params);

      RxView.clicks(mSheetBinding.btnJobResume).subscribe(new Action1<Void>() {
        @Override
        public void call(Void aVoid) {
          hideActionSheet();
          confirmJobResumeDialog();
        }
      });
    }
  }

  private ActionSheetBinding loadSheetBinding() {
    return DataBindingUtil.inflate(getActivity().getLayoutInflater(),
        R.layout.bottom_sheet_job_action, null, false);
  }

  /**
   * When a Requester Try to Complete A JOB
   */
  private void confirmJobCompletedDialog() {
    final SimpleDialogFragment simpleDialogFragment =
        SimpleDialogFragment.getDialogFragment(R.string.dialog_title_complete_job);
    simpleDialogFragment.setPositiveClickListener(new SimpleDialogFragment.OnClickListener() {
      @Override
      public void onPositiveClick() {
        simpleDialogFragment.dismiss();
        showRatingCompleteJob();
      }
    });
    simpleDialogFragment.show(getFragmentManager(), null);
  }

  /**
   * A Requester Try to Pause a job
   */
  private void confirmJobPauseDialog() {
    final SimpleDialogFragment simpleDialogFragment =
        SimpleDialogFragment.getDialogFragment(R.string.dialog_title_pause_job);
    simpleDialogFragment.setPositiveClickListener(new SimpleDialogFragment.OnClickListener() {
      @Override
      public void onPositiveClick() {
        simpleDialogFragment.dismiss();
        mViewModel.requestUpdateJobStatus(mJobDetail, RestFields.STATUS.PAUSED);
      }
    });
    simpleDialogFragment.show(getFragmentManager(), null);
  }

  /**
   * When A Requester Try to Resume/Reactive a Job
   */
  private void confirmJobResumeDialog() {
    final SimpleDialogFragment simpleDialogFragment =
        SimpleDialogFragment.getDialogFragment(R.string.dialog_title_re_activate);
    simpleDialogFragment.setPositiveClickListener(new SimpleDialogFragment.OnClickListener() {
      @Override
      public void onPositiveClick() {
        simpleDialogFragment.dismiss();
        mViewModel.requestUpdateJobStatus(mJobDetail, RestFields.STATUS.RESUMED);
      }
    });
    simpleDialogFragment.show(getFragmentManager(), null);
  }

  private void showRatingCompleteJob() {
    final JobRatingDialog jobRatingDialog = JobRatingDialog.getRatingDialog(AppMode.QUESTIONER);
    jobRatingDialog.setCancelable(false);
    jobRatingDialog.setOnJobRatingListener(new JobRatingDialog.OnClickListener() {
      @Override
      public void submitRating(String rating) {
        jobRatingDialog.dismiss();
        mViewModel.requestUpdateJobStatus(mJobDetail, RestFields.STATUS.COMPLETED, rating);
      }
    });
    jobRatingDialog.show(getFragmentManager(), null);
  }

  @Override
  public void onStatusUpdate(String message, int requestStatus) {
    showToast(message);
    switch (requestStatus) {
      case RestFields.STATUS.PAUSED: {
        mJobDetail.setJob_status(RestFields.STATUS.PAUSED);
        loadJobDetailsSettings(Status.PAUSED);
        Communicator.getCommunicator().notifyQuestionerJob(Status.PAUSED);
        break;
      }
      case RestFields.STATUS.RESUMED: {
        mJobDetail.setJob_status(RestFields.STATUS.IN_PROCESS);
        loadJobDetailsSettings(Status.IN_PROCESS);
        Communicator.getCommunicator().notifyQuestionerJob(Status.IN_PROCESS);
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

  @Override
  public void onStatusUpdateFail(String message, int requestStatus) {
    showToast(message);
  }

  @Override
  public void onStatusUpdateException(String message, int requestStatus) {
    showToast(message);
  }

  @Override
  public void onDeliveryClick() {
    UniDealDeliveryPlaceActivity.startDeliveryPlaceActivity(getActivity(), AppMode.QUESTIONER);
  }
}
