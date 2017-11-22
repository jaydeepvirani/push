package com.android.unideal.questioner.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.data.JobDetail;
import com.android.unideal.data.Status;
import com.android.unideal.databinding.RowCancelJobBinding;
import com.android.unideal.databinding.RowCompleteJobBinding;
import com.android.unideal.databinding.RowOpenJobBinding;
import com.android.unideal.databinding.RowPauseJobBinding;
import com.android.unideal.databinding.RowProcessJobBinding;
import com.android.unideal.enums.AppMode;
import com.android.unideal.questioner.viewmodel.JobViewModel;
import com.android.unideal.util.Communicator;
import java.util.ArrayList;
import java.util.List;

public class QuestionerJobListAdapter
    extends RecyclerView.Adapter<QuestionerJobListAdapter.JobItemViewHolder> {
  private List<JobDetail> jobsDataList = new ArrayList<>();
  private String mode;
  private Context mContext;

  public QuestionerJobListAdapter(String mode, Context context) {
    this.mode = mode;
    this.mContext = context;
  }

  private Context getActivityContext() {
    return mContext;
  }

  public void addJobItem(JobDetail job) {
    jobsDataList.add(job);
  }

  public void addJobList(List<JobDetail> jobList) {
    jobsDataList.clear();
    jobsDataList.addAll(jobList);
    notifyDataSetChanged();
  }

  @Override
  public JobItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return findJobViewType(parent, Status.values()[viewType]);
  }

  private JobItemViewHolder findJobViewType(ViewGroup parent, Status jobStatus) {
    View mItemView = null;
    switch (jobStatus) {
      case OPEN: {
        mItemView = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
            R.layout.item_quer_open_job, parent, false).getRoot();
        break;
      }
      case IN_PROCESS: {
        mItemView = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
            R.layout.item_quer_process_job, parent, false).getRoot();
        break;
      }
      case COMPLETED: {
        mItemView = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
            R.layout.item_quer_complete_job, parent, false).getRoot();
        break;
      }
      case PAUSED: {
        mItemView = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
            R.layout.item_quer_pause_job, parent, false).getRoot();
        break;
      }
      case CANCELLED: {
        mItemView = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
            R.layout.item_quer_cancel_job, parent, false).getRoot();
        break;
      }
    }
    return new JobItemViewHolder(mItemView, jobStatus);
  }

  @Override
  public void onBindViewHolder(JobItemViewHolder viewHolder, int position) {
    viewHolder.bindingRowItem(position);
  }

  @Override
  public int getItemCount() {
    return jobsDataList.size();
  }

  @Override
  public int getItemViewType(int position) {
    JobDetail jobDetail = jobsDataList.get(position);
    Status status = jobDetail.getStatusEnum();
    return status.ordinal();
  }

  public class JobItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private View mItemView;
    private Status mJobStatus;
    private int mPosition;

    public JobItemViewHolder(View itemView, Status status) {
      super(itemView.getRootView());
      this.mItemView = itemView;
      this.mJobStatus = status;
      itemView.setOnClickListener(this);
    }

    public void bindingRowItem(int position) {
      this.mPosition = position;
      switch (mJobStatus) {
        case OPEN: {
          RowOpenJobBinding openJobBinding = DataBindingUtil.bind(mItemView);
          openJobBinding.setViewmodel(
              new JobViewModel(jobsDataList.get(position), getActivityContext()));
          break;
        }
        case IN_PROCESS: {
          RowProcessJobBinding processJobBinding = DataBindingUtil.bind(mItemView);
          processJobBinding.setViewmodel(
              new JobViewModel(jobsDataList.get(position), getActivityContext()));
          break;
        }
        case COMPLETED: {
          RowCompleteJobBinding rowCompleteJobBinding = DataBindingUtil.bind(mItemView);
          rowCompleteJobBinding.setViewmodel(
              new JobViewModel(jobsDataList.get(position), getActivityContext()));
          break;
        }
        case CANCELLED: {
          RowCancelJobBinding rowCancelJobBinding = DataBindingUtil.bind(mItemView);
          rowCancelJobBinding.setViewmodel(
              new JobViewModel(jobsDataList.get(position), getActivityContext()));
          break;
        }
        case PAUSED: {
          JobDetail jobDetail = jobsDataList.get(position);
          RowPauseJobBinding pausedJobsBinding = DataBindingUtil.bind(mItemView);
          pausedJobsBinding.setViewmodel(new JobViewModel(jobDetail, getActivityContext()));
          int isDispute = jobDetail.getIs_dispute();
          pausedJobsBinding.disputeImage.setVisibility(isDispute == 1 ? View.VISIBLE : View.GONE);

          break;
        }
      }
    }

    @Override
    public void onClick(View view) {
      //When click on item will open the details of the job
      //We send the Job data to the activity
      if (mode == null) {
        //Communicator.getCommunicator().emitJobClick(jobsDataList.get(mPosition));
      } else if (mode.equals(AppMode.QUESTIONER.name())) {
        Communicator.getCommunicator().emitJobClick(jobsDataList.get(mPosition));
      }
    }
  }
}
