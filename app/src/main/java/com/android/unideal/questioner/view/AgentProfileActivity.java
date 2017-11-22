package com.android.unideal.questioner.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import com.android.unideal.R;
import com.android.unideal.data.Applicant;
import com.android.unideal.databinding.AgentProfileQuestionerBinding;
import com.android.unideal.enums.AppMode;
import com.android.unideal.questioner.adapter.ProfileJobAdapter;
import com.android.unideal.questioner.questionhelper.AwardJobDialogHelper;
import com.android.unideal.questioner.questionhelper.AwardJobModel;
import com.android.unideal.questioner.view.payment.ManageCardActivity;
import com.android.unideal.questioner.viewmodel.AgentProfileActivityViewModel;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.response.Category;
import com.android.unideal.rest.response.ProfileResponse;
import com.android.unideal.util.AppUtility;
import com.android.unideal.util.CategoryList;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.SessionManager;
import com.jakewharton.rxbinding.view.RxView;
import com.squareup.picasso.Picasso;
import rx.functions.Action1;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by ADMIN on 18-10-2016.
 */

public class AgentProfileActivity extends AppCompatActivity
    implements AgentProfileActivityViewModel.AgentProfileQuestionerListener,
    AwardJobModel.AwardListener {

  public static final String APPLICANT = "applicant";
  private static final String ARG_OPEN_MODE = "open_mode";
  private static final String ARG_USER_ID = "user_id";
  private static final String JOB_ID = "job_id";
  private static final String CONSIGNMENT_SIZE = "consignment_size";
  private static final String OFFER_PRICE = "offer_price";
  private static final String DELIVERY_PLACE = "delivery_place";
  private static final String TAG = "AgentProfileActivity";
  public static String JOB_STATUS = "job_status";
  private AgentProfileQuestionerBinding mBinding;
  private AgentProfileActivityViewModel mViewModel;
  private ProfileJobAdapter mProfileJobAdapter;
  private String argUserId;
  private int jobId;
  private Applicant applicant;
  private AwardJobModel awardJobModel;
  private float consignmentSize;
  //agent offered price;
  private float offeredPrice;

  /**
   * @param calledFrom 0 - default applicants list mode 1- Open from messages section.
   * @param applicant May be null if the called flag from 1
   */
  public static Intent getInstance(Activity activity, int calledFrom, Applicant applicant,
      String userId, int jobId, float consignmentSize, float offeredPrice, String deliveryPlace) {
    Intent intent = new Intent(activity, AgentProfileActivity.class);
    // help to detect the mode
    intent.putExtra(ARG_OPEN_MODE, calledFrom);
    intent.putExtra(ARG_USER_ID, userId);
    intent.putExtra(JOB_ID, jobId);
    intent.putExtra(APPLICANT, applicant);
    intent.putExtra(CONSIGNMENT_SIZE, consignmentSize);
    intent.putExtra(OFFER_PRICE, offeredPrice);
    intent.putExtra(DELIVERY_PLACE, deliveryPlace);
    return intent;
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mBinding = loadBinding();
    mViewModel = new AgentProfileActivityViewModel(this);
    mViewModel.onCreateActivity();
  }

  private AgentProfileQuestionerBinding loadBinding() {
    return DataBindingUtil.setContentView(this, R.layout.activity_agent_profile_from_questioner);
  }

  private void extractData(Intent intent) {
    argUserId = intent.getStringExtra(ARG_USER_ID);
    jobId = intent.getIntExtra(JOB_ID, -1);
    applicant = intent.getParcelableExtra(APPLICANT);
    consignmentSize = intent.getFloatExtra(CONSIGNMENT_SIZE, -1);
    offeredPrice = intent.getFloatExtra(OFFER_PRICE, -1f);
  }

  @Override
  public void initialization() {
    extractData(getIntent());

    //offer layer
    mBinding.offerPriceLayer.setVisibility(offeredPrice != -1 ? View.VISIBLE : View.GONE);
    mBinding.textViewOfferPrice.setText(getString(R.string.agent_price, (int) offeredPrice));

    mBinding.postLayout.setVisibility(applicant.getIs_offered() == 1 ? View.GONE : View.VISIBLE);
    //delivery method
    if (applicant != null) {
      mBinding.deliveryMethodLayer.setVisibility(View.VISIBLE);
      if (!TextUtils.isEmpty(applicant.getDelivery_place())) {
        mBinding.textViewMethod.setText(applicant.getDelivery_place());
      } else {
        mBinding.textViewMethod.setText(
            getApplication().getString(R.string.title_unideal_delivery_place));
        mBinding.textViewMethod.setPaintFlags(
            mBinding.textViewMethod.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
      }
    } else {
      mBinding.deliveryMethodLayer.setVisibility(View.GONE);
    }

    RxView.clicks(mBinding.textViewMethod).subscribe(new Action1<Void>() {
      @Override
      public void call(Void aVoid) {
        UniDealDeliveryPlaceActivity.startDeliveryPlaceActivity(AgentProfileActivity.this,
            AppMode.QUESTIONER);
      }
    });

    mBinding.imageViewBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });

    mBinding.acceptButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onAccept();
      }
    });

    mBinding.hideButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onReject();
      }
    });

    awardJobModel = new AwardJobModel(this);
    mViewModel.getProfileDetails(argUserId);

    createJobListView();
  }

  private void onReject() {
    if (applicant == null) {
      Log.d(TAG, "onReject: applicant is null");
      return;
    }
    int userID = SessionManager.get(this).getUserId();
    awardJobModel.rejectApplicant(userID, String.valueOf(jobId), applicant.getApplicantId(),
        applicant);
  }

  private void onAccept() {
    if (applicant == null) {
      Log.d(TAG, "onAccept: applicant is null");
      return;
    }
    if (consignmentSize == -1) {
      Log.d(TAG, "onAccept: consignment size is wrong");
      AppUtility.showToast(this, R.string.error_consignment_size_not_avl);
      return;
    }

    AwardJobDialogHelper dialogHelper = new AwardJobDialogHelper(this, consignmentSize);
    dialogHelper.setJobListener(new AwardJobDialogHelper.JobListener() {
      @Override
      public void onAppliedSuccess(float consignmentSize) {
        int userID = SessionManager.get(AgentProfileActivity.this).getUserId();
        if (consignmentSize == -1) {
          awardJobModel.acceptApplicant(userID, String.valueOf(jobId), applicant.getApplicantId(),
              applicant, -1, -1);
        } else {
          awardJobModel.acceptApplicant(userID, String.valueOf(jobId), applicant.getApplicantId(),
              applicant, 1, consignmentSize);
        }
      }
    });
    dialogHelper.showDialog();
  }

  @Override
  public void setUserData(final ProfileResponse response) {
    mBinding.ratingBar.setRating(Float.parseFloat(response.getResponse().getReviews()));
    mBinding.reviewCount.setText("(" + response.getResponse().getCompletedJobs() + ")");

    if (!TextUtils.isEmpty(response.getResponse().getUserDoc())) {
      String fileName = response.getResponse()
          .getUserDoc()
          .substring(response.getResponse().getUserDoc().lastIndexOf('/') + 1,
              response.getResponse().getUserDoc().length());
      SpannableString content = new SpannableString(fileName);
      content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
      mBinding.document.setText(content);
      mBinding.document.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent i = new Intent(Intent.ACTION_VIEW);
          i.setData(Uri.parse(response.getResponse().getUserDoc()));
          startActivity(i);
        }
      });
      //show document link
      mBinding.documentLayout.setVisibility(View.VISIBLE);
    } else {
      mBinding.documentLayout.setVisibility(View.GONE);
    }

    if (!TextUtils.isEmpty(response.getResponse().getName())) {
      mBinding.clientName.setText(response.getResponse().getName());
    }

    if (!TextUtils.isEmpty(response.getResponse().getProfilePic())) {
      Picasso.with(this).load(response.getResponse().getProfilePic()).into(mBinding.profileImage);
    }

    if (!TextUtils.isEmpty(response.getResponse().getBio())) {
      mBinding.bio.setText(response.getResponse().getBio());
      mBinding.bioLayout.setVisibility(View.VISIBLE);
    } else {
      mBinding.bioLayout.setVisibility(View.GONE);
    }

    if (!TextUtils.isEmpty(response.getResponse().getExpertiseId())) {
      for (Category categoryList : CategoryList.parentCategoryList(this)) {
        if (categoryList.getCategoryId() != null && String.valueOf(categoryList.getCategoryId())
            .contains(response.getResponse().getExpertiseId())) {
          mBinding.expertise.setText(categoryList.getCategoryName());
        }
      }
      mBinding.expertiseLayout.setVisibility(View.VISIBLE);
    } else {
      mBinding.expertiseLayout.setVisibility(View.GONE);
    }

    if (response.getUserJobList().size() == 0) {
      mBinding.completedJobLayout.setVisibility(View.GONE);
    } else {
      mProfileJobAdapter.addJobList(response.getUserJobList());
    }
  }

  @Override
  public void showProgressBar() {
    mBinding.progressBarLayout.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideProgressBar() {
    mBinding.progressBarLayout.setVisibility(View.GONE);
  }

  @Override
  public void onAcceptSuccess(Applicant applicant) {
    Intent intent = getIntent();
    intent.putExtra(JOB_STATUS, RestFields.ACTION_ACCEPT);
    intent.putExtra(APPLICANT, applicant);
    setResult(RESULT_OK, intent);
    finish();
  }

  @Override
  public void onRejectSuccess(Applicant applicant) {
    Intent intent = getIntent();
    intent.putExtra(JOB_STATUS, RestFields.ACTION_REJECT);
    intent.putExtra(APPLICANT, applicant);
    setResult(RESULT_OK, intent);
    finish();
  }

  /**
   * This will create the recycler view bind the adapter and load the Job data
   */
  private void createJobListView() {
    mProfileJobAdapter = new ProfileJobAdapter(this);
    mBinding.jobList.setHasFixedSize(true);
    mBinding.jobList.setLayoutManager(new LinearLayoutManager(this));
    mBinding.jobList.setAdapter(mProfileJobAdapter);
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
    showEmptyCardDialog();
  }

  public void showEmptyCardDialog() {
    DialogUtils.showDialog(this, getString(R.string.error_default_card_award_job),
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            ManageCardActivity.getManageCardIntent(AgentProfileActivity.this);
          }
        });
  }
}
