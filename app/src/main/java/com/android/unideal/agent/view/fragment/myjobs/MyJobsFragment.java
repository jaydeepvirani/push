package com.android.unideal.agent.view.fragment.myjobs;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.unideal.R;
import com.android.unideal.agent.view.AgentHomeActivity;
import com.android.unideal.agent.view.JobDetailsActivity;
import com.android.unideal.data.JobDetail;
import com.android.unideal.data.Status;
import com.android.unideal.databinding.MyJobsDataBinding;
import com.android.unideal.util.Communicator;
import com.special.ResideMenu.ResideMenu;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by bhavdip on 10/1/16.
 */
public class MyJobsFragment extends Fragment {

  private MyJobsDataBinding mJobDataBinding;

  private Subscription mJobSubscription;
  /**
   * Icon for the job tab layout
   */
  private int[] iconResId = {
      R.drawable.ic_applied, R.drawable.ic_inprogress, R.drawable.ic_completed,
      R.drawable.ic_paused, R.drawable.ic_cancelled
  };

  /**
   * This is the job status array reside the predefine status
   */
  private Status[] jobStatus = {
      Status.APPLIED, Status.IN_PROCESS, Status.COMPLETED, Status.PAUSED, Status.CANCELLED
  };

  public static Fragment getInstance() {
    return new MyJobsFragment();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mJobDataBinding = loadDataBinding(inflater, container);
    return mJobDataBinding.getRoot();
  }

  private MyJobsDataBinding loadDataBinding(LayoutInflater inflater,
      @Nullable ViewGroup container) {
    return DataBindingUtil.inflate(inflater, R.layout.fragment_agent_my_jobs, container, false);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    onListenJobClick();
    bindViews();
  }

  @Override
  public void onDestroy() {
    if (mJobSubscription != null) {
      mJobSubscription.unsubscribe();
      mJobSubscription = null;
    }
    super.onDestroy();
  }

  private void bindViews() {
    mJobDataBinding.actionBar.textViewTitle.setText(
        getResources().getString(R.string.title_my_jobs_agent));
    mJobDataBinding.viewPager.setAdapter(new MyJobAdapter(getChildFragmentManager()));
    mJobDataBinding.viewPager.addOnPageChangeListener(new WatchPageChange());
    mJobDataBinding.tabLayout.setupWithViewPager(mJobDataBinding.viewPager);
    setUpTabLayout();
  }

  private void setUpTabLayout() {
    int tabCount = mJobDataBinding.tabLayout.getTabCount();
    for (int i = 0; i < tabCount; i++) {
      TabLayout.Tab tabCall = mJobDataBinding.tabLayout.getTabAt(i);
      if (tabCall != null) tabCall.setIcon(ContextCompat.getDrawable(getActivity(), iconResId[i]));
    }
  }

  /**
   * Disable the Siding Left Menu
   */
  private void disableMenuDirection() {
    AgentHomeActivity activity = (AgentHomeActivity) getActivity();
    activity.setDirectionDisable(ResideMenu.DIRECTION_LEFT);
  }

  /**
   * Enable the ReSiding Left Menu
   */
  private void enableMenuDirection() {
    AgentHomeActivity activity = (AgentHomeActivity) getActivity();
    activity.setDirectionEnable(ResideMenu.DIRECTION_LEFT);
  }

  /**
   * Must register on the creation of the fragment
   * and must un subscribe while on destroy the view
   * Listen the all different mode job item click here
   */
  private void onListenJobClick() {
    mJobSubscription =
        Communicator.getCommunicator().subscribeItemClick().subscribe(new Action1<JobDetail>() {
          @Override
          public void call(JobDetail selectedJobData) {
            //start the job details activity here
            Intent detailIntent = getJobDetails(selectedJobData);
            startActivity(detailIntent);
          }
        });
  }

  private Intent getJobDetails(JobDetail inputJobDetails) {
    return JobDetailsActivity.getActivity(getActivity(), inputJobDetails.getJob_id());
  }

  /**
   * Job Adapter that can load the data according to there Job Status Type
   */
  public class MyJobAdapter extends FragmentStatePagerAdapter {
    public MyJobAdapter(FragmentManager fragmentManager) {
      super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
      return AgentJobsListFragment.getInstance(jobStatus[position]);
    }

    @Override
    public int getCount() {
      return jobStatus.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return getString(jobStatus[position].getStatusName());
    }
  }

  public class WatchPageChange implements ViewPager.OnPageChangeListener {

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
      if (position == 0) {
        enableMenuDirection();
      } else {
        disableMenuDirection();
      }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
  }
}
