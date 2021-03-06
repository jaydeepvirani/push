package com.android.unideal.questioner.view.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import co.mobiwise.materialintro.prefs.PreferencesManager;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;
import com.android.unideal.R;
import com.android.unideal.agent.viewmodel.SettingsViewModel;
import com.android.unideal.auth.view.LogInActivity;
import com.android.unideal.data.UserDetail;
import com.android.unideal.databinding.RequesterSettingBinding;
import com.android.unideal.enums.AppMode;
import com.android.unideal.flurry.FlurryManager;
import com.android.unideal.questioner.view.TermsNConditionActivity;
import com.android.unideal.questioner.view.payment.ManageCardActivity;
import com.android.unideal.rest.RestClient;
import com.android.unideal.util.Consts;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.SessionManager;
import com.android.unideal.util.ShowCasePreference;
import com.social.fb.FaceBookHelper;
import java.util.Locale;

/**
 * Created by ADMIN on 12-10-2016.
 */

public class QuestionerSettingsFragment extends Fragment
    implements SettingsViewModel.SettingsListener {
  private RequesterSettingBinding mBinding;
  private SettingsViewModel mViewModel;
  private int newPost, jobStatus, transactionStatus, tourGuideStatus;
  private MaterialIntroView materialIntroView;
  private String TOUR_GUIDE_STATUS = "tourGuideStatus";

  public static Fragment getInstance() {
    return new QuestionerSettingsFragment();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mBinding = loadBinding(inflater, container);
    mViewModel = new SettingsViewModel(getActivity(), this);
    if (SessionManager.get(getActivity()).shouldShowTourGuide() == Consts.SHOWCASE_GUIDE_SHOW) {
      setShowCaseLayout();
    }
    return mBinding.getRoot();
  }

  private void setShowCaseLayout() {
    materialIntroView = new MaterialIntroView.Builder(getActivity()).enableDotAnimation(true)
        .setFocusGravity(FocusGravity.CENTER)
        .setFocusType(Focus.NORMAL)
        .setDelayMillis(200)
        .enableIcon(false)
        .enableFadeAnimation(true)
        .dismissOnTouch(Consts.dismissOnTouch)
        .performClick(false)
        .setShape(ShapeType.CIRCLE)
        .setInfoText(getString(R.string.txt_setting_torguide))
        .setTarget(mBinding.tourGuideStatus)
        .setUsageId(TOUR_GUIDE_STATUS) //THIS SHOULD BE UNIQUE ID
        .show();
  }

  private RequesterSettingBinding loadBinding(LayoutInflater inflater, ViewGroup container) {
    return DataBindingUtil.inflate(inflater, R.layout.fragment_settings_questioner, container,
        false);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mViewModel.onActivityCreated();
  }

  @Override
  public void startBindingViews() {
    if (SessionManager.get(getContext()) != null) {
      if ((SessionManager.get(getContext()).getLanguageMode().equals(Consts.ENGLISH_MODE))) {
        mBinding.english.setChecked(true);
        mBinding.chinese.setChecked(false);
      } else if (SessionManager.get(getActivity()).getLanguageMode().equals(Consts.CHINESE_MODE)) {
        mBinding.chinese.setChecked(true);
        mBinding.english.setChecked(false);
      } else {
        mBinding.chinese.setChecked(false);
        mBinding.english.setChecked(true);
      }
    }
    loadRadioButtonEvent();
    mBinding.logOutButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        DialogUtils.showDialog(getContext(), R.string.title_app_name, R.string.message_log_out,
            R.string.text_log_out, R.string.btn_cancel, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                logoutUser();
              }
            });
      }
    });

    mBinding.deleteAcount.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        DialogUtils.showDialog(getContext(), R.string.title_app_name,
            R.string.message_delete_account, R.string.btn_delete, R.string.btn_cancel,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleteUser();
              }
            });
      }
    });

    mBinding.manageCreditCard.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ManageCardActivity.getManageCardIntent(getActivity());
      }
    });
    getSettingSession();
    mBinding.newPostNotification.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
              newPost = 1;
            } else {
              newPost = 0;
            }
            mViewModel.updateSetting(newPost, jobStatus, transactionStatus, tourGuideStatus);
          }
        });
    mBinding.jobStatusNotification.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
              jobStatus = 1;
            } else {
              jobStatus = 0;
            }
            mViewModel.updateSetting(newPost, jobStatus, transactionStatus, tourGuideStatus);
          }
        });

    mBinding.transactionsNotifications.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
              transactionStatus = 1;
            } else {
              transactionStatus = 0;
            }
            mViewModel.updateSetting(newPost, jobStatus, transactionStatus, tourGuideStatus);
          }
        });
    mBinding.tourGuideStatus.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
              tourGuideStatus = 1;
            } else {
              tourGuideStatus = 0;
            }
            mViewModel.updateSetting(newPost, jobStatus, transactionStatus, tourGuideStatus);
          }
        });
    mBinding.FAQ.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent =
            TermsNConditionActivity.startTermsNConditionActivity(getActivity(), AppMode.QUESTIONER);
        startActivity(intent);
      }
    });
    mBinding.contactUs.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        openEmailDialog();
      }
    });
  }

  private void openEmailDialog() {
    try {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      Uri data = Uri.parse("mailto:"
          + SessionManager.get(getActivity()).getAdminEmail()
          + "?subject="
          + ""
          + "&body=");
      intent.setData(data);
      getActivity().startActivity(intent);
    } catch (android.content.ActivityNotFoundException ex) {
      DialogUtils.showToast(getActivity(), "App not available");
    }
  }

  public void getSettingSession() {
    newPost = SessionManager.get(getContext()).getNewJobNotification();
    if (newPost == 1) {
      mBinding.newPostNotification.setChecked(true);
    } else {
      mBinding.newPostNotification.setChecked(false);
    }
    jobStatus = SessionManager.get(getContext()).getJobStatusNotification();
    if (jobStatus == 1) {
      mBinding.jobStatusNotification.setChecked(true);
    } else {
      mBinding.jobStatusNotification.setChecked(false);
    }
    transactionStatus = SessionManager.get(getContext()).getTransactionNotification();
    if (transactionStatus == 1) {
      mBinding.transactionsNotifications.setChecked(true);
    } else {
      mBinding.transactionsNotifications.setChecked(false);
    }
    tourGuideStatus = SessionManager.get(getContext()).shouldShowTourGuide();
    if (tourGuideStatus == 1) {
      mBinding.tourGuideStatus.setChecked(true);
    } else {
      mBinding.tourGuideStatus.setChecked(false);
    }
  }

  private void deleteUser() {
    int userId = SessionManager.get(getActivity()).getUserId();
    mViewModel.deleteUser(userId);
  }

  private void logoutUser() {
    UserDetail userDetail = SessionManager.get(getActivity()).getActiveUser();
    FlurryManager.startEventLogOut(userDetail);
    mViewModel.logoutUser(userDetail.getUserId(), userDetail.getDeviceToken());
  }

  private void loadRadioButtonEvent() {
    mBinding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

      @Override
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        Log.d("chk", "id" + checkedId);
        UserDetail userDetail = SessionManager.get(getActivity()).getActiveUser();
        if (checkedId == R.id.english) {
          //some code
          changeLanguages(Consts.LOCALE_ENGLISH);
          SessionManager.get(getContext()).setLanguageMode(Consts.ENGLISH_MODE);
          RestClient.resetClient();
          FlurryManager.loggedLocale(userDetail.getUserId(), Consts.LOCALE_ENGLISH,
              AppMode.QUESTIONER.name());
        } else if (checkedId == R.id.chinese) {
          //some code
          changeLanguages(Consts.LOCALE_CHINESE);
          SessionManager.get(getContext()).setLanguageMode(Consts.CHINESE_MODE);
          RestClient.resetClient();
          FlurryManager.loggedLocale(userDetail.getUserId(), Consts.LOCALE_CHINESE,
              AppMode.QUESTIONER.name());
        } else {
          //some code
          changeLanguages(Consts.LOCALE_ENGLISH);
          SessionManager.get(getContext()).setLanguageMode(Consts.ENGLISH_MODE);
          RestClient.resetClient();
        }
      }
    });
  }

  private void changeLanguages(String languageMode) {
    Locale myLocale = new Locale(languageMode);
    Resources res = getResources();
    DisplayMetrics dm = res.getDisplayMetrics();
    Configuration conf = res.getConfiguration();
    conf.locale = myLocale;
    res.updateConfiguration(conf, dm);
    getActivity().getApplicationContext()
        .getResources()
        .updateConfiguration(conf, getResources().getDisplayMetrics());
    onConfigurationChanged(conf);
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    mBinding.manageNotification.setText(getString(R.string.text_manage_notifications));
    mBinding.expertTopic.setText(getString(R.string.text_expert_topic));
    mBinding.lbljobStatus.setText(getString(R.string.title_job_status));
    mBinding.lblTransaction.setText(getString(R.string.transaction));
    mBinding.manageCreditCard.setText(getString(R.string.text_manage_credit_card));
    mBinding.settings.setText(getString(R.string.settings));
    mBinding.deleteAcount.setText(getString(R.string.text_delete_account));
    mBinding.logOutButton.setText(getString(R.string.text_log_out));
    mBinding.textViewContactUs.setText(getString(R.string.contact_us));
    mBinding.textViewTNQ.setText(getString(R.string.termsNCondition));
    mBinding.lblTourGuide.setText(getString(R.string.app_tour_guide));
    (getActivity()).onConfigurationChanged(newConfig);
  }

  @Override
  public void onLogoutSuccess() {
    new FaceBookHelper(getActivity()).logout();
    FlurryManager.stopEventLogOut();
    openLoginActivity();
  }

  @Override
  public void onDeleteSuccess() {
    new FaceBookHelper(getActivity()).logout();
    openLoginActivity();
  }

  @Override
  public void showAlertDialog(String message) {
    DialogUtils.showDialog(getContext(), R.string.title_app_name, message, R.string.btn_ok,
        R.string.btn_cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });
  }

  private void openLoginActivity() {
    ShowCasePreference.get(getActivity()).clearShowCase();
    new PreferencesManager(getActivity()).resetAll();
    SessionManager.get(getActivity()).logout();
    Intent intent = LogInActivity.startLogInActivity(getActivity());
    startActivity(intent);
    getActivity().finish();
  }
}
