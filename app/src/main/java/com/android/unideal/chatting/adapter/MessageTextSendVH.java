package com.android.unideal.chatting.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import com.android.unideal.R;
import com.android.unideal.data.socket.MediaData;
import com.android.unideal.data.socket.Message;
import com.android.unideal.databinding.ItemMessageTextSendBinding;
import com.android.unideal.enums.AppMode;
import com.android.unideal.util.DateTimeUtils;
import com.squareup.picasso.Picasso;

/** Created by CS02 on 12/7/2016. */
public class MessageTextSendVH extends RecyclerView.ViewHolder implements ConversationInterface {
  private ItemMessageTextSendBinding mBinding;
  private AppMode mCurrentAppMode;
  private String userImage;

  public MessageTextSendVH(View itemView, AppMode appMode, String userImage) {
    super(itemView);
    mBinding = DataBindingUtil.bind(itemView);
    mCurrentAppMode = appMode;
    this.userImage = userImage;
  }

  @Override
  public void bind(Message message) {
    MediaData data = message.getMediaData();
    String time = message.getSend_time();
    String timeString = DateTimeUtils.messageTime(time);
    mBinding.chatTime.setText(timeString);
    // set background bubble
    if (mCurrentAppMode == AppMode.AGENT) {
      mBinding.bubbleLayout.setBackgroundResource(R.drawable.drw_bubble_blue);
      mBinding.arrow.setImageResource(R.drawable.bubble_arrow_agent);
    } else {
      mBinding.bubbleLayout.setBackgroundResource(R.drawable.drw_bubble_green);
      mBinding.arrow.setImageResource(R.drawable.bubble_arrow_requester);
    }
    // set user profile image
    if (userImage == null) {
      Picasso.with(mBinding.imageViewProfile.getContext()).cancelRequest(mBinding.imageViewProfile);
    } else {
      Picasso.with(mBinding.imageViewProfile.getContext())
          .load(userImage)
          .into(mBinding.imageViewProfile);
    }
    // set content text
    if (TextUtils.isEmpty(data.getText())) {
      mBinding.textViewMessage.setVisibility(View.GONE);
    } else {
      mBinding.textViewMessage.setVisibility(View.VISIBLE);
      mBinding.textViewMessage.setText(data.getText() + "     ");
    }
    // set content image
    if (TextUtils.isEmpty(data.getImageUrl())) {
      mBinding.imageViewPhoto.setVisibility(View.GONE);
      Picasso.with(mBinding.imageViewPhoto.getContext())
          .load(R.drawable.image_border_background)
          .into(mBinding.imageViewPhoto);
    } else {
      mBinding.imageViewPhoto.setVisibility(View.VISIBLE);
      Picasso.with(mBinding.imageViewPhoto.getContext())
          .load(data.getImageUrl())
          .into(mBinding.imageViewPhoto);
    }
    int status = message.getStatus();
    if (status == Message.STATUS_SEEN) {
      mBinding.statusImage.setImageResource(R.drawable.ic_status_seen);
    } else if (status == Message.STATUS_SEND) {
      mBinding.statusImage.setImageResource(R.drawable.ic_status_delivered);
    } else {
      mBinding.statusImage.setImageResource(R.drawable.ic_status_none);
    }
  }
}


