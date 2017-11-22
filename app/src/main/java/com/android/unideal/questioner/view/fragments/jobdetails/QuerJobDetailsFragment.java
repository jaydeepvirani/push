package com.android.unideal.questioner.view.fragments.jobdetails;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;
import com.android.unideal.R;
import com.android.unideal.chatting.view.ChattingFragment;
import com.android.unideal.data.JobDetail;
import com.android.unideal.data.Status;
import com.android.unideal.databinding.QJobDetailsBinding;
import com.android.unideal.enums.AppMode;
import com.android.unideal.questioner.view.QuestionerJobDetailsActivity;
import com.android.unideal.util.Consts;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.SessionManager;
import com.jakewharton.rxbinding.view.RxView;
import rx.functions.Action1;

/**
 * Created by bhavdip on 10/16/16.
 * Questioner job details fragment that hold all type of different fragment
 * based on the job details status.
 * 1. Details 2.Applicants 3.Job Center 4. dispute Center
 */

public class QuerJobDetailsFragment extends Fragment implements ApplicantCountListener {

  public static final String KEY_JOB_ID = "job_id";
  private MaterialIntroView materialIntroView;
  private JobDetail chooseJobDetails;
  private int chooseJobId;
  private QJobDetailsBinding mDetailsBinding;
  private String FOCUS_APPLICANTS = "applicant";

  public static Fragment getInstance(int selectedJob) {
    Bundle mBundle = new Bundle();
    mBundle.putInt(KEY_JOB_ID, selectedJob);
    QuerJobDetailsFragment fragment = new QuerJobDetailsFragment();
    fragment.setArguments(mBundle);
    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments().containsKey(KEY_JOB_ID)) {
      chooseJobId = getArguments().getInt(KEY_JOB_ID);
    }
  }

  /**
   * By Pass the on Reuqest Permission Result to the RTM Manager
   */
  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    for (Fragment fragment : getChildFragmentManager().getFragments()) {
      if (fragment != null) {
        fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
      }
    }
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mDetailsBinding =
        DataBindingUtil.inflate(inflater, R.layout.fragment_quer_job_details, container, false);
    return mDetailsBinding.getRoot();
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    loadTopTabs();
    jobDetailsFragment();
  }

  public void updateJobDetails(JobDetail updatedJobDetails) {
    this.chooseJobDetails = updatedJobDetails;
    loadTopTabs();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == QuestionerJobDetailsActivity.RESULT_OK) {
      Log.d("QuerJobDetailsFragment", "onActivityResult: ");
    }
  }

  /**
   *
   */
  private void loadTopTabs() {
    if (chooseJobDetails == null) {
      return;
    }
    //default Details fragment load
    mDetailsBinding.textViewDetails.setVisibility(View.VISIBLE);
    mDetailsBinding.textViewDetails.setSelected(true);
    Status status = chooseJobDetails.getStatusEnum();
    //TODO java.lang.NullPointerException
    switch (status) {
      case OPEN: {
        tabOpen();
        break;
      }
      case IN_PROCESS: {
        tabInProgress();
        break;
      }
      case COMPLETED: {
        tabCompleted();
        break;
      }
      case PAUSED: {
        tabPaused();
        break;
      }
      case CANCELLED: {
        tabCanceled();
        break;
      }
    }
  }

  private void tabOpen() {
    if (SessionManager.get(getActivity()).shouldShowTourGuide() == Consts.SHOWCASE_GUIDE_SHOW) {
      showCaseLayout();
    }
    mDetailsBinding.textViewApplicants.setVisibility(View.VISIBLE);
    RxView.clicks(mDetailsBinding.textViewDetails).subscribe(new Action1<Void>() {
      @Override
      public void call(Void aVoid) {
        mDetailsBinding.textViewApplicants.setSelected(false);
        mDetailsBinding.textViewDetails.setSelected(true);
        jobDetailsFragment();
      }
    });

    RxView.clicks(mDetailsBinding.textViewApplicants).subscribe(new Action1<Void>() {
      @Override
      public void call(Void aVoid) {
        mDetailsBinding.textViewApplicants.setSelected(true);
        mDetailsBinding.textViewDetails.setSelected(false);
        jobApplicantsList();
      }
    });
  }

  private void showCaseLayout() {
    materialIntroView = new MaterialIntroView.Builder(getActivity()).enableDotAnimation(true)
        .setFocusGravity(FocusGravity.CENTER)
        .dismissOnTouch(Consts.dismissOnTouch)
        .enableIcon(false)
        .setFocusType(Focus.MINIMUM)
        .setShape(ShapeType.CIRCLE)
        .setDelayMillis(200)
        .enableFadeAnimation(true)
        .performClick(false)
        .setInfoText(getString(R.string.txt_show_case_appplicant))
        .setTarget(mDetailsBinding.textViewApplicants)
        .setUsageId(FOCUS_APPLICANTS) //THIS SHOULD BE UNIQUE ID
        .show();
  }

  private void tabInProgress() {
    mDetailsBinding.textViewJobCenter.setVisibility(View.VISIBLE);
    RxView.clicks(mDetailsBinding.textViewDetails).subscribe(new Action1<Void>() {
      @Override
      public void call(Void aVoid) {
        mDetailsBinding.textViewDetails.setSelected(true);
        mDetailsBinding.textViewJobCenter.setSelected(false);
        jobDetailsFragment();
      }
    });

    RxView.clicks(mDetailsBinding.textViewJobCenter).subscribe(new Action1<Void>() {
      @Override
      public void call(Void aVoid) {
        mDetailsBinding.textViewDetails.setSelected(false);
        mDetailsBinding.textViewJobCenter.setSelected(true);
        if (chooseJobDetails.getMsg_thread_id() != null) {
          jobCenterFragment();
        } else {
          DialogUtils.showToast(getActivity(), getString(R.string.error_thread_id));
          mDetailsBinding.textViewDetails.setSelected(true);
          mDetailsBinding.textViewJobCenter.setSelected(false);
          jobDetailsFragment();
        }
      }
    });
  }

  private void tabCompleted() {
    mDetailsBinding.textViewJobCenter.setVisibility(View.VISIBLE);
    RxView.clicks(mDetailsBinding.textViewDetails).subscribe(new Action1<Void>() {
      @Override
      public void call(Void aVoid) {
        mDetailsBinding.textViewDetails.setSelected(true);
        mDetailsBinding.textViewJobCenter.setSelected(false);
        jobDetailsFragment();
      }
    });
    RxView.clicks(mDetailsBinding.textViewJobCenter).subscribe(new Action1<Void>() {
      @Override
      public void call(Void aVoid) {
        mDetailsBinding.textViewDetails.setSelected(false);
        mDetailsBinding.textViewJobCenter.setSelected(true);
        jobCenterFragment();
      }
    });
  }

  private void tabCanceled() {
    mDetailsBinding.textViewJobCenter.setVisibility(View.VISIBLE);
    RxView.clicks(mDetailsBinding.textViewDetails).subscribe(new Action1<Void>() {
      @Override
      public void call(Void aVoid) {
        mDetailsBinding.textViewDetails.setSelected(true);
        mDetailsBinding.textViewJobCenter.setSelected(false);
        jobDetailsFragment();
      }
    });
    RxView.clicks(mDetailsBinding.textViewJobCenter).subscribe(new Action1<Void>() {
      @Override
      public void call(Void aVoid) {
        mDetailsBinding.textViewDetails.setSelected(false);
        mDetailsBinding.textViewJobCenter.setSelected(true);
        jobCenterFragment();
      }
    });
  }

  private void tabPaused() {
    if (chooseJobDetails.getIs_dispute() == 1) {
      mDetailsBinding.textViewDisputeCenter.setVisibility(View.VISIBLE);
      mDetailsBinding.textViewJobCenter.setVisibility(View.GONE);
      RxView.clicks(mDetailsBinding.textViewDetails).subscribe(new Action1<Void>() {
        @Override
        public void call(Void aVoid) {
          mDetailsBinding.textViewDetails.setSelected(true);
          mDetailsBinding.textViewDisputeCenter.setSelected(false);
          jobDetailsFragment();
        }
      });
      RxView.clicks(mDetailsBinding.textViewDisputeCenter).subscribe(new Action1<Void>() {
        @Override
        public void call(Void aVoid) {
          mDetailsBinding.textViewDetails.setSelected(false);
          mDetailsBinding.textViewDisputeCenter.setSelected(true);
          jobCenterFragment();
        }
      });
    } else {
      mDetailsBinding.textViewDisputeCenter.setVisibility(View.GONE);
      mDetailsBinding.textViewJobCenter.setVisibility(View.VISIBLE);
      RxView.clicks(mDetailsBinding.textViewDetails).subscribe(new Action1<Void>() {
        @Override
        public void call(Void aVoid) {
          mDetailsBinding.textViewDetails.setSelected(false);
          mDetailsBinding.textViewJobCenter.setSelected(true);
          jobDetailsFragment();
        }
      });
      RxView.clicks(mDetailsBinding.textViewJobCenter).subscribe(new Action1<Void>() {
        @Override
        public void call(Void aVoid) {
          mDetailsBinding.textViewDetails.setSelected(false);
          mDetailsBinding.textViewJobCenter.setSelected(true);
          jobCenterFragment();
        }
      });
    }
  }

  private void jobDetailsFragment() {
    FragmentManager fragmentManager = getChildFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    Fragment detailsFragment = fragmentManager.findFragmentByTag("Details");
    if (detailsFragment == null) {
      if (chooseJobDetails == null) {
        detailsFragment = DetailsFragment.getInstance(chooseJobId);
      } else {
        detailsFragment = DetailsFragment.getInstance(chooseJobDetails.getJob_id());
      }
    }
    fragmentTransaction.replace(R.id.container, detailsFragment, "Details");
    fragmentTransaction.commit();
  }

  private void jobCenterFragment() {
    FragmentManager fragmentManager = getChildFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    Fragment msgFragment = fragmentManager.findFragmentByTag("ChatFragment");
    if (msgFragment == null) {
      msgFragment = ChattingFragment.getFragment(AppMode.QUESTIONER, chooseJobDetails, true);
    }
    fragmentTransaction.replace(R.id.container, msgFragment, "ChatFragment");
    fragmentTransaction.commit();
  }

  private void jobApplicantsList() {
    FragmentManager fragmentManager = getChildFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    Fragment applicantsFragment = fragmentManager.findFragmentByTag("ApplicantsFragment");
    if (applicantsFragment == null) {
      applicantsFragment = ApplicantsFragment.getInstance(chooseJobDetails);
    }
    fragmentTransaction.replace(R.id.container, applicantsFragment, "ApplicantsFragment");
    fragmentTransaction.commit();
  }

  @Override
  public void showApplicantCount(int applicantCount) {
    if (applicantCount > 1) {
      mDetailsBinding.textViewApplicants.setText(
          getString(R.string.applicantsSize, applicantCount));
    } else {
      mDetailsBinding.textViewApplicants.setText(getString(R.string.applicantSize, applicantCount));
    }
  }
}
