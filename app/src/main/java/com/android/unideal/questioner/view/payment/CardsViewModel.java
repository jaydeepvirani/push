package com.android.unideal.questioner.view.payment;

import android.app.Activity;
import android.util.Log;
import com.android.unideal.data.UserDetail;
import com.android.unideal.questioner.data.CardData;
import com.android.unideal.rest.EmptyCallbackWrapper;
import com.android.unideal.rest.PaymentCallbackWrapper;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import com.android.unideal.rest.response.payment.CardsResponse;
import com.android.unideal.rest.response.payment.VaultError;
import com.android.unideal.util.SessionManager;
import java.util.List;
import retrofit2.Call;

/**
 * Created by bhavdip on 26/12/16.
 */

public class CardsViewModel {

  private static final String TAG = "CardsViewModel";
  private Activity mActivityContext;
  private CardsListListener mCardsListListener;
  private int pageIndex = 1;

  public CardsViewModel(Activity activity, CardsListListener listListener) {
    this.mActivityContext = activity;
    this.mCardsListListener = listListener;
  }

  public void onActivityCreated() {
    mCardsListListener.startDataBinding();
  }

  public void resetIndex() {
    pageIndex = 1;
  }

  public void getCardsListByCustomerId(String externalCustomerId) {
    mCardsListListener.showProgressBar(false);
    String authorizationToken = SessionManager.get(mActivityContext).getAuthorizationToken();
    Call<CardsResponse> creditCardsList = RestClient.getPaymentAPI()
        .getCreditCards(authorizationToken, pageIndex++, externalCustomerId);
    creditCardsList.enqueue(new PaymentCallbackWrapper<CardsResponse>() {
      @Override
      public void onSuccess(CardsResponse cardData) {
        mCardsListListener.hideProgressBar(false);
        mCardsListListener.onReceiveCardList(cardData.getItems());
      }

      @Override
      public void onFailure(VaultError response) {
        mCardsListListener.hideProgressBar(false);
        mCardsListListener.onErrorCardList(response.getMessage());
        Log.d(TAG, "onFailure() called with: response = [" + response.getMessage() + "]");
      }

      @Override
      public void exception(VaultError errorResponse) {
        mCardsListListener.hideProgressBar(false);
        mCardsListListener.onFailToCardList(errorResponse.getMessage());
        Log.d(TAG, "exception() called with: errorResponse = [" + errorResponse.getMessage() + "]");
      }
    });
  }

  /**
   * We make two calls
   * 1.Remove from our back-end system
   * 2.Remove from the Paypal Server
   */
  public void detachedCardByUserId(final int itemPosition, final String creditCardId) {
    mCardsListListener.showProgressBar(true);
    UserDetail userDetail = SessionManager.get(mActivityContext).getActiveUser();
    int userId = userDetail.getUserId();
    Call<GenericResponse> detachedCreditCardCall =
        RestClient.get().detachedCreditCard(userId, creditCardId);
    detachedCreditCardCall.enqueue(new EmptyCallbackWrapper() {
      @Override
      public void onSuccess(GenericResponse response) {
        mCardsListListener.hideProgressBar(true);
        if (response.isSuccess() == 1 || response.isSuccess() == 2) {
          removeCardById(itemPosition, creditCardId);
        }
      }

      @Override
      public void onFailure(GenericResponse response) {
        mCardsListListener.hideProgressBar(true);
        mCardsListListener.onCardRemoveError(response.getMessage());
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        mCardsListListener.hideProgressBar(true);
        mCardsListListener.onCardRemoveError(errorResponse.getMessage());
      }
    });
  }

  public void removeCardById(final int itemPosition, String creditCardId) {
    mCardsListListener.showProgressBar(true);
    String authorizationToken = SessionManager.get(mActivityContext).getAuthorizationToken();
    Call<CardData> removeCardCall =
        RestClient.getPaymentAPI().removeCreditCard(authorizationToken, creditCardId);
    removeCardCall.enqueue(new PaymentCallbackWrapper<CardData>() {
      @Override
      public void onSuccess(CardData cardData) {
        mCardsListListener.hideProgressBar(true);
        if (cardData != null && cardData.getState().equals("ok")) {
          mCardsListListener.onCardRemoveSuccessfully(itemPosition);
        } else {
          mCardsListListener.onCardRemoveSuccessfully(itemPosition);
        }
      }

      @Override
      public void onFailure(VaultError response) {
        mCardsListListener.hideProgressBar(true);
        mCardsListListener.onCardRemoveError(response.getMessage());
      }

      @Override
      public void exception(VaultError errorResponse) {
        mCardsListListener.hideProgressBar(true);
        mCardsListListener.onCardRemoveError(errorResponse.getMessage());
      }
    });
  }

  /**
   * @param cardId card id as card token
   */
  public void saveDefaultCardId(final String cardId) {
    mCardsListListener.showProgressBar(true);
    UserDetail userDetail = SessionManager.get(mActivityContext).getActiveUser();
    Call<GenericResponse> saveDefaultCardCall =
        RestClient.get().setDefaultCardById(userDetail.getUserId(), cardId);
    saveDefaultCardCall.enqueue(new EmptyCallbackWrapper() {
      @Override
      public void onSuccess(GenericResponse response) {
        if (response.isSuccess() == 1) {
          mCardsListListener.setDefaultCardSuccessfully(cardId);
        }
        mCardsListListener.hideProgressBar(true);
      }

      @Override
      public void onFailure(GenericResponse response) {
        mCardsListListener.hideProgressBar(true);
        mCardsListListener.onFailToSetDefaultCard(response.getMessage());
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        mCardsListListener.hideProgressBar(true);
        mCardsListListener.onFailToSetDefaultCard(errorResponse.getMessage());
      }
    });
  }

  public interface CardsListListener {
    void startDataBinding();

    void showProgressBar(boolean showOutSide);

    void hideProgressBar(boolean showOutSide);

    void onReceiveCardList(List<CardData> cardDataList);

    void onErrorCardList(String errorMessage);

    void onFailToCardList(String errorMessage);

    void onCardRemoveSuccessfully(int position);

    void setDefaultCardSuccessfully(String cardId);

    void onFailToSetDefaultCard(String errorMessage);

    void onCardRemoveError(String errorMessage);
  }
}
