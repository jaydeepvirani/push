package com.android.unideal.chatting.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.data.socket.Message;
import com.android.unideal.data.socket.MessageStatus;
import com.android.unideal.enums.AppMode;
import com.android.unideal.util.ItemClickListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bhavdip on 10/6/16. This is the adapter can handle all conversation between agent and
 * questioner
 */
public class ConversationAdapter extends RecyclerView.Adapter {

  private static final int TYPE_SEND = 11;
  private static final int TYPE_RCV = 21;

  private List<Message> conversationList = new ArrayList<>();
  private Context activityContext;
  private AppMode mCurrentAppMode;
  private int currentUserId;
  private String senderProfileImage;
  private String receiverProfileImage;
  private String adminImage;
  private ItemClickListener<Message> listener;

  public ConversationAdapter(Context context, AppMode appMode, int userId, String senderImage,
      String receiverImage, String adminImage) {

    this.activityContext = context;
    this.mCurrentAppMode = appMode;
    this.currentUserId = userId;
    this.senderProfileImage = senderImage;
    this.receiverProfileImage = receiverImage;
    this.adminImage = adminImage;
  }

  public void setListener(ItemClickListener<Message> listener) {
    this.listener = listener;
  }

  public Context getActivityContext() {
    return activityContext;
  }

  public void appendMessage(Message message) {
    conversationList.add(message);
    notifyItemInserted(getItemCount());
  }

  public void addItems(List<Message> historyList) {
    int startPos = conversationList.size() - 1;
    conversationList.addAll(historyList);
    notifyItemRangeInserted(startPos, historyList.size());
  }

  public void addItemsToTop(List<Message> historyList) {
    Collections.reverse(historyList);
    conversationList.addAll(0, historyList);
    notifyItemRangeInserted(0, historyList.size());
  }

  public void updateStatus(MessageStatus status) {
    if (status == null) {
      return;
    }
    for (int i = 0; i < conversationList.size(); i++) {
      Message message = conversationList.get(i);
      if (message.getMessage_id() == status.getMessage_id()) {
        message.setStatus(status.getStatus());
        notifyItemChanged(i);
        return;
      }
    }
  }

  public void updateToAllRead() {
    for (int i = 0; i < conversationList.size(); i++) {
      Message message = conversationList.get(i);
      if (message.getStatus() != Message.STATUS_SEEN) {
        message.setStatus(Message.STATUS_SEEN);
        notifyItemChanged(i);
      }
    }
  }

  public void updateToAllReceived() {
    for (int i = 0; i < conversationList.size(); i++) {
      Message message = conversationList.get(i);
      if (message.getStatus() == Message.STATUS_NONE) {
        message.setStatus(Message.STATUS_SEND);
        notifyItemChanged(i);
      }
    }
  }

  public List<Message> getItems() {
    return conversationList;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
    View itemView;

    switch (viewType) {
      case TYPE_SEND:
        itemView = layoutInflater.inflate(R.layout.item_message_text_send, parent, false);
        return new MessageTextSendVH(itemView, mCurrentAppMode, senderProfileImage);
      case TYPE_RCV:
        itemView = layoutInflater.inflate(R.layout.item_message_rcv, parent, false);
        return new MessageTextRcvdVH(itemView, receiverProfileImage, adminImage);
    }
    return null;
  }

  @Override
  public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
    //ConversationInterface anInterface;
    Message message = conversationList.get(position);
    //anInterface = (ConversationInterface) holder;
    //anInterface.bind(message);
    switch (holder.getItemViewType()) {
      case TYPE_SEND:
        ((MessageTextSendVH) holder).bind(message);
        break;
      case TYPE_RCV:
        ((MessageTextRcvdVH) holder).bind(message);
        break;
    }
    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onItemClick(holder.getAdapterPosition());
      }
    });
  }

  private void onItemClick(int position) {
    if (listener != null) {
      Message message = conversationList.get(position);
      listener.onItemClick(message, position);
    }
  }

  @Override
  public int getItemCount() {
    return conversationList.size();
  }

  @Override
  public int getItemViewType(int position) {
    Message message = conversationList.get(position);
    if (message.getSender_id() == currentUserId) {
      return TYPE_SEND;
    } else {
      return TYPE_RCV;
    }
  }
}
