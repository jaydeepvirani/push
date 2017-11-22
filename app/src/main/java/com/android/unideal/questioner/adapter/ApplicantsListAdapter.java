package com.android.unideal.questioner.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import co.mobiwise.materialintro.view.MaterialIntroView;
import com.android.unideal.R;
import com.android.unideal.data.Applicant;
import com.android.unideal.databinding.ItemApplicantsBinding;
import com.android.unideal.util.AppUtility;
import com.android.unideal.util.BindingUtils;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.jakewharton.rxbinding.view.RxView;
import java.util.ArrayList;
import java.util.List;
import rx.functions.Action1;

/**
 * Created by bhavdip on 18/10/16.
 */

public class ApplicantsListAdapter
    extends RecyclerSwipeAdapter<ApplicantsListAdapter.ApplicantsViewHolder> {

  private List<Applicant> applicantsList = new ArrayList<>();
  private ApplicantsListener mListener;
  private MaterialIntroView materialIntroView;
  private Context mContext;

  public ApplicantsListAdapter(ApplicantsListener listener, Context context) {
    this.mListener = listener;
    this.mContext = context;
  }

  public void addApplicationList(List<Applicant> userList) {
    applicantsList.addAll(userList);
    notifyDataSetChanged();
  }

  @Override
  public ApplicantsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ApplicantsViewHolder(loadItemBinding(parent), mContext);
  }

  private ItemApplicantsBinding loadItemBinding(ViewGroup parent) {
    return DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
        R.layout.item_applicants, parent, false);
  }

  @Override
  public void onBindViewHolder(ApplicantsViewHolder holder, int position) {
    holder.bindingView(position);
  }

  @Override
  public int getItemCount() {
    return applicantsList.size();
  }

  @Override
  public int getSwipeLayoutResourceId(int position) {
    return R.id.swipeLayout;
  }

  /**
   * remove item for list
   *
   * @param applicantId applicant id
   */
  public void removeItem(int applicantId) {
    for (int itemPosition = 0; itemPosition < applicantsList.size(); itemPosition++) {
      Applicant applicant = applicantsList.get(itemPosition);
      if (applicant.getApplicantId() == applicantId) {
        //mItemManger.getOpenLayouts();
        //mItemManger.removeShownLayouts(mBinding.swipeLayout);
        applicantsList.remove(itemPosition);
        notifyItemRemoved(itemPosition);
        notifyItemRangeChanged(itemPosition, applicantsList.size());
        mItemManger.closeAllItems();
        return;
      }
    }
  }

  public interface ApplicantsListener {
    void onAccept(Applicant applicant, int position);

    void onReject(Applicant applicant, int position);

    void onItemClick(Applicant applicant, int position);

    void onDeliveryClick();
  }

  public class ApplicantsViewHolder extends RecyclerView.ViewHolder
      implements View.OnClickListener {

    private int itemPosition;
    private ItemApplicantsBinding mBinding;

    public ApplicantsViewHolder(final ItemApplicantsBinding itemView, Context mContext) {
      super(itemView.getRoot());
      mBinding = itemView;
      mBinding.itemView.setOnClickListener(this);
      mBinding.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
      mBinding.swipeLayout.addDrag(SwipeLayout.DragEdge.Left,
          mBinding.swipeLayout.findViewById(R.id.ignoreApplicants));
      mBinding.swipeLayout.addDrag(SwipeLayout.DragEdge.Right,
          mBinding.swipeLayout.findViewById(R.id.acceptApplicants));

      mBinding.acceptApplicants.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (mListener != null) {
            mListener.onAccept(applicantsList.get(itemPosition), itemPosition);
          }
        }
      });

      mBinding.ignoreApplicants.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (mListener != null) {
            mListener.onReject(applicantsList.get(itemPosition), itemPosition);
          }
        }
      });

      mBinding.imageViewProfile.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (mListener != null) {
            mListener.onItemClick(applicantsList.get(itemPosition), itemPosition);
          }
        }
      });

      RxView.clicks(mBinding.deliveryAddress).subscribe(new Action1<Void>() {
        @Override
        public void call(Void aVoid) {
          Applicant applicant = applicantsList.get(itemPosition);
          if (TextUtils.isEmpty(applicant.getDelivery_place())) {
            if (mListener != null) {
              mListener.onDeliveryClick();
            }
          }
        }
      });
    }

    @Override
    public void onClick(View v) {
      if (mListener != null) {
        mListener.onItemClick(applicantsList.get(itemPosition), itemPosition);
      }
    }

    private void bindingView(int position) {
      this.itemPosition = position;
      Applicant applicant = applicantsList.get(position);
      mBinding.textViewUserName.setText(applicant.getUserName());
      mBinding.userRatingBar.setRating(applicant.getReviews());
      //if delivery place is empty means it's default unideal place
      if (TextUtils.isEmpty(applicant.getDelivery_place())) {
        mBinding.deliveryAddress.setText(
            itemView.getContext().getString(R.string.title_unideal_delivery_place));
        mBinding.deliveryAddress.setPaintFlags(
            mBinding.deliveryAddress.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
      } else {
        mBinding.deliveryAddress.setText(applicant.getDelivery_place());
      }
      String offer = itemView.getContext().getString(R.string.suffix_currency);
      mBinding.offer.setText(applicant.getOffer() + " " + offer);
      BindingUtils.profileImage(mBinding.imageViewProfile,
          applicantsList.get(position).getProfilePic());
      mItemManger.bindView(mBinding.getRoot(), position);
      //if already offer to the agent we setSwipeEnabled false
      if (applicant.getIs_offered() == 1) {
        mBinding.swipeLayout.setSwipeEnabled(false);
        mBinding.leftEdge.setVisibility(View.VISIBLE);
        AppUtility.loadSVGImage(mBinding.status, R.drawable.ic_handshake,
            R.color.colorPersianGreen);
      } else {
        mBinding.leftEdge.setVisibility(View.GONE);
        mBinding.swipeLayout.setSwipeEnabled(true);
      }
    }
  }
}
