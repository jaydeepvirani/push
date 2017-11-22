package com.android.unideal.questioner.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.databinding.RequesterBankInfoBinding;
import com.android.unideal.questioner.data.BankAccountData;
import com.android.unideal.questioner.viewmodel.BankAccountViewModel;
import com.android.unideal.questioner.viewmodel.BankInfoItemViewModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADMIN on 19-10-2016.
 */

public class BankAccountAdapter
    extends RecyclerView.Adapter<BankAccountAdapter.BankAccountViewHolder> {

  static int selectedPosition = 0;
  BankAccountData bankAccountData;
  private List<BankAccountData> bankAccountDataList = new ArrayList<>();
  private BankAccountViewModel.BankAccountListener mBankAccountListener;

  public BankAccountAdapter(BankAccountViewModel.BankAccountListener listener) {
    this.mBankAccountListener = listener;
    bankAccountData = new BankAccountData();
  }

  public void removeBankAccountInfo(int location) {
    bankAccountDataList.remove(location);
  }

  public void addBankAccountInfo(List<BankAccountData> datas) {
    bankAccountDataList.clear();
    bankAccountDataList.addAll(datas);
  }

  public void addBankAccountInfo(BankAccountData bankAccountData) {
    bankAccountDataList.add(bankAccountData);
  }

  public void clearData() {
    bankAccountDataList.clear();
    notifyDataSetChanged();
  }

  @Override
  public BankAccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    RequesterBankInfoBinding binding =
        DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
            R.layout.item_bank_account_info_requester, parent, false);
    return new BankAccountViewHolder(binding, mBankAccountListener);
  }

  @Override
  public void onBindViewHolder(BankAccountViewHolder holder, int position) {
    holder.bindBankAccountInfo(bankAccountDataList.get(position), position);
  }

  @Override
  public int getItemCount() {
    return bankAccountDataList.size();
  }

  public class BankAccountViewHolder extends RecyclerView.ViewHolder
      implements View.OnClickListener {

    private RequesterBankInfoBinding infoBinding;
    private BankAccountViewModel.BankAccountListener listener;
    private BankAccountData accountData;
    private int location;
    private boolean onBind;

    public BankAccountViewHolder(RequesterBankInfoBinding infoBinding1,
        BankAccountViewModel.BankAccountListener listener) {
      super(infoBinding1.getRoot());
      this.infoBinding = infoBinding1;
      this.listener = listener;
    }

    public void bindBankAccountInfo(final BankAccountData accountData, final int position) {
      this.accountData = accountData;
      this.location = position;
      infoBinding.setViewmodel(new BankInfoItemViewModel(itemView.getContext(), accountData));
      onBind = true;
      if (selectedPosition == position) {
        infoBinding.jobcmplete.setChecked(true);
      } else {
        infoBinding.jobcmplete.setChecked(false);
      }
      onBind = false;
      infoBinding.getRoot().setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
          if (!onBind) {
            selectedPosition = location;
            notifyDataSetChanged();
            listener.defaultBankSelect(accountData);
          }
          return false;
        }
      });
      infoBinding.btnRemoveBank.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

      listener.removeBankInfo(accountData, location);
    }
  }
}

