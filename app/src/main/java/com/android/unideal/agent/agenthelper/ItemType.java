package com.android.unideal.agent.agenthelper;

import com.android.unideal.R;

/**
 * Created by bhavdip on 10/1/16.
 */

public enum ItemType {
  HOME(R.string.title_home),
  MY_JOBS(R.string.title_my_jobs_agent),
  RATING(R.string.title_ratings),
  NOTIFICATIONS(R.string.title_notification_empty),
  PROFILE(R.string.title_profile),
  SETTINGS(R.string.title_settings),
  SWITCH_QUESTIONER(R.string.title_switch_to_questioner),
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
