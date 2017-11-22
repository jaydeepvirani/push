package com.android.unideal.flurry;

import android.content.Context;
import android.util.Log;
import com.android.unideal.BuildConfig;
import com.android.unideal.R;
import com.android.unideal.data.UserDetail;
import com.android.unideal.util.AppUtility;
import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryAgentListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bhavdip on 27/1/17.
 */

public class FlurryManager implements FlurryAgentListener {
  private static final String TAG = "FlurryManager";
  private static String EVENT_LOGOUT = "logout";
  private static String EVENT_LOGIN = "login";
  private static String EVENT_NW_JOB = "job";
  private static String EVENT_VISIT_JOB = "visit_job_creation_page";
  private static String EVENT_LANGUAGE = "app_language";
  private static String EVENT_ROLE = "switch_role";
  private static String EVENT_CREATION_PAGE = "visit_card_creation_page";
  private static String EVENT_CARD_INSERTED = "card_inserted";

  private static String EVENT_AGENT_FILTER = "agent_filter_jobs";
  private static String EVENT_AGENT_VIEW_JOB = "agent_view_job";
  private static String EVENT_AGENT_APPLIED = "agent_applied_job";
  private static String EVENT_SKIP_WALK_THROUGH = "skipped_walk_through";
  private static String EVENT_FINISH_WALK_THROUGH = "finished_walk_through";

  private static String PARAM_USER_ID = "userid";
  private static String PARAM_JOB_ID = "job_id";
  private static String PARAM_JOB_TITLE = "job_title";
  private static String PARAM_FB = "facebook_id";
  private static String PARAM_NAME = "username";
  private static String PARAM_EMAIL = "email";
  private static String PARAM_GOOGLE = "google_id";
  private static String PARAM_LOCALE = "locale";
  private static String PARAM_MODE = "app_mode";
  private static String PARAM_LOCATION = "location";
  private static String PARAM_CATEGORY = "category";
  private static String PARAM_PRICE_RANGE = "price_range";
  private static String PARAM_SHARE = "share_code_";
  private static String PARAM_TO_ROLE = "to_role";
  private static String PARAM_SKIP_WT = "screen_no ";
  private static String PARAM_FINISH_WT = "is_finish ";

  private static FlurryManager flurryManager = new FlurryManager();

  private FlurryManager() {
  }

  public static FlurryManager loadManager() {
    return flurryManager;
  }

  /**
   * Logs an event for analytics.
   *
   * @param eventName name of the event
   * @param eventParams event parameters (can be null)
   * @param timed <code>true</code> if the event should be timed, false otherwise
   */
  private static void logEvent(String eventName, Map<String, String> eventParams, boolean timed) {
    FlurryAgent.logEvent(eventName, eventParams, timed);
  }

  private static void endTimedEvent(String eventName, Map<String, String> eventParams) {
    FlurryAgent.endTimedEvent(eventName, eventParams);
  }

  private static void endTimedEvent(String eventName) {
    FlurryAgent.endTimedEvent(eventName);
  }

  public static void setUserId(String userId) {
    FlurryAgent.setUserId(AppUtility.convertMD5(userId));
  }

  public static void setUserId(int userId) {
    FlurryAgent.setUserId(AppUtility.convertMD5(String.valueOf(userId)));
  }

  public static void startEventLogIn(UserDetail userDetail) {
    if (userDetail == null) return;
    Map<String, String> useParams = new HashMap<>(4);
    //param keys and values have to be of String type
    useParams.put(PARAM_USER_ID, String.valueOf(userDetail.getUserId()));
    useParams.put(PARAM_EMAIL, userDetail.getEmailAddress());
    useParams.put(PARAM_FB, userDetail.getFacebookId());
    useParams.put(PARAM_GOOGLE, userDetail.getGoogleplusId());
    logEvent(EVENT_LOGIN, useParams, false);
  }

  public static void startEventLogOut(UserDetail userDetail) {
    if (userDetail == null) return;
    Map<String, String> useParams = new HashMap<>(5);
    //param keys and values have to be of String type
    useParams.put(PARAM_USER_ID, String.valueOf(userDetail.getUserId()));
    useParams.put(PARAM_NAME, String.valueOf(userDetail.getName()));
    useParams.put(PARAM_EMAIL, userDetail.getEmailAddress());
    useParams.put(PARAM_FB, userDetail.getFacebookId());
    useParams.put(PARAM_GOOGLE, userDetail.getGoogleplusId());
    logEvent(EVENT_LOGOUT, useParams, true);
  }

  public static void stopEventLogOut() {
    endTimedEvent(EVENT_LOGOUT);
  }

  public static void startCreateJob(int userId, String jobTitle) {
    //Flurry create a new job
    Map<String, String> jobParams = new HashMap<>();
    jobParams.put(PARAM_USER_ID, String.valueOf(userId));
    jobParams.put(PARAM_JOB_TITLE, String.valueOf(jobTitle));
    logEvent(EVENT_NW_JOB, jobParams, true);
  }

  public static void stopCreateJob(int userId, int jobId, String jobTitle) {
    //Flurry create a new job
    Map<String, String> jobParams = new HashMap<>();
    jobParams.put(PARAM_USER_ID, String.valueOf(userId));
    jobParams.put(PARAM_JOB_ID, String.valueOf(jobId));
    jobParams.put(PARAM_JOB_TITLE, String.valueOf(jobTitle));
    endTimedEvent(EVENT_NW_JOB, jobParams);
  }

  public static void loggedLocale(int userId, String localeLanguage, String activeMode) {
    Map<String, String> usersParams = new HashMap<>();
    usersParams.put(PARAM_USER_ID, String.valueOf(userId));
    usersParams.put(PARAM_LOCALE, localeLanguage);
    usersParams.put(PARAM_MODE, activeMode);
    logEvent(EVENT_LANGUAGE, usersParams, false);
  }

  /**
   * Trigger when users click “Switch to role”
   */
  public static void swtichRole(int userId, String activeMode) {
    Map<String, String> params = new HashMap<>();
    params.put(PARAM_USER_ID, String.valueOf(userId));
    params.put(PARAM_TO_ROLE, activeMode);
    params.put(PARAM_MODE, activeMode);
    logEvent(EVENT_ROLE, params, false);
  }

  /**
   * Trigger when user enter Manage Credit Card Page
   */
  public static void cardCreation(int userId, String activeMode) {
    Map<String, String> params = new HashMap<>();
    params.put(PARAM_USER_ID, String.valueOf(userId));
    params.put(PARAM_TO_ROLE, activeMode);
    params.put(PARAM_MODE, activeMode);
    logEvent(EVENT_CREATION_PAGE, params, false);
  }

  /**
   * Trigger when user enter Manage Credit Card Page
   */
  public static void cardInsert(int userId, String activeMode) {
    Map<String, String> params = new HashMap<>();
    params.put(PARAM_USER_ID, String.valueOf(userId));
    params.put(PARAM_TO_ROLE, activeMode);
    params.put(PARAM_MODE, activeMode);
    logEvent(EVENT_CARD_INSERTED, params, false);
  }

  public static void agentFilterApply(String location, String category, String price_range) {
    Map<String, String> params = new HashMap<>();
    params.put(PARAM_LOCATION, location);
    params.put(PARAM_CATEGORY, category);
    params.put(PARAM_PRICE_RANGE, price_range);
    logEvent(EVENT_AGENT_FILTER, params, false);
  }

  public static void agentViewJob(int userId, String job_id) {
    Map<String, String> params = new HashMap<>();
    params.put(PARAM_USER_ID, String.valueOf(userId));
    params.put(PARAM_JOB_ID, job_id);
    logEvent(EVENT_AGENT_VIEW_JOB, params, false);
  }

  public static void agentApplyOnJob(int userId, String job_id) {
    Map<String, String> params = new HashMap<>();
    params.put(PARAM_USER_ID, String.valueOf(userId));
    params.put(PARAM_JOB_ID, job_id);
    logEvent(EVENT_AGENT_APPLIED, params, false);
  }

  public static void shareReferralCode(String method, int userId) {
    Map<String, String> params = new HashMap<>();
    params.put(PARAM_USER_ID, String.valueOf(userId));
    logEvent(PARAM_SHARE + "_" + method, params, false);
  }

  public static void skipWalkThrough(int screenNumber) {
    Map<String, String> params = new HashMap<>();
    params.put(PARAM_SKIP_WT, String.valueOf(screenNumber));
    params.put(PARAM_FINISH_WT, String.valueOf("false"));
    logEvent(EVENT_SKIP_WALK_THROUGH, params, false);
  }

  public static void finishWalthThrough() {
    Map<String, String> params = new HashMap<>();
    params.put(PARAM_FINISH_WT, String.valueOf("true"));
    logEvent(EVENT_FINISH_WALK_THROUGH, params, false);
  }

  public void initManager(Context appContext) {
    new FlurryAgent.Builder().withLogEnabled(BuildConfig.DEBUG)
        .withCaptureUncaughtExceptions(true)
        .withListener(this)
        .build(appContext, appContext.getString(R.string.flurry_api_key));
  }

  @Override
  public void onSessionStarted() {
    consoleLog("onSessionStarted");
  }

  private void consoleLog(String logMsg) {
    if (BuildConfig.DEBUG) Log.d(TAG, logMsg);
  }
}
