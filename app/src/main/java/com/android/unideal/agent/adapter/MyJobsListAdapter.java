package com.android.unideal.agent.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.agent.viewmodel.JobsViewModel;
import com.android.unideal.data.JobDetail;
import com.android.unideal.data.Status;
import com.android.unideal.data.UserDetail;
import com.android.unideal.databinding.ItemAgentAppliedJob;
import com.android.unideal.databinding.ItemAgentJobCancelled;
import com.android.unideal.databinding.ItemAgentJobComplete;
import com.android.unideal.databinding.ItemAgentJobPaused;
import com.android.unideal.databinding.ItemAgentRunningJob;
import com.android.unideal.util.Communicator;
import com.android.unideal.util.SessionManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhavdip on 10/2/16.
 */

public class MyJobsListAdapter extends RecyclerView.Adapter<MyJobsListAdapter.MyJobItemViewHolder> {

  private List<JobDetail> agentJobsDataList = new ArrayList<>();
  private Context mContext;
  private int expertise;

  public MyJobsListAdapter(Context context) {
    this.mContext = context;
    UserDetail userDetail = SessionManager.get(context).getActiveUser();
    if (userDetail != null) {
      this.expertise = userDetail.getUser_expertise();
    }
  }

  public Context getContext() {
    return mContext;
  }

  public void addJobItem(JobDetail newJob) {
    agentJobsDataList.add(newJob);
    notifyItemInserted(agentJobsDataList.size());
  }

  @Override
  public MyJobItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return findJobViewType(parent, Status.values()[viewType]);
  }

  private MyJobItemViewHolder findJobViewType(ViewGroup parent, Status jobStatus) {
    View mItemView = null;
    switch (jobStatus) {
      case OPEN:
      case APPLIED: {
        mItemView = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
            R.layout.item_agent_applied_job, parent, false).getRoot();
        break;
      }
      case IN_PROCESS: {
        mItemView = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
            R.layout.item_agent_running_job, parent, false).getRoot();
        break;
      }
      case COMPLETED: {
        mItemView = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
            R.layout.item_agent_complete_job, parent, false).getRoot();
        break;
      }
      case PAUSED: {
        mItemView = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
            R.layout.item_agent_paused_job, parent, false).getRoot();
        break;
      }
      case CANCELLED: {
        mItemView = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
            R.layout.item_agent_cancelled_job, parent, false).getRoot();
        break;
      }
    }
    return new MyJobItemViewHolder(mItemView, jobStatus);
  }

  @Override
  public void onBindViewHolder(MyJobItemViewHolder viewHolder, int position) {
    viewHolder.bindView(position);
  }

  @Override
  public int getItemCount() {
    return agentJobsDataList.size();
  }

  @Override
  public int getItemViewType(int position) {
    JobDetail jobDetail = agentJobsDataList.get(position);
    Status status = jobDetail.getStatusEnum();
    return status.ordinal();
  }

  public void addJobList(List<JobDetail> data) {
    agentJobsDataList.clear();
    agentJobsDataList.addAll(data);
    notifyDataSetChanged();
  }

  /**
   *
   */
  public class MyJobItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private View mItemView;
    private int mPosition;
    private Status mJobStatus;

    public MyJobItemViewHolder(View itemView, Status status) {
      super(itemView);
      this.mItemView = itemView;
      this.mJobStatus = status;
      itemView.setOnClickListener(this);
    }

    private void bindView(int position) {
      this.mPosition = position;
      JobDetail jobDetail = agentJobsDataList.get(position);
      JobsViewModel jobsViewModel = new JobsViewModel(getContext(), jobDetail, expertise,true);
      switch (mJobStatus) {
        case OPEN:
        case APPLIED: {
          ItemAgentAppliedJob agentAppliedJob = DataBindingUtil.bind(mItemView);
          agentAppliedJob.setViewmodel(jobsViewModel);
          break;
        }
        case IN_PROCESS: {
          ItemAgentRunningJob agentRunningJob = DataBindingUtil.bind(mItemView);
          agentRunningJob.setViewmodel(jobsViewModel);
          break;
        }
        case COMPLETED: {
          ItemAgentJobComplete completeJobBinding = DataBindingUtil.bind(mItemView);
          completeJobBinding.setViewmodel(jobsViewModel);
          break;
        }
        case CANCELLED: {
          ItemAgentJobCancelled cancelledJobBinding = DataBindingUtil.bind(mItemView);
          cancelledJobBinding.setViewmodel(jobsViewModel);
          break;
        }
        case PAUSED: {
          ItemAgentJobPaused pausedJobBinding = DataBindingUtil.bind(mItemView);
          pausedJobBinding.setViewmodel(jobsViewModel);
          int isDispute = jobDetail == null ? 0 : jobDetail.getIs_dispute();
          pausedJobBinding.disputeImage.setVisibility(isDispute == 1 ? View.VISIBLE : View.GONE);
          break;
        }
      }

    }

    @Override
    public void onClick(View v) {
      // Fire the Job Item click listener to fragment
      Communicator.getCommunicator().emitJobClick(agentJobsDataList.get(mPosition));
    }
  }
}
