package com.android.unideal.rest.service;

import com.android.unideal.data.AgentOfferData;
import com.android.unideal.data.Applicant;
import com.android.unideal.data.JobDetail;
import com.android.unideal.data.Notifications;
import com.android.unideal.data.UserDetail;
import com.android.unideal.data.socket.MediaData;
import com.android.unideal.questioner.data.BankAccountData;
import com.android.unideal.rest.response.AppConfigData;
import com.android.unideal.rest.response.GenericResponse;
import com.android.unideal.rest.response.NewJobResponse;
import com.android.unideal.rest.response.NotificationResponse;
import com.android.unideal.rest.response.ProfileResponse;
import com.android.unideal.rest.response.RattingResponse;
import com.android.unideal.rest.response.TransactionResponse;
import java.util.List;
import java.util.Map;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

/**
 * Interface for application authentication API calls.
 */
public interface AppAPI {

  @Multipart
  @POST("users/register")
  Call<GenericResponse> register(@PartMap Map<String, RequestBody> params);

  @FormUrlEncoded
  @POST("users/logingoogle")
  Call<GenericResponse<UserDetail>> loginWithGoogle(@FieldMap Map<String, String> params);

  @FormUrlEncoded
  @POST("users/loginfb")
  Call<GenericResponse<UserDetail>> loginWithFaceBook(@FieldMap Map<String, String> params);

  @FormUrlEncoded
  @POST("users/googlestatus")
  Call<GenericResponse<UserDetail>> checkForGoogleId(@Field("google_id") String googleId);

  @FormUrlEncoded
  @POST("users/facebookstatus")
  Call<GenericResponse<UserDetail>> checkForFbId(@Field("facebook_id") String facebookId);

  @FormUrlEncoded
  @POST("users/login")
  Call<GenericResponse<UserDetail>> loginWithCustom(@FieldMap Map<String, Object> params);

  @FormUrlEncoded
  @POST("users/resendmail")
  Call<GenericResponse> resendActivationCode(@FieldMap Map<String, Object> params);

  @FormUrlEncoded
  @POST("users/forgotpassword")
  Call<GenericResponse> resetPassword(@FieldMap Map<String, Object> params);

  @FormUrlEncoded
  @POST("users/logout")
  Call<GenericResponse> logout(@FieldMap Map<String, Object> params);

  @FormUrlEncoded
  @POST("users/delete")
  Call<GenericResponse> deleteUser(@FieldMap Map<String, Object> params);

  @FormUrlEncoded
  @POST("payment/banklist")
  Call<GenericResponse<List<BankAccountData>>> getBankList(@FieldMap Map<String, Object> params);

  @FormUrlEncoded
  @POST("payment/addbank")
  Call<GenericResponse<BankAccountData>> passBankInfo(@FieldMap Map<String, Object> params);

  @FormUrlEncoded
  @POST("payment/deletebank")
  Call<GenericResponse<BankAccountData>> removeBankAccount(@FieldMap Map<String, Object> params);

  @FormUrlEncoded
  @POST("payment/setdefaultbank")
  Call<GenericResponse<BankAccountData>> defaultBankSelect(@FieldMap Map<String, Object> params);

  @FormUrlEncoded
  @POST("users/switchuser")
  Call<GenericResponse> switchUser(@Field("user_id") int userId,
      @Field("new_user_type") int newUserType);

  @POST("config/allappconfig")
  Call<GenericResponse<AppConfigData>> appConfiguration();

  @FormUrlEncoded
  @POST("users/profile")
  Call<GenericResponse<UserDetail>> getUserProfile(@FieldMap Map<String, Object> params);

  @Multipart
  @POST("users/update")
  Call<GenericResponse<UserDetail>> updateProfile(@PartMap Map<String, RequestBody> params);

  @FormUrlEncoded
  @POST("job/ratings")
  Call<GenericResponse<RattingResponse>> getRatting(@FieldMap Map<String, Object> params);

  @FormUrlEncoded
  @POST("job/profiledetails")
  Call<GenericResponse<ProfileResponse>> getProfileDetail(@FieldMap Map<String, String> params);

  @FormUrlEncoded
  @POST("users/updatesettings")
  Call<GenericResponse<UserDetail>> updateUserSetting(@FieldMap Map<String, Object> params);

  @FormUrlEncoded
  @POST("users/updatetoken")
  Call<GenericResponse> updateToken(@FieldMap Map<String, Object> params);

  @FormUrlEncoded
  @POST("job/newjob")
  Call<GenericResponse<NewJobResponse>> createNewJob(@FieldMap Map<String, Object> params);

  @Multipart
  @POST("job/newjobimages")
  Call<GenericResponse> newjobimages(@PartMap Map<String, RequestBody> params,
      @Part List<MultipartBody.Part> attachedFile);

  @FormUrlEncoded
  @POST("job/listapplicants")
  Call<GenericResponse<List<Applicant>>> getApplicantList(@FieldMap Map<String, Object> params);

  @FormUrlEncoded
  @POST("job/sendproposal")
  Call<GenericResponse<JobDetail>> sendProposal(@FieldMap Map<String, Object> params);

  @FormUrlEncoded
  @POST("job/listjobs")
  Call<GenericResponse<List<JobDetail>>> getJobList(@FieldMap Map<String, String> params);

  @FormUrlEncoded
  @POST("job/jobdetails")
  Call<GenericResponse<JobDetail>> getJobDetail(@Field("user_id") int userId,
      @Field("job_id") int jobId, @Field("user_type") int userType);

  @FormUrlEncoded
  @POST("job/updatejobstatus")
  Call<GenericResponse> updateJobStatus(@FieldMap Map<String, Object> params);

  @FormUrlEncoded
  @POST("job/applicantaction")
  Call<GenericResponse> applicantAction(@FieldMap Map<String, Object> map);

  @FormUrlEncoded
  @POST("job/getagentsoffers")
  Call<GenericResponse<List<AgentOfferData>>> getAgentOffers(@FieldMap Map<String, Object> params);

  @FormUrlEncoded
  @POST("job/agentofferaction")
  Call<GenericResponse> agentofferaction(@FieldMap Map<String, Object> params);

  @Multipart
  @POST("message/uploadattachment")
  Call<GenericResponse<MediaData>> uploadAttachment(@PartMap Map<String, RequestBody> params);

  @FormUrlEncoded
  @POST("notification/notificationlist")
  Call<NotificationResponse> getNotificationList(@FieldMap Map<String, Object> params);

  @FormUrlEncoded
  @POST("notification/updatereadstate")
  Call<GenericResponse> updateNotificationState(@FieldMap Map<String, Object> params);

  @FormUrlEncoded
  @POST("notification/requestnewupdate")
  Call<GenericResponse<List<Notifications>>> getUpdateNotifications(
      @FieldMap Map<String, Object> params);

  @FormUrlEncoded
  @POST("payment/savedefaultcard")
  Call<GenericResponse> setDefaultCardById(@Field("user_id") int userId,
      @Field("card_id") String cardId);

  @FormUrlEncoded
  @POST("payment/validatecreditcard")
  Call<GenericResponse> validateCreditCard(@Field("user_id") int userId,
      @Field("card_id") String cardId);

  @FormUrlEncoded
  @POST("payment/detachedcard")
  Call<GenericResponse> detachedCreditCard(@Field("user_id") int userId,
      @Field("card_id") String cardId);

  @FormUrlEncoded
  @POST("payment/transactionhistory")
  Call<TransactionResponse> fetchTransactionHistory(@Field("user_id") int userId,
      @Field("page_index") int page_index);

  @FormUrlEncoded
  @POST("job/referralprogram")
  Call<GenericResponse> promocodeProgram(@Field("user_id") int userId,
      @Field("code_type") int code_type, @Field("coupon_code") String coupon_code);

  @FormUrlEncoded
  @POST("job/referralprogram")
  Call<GenericResponse> referralProgram(@Field("code_type") int code_type,
      @Field("coupon_code") String coupon_code);
}
