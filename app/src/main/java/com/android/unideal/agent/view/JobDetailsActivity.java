package com.android.unideal.agent.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.android.unideal.R;
import com.android.unideal.agent.view.fragment.jobdetails.JobDetailsFragment;
import com.android.unideal.data.Status;
import com.android.unideal.data.UserDetail;
import com.android.unideal.flurry.FlurryManager;
import com.android.unideal.util.Communicator;
import com.android.unideal.util.SessionManager;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by bhavdip on 3/10/16.
 */
public class JobDetailsActivity extends AppCompatActivity {

  public static final String KEY_RAW_JOB = "rawData";

  /**
   * It will handle the both fragments for open and running job
   * based on define Mode. Pass the Job Model while start the
   * activity. It will care the job model data, for display
   * the details of the job using it.
   */
  public static Intent getActivity(Context context, int jobId) {
    Intent mIntent = new Intent(context, JobDetailsActivity.class);
    mIntent.putExtra(KEY_RAW_JOB, jobId);
    return mIntent;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    DataBindingUtil.setContentView(this, R.layout.activity_job_details);
    extractIntentData();
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  private void extractIntentData() {
    if (getIntent().hasExtra(KEY_RAW_JOB)) {
      int jobId = getIntent().getIntExtra(KEY_RAW_JOB, -1);
      //Flurry
      final UserDetail userDetail = SessionManager.get(this).getActiveUser();
      FlurryManager.agentViewJob(userDetail.getUserId(), String.valueOf(jobId));

      jobDetailsFragment(jobId);
    } else {
      finish();
    }
  }

  private void jobDetailsFragment(int requestJobData) {
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.main_content, JobDetailsFragment.getInstance(requestJobData))
        .commit();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    notifyToAgentJobListHome();
  }

  private void notifyToAgentJobListHome() {
    Communicator.getCommunicator().notifyAgentJob(Status.OPEN);
    Communicator.getCommunicator().notifyAgentJob(Status.IN_PROCESS);
    Communicator.getCommunicator().notifyAgentJob(Status.COMPLETED);
    Communicator.getCommunicator().notifyAgentJob(Status.PAUSED);
  }
}
