package com.android.unideal.util.datetimehelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import com.android.unideal.R;
import com.android.unideal.enums.AppMode;
import com.android.unideal.util.Consts;
import com.android.unideal.util.DateTimeUtils;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by bhavdip on 16/11/16.
 */

public class DateTimerHelper {
  private onDateTimeListener onDateTimeListener;
  private Context activityContext;
  private AppMode currentMode;
  private int mYear, mMonth, mDay;
  private int mMinute, mHourOfDay;
  private Calendar selectedCalendar;
  private SingleDateAndTimePickerDialog defaultDateTimePicker;

  public DateTimerHelper(Activity activityContext, AppMode appMode,
      onDateTimeListener dateTimeListener) {
    this.onDateTimeListener = dateTimeListener;
    this.activityContext = activityContext;
    this.currentMode = appMode;
    initCalendar();
  }

  private void initCalendar() {
    selectedCalendar = Calendar.getInstance();
    final Calendar initCalendar = Calendar.getInstance();
    mYear = initCalendar.get(Calendar.YEAR);
    mMonth = initCalendar.get(Calendar.MONTH);
    mDay = initCalendar.get(Calendar.DAY_OF_MONTH);
    mHourOfDay = initCalendar.get(Calendar.HOUR_OF_DAY);
    mMinute = initCalendar.get(Calendar.MINUTE);
  }

  /**
   * 0 = normal
   * 1 = past mode
   * 2 = future mode
   */
  @SuppressLint("WrongConstant")
  public void showCalendar(int mode) {
    Calendar maxDataRange = Calendar.getInstance();

    if (mode == 0) {
      Date today = new Date();
      maxDataRange.setTime(today);
      maxDataRange.add(Calendar.DAY_OF_MONTH, Consts.daysSelect);
    } else if (mode == 1) {
      Date today = new Date();
      maxDataRange.setTime(today);
    }
    defaultDateTimePicker =
        new SingleDateAndTimePickerDialog.Builder(activityContext).bottomSheet().build();
    if (currentMode == AppMode.AGENT) {
      defaultDateTimePicker.setBackgroundColor(
          ContextCompat.getColor(activityContext, R.color.colorWildSand));
      defaultDateTimePicker.setMainColor(
          ContextCompat.getColor(activityContext, R.color.colorCuriousBlue));
    } else if (currentMode == AppMode.QUESTIONER) {
      defaultDateTimePicker.setBackgroundColor(
          ContextCompat.getColor(activityContext, R.color.colorWildSand));
      defaultDateTimePicker.setMainColor(
          ContextCompat.getColor(activityContext, R.color.colorPersianGreen));
    }
    if (mode == 1) {
      defaultDateTimePicker.setMustBeOnFuture(false);
    } else {
      defaultDateTimePicker.setMustBeOnFuture(true);
    }
    if (selectedCalendar != null) {
      defaultDateTimePicker.setDefaultDate(selectedCalendar.getTime());
    }

    if (mode == 0 || mode == 1) {
      if (mode == 0) {
        //The problem due to the job's completion days is < 3 days
        //We should Set the min date in Add Job as 3 Days from current time
        Date today = new Date();
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(today); // Now use today date.
        startDate.add(Calendar.DATE, 3); // Adding 5 days
        defaultDateTimePicker.setMinDateRange(startDate.getTime());
        defaultDateTimePicker.setDefaultDate(startDate.getTime());
      }
      defaultDateTimePicker.setMaxDateRange(maxDataRange.getTime());
    }

    defaultDateTimePicker.setListener(new SingleDateAndTimePickerDialog.Listener() {
      @Override
      public void onDateSelected(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        onHoldDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE));
        onDateTimeListener.onDateSet();
      }
    });
    defaultDateTimePicker.setCurved(true).display();
  }

  private void onHoldDate(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
    mYear = year;
    mMonth = month;
    mDay = dayOfMonth;
    mHourOfDay = hourOfDay;
    mMinute = minute;
    selectedCalendar.set(mYear, mMonth, mDay, mHourOfDay, mMinute);
  }

  /**
   * Must be call onPause of activity/fragment
   */
  public void closeAllDialog() {
    if (defaultDateTimePicker != null) {
      if (defaultDateTimePicker.isDisplaying()) {
        defaultDateTimePicker.close();
      }
    }
  }

  public Calendar getSelectedCalendar() {
    return selectedCalendar;
  }

  public void setSelectedCalendar(Calendar selectedCalendar) {
    this.selectedCalendar = selectedCalendar;
  }

  /**
   * It will return a format date in current system time zone
   */
  public String getChooseDateTimeInFormat(String responseFormat) {
    selectedCalendar.set(mYear, mMonth, mDay, mHourOfDay, mMinute);
    Date chooseDateTime = new Date(selectedCalendar.getTimeInMillis());
    return DateTimeUtils.getDateToString(chooseDateTime, responseFormat);
  }

  public interface onDateTimeListener {
    void onDateSet();
  }
}
