package com.android.unideal.questioner.view.fragments;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.agent.view.JobMessageActivity;
import com.android.unideal.data.Applicant;
import com.android.unideal.data.JobDetail;
import com.android.unideal.data.socket.Messages;
import com.android.unideal.databinding.QuestionerMessageBinding;
import com.android.unideal.enums.AppMode;
import com.android.unideal.questioner.adapter.QuestionerMessagesAdapter;
import com.android.unideal.questioner.viewmodel.MessageFragmentViewModel;
import com.android.unideal.util.DividerDecoration;
import java.util.List;

/**
 * Created by ADMIN on 12-10-2016.
 */

public class QuestionerMessageFragment extends Fragment
    implements MessageFragmentViewModel.QuestionerMessageListener,
    QuestionerMessagesAdapter.OnLoadMoreListener, QuestionerMessagesAdapter.OnMessagesItemClick,
    SwipeRefreshLayout.OnRefreshListener {

  private static final String TAG = "QuestionerMsgFragment";
  private final int REA_INIT_CONVERSATION = 444;
  private QuestionerMessageBinding mBinding;
  private MessageFragmentViewModel mViewModel;
  private QuestionerMessagesAdapter mAdapter;
  private RecyclerView.ItemDecoration mDividerItemDecoration;

  public static Fragment getInstance() {
    return new QuestionerMessageFragment();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mBinding = loadBinding(inflater, container);
    return mBinding.getRoot();
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mViewModel = new MessageFragmentViewModel(getActivity(), this);
    mViewModel.onActivityCreated();
  }

  private QuestionerMessageBinding loadBinding(LayoutInflater inflater,
      @Nullable ViewGroup container) {
    return DataBindingUtil.inflate(inflater, R.layout.fragment_messages_questioner, container,
        false);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REA_INIT_CONVERSATION) {
      if (resultCode == Activity.RESULT_OK) {
        //start refresh or update the list
        mViewModel.refreshMessages();
      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    mViewModel.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
    mViewModel.onPause();
  }

  @Override
  public void startBindingViews() {
    //set up message recycler view
    setupRecyclerView();
    //register the refreshLayout
    mBinding.swipeRefresh.setOnRefreshListener(this);
  }

  private void setupRecyclerView() {
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    mBinding.messageList.setLayoutManager(linearLayoutManager);
    mAdapter = new QuestionerMessagesAdapter(getActivity());
    mAdapter.setLoadMoreListener(this);
    mAdapter.setOnMessagesItemClick(this);
    mBinding.messageList.setAdapter(mAdapter);
    addDivider(mBinding.messageList);
  }

  public void addDivider(RecyclerView recyclerView) {
    Drawable dividerDrawable =
        ContextCompat.getDrawable(getActivity(), R.drawable.divider_recycleview);
    mDividerItemDecoration = new DividerDecoration(dividerDrawable);
    recyclerView.addItemDecoration(mDividerItemDecoration);
  }

  @Override
  public void showProgressBar(final boolean visibility) {
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (visibility) {
          mBinding.progressBarLayout.setVisibility(View.VISIBLE);
          //If it is first time then we hide and show the recycler view
          if (mViewModel.getCurrentIndex() == 0) {
            mBinding.messageList.setVisibility(View.GONE);
          }
        } else {
          mBinding.progressBarLayout.setVisibility(View.GONE);
          //If it is first time then we hide and show the recycler view
          mBinding.messageList.setVisibility(View.VISIBLE);
        }
      }
    });
  }

  @Override
  public void bindMessagesList(List<Messages> messages) {
    mBinding.emptyViewLayout.setVisibility(View.GONE);
    mAdapter.addMessagesData(messages);
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        mAdapter.notifyDataChanged();
      }
    });
  }

  @Override
  public void onLoadMore() {
    int index = mAdapter.getDataSetSize();
    Log.d(TAG, "onLoadMore: Item Index [" + index + "]");
    //Start next page request here
    mViewModel.getMessagesListData();
  }

  @Override
  public void showLoadMoreView(boolean visibility) {
    if (visibility) {
      //Calling loadMore function in Runnable to fix the
      // java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling error
      mBinding.messageList.post(new Runnable() {
        @Override
        public void run() {
          mAdapter.addLoadMore();
        }
      });
    } else {
      mAdapter.removeLoadMore();
    }
  }

  @Override
  public void bindEmptyView() {
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        mBinding.emptyViewLayout.setVisibility(View.VISIBLE);
      }
    });
  }

  @Override
  public void onSocketConnected() {
    Log.d(TAG, "onSocketConnected: ");
    mViewModel.startAuthentication();
  }

  @Override
  public void onAuthSuccess() {
    Log.d(TAG, "onAuthSuccess: ");
    //start loading data
    if (mViewModel.getCurrentIndex() == 0) {
      mViewModel.getMessagesListData();
    }
  }

  @Override
  public void onAuthFailed() {
    Log.d(TAG, "onAuthFailed: ");
  }

  @Override
  public void setMoreDataAvailable(boolean available) {
    mAdapter.setMoreDataAvailable(available);
  }

  @Override
  public void onItemClick(Messages chooseItems) {
    //Note we have not job details here but we build here
    //using messages
    JobDetail jobDetail = new JobDetail();
    //job_id
    jobDetail.setJob_id(chooseItems.getJobId());
    //job status
    jobDetail.setJob_status(chooseItems.getJobStatus());

    //msg user id agent id
    jobDetail.setMsg_user_id(chooseItems.getAgentId());
    //msg user/agent name
    jobDetail.setMsg_user_name(chooseItems.getAgentName());
    //thread_id
    jobDetail.setMsg_thread_id(chooseItems.getThreadId());
    // profile pic
    jobDetail.setMsg_user_profile_url(chooseItems.getAgentProfilePicture());
    // consignment_size
    jobDetail.setConsignment_size(chooseItems.getConsignmentSize());

    //TODO add for give the support of the agent profile screen open from messages section
    //offered priced
    jobDetail.setOffered_price(chooseItems.getOfferedPrice());
    //offered method/place
    jobDetail.setDeliveryplace(chooseItems.getDeliveryMethod());

    boolean isOffered = chooseItems.getIsOffered() == 1;

    Applicant applicant = new Applicant();
    //job id
    applicant.setJobId(jobDetail.getJob_id());

    applicant.setUserId(jobDetail.getUser_id());

    applicant.setUserName(jobDetail.getUser_name());
    //agent id
    applicant.setApplicantId(jobDetail.getMsg_user_id());
    //delivery place
    applicant.setDelivery_place(jobDetail.getDeliveryplace());
    //is offered
    applicant.setIs_offered(chooseItems.getIsOffered());

    Intent conversationIntent =
        JobMessageActivity.getActivity(getActivity(), AppMode.QUESTIONER, jobDetail, applicant,
            isOffered);
    startActivityForResult(conversationIntent, REA_INIT_CONVERSATION);
  }

  @Override
  public void onRefresh() {
    mViewModel.refreshMessages();
  }

  @Override
  public void onUpdateMessages(List<Messages> updatedMessagesList) {
    if (updatedMessagesList != null && updatedMessagesList.size() > 0) {
      mAdapter.onUpdateDataSet(updatedMessagesList);
    }
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        mAdapter.notifyDataChanged();
      }
    });
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        mBinding.swipeRefresh.setRefreshing(false);
      }
    });
  }

  @Override
  public void onNewAgentMessages() {
    mViewModel.refreshMessages();
  }
}
