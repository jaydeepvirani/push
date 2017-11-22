package com.android.unideal.agent.viewmodel;

import android.util.Log;
import com.android.unideal.data.Notifications;
import com.android.unideal.rest.CallbackWrapper;
import com.android.unideal.rest.RestClient;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.response.ErrorResponse;
import com.android.unideal.rest.response.GenericResponse;
import com.android.unideal.rest.response.NotificationResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by CS02 on 12/22/2016.
 */

public class NotificationViewModel {
  private static final String TAG = "NotificationViewModel";
  private NotificationListener mListener;
  private int mPageIndex = 0;

  public NotificationViewModel() {
  }

  private void hideProgressBar() {
    if (mPageIndex > 0) {
      mListener.showLoadMoreView(false);
    } else {
      mListener.showProgressBar(false);
    }
  }

  private void showProgressBar() {
    if (mPageIndex > 0) {
      mListener.showLoadMoreView(true);
    } else {
      mListener.showProgressBar(true);
    }
  }

  public void getNotificationListData(int userId, int userType) {
    showProgressBar();
    Map<String, Object> hashMap = new HashMap<>();
    hashMap.put(RestFields.KEY_USER_ID, String.valueOf(userId));
    hashMap.put(RestFields.KEY_USER_TYPE, userType);
    hashMap.put(RestFields.KEY_PAGE_INDEX, mPageIndex);
    Call<NotificationResponse> call = RestClient.get().getNotificationList(hashMap);
    call.enqueue(new Callback<NotificationResponse>() {
      @Override
      public void onResponse(Call<NotificationResponse> call,
          Response<NotificationResponse> response) {
        hideProgressBar();
        if (response.isSuccessful()) {
          NotificationResponse notiRes = response.body();
          if (notiRes != null) {
            if (notiRes.getSuccess() == 1) {
              onNotificationListReceived(notiRes);
            } else {
              showError(notiRes.getMessage());
            }
          } else {
            showError();
          }
        } else {
          showError();
        }
      }

      @Override
      public void onFailure(Call<NotificationResponse> call, Throwable t) {
        hideProgressBar();
        showError(t.getMessage());
      }
    });
  }

  private void showError(String message) {
    Log.d(TAG, "showError: ");
    if (mListener != null) {
      mListener.showToast(message);
    }
  }

  private void showError() {
    showError("Please try again");
  }

  public void refreshNotifications(int userId, int userType, String lastUpdateDate) {
    Map<String, Object> hashMap = new HashMap<>();
    hashMap.put(RestFields.KEY_USER_ID, String.valueOf(userId));
    hashMap.put(RestFields.KEY_USER_TYPE, userType);
    hashMap.put(RestFields.KEY_LAST_UPDATE_DATE, lastUpdateDate);
    Call<GenericResponse<List<Notifications>>> call =
        RestClient.get().getUpdateNotifications(hashMap);
    call.enqueue(new CallbackWrapper<List<Notifications>>() {
      @Override
      public void onSuccess(GenericResponse<List<Notifications>> response) {
        onUpdateNotifications(response.getData());
      }

      @Override
      public void onFailure(GenericResponse<List<Notifications>> response) {
        onUpdateNotifications(null);
      }

      @Override
      public void exception(ErrorResponse errorResponse) {
        onUpdateNotifications(null);
      }
    });
  }

  public void readNotification(int userId, int userType, final int notification_id) {
    Map<String, Object> hashMap = new HashMap<>();
    hashMap.put(RestFields.KEY_USER_ID, String.valueOf(userId));
    hashMap.put(RestFields.KEY_USER_TYPE, userType);
    hashMap.put(RestFields.KEY_IS_READ, RestFields.READ);
    hashMap.put(RestFields.KEY_NOTIFICATION_ID, notification_id);
    Call<GenericResponse> call = RestClient.get().updateNotificationState(hashMap);
    call.enqueue(new Callback<GenericResponse>() {
      @Override
      public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
        if (response.isSuccessful()) {
          if (response.body().isSuccess() == 1) {
            if (response.body().getStatus_code() == RestFields.STATUS_SUCCESS) {
              mListener.onReadNotification(notification_id);
            } else {
              Log.d(TAG, "onFailure: ");
            }
          } else {
            Log.d(TAG, "onFailure: ");
          }
        } else {
          Log.d(TAG, "onFailure: ");
        }
      }

      @Override
      public void onFailure(Call<GenericResponse> call, Throwable t) {
        Log.e(TAG, "onFailure: ", t.fillInStackTrace());
      }
    });
  }

  public int getCurrentIndex() {
    return mPageIndex;
  }

  public void onNotificationListReceived(NotificationResponse notificationResponse) {
    //once we add result message data into the adapter
    //then check the has more or not for load more enable disable
    if (notificationResponse.isHas_more()) {
      mListener.setMoreDataAvailable(true);
    } else {
      mListener.setMoreDataAvailable(false);
    }
    //we would allow to increase the page index
    mPageIndex++;
    List<Notifications> messagesList = notificationResponse.getData();
    if (messagesList != null && messagesList.size() > 0) {
      this.mListener.bindMessagesList(messagesList);
    } else {
      this.mListener.bindEmptyView();
    }
    mListener.updateNotificationTitle(notificationResponse.getTotal_unread());
  }

  public void onUpdateNotifications(List<Notifications> list) {
    if (list != null && list.size() > 0) {
      mListener.onUpdateMessages(list);
    } else {
      mListener.onUpdateMessages(null);
    }
  }

  public void setListener(NotificationListener listener) {
    this.mListener = listener;
  }

  public interface NotificationListener {
    void showProgressBar(boolean visibility);

    void showLoadMoreView(boolean visibility);

    void setMoreDataAvailable(boolean available);

    void bindMessagesList(List<Notifications> notificationsList);

    void onUpdateMessages(List<Notifications> notificationsList);

    void bindEmptyView();

    void showToast(String message);

    void updateNotificationTitle(int count);

    void onReadNotification(int notification_id);
  }
}
