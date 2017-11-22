package com.android.unideal.questioner.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.data.Transaction;
import com.android.unideal.data.TransactionMode;
import com.android.unideal.databinding.ItemBalanceDataBinding;
import com.android.unideal.databinding.LoadMoreItemBinding;
import com.android.unideal.util.AppUtility;
import com.android.unideal.util.DateTimeUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC15 on 10/18/2016.
 */

public class BalanceHistoryAdapter extends RecyclerView.Adapter {

  public final int TYPE_TRANSACTION = 0;
  public final int TYPE_LOAD = 1;
  private List<Transaction> balanceHistoryList = new ArrayList<>();
  private Context mContext;
  private OnLoadMoreListener loadMoreListener;
  private boolean isLoading = false, isMoreDataAvailable = true;

  public BalanceHistoryAdapter(Context context) {
    this.mContext = context;
  }

  private Context getContext() {
    return mContext;
  }

  public void addHistoryItems(List<Transaction> list) {
    this.balanceHistoryList.addAll(list);
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == TYPE_TRANSACTION) {
      return new BalanceHistoryViewHolder(loadBinding(parent));
    } else {
      //if view type is load more
      return new LoadViewHolder(loadMoreBinding(parent));
    }
  }

  private ItemBalanceDataBinding loadBinding(ViewGroup parent) {
    return DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
        R.layout.item_questioner_balance, parent, false);
  }

  private LoadMoreItemBinding loadMoreBinding(ViewGroup parent) {
    return DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
        R.layout.item_load_more, parent, false);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (position >= getDataSetSize()
        && isMoreDataAvailable
        && !isLoading
        && loadMoreListener != null) {
      isLoading = true;
      loadMoreListener.onLoadMore();
    }
    if (getItemViewType(position) == TYPE_TRANSACTION) {
      if (holder instanceof BalanceHistoryViewHolder) {
        ((BalanceHistoryViewHolder) holder).bindingViews(position);
      }
    }
  }

  @Override public int getItemViewType(int position) {
    if (balanceHistoryList.get(position) == null) {
      return TYPE_LOAD;
    } else {
      return TYPE_TRANSACTION;
    }
  }

  @Override public int getItemCount() {
    return balanceHistoryList.size();
  }

  public int getDataSetSize() {
    if (balanceHistoryList != null) {
      return (balanceHistoryList.size() - 1);
    } else {
      return 0;
    }
  }

  /* notifyDataSetChanged is final method so we can't override it
         call adapter.notifyDataChanged(); after update the list
         */
  public void notifyDataChanged() {
    notifyDataSetChanged();
    isLoading = false;
  }

  /**
   * Must call inside the onRunUI thread
   * Must call before make a network call and receive
   * a data
   */
  public void addLoadMore() {
    if (balanceHistoryList != null && balanceHistoryList.size() > 0) {
      balanceHistoryList.add(null);
      notifyItemInserted(balanceHistoryList.size() - 1);
    }
  }

  /**
   * Must call before receive the data set from server and
   * start adding into the adapter
   * otherwise it will remove the original item from
   * the collection list
   */
  public void removeLoadMore() {
    if (balanceHistoryList != null && balanceHistoryList.size() > 0) {
      balanceHistoryList.remove(balanceHistoryList.size() - 1);
    }
  }

  public BalanceHistoryAdapter setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
    this.loadMoreListener = loadMoreListener;
    return this;
  }

  public BalanceHistoryAdapter setMoreDataAvailable(boolean moreDataAvailable) {
    isMoreDataAvailable = moreDataAvailable;
    return this;
  }

  public interface OnLoadMoreListener {
    void onLoadMore();
  }

  public class BalanceHistoryViewHolder extends RecyclerView.ViewHolder {
    private ItemBalanceDataBinding mBalanceDataBinding;

    public BalanceHistoryViewHolder(ItemBalanceDataBinding balanceDataBinding) {
      super(balanceDataBinding.getRoot());
      this.mBalanceDataBinding = balanceDataBinding;
    }

    private void bindingViews(int location) {
      Transaction rawData = balanceHistoryList.get(location);

      if (!TextUtils.isEmpty(rawData.getMessage())) {
        mBalanceDataBinding.textViewBalMsg.setText(rawData.getMessage());
      }
      mBalanceDataBinding.textViewAmount.setText(String.valueOf(rawData.getTransaction_amount()));
      mBalanceDataBinding.textViewBalDate.setText(
          DateTimeUtils.transactionDateTime(getContext(), rawData.getTransaction_date()));

      switch (TransactionMode.values()[rawData.getType() - 1]) {
        case REFUNDED: {
          mBalanceDataBinding.ribbon.setBackgroundColor(findColor(R.color.colorReceived));
          mBalanceDataBinding.viewSeparator.setBackgroundColor(findColor(R.color.colorReceived));
          mBalanceDataBinding.textViewBalMsg.setTextColor(findColor(R.color.colorReceived));
          mBalanceDataBinding.textViewAmount.setTextColor(findColor(R.color.colorReceived));
          mBalanceDataBinding.textViewCurrency.setTextColor(findColor(R.color.colorReceived));
          break;
        }
        case CHARGED: {
          mBalanceDataBinding.ribbon.setBackgroundColor(findColor(R.color.colorDeducted));
          mBalanceDataBinding.viewSeparator.setBackgroundColor(findColor(R.color.colorDeducted));
          mBalanceDataBinding.textViewBalMsg.setTextColor(findColor(R.color.colorDeducted));
          mBalanceDataBinding.textViewAmount.setTextColor(findColor(R.color.colorDeducted));
          mBalanceDataBinding.textViewCurrency.setTextColor(findColor(R.color.colorDeducted));
          break;
        }
      }
    }

    private String findString(int resourceID) {
      return AppUtility.findStringResource(getContext(), resourceID);
    }

    private int findColor(int colorResource) {
      return ContextCompat.getColor(getContext(), colorResource);
    }
  }

  public class LoadViewHolder extends RecyclerView.ViewHolder {
    public LoadViewHolder(LoadMoreItemBinding itemView) {
      super(itemView.getRoot());
    }
  }
}
