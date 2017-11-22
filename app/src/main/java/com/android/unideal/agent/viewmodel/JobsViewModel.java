package com.android.unideal.agent.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import com.android.unideal.R;
import com.android.unideal.data.JobDetail;
import com.android.unideal.data.Status;
import com.android.unideal.util.DateTimeUtils;

import java.util.List;
import java.util.Locale;

/**
 * Created by bhavdip on 10/2/16.
 */

public class JobsViewModel {
  public ObservableField<String> jobTypeField = new ObservableField<>();
  public ObservableField<Integer> jobTypeColor = new ObservableField<>();
  public ObservableField<String> jobIdField = new ObservableField<>();
  public ObservableField<String> jobTotalApplicants = new ObservableField<>();
  public ObservableField<String> jobClientName = new ObservableField<>();
  public ObservableField<String> jobTitle = new ObservableField<>();
  public ObservableField<String> jobPrice = new ObservableField<>();
  public ObservableField<String> jobEndDate = new ObservableField<>();
  public ObservableField<Float> jobRatingBar = new ObservableField<>();
  public ObservableField<Integer> jobMessageColor = new ObservableField<>();
  public ObservableField<Integer> ribbonVisibility = new ObservableField<>(View.GONE);
  public ObservableField<Integer> ribbonColor = new ObservableField<>(R.color.colorCuriousBlue);
  public ObservableField<String> ribbonText = new ObservableField<>();
  public ObservableField<String> imageUrl = new ObservableField<>();
  private JobDetail mJobData;
  private Context mContext;
  private int expertise;
  private boolean isShownConsignMentSize;

  public JobsViewModel(Context context, JobDetail jobData, int expertise,
      boolean isConsignmentSize) {
    this.mJobData = jobData;
    this.mContext = context;
    this.expertise = expertise;
    this.isShownConsignMentSize = isConsignmentSize;
    refreshItemData();
  }

  public void setJobData(JobDetail updateJobData) {
    this.mJobData = updateJobData;
    refreshItemData();
  }

  private void refreshItemData() {
    //TODO currently the skill color is hard cord here
    if (mJobData.getCategory_id() == expertise) {
      jobTypeColor.set(ContextCompat.getColor(mContext, R.color.colorSkillHighlight));
    } else {
      jobTypeColor.set(ContextCompat.getColor(mContext, R.color.colorSantasGray));
    }

    jobTypeField.set(mJobData.getCategory_name());
    jobIdField.set(String.format("#%s", mJobData.getJob_id()));
    if (mJobData.getStatusEnum() == Status.IN_PROCESS) {
      jobTotalApplicants.set(
          String.format(Locale.getDefault(), "%d Messages", mJobData.getMessages()));
      //if (mJobData.getMessagesList()
      //    .get(AgentJobListProvider.randInt(0, mJobData.getMessagesList().size() - 1))
      //    .isRead()) {
      //  jobMessageColor.set(ContextCompat.getColor(mContext, R.color.colorShark));
      //} else {
      //  jobMessageColor.set(ContextCompat.getColor(mContext, R.color.colorJade));
      //}
      jobMessageColor.set(ContextCompat.getColor(mContext, R.color.colorShark));
    } else {
      int postRest = mJobData.getApplicant() > 1 ? R.string.applicants : R.string.applicant;
      jobTotalApplicants.set(
          String.format(Locale.getDefault(), "%d " + mContext.getString(postRest),
              mJobData.getApplicant()));
    }
    if (TextUtils.isEmpty(mJobData.getUser_name())) {
      jobClientName.set(mContext.getString(R.string.text_user_name_me));
    } else {
      jobClientName.set(mJobData.getUser_name());
    }
    jobTitle.set(mJobData.getJob_title());
    // In agent case we need to show agent fee
    String consignment = String.valueOf(mJobData.getConsignment_size());
    if (isShownConsignMentSize) {
      jobPrice.set(
          String.format(Locale.getDefault(), mContext.getString(R.string.agent_price_float),
              consignment));
    } else {
      jobPrice.set(
          String.format(Locale.getDefault(), mContext.getString(R.string.agent_price_float),
              String.valueOf(mJobData.getAgent_commision())));
    }

    jobEndDate.set(DateTimeUtils.jobEndDateTime(mContext, mJobData.getJob_end_on()));
    jobRatingBar.set(mJobData.getReviews());

    if (mJobData.getIs_questioner_confirm() == 0 && mJobData.getIs_agent_confirm() == 1) {
      ribbonVisibility.set(View.VISIBLE);
      ribbonColor.set(ContextCompat.getColor(mContext, R.color.colorYellow));
      ribbonText.set(mContext.getString(R.string.text_waiting_confirmation));
    } else if (mJobData.getIs_questioner_confirm() == 1 && mJobData.getIs_agent_confirm() == 0) {
      ribbonVisibility.set(View.VISIBLE);
      ribbonColor.set(ContextCompat.getColor(mContext, R.color.colorRed));
      ribbonText.set(mContext.getString(R.string.text_confirmation_required));
    } else {
      ribbonVisibility.set(View.GONE);
    }

    if (mJobData != null) {
      List<String> list = mJobData.getFiles();
      if(list != null && list.size() > 0){
        String thumbURL = list.get(0);
        imageUrl.set(thumbURL);
      }

    }

  }
}
