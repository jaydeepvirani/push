package com.android.unideal.questioner.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;
import com.android.unideal.R;
import com.android.unideal.agent.view.AgentHomeActivity;
import com.android.unideal.agent.view.fragment.notification.NotificationsFragment;
import com.android.unideal.agent.viewmodel.HomeUpdateListener;
import com.android.unideal.data.UnReadCount;
import com.android.unideal.data.UserDetail;
import com.android.unideal.data.socket.Message;
import com.android.unideal.databinding.QuestionerHomeBinding;
import com.android.unideal.enums.AppMode;
import com.android.unideal.flurry.FlurryManager;
import com.android.unideal.questioner.questionhelper.ItemType;
import com.android.unideal.questioner.questionhelper.QuestionerProvider;
import com.android.unideal.questioner.view.fragments.QuestionerBalanceFragment;
import com.android.unideal.questioner.view.fragments.QuestionerHelpFragment;
import com.android.unideal.questioner.view.fragments.QuestionerMessageFragment;
import com.android.unideal.questioner.view.fragments.QuestionerProfileFragment;
import com.android.unideal.questioner.view.fragments.QuestionerSettingsFragment;
import com.android.unideal.questioner.view.fragments.ReferCodeFragment;
import com.android.unideal.questioner.view.fragments.myjobs.MyJobsFragment;
import com.android.unideal.questioner.view.fragments.myjobs.QuestionerRatingsFragment;
import com.android.unideal.questioner.viewmodel.QuestionerHomeActivityViewModel;
import com.android.unideal.rest.EmptyCallbackWrapper;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import com.android.unideal.rest.response.NotificationResponse;
import com.android.unideal.service.UploadingService;
import com.android.unideal.socket.SocketBaseActivity;
import com.android.unideal.socket.SocketConst;
import com.android.unideal.socket.SocketListener;
import com.android.unideal.socket.SocketManager;
import com.android.unideal.util.AppUtility;
import com.android.unideal.util.BindingUtils;
import com.android.unideal.util.Consts;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.SessionManager;
import com.android.unideal.util.ShowCasePreference;
import com.android.unideal.util.converter.JacksonConverter;
import com.jakewharton.rxbinding.view.RxView;
import com.special.ResideMenu.MenuItem;
import com.special.ResideMenu.MenuItemClickListener;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.functions.Action1;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by ADMIN on 11-10-2016.
 */

public class QuestionerHomeActivity extends SocketBaseActivity
    implements QuestionerHomeActivityViewModel.QuestionerListener, MenuItemClickListener,
    MaterialIntroListener, HomeUpdateListener, SocketListener {
  private static final String FOCUS_MSG = "focus_msg";
  private static final String FOCUS_PROFILE = "focus_profile";
  private static final String FOCUS_AGT = "focus_agt";
  private static final String FOCUS_REFER_CODE = "focus_refer_code";
  private static final String FOCUS_MENU = "focus_menu";
  MaterialIntroView materialIntroView;
  List<ResideMenuItem> menuItems;
  private QuestionerHomeBinding mBinding;
  private QuestionerHomeActivityViewModel mViewModel;
  private ResideMenu resideMenu;
  private TextView textViewUserType, textViewRequesterName;
  private ImageView imageViewUserPic;
  private SocketManager mSocketManager;
  private boolean isShowCaseLayoutVisible = true;

  public static Intent getActivityIntent(Activity activity) {
    return new Intent(activity, QuestionerHomeActivity.class);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_questioner);
    mViewModel = new QuestionerHomeActivityViewModel(this, this);
    mViewModel.onCreate();
    setShowCaseLayout();
  }

  public void setShowCaseLayout() {
    resideMenu.setMenuListener(new ResideMenu.OnMenuListener() {
      @Override
      public void openMenu() {
        openSocket();
        updateNotificationCount();
        if (resideMenu == null) {
          return;
        }
        if (SessionManager.get(QuestionerHomeActivity.this).shouldShowTourGuide()
            == Consts.SHOWCASE_GUIDE_SHOW) {
          if (ShowCasePreference.get(QuestionerHomeActivity.this).getShowCaseLayoutMsg()) {
            ShowCasePreference.get(QuestionerHomeActivity.this).setShowCaseLayoutMsg(false);
            menuItems = resideMenu.getMenuItems(ResideMenu.DIRECTION_LEFT);
            TextView message = menuItems.get(1).textView();
            setMaterialView(message, FOCUS_MSG, getString(R.string.txt_show_case_que_msg));
            return;
          }
        }
        if (SessionManager.get(QuestionerHomeActivity.this).shouldShowTourGuide()
            == Consts.SHOWCASE_GUIDE_SHOW) {
          if (ShowCasePreference.get(QuestionerHomeActivity.this).getShowCaseLayoutProf()) {
            ShowCasePreference.get(QuestionerHomeActivity.this).setShowCaseLayoutProf(false);
            menuItems = resideMenu.getMenuItems(ResideMenu.DIRECTION_LEFT);
            TextView message = menuItems.get(4).textView();
            setMaterialView(message, FOCUS_PROFILE, getString(R.string.txt_show_case_que_profile));
            return;
          }
        }
        if (SessionManager.get(QuestionerHomeActivity.this).shouldShowTourGuide()
            == Consts.SHOWCASE_GUIDE_SHOW) {
          if (ShowCasePreference.get(QuestionerHomeActivity.this).getShowCaseLayoutSwitch()) {
            ShowCasePreference.get(QuestionerHomeActivity.this).setShowCaseSwitch(false);
            menuItems = resideMenu.getMenuItems(ResideMenu.DIRECTION_LEFT);
            TextView message = menuItems.get(7).textView();
            setMaterialView(message, FOCUS_AGT, getString(R.string.txt_show_case_que_switch_ac));
            return;
          }
        }
        if (SessionManager.get(QuestionerHomeActivity.this).shouldShowTourGuide()
            == Consts.SHOWCASE_GUIDE_SHOW) {
          if (ShowCasePreference.get(QuestionerHomeActivity.this).getShowCaseLayoutReferCode()) {
            ShowCasePreference.get(QuestionerHomeActivity.this).setShowCaseReferCode(false);
            menuItems = resideMenu.getMenuItems(ResideMenu.DIRECTION_LEFT);
            TextView message = menuItems.get(9).textView();
            setMaterialView(message, FOCUS_REFER_CODE,
                getString(R.string.txt_show_case_que_refer_code));
            return;
          }
        }
      }

      @Override
      public void closeMenu() {
      }
    });
    if (SessionManager.get(QuestionerHomeActivity.this).shouldShowTourGuide()
        == Consts.SHOWCASE_GUIDE_SHOW) {
      setMaterialView(mBinding.menuButton, FOCUS_MENU, getString(R.string.txt_show_case_que_menu));
    }
  }

  private void openSocket() {
    mSocketManager.setSocketListener(QuestionerHomeActivity.this);
    if (mSocketManager.isConnected()) {
      mSocketManager.emitEvent(SocketConst.EVENT_AUTH_REQ,
          SessionManager.get(QuestionerHomeActivity.this).getUserId(),
          AppMode.QUESTIONER.getValue());
    } else {
      mSocketManager.openSocket();
      mSocketManager.listenEvents();
      mSocketManager.connect();
    }
  }

  private void setMaterialView(TextView view, String id, String text) {
    materialIntroView = new MaterialIntroView.Builder(this).enableDotAnimation(true)
        .setFocusGravity(FocusGravity.CENTER)
        .setFocusType(Focus.ALL)
        .setShape(ShapeType.CIRCLE)
        .setTargetPadding(30)
        .performClick(false)
        .setListener(this)
        .setDelayMillis(200)
        .enableIcon(false)
        .enableFadeAnimation(true)
        .dismissOnTouch(Consts.dismissOnTouch)
        .setInfoText(text)
        .setTarget(view)
        .setUsageId(id) //THIS SHOULD BE UNIQUE ID
        .show();
  }

  private void setMaterialView(ImageView view, String id, String text) {
    materialIntroView = new MaterialIntroView.Builder(this).enableDotAnimation(true)
        .setFocusGravity(FocusGravity.CENTER)
        .setFocusType(Focus.ALL)
        .setShape(ShapeType.CIRCLE)
        .setDelayMillis(200)
        .enableIcon(false)
        .enableFadeAnimation(true)
        .dismissOnTouch(Consts.dismissOnTouch)
        .performClick(false)
        .setInfoText(text)
        .setTarget(view)
        .setUsageId(id) //THIS SHOULD BE UNIQUE ID
        .show();
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
  public void onConfigurationChanged(Configuration newConfig) {
    QuestionerProvider menuProvider = QuestionerProvider.getProvider();
    resideMenu.setMenuItems(menuProvider.getMenuItemList(), ResideMenu.DIRECTION_LEFT);
    textViewUserType.setText(getString(R.string.questioner));
  }

  @Override
  public void onBackPressed() {
    setResult(RESULT_OK);
    finish();
  }

  @Override
  public void setUpResideMenu() {
    //start connect to socket
    mSocketManager = SocketManager.getInstance();
    //explicitly hide the soft window keyboard
    AppUtility.hideSoftKeyBoard(this, getCurrentFocus());
    // attach to current activity;
    resideMenu = new ResideMenu(QuestionerHomeActivity.this, R.layout.menu_header_questioner);
    textViewUserType = (TextView) resideMenu.findViewById(R.id.customerType);
    textViewRequesterName = (TextView) resideMenu.findViewById(R.id.textViewRequesterName);
    imageViewUserPic = (ImageView) resideMenu.findViewById(R.id.imageViewRequesterPic);
    resideMenu.setBackgroundColor(getColor(R.color.colorPersianGreen));
    resideMenu.attachToActivity(this);
    resideMenu.setScaleValue(0.6f);
    resideMenu.setMenuItemListener(this);
    resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
    QuestionerProvider menuProvider = QuestionerProvider.getProvider();
    resideMenu.setMenuItems(menuProvider.getMenuItemList(), ResideMenu.DIRECTION_LEFT);
    //default load the My Jobs Fragment
    changeFragment(new MyJobsFragment());

    resideMenu.defaultSelectedState();
  }

  public void startBindingViews() {
    RxView.clicks(mBinding.menuButton).subscribe(new Action1<Void>() {
      @Override
      public void call(Void aVoid) {
        resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
      }
    });
  }

  @Override
  public void updateProfileInfo() {
    if (resideMenu != null) {
      // For load profile picture load user details
      UserDetail userDetail = getUserDetail();
      if (userDetail != null) {
        BindingUtils.profileImage(imageViewUserPic, userDetail.getProfilePic());
        if (textViewRequesterName != null) textViewRequesterName.setText(userDetail.getName());
      }
    }
  }

  @Override
  public void startUploadingService() {
    //start uploading service
    Intent uploadingIntent = UploadingService.getServiceIntent(this);
    startService(uploadingIntent);
  }

  private UserDetail getUserDetail() {
    if (SessionManager.get(this) != null) {
      if (SessionManager.get(this).getActiveUser() != null) {
        return SessionManager.get(this).getActiveUser();
      }
      return null;
    }
    return null;
  }

  public void setDirectionDisable(int directionDisable) {
    resideMenu.setSwipeDirectionDisable(directionDisable);
  }

  public void setDirectionEnable(int directionDisable) {
    resideMenu.setSwipeDirectionEnable(directionDisable);
  }

  private void changeFragment(Fragment targetFragment) {
    closeResideMenu();
    resideMenu.clearIgnoredViewList();
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.container, targetFragment, "fragment")
        .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        .commit();
  }

  private void closeResideMenu() {
    if (resideMenu != null && resideMenu.isOpened()) {
      resideMenu.closeMenu();
    }
  }

  @Override
  public void onMenuItemClick(MenuItem menuItem) {
    ItemType itemType = (ItemType) menuItem.getMenuType().getType();
    switch (itemType) {
      case MY_JOBS: {
        changeFragment(MyJobsFragment.getInstance(isShowCaseLayoutVisible));
        break;
      }
      case MESSAGES: {
        changeFragment(QuestionerMessageFragment.getInstance());
        break;
      }
      case RATING: {
        changeFragment(QuestionerRatingsFragment.getInstance());
        break;
      }
      case NOTIFICATIONS: {
        changeFragment(NotificationsFragment.getInstance(AppMode.QUESTIONER));
        break;
      }
      case PROFILE: {
        changeFragment(QuestionerProfileFragment.getInstance());
        break;
      }
      case TRANSACTION: {
        changeFragment(QuestionerBalanceFragment.getInstance());
        break;
      }

      case SETTINGS: {
        changeFragment(QuestionerSettingsFragment.getInstance());
        break;
      }
      case SWITCH_AGENT: {
        showSwitchDialog();
        break;
      }
      case HELP: {
        changeFragment(QuestionerHelpFragment.getInstance());
        break;
      }
      case REFER_EARN_CODE: {
        changeFragment(ReferCodeFragment.getInstance());
        break;
      }
    }
  }

  private void showSwitchDialog() {
    DialogUtils.showDialog(this, R.string.title_app_name, R.string.switch_to_agent, R.string.btn_ok,
        R.string.btn_cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            switchUser();
          }
        });
  }

  private void switchUser() {
    DialogUtils.getInstance().showProgressDialog(this);
    final int userId = SessionManager.get(this).getUserId();
    Call<GenericResponse> call = RestClient.get().switchUser(userId, RestFields.USER_TYPE_AGENT);
    call.enqueue(new EmptyCallbackWrapper() {
      @Override
      public void onSuccess(GenericResponse response) {
        FlurryManager.swtichRole(userId, AppMode.AGENT.name());
        DialogUtils.getInstance().hideProgressDialog();
        openHomeScreen();
      }

      @Override
      public void onFailure(GenericResponse response) {
        DialogUtils.getInstance().hideProgressDialog();
        openHomeScreen();
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        DialogUtils.getInstance().hideProgressDialog();
        openHomeScreen();
      }
    });
  }

  private void openHomeScreen() {
    startActivity(AgentHomeActivity.getActivityIntent(this));
    SessionManager.get(QuestionerHomeActivity.this).setUserMode(AppMode.AGENT.getValue());
    finish();
  }

  @Override
  protected void onOpenApp() {
    mSocketManager.openSocket();
    mSocketManager.listenEvents();
    mSocketManager.connect();
  }

  @Override
  protected void onCloseApp() {
    mSocketManager.disconnect();
  }

  @Override
  public void updateNotification(int count) {
    if (resideMenu == null) {
      return;
    }
    List<ResideMenuItem> menuItems = resideMenu.getMenuItems(ResideMenu.DIRECTION_LEFT);
    if (menuItems != null && menuItems.size() > 3) {
      ResideMenuItem notificationItem = menuItems.get(3);
      if (count == 0) {
        notificationItem.setTitle(R.string.title_notification_empty);
      } else {
        notificationItem.setTitle(getString(R.string.title_notification, count));
      }
    }
  }

  public void updateNotificationCount() {
    Map<String, Object> hashMap = new HashMap<>();
    hashMap.put(RestFields.KEY_USER_ID, String.valueOf(SessionManager.get(this).getUserId()));
    hashMap.put(RestFields.KEY_USER_TYPE, AppMode.QUESTIONER.getValue());
    hashMap.put(RestFields.KEY_PAGE_INDEX, 0);
    Call<NotificationResponse> call = RestClient.get().getNotificationList(hashMap);
    call.enqueue(new Callback<NotificationResponse>() {
      @Override
      public void onResponse(Call<NotificationResponse> call,
          Response<NotificationResponse> response) {
        if (response.isSuccessful()) {
          NotificationResponse notiRes = response.body();
          if (notiRes != null) {
            if (notiRes.getSuccess() == 1) {
              updateNotification(notiRes.getTotal_unread());
            }
          }
        }
      }

      @Override
      public void onFailure(Call<NotificationResponse> call, Throwable t) {
      }
    });
  }

  public void updateMessageCount(final int count) {
    if (resideMenu == null) {
      return;
    }
    List<ResideMenuItem> menuItems = resideMenu.getMenuItems(ResideMenu.DIRECTION_LEFT);
    if (menuItems != null && menuItems.size() > 1) {
      final ResideMenuItem messageItem = menuItems.get(1);
      if (count == 0) {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            messageItem.setTitle(R.string.title_messages_empety);
          }
        });
      } else {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            messageItem.setTitle(getString(R.string.title_messages, count));
          }
        });
      }
    }
  }

  @Override
  public void onSocketConnected() {

  }

  @Override
  public void onSocketError(Object... args) {
    Log.d("QuestionerHomeActivity", "onSocketError() called with: args = [" + args + "]");
  }

  @Override
  public void onAuthSuccess(Object... args) {
    Log.d("QuestionerHomeActivity",
        "onAuthSuccess() called with: args = [" + Arrays.toString(args) + "]");
    mSocketManager.emitEvent(SocketConst.EVENT_GET_UNREAD_COUNT,
        SessionManager.get(QuestionerHomeActivity.this).getUserId());
  }

  @Override
  public void onMessageReceive(Message message) {
    Log.d("onMessageReceive", "onMessageReceive() called with: message = [" + message + "]");
  }

  @Override
  public void onMessagesListReceived(Object... args) {

  }

  @Override
  public void onUpdateMessages(Object... args) {

  }

  @Override
  public void onHistoryReceive(Object... args) {

  }

  @Override
  public void onStatusReceive(Object... args) {

  }

  @Override
  public void onReceivedMessage(Object... args) {

  }

  @Override
  public void onReadMessage(Object... args) {

  }

  @Override
  public void onNewAgentMessages(Object... args) {

  }

  @Override
  public void onUnreadCount(Object... args) {
    Log.d("unReadCount", "[" + Arrays.toString(args) + "]");
    UnReadCount unReadCount =
        JacksonConverter.getObjectFromJSON((String) args[0], UnReadCount.class);
    if (unReadCount != null) {
      updateMessageCount(unReadCount.getUnreadCount());
    }
  }

  @Override
  public void onUserClicked(String usesId) {

  }
}
