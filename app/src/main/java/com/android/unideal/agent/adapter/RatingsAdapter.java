package com.android.unideal.agent.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.agent.viewmodel.RatingsItemViewModel;
import com.android.unideal.databinding.AgentRatingHeaderBinding;
import com.android.unideal.databinding.ItemRatingAgentBinding;
import com.android.unideal.rest.response.JobRatting;
import com.android.unideal.rest.response.RattingResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADMIN on 05-10-2016.
 */

public class RatingsAdapter extends RecyclerView.Adapter {
  private static final String TAG = "RatingsAdapter";
  private static final int TYPE_HEADER = 0;
  private static final int TYPE_ITEM = 1;
  private Context context;
  private List<JobRatting> agentJobListResponses = new ArrayList<>();
  private RattingResponse response = new RattingResponse();

  public RatingsAdapter(Context context) {
    this.context = context;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == TYPE_HEADER) {
      AgentRatingHeaderBinding binding =
          DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
              R.layout.item_ratings_header_agent, parent, false);
      return new HeaderViewHolder(binding);
    } else if (viewType == TYPE_ITEM) {
      ItemRatingAgentBinding binding =
          DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_ratings,
              parent, false);
      return new AgentRatingViewHolder(binding);
    }
    return null;
  }

  public void addData(List<JobRatting> agentJobListResponseList) {
    agentJobListResponses.addAll(agentJobListResponseList);
    notifyDataSetChanged();
  }

  public void addRattingData(RattingResponse rattingResponse) {
    this.response = rattingResponse;
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof AgentRatingViewHolder) {
      JobRatting currentItem = getItem(position);
      ((AgentRatingViewHolder) holder).bindList(currentItem, position);
    } else if (holder instanceof HeaderViewHolder) {
      ((HeaderViewHolder) holder).bind(response, context);
    }
  }

  public JobRatting getItem(int position) {
    return agentJobListResponses.get(position - 1);
  }

  //    need to override this method
  @Override
  public int getItemViewType(int position) {
    if (isPositionHeader(position)) {
      return TYPE_HEADER;
    }
    return TYPE_ITEM;
  }

  private boolean isPositionHeader(int position) {
    return position == 0;
  }

  @Override
  public int getItemCount() {
    return agentJobListResponses.size() + 1;
  }

  public class AgentRatingViewHolder extends RecyclerView.ViewHolder
      implements View.OnClickListener {
    ItemRatingAgentBinding binding;
    int position;

    public AgentRatingViewHolder(ItemRatingAgentBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
      this.binding.getRoot().setOnClickListener(this);
    }

    void bindList(JobRatting agentJobListResponse, int position) {
      this.position = position;
      if (binding.getViewmodel() == null) {
        binding.setViewmodel(new RatingsItemViewModel(agentJobListResponse));
      } else {
        binding.getViewmodel().setRatingResponse(agentJobListResponse);
      }
    }

    @Override
    public void onClick(View view) {
      //When click on item will open the details of the job
      //We send the Job data to the activity
      //TODO currently passed the dummy content it should from the array of this adapter
      //Communicator.getCommunicator().emitJobClick(AgentJobListProvider.requestAgentJobList().get(0));
    }
  }

  private class HeaderViewHolder extends RecyclerView.ViewHolder {
    final AgentRatingHeaderBinding binding;
    //    double subTotal = 0.0;

    public HeaderViewHolder(AgentRatingHeaderBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    void bind(RattingResponse response, Context context) {
      binding.noOfRatings.setText("(" + String.valueOf(response.getJobRattingsList().size()) + ")");
      binding.ratingBar.setRating(response.getAvg_ratting());
    }
  }
}
