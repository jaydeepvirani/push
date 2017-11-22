package com.android.unideal.questioner.viewmodel;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import com.android.unideal.data.UserDetail;
import com.android.unideal.questioner.data.BankAccountData;
import com.android.unideal.rest.CallbackWrapper;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.SessionManager;
import com.android.unideal.util.SimpleTextWatcher;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;

/**
 * Created by ADMIN on 19-10-2016.
 */

public class BankAccountViewModel {
  private Context context;
  private Calendar myCalendar = Calendar.getInstance();
  private SimpleDateFormat sdf;
  private String myFormat;
  private DatePickerDialog.OnDateSetListener date;
  private String bank_Id, bank_Name;
  private FragmentManager manager;
  private BankAccountListener listener;

  public BankAccountViewModel(BankAccountListener listener, Context context,
      FragmentManager manager) {
    this.listener = listener;
    this.context = context;
    this.manager = manager;
  }

  public void openBirthDate(View view) {
    listener.hideKeyBoard();

    date = new DatePickerDialog.OnDateSetListener() {

      @Override
      public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // TODO Auto-generated method stub

        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        myFormat = "MM/dd/yy"; //In which you need put here
        sdf = new SimpleDateFormat(myFormat, Locale.US);
        listener.date(sdf.format(myCalendar.getTime()));
      }
    };
    new DatePickerDialog(context, date, myCalendar.get(Calendar.YEAR),
        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
  }

  public void onClickBack(View view) {
    listener.onBackPress();
  }

  public void openBankInfoWindow(View view) {
    listener.openBankInfoWindow();
  }

  public void onAddaccount(View view) {
    listener.addAccountValidation();
  }

  public TextWatcher OnUsernameChanged() {
    return new SimpleTextWatcher() {
      @Override
      public void onTextChanged(String newValue) {
        listener.textChanged(newValue);
      }
    };
  }

  public boolean isVisible(boolean visible) {
    return false;
  }

  public void fetchBankInfo() {
    listener.showProgressBar();
    Call<GenericResponse<List<BankAccountData>>> responseCall =
        RestClient.get().getBankList(getBankParams());
    responseCall.enqueue(new CallbackWrapper<List<BankAccountData>>() {
      @Override
      public void onSuccess(GenericResponse<List<BankAccountData>> response) {
        listener.hideProgressBar();
        listener.showInfo(response.getData());
      }

      @Override
      public void onFailure(GenericResponse<List<BankAccountData>> response) {
        listener.hideProgressBar();
        listener.onError(response.getMessage());
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        listener.hideProgressBar();
        listener.onError(errorResponse.getMessage());
      }
    });
  }

  public HashMap<String, Object> getBankParams() {
    UserDetail userDetail = SessionManager.get(context).getActiveUser();
    HashMap<String, Object> params = new HashMap<>();
    params.put(RestFields.KEY_USER_TYPE, RestFields.USER_TYPE_REQUESTER);
    params.put(RestFields.KEY_USER_ID, userDetail.getUserId());
    params.put(RestFields.KEY_FROM_SOCIAL_NETWORK, userDetail.getFromSocialNetwork());
    params.put(RestFields.KEY_GENDER, userDetail.getGender());
    return params;
  }

  public void submitBankInfo(String bankname, String accountno, String userid, String swiftcode) {

    DialogUtils.getInstance().showProgressDialog(context);
    Call<GenericResponse<BankAccountData>> responseCall =
        RestClient.get().passBankInfo(passBankParams(bankname, accountno, swiftcode));
    responseCall.enqueue(new CallbackWrapper<BankAccountData>() {
      @Override
      public void onSuccess(GenericResponse<BankAccountData> response) {
        DialogUtils.getInstance().hideProgressDialog();
        listener.passBankInfo(response.getData());
      }

      @Override
      public void onFailure(GenericResponse<BankAccountData> response) {
        DialogUtils.getInstance().hideProgressDialog();
        listener.onError(response.getMessage());
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        DialogUtils.getInstance().hideProgressDialog();
        listener.onError(errorResponse.getMessage());
      }
    });
  }

  public HashMap<String, Object> passBankParams(String bankname, String accountno,
      String swiftcode) {
    UserDetail userDetail = SessionManager.get(context).getActiveUser();
    HashMap<String, Object> params = new HashMap<>();
    params.put(RestFields.KEY_USER_TYPE, RestFields.USER_TYPE_REQUESTER);
    params.put(RestFields.KEY_USER_ID, userDetail.getUserId());
    params.put(RestFields.KEY_BANK_NAME, bankname);
    params.put(RestFields.KEY_ACCOUNT_NUMBER, accountno);
    params.put(RestFields.KEY_SWIFT_CODE, swiftcode);

    return params;
  }

  public void deleteBankAccount(final BankAccountData accountData, final int position) {
    DialogUtils.getInstance().showProgressDialog(context);
    Call<GenericResponse<BankAccountData>> responseCall =
        RestClient.get().removeBankAccount(deleteBankParams(accountData));
    responseCall.enqueue(new CallbackWrapper<BankAccountData>() {
      @Override
      public void onSuccess(GenericResponse<BankAccountData> response) {
        DialogUtils.getInstance().hideProgressDialog();
        listener.deleteSuccessFully(position);
      }

      @Override
      public void onFailure(GenericResponse<BankAccountData> response) {
        DialogUtils.getInstance().hideProgressDialog();
        listener.onError(response.getMessage());
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        DialogUtils.getInstance().hideProgressDialog();
        listener.onError(errorResponse.getMessage());
      }
    });
  }

  public HashMap<String, Object> deleteBankParams(BankAccountData bankAccountData) {
    UserDetail userDetail = SessionManager.get(context).getActiveUser();
    HashMap<String, Object> params = new HashMap<>();
    params.put(RestFields.KEY_USER_TYPE, RestFields.USER_TYPE_REQUESTER);
    params.put(RestFields.KEY_USER_ID, userDetail.getUserId());
    params.put(RestFields.KEY_BANK_DETAIL_ID, bankAccountData.getBank_id());
    return params;
  }

  public void defaultBankSelection(BankAccountData bankAccountData) {
    DialogUtils.getInstance().showProgressDialog(context);
    Call<GenericResponse<BankAccountData>> responseCall =
        RestClient.get().defaultBankSelect(defaultBankParams(bankAccountData));
    responseCall.enqueue(new CallbackWrapper<BankAccountData>() {
      @Override
      public void onSuccess(GenericResponse<BankAccountData> response) {
        DialogUtils.getInstance().hideProgressDialog();
      }

      @Override
      public void onFailure(GenericResponse<BankAccountData> response) {
        DialogUtils.getInstance().hideProgressDialog();
        listener.onError(response.getMessage());
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        DialogUtils.getInstance().hideProgressDialog();
        listener.onError(errorResponse.getMessage());
      }
    });
  }

  public HashMap<String, Object> defaultBankParams(BankAccountData bankAccountData) {
    UserDetail userDetail = SessionManager.get(context).getActiveUser();
    HashMap<String, Object> params = new HashMap<>();
    params.put(RestFields.KEY_USER_TYPE, RestFields.USER_TYPE_REQUESTER);
    params.put(RestFields.KEY_USER_ID, userDetail.getUserId());
    params.put(RestFields.KEY_BANK_DETAIL_ID, bankAccountData.getBank_id());
    return params;
  }

  public interface BankAccountListener {

    void removeBankInfo(BankAccountData accountData, int positio);

    void bindBankSpinnerOptions();

    void onBackPress();

    void error();

    void openBankInfoWindow();

    void hidePopupWindow();

    void passBankInfo(BankAccountData bankAccountData);

    void addAccountValidation();

    void textChanged(String chnaged);

    void date(String date);

    void showAdapter();

    void showInfo(List<BankAccountData> bankaccountdata);

    void hideKeyBoard();

    void showProgressBar();

    void hideProgressBar();

    void onError(String message);

    void deleteSuccessFully(int position);

    void defaultBankSelect(BankAccountData bankAccountData);
  }
}
