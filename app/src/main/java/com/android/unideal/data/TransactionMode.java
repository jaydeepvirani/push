package com.android.unideal.data;

/**
 * TransactionMode i.e Received Payment, Request for Withdraw
 * and Deducted Amount by UniDeal.
 */

public enum TransactionMode {

  CHARGED("charged"), REFUNDED("refunded");

  private String modeName;

  TransactionMode(String modeName) {
    this.modeName = modeName;
  }

  public String getModeName() {
    return modeName;
  }

  public TransactionMode setModeName(String modeName) {
    this.modeName = modeName;
    return this;
  }
}
