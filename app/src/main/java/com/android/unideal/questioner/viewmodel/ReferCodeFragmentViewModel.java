package com.android.unideal.questioner.viewmodel;

import android.content.Context;

/**
 * Created by ADMIN on 11-10-2016.
 */

public class ReferCodeFragmentViewModel {
  private ReferCodeListener mListener;
  private Context context;

  public ReferCodeFragmentViewModel(Context context, ReferCodeListener mListener) {
    this.context = context;
    this.mListener = mListener;
  }

  public interface ReferCodeListener {

  }
}
