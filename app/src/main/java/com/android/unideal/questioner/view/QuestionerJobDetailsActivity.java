package com.android.unideal.questioner.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.android.unideal.R;
import com.android.unideal.data.Applicant;
import com.android.unideal.data.JobDetail;
import com.android.unideal.data.Status;
import com.android.unideal.databinding.QuerJobDetailsBinding;
import com.android.unideal.questioner.view.fragments.jobdetails.QuerJobDetailsFragment;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rtp.RunTimePermission;
import com.android.unideal.rtp.RunTimePermissionManager;
import com.android.unideal.util.Communicator;
import com.android.unideal.util.DialogUtils;
import com.jakewharton.rxbinding.view.RxView;
import java.util.HashMap;
import rx.Subscription;
import rx.functions.Action1;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by bhavdip on 10/16/16.
 *
 * Questioner Job Details handler, this would load the job details fragment
 * that can take care of the rest of the thing for load the different type
 * of the fragment.
 */

public class QuestionerJobDetailsActivity extends AppCompatActivity {

  public static final String KEY_JOB_ID = "Job_Id";
  private static final int REQ_STORAGE_PERMISSION = 722;
  public static String JOB_STATUS = "JOB_STATUS";
  private QuerJobDetailsBinding mJobDetailsBinding;
  private Subscription userSubscription;
  private Subscription applicantsSubscription;
  private int mJobId;
  private RunTimePermissionManager mRTManager;

  public static Intent getActivity(Context context, int jobId) {
    Intent detailsIntent = new Intent(context, QuestionerJobDetailsActivity.class);
    detailsIntent.putExtra(KEY_JOB_ID, jobId);
    return detailsIntent;
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == QuestionerJobDetailsActivity.RESULT_OK) {
      Log.d("QueJobDetailActivity", "onActivityResult: ");
      for (Fragment fragment : getSupportFragmentManager().getFragments()) {
        if (fragment != null) {
          fragment.onActivityResult(requestCode, resultCode, data);
        }
      }
    }
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mJobDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_quer_job_details);
    extractBundleData();
    bindingViews();
    configureRunTimePermission();
    loadJobDetailFragments();
  }

  /**
   * By Pass the on Reuqest Permission Result to the RTM Manager
   */
  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (mRTManager != null) {
      mRTManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    for (Fragment fragment : getSupportFragmentManager().getFragments()) {
      if (fragment != null) {
        fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
      }
    }
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    notifyToQuestionerHome();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    removeSubscriptionUser();
  }

  private void extractBundleData() {
    if (getIntent().hasExtra(KEY_JOB_ID)) {
      mJobId = getIntent().getIntExtra(KEY_JOB_ID, -1);
    }
  }

  private void bindingViews() {
    subScribeUseItemClick(mJobId);
    mJobDetailsBinding.toolbar.textViewTitle.setText(
        getString(R.string.title_job_id, String.valueOf(mJobId)));
    mJobDetailsBinding.toolbar.imageViewBack.setVisibility(View.VISIBLE);
    RxView.clicks(mJobDetailsBinding.toolbar.imageViewBack).subscribe(new Action1<Void>() {
      @Override
      public void call(Void aVoid) {
        notifyToQuestionerHome();
        finish();
      }
    });
  }

  private void loadJobDetailFragments() {
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.replace(R.id.detailsContainer, QuerJobDetailsFragment.getInstance(mJobId));
    fragmentTransaction.commit();
  }

  /**
   * Register the User Item Click Listener
   */
  private void subScribeUseItemClick(final int job_id) {
    userSubscription =
        Communicator.getCommunicator().subscribeUserItemClick().subscribe(new Action1<Applicant>() {
          @Override
          public void call(Applicant applicant) {
            // open profile screen only when job is in applied mode
            /*if (mJobData.getStatusEnum() == Status.OPEN) {
              openAgentProfileActivity(String.valueOf(applicant.getApplicantId()), job_id,
                  applicant);
            }*/
          }
        });

    applicantsSubscription = Communicator.getCommunicator()
        .subscribeApplicantAccept()
        .subscribe(new Action1<Applicant>() {
          @Override
          public void call(Applicant applicant) {
            //mJobData.setJob_status(RestFields.STATUS.IN_PROCESS);
            loadJobDetailFragments();
          }
        });
  }

  /**
   * Remove the subscription for listen the user item click
   */
  private void removeSubscriptionUser() {
    if (userSubscription != null) {
      userSubscription.unsubscribe();
    }
    if (applicantsSubscription != null) {
      applicantsSubscription.unsubscribe();
    }
  }

  /**
   * Call this method onCreate of the activity for configuration initialization
   */
  private void configureRunTimePermission() {
    mRTManager = new RunTimePermissionManager(this);
    mRTManager.registerCallback(new QuestionerJobDetailsActivity.RunTimeHandler());
  }

  /**
   * This is the entry point for start query for permission to the manager
   */
  public void permissionRequestList() {
    HashMap<String, String> permissionsMap = new HashMap<>();
    permissionsMap.put(Manifest.permission.WRITE_EXTERNAL_STORAGE,
        getResources().getString(R.string.rtm_storage_permission));
    mRTManager.buildPermissionList(permissionsMap);
  }

  private void notifyToQuestionerHome() {
    Communicator.getCommunicator().notifyQuestionerJob(Status.OPEN);
    Communicator.getCommunicator().notifyQuestionerJob(Status.IN_PROCESS);
    Communicator.getCommunicator().notifyQuestionerJob(Status.COMPLETED);
    Communicator.getCommunicator().notifyQuestionerJob(Status.PAUSED);
  }

  public class RunTimeHandler implements RunTimePermissionManager.callbackRunTimePermission {

    @Override
    public void showRationalDialog(String message) {
      DialogUtils.showDialog(QuestionerJobDetailsActivity.this, R.string.title_app_name, message,
          R.string.btn_ok, R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              mRTManager.askRunTimePermissions(REQ_STORAGE_PERMISSION);
            }
          });
    }

    @Override
    public void deniedPermission(String deniedPermission) {
    }

    @Override
    public void requestAllPermissionGranted() {
      // fire the run time permission is gratnted to all subscriber
      Communicator.getCommunicator()
          .sendRunTimePermissionUpdate(new RunTimePermission().setAllPermissionGranted(true));
    }
  }
}
