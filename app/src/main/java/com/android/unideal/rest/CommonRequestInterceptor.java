package com.android.unideal.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by DSK02 on 6/8/2016.
 */

public class CommonRequestInterceptor implements Interceptor {
  private HashMap<String, String> requstHashMap = new HashMap<>();

  public CommonRequestInterceptor(HashMap<String, String> requstHashMap) {
    this.requstHashMap = requstHashMap;
  }

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    HttpUrl.Builder urlBuilder = request.url().newBuilder();
    for (Map.Entry<String, String> entry : requstHashMap.entrySet()) {
      urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
    }
    HttpUrl url = urlBuilder.build();
    request = request.newBuilder().url(url).build();
    return chain.proceed(request);
  }
}
