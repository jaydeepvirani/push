package com.android.unideal.util;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import com.android.unideal.R;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by CS02 on 11/21/2016.
 */

public class DateTimeUtils {
  public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
  public static final String END_DATE_FORMAT = "yyyy-MM-dd HH:mm";
  public static final String TRANSACTION_DATE_TIME = "yyyy-MM-dd HH:mm";
  public static final String TIME_AGO_FORMAT = "yyyy-MM-dd HH:mm:ss";
  public static final String DATE_FORMAT_DD_MM_YYYY = "dd/MM/yyyy";
  public static final String DATE_FORMAT_MSG = "hh:mm a MM,ddd";
  public static final String TIME_FORMAT_MSG = "hh:mm a";

  public static Calendar convertToCalender(String dateString, String format) {
    if (TextUtils.isEmpty(dateString)) {
      return null;
    }
    Date date = convertToDate(dateString, format);
    if (date == null) {
      return null;
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar;
  }

  /**
   * Convert to UTC Format Date
   */
  public static Date convertToDate(String dateString, String format) {
    if (TextUtils.isEmpty(dateString)) {
      return null;
    }
    DateFormat originalFormat = new SimpleDateFormat(format, Locale.ENGLISH);
    originalFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    try {
      return originalFormat.parse(dateString);
    } catch (ParseException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   *
   * @param date
   * @param format
   * @return
   */
  public static String getDateToString(Date date, String format) {
    try {
      SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
      mSimpleDateFormat.setTimeZone(TimeZone.getDefault());
      return mSimpleDateFormat.format(date);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }

  /**
   *
   * @param inputDate
   * @param inputDateFormat
   * @return
   */
  public static Date getStringToDate(String inputDate, String inputDateFormat) {
    if (inputDate == null) {
      return null;
    }
    try {
      SimpleDateFormat mSimpleDateFormat =
          new SimpleDateFormat(inputDateFormat, Locale.getDefault());
      mSimpleDateFormat.setTimeZone(TimeZone.getDefault());
      return mSimpleDateFormat.parse(inputDate);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String messageTime(String dateString) {
    Date date = getStringToDate(UTCtoDefault(dateString), DATE_FORMAT);
    if (date == null) {
      return null;
    } else {
      return messageTimeConvert(date);
    }
  }

  private static String messageTimeConvert(Date date) {
    String words;
    long diff = new Date().getTime() - date.getTime();
    double seconds = Math.abs(diff) / 1000;
    double minutes = seconds / 60;
    double hours = minutes / 60;
    String format = hours < 24 ? TIME_FORMAT_MSG : DATE_FORMAT_MSG;
    DateFormat convertFormat = new SimpleDateFormat(format, Locale.ENGLISH);
    words = convertFormat.format(date);
    return words;
  }

  public static String timeAgo(Context context, String dateString) {
    Date date = getStringToDate(dateString, DateTimeUtils.DATE_FORMAT);
    if (date == null) {
      return null;
    } else {
      return timeAgo(context, date);
    }
  }




  public static String timeAgo(Context context, Date date) {
    String words;
    long diff = new Date().getTime() - date.getTime();
    boolean isPast = true;
    if (diff < 0) {
      diff = Math.abs(diff);
      isPast = false;
    }
    double seconds = Math.abs(diff) / 1000;
    double minutes = seconds / 60;
    double hours = minutes / 60;
    double days = hours / 24;
    double years = days / 365;
    Resources r = context.getResources();
    if (seconds < 60) {
      if (isPast) {
        words = r.getString(R.string.time_ago_seconds);
      } else {
        words = r.getString(R.string.time_remaining_seconds);
      }
    } else if (minutes < 60) {
      if (minutes > 1) {
        if (isPast) {
          words = r.getString(R.string.time_ago_minutes, Math.round(minutes));
        } else {
          words = r.getString(R.string.time_remaining_minutes, Math.round(minutes));
        }
      } else {
        if (isPast) {
          words = r.getString(R.string.time_ago_minute, Math.round(minutes));
        } else {
          words = r.getString(R.string.time_remaining_minute, Math.round(minutes));
        }
      }
    } else if (minutes < 90) {
      if (isPast) {
        words = r.getString(R.string.time_ago_hour, 1);
      } else {
        words = r.getString(R.string.time_remain_hour, 1);
      }
    } else if (hours < 24) {
      if (isPast) {
        words = r.getString(R.string.time_ago_hours, Math.round(hours));
      } else {
        words = r.getString(R.string.time_remain_hours, Math.round(hours));
      }
    } else {
      DateFormat convertFormat = new SimpleDateFormat(TIME_AGO_FORMAT, Locale.ENGLISH);
      words = convertFormat.format(date);
    }
    return words;
  }

  public static String jobEndDateTime(Context context, String dateString) {
    Date date = getStringToDate(UTCtoDefault(dateString), DATE_FORMAT);
    if (date == null) {
      return null;
    }
    return getDateToString(date, END_DATE_FORMAT);
  }

  public static String transactionDateTime(Context context, String dateString) {
    Date date = getStringToDate(UTCtoDefault(dateString), DATE_FORMAT);
    if (date == null) {
      return null;
    }
    return getDateToString(date, TRANSACTION_DATE_TIME);
  }

  public static String convertToUTCDate(String dateString, String format) {
    if (TextUtils.isEmpty(dateString)) {
      return null;
    }
    //1. First a input date is in default time zone that need to convert into date format
    DateFormat originalFormat = new SimpleDateFormat(format, Locale.getDefault());
    originalFormat.setTimeZone(TimeZone.getDefault());
    Date originalDate = null;
    try {
      originalDate = originalFormat.parse(dateString);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    //2 Once we have Date Object we can convert into a UTC format
    SimpleDateFormat destinationFormat = new SimpleDateFormat(format, Locale.getDefault());
    destinationFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    return destinationFormat.format(originalDate);
  }

  public static String UTCtoDefault(String datetime) {
    if (datetime == null) {
      return null;
    }
    //1. First we have UTC format string convert into the Date format
    SimpleDateFormat sourceFormat = new SimpleDateFormat(DATE_FORMAT);
    sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    Date parsed = null;
    try {
      parsed = sourceFormat.parse(datetime);
    } catch (ParseException e) {
      e.printStackTrace();
    } // => Date is in UTC now
    //2. Once we have Date object we can convert into our our default zone format
    TimeZone tz = TimeZone.getDefault();
    SimpleDateFormat destFormat = new SimpleDateFormat(DATE_FORMAT);
    destFormat.setTimeZone(tz);
    return destFormat.format(parsed);
  }

  public static String getCurrentTime() {
    Date date = new Date();
    date.setTime(System.currentTimeMillis());
    return convertToUTCDate(getDateToString(date, DATE_FORMAT), DATE_FORMAT);
  }
}
