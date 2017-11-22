package com.android.unideal.questioner.view.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.data.Transaction;
import com.android.unideal.data.UserDetail;
import com.android.unideal.databinding.QuestionerBalanceBinding;
import com.android.unideal.questioner.adapter.BalanceHistoryAdapter;
import com.android.unideal.questioner.viewmodel.TransactionViewModel;
import com.android.unideal.util.SessionManager;
import java.util.List;

/**
 * Created by PC15 on 10/18/2016.
 */

public class QuestionerBalanceFragment extends Fragment
    implements TransactionViewModel.ViewModelListener, BalanceHistoryAdapter.OnLoadMoreListener {
  private static final String TAG = "QuestionerBalanceFragment";
  private QuestionerBalanceBinding mDataBinding;
  private BalanceHistoryAdapter mHistoryAdapter;
  private TransactionViewModel mViewModel;

  public static Fragment getInstance() {
    return new QuestionerBalanceFragment();
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mViewModel = new TransactionViewModel(this);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mDataBinding = loadBinding(inflater, container);
    return mDataBinding.getRoot();
  }

  public QuestionerBalanceBinding loadBinding(LayoutInflater inflater,
      @Nullable ViewGroup container) {
    return DataBindingUtil.inflate(inflater, R.layout.fragment_questioner_balance, container,
        false);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mViewModel.onActivityCreated();
    mViewModel.fetchPaymentHistory(getActiveUser().getUserId());
  }

  private UserDetail getActiveUser() {
    return SessionManager.get(getActivity()).getActiveUser();
  }

  @Override
  public void startBindingViews() {
    initializeHistoryList();
  }

  @Override
  public void showProgressBar(final boolean visibility) {
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (visibility) {
          mDataBinding.progressBarLayout.setVisibility(View.VISIBLE);
          //If it is first time then we hide and show the recycler view
          if (mViewModel.getCurrentIndex() == 0) {
            mDataBinding.spendTransactionList.setVisibility(View.GONE);
          }
        } else {
          mDataBinding.progressBarLayout.setVisibility(View.GONE);
          //If it is first time then we hide and show the recycler view
          mDataBinding.spendTransactionList.setVisibility(View.VISIBLE);
        }
      }
    });
  }

  @Override
  public void showLoadMoreView(boolean visibility) {
    if (visibility) {
      //Calling loadMore function in Runnable to fix the
      // java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling error
      mDataBinding.spendTransactionList.post(new Runnable() {
        @Override
        public void run() {
          mHistoryAdapter.addLoadMore();
        }
      });
    } else {
      mHistoryAdapter.removeLoadMore();
    }
  }

  @Override
  public void bindMySpends(int totalSpend) {
    mDataBinding.textViewTotalSpent.setText(getString(R.string.total_spend, totalSpend));
  }

  @Override
  public void bindMyPromoBalance(int balance) {
    mDataBinding.textViewPromo.setText(getString(R.string.total_spend, balance));
  }

  @Override
  public void bindTransactionList(List<Transaction> transactionList) {
    if (transactionList != null && transactionList.size() > 0) {
      mHistoryAdapter.addHistoryItems(transactionList);
      mHistoryAdapter.notifyDataSetChanged();
    }
  }

  @Override
  public void setMoreDataAvailable(boolean visibility) {
    mHistoryAdapter.setMoreDataAvailable(visibility);
  }

  @Override
  public void bindEmptyView() {
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        mDataBinding.emptyViewLayout.setVisibility(View.VISIBLE);
      }
    });
  }

  private void initializeHistoryList() {
    mHistoryAdapter = new BalanceHistoryAdapter(getActivity());
    mHistoryAdapter.setLoadMoreListener(this);
    mDataBinding.spendTransactionList.setHasFixedSize(true);
    mDataBinding.spendTransactionList.setLayoutManager(new LinearLayoutManager(getContext()));
    mDataBinding.spendTransactionList.setAdapter(mHistoryAdapter);
  }

  @Override
  public void onLoadMore() {
    int index = mHistoryAdapter.getDataSetSize();
    Log.d(TAG, "onLoadMore: " + index);
    //Start next page request here
    mViewModel.fetchPaymentHistory(getActiveUser().getUserId());
  }
}
