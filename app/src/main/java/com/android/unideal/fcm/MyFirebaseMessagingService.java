package com.android.unideal.fcm;

import android.text.TextUtils;
import android.util.Log;
import com.android.unideal.notification.Announcement;
import com.android.unideal.notification.JobNotification;
import com.android.unideal.notification.MsgNotification;
import com.android.unideal.notification.NotificationHelper;
import com.android.unideal.util.Consts;
import com.android.unideal.util.converter.JacksonConverter;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;

/**
 * Created by Belal on 5/27/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

  private static final String TAG = "MyFirebaseMsgService";

  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    //Displaying data in log
    //It is optional
     Log.d(TAG, "From: " + remoteMessage.getFrom());
    Map<String, String> map = remoteMessage.getData();
    if (map.containsKey("push_notification_type")) {
      String type = map.get("push_notification_type");
      String dataJson = map.get("data");
      Log.d(TAG, "onMessageReceived: data " + dataJson);
      if (type != null) {
        try {
          int typeInt = Integer.parseInt(type);
          switch (typeInt) {
            case Consts.NOTI_TYPE_JOB:
              handleJobNotification(dataJson);
              break;
            case Consts.NOTI_TYPE_MESSAGING:
              handleMessageNotification(dataJson);
              break;
            case Consts.NOTI_TYPE_ANNOUNCEMENT:
              handleAnnouncement(dataJson);
              break;
          }
        } catch (NumberFormatException e) {
          e.printStackTrace();
        }
      } else {
        Log.d(TAG, "onMessageReceived: type not found");
      }
    } else {
      Log.d(TAG, "onMessageReceived: type not found");
    }

    //Calling method to generate notification
    //sendNotification(remoteMessage.getNotification().getBody());
  }

  private void handleAnnouncement(String dataJson) {
    if (TextUtils.isEmpty(dataJson)) {
      Log.d(TAG, "handleAnnouncement: data is empty");
      return;
    }
    Announcement announcement = JacksonConverter.getObjectFromJSON(dataJson, Announcement.class);
    if (announcement == null) {
      Log.d(TAG, "handleAnnouncement: json error");
      return;
    }
    NotificationHelper.showAnnouncement(this, announcement);
  }

  private void handleJobNotification(String dataJson) {
    if (TextUtils.isEmpty(dataJson)) {
      Log.d(TAG, "handleJobNotification: data is empty");
      return;
    }
    JobNotification notification =
        JacksonConverter.getObjectFromJSON(dataJson, JobNotification.class);
    if (notification == null) {
      Log.d(TAG, "handleJobNotification: json error");
      return;
    }
    NotificationHelper.showJobNotification(this, notification);
  }

  private void handleMessageNotification(String dataJson) {
    if (TextUtils.isEmpty(dataJson)) {
      Log.d(TAG, "handleMessageNotification: data is empty");
      return;
    }
    MsgNotification notification =
        JacksonConverter.getObjectFromJSON(dataJson, MsgNotification.class);
    if (notification == null) {
      Log.d(TAG, "handleMessageNotification: json error");
      return;
    }
    NotificationHelper.showNotification(this, notification);
  }
}