package com.android.unideal.agent.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.agent.viewmodel.AgentJobListItemViewModel;
import com.android.unideal.data.JobDetail;
import com.android.unideal.data.UserDetail;
import com.android.unideal.databinding.ItemAgentListBinding;
import com.android.unideal.util.Communicator;
import com.android.unideal.util.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class AgentJobListAdapter extends RecyclerView.Adapter {
  private Context context;
  private AgentJobListListener mListener;
  private RecyclerView recyclerView;
  private List<JobDetail> agentJobListResponses = new ArrayList<>();
  private int userExpertise;

  public AgentJobListAdapter(Context context, RecyclerView recyclerView) {
    this.context = context;
    this.recyclerView = recyclerView;
    UserDetail userDetail = SessionManager.get(context).getActiveUser();
    if (userDetail != null) {
      this.userExpertise = userDetail.getUser_expertise();
    }
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    ItemAgentListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
        R.layout.item_agent_job_list, parent, false);
    return new AgentJobListViewHolder(binding);
  }

  public void addData(List<JobDetail> agentJobListResponseList) {
    if (agentJobListResponses.size() > 0) {
      agentJobListResponses.clear();
    }
    agentJobListResponses.addAll(agentJobListResponseList);
    notifyDataSetChanged();
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof AgentJobListViewHolder) {
      JobDetail currentItem = getItem(position);
      ((AgentJobListViewHolder) holder).bindList(currentItem, position);
    }
  }

  @Override
  public int getItemCount() {
    return agentJobListResponses.size();
  }

  public JobDetail getItem(int position) {
    return agentJobListResponses.get(position);
  }

  public void setAgentListener(AgentJobListListener mListener) {
    this.mListener = mListener;
  }

  public interface AgentJobListListener {

  }

  public class AgentJobListViewHolder extends RecyclerView.ViewHolder
      implements View.OnClickListener {
    ItemAgentListBinding binding;
    int position;

    private AgentJobListViewHolder(ItemAgentListBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
      this.binding.getRoot().setOnClickListener(this);
    }

    void bindList(JobDetail agentJobListResponse, int position) {
      this.position = position;
      if (binding.getViewmodel() == null) {
        binding.setViewmodel(
            new AgentJobListItemViewModel(itemView.getContext(), agentJobListResponse,
                userExpertise));
      } else {
        binding.getViewmodel().setJobDetail(agentJobListResponse);
      }
    }



    @Override
    public void onClick(View view) {
      //When click on item will open the details of the job
      //We send the Job data to the activity
      Communicator.getCommunicator().emitJobClick(agentJobListResponses.get(position));
    }
  }
}
