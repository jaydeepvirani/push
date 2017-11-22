package com.android.unideal.questioner.view.fragments.myjobs;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.data.JobDetail;
import com.android.unideal.data.Status;
import com.android.unideal.databinding.QuesJobListDataBinding;
import com.android.unideal.enums.AppMode;
import com.android.unideal.questioner.adapter.QuestionerJobListAdapter;
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
 * Created by bhavdip on 10/13/16.
 */

public class JobsListFragment extends Fragment {

  public final static String KEY_STATUS = "status";

  private Status mJobStatus;
  private QuesJobListDataBinding mListDataBinding;
  private QuestionerJobListAdapter mJobListAdapter;
  private Subscription refreshSubscription;

  public static Fragment getInstance(Status status) {
    JobsListFragment listFragment = new JobsListFragment();
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
    mListDataBinding = loadBinding(inflater, container);
    return mListDataBinding.getRoot();
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

  private void subscribeRefresh() {
    refreshSubscription =
        Communicator.getCommunicator().subRefreshQuestionerJob().subscribe(new Action1<Status>() {
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

  private QuesJobListDataBinding loadBinding(LayoutInflater inflater,
      @Nullable ViewGroup container) {
    return DataBindingUtil.inflate(inflater, R.layout.fragment_questioner_job_list, container,
        false);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    createJobListView();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    unSubScribeRefresh();
  }

  /**
   * This will create the recycler view bind the adapter and load the Job data
   */
  private void createJobListView() {
    mJobListAdapter = new QuestionerJobListAdapter(AppMode.QUESTIONER.name(), getActivity());
    mListDataBinding.questionerJobList.setHasFixedSize(true);
    mListDataBinding.questionerJobList.setLayoutManager(new LinearLayoutManager(getContext()));
    mListDataBinding.questionerJobList.setAdapter(mJobListAdapter);
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
    hashMap.put(RestFields.KEY_USER_TYPE, String.valueOf(RestFields.USER_TYPE_REQUESTER));
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
        hideProgressBar();
        mJobListAdapter.addJobList(Collections.<JobDetail>emptyList());
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
    mListDataBinding.emptyView.setVisibility(View.GONE);
  }

  private void showEmptyView(String emptyMessage) {
    mListDataBinding.emptyView.setVisibility(View.VISIBLE);
    mListDataBinding.emptyView.setText(emptyMessage);
  }

  private void showProgressBar() {
    mListDataBinding.progressBar.setVisibility(View.VISIBLE);
    mListDataBinding.emptyView.setVisibility(View.VISIBLE);
  }

  private void hideProgressBar() {
    mListDataBinding.progressBar.setVisibility(View.GONE);
  }
}
