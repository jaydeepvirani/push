package com.android.unideal.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import com.android.unideal.enums.AppMode;
import com.android.unideal.rest.EmptyCallbackWrapper;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.RestUtils;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import com.android.unideal.util.SessionManager;
import com.android.unideal.util.session.UploadManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * Created by bhavdip on 17/11/16.
 */

public class UploadingService extends Service {
  private static final String TAG = "UploadingService";
  private UploadManager mUploadManager;
  private SessionManager mAppSessionManager;
  private ArrayList<String> mUploadFilesList;

  /**
   * This service would start from two place one for while
   * 1.Creating a new job
   * 2.When Application is start
   *
   * Return a Service Intent for uploading a files
   */
  public static Intent getServiceIntent(Activity activityContext) {
    return new Intent(activityContext, UploadingService.class);
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(TAG, "Received start id " + startId + ": " + intent);
    return START_NOT_STICKY;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Log.d(TAG, "Service is start running...");
    //1. Take a Upload Manager
    mUploadManager = UploadManager.get(getApplicationContext());
    mAppSessionManager = SessionManager.get(getApplicationContext());
    //2.Fetch all pending upload file list
    mUploadFilesList = mUploadManager.pendingUploadList();
    startUploadingProcess();
  }

  private void popUploadItem() {
    if (mUploadFilesList != null && mUploadFilesList.size() > 0) {
      mUploadFilesList.remove(0);
    }
  }

  /**
   * This function is recursively call for uploading pending file
   * once all uploading is finish it will stop itself
   */
  private void startUploadingProcess() {
    if (mUploadFilesList != null && mUploadFilesList.size() > 0) {
      String jobId = mUploadFilesList.get(0);
      ArrayList<String> filePathList = mUploadManager.getUploadFileList(jobId);
      if (!TextUtils.isEmpty(jobId) && filePathList != null && filePathList.size() > 0) {
        uploadingProcess(jobId, filePathList);
      } else {
        Log.e(TAG, "Upload File Path is not found.");
        startUploadingProcess();
      }
    } else {
      Log.e(TAG, "Uploading File List is Empty");
      Log.d(TAG, "Service is stop...");
      stopSelf();
    }
  }

  private void uploadingProcess(final String jobId, ArrayList<String> fileList) {
    Log.d(TAG, "Uploading Process Start " + jobId);
    //1. Job Id
    String jobNumber = jobId;
    //2. Active User Id
    int activeUserId = mAppSessionManager.getUserId();
    //3.Prepare Has Map for request parameter
    HashMap<String, RequestBody> requestParameters = new HashMap<>();
    requestParameters.put(RestFields.KEY_JOB_ID, RestUtils.TypedString(jobNumber));
    requestParameters.put(RestFields.KEY_USER_ID, RestUtils.TypedString(activeUserId));
    requestParameters.put(RestFields.KEY_USER_TYPE,
        RestUtils.TypedString(AppMode.QUESTIONER.getValue()));
    //4. attached a multiple files
    List<MultipartBody.Part> attachedFilesList = new ArrayList<>();
    for (int i = 0; i < fileList.size(); i++) {
      Log.d(TAG, "Attached File Name " + fileList.get(i));
      //add file in multipart list
      attachedFilesList.add(RestUtils.prepareMultiFilePart(RestFields.KEY_JOB_FILES + "[" + i + "]",
          fileList.get(i)));
    }
    Call<GenericResponse> requestUploadFiles =
        RestClient.get().newjobimages(requestParameters, attachedFilesList);
    requestUploadFiles.enqueue(new EmptyCallbackWrapper() {
      @Override
      public void onSuccess(GenericResponse response) {
        //If successfully uploading a job have to remove it from the list
        mUploadManager.removeJobFiles(jobId);
        //Remove the top item from the list
        popUploadItem();
        Log.d(TAG,
            "200: Uploading of file for (" + jobId + ") is completed. " + response.getMessage());
        startUploadingProcess();
      }

      @Override
      public void onFailure(GenericResponse response) {
        Log.e(TAG,
            "201: Uploading of file for (" + jobId + ") is failed. " + response.getMessage());
        //Remove the top item from the list
        popUploadItem();
        startUploadingProcess();
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        Log.e(TAG,
            "201: Uploading of file for (" + jobId + ") is failed. " + errorResponse.getMessage());
        //Remove the top item from the list
        popUploadItem();
        startUploadingProcess();
      }
    });
  }
}
