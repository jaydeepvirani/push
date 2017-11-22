package com.android.unideal.questioner.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import com.android.unideal.R;
import com.android.unideal.databinding.BankAccountInfoBinding;
import com.android.unideal.enums.AppMode;
import com.android.unideal.questioner.adapter.BankAccountAdapter;
import com.android.unideal.questioner.data.BankAccountData;
import com.android.unideal.questioner.view.dialog.EnterBankNameDialog;
import com.android.unideal.questioner.viewmodel.BankAccountViewModel;
import com.android.unideal.util.AppUtility;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.TransitionUtility;
import java.util.List;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by ADMIN on 19-10-2016.
 */

public class BankAccountInfoActivity extends AppCompatActivity
    implements BankAccountViewModel.BankAccountListener {
  public static final int REQUEST_CODE = 1011;
  private String bankName, accountNo, userId, birthdate, swiftCode, newbankName;
  private BankAccountInfoBinding mInfoBinding;
  private BankAccountViewModel mInfoViewModel;
  private BankAccountAdapter mBankAccountAdapter;
  private Animation animShow, animHide;
  private boolean bankNameflag = false;

  /**
   * @param fragment
   */
  public static void startBankAccountInfoActivity(Fragment fragment) {
    Intent intent = new Intent(fragment.getContext(), BankAccountInfoActivity.class);
    fragment.startActivityForResult(intent, REQUEST_CODE);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mInfoBinding = DataBindingUtil.setContentView(this, R.layout.activity_bank_acc_info);
    mInfoViewModel =
        new BankAccountViewModel(BankAccountInfoActivity.this, this, getSupportFragmentManager());
    mInfoBinding.setViewmodel(mInfoViewModel);
    setUpRecyclerView();
    setUpAnimation();
    hidePopUpWindow();
    bindBankSpinnerOptions();
    mInfoViewModel.fetchBankInfo();
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  @Override
  protected void onResume() {
    super.onResume();
    TransitionUtility.openBottomToTopTransition(this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    TransitionUtility.closeBottomToTopTransition(this);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  @Override
  public void onBackPressed() {
    onBackPress();
  }

  private void setUpRecyclerView() {
    //show recyclerview to show banklist
    mBankAccountAdapter = new BankAccountAdapter(this);
    mInfoBinding.recyclerViewBankInfo.setAdapter(mBankAccountAdapter);
    mInfoBinding.recyclerViewBankInfo.setLayoutManager(
        new LinearLayoutManager(getApplicationContext()));
  }

  private void setUpAnimation() {
    //TODO default visible
    mInfoBinding.slidingPopupWindow.setVisibility(View.VISIBLE);
    animShow = AnimationUtils.loadAnimation(this, R.anim.sliding_popup_show);
    animHide = AnimationUtils.loadAnimation(this, R.anim.sliding_popup_hide);
  }

  @Override
  public void onBackPress() {
    if (mInfoBinding.slidingPopupWindow.getVisibility() == View.VISIBLE) {
      hidePopUpWindow();
    } else {
      finish();
    }
  }

  @Override
  public void error() {
    mInfoBinding.dateofbirth.getBackground()
        .setColorFilter(getResources().getColor(R.color.colorPersianGreen),
            PorterDuff.Mode.SRC_ATOP);
  }

  @Override
  public void openBankInfoWindow() {
    //show the sliding bottom panel
    if (mInfoBinding.slidingPopupWindow.getVisibility() != View.VISIBLE) {
      showPopUpWindow();
    } else {
      hidePopUpWindow();
    }
  }

  @Override
  public void hidePopupWindow() {
    hidePopUpWindow();
  }

  @Override
  public void passBankInfo(BankAccountData bankAccountData) {
    //to add a bank user that add or update bank into banklist
    mBankAccountAdapter.addBankAccountInfo(bankAccountData);
    mBankAccountAdapter.notifyDataSetChanged();
    mInfoBinding.emptyView.setVisibility(View.GONE);
  }

  @Override
  public void addAccountValidation() {
    bankName = mInfoBinding.banksOptions.getSelectedItem().toString();
    accountNo = mInfoBinding.editTextAccountNumber.getText().toString();
    birthdate = mInfoBinding.dateofbirth.getText().toString();
    swiftCode = mInfoBinding.swiftcode.getText().toString().trim();
    newbankName = mInfoBinding.enterBankName.getText().toString();
    if (bankNameflag) {
      if (TextUtils.isEmpty(mInfoBinding.enterBankName.getText().toString())) {
        mInfoBinding.enterBankName.getBackground()
            .setColorFilter(ContextCompat.getColor(this, R.color.colorRed),
                PorterDuff.Mode.SRC_ATOP);
      } else if (TextUtils.isEmpty(accountNo)) {
        mInfoBinding.editTextAccountNumber.getBackground()
            .setColorFilter(ContextCompat.getColor(this, R.color.colorRed),
                PorterDuff.Mode.SRC_ATOP);
      } else if ((accountNo.length() < 16)) {
        DialogUtils.showToast(this, R.string.enteraccount);
      } else {
        mInfoViewModel.submitBankInfo(newbankName, accountNo, userId, swiftCode);
        mInfoBinding.editTextAccountNumber.getBackground()
            .setColorFilter(ContextCompat.getColor(this, R.color.colorWhite),
                PorterDuff.Mode.SRC_ATOP);
      }
    } else {
      if (TextUtils.isEmpty(accountNo)) {
        mInfoBinding.editTextAccountNumber.getBackground()
            .setColorFilter(ContextCompat.getColor(this, R.color.colorRed),
                PorterDuff.Mode.SRC_ATOP);
      } else if ((accountNo.length() < 16)) {
        DialogUtils.showToast(this, R.string.enteraccount);
      } else {
        mInfoViewModel.submitBankInfo(bankName, accountNo, userId, swiftCode);
        hidePopupWindow();
        hideKeyBoard();
        mInfoBinding.editTextAccountNumber.getBackground()
            .setColorFilter(ContextCompat.getColor(this, R.color.colorWhite),
                PorterDuff.Mode.SRC_ATOP);
      }
    }
  }

  @Override
  public void textChanged(String newValue) {
    mInfoBinding.editTextAccountNumber.getBackground()
        .setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
    mInfoBinding.swiftcode.getBackground()
        .setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
  }

  @Override
  public void date(String date) {
    mInfoBinding.dateofbirth.setText(date);
  }

  @Override
  public void showAdapter() {

  }

  @Override
  public void showInfo(List<BankAccountData> bankAccountData) {
    //show a banklist when user previously add a bank first of all add bank account
    if (bankAccountData != null && bankAccountData.size() > 0) {
      mBankAccountAdapter.addBankAccountInfo(bankAccountData);
      mBankAccountAdapter.notifyDataSetChanged();
      mInfoBinding.emptyView.setVisibility(View.INVISIBLE);
    } else {
      mBankAccountAdapter.clearData();
      mInfoBinding.emptyView.setVisibility(View.VISIBLE);
    }
  }

  @Override
  public void hideKeyBoard() {
    AppUtility.hideSoftKeyBoard(this, getCurrentFocus());
  }

  @Override
  public void showProgressBar() {
    mInfoBinding.progressBar2.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideProgressBar() {
    mInfoBinding.progressBar2.setVisibility(View.GONE);
  }

  @Override
  public void onError(String message) {
    mBankAccountAdapter.clearData();
    mInfoBinding.emptyView.setText(message);
    mInfoBinding.emptyView.setVisibility(View.VISIBLE);
  }

  @Override
  public void deleteSuccessFully(int position) {
    mBankAccountAdapter.removeBankAccountInfo(position);
    mBankAccountAdapter.notifyDataSetChanged();
  }

  @Override
  public void defaultBankSelect(BankAccountData bankAccountData) {
    mInfoViewModel.defaultBankSelection(bankAccountData);
  }

  private void showPopUpWindow() {
    mInfoBinding.editTextAccountNumber.getBackground()
        .setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
    mInfoBinding.slidingPopupWindow.setVisibility(View.VISIBLE);
    mInfoBinding.slidingPopupWindow.startAnimation(animShow);
  }

  private void hidePopUpWindow() {

    mInfoBinding.editTextAccountNumber.setText("");
    mInfoBinding.swiftcode.setText("");
    mInfoBinding.slidingPopupWindow.startAnimation(animHide);
    mInfoBinding.slidingPopupWindow.setVisibility(View.GONE);
  }

  @Override
  public void removeBankInfo(final BankAccountData bankAccountData, final int position) {
    //user can remove or delete a bank account to
    DialogUtils.showDialog(this, R.string.title_app_name, R.string.message_delete_account,
        R.string.btn_ok, R.string.btn_cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            mInfoViewModel.deleteBankAccount(bankAccountData, position);
          }
        });
  }

  @Override
  public void bindBankSpinnerOptions() {
    //load bank account option
    mInfoBinding.banksOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String bankname = parent.getSelectedItem().toString();
        if (bankname.equals("other")) {
          bankNameflag = true;
          mInfoBinding.bankLayout.setVisibility(View.VISIBLE);
          EnterBankNameDialog enterBankNameDialog = new EnterBankNameDialog(AppMode.QUESTIONER);
          enterBankNameDialog.setOnDialogBankListener(new EnterBankNameDialog.BankNameListener() {
            @Override
            public void onBank(String bankname) {
              mInfoBinding.enterBankName.setText(bankname);
            }
          });
          enterBankNameDialog.show(getSupportFragmentManager(), null);
        } else {
          bankNameflag = false;
          mInfoBinding.bankLayout.setVisibility(View.GONE);
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });
    ArrayAdapter<CharSequence> staticAdapter =
        ArrayAdapter.createFromResource(this, R.array.bank_options, R.layout.spinner_white_item);
    staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mInfoBinding.banksOptions.setAdapter(staticAdapter);
    final String bank = mInfoBinding.banksOptions.getSelectedItem().toString();
    Log.d("bank", bank);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    return super.onPrepareOptionsMenu(menu);
  }
}

