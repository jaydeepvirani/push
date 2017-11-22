package com.android.unideal.agent.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import co.mobiwise.materialintro.animation.MaterialIntroListener;
import com.android.unideal.R;
import com.android.unideal.agent.adapter.AgentJobListAdapter;
import com.android.unideal.agent.view.AgentHomeActivity;
import com.android.unideal.agent.view.FilterActivity;
import com.android.unideal.agent.view.JobDetailsActivity;
import com.android.unideal.agent.viewmodel.AgentJobListFragmentViewModel;
import com.android.unideal.data.JobDetail;
import com.android.unideal.databinding.AgentJobBinding;
import com.android.unideal.rest.RestFields;
import com.android.unideal.util.AppUtility;
import com.android.unideal.util.Communicator;
import com.android.unideal.util.Consts;
import com.android.unideal.util.DividerDecoration;
import com.android.unideal.util.SessionManager;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.special.ResideMenu.ResideMenu;
import java.util.HashMap;
import java.util.List;

import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by ADMIN on 23-09-2016.
 */

public class AgentHomeFragment extends Fragment
    implements AgentJobListFragmentViewModel.AgentJobListener,
    AgentJobListAdapter.AgentJobListListener, MaterialIntroListener {
  public static final int REQUEST_FOR_JOB_APPLIED = 910;
  private static final int REQUEST_FILTER = 111;
  private AgentJobBinding mBinding;
  private AgentJobListAdapter mAdapter;
  private AgentJobListFragmentViewModel mViewModel;
  private MaterialIntroView materialIntroView;
  private String FOCUS_FILTER = "filter_id";
  private ResideMenu resideMenu;
  /**
   * This will hold the subscription reference
   * When fragment destroy using it un-subscript the
   * event
   */
  private Subscription mSubscription;
  private HashMap<String, String> filterParams = new HashMap<>();

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_agent_home, container, false);
    mViewModel = new AgentJobListFragmentViewModel(this);
    mBinding.setViewmodel(mViewModel);
    AgentHomeActivity parentActivity = (AgentHomeActivity) getActivity();
    resideMenu = parentActivity.getResideMenu();
    setUpSearchButton();
    setUpShowCaseLayout();
    return mBinding.getRoot();
  }

  private void setUpShowCaseLayout() {
    if (SessionManager.get(getActivity()).shouldShowTourGuide() == Consts.SHOWCASE_GUIDE_SHOW) {
      materialIntroView = new MaterialIntroView.Builder(getActivity()).enableDotAnimation(true)
          .setFocusGravity(FocusGravity.CENTER)
          .setFocusType(Focus.ALL)
          .setShape(ShapeType.CIRCLE)
          .setDelayMillis(200)
          .enableIcon(false)
          .enableFadeAnimation(true)
          .dismissOnTouch(Consts.dismissOnTouch)
          .performClick(false)
          .setInfoText(getString(R.string.txt_show_case_agt_filter_job))
          .setTarget(mBinding.filter)
          .setUsageId(FOCUS_FILTER) //THIS SHOULD BE UNIQUE ID
          .show();
    }
  }

  private void setUpSearchButton() {
    mBinding.editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
          requestAgentJobListWithSearch();
          return true;
        }
        return false;
      }
    });
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mViewModel.onActivityCreated();
    // don't need to call, cz search edit text will call first time also
    //requestAgentJobList();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case REQUEST_FOR_JOB_APPLIED: {
        if (resultCode == Activity.RESULT_OK) {
          //refresh Agent Job List
          requestAgentJobList();
        }
        break;
      }
      case REQUEST_FILTER: {
        if (resultCode == Activity.RESULT_OK) {
          if (data == null) {
            requestAgentJobList();
          } else {
            String postingDate = data.getStringExtra(FilterActivity.DATA_POSTING_DATE);
            String expiryDate = data.getStringExtra(FilterActivity.DATA_EXPIRY_DATE);
            int startRang = data.getIntExtra(FilterActivity.DATA_START_RANG, -1);
            int endRang = data.getIntExtra(FilterActivity.DATA_END_RANG, -1);
            int categoryId = data.getIntExtra(FilterActivity.DATA_CATEGORY_ID, -1);
            int subCategoryId = data.getIntExtra(FilterActivity.DATA_SUB_CATEGORY_ID, -1);
            requestAgentJobList(postingDate, expiryDate, startRang, endRang, categoryId,
                subCategoryId);
          }
        }
        break;
      }
    }
  }

  @Override
  public void onDestroyView() {
    removeItemClick();
    super.onDestroyView();
  }

  @Override
  public void startBindingViews() {
    startHandleItemClick();
    mBinding.filter.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        resideMenu.setSwipeDirectionEnable(ResideMenu.DIRECTION_LEFT);
        openFilterActivity();
      }
    });

    RxTextView.textChanges(mBinding.editTextSearch).subscribe(new Action1<CharSequence>() {
      @Override
      public void call(CharSequence charSequence) {
        if (charSequence.length() > 0) {
          mBinding.search.setVisibility(View.VISIBLE);
        } else {
          mBinding.search.setVisibility(View.GONE);
          requestAgentJobList();
        }
      }
    });

    RxView.clicks(mBinding.search).subscribe(new Action1<Void>() {
      @Override
      public void call(Void aVoid) {
        requestAgentJobListWithSearch();
      }
    });
  }

  private void openFilterActivity() {
    String startDate = filterParams.get(RestFields.KEY_POSTING_DATE);
    String endDate = filterParams.get(RestFields.KEY_EXPIRY_DATE);
    String categoryId = filterParams.get(RestFields.KEY_CATEGORY_ID);
    String subCategoryId = filterParams.get(RestFields.KEY_SUB_CATEGORY_ID);
    String startRang = filterParams.get(RestFields.KEY_JOB_FEE_MIN);
    String endRang = filterParams.get(RestFields.KEY_JOB_FEE_MAX);
    Intent intent =
        FilterActivity.startFilterActivity(getActivity(), startDate, endDate, startRang, endRang,
            categoryId, subCategoryId);
    startActivityForResult(intent, REQUEST_FILTER);
  }

  @Override
  public void setupRecyclerView() {
    setupRecyclerView(mBinding.jobList);
  }

  private void requestAgentJobList() {
    if (getActivity() != null) {
      int useId = SessionManager.get(getActivity()).getUserId();
      filterParams.clear();
      mViewModel.getJobList(String.valueOf(useId));
    }
  }

  private void requestAgentJobListWithSearch() {
    if (getActivity() != null) {
      AppUtility.hideSoftKeyBoard(getActivity(), getActivity().getCurrentFocus());
      int useId = SessionManager.get(getActivity()).getUserId();
      String searchQuery = mBinding.editTextSearch.getText().toString().trim();
      mViewModel.getJobList(String.valueOf(useId), filterParams, searchQuery);
    }
  }

  private void requestAgentJobList(String postingDate, String expDate, int startRang, int endRang,
      int categoryId, int subCategoryId) {
    if (getActivity() != null) {
      filterParams.clear();
      int useId = SessionManager.get(getActivity()).getUserId();
      if (!TextUtils.isEmpty(postingDate)) {
        filterParams.put(RestFields.KEY_POSTING_DATE, postingDate);
      }
      if (!TextUtils.isEmpty(expDate)) {
        filterParams.put(RestFields.KEY_EXPIRY_DATE, expDate);
      }
      if (startRang != -1) {
        filterParams.put(RestFields.KEY_JOB_FEE_MIN, String.valueOf(startRang));
      }
      if (endRang != -1) {
        filterParams.put(RestFields.KEY_JOB_FEE_MAX, String.valueOf(endRang));
      }
      if (categoryId != -1) {
        filterParams.put(RestFields.KEY_CATEGORY_ID, String.valueOf(categoryId));
      }
      if (subCategoryId != -1) {
        filterParams.put(RestFields.KEY_SUB_CATEGORY_ID, String.valueOf(subCategoryId));
      }
      mViewModel.getJobList(String.valueOf(useId), filterParams);
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
  public void responseAgentJobList(List<JobDetail> jobList, String message) {
    mAdapter.addData(jobList);
    updateEmptyView(message);
    updateFilterIcon();
  }

  private void updateFilterIcon() {
    mBinding.filter.setSelected(!filterParams.isEmpty());
  }

  private void updateEmptyView(String message) {
    if (mAdapter.getItemCount() > 0) {
      mBinding.emptyView.setVisibility(View.GONE);
    } else {
      mBinding.emptyView.setVisibility(View.VISIBLE);
      if (TextUtils.isEmpty(message)) {
        message = getString(R.string.please_try_again);
      }
      mBinding.emptyView.setText(message);
    }
  }

  private void setupRecyclerView(RecyclerView recyclerView) {
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    Drawable dividerDrawable =
        ContextCompat.getDrawable(getActivity(), R.drawable.divider_recycleview);
    RecyclerView.ItemDecoration mDividerItemDecoration = new DividerDecoration(dividerDrawable);
    recyclerView.addItemDecoration(mDividerItemDecoration);

    mAdapter = new AgentJobListAdapter(getActivity(), recyclerView);
    mAdapter.setAgentListener(this);
    recyclerView.setAdapter(mAdapter);
  }

  private void startHandleItemClick() {

    mSubscription =
        Communicator.getCommunicator().subscribeItemClick().subscribe(new Action1<JobDetail>() {
          @Override
          public void call(JobDetail jobDetail) {
            // When user click we receive the job model
            Intent detailsIntent =
                JobDetailsActivity.getActivity(getActivity(), jobDetail.getJob_id());
            startActivityForResult(detailsIntent, REQUEST_FOR_JOB_APPLIED);
          }
        });
  }

  /**
   * Must call it when we clean up the resource.
   */
  private void removeItemClick() {
    if (mSubscription != null) {
      mSubscription.unsubscribe();
    }
  }

  @Override
  public void onUserClicked(String s) {

  }
}
