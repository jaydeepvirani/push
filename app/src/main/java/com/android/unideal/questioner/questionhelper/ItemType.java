package com.android.unideal.questioner.questionhelper;

import com.android.unideal.R;

/**
 * Created by ADMIN on 11-10-2016.
 */

public enum ItemType {
  MY_JOBS(R.string.title_my_jobs),
  MESSAGES(R.string.title_messages_empety),
  RATING(R.string.title_ratings),
  NOTIFICATIONS(R.string.title_notification_empty),
  PROFILE(R.string.title_profile),
  TRANSACTION(R.string.title_transaction),
  SETTINGS(R.string.title_settings),
  SWITCH_AGENT(R.string.title_switch_to_agent),
  HELP(R.string.title_help),
  REFER_EARN_CODE(R.string.title_refer_my_code);

  private int itemName;

  ItemType(int name) {
    this.itemName = name;
  }

  public int getItemName() {
    return itemName;
  }
}