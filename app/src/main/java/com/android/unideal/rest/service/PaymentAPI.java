package com.android.unideal.rest.service;

import com.android.unideal.questioner.data.CardData;
import com.android.unideal.questioner.data.TokenData;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.response.payment.CardsResponse;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by bhavdip on 29/12/16.
 */

public interface PaymentAPI {
  @FormUrlEncoded
  @POST("oauth2/token")
  Call<TokenData> getAccessToken(@Header(RestFields.KEY_AUTHORIZATION) String authorization,
      @Field(RestFields.KEY_GRANT_TYPE) String grantType);

  @POST("vault/credit-card")
  @Headers("Content-Type: application/json")
  Call<CardData> saveCreditCardInfo(@Header(RestFields.KEY_AUTHORIZATION) String authorization,
      @Body Map<String, Object> params);

  @GET("vault/credit-cards")
  @Headers("Content-Type: application/json")
  Call<CardsResponse> getCreditCards(@Header(RestFields.KEY_AUTHORIZATION) String authorization,
      @Query("page") int page, @Query(RestFields.KEY_CUSTOMER_ID) String externalCustomerId);

  @DELETE("vault/credit-cards/{credit_card_id}")
  @Headers("Content-Type: application/json")
  Call<CardData> removeCreditCard(@Header(RestFields.KEY_AUTHORIZATION) String authorization,
      @Path(RestFields.KEY_CREDIT_CARD_ID) String creditCardId);
}
