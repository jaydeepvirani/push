package com.android.unideal.questioner.view.fragments.jobdetails;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.view.MaterialIntroView;

import com.android.unideal.R;
import com.android.unideal.data.Applicant;
import com.android.unideal.data.JobDetail;
import com.android.unideal.data.Status;
import com.android.unideal.data.UserDetail;
import com.android.unideal.databinding.ApplicantsBinding;
import com.android.unideal.enums.AppMode;
import com.android.unideal.questioner.adapter.ApplicantsListAdapter;
import com.android.unideal.questioner.questionhelper.AwardJobDialogHelper;
import com.android.unideal.questioner.questionhelper.AwardJobModel;
import com.android.unideal.questioner.view.AgentProfileActivity;
import com.android.unideal.questioner.view.QuestionerJobDetailsActivity;
import com.android.unideal.questioner.view.UniDealDeliveryPlaceActivity;
import com.android.unideal.questioner.view.payment.AddNewCardActivity;
import com.android.unideal.questioner.view.payment.ManageCardActivity;
import com.android.unideal.questioner.viewmodel.QueApplicantFragmentViewModel;
import com.android.unideal.rest.RestFields;
import com.android.unideal.util.AppUtility;
import com.android.unideal.util.Communicator;
import com.android.unideal.util.Consts;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.SessionManager;
import com.daimajia.swipe.util.Attributes;

import de.hdodenhof.circleimageview.CircleImageView;

import java.util.List;

/**
 * Created by bhavdip on 18/10/16.
 */

public class ApplicantsFragment extends Fragment
        implements QueApplicantFragmentViewModel.QueApplicantListener,
        ApplicantsListAdapter.ApplicantsListener, AwardJobModel.AwardListener, MaterialIntroListener {
    public static final String APPLICANT = "applicant";
    private static final int REQ_PROFILE_ACTIVITY = 7;
    private static final String ARG_JOB_DETAILS = "jobDetails";
    private static final String FOCUS_AGENT_PROFILE = "agentProfile";
    private static String JOB_STATUS = "job_status";
    private ApplicantsBinding mApplicantsBinding;
    private ApplicantsListAdapter mApplicantsListAdapter;
    private QueApplicantFragmentViewModel mViewModel;
    private JobDetail mJobDetail;
    private ApplicantCountListener mApplicantCountListener;
    private AwardJobModel awardJobModel;
    private MaterialIntroView materialIntroView;

    public static Fragment getInstance(JobDetail jobDetail) {
        Fragment fragment = new ApplicantsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_JOB_DETAILS, jobDetail);
        fragment.setArguments(bundle);
        return fragment;
    }

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttachToContext(context);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }
    }

    private void onAttachToContext(Context context) {
        if (getParentFragment() instanceof ApplicantCountListener) {
            mApplicantCountListener = (ApplicantCountListener) getParentFragment();
        } else {
            throw new RuntimeException("Parent fragment must implement ApplicantCountListener ");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mJobDetail = getArguments().getParcelable(ARG_JOB_DETAILS);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mApplicantsBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_applicantshowcase, container, false);
        mViewModel = new QueApplicantFragmentViewModel(this);
        mApplicantsBinding.setViewmodel(mViewModel);
        awardJobModel = new AwardJobModel(this);
        String userId = String.valueOf(SessionManager.get(getActivity()).getUserId());
        mViewModel.getApplicantList(userId, String.valueOf(mJobDetail.getJob_id()));
        return mApplicantsBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpRecyclerViews();
    }

    private void setUpRecyclerViews() {
        mApplicantsListAdapter = new ApplicantsListAdapter(this, getContext());
        mApplicantsListAdapter.setMode(Attributes.Mode.Single);
        mApplicantsBinding.applicantList.setHasFixedSize(true);
        mApplicantsBinding.applicantList.setLayoutManager(new LinearLayoutManager(getContext()));
        mApplicantsBinding.applicantList.setAdapter(mApplicantsListAdapter);
    }

    private void updateApplicantCount() {

        if (mApplicantsListAdapter != null && mApplicantCountListener != null) {
            mApplicantCountListener.showApplicantCount(mApplicantsListAdapter.getItemCount());
        }
    }

    @Override
    public void loadApplicantList(List<Applicant> data, String message) {
        mApplicantsListAdapter.addApplicationList(data);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mApplicantsListAdapter.getItemCount() > 0) {
                    showCaseLayout((CircleImageView) mApplicantsBinding.applicantList.getChildAt(0)
                            .findViewById(R.id.imageViewProfile));
                }
            }
        }, 100);

        updateApplicantCount();
        updateEmptyMessage(message);
    }

    private void updateEmptyMessage(String message) {
        if (mApplicantsListAdapter.getItemCount() > 0) {
            mApplicantsBinding.emptyView.setVisibility(View.GONE);
        } else {
            mApplicantsBinding.emptyView.setVisibility(View.VISIBLE);
            if (message == null) {
                message = getString(R.string.no_data_available);
            }
            mApplicantsBinding.emptyView.setText(message);
        }
    }

    @Override
    public void showProgressDialog() {
        DialogUtils.getInstance().showProgressDialog(getActivity());
    }

    @Override
    public void hideProgressDialog() {
        DialogUtils.getInstance().hideProgressDialog();
    }

    @Override
    public void onAcceptSuccess(Applicant applicant) {
        Communicator.getCommunicator().onApplicationAccept(applicant);
    }

    @Override
    public void onRejectSuccess(Applicant applicant) {
        mApplicantsListAdapter.removeItem(applicant.getApplicantId());
        updateApplicantCount();
        updateEmptyMessage(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PROFILE_ACTIVITY) {
            if (resultCode == QuestionerJobDetailsActivity.RESULT_OK) {
                Log.d("ApplicantsFragment", "onActivityResult: ");
                int jobStatus = data.getIntExtra(JOB_STATUS, -1);
                if (jobStatus != -1) {
                    Applicant applicant = data.getParcelableExtra(APPLICANT);
                    if (jobStatus == RestFields.ACTION_ACCEPT) {
                        Communicator.getCommunicator().onApplicationAccept(applicant);
                    } else if (jobStatus == RestFields.ACTION_REJECT) {
                        mApplicantsListAdapter.removeItem(applicant.getApplicantId());
                        updateApplicantCount();
                        updateEmptyMessage(null);
                    } else {
                        getActivity().finish();
                    }
                }
            }
        }
    }

    @Override
    public void showToast(String message) {
        AppUtility.showToast(getActivity(), message);
    }

    @Override
    public void onAccept(final Applicant applicant, int position) {
        if (isCardEmpty()) {
            // if card is empty then addcard activity
            DialogUtils.showDialog(getContext(), getString(R.string.error_default_card_award_job), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ManageCardActivity.getManageCardIntent(getActivity());
                }
            });
        } else {
            //if card is not empty then ajent award a job
            AwardJobDialogHelper dialogHelper =
                    new AwardJobDialogHelper(getActivity(), applicant.getOffer());
            dialogHelper.setJobListener(new AwardJobDialogHelper.JobListener() {
                @Override
                public void onAppliedSuccess(float consignmentSize) {
                    int userID = SessionManager.get(getActivity()).getUserId();
                    if (consignmentSize == -1) {
                        awardJobModel.acceptApplicant(userID, String.valueOf(mJobDetail.getJob_id()),
                                applicant.getApplicantId(), applicant, -1, -1);
                    } else {
                        awardJobModel.acceptApplicant(userID, String.valueOf(mJobDetail.getJob_id()),
                                applicant.getApplicantId(), applicant, 1, consignmentSize);
                    }
                }
            });
            dialogHelper.showDialog();
        }

    }

    private boolean isCardEmpty() {
        if (TextUtils.isEmpty(getDefaultCard())) {
            return true;
        } else {
            return false;
        }
    }

    private String getDefaultCard() {
        UserDetail userDetail = SessionManager.get(getActivity()).getActiveUser();
        if (userDetail == null) {
            return null;
        } else {
            return userDetail.getDefault_card();
        }
    }

    @Override
    public void onReject(Applicant applicant, int position) {
        int userID = SessionManager.get(getActivity()).getUserId();
        awardJobModel.rejectApplicant(userID, String.valueOf(mJobDetail.getJob_id()),
                applicant.getApplicantId(), applicant);
    }

    @Override
    public void onItemClick(Applicant applicant, int position) {
        if (mJobDetail.getStatusEnum() == Status.OPEN) {
            openAgentProfileActivity(String.valueOf(applicant.getApplicantId()), mJobDetail.getJob_id(),
                    mJobDetail.getConsignment_size(), applicant, applicant.getOffer());
        }
    }

    @Override
    public void onDeliveryClick() {
        UniDealDeliveryPlaceActivity.startDeliveryPlaceActivity(getActivity(), AppMode.QUESTIONER);
    }

    private void showCaseLayout(CircleImageView imageViewProfile) {
        if (SessionManager.get(getActivity()).shouldShowTourGuide() == Consts.SHOWCASE_GUIDE_SHOW) {
            materialIntroView = new MaterialIntroView.Builder(getActivity()).enableDotAnimation(true)
                    .setFocusGravity(FocusGravity.CENTER)
                    .dismissOnTouch(Consts.dismissOnTouch)
                    .setFocusType(Focus.ALL)
                    .setDelayMillis(200)
                    .enableFadeAnimation(false)
                    .enableIcon(false)
                    .setListener(this)
                    .performClick(false)
                    .setInfoText(getString(R.string.txt_show_case_agent_profile))
                    .setTarget(imageViewProfile)
                    .setUsageId(FOCUS_AGENT_PROFILE)
                    .show();
        }
    }

    private void openAgentProfileActivity(String userId, int job_id, float consignmentSize,
                                          Applicant applicant, float agentOfferPrice) {
        Intent intent = AgentProfileActivity.getInstance(getActivity(), 0, applicant, userId, job_id,
                consignmentSize, agentOfferPrice, applicant.getDelivery_place());
        startActivityForResult(intent, REQ_PROFILE_ACTIVITY);
    }

    @Override
    public void onUserClicked(String materialIntroViewId) {
        if (materialIntroViewId.equals(FOCUS_AGENT_PROFILE)) {
            materialIntroView.dismiss();
        }
    }
}
