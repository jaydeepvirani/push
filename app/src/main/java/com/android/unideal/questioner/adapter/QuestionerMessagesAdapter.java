package com.android.unideal.questioner.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.data.socket.Messages;
import com.android.unideal.databinding.ItemMessageBinding;
import com.android.unideal.databinding.LoadMoreItemBinding;
import com.android.unideal.questioner.viewmodel.QuestionerMessagesItemViewModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ADMIN on 12-10-2016.
 */

public class QuestionerMessagesAdapter extends RecyclerView.Adapter {

  public final int TYPE_MESSAGES = 0;
  public final int TYPE_LOAD = 1;
  private Context mContext;
  private List<Messages> messagesList = new ArrayList<>();
  /*
    * isLoading - to set the remote loading and complete status to fix back to back load more call
    * isMoreDataAvailable - to set whether more data from server available or not.
    * It will prevent useless load more request even after all the server data loaded
    * */
  private OnLoadMoreListener loadMoreListener;
  private OnMessagesItemClick onMessagesItemClick;
  private boolean isLoading = false, isMoreDataAvailable = true;

  public QuestionerMessagesAdapter(Context context) {
    this.mContext = context;
  }

  private Context getContext() {
    return mContext;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == TYPE_MESSAGES) {
      ItemMessageBinding binding = loadItemBinding(parent);
      return new QuestionerMessagesViewHolder(binding);
    } else {
      //if view type is load more
      return new LoadViewHolder(loadMoreBinding(parent));
    }
  }

  private ItemMessageBinding loadItemBinding(ViewGroup parent) {
    return DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
        R.layout.item_messages_questioner, parent, false);
  }

  private LoadMoreItemBinding loadMoreBinding(ViewGroup parent) {
    return DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
        R.layout.item_load_more, parent, false);
  }

  public void addMessagesData(List<Messages> messages) {
    messagesList.addAll(messages);
  }

  public int getDataSetSize() {
    if (messagesList != null) {
      return (messagesList.size() - 1);
    } else {
      return 0;
    }
  }

  /**
   * Must call inside the onRunUI thread
   * Must call before make a network call and receive
   * a data
   */
  public void addLoadMore() {
    if (messagesList != null && messagesList.size() > 0) {
      messagesList.add(null);
      notifyItemInserted(messagesList.size() - 1);
    }
  }

  /**
   * Must call before receive the data set from server and
   * start adding into the adapter
   * otherwise it will remove the original item from
   * the collection list
   */
  public void removeLoadMore() {
    if (messagesList != null && messagesList.size() > 0) {
      messagesList.remove(messagesList.size() - 1);
    }
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (position >= getDataSetSize()
        && isMoreDataAvailable
        && !isLoading
        && loadMoreListener != null) {
      isLoading = true;
      loadMoreListener.onLoadMore();
    }
    if (getItemViewType(position) == TYPE_MESSAGES) {
      if (holder instanceof QuestionerMessagesViewHolder) {
        Messages currentItem = getItem(position);
        ((QuestionerMessagesViewHolder) holder).bindList(currentItem, position);
      }
    }
  }

  @Override
  public int getItemCount() {
    return messagesList.size();
  }

  @Override
  public int getItemViewType(int position) {
    if (messagesList.get(position) == null) {
      return TYPE_LOAD;
    } else {
      return TYPE_MESSAGES;
    }
  }

  public Messages getItem(int position) {
    return messagesList.get(position);
  }

  public void setMoreDataAvailable(boolean moreDataAvailable) {
    isMoreDataAvailable = moreDataAvailable;
  }

  public void onUpdateDataSet(List<Messages> updatedMessagesList) {
    Collections.reverse(updatedMessagesList);
    Map<String, Messages> originalMessageMap = new LinkedHashMap<>();
    for (Messages mObjects : messagesList) {
      originalMessageMap.put(mObjects.getThreadId(), mObjects);
    }
    for (Messages nwSourceMessages : updatedMessagesList) {
      if (originalMessageMap.containsKey(nwSourceMessages.getThreadId())) {
        originalMessageMap.remove(nwSourceMessages.getThreadId());
      }
      Map<String, Messages> tempHashMap = new LinkedHashMap<>();
      tempHashMap.putAll(originalMessageMap);
      originalMessageMap.clear();
      originalMessageMap.put(nwSourceMessages.getThreadId(), nwSourceMessages);
      originalMessageMap.putAll(tempHashMap);
      tempHashMap.clear();
    }
    messagesList.clear();
    messagesList.addAll(originalMessageMap.values());
  }

  /* notifyDataSetChanged is final method so we can't override it
       call adapter.notifyDataChanged(); after update the list
       */
  public void notifyDataChanged() {
    notifyDataSetChanged();
    isLoading = false;
  }

  public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
    this.loadMoreListener = loadMoreListener;
  }

  public void setOnMessagesItemClick(OnMessagesItemClick onMessagesItemClick) {
    this.onMessagesItemClick = onMessagesItemClick;
  }

  public interface OnLoadMoreListener {
    void onLoadMore();
  }

  public interface OnMessagesItemClick {
    void onItemClick(Messages chooseItems);
  }

  public class QuestionerMessagesViewHolder extends RecyclerView.ViewHolder
      implements View.OnClickListener {
    ItemMessageBinding binding;
    int position;

    public QuestionerMessagesViewHolder(ItemMessageBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
      this.binding.getRoot().setOnClickListener(this);
    }

    void bindList(Messages messages, int position) {
      this.position = position;
      binding.setViewmodel(new QuestionerMessagesItemViewModel(itemView.getContext(), messages));
      if (messages.getTotalUnread() > 0) {
        binding.getRoot().setBackgroundResource(R.drawable.drw_card_background_highlight);
      } else {
        binding.getRoot().setBackgroundResource(R.drawable.drw_card_background);
      }
    }

    @Override
    public void onClick(View view) {
      //When click on item will open the details of the job
      //We send the Job data to the activity
      if (messagesList.get(position) != null) {
        Messages originalMsg = messagesList.get(position);
        originalMsg.setTotalUnread(0);
        onMessagesItemClick.onItemClick(messagesList.get(position));
      }
    }
  }

  public class LoadViewHolder extends RecyclerView.ViewHolder {

    public LoadViewHolder(LoadMoreItemBinding itemView) {
      super(itemView.getRoot());
    }
  }
}
