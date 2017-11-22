package com.android.unideal.questioner.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.databinding.ItemRatingQuestionerBinding;
import com.android.unideal.databinding.QuestionerRatingHeaderBinding;
import com.android.unideal.questioner.viewmodel.QuestionerRatingItemViewModel;
import com.android.unideal.rest.response.JobRatting;
import com.android.unideal.rest.response.RattingResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MRUGESH on 10/15/2016.
 */

public class QuestionerRatingsAdapter extends RecyclerView.Adapter {
  private static final int TYPE_HEADER = 0;
  private static final int TYPE_ITEM = 1;
  private static final String TAG = "RatingsAdapter";
  private Context context;
  private QuestionerAdapterRatingListener mListener;
  private RecyclerView recyclerView;
  private List<JobRatting> agentJobListResponses = new ArrayList<>();
  private RattingResponse response = new RattingResponse();
  private float averageRatings;
  private int totalRatings = 0;

  public QuestionerRatingsAdapter(Context context, RecyclerView recyclerView) {
    this.context = context;
    this.recyclerView = recyclerView;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == TYPE_HEADER) {
      QuestionerRatingHeaderBinding binding =
          DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
              R.layout.item_ratings_header_questioner, parent, false);
      return new HeaderViewHolder(binding, response);
    } else if (viewType == TYPE_ITEM) {
      ItemRatingQuestionerBinding binding =
          DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
              R.layout.item_ratings_questioner, parent, false);
      return new QuestionerRatingViewHolder(binding);
    }
    return null;
  }

  public void addData(List<JobRatting> agentJobListResponseList) {
    agentJobListResponses.addAll(agentJobListResponseList);
    notifyDataSetChanged();
  }

  public JobRatting getItem(int position) {
    return agentJobListResponses.get(position - 1);
  }

  public void addRattingData(RattingResponse rattingResponse) {
    this.response = rattingResponse;
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof QuestionerRatingViewHolder) {
      JobRatting currentItem = getItem(position);
      ((QuestionerRatingViewHolder) holder).bindList(currentItem, position);
    } else if (holder instanceof HeaderViewHolder) {
      ((HeaderViewHolder) holder).bind(response, context);
    }
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

  public void setRatingListener(QuestionerAdapterRatingListener mListener) {
    this.mListener = mListener;
  }

  public interface QuestionerAdapterRatingListener {

  }

  public class QuestionerRatingViewHolder extends RecyclerView.ViewHolder
      implements View.OnClickListener {
    ItemRatingQuestionerBinding binding;
    int position;

    public QuestionerRatingViewHolder(ItemRatingQuestionerBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
      this.binding.getRoot().setOnClickListener(this);
    }

    void bindList(JobRatting agentJobListResponse, int position) {
      this.position = position;
      if (binding.getViewmodel() == null) {
        binding.setViewmodel(
            new QuestionerRatingItemViewModel(itemView.getContext(), agentJobListResponse));
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
    final QuestionerRatingHeaderBinding binding;

    public HeaderViewHolder(QuestionerRatingHeaderBinding binding, RattingResponse response) {
      super(binding.getRoot());
      this.binding = binding;
    }

    void bind(RattingResponse jobRatting, Context context) {
      binding.noOfRatings.setText(
          "(" + String.valueOf(jobRatting.getJobRattingsList().size()) + ")");
      binding.ratingBar.setRating(jobRatting.getAvg_ratting());
    }
  }
}

