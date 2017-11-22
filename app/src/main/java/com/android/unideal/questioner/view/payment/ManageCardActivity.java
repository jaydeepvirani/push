package com.android.unideal.questioner.view.payment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import com.android.unideal.R;
import com.android.unideal.databinding.ActivityManageCard;
import com.android.unideal.util.DialogUtils;
import com.jakewharton.rxbinding.view.RxView;
import rx.functions.Action1;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by bhavdip on 26/12/16.
 */
public class ManageCardActivity extends AppCompatActivity
    implements CardListViewModel.CardListListener {

  public static final int REQUEST_NW_CARD = 131;
  public static final String KEY_CAR_DETAILS = "cardDetails";
  private static final String TAG = "ManageCardActivity";
  private ActivityManageCard mManageCardCard;
  private CardListViewModel mCardListViewModel;

  /**
   * Open Manage card Activity, This is return the choose card information.
   */
  public static void getManageCardIntent(Activity activity) {
    Intent cardIntent = new Intent(activity, ManageCardActivity.class);
    activity.startActivity(cardIntent);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mManageCardCard = DataBindingUtil.setContentView(this, R.layout.activity_manage_card);
    mCardListViewModel = new CardListViewModel(this, this);
    mCardListViewModel.onCreate();
    mCardListViewModel.generateAccessToken();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_NW_CARD) {
      if (resultCode == Activity.RESULT_OK) {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
          if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
          }
        }
      }
    }
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  @Override
  public void startBindingView() {
    RxView.clicks(mManageCardCard.btnAddCreditCard).subscribe(new Action1<Void>() {
      @Override
      public void call(Void aVoid) {
        addNewCardActivity();
      }
    });
    RxView.clicks(mManageCardCard.btnBack).subscribe(new Action1<Void>() {
      @Override
      public void call(Void aVoid) {
        finish();
      }
    });
  }

  @Override
  public void showProgressBar() {
    mManageCardCard.showProgressbar.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideProgressBar() {
    mManageCardCard.showProgressbar.setVisibility(View.GONE);
  }

  @Override
  public void onTokenAccessError(String errorMessage) {
    if (!TextUtils.isEmpty(errorMessage)) {
      DialogUtils.showToast(this, errorMessage);
    }
  }

  @Override
  public void loadCardListFragment() {
    loadCardFragment();
  }

  private void loadCardFragment() {
    if (!isFinishing()) {
      Fragment cardFragment = CardsFragment.getCardsListFragment();
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.container, cardFragment)
          .commitAllowingStateLoss();
    }
  }

  private void addNewCardActivity() {
    Intent nwCardIntent = AddNewCardActivity.getAddNewCardIntent(this);
    startActivityForResult(nwCardIntent, REQUEST_NW_CARD);
  }
}
