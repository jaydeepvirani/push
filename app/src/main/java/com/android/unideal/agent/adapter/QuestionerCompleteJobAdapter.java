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
import com.android.unideal.databinding.ItemAgentJobComplete;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADMIN on 24-10-2016.
 */

public class QuestionerCompleteJobAdapter
    extends RecyclerView.Adapter<QuestionerCompleteJobAdapter.MyJobItemViewHolder> {

  private List<JobDetail> agentJobsDataList = new ArrayList<>();
  private Context mContext;

  public QuestionerCompleteJobAdapter(Context context) {
    this.mContext = context;
  }

  public Context getContext() {
    return mContext;
  }

  public void addJobItem(List<JobDetail> newJob) {
    agentJobsDataList.addAll(newJob);
    notifyItemInserted(agentJobsDataList.size());
  }

  @Override
  public QuestionerCompleteJobAdapter.MyJobItemViewHolder onCreateViewHolder(ViewGroup parent,
      int viewType) {
    return findJobViewType(parent);
  }

  private QuestionerCompleteJobAdapter.MyJobItemViewHolder findJobViewType(ViewGroup parent) {
    View mItemView = null;

    mItemView = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
        R.layout.item_agent_complete_job, parent, false).getRoot();
    return new QuestionerCompleteJobAdapter.MyJobItemViewHolder(mItemView);
  }

  @Override
  public void onBindViewHolder(QuestionerCompleteJobAdapter.MyJobItemViewHolder viewHolder,
      int position) {
    viewHolder.bindView(position, agentJobsDataList.get(position));
  }

  @Override
  public int getItemCount() {
    return agentJobsDataList.size();
  }

  /**
   *
   */
  public class MyJobItemViewHolder extends RecyclerView.ViewHolder {
    private View mItemView;
    private int mPosition;

    public MyJobItemViewHolder(View itemView) {
      super(itemView);
      this.mItemView = itemView;
    }

    private void bindView(int position, JobDetail jobDetail) {
      this.mPosition = position;
      ItemAgentJobComplete completeJobBinding = DataBindingUtil.bind(mItemView);
      //TODO load job list in profile
      completeJobBinding.setViewmodel(
          new JobsViewModel(getContext(), jobDetail, jobDetail.getJob_status(),false));
    }
  }
}
