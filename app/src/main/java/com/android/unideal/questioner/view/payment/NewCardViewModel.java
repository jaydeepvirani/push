package com.android.unideal.questioner.view.payment;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.android.unideal.questioner.data.CardData;
import com.android.unideal.rest.EmptyCallbackWrapper;
import com.android.unideal.rest.PaymentCallbackWrapper;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import com.android.unideal.rest.response.payment.VaultError;
import com.android.unideal.rest.service.PaymentAPI;
import com.android.unideal.util.SessionManager;
import java.util.HashMap;
import retrofit2.Call;

/**
 * Created by bhavdip on 29/12/16.
 */

public class NewCardViewModel {

  private static final String TAG = "NewCardViewModel";
  private NewCardListener mNewCardListener;
  private Context mActivityContext;

  public NewCardViewModel(Context activityContext, NewCardListener newCardListener) {
    mNewCardListener = newCardListener;
    this.mActivityContext = activityContext;
  }

  public void onCreate() {
    mNewCardListener.startBindingViews();
  }

  public void saveCreditCard(CardData nwCardData) {
    Log.d(TAG, "saveCreditCard() called with: nwCardData = [" + nwCardData.getType() + "]");
    String authorizationToken = SessionManager.get(mActivityContext).getAuthorizationToken();
    PaymentAPI paymentAPI = RestClient.getPaymentAPI();
    Call<CardData> cardDataCall =
        paymentAPI.saveCreditCardInfo(authorizationToken, getParamsCardData(nwCardData));
    cardDataCall.enqueue(new PaymentCallbackWrapper<CardData>() {
      @Override
      public void onSuccess(CardData cardData) {
        Log.d(TAG, "save credit card in vault server..");
        // send card details to third party server for validate the card id
        validateCreditCard(cardData);
      }

      @Override
      public void onFailure(VaultError vaultError) {
        mNewCardListener.hideProgressBar();
        if (vaultError != null && !TextUtils.isEmpty(vaultError.getMessage())) {
          Log.d(TAG, "onFailure: " + vaultError.getMessage());
          mNewCardListener.onFailureToSaveCard(vaultError);
        }
      }

      @Override
      public void exception(VaultError errorResponse) {
        mNewCardListener.hideProgressBar();
        Log.d(TAG, "exception: " + errorResponse.getMessage());
        mNewCardListener.onFailureToSaveCard(errorResponse);
      }
    });
  }

  /**
   * This call validate the credit card by doing a authorization on it.
   * If it is true card then it will response successful other wise
   * consider a card is false and r
   */
  public void validateCreditCard(final CardData nwCardData) {
    int activeUserId = SessionManager.get(mActivityContext).getUserId();
    Call<GenericResponse> validateCreditCardCall =
        RestClient.get().validateCreditCard(activeUserId, nwCardData.getId());
    validateCreditCardCall.enqueue(new EmptyCallbackWrapper() {
      @Override
      public void onSuccess(GenericResponse response) {
        Log.d(TAG, "saved credit card in valid and true card..");
        mNewCardListener.hideProgressBar();
        if (nwCardData.getState().equals("ok")) {
          mNewCardListener.onSaveSuccessfully(response, nwCardData);
        }
      }

      @Override
      public void onFailure(GenericResponse response) {
        Log.d(TAG,
            "saved credit card in false and we are going to remove it from vault's server..");
        // if failure means the card details is wrong reset the card information
        // and remove the card info from the Vault server
        removeCardById(nwCardData.getId(), response.getMessage());
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        mNewCardListener.hideProgressBar();
        mNewCardListener.onErrorValidateCard(errorResponse);
      }
    });
  }

  /**
   *
   * @param creditCardId
   * @param errorMessage
   */
  public void removeCardById(String creditCardId, final String errorMessage) {
    String authorizationToken = SessionManager.get(mActivityContext).getAuthorizationToken();
    Call<CardData> removeCardCall =
        RestClient.getPaymentAPI().removeCreditCard(authorizationToken, creditCardId);
    removeCardCall.enqueue(new PaymentCallbackWrapper<CardData>() {
      @Override
      public void onSuccess(CardData cardData) {
        //hide a progress bar and credit card is remove successfully.
        Log.d(TAG, "saved credit card in deleted successfully");
        mNewCardListener.hideProgressBar();
        mNewCardListener.onErrorValidateCard(new ErrorResponse().setMessage(errorMessage));
      }

      @Override
      public void onFailure(VaultError vaultError) {
        mNewCardListener.hideProgressBar();
        if (vaultError != null && !TextUtils.isEmpty(vaultError.getMessage())) {
          Log.d(TAG, "onFailure: " + vaultError.getMessage());
          mNewCardListener.onFailureToSaveCard(vaultError);
        }
      }

      @Override
      public void exception(VaultError errorResponse) {
        mNewCardListener.hideProgressBar();
        Log.d(TAG, "exception: " + errorResponse.getMessage());
        mNewCardListener.onFailureToSaveCard(errorResponse);
      }
    });
  }

  private HashMap<String, Object> getParamsCardData(CardData cardData) {
    HashMap<String, Object> params = new HashMap<>();
    params.put(RestFields.KEY_PAYER_ID, cardData.getPayer_id());
    params.put(RestFields.KEY_CUSTOMER_ID, cardData.getExternal_customer_id());
    params.put(RestFields.KEY_MERCHANT_ID, cardData.getMerchant_id());
    params.put(RestFields.KEY_TYPE, cardData.getType());
    params.put(RestFields.KEY_PHONE_NUMBER, cardData.getNumber());
    params.put(RestFields.KEY_EXPIRE_MONTH, cardData.getExpire_month());
    params.put(RestFields.KEY_EXPIRE_YEAR, cardData.getExpire_year());
    params.put(RestFields.KEY_FIRST_NAME, cardData.getFirst_name());
    params.put(RestFields.KEY_LAST_NAME, cardData.getLast_name());
    return params;
  }

  public interface NewCardListener {
    void startBindingViews();

    void showProgressBar();

    void hideProgressBar();

    void onSaveSuccessfully(GenericResponse response, CardData cardData);

    void onFailureToSaveCard(VaultError vaultError);

    void onErrorValidateCard(ErrorResponse errorResponse);
  }
}
