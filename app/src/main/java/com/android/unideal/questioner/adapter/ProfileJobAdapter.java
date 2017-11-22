package com.android.unideal.questioner.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.data.JobDetail;
import com.android.unideal.databinding.RowCancelJobBinding;
import com.android.unideal.questioner.viewmodel.JobViewModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADMIN on 24-11-2016.
 */

public class ProfileJobAdapter extends RecyclerView.Adapter<ProfileJobAdapter.ProfileViewHolder> {
  private List<JobDetail> jobsDataList = new ArrayList<>();
  private Context mContext;

  public ProfileJobAdapter(Context context) {
    this.mContext = context;
  }

  public void addJobList(List<JobDetail> jobList) {
    jobsDataList.clear();
    jobsDataList.addAll(jobList);
    notifyDataSetChanged();
  }

  @Override
  public ProfileJobAdapter.ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View mItemView = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
        R.layout.item_quer_cancel_job, parent, false).getRoot();
    return new ProfileViewHolder(mItemView);
  }

  @Override
  public void onBindViewHolder(ProfileJobAdapter.ProfileViewHolder holder, int position) {
    holder.bindJobItem(position, mContext, jobsDataList.get(position));
  }

  @Override
  public int getItemCount() {
    return jobsDataList.size();
  }

  public class ProfileViewHolder extends RecyclerView.ViewHolder {
    private View mItemView;

    public ProfileViewHolder(View itemView) {
      super(itemView);
      this.mItemView = itemView;
    }

    public void bindJobItem(int position, Context context, JobDetail jobDetail) {
      RowCancelJobBinding rowCancelJobBinding = DataBindingUtil.bind(mItemView);
      rowCancelJobBinding.setViewmodel(new JobViewModel(jobDetail, context));
    }
  }
}
