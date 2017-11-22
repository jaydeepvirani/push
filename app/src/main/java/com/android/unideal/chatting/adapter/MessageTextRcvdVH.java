package com.android.unideal.chatting.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import com.android.unideal.R;
import com.android.unideal.data.socket.MediaData;
import com.android.unideal.data.socket.Message;
import com.android.unideal.databinding.ItemMessageRcvBinding;
import com.android.unideal.rest.RestFields;
import com.android.unideal.util.DateTimeUtils;
import com.squareup.picasso.Picasso;

/** Created by CS02 on 12/7/2016. */
public class MessageTextRcvdVH extends RecyclerView.ViewHolder implements ConversationInterface {
  private ItemMessageRcvBinding mBinding;
  private String userImage;
  private String adminImage;

  public MessageTextRcvdVH(View itemView, String userImage, String adminImage) {
    super(itemView);
    mBinding = DataBindingUtil.bind(itemView);
    this.userImage = userImage;
    this.adminImage = adminImage;
  }

  @Override
  public void bind(Message message) {
    MediaData data = message.getMediaData();
    String time = message.getSend_time();
    String timeString = DateTimeUtils.messageTime(time);
    mBinding.chatTime.setText(timeString);

    // set user profile image
    String imageUrl;
    if (message.getUser_type() == RestFields.USER_TYPE_ADMIN) {
      imageUrl = adminImage;
    } else {
      imageUrl = userImage;
    }
    if (imageUrl == null) {
      Picasso.with(mBinding.imageViewProfile.getContext()).cancelRequest(mBinding.imageViewProfile);
    } else {
      Picasso.with(mBinding.imageViewProfile.getContext()).load(imageUrl)
          .into(mBinding.imageViewProfile);
    }
    // set content text
    if (TextUtils.isEmpty(data.getText())) {
      mBinding.textViewMessage.setVisibility(View.GONE);
    } else {
      mBinding.textViewMessage.setVisibility(View.VISIBLE);
      mBinding.textViewMessage.setText(data.getText());
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
  }
}
