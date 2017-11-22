package com.android.unideal.agent.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.data.Notifications;
import com.android.unideal.databinding.ItemNotifBinding;
import com.android.unideal.databinding.LoadMoreItemBinding;
import com.android.unideal.enums.AppMode;
import com.android.unideal.rest.RestFields;
import com.android.unideal.util.DateTimeUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADMIN on 12-10-2016.
 */

public class NotificationAdapter extends RecyclerView.Adapter {

  public final int TYPE_ITEM = 0;
  public final int TYPE_LOAD = 1;
  private Context mContext;
  private List<Notifications> notificationsList = new ArrayList<>();
  /*
    * isLoading - to set the remote loading and complete status to fix back to back load more call
    * isMoreDataAvailable - to set whether more data from server available or not.
    * It will prevent useless load more request even after all the server data loaded
    * */
  private OnLoadMoreListener loadMoreListener;
  private OnItemClick onItemClick;
  private boolean isLoading = false, isMoreDataAvailable = true;
  private int tintColor;
  private int backGround;

  public NotificationAdapter(Context context, AppMode appMode) {
    this.mContext = context;
    if (appMode == AppMode.AGENT) {
      tintColor = ContextCompat.getColor(mContext, R.color.colorCuriousBlue);
      backGround = R.drawable.drw_highlight_card;
    } else {
      tintColor = ContextCompat.getColor(mContext, R.color.colorPersianGreen);
      backGround = R.drawable.drw_card_background_highlight;
    }
  }

  private Context getContext() {
    return mContext;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    if (viewType == TYPE_ITEM) {
      ItemNotifBinding binding =
          DataBindingUtil.inflate(inflater, R.layout.item_notification, parent, false);
      return new NotificationViewHolder(binding);
    } else {
      //if view type is load more
      LoadMoreItemBinding binding =
          DataBindingUtil.inflate(inflater, R.layout.item_load_more, parent, false);
      return new LoadViewHolder(binding);
    }
  }

  public void addData(List<Notifications> list) {
    notificationsList.addAll(list);
  }

  public int getDataSetSize() {
    if (notificationsList != null) {
      return (notificationsList.size() - 1);
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
    if (notificationsList != null && notificationsList.size() > 0) {
      notificationsList.add(null);
      notifyItemInserted(notificationsList.size() - 1);
    }
  }

  public boolean readNotification(int notificationId) {
    for (int i = 0; i < notificationsList.size(); i++) {
      Notifications notifications = notificationsList.get(i);
      if (notifications.getNotification_id() == notificationId) {
        notifications.setIs_read(1);
        notifyItemChanged(i);
        return true;
      }
    }
    return false;
  }

  /**
   * Must call before receive the data set from server and
   * start adding into the adapter
   * otherwise it will remove the original item from
   * the collection list
   */
  public void removeLoadMore() {
    if (notificationsList != null && notificationsList.size() > 0) {
      notificationsList.remove(notificationsList.size() - 1);
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
    if (getItemViewType(position) == TYPE_ITEM) {
      if (holder instanceof NotificationViewHolder) {
        ((NotificationViewHolder) holder).bindViews(position);
      }
    }
  }

  @Override
  public int getItemCount() {
    return notificationsList.size();
  }

  @Override
  public int getItemViewType(int position) {
    if (notificationsList.get(position) == null) {
      return TYPE_LOAD;
    } else {
      return TYPE_ITEM;
    }
  }

  public Notifications getItem(int position) {
    return notificationsList.get(position);
  }

  public void setMoreDataAvailable(boolean moreDataAvailable) {
    isMoreDataAvailable = moreDataAvailable;
  }

  public void onUpdateDataSet(List<Notifications> updatedList) {
    notificationsList.addAll(0, updatedList);
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

  public void setOnItemClick(OnItemClick onItemClick) {
    this.onItemClick = onItemClick;
  }

  private String getResource(int resourceId) {
    return mContext.getResources().getString(resourceId);
  }

  public interface OnLoadMoreListener {
    void onLoadMore();
  }

  public interface OnItemClick {
    void onItemClick(Notifications chooseItems);
  }

  public class NotificationViewHolder extends RecyclerView.ViewHolder {

    private ItemNotifBinding mItemNotifBinding;

    public NotificationViewHolder(ItemNotifBinding itemNotifBinding) {
      super(itemNotifBinding.getRoot());
      this.mItemNotifBinding = itemNotifBinding;
    }

    private void bindViews(int location) {
      final Notifications rawData = notificationsList.get(location);
      viewBinding(rawData);
      String timeString = DateTimeUtils.UTCtoDefault(rawData.getCreated_date());
      String timeAgoString = DateTimeUtils.timeAgo(getContext(),timeString);
      mItemNotifBinding.textViewDate.setText(timeAgoString);
      if (rawData.getIs_read() == 0) {
        mItemNotifBinding.getRoot().setBackgroundResource(backGround);
      } else {
        mItemNotifBinding.getRoot().setBackgroundResource(R.drawable.drw_card_background);
      }
      itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (onItemClick != null) {
            onItemClick.onItemClick(rawData);
          }
        }
      });
    }

    private void viewBinding(Notifications notifications) {
      mItemNotifBinding.textViewMessage.setText(notifications.getMessage());
      mItemNotifBinding.textViewDate.setText(notifications.getCreated_date());

      switch (notifications.getNotification_type()) {
        case 1: {//accept/decline
          if (notifications.getIs_awared() == 1) {
            mItemNotifBinding.imageViewIcon.setImageResource(R.drawable.ic_job_accepts);
          } else {
            mItemNotifBinding.imageViewIcon.setImageResource(R.drawable.ic_job_decline);
          }
          break;
        }
        case 2: {
          //job status
          int jobStatus = notifications.getJob_status();
          if (jobStatus == RestFields.STATUS.PAUSED) {
            mItemNotifBinding.imageViewIcon.setImageResource(R.drawable.ic_paused);
          } else if (jobStatus == RestFields.STATUS.IN_PROCESS) {
            mItemNotifBinding.imageViewIcon.setImageResource(R.drawable.ic_inprogress);
          } else if (jobStatus == RestFields.STATUS.COMPLETED) {
            mItemNotifBinding.imageViewIcon.setImageResource(R.drawable.ic_completed);
          } else if (jobStatus == RestFields.STATUS.APPLIED) {
            mItemNotifBinding.imageViewIcon.setImageResource(R.drawable.ic_applied);
          } else if (jobStatus == RestFields.STATUS.CANCELLED) {
            mItemNotifBinding.imageViewIcon.setImageResource(R.drawable.ic_cancelled);
          } else if (jobStatus == RestFields.STATUS.RESUMED) {
            mItemNotifBinding.imageViewIcon.setImageResource(R.drawable.ic_inprogress);
          } else {
            // open
            mItemNotifBinding.imageViewIcon.setImageResource(R.drawable.ic_open);
          }
          break;
        }
        case 3: {
          //New Category’s Job Added
          mItemNotifBinding.imageViewIcon.setImageResource(R.drawable.ic_new_job);
          break;
        }
      }
      DrawableCompat.setTint(mItemNotifBinding.imageViewIcon.getDrawable().mutate(), tintColor);
    }
  }

  public class LoadViewHolder extends RecyclerView.ViewHolder {

    public LoadViewHolder(LoadMoreItemBinding itemView) {
      super(itemView.getRoot());
    }
  }
}
