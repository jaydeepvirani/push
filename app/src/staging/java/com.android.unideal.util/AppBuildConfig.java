package com.android.unideal.util;

public class AppBuildConfig {
  public final static String FB_APP_ID = "909689245798403";
  private final static String BASE_URL = "http://192.168.1.169/unideal-web/api/";
  private final static String SOCKET_HOST_URL = "http://192.168.1.208:7879/";
  private final static String PAYMENT_HOST_URL = "https://api.sandbox.paypal.com/v1/";
  private final static String APP_LOG = "UNIBuy-Debug";

  public static String getBaseUrl() {
    return BASE_URL;
  }

  public static String getPaymentGatewayUrl() {
    return PAYMENT_HOST_URL;
  }

  public static String getAppLog() {
    return APP_LOG;
  }

  public static String getSocketHostURL() {
    return SOCKET_HOST_URL;
  }
}
