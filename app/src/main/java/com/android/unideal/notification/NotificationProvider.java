package com.android.unideal.notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.android.unideal.util.converter.JacksonConverter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by CS02 on 12/20/2016.
 */

public class NotificationProvider {
  private static final String PREF_NAME = "NotificationsList.xml";

  private static NotificationProvider SINGLETON;
  private Context _context;
  // Shared pref mode
  private int PRIVATE_MODE = 0;
  private SharedPreferences pref;
  private SharedPreferences.Editor editor;

  private NotificationProvider(Context context) {
    this._context = context;
    pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    editor = pref.edit();
  }

  public static NotificationProvider get(Context context) {
    if (SINGLETON == null) {
      SINGLETON = new NotificationProvider(context);
    }
    return SINGLETON;
  }

  public void addNotification(MsgNotification notification) {
    if (notification == null) {
      return;
    }
    if (editor == null) {
      return;
    }
    String threadId = String.valueOf(notification.getMsg_thread_id());
    Set<String> oldList = getNotifications(threadId);
    oldList.add(JacksonConverter.getStringFromObject(notification));
    editor.putStringSet(threadId, oldList);
    editor.commit();
  }

  /**
   * remove message notification of all threads
   *
   * @param threadId thread id
   */
  public void removeNotification(String threadId) {
    if (TextUtils.isEmpty(threadId)) {
      return;
    }
    editor.remove(threadId);
    editor.commit();
  }

  public void removeAllMsgNotification() {
    editor.clear();
  }

  public Set<String> getNotifications(String threadId) {
    try {
      return pref.getStringSet(threadId, new HashSet<String>());
    } catch (Exception e) {
      return Collections.emptySet();
    }
  }

  public ArrayList<MsgNotification> getAllNotifications() {
    ArrayList<MsgNotification> notifications = new ArrayList<>();

    for (Map.Entry<String, ?> stringEntry : pref.getAll().entrySet()) {
      String objJson = String.valueOf(stringEntry.getValue());
      if (!TextUtils.isEmpty(objJson)) {
        List<MsgNotification> notification =
            JacksonConverter.getListTypeFromJSON(objJson, MsgNotification.class);
        if (notification != null) notifications.addAll(notification);
      }
    }
    return notifications;
  }

  public ArrayList<MsgNotification> getAllNotifications(String threadId) {
    ArrayList<MsgNotification> notifications = new ArrayList<>();
    for (String objJson : getNotifications(threadId)) {
      if (!TextUtils.isEmpty(objJson)) {
        MsgNotification notification =
            JacksonConverter.getObjectFromJSON(objJson, MsgNotification.class);
        if (notification != null) notifications.add(notification);
      }
    }
    return notifications;
  }
}
