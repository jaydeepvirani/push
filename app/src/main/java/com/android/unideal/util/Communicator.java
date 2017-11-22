package com.android.unideal.util;

import com.android.unideal.data.Applicant;
import com.android.unideal.data.JobDetail;
import com.android.unideal.data.Status;
import com.android.unideal.rtp.RunTimePermission;
import rx.subjects.PublishSubject;

/**
 * Created by bhavdip on 3/10/16.
 */

public class Communicator {
  private static Communicator mCommunicator = new Communicator();
  private PublishSubject<JobDetail> mJobItemClickListener = PublishSubject.create();
  private PublishSubject<Applicant> mUserClickListener = PublishSubject.create();
  private PublishSubject<RunTimePermission> runTimeSubject = PublishSubject.create();
  private PublishSubject<Applicant> mApplicantListener = PublishSubject.create();
  private PublishSubject<Status> refreshQuestionerJob = PublishSubject.create();
  private PublishSubject<Status> refreshAgentJob = PublishSubject.create();

  private Communicator() {
  }

  public static Communicator getCommunicator() {
    return mCommunicator;
  }

  public void emitJobClick(JobDetail selectJobItem) {
    mJobItemClickListener.onNext(selectJobItem);
  }

  public void onUserItemClick(Applicant applicant) {
    mUserClickListener.onNext(applicant);
  }

  public void onApplicationAccept(Applicant applicant) {
    mApplicantListener.onNext(applicant);
  }

  /**
   * Return Subscription Make sure you should Un-subscribe it on Destroy view
   */
  public PublishSubject<JobDetail> subscribeItemClick() {
    return mJobItemClickListener;
  }

  /**
   * Send the run time permission update to the all observer by sending the message
   */
  public void sendRunTimePermissionUpdate(RunTimePermission runTimePermission) {
    runTimeSubject.onNext(runTimePermission);
  }

  /**
   * Start Listen the new update send by the RTM Manager
   */
  public PublishSubject<RunTimePermission> subscribeRunTimeChannel() {
    return runTimeSubject;
  }

  public PublishSubject<Applicant> subscribeUserItemClick() {
    return mUserClickListener;
  }

  public PublishSubject<Applicant> subscribeApplicantAccept() {
    return mApplicantListener;
  }

  public PublishSubject<Status> subRefreshQuestionerJob() {
    return refreshQuestionerJob;
  }

  public void notifyQuestionerJob(Status status) {
    refreshQuestionerJob.onNext(status);
  }

  public PublishSubject<Status> subRefreshAgentJob() {
    return refreshAgentJob;
  }

  public void notifyAgentJob(Status status) {
    refreshAgentJob.onNext(status);
  }
}
