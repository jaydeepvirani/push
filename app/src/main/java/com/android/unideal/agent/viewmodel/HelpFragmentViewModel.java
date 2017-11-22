package com.android.unideal.agent.viewmodel;

import android.content.Context;

/**
 * Created by ADMIN on 19-10-2016.
 */

public class HelpFragmentViewModel {
  private Context context;
  private AgentHelpListener mListener;

  public HelpFragmentViewModel(Context context, AgentHelpListener mListener) {
    this.context = context;
    this.mListener = mListener;
  }

  public interface AgentHelpListener {

  }
}
