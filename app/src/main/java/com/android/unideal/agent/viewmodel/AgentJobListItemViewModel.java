package com.android.unideal.agent.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.android.unideal.R;
import com.android.unideal.data.JobDetail;
import com.android.unideal.util.DateTimeUtils;
import com.squareup.picasso.Picasso;

import java.util.Locale;
import java.util.List;


/**
 * Created by ADMIN on 26-09-2016.
 */

public class AgentJobListItemViewModel extends BaseObservable {
  public ObservableField<Integer> jobTypeColor = new ObservableField<>();
  private JobDetail jobDetail;
  private Context context;

  public AgentJobListItemViewModel(Context context, JobDetail jobDetail, int expertise) {
    this.context = context;
    this.jobDetail = jobDetail;
    if (jobDetail.getCategory_id() == expertise) {
      jobTypeColor.set(ContextCompat.getColor(context, R.color.colorSkillHighlight));
    } else {
      jobTypeColor.set(ContextCompat.getColor(context, R.color.colorSantasGray));
    }
   // loadThumb();
  }

  public void setJobDetail(JobDetail jobDetail) {
    this.jobDetail = jobDetail;
    notifyChange();
   // loadThumb();

  }

  public String getQuestion() {
    if (jobDetail != null && !TextUtils.isEmpty(jobDetail.getJob_title())) {
      return jobDetail.getJob_title();
    } else {
      return "";
    }
  }

  public String getJobPrice() {
    if (jobDetail != null) {
      String consignment = String.valueOf(jobDetail.getConsignment_size());

      return String.format(Locale.getDefault(), context.getString(R.string.agent_price_float),
          consignment);
    } else {
      return "0.0 HK$";
    }
  }

  public String getJobType() {
    if (jobDetail != null && !TextUtils.isEmpty(jobDetail.getCategory_name())) {
      return jobDetail.getCategory_name();
    } else {
      return "";
    }
  }

  public String getJobId() {
    if (jobDetail != null) {
      return "#" + jobDetail.getJob_id();
    } else {
      return "";
    }
  }

  public String getNoOfApplication() {
    if (jobDetail != null) {
      int postRest = jobDetail.getApplicant() > 1 ? R.string.applicants : R.string.applicant;
      return String.format(Locale.getDefault(), "%d " + context.getString(postRest),
          jobDetail.getApplicant());
    } else {
      return 0 + " " + context.getString(R.string.applicant);
    }
  }

  public String getClientName() {
    if (jobDetail != null) {
      return jobDetail.getUser_name();
    } else {
      return "";
    }
  }

  public String getEndDate() {
    if (jobDetail != null) {
      return DateTimeUtils.jobEndDateTime(context, jobDetail.getJob_end_on());
    } else {
      return "";
    }
  }

  public String getImageUrl() {
    if (jobDetail != null) {
        List<String> list = jobDetail.getFiles();
      if(list != null && list.size() > 0){
        String thumbURL = list.get(0);
        return thumbURL;
      }else return "";

    }else {
      return "";
    }
  }

}
