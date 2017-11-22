package com.android.unideal.agent.view.fragment;

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
import com.android.unideal.agent.adapter.RatingsAdapter;
import com.android.unideal.agent.viewmodel.RatingsViewModel;
import com.android.unideal.databinding.AgentRatingsBinding;
import com.android.unideal.rest.response.RattingResponse;
import com.android.unideal.util.DividerDecoration;

public class RatingsFragment extends Fragment
        implements RatingsViewModel.RatingsListener {
    private RatingsViewModel mViewModel;
    private AgentRatingsBinding mBinding;
    private RatingsAdapter mAdapter;
    private RecyclerView.ItemDecoration mDividerItemDecoration;

    public static Fragment getInstance() {
        return new RatingsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_ratings_agent, container, false);
        mViewModel = new RatingsViewModel(getActivity(), this);
        mViewModel.loadData();
        setupRecyclerView(mBinding.ratingsList);
        return mBinding.getRoot();
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new RatingsAdapter(getActivity());
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
        mBinding.progressBarLayout.setVisibility(View.VISIBLE);
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
        mBinding.progressBarLayout.setVisibility(View.GONE);
    }

    @Override
    public void onError(String message) {
        mBinding.emptyView.setText(message);
        mBinding.emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmptyView() {
        mBinding.emptyView.setVisibility(View.VISIBLE);
        mBinding.emptyView.setText(getString(R.string.no_rating_available));
    }
}
