package com.android.unideal.data;

/**
 * Created by bhavdip on 10/9/16.
 */

public class Transaction {

  private int transaction_id;
  private int type;
  private String message;
  private int job_id;
  private int transaction_amount;
  private String transaction_date;

  public int getType() {
    return type;
  }

  public Transaction setType(int type) {
    this.type = type;
    return this;
  }

  public String getMessage() {
    return message;
  }

  public Transaction setMessage(String message) {
    this.message = message;
    return this;
  }

  public int getJob_id() {
    return job_id;
  }

  public Transaction setJob_id(int job_id) {
    this.job_id = job_id;
    return this;
  }

  public int getTransaction_amount() {
    return transaction_amount;
  }

  public Transaction setTransaction_amount(int transaction_amount) {
    this.transaction_amount = transaction_amount;
    return this;
  }

  public String getTransaction_date() {
    return transaction_date;
  }

  public Transaction setTransaction_date(String transaction_date) {
    this.transaction_date = transaction_date;
    return this;
  }

  public int getTransaction_id() {
    return transaction_id;
  }

  public Transaction setTransaction_id(int transaction_id) {
    this.transaction_id = transaction_id;
    return this;
  }
}
