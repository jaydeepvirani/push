package com.android.unideal.agent.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;
import android.support.v4.content.ContextCompat;
import com.android.unideal.R;
import com.android.unideal.data.AgtProfileDetailJobResponse;
import java.util.Locale;

/**
 * Created by ADMIN on 15-11-2016.
 */

public class QuestionerCompletedJobItemViewModel {
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
  private AgtProfileDetailJobResponse mJobData;
  private Context mContext;

  public QuestionerCompletedJobItemViewModel(Context context, AgtProfileDetailJobResponse jobData) {
    this.mJobData = jobData;
    this.mContext = context;
    refreshItemData();
  }

  public void setJobData(AgtProfileDetailJobResponse updateJobData) {
    this.mJobData = updateJobData;
    refreshItemData();
  }

  private void refreshItemData() {
    //TODO currently the skill color is hard cord here
    if (mJobData.getJoCategory() == 1
        || mJobData.getJoCategory() == 2
        || mJobData.getJoCategory() == 3) {
      jobTypeColor.set(ContextCompat.getColor(mContext, R.color.colorSkillHighlight));
    } else {
      jobTypeColor.set(ContextCompat.getColor(mContext, R.color.colorSantasGray));
    }

    jobTypeField.set(String.valueOf(mJobData.getJoCategory()));
    jobIdField.set(String.format("#%s", mJobData.getJobId()));
    if (mJobData.getJobStatus() == 2) {
      jobTotalApplicants.set(
          String.format(Locale.getDefault(), "%d Messages", mJobData.getJobTotalApplicants()));
    } else {
      jobTotalApplicants.set(
          String.format(Locale.getDefault(), "%d Applicant", mJobData.getJobTotalApplicants()));
    }
    jobClientName.set(mJobData.getClientName());
    jobTitle.set(mJobData.getJobTitle());
    jobPrice.set(String.format(Locale.getDefault(), "%d HKS", mJobData.getConsignmentSize()));
    jobEndDate.set(mJobData.getJobEndDate());
    jobRatingBar.set(mJobData.getJobRatingBar());

    if (mJobData.getJobStatus() == 2) {
      if (mJobData.isRead()) {
        jobMessageColor.set(ContextCompat.getColor(mContext, R.color.colorShark));
      } else {
        jobMessageColor.set(ContextCompat.getColor(mContext, R.color.colorJade));
      }
    }
  }
}
