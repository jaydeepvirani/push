package com.android.unideal.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import com.android.unideal.R;
import com.android.unideal.agent.view.AgentHomeActivity;
import com.android.unideal.agent.view.JobMessageActivity;
import com.android.unideal.data.JobDetail;
import com.android.unideal.enums.AppMode;
import com.android.unideal.questioner.view.QuestionerJobDetailsActivity;
import com.android.unideal.rest.RestFields;

import java.util.List;

/**
 * Created by CS02 on 12/20/2016.
 */

public class NotificationHelper {
  private static final int MESSAGE_KEY = 1;
  private static final int MAX_INBOX_SIZE = 5;

  /**
   * This method create notification with notification id = hash code of thread id.
   * We can clear particular notification using this notification id.
   *
   * This method save notification content to shared pref grouped by thread id,
   * so that whenever new notification generate it will create inbox style notification
   * with old saved notification type.
   *
   * If there are not any old notification then it will display normal notification.
   *
   * When user tap on notification JobMessageActivity will be opened.
   * We need to clear notification content from shared pref when JobMessageActivity open.
   *
   * @param context app context
   * @param notiObj MsgNotification object
   */
  public static void showNotification(Context context, MsgNotification notiObj) {
    // create job detail obj
    JobDetail jobDetail = new JobDetail();
    jobDetail.setMsg_user_id(notiObj.getMsg_user_id());
    jobDetail.setMsg_user_name(notiObj.getMsg_user_name());
    jobDetail.setJob_id(notiObj.getJob_id());
    //jobDetail.setJob_status(notification.getJobStatusNotification());
    jobDetail.setMsg_thread_id(notiObj.getMsg_thread_id());
    jobDetail.setMsg_user_profile_url(notiObj.getMsg_user_profile_url());
    //jobDetail.setConsignment_size(notiObj.getConsignment_size());

    // notification id
    int notificationId = notiObj.getMsg_thread_id().hashCode();
    //boolean isOffer = notiObj.getIs_offered() == 1;
    // create pending intent
    Intent notificationIntent =
        JobMessageActivity.getActivity(context, AppMode.AGENT, jobDetail, true);
    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent pIntent = PendingIntent.getActivity(context, notificationId, notificationIntent,
        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

    // make title
    StringBuilder titleBuilder = new StringBuilder();
    if (notiObj.getMsg_user_name() != null) {
      titleBuilder.append(notiObj.getMsg_user_name());
      titleBuilder.append(" - ");
    }
    titleBuilder.append("Job #");
    titleBuilder.append(notiObj.getJob_id());
    String title = titleBuilder.toString();

    // build notification
    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
    builder.setContentTitle(title).setSmallIcon(getNotificationIcon())
        //.setColor(getNotificationColor(context,notiObj.getUser_type()))
        .setContentText(notiObj.getText())
        .setSound(defaultSoundUri)
        .setTicker(notiObj.getText())
        .setContentIntent(pIntent);

    //check for old messages
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
      List<MsgNotification> oldList =
          NotificationProvider.get(context).getAllNotifications(notiObj.getMsg_thread_id());
      if (oldList.size() > 0) {
        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
        if (oldList.size() > MAX_INBOX_SIZE) {
          for (int i = 0; i < MAX_INBOX_SIZE; i++) {
            MsgNotification msgNotification = oldList.get(i);
            style.addLine(msgNotification.getText());
          }
          int summaryCount = oldList.size() - MAX_INBOX_SIZE;
          style.setSummaryText("+" + summaryCount + " more");
        } else {
          for (MsgNotification msgNotification : oldList) {
            style.addLine(msgNotification.getText());
          }
        }
        //style.setBigContentTitle(notiObj.getMsg_user_name());
        builder.setStyle(style);
      }
    }
    NotificationManager notificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(notificationId, builder.build());

    // save notification to shared pref
    NotificationProvider.get(context).addNotification(notiObj);
  }

  private static int getNotificationColor(Context context) {
    return ContextCompat.getColor(context, R.color.colorPersianGreen);
  }

  private static int getNotificationIcon() {
    boolean useWhiteIcon =
        (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
    return useWhiteIcon ? R.drawable.ic_notification : R.mipmap.ic_launcher;
  }

  /**
   * This method create notification for job with notification id = job id.
   * We can clear particular notification using this notification id.
   *
   * @param context app context
   * @param notiObj JobNotification object
   */
  public static void showJobNotification(Context context, JobNotification notiObj) {

    // create job detail obj
    JobDetail jobDetail = new JobDetail();
    jobDetail.setMsg_user_id(notiObj.getMsg_user_id());
    jobDetail.setMsg_user_name(notiObj.getMsg_user_name());
    jobDetail.setJob_id(notiObj.getJob_id());
    //jobDetail.setJob_status(notification.getJobStatusNotification());
    jobDetail.setUser_id(notiObj.getUser_id());

    // notification id
    int notificationId = notiObj.getJob_id();

    // create pending intent
    Intent notificationIntent;
    if (notiObj.getUser_type() == RestFields.USER_TYPE_AGENT) {
      notificationIntent = AgentHomeActivity.getContextIntent(context);
      //notificationIntent = JobDetailsActivity.getActivity(context, jobDetail.getJob_id());
    } else {
      notificationIntent = QuestionerJobDetailsActivity.getActivity(context, jobDetail.getJob_id());
    }
    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent pIntent = PendingIntent.getActivity(context, notificationId, notificationIntent,
        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

    // make title
    StringBuilder titleBuilder = new StringBuilder();
    if (notiObj.getMsg_user_name() != null) {
      titleBuilder.append(notiObj.getMsg_user_name());
      titleBuilder.append(" - ");
    }
    titleBuilder.append("Job #");
    titleBuilder.append(notiObj.getJob_id());
    String title = titleBuilder.toString();

    // build notification
    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
    builder.setContentTitle(title).setSmallIcon(getNotificationIcon())
        //.setColor(getNotificationColor(context))
        .setContentText(notiObj.getText())
        .setSound(defaultSoundUri)
        .setTicker(notiObj.getText())
        .setContentIntent(pIntent);

    Notification notification = builder.build();
    notification.defaults = notification.flags |= Notification.FLAG_AUTO_CANCEL;
    NotificationManager notificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(notificationId, notification);
  }

  public static void showAnnouncement(Context context, Announcement announcement) {

    // build notification
    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
    builder.setContentTitle(announcement.getMsg_user_name()).setSmallIcon(getNotificationIcon())
        //.setColor(getNotificationColor(context))
        .setContentText(announcement.getText())
        .setSound(defaultSoundUri)
        .setTicker(announcement.getMsg_user_name() + " : " + announcement.getText());

    Notification notification = builder.build();
    notification.defaults = notification.flags |= Notification.FLAG_AUTO_CANCEL;
    NotificationManager notificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(MESSAGE_KEY, notification);
  }

  public static void clearMsgNotification(Context context, String threadId) {
    if (TextUtils.isEmpty(threadId)) {
      return;
    }
    clearNotification(context, threadId.hashCode());
  }

  public static void clearNotification(Context context, int notificationId) {
    NotificationManager notificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancel(notificationId);
  }

  public static void clearAllNotifications(Context context) {
    NotificationManager notificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancelAll();
  }
}
