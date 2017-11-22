package com.android.unideal.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import com.android.unideal.R;
import com.android.unideal.data.Admin;
import com.android.unideal.data.UserDetail;
import com.android.unideal.enums.AppMode;
import com.android.unideal.questioner.data.TokenData;
import com.android.unideal.rest.response.AppConfigData;
import com.android.unideal.rest.response.Category;
import com.android.unideal.rest.response.SubCategory;
import com.android.unideal.util.converter.JacksonConverter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADMIN on 11-10-2016.
 */

public class SessionManager {
  private static final String TAG = "SessionManager";
  // LogCat tag
  private static final String PREF_KEY_CATEGORY_LIST = "category_list";
  private static final String PREF_KEY_USER_DETAIL = "user_detail";
  private static final String PREF_KEY_PROMO_CODE_STATUS = "promo_code_status";
  private static final String PREF_KEY_TERMS_COND_URL = "termsNConditionUrl";
  private static final String PREF_KEY_HELP_URL = "helpUrl";
  private static final String PREF_KEY_MIN_FEE = "minFee";
  private static final String PREF_KEY_MAX_FEE = "maxFee";
  private static final String KEY_NEW_JOB_NOTIFICATION = "newjob_notification";
  private static final String KEY_TRANSACTION_NOTIFICATION = "transaction_notification";
  private static final String KEY_JOB_STATUS_NOTIFICATION = "jobstatus_notification";
  private static final String KEY_APP_TOUR_GUIDE = "app_tour_guide";
  private static final String KEY_NUMBER = "phone_number";
  private static final String PREF_KEY_ADMIN_DETAIL = "admin_detail";
  private static final String PREF_NAME = "user_list";
  private static final String USER_MODE = "user_mode";
  private static final String USER_ID = "user_id";
  private static final String DEVICE_TOKEN = "device_token";
  private static final String PREF_REMEMBER_ME = "rememberme";
  private static final String PREF_KEY_SUB_CATEGORY_LIST = "sub_category_list";
  private static final String PREF_KEY_ACCESS_TOKEN = "access_token";
  private static final String PREF_KEY_TOKEN_TYPE = "token_type";
  private static final String PREF_KEY_SECRET = "pay_secret";
  private static final String PREF_KEY_CLIENT = "pay_client_key";
  private static final String PREF_KEY_DELIVERY_PLACE_URL = "delivery_place_url";
  private static final String PREF_KEY_DISCLAIMER_URL = "disclaimer_url";
  private static final String PREF_GOOGLE_STORE = "play_store_link";
  private static final String PREF_APPLE_STORE = "app_store_link";

  private static final String PREF_AGT_TERMS_URL = "agent_terms_condition_Url";

  private static final String PREF_IS_MENU_OPEN = "menuOpen";
  private static SessionManager SINGLETON;
  private Context _context;
  // Shared pref mode
  private int PRIVATE_MODE = 0;
  private SharedPreferences pref;
  private SharedPreferences.Editor editor;

  private SessionManager(Context context) {
    this._context = context;
    pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    editor = pref.edit();
  }

  public static SessionManager get(Activity activity) {
    return get(activity.getApplicationContext());
  }

  public static SessionManager get(Context context) {
    if (SINGLETON == null) {
      SINGLETON = new SessionManager(context);
    }
    return SINGLETON;
  }

  private String getString(int resourceId) {
    return _context.getString(resourceId);
  }

  public void saveUserDetail(UserDetail userDetail) {
    if (userDetail == null) {
      editor.putString(PREF_KEY_USER_DETAIL, null);
      editor.remove(USER_ID);
      editor.remove(USER_MODE);
      editor.commit();
    } else {
      editor.putString(PREF_KEY_USER_DETAIL, JacksonConverter.getStringFromObject(userDetail));
      editor.putInt(USER_MODE, userDetail.getUserType());
      editor.putInt(USER_ID, userDetail.getUserId());
      editor.commit();
    }
  }

  /**
   * This function is called when user successfully logged into the application
   * Get all previous save settings and update the existing settings to their older
   * settings
   */
  public void saveUsersSettings(UserDetail userDetail) {
    if (userDetail != null) {
      //topic notification
      setNewJobNotification(userDetail.getNewjobNotification());
      //job status
      setJobStatusNotification(userDetail.getNewjobNotification());
      //transaction
      setTransactionNotification(userDetail.getTransactionNotification());
      //app tour guide
      setShowTourGuide(userDetail.getApp_tour_guide());
    } else {
      setNewJobNotification(0);
      setJobStatusNotification(0);
      setTransactionNotification(0);
      setShowTourGuide(1);
    }
  }

  public int getUserMode() {
    return pref.getInt(USER_MODE, AppMode.AGENT.getValue());
  }

  public void setUserMode(int userMode) {
    UserDetail userDetail = getActiveUser();
    if (userDetail != null) {
      userDetail.setUserType(userMode);
      saveUserDetail(userDetail);
    }
  }

  public void saveUserDefaultCard(String defaultCardId) {
    UserDetail userDetail = getActiveUser();
    if (userDetail != null && !TextUtils.isEmpty(defaultCardId)) {
      userDetail.setDefault_card(defaultCardId);
      saveUserDetail(userDetail);
    } else {
      if (userDetail != null) {
        userDetail.setDefault_card("");
        saveUserDetail(userDetail);
      }
    }
  }

  public int getUserId() {
    return pref.getInt(USER_ID, -1);
  }

  public UserDetail getActiveUser() {
    String consumerInfo = pref.getString(PREF_KEY_USER_DETAIL, null);
    if (consumerInfo != null) {
      return JacksonConverter.getObjectFromJSON(consumerInfo, UserDetail.class);
    }
    return null;
  }

  public void saveAppConfiguration(AppConfigData appConfigData) {
    if (appConfigData != null) {
      editor.putBoolean(PREF_KEY_PROMO_CODE_STATUS, appConfigData.getPromocodeStatus() != 0);
      editor.putString(PREF_KEY_TERMS_COND_URL, appConfigData.getTermsAndCondition());
      editor.putString(PREF_KEY_HELP_URL, appConfigData.getHelpScreenUrl());
      editor.putInt(PREF_KEY_MIN_FEE, appConfigData.getMinRangeValue());
      editor.putInt(PREF_KEY_MAX_FEE, appConfigData.getMaxRangeValue());
      String adminJson = JacksonConverter.getStringFromObject(appConfigData.getAdmin());
      editor.putString(PREF_KEY_ADMIN_DETAIL, adminJson);
      //category list
      editor.putString(PREF_KEY_CATEGORY_LIST,
          JacksonConverter.getStringFromObject(appConfigData.getCategory()));
      editor.putString(PREF_KEY_SUB_CATEGORY_LIST,
          JacksonConverter.getStringFromObject(appConfigData.getJobSubCategory()));
      editor.putString(PREF_KEY_DELIVERY_PLACE_URL, appConfigData.getDelivery_place_url());
      editor.putString(PREF_KEY_DISCLAIMER_URL, appConfigData.getDisclaimer_url());
      editor.putString(PREF_AGT_TERMS_URL, appConfigData.getAgt_terms_url());

      editor.putString(PREF_GOOGLE_STORE, appConfigData.getPlay_store_link());
      editor.putString(PREF_APPLE_STORE, appConfigData.getApp_store_link());

      editor.commit();
    }
  }

  public String getAgtTermsUrl() {
    return pref.getString(PREF_AGT_TERMS_URL, "");
  }

  public String getQueCrJobUrl() {
    return pref.getString(PREF_KEY_DISCLAIMER_URL, "");
  }

  public boolean getPromoCode() {
    return pref.getBoolean(PREF_KEY_PROMO_CODE_STATUS, true);
  }

  public String getTermsNConditionURL() {
    return pref.getString(PREF_KEY_TERMS_COND_URL, "");
  }

  public String getPlayStoreLink() {
    return pref.getString(PREF_GOOGLE_STORE, "");
  }

  public String getAppleStoreLink() {
    return pref.getString(PREF_APPLE_STORE, "");
  }


  public String getAdminEmail() {
    Admin admin = getAdminDetail();
    if (admin != null) {
      return admin.getEmail();
    } else {
      return null;
    }
  }

  public Admin getAdminDetail() {
    String adminJson = pref.getString(PREF_KEY_ADMIN_DETAIL, null);
    if (adminJson != null) {
      return JacksonConverter.getObjectFromJSON(adminJson, Admin.class);
    }
    return null;
  }

  public String getHelpUrl() {
    return pref.getString(PREF_KEY_HELP_URL, "");
  }

  public int getMinFee() {
    return pref.getInt(PREF_KEY_MIN_FEE, 1);
  }

  public int getMaxFee() {
    return pref.getInt(PREF_KEY_MAX_FEE, 5000);
  }

  public List<Category> getCategoryList() {
    String categoriesJSON = pref.getString(PREF_KEY_CATEGORY_LIST, "");
    if (!TextUtils.isEmpty(categoriesJSON)) {
      //load original category
      List<Category> categories =
          JacksonConverter.getListTypeFromJSON(categoriesJSON, Category.class);
      List<Category> finalCategories = new ArrayList<>();
      finalCategories.add(loadDefaultCategory());
      if (categories != null) finalCategories.addAll(categories);
      return finalCategories;
    } else {
      return null;
    }
  }

  /**
   * Category as Favourable
   */
  public List<Category> getFavourableList() {
    String categoriesJSON = pref.getString(PREF_KEY_CATEGORY_LIST, "");
    if (!TextUtils.isEmpty(categoriesJSON)) {
      //load original category
      List<Category> categories =
          JacksonConverter.getListTypeFromJSON(categoriesJSON, Category.class);
      List<Category> finalCategories = new ArrayList<>();
      finalCategories.add(loadDefaultFavourable());
      if (categories != null) finalCategories.addAll(categories);
      return finalCategories;
    } else {
      return null;
    }
  }

  public List<SubCategory> getSubCategoryList() {
    String subCategoriesJSON = pref.getString(PREF_KEY_SUB_CATEGORY_LIST, "");
    if (!TextUtils.isEmpty(subCategoriesJSON)) {
      //load original Sub category
      List<SubCategory> categories =
          JacksonConverter.getListTypeFromJSON(subCategoriesJSON, SubCategory.class);
      List<SubCategory> finalSubCategories = new ArrayList<>();
      finalSubCategories.add(loadDefaultSubCategory());
      if (categories != null) finalSubCategories.addAll(categories);
      return finalSubCategories;
    } else {
      return null;
    }
  }

  private Category loadDefaultCategory() {
    Category defaultCategory = new Category();
    defaultCategory.setCategoryId(-1);
    defaultCategory.setCategoryName(getString(R.string.category_default));
    defaultCategory.setCategoryNameCh(getString(R.string.category_default));
    return defaultCategory;
  }

  private Category loadDefaultFavourable() {
    Category defaultCategory = new Category();
    defaultCategory.setCategoryId(-1);
    defaultCategory.setCategoryName(getString(R.string.favourable_default));
    defaultCategory.setCategoryNameCh(getString(R.string.favourable_default));
    return defaultCategory;
  }

  private SubCategory loadDefaultSubCategory() {
    SubCategory defaultSubCategory = new SubCategory();
    defaultSubCategory.setCategory_id(-1);
    defaultSubCategory.setSub_category_id(-1);
    defaultSubCategory.setSub_category_name(getString(R.string.sub_category_default));
    defaultSubCategory.setSub_category_name_ch(getString(R.string.sub_category_default));
    return defaultSubCategory;
  }

  public String getLanguageMode() {
    return pref.getString(Consts.KEY_LANGUAGE_MODE, Consts.ENGLISH_MODE);
  }

  public void setLanguageMode(String languageMode) {
    editor.putString(Consts.KEY_LANGUAGE_MODE, languageMode);
    editor.commit();
  }

  public String getDeviceToken() {
    return pref.getString(DEVICE_TOKEN, null);
  }

  public void setDeviceToken(String deviceToken) {
    editor.putString(DEVICE_TOKEN, deviceToken);
    editor.commit();
  }

  public boolean isRememberMe() {
    return pref.getBoolean(PREF_REMEMBER_ME, false);
  }

  public void setRememberMe(boolean isRememberMe) {
    editor.putBoolean(PREF_REMEMBER_ME, isRememberMe);
    editor.commit();
  }

  public int getNewJobNotification() {
    return pref.getInt(KEY_NEW_JOB_NOTIFICATION, 1);
  }

  public void setNewJobNotification(int newJob) {
    editor.putInt(KEY_NEW_JOB_NOTIFICATION, newJob);
    editor.commit();
  }

  public int getJobStatusNotification() {
    return pref.getInt(KEY_JOB_STATUS_NOTIFICATION, 1);
  }

  public void setJobStatusNotification(int jobStatus) {
    editor.putInt(KEY_JOB_STATUS_NOTIFICATION, jobStatus);
    editor.commit();
  }

  public int getTransactionNotification() {
    return pref.getInt(KEY_TRANSACTION_NOTIFICATION, 1);
  }

  public void setTransactionNotification(int transaction) {
    editor.putInt(KEY_TRANSACTION_NOTIFICATION, transaction);
    editor.commit();
  }

  /**
   * This will apply for both agent and requester
   */
  public int shouldShowTourGuide() {
    return pref.getInt(KEY_APP_TOUR_GUIDE, 0);
  }

  /**
   * Set the tour guide option for both user
   */
  public void setShowTourGuide(int tourGuide) {
    editor.putInt(KEY_APP_TOUR_GUIDE, tourGuide);
    editor.commit();
  }

  public String getContactNumber() {
    return pref.getString(KEY_NUMBER, "");
  }

  public void setContactNumber(String contactNumber) {
    editor.putString(KEY_NUMBER, contactNumber);
    editor.commit();
  }

  public void logout() {
    saveUserDetail(null);
    saveUsersSettings(null);
    setRememberMe(false);
    clearTokenDetails();
    setDeviceToken(null);
  }

  public void saveAccessToken(String accessToken) {
    String originalToken = accessToken;
    String encodedToken = "af23" + originalToken + "dte=";
    editor.putString(PREF_KEY_ACCESS_TOKEN, encodedToken);
    editor.commit();
  }

  public String getAccessToken() {
    String encodedToken = pref.getString(PREF_KEY_ACCESS_TOKEN, "");
    String prefixTrim = encodedToken.replace("af23", "");
    return prefixTrim.replace("dte=", "");
  }

  public void saveTokenType(String tokenType) {
    editor.putString(PREF_KEY_TOKEN_TYPE, tokenType);
    editor.commit();
  }

  public String getTokenType() {
    return pref.getString(PREF_KEY_TOKEN_TYPE, "");
  }

  public void saveTokenDetails(TokenData tokenData) {
    saveAccessToken(tokenData.getAccess_token());
    saveTokenType(tokenData.getToken_type());
  }

  public void clearTokenDetails() {
    //Paypal Access Token
    saveAccessToken("");
    saveTokenType("");
  }

  public void saveCredentials(String clientKey, String secretKey) {
    saveClientKey(clientKey);
    saveSecretKey(secretKey);
  }

  public String[] getPayCredentials() {
    String[] credentials = new String[2];
    credentials[0] = pref.getString(PREF_KEY_CLIENT, "");
    credentials[1] = pref.getString(PREF_KEY_SECRET, "");
    return credentials;
  }

  private void saveClientKey(String clientKey) {
    editor.putString(PREF_KEY_CLIENT, clientKey);
    editor.commit();
  }

  private void saveSecretKey(String secretKey) {
    editor.putString(PREF_KEY_SECRET, secretKey);
    editor.commit();
  }

  public String getAuthorizationToken() {
    String tokenMethod = getTokenType();
    String accessToken = getAccessToken();
    Log.d(TAG, "getAuthorizationToken: " + tokenMethod + " " + accessToken);
    return String.valueOf(tokenMethod + " " + accessToken);
  }

  public void saveDeliveryPlaceURL(String deliveryPlace) {
    editor.putString(PREF_KEY_DELIVERY_PLACE_URL, deliveryPlace);
    editor.commit();
  }

  public void setIsFirstTimeOpen(boolean isFirstTimeOpen) {
    editor.putBoolean(PREF_IS_MENU_OPEN, isFirstTimeOpen);
    editor.commit();
  }

  public boolean isFirstTimeOpen() {
    return pref.getBoolean(PREF_IS_MENU_OPEN, false);
  }

  public String getDeliveryPlaceURL() {
    return pref.getString(PREF_KEY_DELIVERY_PLACE_URL, "");
  }
}
