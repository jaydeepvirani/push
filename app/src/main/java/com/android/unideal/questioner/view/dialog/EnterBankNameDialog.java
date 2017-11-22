package com.android.unideal.questioner.view.dialog;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.databinding.EnterBankBinding;
import com.android.unideal.enums.AppMode;
import com.android.unideal.util.AppUtility;
import com.android.unideal.util.SimpleTextWatcher;

/**
 * Created by welcome on 13-06-2016.
 */
@SuppressLint("ValidFragment")
public class EnterBankNameDialog extends DialogFragment {
  private EnterBankBinding mBinding;
  private BankNameListener BankNameListener;
  private AppMode appMode;

  public EnterBankNameDialog(AppMode appModeDialog) {
    appMode = appModeDialog;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_enter_bank, container, false);
    if (appMode == AppMode.QUESTIONER) {
      mBinding.ok.getBackground()
          .setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPersianGreen),
              PorterDuff.Mode.SRC_ATOP);
    } else if (appMode == AppMode.AGENT) {
      mBinding.ok.getBackground()
          .setColorFilter(ContextCompat.getColor(getContext(), R.color.colorCerulean),
              PorterDuff.Mode.SRC_ATOP);
    }
    mBinding.ok.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onClickAddName();
      }
    });
    mBinding.cancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });
    detectFieldChange();
    return mBinding.getRoot();
  }

  private void detectFieldChange() {
    mBinding.entermessage.addTextChangedListener(new SimpleTextWatcher() {
      @Override
      public void onTextChanged(String newValue) {
        AppUtility.setEditTextColor(mBinding.entermessage, R.color.colorGallery);
      }
    });
  }

  public void setOnDialogBankListener(BankNameListener listener) {
    this.BankNameListener = listener;
  }

  @Override
  public void onStart() {
    super.onStart();
    if (getDialog() != null) {
      int width = ViewGroup.LayoutParams.WRAP_CONTENT;
      int height = ViewGroup.LayoutParams.WRAP_CONTENT;
      getDialog().getWindow().setLayout(width, height);
    }
  }

  public void onClickAddName() {
    if (TextUtils.isEmpty(mBinding.entermessage.getText().toString())) {
      mBinding.entermessage.getBackground()
          .setColorFilter(ContextCompat.getColor(getContext(), R.color.colorRed),
              PorterDuff.Mode.SRC_ATOP);
    } else {
      BankNameListener.onBank(mBinding.entermessage.getText().toString());
      dismiss();
    }
  }

  public void onTextChanged(String newValue) {
    if (TextUtils.isEmpty(newValue)) {
      mBinding.entermessage.getBackground()
          .setColorFilter(ContextCompat.getColor(getContext(), R.color.colorRed),
              PorterDuff.Mode.SRC_ATOP);
    } else {
      mBinding.entermessage.getBackground()
          .setColorFilter(ContextCompat.getColor(getContext(), R.color.colorOsloGray),
              PorterDuff.Mode.SRC_ATOP);
    }
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
  }

  public interface BankNameListener {
    void onBank(String bankname);
  }
}
