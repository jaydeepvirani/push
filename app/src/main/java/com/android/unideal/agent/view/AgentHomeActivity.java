package com.android.unideal.agent.view;

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
import com.android.unideal.R;
import com.android.unideal.agent.agenthelper.AgentMenuProvider;
import com.android.unideal.agent.agenthelper.ItemType;
import com.android.unideal.agent.view.fragment.AgentHomeFragment;
import com.android.unideal.agent.view.fragment.AgentSettingsFragment;
import com.android.unideal.agent.view.fragment.HelpFragment;
import com.android.unideal.agent.view.fragment.ProfileFragment;
import com.android.unideal.agent.view.fragment.RatingsFragment;
import com.android.unideal.agent.view.fragment.ReferCodeFragment;
import com.android.unideal.agent.view.fragment.myjobs.MyJobsFragment;
import com.android.unideal.agent.view.fragment.notification.NotificationsFragment;
import com.android.unideal.agent.viewmodel.AgentHomeViewModel;
import com.android.unideal.agent.viewmodel.HomeUpdateListener;
import com.android.unideal.data.AgentOfferData;
import com.android.unideal.data.UserDetail;
import com.android.unideal.databinding.HomeBinding;
import com.android.unideal.enums.AppMode;
import com.android.unideal.flurry.FlurryManager;
import com.android.unideal.questioner.view.QuestionerHomeActivity;
import com.android.unideal.rest.EmptyCallbackWrapper;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import com.android.unideal.rest.response.NotificationResponse;
import com.android.unideal.socket.SocketBaseActivity;
import com.android.unideal.socket.SocketManager;
import com.android.unideal.util.AppUtility;
import com.android.unideal.util.BindingUtils;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.SessionManager;
import com.android.unideal.util.converter.JacksonConverter;
import com.jakewharton.rxbinding.view.RxView;
import com.special.ResideMenu.MenuItem;
import com.special.ResideMenu.MenuItemClickListener;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.functions.Action1;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by ADMIN on 23-09-2016.
 */

public class AgentHomeActivity extends SocketBaseActivity
    implements AgentHomeViewModel.AgentHomeListener, MenuItemClickListener, HomeUpdateListener {
  private static final String TAG = "AgentHomeActivity";
  private TextView textViewUserType, textViewUserName;
  private ImageView imageViewProfilePic;
  private HomeBinding mBinding;
  private AgentHomeViewModel mViewModel;
  private ResideMenu resideMenu;
  private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
    @Override
    public void openMenu() {
      updateNotificationCount();
    }

    @Override
    public void closeMenu() {
    }
  };
  private SocketManager mSocketManager;
  private List<MenuItem> menuItemList;

  public static Intent getActivityIntent(Activity activity) {
    return new Intent(activity, AgentHomeActivity.class);
  }

  public static Intent getContextIntent(Context appContext) {
    return new Intent(appContext, AgentHomeActivity.class);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_agent);
    mViewModel = new AgentHomeViewModel(this, this);
    mBinding.setViewmodel(mViewModel);
    mViewModel.onCreate();
    //start connect to socket
    mSocketManager = SocketManager.getInstance();
  }

  @Override
  public void showProgressDialog() {
    DialogUtils.getInstance().showProgressDialog(this);
  }

  @Override
  public void hideProgressDialog() {
    DialogUtils.getInstance().hideProgressDialog();
  }

  @Override
  public void showToast(String message) {
    AppUtility.showToast(this, message);
  }

  @Override
  public void onWatchOffers(List<AgentOfferData> offerDataList, String extra) {
    if (offerDataList != null) {
      String listToJson = JacksonConverter.getStringFromObject(offerDataList);
      Log.d(TAG, "onWatchOffers: " + listToJson);
      AgentOfferActivity.startOfferActivity(AgentHomeActivity.this, listToJson);
    } else {
      Log.d(TAG, "onWatchOffers: " + extra);
    }
  }

  public void updateNotificationCount() {
    Map<String, Object> hashMap = new HashMap<>();
    hashMap.put(RestFields.KEY_USER_ID, String.valueOf(SessionManager.get(this).getUserId()));
    hashMap.put(RestFields.KEY_USER_TYPE, AppMode.AGENT.getValue());
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

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    registerForNewOffers();
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

  //@Override
  //public boolean dispatchTouchEvent(MotionEvent ev) {
  //  return resideMenu.dispatchTouchEvent(ev);
  //}

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    AgentMenuProvider menuProvider = AgentMenuProvider.getProvider();
    menuItemList = menuProvider.getMenuItemList();
    resideMenu.setMenuItems(menuItemList, ResideMenu.DIRECTION_LEFT);
    textViewUserType.setText(getText(R.string.agent));
  }

  @Override
  public void onBackPressed() {
    setResult(RESULT_OK);
    finish();
  }

  @Override
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
        BindingUtils.profileImage(imageViewProfilePic, userDetail.getProfilePic());
        if (textViewUserName != null) textViewUserName.setText(userDetail.getName());
      }
    }
  }

  @Override
  public void setUpResideMenu() {
    //explicitly hide the soft window keyboard
    AppUtility.hideSoftKeyBoard(this, getCurrentFocus());
    // attach to current activity;
    resideMenu = new ResideMenu(AgentHomeActivity.this, R.layout.menu_header_agent);
    textViewUserType = (TextView) resideMenu.findViewById(R.id.customerType);
    imageViewProfilePic = (ImageView) resideMenu.findViewById(R.id.imageViewAgentPicture);
    textViewUserName = (TextView) resideMenu.findViewById(R.id.textViewAgentName);

    //resideMenu.setBackground(R.drawable.drw_rm_bg_agent);
    resideMenu.setBackgroundColor(getColor(R.color.colorCuriousBlue));

    resideMenu.attachToActivity(this);
    resideMenu.setScaleValue(0.6f);
    resideMenu.setMenuItemListener(this);
    resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
    AgentMenuProvider menuProvider = AgentMenuProvider.getProvider();
    resideMenu.setMenuItems(menuProvider.getMenuItemList(), ResideMenu.DIRECTION_LEFT);
    changeFragment(new AgentHomeFragment());
    resideMenu.defaultSelectedState();
    resideMenu.setMenuListener(menuListener);
  }

  public ResideMenu getResideMenu() {
    return resideMenu;
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

    if (targetFragment instanceof AgentHomeFragment || targetFragment instanceof MyJobsFragment) {
      registerForNewOffers();
    }
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
      case HOME: {
        changeFragment(new AgentHomeFragment());
        break;
      }
      case MY_JOBS: {
        changeFragment(MyJobsFragment.getInstance());
        break;
      }
      case RATING: {
        changeFragment(RatingsFragment.getInstance());
        break;
      }
      case NOTIFICATIONS: {
        changeFragment(NotificationsFragment.getInstance(AppMode.AGENT));
        break;
      }
      case PROFILE: {
        changeFragment(ProfileFragment.getInstance());
        break;
      }
      case SETTINGS: {
        changeFragment(AgentSettingsFragment.getInstance());
        break;
      }
      case SWITCH_QUESTIONER: {
        showSwitchDialog();
        break;
      }
      case HELP: {
        changeFragment(HelpFragment.getInstance());
        break;
      }
      case REFER_EARN_CODE: {
        changeFragment(ReferCodeFragment.getInstance());
        break;
      }
    }
  }

  private void showSwitchDialog() {
    DialogUtils.showDialog(this, R.string.title_app_name, R.string.switch_to_questioner,
        R.string.btn_ok, R.string.btn_cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            switchUser();
          }
        });
  }

  private void switchUser() {
    DialogUtils.getInstance().showProgressDialog(this);
    final  int userId = SessionManager.get(this).getUserId();
    Call<GenericResponse> call =
        RestClient.get().switchUser(userId, RestFields.USER_TYPE_REQUESTER);
    call.enqueue(new EmptyCallbackWrapper() {
      @Override
      public void onSuccess(GenericResponse response) {
        //flurry
        FlurryManager.swtichRole(userId, AppMode.QUESTIONER.name());
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
    startActivity(QuestionerHomeActivity.getActivityIntent(this));
    SessionManager.get(AgentHomeActivity.this).setUserMode(AppMode.QUESTIONER.getValue());
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

  private void registerForNewOffers() {
    //when loading Home Fragment make the call
    mViewModel.listenOnNewOffers(getUserDetail());
  }
}
