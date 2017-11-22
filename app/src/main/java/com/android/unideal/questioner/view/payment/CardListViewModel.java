package com.android.unideal.questioner.view.payment;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import com.android.unideal.questioner.data.TokenData;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.RestFields;
import com.android.unideal.util.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.login.widget.ProfilePictureView.TAG;

/**
 * Created by bhavdip on 12/30/16.
 */

public class CardListViewModel {
  private Context mActivityContext;
  private CardListListener mListListener;

  public CardListViewModel(Context activityContext, CardListListener listListener) {
    this.mActivityContext = activityContext;
    this.mListListener = listListener;
  }

  private Context getActivityContext() {
    return mActivityContext;
  }

  public void onCreate() {
    mListListener.startBindingView();
  }

  public void generateAccessToken() {
    mListListener.showProgressBar();

    //1.Get the client id from the session manager
    //2.Get the Secret id from the session manager
    SessionManager sessionManager = SessionManager.get(getActivityContext());
    String clientKey = sessionManager.getPayCredentials()[0];
    String secretKey = sessionManager.getPayCredentials()[1];
    final String credentials = (clientKey + ":" + secretKey);
    String encodeCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
    String basicAuth = "Basic " + encodeCredentials;
    Call<TokenData> tokenDataCall =
        RestClient.getPaymentAPI().getAccessToken(basicAuth, RestFields.VALUE_GRANT_TYPE);
    tokenDataCall.enqueue(new Callback<TokenData>() {
      @Override
      public void onResponse(Call<TokenData> call, Response<TokenData> response) {
        mListListener.hideProgressBar();
        if (response.isSuccessful()) {
          TokenData mTokenData = response.body();
          if (mTokenData != null && !TextUtils.isEmpty(mTokenData.getAccess_token())) {
            Log.d(TAG, "AccessToken: " + mTokenData.getAccess_token());
            SessionManager.get(getActivityContext()).saveTokenDetails(mTokenData);
            mListListener.loadCardListFragment();
          } else {
            SessionManager.get(getActivityContext()).clearTokenDetails();
            mListListener.onTokenAccessError(response.message());
          }
        }
      }

      @Override
      public void onFailure(Call<TokenData> call, Throwable t) {
        Log.e(TAG, "onFailure: " + t.getMessage());
        mListListener.hideProgressBar();
        mListListener.onTokenAccessError(t.getMessage());
      }
    });
  }

  public interface CardListListener {
    void startBindingView();

    void showProgressBar();

    void hideProgressBar();

    void onTokenAccessError(String errorMessage);

    void loadCardListFragment();
  }
}
