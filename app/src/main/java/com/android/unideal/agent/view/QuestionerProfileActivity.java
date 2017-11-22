package com.android.unideal.agent.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.View;
import com.android.unideal.R;
import com.android.unideal.agent.adapter.QuestionerCompleteJobAdapter;
import com.android.unideal.agent.viewmodel.QuestionerProfileViewModel;
import com.android.unideal.databinding.ProfileBinding;
import com.android.unideal.rest.response.ProfileResponse;
import com.squareup.picasso.Picasso;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by ADMIN on 24-10-2016.
 */

public class QuestionerProfileActivity extends AppCompatActivity
    implements QuestionerProfileViewModel.QuestionerProfileListener {
  private static final String ARG_USER_ID = "user_id";

  private ProfileBinding mBinding;
  private QuestionerProfileViewModel mViewModel;
  private QuestionerCompleteJobAdapter questionerCompleteJobAdapter;
  private String userId;

  public static Intent getInstance(Activity activity, String userId) {
    Intent intent = new Intent(activity, QuestionerProfileActivity.class);
    intent.putExtra(ARG_USER_ID, userId);
    return intent;
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mBinding =
        DataBindingUtil.setContentView(this, R.layout.activity_questioner_profile_from_agent);
    userId = getIntent().getStringExtra(ARG_USER_ID);
    mViewModel = new QuestionerProfileViewModel(this, this);
    mViewModel.getProfileDetail(userId);
    mBinding.imageViewBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
    createJobListView();
  }

  /**
   * This will create the recycler view bind the adapter and load the Job data
   */
  private void createJobListView() {
    questionerCompleteJobAdapter = new QuestionerCompleteJobAdapter(QuestionerProfileActivity.this);
    mBinding.JobList.setHasFixedSize(true);
    mBinding.JobList.setLayoutManager(new LinearLayoutManager(this));
    mBinding.JobList.setAdapter(questionerCompleteJobAdapter);
  }

  @Override
  public void loadData(ProfileResponse profileResponse) {
    if (!TextUtils.isEmpty(profileResponse.getResponse().getUserDoc())) {
      String fileName = profileResponse.getResponse()
          .getUserDoc()
          .substring(profileResponse.getResponse().getUserDoc().lastIndexOf('/') + 1,
              profileResponse.getResponse().getUserDoc().length());
      SpannableString content = new SpannableString(fileName);
      content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
    }
    mBinding.ratingBar.setRating(Float.parseFloat(profileResponse.getResponse().getReviews()));
    if (!TextUtils.isEmpty(profileResponse.getResponse().getName())) {
      mBinding.clientName.setText(profileResponse.getResponse().getName());
    }
    if (!TextUtils.isEmpty(profileResponse.getResponse().getProfilePic())) {
      Picasso.with(this).load(profileResponse.getResponse().getProfilePic())
          .into(mBinding.profileImage);
    }
    if (profileResponse.getUserJobList().size() == 0) {
      mBinding.completedJob.setText(getString(R.string.no_completed_jos));
    } else {
      questionerCompleteJobAdapter.addJobItem(profileResponse.getUserJobList());
    }

    mBinding.jobCount.setText("(" + profileResponse.getResponse().getCompletedJobs() + ")");
  }

  @Override
  public void showProgressBar() {
    mBinding.progressBarLayout.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideProgressBar() {
    mBinding.progressBarLayout.setVisibility(View.GONE);
  }
}
