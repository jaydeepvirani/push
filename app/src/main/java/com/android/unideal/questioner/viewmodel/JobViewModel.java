package com.android.unideal.questioner.viewmodel;

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
 * Created by bhavdip on 10/14/16.
 */

public class JobViewModel {

  public ObservableField<String> jobTypeField = new ObservableField<>();
  public ObservableField<String> jobIdField = new ObservableField<>();
  public ObservableField<String> jobTotalApplicants = new ObservableField<>();
  public ObservableField<String> totalMessage = new ObservableField<>();
  public ObservableField<String> jobClientName = new ObservableField<>();
  public ObservableField<String> jobTitle = new ObservableField<>();
  public ObservableField<String> jobPrice = new ObservableField<>();
  public ObservableField<String> jobEndDate = new ObservableField<>();
  public ObservableField<Float> jobRatingBar = new ObservableField<>();
  public ObservableField<Integer> ribbonVisibility = new ObservableField<>(View.GONE);
  public ObservableField<Integer> ribbonColor = new ObservableField<>(R.color.colorCuriousBlue);
  public ObservableField<String> ribbonText = new ObservableField<>();
  public ObservableField<String> imageUrl = new ObservableField<>();

  public Context mContext;
  private JobDetail mJobData;

  public JobViewModel(JobDetail jobData, Context context) {
    this.mJobData = jobData;
    this.mContext = context;
    refreshItemData();
  }

  public void setJobData(JobDetail updateJobData) {
    this.mJobData = updateJobData;
    refreshItemData();
  }

  private void refreshItemData() {
    jobTypeField.set(mJobData.getCategory_name());
    jobIdField.set(String.format("#%s", mJobData.getJob_id()));
    int postRest = mJobData.getApplicant() > 1 ? R.string.applicants : R.string.applicant;
    jobTotalApplicants.set(String.format(Locale.getDefault(), "%d " + mContext.getString(postRest),
        mJobData.getApplicant()));
    if (TextUtils.isEmpty(mJobData.getUser_name())) {
      jobClientName.set(mContext.getString(R.string.text_user_name_me));
    } else {
      jobClientName.set(mJobData.getUser_name());
    }
    jobTitle.set(mJobData.getJob_title());
    String consignment = String.valueOf(mJobData.getConsignment_size());
    jobPrice.set(String.format(Locale.getDefault(), mContext.getString(R.string.agent_price_float),
        consignment));
    Status status = mJobData.getStatusEnum();
    if (status == Status.CANCELLED) {
      jobEndDate.set(DateTimeUtils.jobEndDateTime(mContext, mJobData.getCancelled_on()));
    } else {
      jobEndDate.set(DateTimeUtils.jobEndDateTime(mContext, mJobData.getJob_end_on()));
    }
    totalMessage.set(String.format(Locale.getDefault(), "%d Messages", mJobData.getMessages()));
    jobRatingBar.set(mJobData.getReviews());

    if (mJobData.getIs_questioner_confirm() == 1 && mJobData.getIs_agent_confirm() == 0) {
      ribbonVisibility.set(View.VISIBLE);
      ribbonColor.set(ContextCompat.getColor(mContext, R.color.colorYellow));
      ribbonText.set(mContext.getString(R.string.text_waiting_confirmation));
    } else if (mJobData.getIs_questioner_confirm() == 0 && mJobData.getIs_agent_confirm() == 1) {
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
