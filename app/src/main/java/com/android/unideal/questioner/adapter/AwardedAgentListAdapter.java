package com.android.unideal.questioner.adapter;

import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.data.Applicant;
import com.android.unideal.databinding.ItemAwardedAgent;
import com.android.unideal.util.AppUtility;
import com.android.unideal.util.BindingUtils;
import com.jakewharton.rxbinding.view.RxView;
import java.util.ArrayList;
import java.util.List;
import rx.functions.Action1;

/**
 * Created by bhavdip on 10/18/16.
 * This adapter use to find the agent list when the job in progress mode
 * means job has been assign to the one of agent, it will display the
 * other agent that involved while job posted.
 */

public class AwardedAgentListAdapter
    extends RecyclerView.Adapter<AwardedAgentListAdapter.AgentViewHolder> {
  private List<Applicant> participantsAgentList = new ArrayList<>();

  private ApplicantsListener applicantsListener;

  public AwardedAgentListAdapter(ApplicantsListener listener) {
    this.applicantsListener = listener;
  }

  public void addParticipant(List<Applicant> userList) {
    participantsAgentList.addAll(userList);
  }

  @Override
  public AgentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new AgentViewHolder(loadAwardedBinding(parent));
  }

  private ItemAwardedAgent loadAwardedBinding(ViewGroup parent) {
    return DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
        R.layout.item_awarded_agent, parent, false);
  }

  @Override
  public void onBindViewHolder(AgentViewHolder holder, int position) {
    holder.bindingAwardedAgent(position);
  }

  @Override
  public int getItemCount() {
    return participantsAgentList.size();
  }

  public interface ApplicantsListener {
    void onDeliveryClick();
  }

  public class AgentViewHolder extends RecyclerView.ViewHolder {

    private ItemAwardedAgent mAwardedAgent;
    private int itemPosition;

    public AgentViewHolder(ItemAwardedAgent awardedAgent) {
      super(awardedAgent.getRoot());
      this.mAwardedAgent = awardedAgent;
      RxView.clicks(mAwardedAgent.deliveryAddress).subscribe(new Action1<Void>() {
        @Override
        public void call(Void aVoid) {
          Applicant applicant = participantsAgentList.get(itemPosition);
          if (TextUtils.isEmpty(applicant.getDelivery_place())) {
            if (applicantsListener != null) {
              applicantsListener.onDeliveryClick();
            }
          }
        }
      });
    }

    private void bindingAwardedAgent(int position) {
      this.itemPosition = position;
      Applicant agent = participantsAgentList.get(itemPosition);
      if (agent != null) {
        mAwardedAgent.textViewUserName.setText(agent.getUserName());
        mAwardedAgent.userRatingBar.setRating(agent.getReviews());
        if (TextUtils.isEmpty(agent.getDelivery_place())) {
          mAwardedAgent.deliveryAddress.setPaintFlags(
              mAwardedAgent.deliveryAddress.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
          mAwardedAgent.deliveryAddress.setText(
              itemView.getContext().getString(R.string.title_unideal_delivery_place));
        } else {
          mAwardedAgent.deliveryAddress.setText(agent.getDelivery_place());
        }
        String offer = itemView.getContext().getString(R.string.suffix_currency);
        mAwardedAgent.offer.setText(agent.getOffer() + " " + offer);
        BindingUtils.profileImage(mAwardedAgent.imageViewProfilePicture, agent.getProfilePic());
        if (agent.getApplicantStatus() == 3) {
          mAwardedAgent.leftEdge.setVisibility(View.VISIBLE);
          AppUtility.loadSVGImage(mAwardedAgent.status, R.drawable.ic_handshake,
              R.color.colorPrimary);
        } else {
          mAwardedAgent.leftEdge.setVisibility(View.GONE);
        }
      }
    }
  }
}
