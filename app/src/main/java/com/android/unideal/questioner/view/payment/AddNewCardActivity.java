package com.android.unideal.questioner.view.payment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import com.android.unideal.R;
import com.android.unideal.databinding.ActivityAddNewCard;
import com.android.unideal.enums.AppMode;
import com.android.unideal.flurry.FlurryManager;
import com.android.unideal.questioner.data.CardData;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import com.android.unideal.rest.response.payment.Detail;
import com.android.unideal.rest.response.payment.VaultError;
import com.android.unideal.util.AppUtility;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.SessionManager;
import com.android.unideal.util.TransitionUtility;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import rx.functions.Action1;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by bhavdip on 26/12/16.
 */

public class AddNewCardActivity extends AppCompatActivity
    implements NewCardViewModel.NewCardListener {

  private static final String TAG = "AddNewCardActivity";
  private static final String[] Months = new String[] {
      "January", "February", "March", "April", "May", "June", "July", "August", "September",
      "October", "November", "December"
  };
  private NewCardViewModel mNewCardViewModel;
  private ActivityAddNewCard mActivityAddNewCard;
  private CardPatternHelper mCardPatternHelper;
  private int chooseYears;
  private int chooseMonths;

  /**
   * @param activity
   * @return
   */
  public static Intent getAddNewCardIntent(Activity activity) {
    return new Intent(activity, AddNewCardActivity.class);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mActivityAddNewCard = DataBindingUtil.setContentView(this, R.layout.activity_add_nw_card);
    mNewCardViewModel = new NewCardViewModel(this, this);
    mNewCardViewModel.onCreate();
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
  public void startBindingViews() {
    mCardPatternHelper = mActivityAddNewCard.editTextCreditCard.getCardHelper();
    registerOnClick();
    startWatchCardChange();
    loadYearsList();
    loadMonthsList();
  }

  @Override
  public void showProgressBar() {
    DialogUtils.getInstance().showProgressDialog(this);
  }

  @Override
  public void hideProgressBar() {
    DialogUtils.getInstance().hideProgressDialog();
  }

  private void registerOnClick() {
    RxView.clicks(mActivityAddNewCard.btnAddCreditCard).subscribe(new Action1<Void>() {
      @Override
      public void call(Void aVoid) {
        startSubmitPaymentDetails();
      }
    });

    RxView.clicks(mActivityAddNewCard.btnBack).subscribe(new Action1<Void>() {
      @Override
      public void call(Void aVoid) {
        finish();
      }
    });
  }

  private void startSubmitPaymentDetails() {
    AppUtility.hideSoftKeyBoard(this, getCurrentFocus());
    if (validateCard()) {
      showProgressBar();//show progressbar
      CardData mCardData = new CardData();
      int activeUserId = SessionManager.get(this).getUserId();
      String customUserId = String.valueOf(activeUserId);
      mCardData.setPayer_id(customUserId);// payer id
      mCardData.setExternal_customer_id(customUserId);//external
      mCardData.setMerchant_id(customUserId);//merchant id
      mCardData.setFirst_name(mActivityAddNewCard.editTextFirstName.getText().toString());
      mCardData.setLast_name(mActivityAddNewCard.editTextLastName.getText().toString());
      String cardNumber =
          mCardPatternHelper.getNormalCardNumber(mActivityAddNewCard.editTextCreditCard.getText());
      Log.d(TAG, "startSubmitPaymentDetails: " + cardNumber);
      int cardId = mCardPatternHelper.checkCardType(cardNumber);
      mCardData.setType(mCardPatternHelper.checkCardTypeById(cardId));
      Log.d(TAG, "startSubmitPaymentDetails: " + mCardData.getType());
      mCardData.setNumber(cardNumber);
      int cardCVVNumber =
          Integer.parseInt(mActivityAddNewCard.editTextCardCVV.getText().toString());
      mCardData.setCvv(cardCVVNumber);
      mCardData.setExpire_month(chooseMonths);
      mCardData.setExpire_year(chooseYears);
      //save credit card data
      mNewCardViewModel.saveCreditCard(mCardData);
    }
  }

  private boolean validateCard() {
    if (mActivityAddNewCard.editTextCreditCard.getText().toString().trim().length() == 0) {
      DialogUtils.showToast(this, R.string.error_pay_not_card);
      return false;
    } else if (!mCardPatternHelper.isValid()) {
      DialogUtils.showToast(this, R.string.error_pay_not_valid_card);
      return false;
    } else if (mActivityAddNewCard.editTextFirstName.getText().toString().trim().length() == 0) {
      DialogUtils.showToast(this, R.string.error_pay_holder_name);
      return false;
    } else if (mActivityAddNewCard.editTextLastName.getText().toString().trim().length() == 0) {
      DialogUtils.showToast(this, R.string.error_pay_holder_name);
      return false;
    } else if (mActivityAddNewCard.editTextCardCVV.getText().toString().trim().length() == 0) {
      DialogUtils.showToast(this, R.string.error_pay_not_valid_cvv);
      return false;
    } else if (chooseMonths == 0) {
      DialogUtils.showToast(this, R.string.error_pay_car_expire);
      return false;
    } else if (chooseYears == 0) {
      DialogUtils.showToast(this, R.string.error_pay_car_expire);
      return false;
    }
    return true;
  }

  private void startWatchCardChange() {
    RxTextView.afterTextChangeEvents(mActivityAddNewCard.editTextCreditCard)
        .subscribe(new Action1<TextViewAfterTextChangeEvent>() {
          @Override
          public void call(TextViewAfterTextChangeEvent textViewAfterTextChangeEvent) {
            String inputCardNumber = textViewAfterTextChangeEvent.editable().toString();
            if (inputCardNumber.length() > 0 && !mCardPatternHelper.findCardNumber(
                inputCardNumber)) {
              String numbersOnly = mCardPatternHelper.keepNumbersOnly(inputCardNumber);
              String code = mCardPatternHelper.formatNumbersAsCode(numbersOnly);
              mActivityAddNewCard.editTextCreditCard.setText(code);
              // You could also remember the previous position of the cursor
              mActivityAddNewCard.editTextCreditCard.setSelection(code.length());
            }
          }
        });
  }

  private void loadYearsList() {
    ArrayList<String> years = new ArrayList<>();
    Calendar calendar = Calendar.getInstance();
    int currentYear = calendar.get(Calendar.YEAR);
    calendar.add(Calendar.YEAR, 20);
    int featureYear = calendar.get(Calendar.YEAR);
    for (int i = currentYear; i <= featureYear; i++) {
      years.add(Integer.toString(i));
    }
    final CardDurationAdapter<String> adapterYears = new CardDurationAdapter<>(this, years);
    mActivityAddNewCard.years.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        chooseYears = Integer.parseInt(adapterYears.getItem(position));
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });
    mActivityAddNewCard.years.setAdapter(adapterYears);
  }

  private void loadMonthsList() {
    final CardDurationAdapter<String> adapterMonths =
        new CardDurationAdapter<>(this, Arrays.asList(Months));
    mActivityAddNewCard.months.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        chooseMonths = (position + 1);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });
    mActivityAddNewCard.months.setAdapter(adapterMonths);
  }

  @Override
  public void onSaveSuccessfully(GenericResponse response, final CardData cardData) {
    //Flurry Add Card Successfully
    FlurryManager.cardInsert(Integer.parseInt(cardData.getPayer_id()), AppMode.QUESTIONER.name());

    DialogUtils.showDialog(this, response.getMessage(), new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        Intent paymentIntent = new Intent();
        paymentIntent.putExtra(ManageCardActivity.KEY_CAR_DETAILS, cardData);
        setResult(RESULT_OK, paymentIntent);
        finish();
      }
    });
  }

  @Override
  public void onFailureToSaveCard(VaultError vaultError) {
    if (vaultError != null) {
      if (vaultError.getDetails() != null && vaultError.getDetails().size() > 0) {
        StringBuilder errorBuilder = new StringBuilder();
        for (Detail detail : vaultError.getDetails()) {
          errorBuilder.append(detail.getIssue());
          errorBuilder.append("\n");
        }
        Log.d(TAG, "onFailureToSaveCard: " + errorBuilder);
        DialogUtils.showDialog(this, errorBuilder.toString(),
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
              }
            });
      }
    }
  }

  @Override
  public void onErrorValidateCard(ErrorResponse errorResponse) {
    Log.d(TAG, "onErrorValidateCard: " + errorResponse.getMessage());
    DialogUtils.showDialog(this, errorResponse.getMessage(), new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {

      }
    });
  }
}
