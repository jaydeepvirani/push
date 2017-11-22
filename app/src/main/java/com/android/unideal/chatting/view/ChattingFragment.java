package com.android.unideal.chatting.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.unideal.ImageViewerActivity;
import com.android.unideal.R;
import com.android.unideal.chatting.adapter.ConversationAdapter;
import com.android.unideal.chatting.viewmodel.ChatPresenter;
import com.android.unideal.chatting.viewmodel.IChatView;
import com.android.unideal.data.Admin;
import com.android.unideal.data.Applicant;
import com.android.unideal.data.JobDetail;
import com.android.unideal.data.Status;
import com.android.unideal.data.UserDetail;
import com.android.unideal.data.socket.MediaData;
import com.android.unideal.data.socket.Message;
import com.android.unideal.data.socket.MessageStatus;
import com.android.unideal.databinding.ChattingWindowBinding;
import com.android.unideal.enums.AppMode;
import com.android.unideal.notification.NotificationHelper;
import com.android.unideal.notification.NotificationProvider;
import com.android.unideal.questioner.questionhelper.AwardJobDialogHelper;
import com.android.unideal.questioner.questionhelper.AwardJobModel;
import com.android.unideal.questioner.view.AgentProfileActivity;
import com.android.unideal.rtp.RunTimePermission;
import com.android.unideal.util.AppUtility;
import com.android.unideal.util.Communicator;
import com.android.unideal.util.DateTimeUtils;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.ImagePicker;
import com.android.unideal.util.ItemClickListener;
import com.android.unideal.util.OnScrollTopListener;
import com.android.unideal.util.RunTimePermissionManager;
import com.android.unideal.util.SessionManager;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import rx.Subscription;
import rx.functions.Action1;

import static com.android.unideal.questioner.view.AgentProfileActivity.APPLICANT;

/**
 * Created by bhavdip on 10/6/16.
 * Chatting / Message Fragment that handle all conversation between
 * Agent and Questioner
 */

public class ChattingFragment extends Fragment
    implements ImagePicker.ImageListener, RunTimePermissionManager.callbackRunTimePermission,
    IChatView, ItemClickListener<Message>, AwardJobModel.AwardListener {
  private static final String TAG = "ChattingFragment";
  private static final String KEY_MODE = "AppMode";
  private static final String KEY_JOB = "JobDetails";
  private static final String KAY_IS_OFFERED = "is_offered";
  private static final String APPLICANTS = "applicants";

  private final static int REQ_PROFILE_IMAGE = 191;
  private final static int REQ_AGENT_PROFILE = 111;

  // request code for runtime permission
  private final static int REQ_PERMISSION_FILE_UPLOAD = 235;
  private ChattingWindowBinding mWindowBinding;
  private ConversationAdapter mConversationAdapter;
  private JobDetail conversationJob;
  private ImagePicker mImagePicker;
  private AppMode mCurrentAppMode;
  private RunTimePermissionManager mRunTimePermission;
  private Applicant mApplicant;

  /**
   * This will hold the subscription reference
   * When fragment destroy using it un-subscribe the
   * event
   */
  private Subscription mSubscription;
  private ChatPresenter mChatPresenter;
  private int currentUser;
  private String threadId;
  private boolean isHasMore = true;
  private ImageViewTask imageViewTask;
  private AwardJobModel awardJobModel;
  private boolean isOffered;

  public static Fragment getFragment(AppMode appMode, JobDetail job, boolean isOffered) {
    ChattingFragment mFragment = new ChattingFragment();
    Bundle mBundle = new Bundle();
    mBundle.putString(KEY_MODE, appMode.name());
    mBundle.putParcelable(KEY_JOB, job);
    mBundle.putBoolean(KAY_IS_OFFERED, isOffered);
    mFragment.setArguments(mBundle);
    return mFragment;
  }

  /**
   *
   * @param appMode
   * @param job
   * @param applicant
   * @param isOffered
   * @return
   */
  public static Fragment getFragment(AppMode appMode, JobDetail job, Applicant applicant,
      boolean isOffered) {
    ChattingFragment mFragment = new ChattingFragment();
    Bundle mBundle = new Bundle();
    mBundle.putString(KEY_MODE, appMode.name());
    mBundle.putParcelable(KEY_JOB, job);
    mBundle.putParcelable(APPLICANTS, applicant);
    mBundle.putBoolean(KAY_IS_OFFERED, isOffered);
    mFragment.setArguments(mBundle);
    return mFragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    extractIntent();
    configureRunTimePermission();
    subscribePermissionChannel();
    configureImagePicker();
    clearNotifications();
  }

  private void clearNotifications() {
    NotificationProvider.get(getActivity()).removeNotification(threadId);
    NotificationHelper.clearMsgNotification(getActivity(), threadId);
  }

  private void configureRunTimePermission() {
    mRunTimePermission = new RunTimePermissionManager(getActivity());
    mRunTimePermission.registerCallback(this);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mWindowBinding = loadBinding(inflater, container);
    isOffered = getArguments().getBoolean(KAY_IS_OFFERED, false);

    bindViewDetails();

    mWindowBinding.offerTitle.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showAgentProfileWithOffers();
      }
    });

    mWindowBinding.imageViewAttachment.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        permissionRequestList();
      }
    });

    currentUser = SessionManager.get(getActivity()).getUserId();
    int userType = SessionManager.get(getActivity()).getUserMode();

    mChatPresenter = new ChatPresenter(this, threadId, currentUser, userType);
    awardJobModel = new AwardJobModel(this);

    mWindowBinding.btnAccept.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onAccept();
      }
    });
    mWindowBinding.btnHide.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onReject();
      }
    });

    return mWindowBinding.getRoot();
  }

  @SuppressLint("WrongConstant")
  private void bindViewDetails() {
    //If the job status is open then only
    if ((conversationJob.getStatusEnum() == Status.OPEN) && !isOffered && (mCurrentAppMode == AppMode.QUESTIONER)) {
      mWindowBinding.sectionViewDetails.setVisibility(View.VISIBLE);
    } else {
      mWindowBinding.sectionViewDetails.setVisibility(View.GONE);
    }
  }

  private void onAccept() {
    if (conversationJob == null) {
      Log.d(TAG, "onAccept: conversationJob is null");
      return;
    }

    AwardJobDialogHelper dialogHelper =
        new AwardJobDialogHelper(getActivity(), conversationJob.getConsignment_size());
    dialogHelper.setJobListener(new AwardJobDialogHelper.JobListener() {
      @Override
      public void onAppliedSuccess(float consignmentSize) {
        int userID = SessionManager.get(getActivity()).getUserId();

        if (consignmentSize == -1) {
          awardJobModel.acceptApplicant(userID, String.valueOf(conversationJob.getJob_id()),
              conversationJob.getMsg_user_id(), null, 0, 0);
        } else {
          awardJobModel.acceptApplicant(userID, String.valueOf(conversationJob.getJob_id()),
              conversationJob.getMsg_user_id(), null, 1, consignmentSize);
        }
      }
    });

    dialogHelper.showDialog();
  }

  private void onReject() {
    int userID = SessionManager.get(getActivity()).getUserId();
    int jobId = conversationJob.getJob_id();
    int applicantId = conversationJob.getMsg_user_id();
    awardJobModel.rejectApplicant(userID, String.valueOf(jobId), applicantId, null);
  }

  @Override
  public void onResume() {
    super.onResume();
    mChatPresenter.initSocket();
  }

  @Override
  public void onPause() {
    super.onPause();
    mChatPresenter.removeListener();
  }

  private ChattingWindowBinding loadBinding(LayoutInflater inflater, ViewGroup container) {
    return DataBindingUtil.inflate(inflater, R.layout.fragment_chatting, container, false);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    bindViews();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQ_AGENT_PROFILE) {
      if (resultCode == Activity.RESULT_OK) {
        Applicant applicant = data.getParcelableExtra(APPLICANT);
        //explicitly hide the offer view details
        applicant.setIs_offered(1);
        conversationJob.setJob_status(1);
        isOffered = true;
        this.mApplicant = applicant;
        bindViewDetails();
      }
    } else {
      mImagePicker.onActivityResult(requestCode, resultCode, data);
      mRunTimePermission.onActivityResult(REQ_PERMISSION_FILE_UPLOAD, requestCode, requestCode,
          data);
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    mImagePicker.onSaveInstanceState(outState);
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    mImagePicker.onRestoreInstanceState(savedInstanceState);
    super.onViewStateRestored(savedInstanceState);
  }

  @Override
  public void onDestroyView() {
    removeItemClick();
    mChatPresenter.removeListener();
    super.onDestroyView();
  }

  /**
   * Do not bind the User Interface in this method its call before
   * the onCreateView at that time the View is yet not return.
   */
  private void extractIntent() {
    if (getArguments().containsKey(KEY_MODE)) {
      mCurrentAppMode = AppMode.valueOf(getArguments().getString(KEY_MODE));
    }
    if (getArguments().containsKey(KEY_JOB)) {
      conversationJob = getArguments().getParcelable(KEY_JOB);
      if (conversationJob != null) {
        threadId = conversationJob.getMsg_thread_id();
      }
    }
    if (getArguments().containsKey(APPLICANTS)) {
      mApplicant = getArguments().getParcelable(APPLICANTS);
    }
  }

  /**
   * Handle the result of request for ask the permission
   */
  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    mRunTimePermission.onRequestPermissionsResult(REQ_PERMISSION_FILE_UPLOAD, permissions,
        grantResults);
  }

  /**
   * Start Listen the Run Time Permission Channel
   */
  private void subscribePermissionChannel() {
    mSubscription = Communicator.getCommunicator()
        .subscribeRunTimeChannel()
        .subscribe(new Action1<RunTimePermission>() {
          @Override
          public void call(RunTimePermission runTimePermission) {
            if (runTimePermission.isAllPermissionGranted()) {
              // here we can feel free to open the image choose
              // because we get the grand for Storage
              //1. Prompt the dialog for choose the image options
              showChooseImageOptions();
            }
          }
        });
  }

  private void configureImagePicker() {
    mImagePicker = new ImagePicker(this, this, false);
  }

  /**
   * It is call after returning the view.
   */
  private void bindViews() {
    mWindowBinding.jobTitle.setVisibility(View.VISIBLE);
    mWindowBinding.jobTitle.setText(getChatTitle());

    mWindowBinding.imageViewAttachment.setVisibility(View.VISIBLE);
    //change the color of the white image into the questioner color
    if (mCurrentAppMode == AppMode.AGENT) {
      AppUtility.loadSVGImage(mWindowBinding.imageViewAttachment, R.drawable.ic_attached_file,
          R.color.colorCuriousBlue);
      mWindowBinding.jobTitle.setBackgroundColor(
          ContextCompat.getColor(getContext(), R.color.colorCuriousBlue));
    }
    if (mCurrentAppMode == AppMode.QUESTIONER) {
      AppUtility.loadSVGImage(mWindowBinding.imageViewAttachment, R.drawable.ic_attached_file,
          R.color.colorPersianGreen);
      mWindowBinding.jobTitle.setBackgroundColor(
          ContextCompat.getColor(getContext(), R.color.colorPersianGreen));
    }

    RxView.clicks(mWindowBinding.imageViewAttachment).subscribe(new Action1<Void>() {
      @Override
      public void call(Void aVoid) {
        AppUtility.hideSoftKeyBoard(getActivity(), getActivity().getCurrentFocus());
        permissionRequestList();
      }
    });

    //if job in in progress mode show the accept and hide options
    Status status = conversationJob.getStatusEnum();
    if (status == Status.IN_PROCESS) {
      mWindowBinding.imageViewAttachment.setVisibility(View.VISIBLE);
    }
    if (status == Status.IN_PROCESS) {
      mWindowBinding.imageViewAttachment.setVisibility(View.VISIBLE);
    }
    if (status == Status.COMPLETED) {
      mWindowBinding.imageViewAttachment.setVisibility(View.VISIBLE);
    }
    if (status == Status.CANCELLED) {
      mWindowBinding.imageViewAttachment.setVisibility(View.VISIBLE);
    }
    bindRecyclerView();
    listenTypingMsg();
  }

  /**
   * Binding the Conversation Screen
   */
  private void bindRecyclerView() {
    UserDetail detail = SessionManager.get(getActivity()).getActiveUser();
    String currentUserImage = null;
    if (detail != null) {
      currentUserImage = detail.getProfilePic();
    }
    String receiverImage = conversationJob.getMsg_user_profile_url();
    Admin admin = SessionManager.get(getActivity()).getAdminDetail();
    String adminImage = null;
    if (admin != null) {
      adminImage = admin.getProfile_url();
    }
    mConversationAdapter =
        new ConversationAdapter(getActivity(), mCurrentAppMode, currentUser, currentUserImage,
            receiverImage, adminImage);

    mConversationAdapter.setListener(this);
    //mConversationAdapter.addItems(conversationJob.getMessagesList());
    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
    layoutManager.setStackFromEnd(true);
    RecyclerView.OnScrollListener onScrollListener = new OnScrollTopListener(layoutManager) {
      @Override
      public void onLoadMore(int current_page) {
        if (isHasMore) {
          mChatPresenter.getMessageHistory(current_page, conversationJob.getMsg_user_id());
        }
      }
    };
    mWindowBinding.conversationList.setLayoutManager(layoutManager);
    mWindowBinding.conversationList.setHasFixedSize(true);
    mWindowBinding.conversationList.setItemViewCacheSize(20);
    mWindowBinding.conversationList.setDrawingCacheEnabled(true);
    mWindowBinding.conversationList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    mWindowBinding.conversationList.setAdapter(mConversationAdapter);
    mWindowBinding.conversationList.addOnScrollListener(onScrollListener);
  }

  private void listenTypingMsg() {
    RxTextView.textChanges(mWindowBinding.editBoxMsg).subscribe(new Action1<CharSequence>() {
      @Override
      public void call(CharSequence charSequence) {
        if (charSequence.length() > 0) {
          mWindowBinding.textViewSend.setEnabled(true);
        } else {
          mWindowBinding.textViewSend.setAlpha(0.5f);
          mWindowBinding.textViewSend.setEnabled(false);
        }
      }
    });
    if (mCurrentAppMode == AppMode.AGENT) {
      mWindowBinding.textViewSend.setTextColor(
          ContextCompat.getColor(getContext(), R.color.colorCuriousBlue));
    } else {
      mWindowBinding.textViewSend.setTextColor(
          ContextCompat.getColor(getContext(), R.color.colorPersianGreen));
    }
    RxView.clicks(mWindowBinding.textViewSend).subscribe(new Action1<Void>() {
      @Override
      public void call(Void aVoid) {
        String nwMsg = mWindowBinding.editBoxMsg.getText().toString().trim();
        mWindowBinding.editBoxMsg.getText().clear();
        Message message = buildTextMsg(nwMsg);
        mChatPresenter.sendMessage(message);
      }
    });
    if (mCurrentAppMode == AppMode.AGENT) {
      if (conversationJob.getStatusEnum() == Status.APPLIED
          || conversationJob.getStatusEnum() == Status.OPEN) {
        mWindowBinding.imageViewAttachment.setVisibility(View.GONE);
        mWindowBinding.editBoxMsg.setEnabled(false);
      } else {
        mWindowBinding.imageViewAttachment.setVisibility(View.VISIBLE);
        mWindowBinding.editBoxMsg.setEnabled(true);
      }
    }
  }

  private void appendMessage(Message message) {
    if (getActivity() == null) {
      Log.d(TAG, "appendMessage: getActivity() is null ");
      return;
    }
    final Message localMessage1 = message;
    mWindowBinding.conversationList.post(new Runnable() {
      @Override
      public void run() {
        mConversationAdapter.appendMessage(localMessage1);
        mWindowBinding.conversationList.scrollToPosition(mConversationAdapter.getItemCount() - 1);
        enableDisableChatBoxForAgent(mConversationAdapter.getItems().size());
      }
    });
  }

  private void appendMessageTop(final List<Message> messageList) {
    mWindowBinding.conversationList.post(new Runnable() {
      @Override
      public void run() {
        mConversationAdapter.addItemsToTop(messageList);
        if (messageList.size() > 0) {
          enableDisableChatBoxForAgent(messageList.size());
        }
      }
    });
  }

  private void enableDisableChatBoxForAgent(int totalMessages) {
    if (mCurrentAppMode == AppMode.AGENT) {
      if (conversationJob.getStatusEnum() == Status.APPLIED
          || conversationJob.getStatusEnum() == Status.OPEN) {
        if (totalMessages > 1) {
          mWindowBinding.editBoxMsg.setEnabled(true);
        } else {
          mWindowBinding.editBoxMsg.setEnabled(false);
        }
      }
    }
  }

  private Message buildTextMsg(String text) {
    MediaData data = new MediaData();
    data.setText(text);
    Message message = buildMessageObj(data);
    message.setMessage_type(Message.TYPE_TEXT);
    return message;
  }

  private Message buildImageMsg(MediaData mediaData) {
    Message message = buildMessageObj(mediaData);
    message.setMessage_type(Message.TYPE_IMAGE);
    return message;
  }

  private Message buildMessageObj(MediaData mediaData) {
    Message message = new Message();
    message.setSender_id(currentUser);
    message.setReceiver_id(conversationJob.getMsg_user_id());
    message.setMediaData(mediaData);
    message.setJob_id(conversationJob.getJob_id());
    message.setUser_type(mCurrentAppMode.getValue());
    message.setSend_time(DateTimeUtils.getCurrentTime());
    return message;
  }

  /**
   * Start the Image choose options activity using CropImage library
   */
  private void showChooseImageOptions() {
    mImagePicker.openDialog(REQ_PROFILE_IMAGE);
  }

  private String getChatTitle() {
    return String.format("%s - Job #%s", conversationJob.getMsg_user_name(),
        conversationJob.getJob_id());
  }

  @Override
  public void onImagePick(int reqCode, String path) {
    if (reqCode == REQ_PROFILE_IMAGE) {
      mChatPresenter.uploadAttachment(path);
    }
  }

  @Override
  public void onImageUploaded(MediaData mediaData) {
    String nwMsg = mWindowBinding.editBoxMsg.getText().toString().trim();
    mWindowBinding.editBoxMsg.getText().clear();
    mediaData.setText(nwMsg);
    Message message = buildImageMsg(mediaData);
    mChatPresenter.sendMessage(message);
  }

  @Override
  public void showProgressBar(boolean isCancelable) {
    DialogUtils.getInstance().showProgressDialog(getActivity());
  }

  @Override
  public void onStatusReceived(final MessageStatus status) {
    mWindowBinding.conversationList.post(new Runnable() {
      @Override
      public void run() {
        if (status != null && status.getSender_id() == currentUser) {
          mConversationAdapter.updateStatus(status);
        }
      }
    });
  }

  @Override
  public void hideProgressBar() {
    DialogUtils.getInstance().hideProgressDialog();
  }

  @Override
  public void onAllMessageRead() {
    mWindowBinding.conversationList.post(new Runnable() {
      @Override
      public void run() {
        mConversationAdapter.updateToAllRead();
      }
    });
  }

  @Override
  public void onAllMessageDelivered() {
    mWindowBinding.conversationList.post(new Runnable() {
      @Override
      public void run() {
        mConversationAdapter.updateToAllReceived();
      }
    });
  }

  @Override
  public void onError(String s) {
    showToast(s);
  }

  /**
   * Must call it when we clean up the resource.
   */
  private void removeItemClick() {
    if (mSubscription != null) {
      mSubscription.unsubscribe();
    }
  }

  /**
   * This will directly call to the activity function
   */
  private void permissionRequestList() {
    //QuestionerJobDetailsActivity detailsActivity = (QuestionerJobDetailsActivity) getActivity();
    //detailsActivity.permissionRequestList();
    HashMap<String, String> permissionsMap = new HashMap<>();
    permissionsMap.put(Manifest.permission.WRITE_EXTERNAL_STORAGE,
        getResources().getString(R.string.message_storage_permission));
    mRunTimePermission.buildPermissionList(permissionsMap, REQ_PERMISSION_FILE_UPLOAD);
  }

  @Override
  public void showRationalDialog(String message, final int code) {
    DialogUtils.showDialog(getContext(), R.string.title_app_name, message, R.string.btn_ok,
        R.string.btn_cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            mRunTimePermission.askRunTimePermissions(code);
          }
        });
  }

  @Override
  public void deniedPermission(String deniedPermission, int code) {
  }

  @Override
  public void requestAllPermissionGranted(int requestCode) {
    mImagePicker.openDialog(REQ_PROFILE_IMAGE);
  }

  @Override
  public void onConnected() {
  }

  @Override
  public void onAuthSuccess() {
    if (mConversationAdapter.getItemCount() == 0) {
      mChatPresenter.getMessageHistory(0, conversationJob.getMsg_user_id());
    }
  }

  @Override
  public void onMessageReceive(Message message) {
    appendMessage(message);
  }

  @Override
  public void onMessageReceive(List<Message> messageList, boolean isHasMore) {
    appendMessageTop(messageList);
    this.isHasMore = isHasMore;
  }

  @Override
  public void showProgressDialog() {
    DialogUtils.getInstance().showProgressDialog(getActivity());
  }

  @Override
  public void hideProgressDialog() {
    DialogUtils.getInstance().hideProgressDialog();
  }

  @Override
  public void showToast(String message) {
    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onItemClick(Message message, int position) {
    if (message.getMessage_type() == Message.TYPE_IMAGE) {
      if (imageViewTask == null) {
        imageViewTask = new ImageViewTask(mConversationAdapter.getItems(), position) {
          @Override
          protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            imageViewTask = null;
            if (strings.size() > 0) {
              Intent intent =
                  ImageViewerActivity.getActivityIntent(getActivity(), selectedPosition, strings,
                      mCurrentAppMode);
              startActivity(intent);
            }
          }
        };
        imageViewTask.execute();
      }
    }
  }

  @Override
  public void onAcceptSuccess(Applicant applicant) {
    //mWindowBinding.sectionAcceptDecline.setVisibility(View.GONE);
    getActivity().setResult(Activity.RESULT_OK);
  }

  @Override
  public void onRejectSuccess(Applicant applicant) {
    getActivity().setResult(Activity.RESULT_CANCELED);
    getActivity().finish();
  }

  private void showAgentProfileWithOffers() {
    //Agent message userId
    int agentUserId = conversationJob.getMsg_user_id();
    int jobId = conversationJob.getJob_id();
    float consignmentSize = conversationJob.getConsignment_size();
    float offeredPrice = conversationJob.getOffered_price();
    String deliveryPlace = conversationJob.getDeliveryplace();
    Intent intent =
        AgentProfileActivity.getInstance(getActivity(), 1, mApplicant, String.valueOf(agentUserId),
            jobId, consignmentSize, offeredPrice, deliveryPlace);
    startActivityForResult(intent, REQ_AGENT_PROFILE);
  }

  private class ImageViewTask extends AsyncTask<Void, Void, ArrayList<String>> {
    int selectedPosition;
    private List<Message> messageList;
    private int position;

    public ImageViewTask(List<Message> messageList, int position) {
      this.messageList = messageList;
      this.position = position;
    }

    @Override
    protected ArrayList<String> doInBackground(Void... params) {
      ArrayList<String> imageList = new ArrayList<>();

      int size = messageList.size();
      selectedPosition = 0;
      int i = 0;
      for (int j = 0; j < size; j++) {
        Message message = messageList.get(j);
        if (message.getMessage_type() == Message.TYPE_IMAGE) {
          MediaData data = message.getMediaData();
          if (data != null && !TextUtils.isEmpty(data.getImageUrl())) {
            imageList.add(data.getImageUrl());
            if (j == position) {
              selectedPosition = i;
            }
            i++;
          }
        }
      }
      return imageList;
    }
  }
}
