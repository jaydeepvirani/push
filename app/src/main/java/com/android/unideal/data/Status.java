package com.android.unideal.data;

import com.android.unideal.R;

/**
 * Created by bhavdip on 10/1/16.
 */

public enum Status {
  OPEN(R.string.open),
  APPLIED(R.string.applie),
  IN_PROCESS(R.string.in_progress),
  COMPLETED(R.string.completed),
  PAUSED(R.string.paused),
  CANCELLED(R.string.cancelled);

  private int statusName;

  Status(int name) {
    this.statusName = name;
  }

  public int getStatusName() {
    return statusName;
  }

}
