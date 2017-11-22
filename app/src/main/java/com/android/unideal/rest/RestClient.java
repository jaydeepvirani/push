package com.android.unideal.rest;

import com.android.unideal.BuildConfig;
import com.android.unideal.UniDealApplication;
import com.android.unideal.rest.service.AppAPI;
import com.android.unideal.rest.service.PaymentAPI;
import com.android.unideal.util.AppBuildConfig;
import com.android.unideal.util.SessionManager;
import com.github.simonpercic.oklog3.OkLogInterceptor;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Rest Client Manager for handle the Http Web API call.
 */
public class RestClient {

  private static final int TIMEOUT_SEC = 600000;
  private static Retrofit restAdapter;
  private static Retrofit paymentAdapter;
  private static AppAPI appAPI;
  private static PaymentAPI paymentAPI;

  static {
    setupAuthRestClient();
    setUpPayPalAdapter();
  }

  private static void setupAuthRestClient() {
    //Common Request in Header
    HashMap<String, String> requestHashMap = new HashMap<>();
    requestHashMap.put(RestFields.KEY_OS_TYPE, RestFields.OS_TYPE);
    String languageMode = SessionManager.get(UniDealApplication.getApplication()).getLanguageMode();
    requestHashMap.put(RestFields.KEY_LANGUAGE, languageMode);
    requestHashMap.put(RestFields.KEY_APP_VERSION, RestFields.APP_VERSION);

    CommonRequestInterceptor commonRequestInterceptor =
        new CommonRequestInterceptor(requestHashMap);

    // create an instance of OkLogInterceptor using a builder()
    OkLogInterceptor okLogInterceptor = OkLogInterceptor.builder().withAllLogData().build();

    //Ok HTTP Logging Interceptor
    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

    //list of interceptor list
    List<Interceptor> basicInterceptor = new ArrayList<>();
    basicInterceptor.add(commonRequestInterceptor); //common request Interceptor
    if (BuildConfig.DEBUG) {
      basicInterceptor.add(okLogInterceptor); // OK Log Interceptor
      basicInterceptor.add(loggingInterceptor);
    }

    //take Ok Http Client
    final OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();

    //Add Basic Interceptors
    for (Interceptor interceptor : basicInterceptor) {
      okHttpBuilder.addInterceptor(interceptor);
    }
    //TimeOut
    okHttpBuilder.readTimeout(TIMEOUT_SEC, TimeUnit.SECONDS);
    okHttpBuilder.writeTimeout(TIMEOUT_SEC, TimeUnit.SECONDS);
    okHttpBuilder.connectTimeout(TIMEOUT_SEC, TimeUnit.SECONDS);

    final OkHttpClient okHttpClient = okHttpBuilder.build();

    restAdapter = new Retrofit.Builder().baseUrl(AppBuildConfig.getBaseUrl())
        .client(okHttpClient)
        .addConverterFactory(JacksonConverterFactory.create())
        .build();
  }

  public static AppAPI get() {
    if (appAPI == null) {
      appAPI = restAdapter.create(AppAPI.class);
    }
    return appAPI;
  }

  public static void resetClient() {
    setupAuthRestClient();
    appAPI = null;
  }

  private static void setUpPayPalAdapter() {
    // create an instance of OkLogInterceptor using a builder()
    OkLogInterceptor okLogInterceptor = OkLogInterceptor.builder().withAllLogData().build();

    //HTTP Logging Interceptor
    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

    List<Interceptor> basicInterceptor = new ArrayList<>();
    if (BuildConfig.DEBUG) {
      basicInterceptor.add(okLogInterceptor); // OK Log Interceptor
      basicInterceptor.add(loggingInterceptor); //
    }

    //take Ok Http Client
    final OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();

    //Add Basic Interceptors
    for (Interceptor interceptor : basicInterceptor) {
      okHttpBuilder.addInterceptor(interceptor);
    }

    //TimeOut
    okHttpBuilder.readTimeout(TIMEOUT_SEC, TimeUnit.SECONDS);
    okHttpBuilder.writeTimeout(TIMEOUT_SEC, TimeUnit.SECONDS);
    okHttpBuilder.connectTimeout(TIMEOUT_SEC, TimeUnit.SECONDS);

    try {
      //builder.socketFactory(new TLSSocketFactory());
      X509TrustManager trustManager = getTrustManager();
      if (trustManager != null) {
        TLSSocketFactory socketFactory = new TLSSocketFactory();
        okHttpBuilder.sslSocketFactory(socketFactory, getTrustManager());
      }
    } catch (KeyManagementException | IllegalStateException | NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    OkHttpClient okHttpClient = okHttpBuilder.build();

    paymentAdapter = new Retrofit.Builder().baseUrl(AppBuildConfig.getPaymentGatewayUrl())
        .client(okHttpClient)
        .addConverterFactory(JacksonConverterFactory.create())
        .build();
  }

  private static X509TrustManager getTrustManager() {
    TrustManagerFactory trustManagerFactory;
    try {
      trustManagerFactory =
          TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init((KeyStore) null);

      TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
      if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
        //throw new IllegalStateException(
        //    "Unexpected default trust managers:" + Arrays.toString(trustManagers));
        return null;
      }
      return (X509TrustManager) trustManagers[0];
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return null;
    } catch (KeyStoreException e) {
      e.printStackTrace();
      return null;
    }
    //SSLContext sslContext = SSLContext.getInstance("SSL");
    //sslContext.init(null, new TrustManager[] { trustManager }, null);
    //SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
  }

  public static PaymentAPI getPaymentAPI() {
    if (paymentAPI == null) {
      paymentAPI = paymentAdapter.create(PaymentAPI.class);
    }
    return paymentAPI;
  }
}
