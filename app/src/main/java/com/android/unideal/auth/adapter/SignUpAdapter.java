package com.android.unideal.auth.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.android.unideal.agent.view.fragment.AgentSignUpFragment;
import com.android.unideal.questioner.view.fragments.QuestionerSignUpFragment;

/**
 * Created by ADMIN on 22-09-2016.
 */

public class SignUpAdapter extends FragmentStatePagerAdapter {
  public SignUpAdapter(FragmentManager fm) {
    super(fm);
  }

  @Override
  public Fragment getItem(int position) {
    switch (position) {
      case 0:
        return AgentSignUpFragment.getInstance();
      case 1:
        return QuestionerSignUpFragment.getInstance();
      default:
        return null;
    }
  }

  @Override
  public int getCount() {
    return 2;
  }
}
