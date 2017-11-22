package com.android.unideal.rest;

import android.util.Base64;
import java.io.IOException;
import java.util.Map;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by DSK02 on 6/8/2016.
 */

public class TokenRequestInterceptor implements Interceptor {
  public TokenRequestInterceptor() {
  }

  @Override
  public Response intercept(Chain chain) throws IOException {
   /* String credentials = RestFields.APP_CLIENT_ID + ":" + RestFields.SECRET;
    final String basic = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
    Request request = chain.request();
    Request.Builder requestBuilder = request.newBuilder();
    requestBuilder.header("Accept", "application/json");
    requestBuilder.header("Accept-Language", "en_US");
    requestBuilder.header("Authorization", basic);

    HttpUrl.Builder urlBuilder = request.url().newBuilder();
    urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());

    requestBuilder.method(original.method(), original.body());
    return chain.proceed(request);*/
    return null;
  }
}
