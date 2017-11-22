package com.android.unideal.questioner.viewmodel;

import com.android.unideal.data.Transaction;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.response.TransactionResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by bhavdip on 20/1/17.
 */

public class TransactionViewModel {

  private ViewModelListener modelListener;
  private int mPageIndex = 0;

  public TransactionViewModel(ViewModelListener listener) {
    this.modelListener = listener;
  }

  public void fetchPaymentHistory(int userId) {
    if (modelListener != null) {
      modelListener.showProgressBar(true);
    }
    if (mPageIndex > 0) {
      modelListener.showLoadMoreView(true);
    }
    Call<TransactionResponse> responseCall =
        RestClient.get().fetchTransactionHistory(userId, mPageIndex);
    responseCall.enqueue(new Callback<TransactionResponse>() {
      @Override
      public void onResponse(Call<TransactionResponse> call,
          Response<TransactionResponse> response) {
        TransactionResponse tranResponse = response.body();
        if (tranResponse == null) return;

        if (mPageIndex > 0) {
          modelListener.showLoadMoreView(false);
        }
        //once we add result message data into the adapter
        //then check the has more or not for load more enable disable
        if (tranResponse.isHasMore()) {
          modelListener.setMoreDataAvailable(true);
        } else {
          modelListener.setMoreDataAvailable(false);
        }
        //we would allow to increase the page index
        mPageIndex++;
        if (tranResponse != null) {
          int totalSpend = tranResponse.getTotalSpend();
          modelListener.bindMySpends(totalSpend);
          modelListener.bindMyPromoBalance(tranResponse.getPromoBalance());
        }
        List<Transaction> transactionList = tranResponse.getTransactionList();
        if (transactionList != null && transactionList.size() > 0) {
          modelListener.showProgressBar(false);
          modelListener.bindTransactionList(transactionList);
        } else {
          modelListener.showProgressBar(false);
          modelListener.bindEmptyView();
        }
      }

      @Override
      public void onFailure(Call<TransactionResponse> call, Throwable t) {
        if (modelListener != null) {
          modelListener.showProgressBar(false);
        }
        if (mPageIndex > 0) {
          modelListener.showLoadMoreView(false);
        }
      }
    });
  }

  public void onActivityCreated() {
    modelListener.startBindingViews();
  }

  public int getCurrentIndex() {
    return mPageIndex;
  }

  public interface ViewModelListener {
    void startBindingViews();

    void showProgressBar(boolean visibility);

    void showLoadMoreView(boolean visibility);

    void bindMySpends(int totalSpend);

    void bindMyPromoBalance(int balance);

    void bindTransactionList(List<Transaction> transactionList);

    void setMoreDataAvailable(boolean visibility);

    void bindEmptyView();
  }
}
