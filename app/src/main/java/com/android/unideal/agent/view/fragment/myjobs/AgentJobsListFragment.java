package com.android.unideal.agent.view.fragment.myjobs;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.agent.adapter.MyJobsListAdapter;
import com.android.unideal.data.JobDetail;
import com.android.unideal.data.Status;
import com.android.unideal.databinding.AgentJobsListBinding;
import com.android.unideal.rest.CallbackWrapper;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import com.android.unideal.util.Communicator;
import com.android.unideal.util.SessionManager;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by bhavdip on 10/1/16.
 */

public class AgentJobsListFragment extends Fragment {

  public final static String KEY_STATUS = "status";
  private AgentJobsListBinding mDataBinding;
  private MyJobsListAdapter mJobListAdapter;
  private Status mJobStatus;
  private Subscription refreshSubscription;

  public static Fragment getInstance(Status status) {
    AgentJobsListFragment listFragment = new AgentJobsListFragment();
    Bundle mBundle = new Bundle();
    mBundle.putString(KEY_STATUS, status.name());
    listFragment.setArguments(mBundle);
    return listFragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    extractIntentData();
    subscribeRefresh();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mDataBinding = loadJobsListBinding(inflater, container);
    return mDataBinding.getRoot();
  }

  private AgentJobsListBinding loadJobsListBinding(LayoutInflater inflater,
      @Nullable ViewGroup container) {
    return DataBindingUtil.inflate(inflater, R.layout.fragment_agent_jobs_list, container, false);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    startBindingViews();
  }

  /**
   * Extract the intent bundle data inside the argument
   * Its content the Job Status
   */
  private void extractIntentData() {
    if (getArguments().containsKey(KEY_STATUS)) {
      mJobStatus = Status.valueOf(getArguments().getString(KEY_STATUS));
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    unSubScribeRefresh();
  }

  private void subscribeRefresh() {
    refreshSubscription =
        Communicator.getCommunicator().subRefreshAgentJob().subscribe(new Action1<Status>() {
          @Override
          public void call(Status requestStatus) {
            //Notes we check the current status and request status matched then refresh it
            if (mJobStatus.getStatusName() == requestStatus.getStatusName()) {
              int jobStatus = JobDetail.convertFromEnam(mJobStatus);
              fetchJobList(jobStatus);
            }
          }
        });
  }

  private void unSubScribeRefresh() {
    if (refreshSubscription != null) {
      refreshSubscription.unsubscribe();
    }
  }

  /**
   * Start Binding RecyclerView Adapter
   */
  private void startBindingViews() {
    mDataBinding.JobsList.setLayoutManager(new LinearLayoutManager(getActivity()));
    mDataBinding.JobsList.setHasFixedSize(true);
    mJobListAdapter = new MyJobsListAdapter(getActivity());
    mDataBinding.JobsList.setAdapter(mJobListAdapter);
    int jobStatus = JobDetail.convertFromEnam(mJobStatus);
    fetchJobList(jobStatus);
  }

  private void fetchJobList(int jobStatus) {
    if (getActivity() == null) {
      return;
    }
    showProgressBar();
    int userId = SessionManager.get(getActivity()).getUserId();
    HashMap<String, String> hashMap = new HashMap<>();
    hashMap.put(RestFields.KEY_USER_ID, String.valueOf(userId));
    hashMap.put(RestFields.KEY_JOB_STATUS, String.valueOf(jobStatus));
    hashMap.put(RestFields.KEY_USER_TYPE, String.valueOf(RestFields.USER_TYPE_AGENT));
    Call<GenericResponse<List<JobDetail>>> call = RestClient.get().getJobList(hashMap);
    call.enqueue(new CallbackWrapper<List<JobDetail>>() {
      @Override
      public void onSuccess(GenericResponse<List<JobDetail>> response) {
        hideProgressBar();
        hideEmptyView();
        mJobListAdapter.addJobList(response.getData());
      }

      @Override
      public void onFailure(GenericResponse<List<JobDetail>> response) {
        mJobListAdapter.addJobList(Collections.<JobDetail>emptyList());
        hideProgressBar();
        showEmptyView(response.getMessage());
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        hideProgressBar();
        showEmptyView("No data found");
        mJobListAdapter.addJobList(Collections.<JobDetail>emptyList());
      }
    });
  }

  private void hideEmptyView() {
    mDataBinding.emptyView.setVisibility(View.GONE);
  }

  private void showEmptyView(String emptyMessage) {
    mDataBinding.emptyView.setVisibility(View.VISIBLE);
    mDataBinding.emptyView.setText(emptyMessage);
  }

  private void showProgressBar() {
    mDataBinding.progressBar.setVisibility(View.VISIBLE);
    mDataBinding.emptyView.setVisibility(View.VISIBLE);
  }

  private void hideProgressBar() {
    mDataBinding.progressBar.setVisibility(View.GONE);
  }
}
