package com.android.unideal.rest.response;

import com.android.unideal.data.Transaction;
import com.android.unideal.data.socket.Messages;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Created by bhavdip on 6/12/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionResponse {
  @JsonProperty("success")
  private int success;
  @JsonProperty("status_code")
  private int status_code;
  @JsonProperty("has_more")
  private boolean hasMore;
  @JsonProperty("data")
  private List<Transaction> transactionList;
  @JsonProperty("total_spend")
  private int totalSpend;
  @JsonProperty("promo_balance")
  private int promoBalance;

  public int getSuccess() {
    return success;
  }

  public TransactionResponse setSuccess(int success) {
    this.success = success;
    return this;
  }

  public int getStatus_code() {
    return status_code;
  }

  public TransactionResponse setStatus_code(int status_code) {
    this.status_code = status_code;
    return this;
  }

  public boolean isHasMore() {
    return hasMore;
  }

  public TransactionResponse setHasMore(boolean hasMore) {
    this.hasMore = hasMore;
    return this;
  }

  public List<Transaction> getTransactionList() {
    return transactionList;
  }

  public TransactionResponse setTransactionList(List<Transaction> transactionList) {
    this.transactionList = transactionList;
    return this;
  }

  public int getTotalSpend() {
    return totalSpend;
  }

  public TransactionResponse setTotalSpend(int totalSpend) {
    this.totalSpend = totalSpend;
    return this;
  }

  public int getPromoBalance() {
    return promoBalance;
  }

  public TransactionResponse setPromoBalance(int promoBalance) {
    this.promoBalance = promoBalance;
    return this;
  }
}
