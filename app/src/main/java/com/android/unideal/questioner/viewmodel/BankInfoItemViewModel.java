package com.android.unideal.questioner.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.util.Log;
import com.android.unideal.questioner.data.BankAccountData;

/**
 * Created by ADMIN on 19-10-2016.
 */

public class BankInfoItemViewModel extends BaseObservable {
  private BankAccountData bankAccountData;
  private Context mContext;

  public BankInfoItemViewModel(Context context, BankAccountData bankAccountData) {
    this.mContext = context;
    this.bankAccountData = bankAccountData;
  }

  public String getBankName() {
    return bankAccountData.getBank_name();
  }

  public String getAccountNumber() {
    String accountNumber = bankAccountData.getBank_account_number();
    StringBuilder sb = new StringBuilder();
    int length;
    length = accountNumber.length() - 4;
    int y = length / 4;
    String s1 = "****";
    Log.d("no", String.valueOf(y));
    sb.append(accountNumber.substring(accountNumber.length() - 4));

    return String.valueOf(sb);
  }
}
