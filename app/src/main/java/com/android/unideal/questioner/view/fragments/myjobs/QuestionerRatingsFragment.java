package com.android.unideal.questioner.view.fragments.myjobs;

import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.databinding.QuestionerRatingsBinding;
import com.android.unideal.questioner.adapter.QuestionerRatingsAdapter;
import com.android.unideal.questioner.viewmodel.QuestionerRatingFragmentViewModel;
import com.android.unideal.rest.response.RattingResponse;
import com.android.unideal.util.DividerDecoration;

/**
 * Created by MRUGESH on 10/15/2016.
 */

public class QuestionerRatingsFragment extends Fragment
    implements QuestionerRatingFragmentViewModel.QuestionerRatingListener,
    QuestionerRatingsAdapter.QuestionerAdapterRatingListener {
  private QuestionerRatingsBinding mBinding;
  private QuestionerRatingFragmentViewModel mViewModel;
  private QuestionerRatingsAdapter mAdapter;
  private RecyclerView.ItemDecoration mDividerItemDecoration;

  public static Fragment getInstance() {
    return new QuestionerRatingsFragment();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mBinding =
        DataBindingUtil.inflate(inflater, R.layout.fragment_ratings_questioner, container, false);
    mViewModel = new QuestionerRatingFragmentViewModel(getActivity(), this);
    setupRecyclerView(mBinding.ratingsList);
    mViewModel.loadData();
    return mBinding.getRoot();
  }

  private void setupRecyclerView(RecyclerView recyclerView) {
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    mAdapter = new QuestionerRatingsAdapter(getActivity(), recyclerView);
    mAdapter.setRatingListener(this);
    recyclerView.setAdapter(mAdapter);
    addDivider(recyclerView);
  }

  public void addDivider(RecyclerView recyclerView) {
    Drawable dividerDrawable =
        ContextCompat.getDrawable(getActivity(), R.drawable.divider_recycleview);
    mDividerItemDecoration = new DividerDecoration(dividerDrawable);
    recyclerView.addItemDecoration(mDividerItemDecoration);
  }

  @Override
  public void showProgressBar() {
    mBinding.progressBar.setVisibility(View.VISIBLE);
    mBinding.ratingsList.setVisibility(View.GONE);
  }

  @Override
  public void loadData(RattingResponse agentJobListResponses) {

    mAdapter.addData(agentJobListResponses.getJobRattingsList());
    mAdapter.addRattingData(agentJobListResponses);
  }

  @Override
  public void hideProgressBar() {
    mBinding.ratingsList.setVisibility(View.VISIBLE);
    mBinding.progressBar.setVisibility(View.GONE);
  }

  @Override
  public void onError(String message) {
    mBinding.emptyView.setVisibility(View.VISIBLE);
    mBinding.emptyView.setText(message);
  }

  @Override
  public void setEmptyView() {
    mBinding.emptyView.setVisibility(View.VISIBLE);
    mBinding.emptyView.setText(getString(R.string.no_rating_available));
  }
}
