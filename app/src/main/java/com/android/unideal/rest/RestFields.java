package com.android.unideal.rest;

import com.android.unideal.enums.AppMode;

/**
 * Contains the list of parameters of rest api call.
 */
public class RestFields {

  public static final int ACTION_ACCEPT = 1;
  public static final int ACTION_REJECT = 2;
  public static final int ACTION_MODIFY = 3;
  public static final int REQUEST_PROMOTIONAL_CODE = 2;
  public static final int REQUEST_REFFERAL_CODE = 1;
  public static final String KEY_ACTION = "action";
  public static final String KEY_FILE_PATH = "file_path";
  public static final String KEY_PAGE_INDEX = "page_index";
  public static final String KEY_LAST_UPDATE_DATE = "last_update_date";
  public static final String KEY_IS_READ = "is_read";
  public static final String KEY_NOTIFICATION_ID = "notification_id";
  public static final int READ = 1;
  public static final int UNREAD = 0;
  public static final String KEY_OFFER = "offer";
  public static final String KEY_DELIVERY = "delivery_place";
  public static final String KEY_IS_INVOICE = "is_invoice";
  public final static String KEY_GRANT_TYPE = "grant_type";
  public final static String VALUE_GRANT_TYPE = "client_credentials";
  public final static String KEY_AUTHORIZATION = "authorization";
  public final static String KEY_CUSTOMER_ID = "external_customer_id";
  public final static String KEY_MERCHANT_ID = "merchant_id";
  public final static String KEY_CREDIT_CARD_ID = "credit_card_id";
  public final static String PREFIX_ID = "";

  public static String APP_VERSION = "1.0";
  public static String OS_TYPE = "2";
  public static int STATUS_SUCCESS = 200;
  public static int RESEND_STATUS_CODE = 303;
  public static int MALE = 1;
  public static int FEMALE = 2;
  public static String KEY_EMAIL_ADDRESS = "email_address";
  public static String KEY_PASSWORD = "password";
  public static String KEY_DEVICE_TOKEN = "device_token";
  public static String KEY_USER_TYPE = "user_type";
  public static String KEY_GOOGLE_PLUS_ID = "google_id";
  public static String KEY_FACEBOOK_ID = "facebook_id";
  public static String KEY_NAME = "name";
  public static String KEY_PROFILE_PIC = "profile_pic";
  public static String KEY_GENDER = "gender";
  public static String KEY_NUMBER = "phone_number";
  public static String KEY_REFERRAL_CODE = "referral_code";
  public static String KEY_USER_ID = "user_id";
  public static String KEY_FROM_SOCIAL_NETWORK = "from_social_network";
  public static String KEY_ACCOUNT_NUMBER = "account_number";
  public static String KEY_SWIFT_CODE = "swift_code";
  public static String KEY_BANK_NAME = "bank_name";
  public static String KEY_BANK_DETAIL_ID = "bank_details_id";
  public static String KEY_OS_TYPE = "os_type";
  public static String KEY_LANGUAGE = "language";
  public static String KEY_APP_VERSION = "version";
  public static String KEY_BIO = "bio";
  public static String KEY_OLD_PASSWORD = "old_password";
  public static String KEY_NEW_PASSWORD = "new_password";
  public static String KEY_EXPERTISE = "expertize";
  public static String KEY_DOCUMENTS = "document";
  public static String KEY_NEW_DEVICE_TOKEN = "new_device_token";
  public static String KEY_OLD_DEVICE_TOKEN = "old_token";
  public static String KEY_AGENTS_FEES = "agent_fees";
  public static String KEY_JOB_DETAILS = "job_details";
  public static String KEY_JOB_ID = "job_id";
  public static String KEY_JOB_TITLE = "job_title";
  public static String KEY_JOB_COMPLETE_DATE = "job_end_on";
  public static String KEY_CONSIGNMENT_SIZE = "consignment_size";
  public static String KEY_CATEGORY_ID = "category_id";
  public static String KEY_SUB_CATEGORY_ID = "sub_category_id";
  public static String KEY_JOB_SEARCH = "job_search";
  public static String KEY_SUB_CATEGORY = "sub_category";
  public static String KEY_QUANTITY = "quantity";
  public static String KEY_JOB_FILES = "job_files";
  public static String KEY_PROMOTIONAL_CODE = "promotional_code";
  public static String KEY_JOB_STATUS_NOTIFICATION = "jobstatus_notification";
  public static String KEY_TRANSACTION_NOTIFICATION = "transaction_notification";
  public static String KEY_NEW_JOB_NOTIFICATION = "newjob_notification";
  public static String KEY_APP_TOUR_GUIDE = "app_tour_guide";
  public static String KEY_JOB_STATUS = "job_status";
  public static String KEY_UPDATE_JOB_STATUS = "request_job_status";
  public static String KEY_REVIEWS = "reviews";
  public static String KEY_APPLICANT_ID = "applicant_id";
  public static String KEY_APPLICANT_ACTION = "applicants_action";
  public static String KEY_IS_CONSIGNMENT_EDIT = "is_consignment_edit";
  public static String KEY_PROPOSAL_DETAILS = "proposal_details";
  public static int USER_TYPE_REQUESTER = AppMode.QUESTIONER.getValue();
  public static int USER_TYPE_AGENT = AppMode.AGENT.getValue();
  public static int USER_TYPE_ADMIN = AppMode.ADMIN.getValue();
  public static String KEY_POSTING_DATE = "job_posting_date";
  public static String KEY_EXPIRY_DATE = "job_expiry_date";
  //Job Status code for Post Server
  public static String KEY_JOB_FEE_MIN = "job_fee_min";
  public static String KEY_JOB_FEE_MAX = "job_fee_max";
  public static String KEY_REQUESTER_ID = "requester_id";

  public static String KEY_PAYER_ID = "payer_id";
  public static String KEY_PHONE_NUMBER = "number";
  public static String KEY_TYPE = "type";
  public static String KEY_EXPIRE_MONTH = "expire_month";
  public static String KEY_EXPIRE_YEAR = "expire_year";
  public static String KEY_FIRST_NAME = "first_name";
  public static String KEY_LAST_NAME = "last_name";

  public static class STATUS {
    public static final int OPEN = 1;
    public static final int APPLIED = 2;
    public static final int IN_PROCESS = 3;
    public static final int COMPLETED = 4;
    public static final int PAUSED = 5;
    public static final int RESUMED = 6;
    public static final int CANCELLED = 7;
  }
}
