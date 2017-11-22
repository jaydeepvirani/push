package com.android.unideal.agent.view.fragment.notification;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.agent.adapter.NotificationAdapter;
import com.android.unideal.agent.view.JobDetailsActivity;
import com.android.unideal.agent.viewmodel.HomeUpdateListener;
import com.android.unideal.agent.viewmodel.NotificationViewModel;
import com.android.unideal.data.Notifications;
import com.android.unideal.databinding.NotificationBinding;
import com.android.unideal.enums.AppMode;
import com.android.unideal.questioner.view.QuestionerJobDetailsActivity;
import com.android.unideal.util.AppUtility;
import com.android.unideal.util.SessionManager;
import java.util.List;

/**
 * Created by bhavdip on 5/10/16.
 */
public class NotificationsFragment extends Fragment
    implements NotificationAdapter.OnItemClick, NotificationAdapter.OnLoadMoreListener,
    SwipeRefreshLayout.OnRefreshListener, NotificationViewModel.NotificationListener {
  private static final String KEY_MODE = "AppMode";

  private NotificationBinding mNotificationBinding;
  private NotificationAdapter mNotificationAdapter;
  private NotificationViewModel mViewModel;
  private HomeUpdateListener mHomeUpdateListener;
  private int unreadCount;
  private AppMode mCurrentAppMode;

  /**
   * Return the Notification Fragment
   */
  public static Fragment getInstance(AppMode appMode) {
    NotificationsFragment mFragment = new NotificationsFragment();
    Bundle mBundle = new Bundle();
    mBundle.putSerializable(KEY_MODE, appMode);
    mFragment.setArguments(mBundle);
    return mFragment;
  }

  @TargetApi(23)
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    onAttachToContext(context);
  }

  /*
   * Deprecated on API 23
   * Use onAttachToContext instead
   */
  @SuppressWarnings("deprecation")
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      onAttachToContext(activity);
    }
  }

  /*
   * Called when the fragment attaches to the context
   */
  protected void onAttachToContext(Context context) {
    if (context instanceof HomeUpdateListener) {
      mHomeUpdateListener = (HomeUpdateListener) context;
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments().containsKey(KEY_MODE)) {
      mCurrentAppMode = (AppMode) getArguments().getSerializable(KEY_MODE);
    }
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mViewModel = new NotificationViewModel();
    mViewModel.setListener(this);
    mNotificationBinding = loadBinding(inflater, container);
    mNotificationBinding.swipeRefresh.setOnRefreshListener(this);
    bindViews();
    updateNotificationTitle(0);
    return mNotificationBinding.getRoot();
  }

  /**
   * It will return the notifications layout
   */
  private NotificationBinding loadBinding(LayoutInflater inflater, ViewGroup container) {
    return DataBindingUtil.inflate(inflater, R.layout.fragment_notifiactions, container, false);
  }

  /**
   * Start binding the all views
   */
  private void bindViews() {
    mNotificationAdapter = new NotificationAdapter(getActivity(), mCurrentAppMode);
    mNotificationAdapter.setLoadMoreListener(this);
    mNotificationAdapter.setOnItemClick(this);
    mNotificationBinding.notificationsList.setHasFixedSize(true);
    mNotificationBinding.notificationsList.setLayoutManager(new LinearLayoutManager(getContext()));
    mNotificationBinding.notificationsList.setAdapter(mNotificationAdapter);
    onLoadMore();
    if (mCurrentAppMode == AppMode.AGENT) {
      mNotificationBinding.toolbar.toolbarLayout.setBackgroundColor(
          ContextCompat.getColor(getContext(), R.color.colorCuriousBlue));
    }
    if (mCurrentAppMode == AppMode.QUESTIONER) {
      mNotificationBinding.toolbar.toolbarLayout.setBackgroundColor(
          ContextCompat.getColor(getContext(), R.color.colorPersianGreen));
    }
  }

  @Override
  public void onItemClick(Notifications chooseItems) {
    if (mCurrentAppMode == AppMode.AGENT) {
      Intent intent = JobDetailsActivity.getActivity(getActivity(), chooseItems.getJob_id());
      startActivity(intent);
    } else {
      Intent intent =
          QuestionerJobDetailsActivity.getActivity(getActivity(), chooseItems.getJob_id());
      startActivity(intent);
    }
    if(chooseItems.getIs_read()==0) {
      readNotification(chooseItems.getNotification_id());
    }
  }

  private void readNotification(int notification_id) {
    if (mCurrentAppMode == null) {
      return;
    }
    int userId = SessionManager.get(getActivity()).getUserId();
    mViewModel.readNotification(userId, mCurrentAppMode.getValue(), notification_id);
  }

  @Override
  public void onLoadMore() {
    if (mCurrentAppMode == null) {
      return;
    }
    int userId = SessionManager.get(getActivity()).getUserId();
    mViewModel.getNotificationListData(userId, mCurrentAppMode.getValue());
  }

  @Override
  public void onRefresh() {
    if (mCurrentAppMode == null) {
      return;
    }
    if (mNotificationAdapter.getItemCount() > 0) {
      Notifications notifications = mNotificationAdapter.getItem(0);
      int userId = SessionManager.get(getActivity()).getUserId();
      mViewModel.refreshNotifications(userId, mCurrentAppMode.getValue(),
          notifications.getCreated_date());
    } else {
      //TODO update call
    }
  }

  @Override
  public void showProgressBar(final boolean visibility) {
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (visibility) {
          mNotificationBinding.progressBarLayout.setVisibility(View.VISIBLE);
          //If it is first time then we hide and show the recycler view
          if (mViewModel.getCurrentIndex() == 0) {
            mNotificationBinding.notificationsList.setVisibility(View.GONE);
          }
        } else {
          mNotificationBinding.progressBarLayout.setVisibility(View.GONE);
          //If it is first time then we hide and show the recycler view
          mNotificationBinding.notificationsList.setVisibility(View.VISIBLE);
        }
      }
    });
  }

  @Override
  public void showLoadMoreView(boolean visibility) {
    if (visibility) {
      //Calling loadMore function in Runnable to fix the
      // java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling error
      mNotificationBinding.notificationsList.post(new Runnable() {
        @Override
        public void run() {
          mNotificationAdapter.addLoadMore();
        }
      });
    } else {
      mNotificationAdapter.removeLoadMore();
    }
  }

  @Override
  public void setMoreDataAvailable(boolean available) {
    mNotificationAdapter.setMoreDataAvailable(available);
  }

  @Override
  public void bindMessagesList(List<Notifications> messages) {
    mNotificationBinding.emptyViewLayout.setVisibility(View.GONE);
    mNotificationAdapter.addData(messages);
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        mNotificationAdapter.notifyDataChanged();
      }
    });
  }

  @Override
  public void onUpdateMessages(final List<Notifications> notificationsList) {
    if (notificationsList != null && notificationsList.size() > 0) {
      mNotificationAdapter.onUpdateDataSet(notificationsList);
      getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          mNotificationAdapter.notifyItemRangeInserted(0, notificationsList.size());
          mNotificationBinding.notificationsList.scrollToPosition(0);
        }
      });
      updateNotificationTitle(unreadCount + notificationsList.size());
    }

    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        mNotificationBinding.swipeRefresh.setRefreshing(false);
      }
    });
  }

  @Override
  public void bindEmptyView() {
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        mNotificationBinding.emptyViewLayout.setVisibility(View.VISIBLE);
      }
    });
  }

  @Override
  public void showToast(String message) {
    AppUtility.showToast(getActivity(), message);
  }

  @Override
  public void updateNotificationTitle(int count) {
    if (count > 0) {
      unreadCount = count;
    } else {
      unreadCount = 0;
    }
    if (count == 0) {
      mNotificationBinding.toolbar.textViewTitle.setText(R.string.title_notification_empty);
    } else {
      mNotificationBinding.toolbar.textViewTitle.setText(
          getString(R.string.title_notification, count));
    }
    if (mHomeUpdateListener != null) mHomeUpdateListener.updateNotification(count);
  }

  @Override
  public void onReadNotification(int notification_id) {
    boolean result = mNotificationAdapter.readNotification(notification_id);
    if (result) {
      updateNotificationTitle(unreadCount - 1);
    }
  }
}
