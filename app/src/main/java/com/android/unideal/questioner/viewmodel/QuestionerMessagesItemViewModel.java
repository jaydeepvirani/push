package com.android.unideal.questioner.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import com.android.unideal.R;
import com.android.unideal.data.Status;
import com.android.unideal.data.socket.Messages;

/**
 * Created by ADMIN on 12-10-2016.
 */

public class QuestionerMessagesItemViewModel {
  public ObservableField<String> jobId = new ObservableField<>();
  public ObservableField<Drawable> jobType = new ObservableField<>();
  public ObservableField<String> agentProfilePic = new ObservableField<>();
  public ObservableField<String> agentName = new ObservableField<>();
  public ObservableField<String> shortMessage = new ObservableField<>();
  public ObservableField<Integer> isTextMsg = new ObservableField<>(View.GONE);
  public ObservableField<Integer> isAttachment = new ObservableField<>(View.GONE);
  private Context context;
  private Messages mMessages;

  public QuestionerMessagesItemViewModel(Context context, Messages messages) {
    this.context = context;
    this.mMessages = messages;
    startBindingViews();
  }

  private void startBindingViews() {
    jobId.set(String.format("# %1d", mMessages.getJobId()));
    jobType.set(getJobStatusIcon());
    agentProfilePic.set(this.mMessages.getAgentProfilePicture());
    if (mMessages.getTotalUnread() > 0) {
      agentName.set(this.mMessages.getAgentName() + "(" + mMessages.getTotalUnread() + ")");
    } else {
      agentName.set(this.mMessages.getAgentName());
    }
    if (mMessages.getMediaData() != null && !TextUtils.isEmpty(
        mMessages.getMediaData().getText())) {
      shortMessage.set(this.mMessages.getMediaData().getText());
    } else {
      shortMessage.set("");
    }
    if (mMessages.getMessageType() == 2) {
      isTextMsg.set(View.GONE);
      isAttachment.set(View.VISIBLE);
    } else {
      isAttachment.set(View.GONE);
      isTextMsg.set(View.VISIBLE);
    }
  }

  public void setMessages(Messages messages) {
    this.mMessages = messages;
  }

  private Drawable getJobStatusIcon() {
    if (this.mMessages != null) {
      Status jobType = mMessages.getStatusEnum();
      if (jobType == Status.OPEN) {
        return ContextCompat.getDrawable(context, R.drawable.ic_open_job_questioner);
      } else if (jobType == Status.CANCELLED) {
        return ContextCompat.getDrawable(context, R.drawable.ic_cancelled_questioner);
      } else if (jobType == Status.PAUSED) {
        return ContextCompat.getDrawable(context, R.drawable.ic_paused_questioner);
      } else if (jobType == Status.IN_PROCESS) {
        return ContextCompat.getDrawable(context, R.drawable.ic_inprogress_questioner);
      } else if (jobType == Status.COMPLETED) {
        return ContextCompat.getDrawable(context, R.drawable.ic_completed_questioner);
      } else {
        return ContextCompat.getDrawable(context, R.drawable.ic_open_job_questioner);
      }
    } else {
      return ContextCompat.getDrawable(context, R.drawable.ic_open_job_questioner);
    }
  }
}
