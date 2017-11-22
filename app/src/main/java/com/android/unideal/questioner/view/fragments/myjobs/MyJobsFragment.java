package com.android.unideal.questioner.view.fragments.myjobs;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;

import com.android.unideal.R;
import com.android.unideal.data.JobDetail;
import com.android.unideal.data.Status;
import com.android.unideal.data.UserDetail;
import com.android.unideal.databinding.QuestionerJobDataBinding;
import com.android.unideal.questioner.view.QuestionerHomeActivity;
import com.android.unideal.questioner.view.QuestionerJobDetailsActivity;
import com.android.unideal.questioner.view.QuestionerNewJobsActivity;
import com.android.unideal.questioner.view.payment.ManageCardActivity;
import com.android.unideal.util.Communicator;
import com.android.unideal.util.Consts;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.SessionManager;
import com.special.ResideMenu.ResideMenu;

import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by bhavdip on 10/13/16.
 * <p>
 * Questioner My Job Fragment that load the question's created
 * jobs and there status of the jobs
 */

public class MyJobsFragment extends Fragment implements MaterialIntroListener {
    public static final int REQUEST_JOB_DETAILS = 810;
    private static final String FOCUS_MENU = "focus_menu";
    private static final String FOCUS_NEW_JOB = "focus_newJob";
    public static String IS_VISIBLE = "showcase_visible";
    private MaterialIntroView materialIntroView;
    private Context mActivity;
    private boolean isVisible = false;
    private int showShowcase = 1;
    /**
     * Icon for the job tab layout
     */
    private int[] iconResId = {
            R.drawable.ic_open, R.drawable.ic_inprogress, R.drawable.ic_completed, R.drawable.ic_paused,
            R.drawable.ic_cancelled
    };
    /**
     * This is the job status array reside the predefine status
     */

    private Status[] jobStatus = {
            Status.OPEN, Status.IN_PROCESS, Status.COMPLETED, Status.PAUSED, Status.CANCELLED
    };
    private QuestionerJobDataBinding mDataBinding;
    private QuestionerJobsAdapter mJobsAdapter;

    /**
     * This will hold the subscription reference
     * When fragment destroy using it unsubscript the
     * event
     */
    private Subscription mSubscription;

    public static Fragment getInstance(boolean isShowCaseLayoutVisible) {
        MyJobsFragment myJobsFragment = new MyJobsFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(IS_VISIBLE, isShowCaseLayoutVisible);
        myJobsFragment.setArguments(bundle);
        return myJobsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mDataBinding = loadBinding(inflater, container);
        showShowcase = SessionManager.get(getActivity()).shouldShowTourGuide();
        if (getArguments() != null) {
            isVisible = getArguments().getBoolean(IS_VISIBLE);
        }

        if ((isVisible) && (showShowcase == Consts.SHOWCASE_GUIDE_SHOW)) {
            setShowCaseLayout(mDataBinding.newJob, FOCUS_NEW_JOB,
                    getString(R.string.txt_show_case_new_job), Focus.ALL);
        }

        mDataBinding.newJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() == null) {
                    return;
                }
                onNewJobClick();
            }
        });
        return mDataBinding.getRoot();
    }

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (Build.VERSION.SDK_INT >= 23) {
            mActivity = (Context) context;
        }
    }

    /*
     * Deprecated on API 23
     * Use onAttachToContext instead
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < 23) {
            mActivity = (Activity) activity;
        }
    }

    private void setShowCaseLayout(View view, String id, String text, Focus focusType) {
        materialIntroView = new MaterialIntroView.Builder(getActivity()).enableDotAnimation(true)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.ALL)
                .setShape(ShapeType.CIRCLE)
                .setDelayMillis(200)
                .dismissOnTouch(Consts.dismissOnTouch)
                .enableFadeAnimation(true)
                .setListener(this)
                .performClick(false)
                .setInfoText(text)
                .setTarget(view)
                .setUsageId(id)
                .enableIcon(false)
                .show();
    }

    private void onNewJobClick() {

        UserDetail userDetail = SessionManager.get(getActivity()).getActiveUser();
        //check it user login from facebook if email not available then open dialog
        if (userDetail.getFromSocialNetwork() == 1 && TextUtils.isEmpty(userDetail.getEmailAddress())) {
            DialogUtils.showDialog(getActivity(), R.string.title_app_name,
                    getString(R.string.text_email_no_available), R.string.text_continue, R.string.btn_cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //check credit card available or not
                            checkCreditCardInfo();
                        }
                    });
        } else {
            checkCreditCardInfo();
        }
    }

    private void checkCreditCardInfo() {
        final String defaultCard = getDefaultCard();
//    if (TextUtils.isEmpty(defaultCard)) {
//      DialogUtils.showDialog(getActivity(), getString(R.string.error_default_card),
//          new DialogInterface.OnClickListener() {
//            @Override public void onClick(DialogInterface dialog, int which) {
//              ManageCardActivity.getManageCardIntent(getActivity());
//            }
//          });
//    } else {
//      startActivity(QuestionerNewJobsActivity.startNewJobsActivity(getActivity()));
//    }
        startActivity(QuestionerNewJobsActivity.startNewJobsActivity(getActivity()));
    }

    private String getDefaultCard() {
        UserDetail userDetail = SessionManager.get(getActivity()).getActiveUser();
        if (userDetail == null) {
            return null;
        } else {
            return userDetail.getDefault_card();
        }
    }

    private QuestionerJobDataBinding loadBinding(LayoutInflater inflater,
                                                 @Nullable ViewGroup container) {
        return DataBindingUtil.inflate(inflater, R.layout.fragment_questioner_my_jobs, container,
                false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindingViewPager();
        bindingTabLayout();
        // subscribe  the job item click
        // It can handle any status of job details
        subscribeJobClick();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unSubscribeJobClick();
    }

    private void bindingViewPager() {
        mDataBinding.actionBar.textViewTitle.setText(getResources().getString(R.string.title_my_jobs));
        mJobsAdapter = new QuestionerJobsAdapter(getChildFragmentManager());
        mDataBinding.viewPager.setAdapter(mJobsAdapter);
        mDataBinding.viewPager.addOnPageChangeListener(new WatchJobFragmentChange());
        mDataBinding.tabLayout.setupWithViewPager(mDataBinding.viewPager);
    }

    /**
     *
     */
    private void bindingTabLayout() {
        int tabCount = mDataBinding.tabLayout.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            TabLayout.Tab tabCall = mDataBinding.tabLayout.getTabAt(i);
            if (tabCall != null)
                tabCall.setIcon(ContextCompat.getDrawable(getActivity(), iconResId[i]));
        }
    }

    private void subscribeJobClick() {
        mSubscription =
                Communicator.getCommunicator().subscribeItemClick().subscribe(new Action1<JobDetail>() {
                    @Override
                    public void call(JobDetail job) {
                        // When user click we receive the job model
                        Intent detailIntent =
                                QuestionerJobDetailsActivity.getActivity(getActivity(), job.getJob_id());
                        startActivityForResult(detailIntent, REQUEST_JOB_DETAILS);
                    }
                });
    }

    /**
     * Must call it when we clean up the resource.
     */
    private void unSubscribeJobClick() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
    }

    /**
     * Disable the Siding Left Menu
     */
    private void disableMenuDirection() {
        QuestionerHomeActivity activity = (QuestionerHomeActivity) getActivity();
        activity.setDirectionDisable(ResideMenu.DIRECTION_LEFT);
    }

    /**
     * Enable the ReSiding Left Menu
     */
    private void enableMenuDirection() {
        QuestionerHomeActivity activity = (QuestionerHomeActivity) getActivity();
        activity.setDirectionEnable(ResideMenu.DIRECTION_LEFT);
    }

    @Override
    public void onUserClicked(String materialIntroViewId) {

    }

    /**
     * This is the adapter for bind with my job section
     * Its used the Job status except the open Job because
     * we need to start status from in applied status instead
     * of the open job
     */
    public class QuestionerJobsAdapter extends FragmentStatePagerAdapter {

        public QuestionerJobsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return JobsListFragment.getInstance(jobStatus[position]);
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

    /**
     * View pager job fragment change listener, we want it because if you try to swipe
     * the view pager, the swipe would listener by the left menu and it will open left
     * menu instead of swipe. It can handle be disable the menu unless user can reach
     * at the first page.
     */
    public class WatchJobFragmentChange implements ViewPager.OnPageChangeListener {

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
