package com.android.unideal.auth.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.android.unideal.R;
import com.android.unideal.auth.adapter.SignUpAdapter;
import com.android.unideal.auth.viewmodel.SignUpViewModel;
import com.android.unideal.databinding.SignUpBinding;
import com.android.unideal.enums.AppMode;
import com.android.unideal.util.AppUtility;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by ADMIN on 21-09-2016.
 */

public class SignUpActivity extends AppCompatActivity implements SignUpViewModel.SignUpListener {
  private static final String TAG = "SignUpActivity";
  private SignUpBinding mBinding;
  private SignUpViewModel mViewModel;

  public static Intent startSignUpActivity(Activity activity) {
    return new Intent(activity, SignUpActivity.class);
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    for (Fragment fragment : getSupportFragmentManager().getFragments()) {
      if (fragment != null) {
        fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
      }
    }
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
    mViewModel = new SignUpViewModel(this, this);
    setPagerAdapter();
    //by default agent is selected when activity starts up
    mBinding.pager.setCurrentItem(0);
    mBinding.agent.setSelected(true);
    mBinding.agent.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.d(TAG, "onClick: agent");
        agentSelect();
        mBinding.pager.setCurrentItem(0);
      }
    });

    mBinding.imageViewBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });

    mBinding.questioner.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.d(TAG, "onClick: questioner");
        questionerSelect();
        mBinding.pager.setCurrentItem(1);
      }
    });
    mBinding.pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.d(TAG, "onPageScrolled: " + position);
      }

      @Override
      public void onPageSelected(int position) {
        if (position == 1) {
          questionerSelect();
        } else if (position == 0) {
          agentSelect();
        }
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });
  }

  private void agentSelect() {
    mBinding.questioner.setSelected(false);
    mBinding.agent.setSelected(true);
    changeSignUpToolBarColor(AppMode.AGENT.getValue());
    changeStatusBarColor(AppMode.AGENT.getValue());
  }

  /**
   * userType 1 --> agent
   * userType 2 --> questioner
   */
  private void changeSignUpToolBarColor(int userType) {
    if (userType == 1) {
      mBinding.signUpToolBar.setBackgroundColor(
          ContextCompat.getColor(this, R.color.colorPersianGreen));
    } else if (userType == 2) {
      mBinding.signUpToolBar.setBackgroundColor(
          ContextCompat.getColor(this, R.color.colorCuriousBlue));
    }
  }

  private void questionerSelect() {
    mBinding.questioner.setSelected(true);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      mBinding.questioner.setBackground(
          ContextCompat.getDrawable(this, R.drawable.drw_sign_up_log_in_button_questioner));
    } else {
      mBinding.questioner.setBackgroundDrawable(
          getResources().getDrawable(R.drawable.drw_sign_up_log_in_button_questioner));
    }
    mBinding.agent.setSelected(false);
    changeSignUpToolBarColor(AppMode.QUESTIONER.getValue());
    changeStatusBarColor(AppMode.QUESTIONER.getValue());
  }

  private void setPagerAdapter() {
    //Creating our pager adapter
    SignUpAdapter adapter = new SignUpAdapter(getSupportFragmentManager());
    //Adding adapter to pager
    mBinding.pager.setAdapter(adapter);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }

  /**
   * userType 1 --> agent
   * userType 2 --> questioner
   */

  private void changeStatusBarColor(int userType) {
    if (userType == 1) {
      int colorInt = ContextCompat.getColor(this, R.color.colorPersianGreen);
      AppUtility.changeStatusBarColor(getWindow(), colorInt);
    } else if (userType == 2) {
      int colorInt = ContextCompat.getColor(this, R.color.colorCuriousBlue);
      AppUtility.changeStatusBarColor(getWindow(), colorInt);
    }
  }
}
