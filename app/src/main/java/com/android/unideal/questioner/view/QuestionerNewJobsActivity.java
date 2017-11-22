package com.android.unideal.questioner.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;

import com.android.unideal.R;
import com.android.unideal.agent.adapter.CategoryAdapter;
import com.android.unideal.data.Status;
import com.android.unideal.data.UserDetail;
import com.android.unideal.databinding.NewJobBinding;
import com.android.unideal.enums.AppMode;
import com.android.unideal.flurry.FlurryManager;
import com.android.unideal.questioner.viewmodel.NewJobsActivityViewModel;
import com.android.unideal.rest.RestFields;
import com.android.unideal.rest.response.Category;
import com.android.unideal.rest.response.GenericResponse;
import com.android.unideal.rest.response.NewJobResponse;
import com.android.unideal.rest.response.SubCategory;
import com.android.unideal.service.UploadingService;
import com.android.unideal.util.AppUtility;
import com.android.unideal.util.CategoryList;
import com.android.unideal.util.Communicator;
import com.android.unideal.util.Consts;
import com.android.unideal.util.DateTimeUtils;
import com.android.unideal.util.DialogUtils;
import com.android.unideal.util.ImagePicker;
import com.android.unideal.util.RunTimePermissionManager;
import com.android.unideal.util.SessionManager;
import com.android.unideal.util.datetimehelper.DateTimerHelper;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class QuestionerNewJobsActivity extends AppCompatActivity
        implements NewJobsActivityViewModel.NewJobsListener,
        RunTimePermissionManager.callbackRunTimePermission, ImagePicker.ImageListener,
        DateTimerHelper.onDateTimeListener {
    public final static int REQ_PROFILE_IMAGE = 222;
    public final static int REQ_PERMISSION_PROFILE_IMAGE = 907;// request code for runtime permission
    public final static int REQ_TERMS_AND_CONDITION = 666; // Terms and condition request code
    private final String TAG = QuestionerNewJobsActivity.class.getName();
    private NewJobBinding mBinding;
    private NewJobsActivityViewModel mViewModel;
    private RunTimePermissionManager mRunTimePermission;
    private DateTimerHelper mDateTimerHelper;
    private ImagePicker imagePicker;
    private CategoryAdapter<Category> mCategoryAdapter;
    private CategoryAdapter<SubCategory> mSubCategoryAdapter;
    private Category chooseCategory;
    private SubCategory subCategory;
    private UserDetail userDetail;
    private String JOB_INFO = "jobInfo";
    private String TNC_TYPE = "createJob";

    public static Intent startNewJobsActivity(Activity activity) {
        return new Intent(activity, QuestionerNewJobsActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_jobs_questioner);
        if (SessionManager.get(this).shouldShowTourGuide() == Consts.SHOWCASE_GUIDE_SHOW) {
            setIntroLayout();
        }
        mViewModel = new NewJobsActivityViewModel(this, this);
        mViewModel.onCreate();
    }

    private void setIntroLayout() {
        new MaterialIntroView.Builder(this).enableDotAnimation(true)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.ALL)
                .setShape(ShapeType.RECTANGLE)
                .enableIcon(false)
                .setDelayMillis(200)
                .dismissOnTouch(Consts.dismissOnTouch)
                .enableFadeAnimation(true)
                .performClick(false)
                .setInfoText(getString(R.string.txt_show_case_job_details))
                .setTarget(mBinding.jobDetails)
                .setUsageId(JOB_INFO) //THIS SHOULD BE UNIQUE ID
                .show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePicker.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_PROFILE_IMAGE: {
                imagePicker.onActivityResult(requestCode, resultCode, data);
                break;
            }
            case REQ_PERMISSION_PROFILE_IMAGE: {
                mRunTimePermission.onActivityResult(REQ_PERMISSION_PROFILE_IMAGE, requestCode, requestCode,
                        data);
                break;
            }
            case REQ_TERMS_AND_CONDITION: {
                if (resultCode == RESULT_OK) {
                    acceptedTermsNConditions();
                }
                break;
            }
        }
    }

    @Override
    public void onInitialization() {
        userDetail = SessionManager.get(QuestionerNewJobsActivity.this).getActiveUser();
        boolean showPromocode = SessionManager.get(this).getPromoCode();
        mBinding.promoCodeLayout.setVisibility(showPromocode ? View.VISIBLE : View.GONE);
        AppUtility.hideSoftKeyBoard(this, getCurrentFocus());//hide soft keyboard
        setUpImagePicker();//set up image picker
        setUpCategorySpinner();// Category Adapter
        setUpSubCategorySpinner();
        configureRunTimePermission();// RunTime Permission Handler
        mBinding.invoiceYes.setChecked(true);
        mDateTimerHelper = new DateTimerHelper(this, AppMode.QUESTIONER, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDateTimerHelper.closeAllDialog();
    }

    @Override
    public void startBindingViews() {
        RxView.clicks(mBinding.imageSelect).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                permissionRequestList();
            }
        });

        RxView.clicks(mBinding.postButton).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (!checkEmptyFields()) {
                    startActivityForResult(
                            DisclaimerActivity.getDialogActivity(QuestionerNewJobsActivity.this,
                                    AppMode.QUESTIONER, TNC_TYPE), REQ_TERMS_AND_CONDITION);
                }
            }
        });

        RxView.clicks(mBinding.calendar).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startShowCalendar();
            }
        });

        RxView.clicks(mBinding.clearButton).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                clearAllData();
            }
        });

        RxView.clicks(mBinding.imageViewBack).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                finish();
            }
        });
        RxTextView.afterTextChangeEvents(mBinding.promoCode)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<TextViewAfterTextChangeEvent>() {
                    @Override
                    public void call(TextViewAfterTextChangeEvent textViewAfterTextChangeEvent) {
                        if (textViewAfterTextChangeEvent.editable() != null) {
                            if (textViewAfterTextChangeEvent.editable().length()
                                    >= Consts.MIN_PROMOTIONAL_LENGTH) {
                                mBinding.btnVerify.setVisibility(View.VISIBLE);
                            } else {
                                mBinding.btnVerify.setVisibility(View.GONE);
                            }
                        }
                    }
                });
        RxView.clicks(mBinding.btnVerify).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                AppUtility.hideSoftKeyBoard(QuestionerNewJobsActivity.this,
                        getCurrentFocus());//hide soft keyboard
                if (userDetail != null) {
                    String promoCode = mBinding.promoCode.getText().toString().trim();
                    mViewModel.verifyPromotionalCode(userDetail.getUserId(), promoCode);
                }
            }
        });

        RxView.clicks(mBinding.btnClear).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                AppUtility.hideSoftKeyBoard(QuestionerNewJobsActivity.this,
                        getCurrentFocus());//hide soft keyboard
                if (userDetail != null) {
                    onRemovePromotionalCode();
                }
            }
        });
    }

    private void setUpCategorySpinner() {
        if (SessionManager.get(QuestionerNewJobsActivity.this) != null) {
            mCategoryAdapter = new CategoryAdapter<>(this, CategoryList.parentCategoryList(this));
            mBinding.category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    chooseCategory = mCategoryAdapter.getItem(position);
                    //clear all sub category item
                    mSubCategoryAdapter.clearAllItems();
                    Observable<SubCategory> resultsObjectObservable =
                            Observable.from(CategoryList.subCategory(QuestionerNewJobsActivity.this));
                    resultsObjectObservable.map(new Func1<SubCategory, SubCategory>() {
                        @Override
                        public SubCategory call(SubCategory subCategory) {
                            if (subCategory.getCategory_id() == -1
                                    || subCategory.getCategory_id() == chooseCategory.getCategoryId()) {
                                return subCategory;
                            }
                            return null;
                        }
                    }).doOnCompleted(new Action0() {
                        @Override
                        public void call() {
                            mBinding.subCategory.setSelection(0);
                        }
                    }).subscribe(new Action1<SubCategory>() {
                        @Override
                        public void call(SubCategory subCategory) {
                            if (subCategory != null) {
                                mSubCategoryAdapter.addItems(subCategory);
                                mSubCategoryAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            mBinding.category.setAdapter(mCategoryAdapter);
        }
    }

    private void setUpSubCategorySpinner() {
        mSubCategoryAdapter = new CategoryAdapter<>(this);
        mBinding.subCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subCategory = mSubCategoryAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mBinding.subCategory.setAdapter(mSubCategoryAdapter);
    }

    private void setUpImagePicker() {
        // initialization of image picker
        imagePicker = new ImagePicker(this, this, false);
    }

    private void clearAllData() {
        mBinding.completionDate.getText().clear();
        mBinding.jobTitle.getText().clear();
        mBinding.jobDetails.getText().clear();
        mBinding.consignmentSize.getText().clear();
        mBinding.promoCode.getText().clear();
        mBinding.imageLayout.removeAllViews();
        mBinding.category.setSelection(0);
        mBinding.subCategory.setSelection(0);
        //clear the cache all image path
        mViewModel.removeAllUploadingFile();
        mBinding.invoiceYes.setChecked(true);
    }

    private boolean checkEmptyFields() {
        if (chooseCategory.getCategoryId() == -1) {
            AppUtility.showToast(this, getString(R.string.select_category));
            return true;
        } else if (TextUtils.isEmpty(mBinding.jobTitle.getText().toString().trim())) {
            mBinding.jobTitle.getBackground()
                    .setColorFilter(getFieldColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
            return true;
        } else if (TextUtils.isEmpty(mBinding.jobDetails.getText().toString().trim())) {
            mBinding.jobDetails.getBackground()
                    .setColorFilter(getFieldColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
            return true;
        } else if (TextUtils.isEmpty(mBinding.completionDate.getText().toString().trim())) {
            mBinding.completionDate.getBackground()
                    .setColorFilter(getFieldColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
            return true;
        }
        return false;
    }

    public int getFieldColor(int colorId) {
        return ContextCompat.getColor(this, colorId);
    }

    private void configureRunTimePermission() {
        mRunTimePermission = new RunTimePermissionManager(this);
        mRunTimePermission.registerCallback(this);
    }

    /**
     * Handle the result of request for ask the permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mRunTimePermission.onRequestPermissionsResult(REQ_PERMISSION_PROFILE_IMAGE, permissions,
                grantResults);
    }

    /**
     * Entry point of asking the run time permission
     */

    private void permissionRequestList() {
        HashMap<String, String> permissionsMap = new HashMap<>();
        permissionsMap.put(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                getResources().getString(R.string.message_storage_permission));
        mRunTimePermission.buildPermissionList(permissionsMap, REQ_PERMISSION_PROFILE_IMAGE);
    }

    @Override
    public void showRationalDialog(String message, final int code) {
        DialogUtils.showDialog(this, R.string.title_app_name, message, R.string.btn_ok,
                R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mRunTimePermission.askRunTimePermissions(code);
                    }
                });
    }

    @Override
    public void deniedPermission(String deniedPermission, int code) {
    }

    @Override
    public void requestAllPermissionGranted(int requestCode) {
        imagePicker.openDialog(REQ_PROFILE_IMAGE);
    }

    @Override
    public void onImagePick(final int reqCode, final String filePath) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (reqCode == REQ_PROFILE_IMAGE) {
                    mBinding.imageLayout.addView(attachedImage(filePath));
                    //update model by attaching file path
                    //mode we track of uploading file
                    mViewModel.addFileForUploading(filePath);
                }
            }
        });
    }

    private View attachedImage(String path) {
        final View closeLayout = getLayoutInflater().inflate(R.layout.layout_close_image, null);
        ImageView imageView = (ImageView) closeLayout.findViewById(R.id.imageSelect);
        ImageView imageClose = (ImageView) closeLayout.findViewById(R.id.imageClose);
        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mBinding.imageLayout.indexOfChild(closeLayout);
                mBinding.imageLayout.removeView(closeLayout);
                if (position != -1) {
                    mViewModel.removeConfigFile(position);
                }
            }
        });
        Picasso picasso = Picasso.with(QuestionerNewJobsActivity.this);
        picasso.load(new File(path)).into(imageView);
        return closeLayout;
    }

    @Override
    public void onError(String s) {
    }

    private void startShowCalendar() {
        mDateTimerHelper.showCalendar(0);
    }

    @Override
    public void onDateSet() {
        String responseDateTime = mDateTimerHelper.getChooseDateTimeInFormat(DateTimeUtils.DATE_FORMAT);
        if (!TextUtils.isEmpty(responseDateTime)) {
            mBinding.completionDate.setText(responseDateTime);
        }
    }

    private void acceptedTermsNConditions() {
        requestForCreateNewJob();//start creating a new job
    }

    private void requestForCreateNewJob() {
//    String defaultCard = getDefaultCard();
//    if (TextUtils.isEmpty(defaultCard)) {
//      DialogUtils.showDialog(this, R.string.error_default_card);
//    } else {
//      mViewModel.createNewJob(collectDataForCreateJob());//start creating a new job
//    }
//
        mViewModel.createNewJob(collectDataForCreateJob());//start creating a new job
    }

    private String getDefaultCard() {
        if (userDetail == null) {
            return null;
        } else {
            return userDetail.getDefault_card();
        }
    }

    private HashMap<String, Object> collectDataForCreateJob() {

        HashMap<String, Object> params = new HashMap<>();
        //1 User Type
        params.put(RestFields.KEY_USER_TYPE, RestFields.USER_TYPE_REQUESTER);
        //2 user ID
        params.put(RestFields.KEY_USER_ID, userDetail.getUserId());
        //3.Category Id
        if (chooseCategory != null) {
            params.put(RestFields.KEY_CATEGORY_ID, chooseCategory.getCategoryId());
        }
        //4.sub category Id
        if (subCategory != null) {
            params.put(RestFields.KEY_SUB_CATEGORY_ID, subCategory.getSub_category_id());
        }
        //4.Job Title
        params.put(RestFields.KEY_JOB_TITLE, mBinding.jobTitle.getText());
        //5.Job Details
        params.put(RestFields.KEY_JOB_DETAILS, mBinding.jobDetails.getText());
        //6.consignment size
        params.put(RestFields.KEY_CONSIGNMENT_SIZE, mBinding.consignmentSize.getText());
        //8.completion time
        //before we send into back end convert into the UTC format
        String jobEndDate = mBinding.completionDate.getText().toString();
        if (!TextUtils.isEmpty(jobEndDate)) {
            String utcDate = DateTimeUtils.convertToUTCDate(jobEndDate, DateTimeUtils.DATE_FORMAT);
            Log.d(TAG, "Job Complete Date:" + utcDate);
            params.put(RestFields.KEY_JOB_COMPLETE_DATE, utcDate);
        }
        params.put(RestFields.KEY_PROMOTIONAL_CODE, mBinding.promoCode.getText());
        int invoice = mBinding.invoiceYes.isChecked() ? 1 : 0;
        params.put(RestFields.KEY_IS_INVOICE, invoice);
        //track creating a new job request
        FlurryManager.startCreateJob(userDetail.getUserId(), mBinding.jobTitle.getText().toString());
        return params;
    }

    @Override
    public void showProgressDialog(boolean show) {
        if (show) {
            DialogUtils.getInstance().showProgressDialog(this);
        } else {
            DialogUtils.getInstance().hideProgressDialog();
        }
    }

    @Override
    public void jobCreatedSuccessful(GenericResponse<NewJobResponse> newJobResponse) {
        AppUtility.showToast(QuestionerNewJobsActivity.this, newJobResponse.getMessage());
        //If a job is created successfully next step is to start uploading service
        //This will extract the Job Id from the input Job Response
        mViewModel.startUploadingAttachedFiles(newJobResponse.getData());
        Communicator.getCommunicator().notifyQuestionerJob(Status.OPEN);
        Communicator.getCommunicator().notifyQuestionerJob(Status.IN_PROCESS);

        FlurryManager.stopCreateJob(newJobResponse.getData().getUserId(),
                newJobResponse.getData().getJobId(), newJobResponse.getData().getJobTitle());
    }

    @Override
    public void startedUploadingJob() {
        //start uploading service
        Intent uploadingIntent = UploadingService.getServiceIntent(this);
        startService(uploadingIntent);
        finish();
    }

    @Override
    public void failToCreateJob(String errorMessage) {
        DialogUtils.showDialog(this, errorMessage, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Communicator.getCommunicator().notifyQuestionerJob(Status.OPEN);
                Communicator.getCommunicator().notifyQuestionerJob(Status.IN_PROCESS);
                finish();
            }
        });
    }

    @Override
    public void retryRequestCall(String errorMessage) {
        DialogUtils.showDialog(this, errorMessage, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestForCreateNewJob();
            }
        });
    }

    @Override
    public void onPromotionalCode(String message) {
        //disable edit text
        mBinding.promoCode.setEnabled(false);
        //Hide verify button
        mBinding.btnVerify.setVisibility(View.GONE);
        //show remove promo code button
        mBinding.btnClear.setVisibility(View.VISIBLE);
        // if message is available then show it
        if (!TextUtils.isEmpty(message)) {
            mBinding.editTextMessage.setText(message);
        }
        mBinding.editTextMessage.setTextColor(
                ContextCompat.getColor(getApplicationContext(), R.color.colorPersianGreen));
        mBinding.editTextMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRemovePromotionalCode() {
        //enable edit text
        mBinding.promoCode.setEnabled(true);
        //hide both button
        mBinding.btnVerify.setVisibility(View.GONE);
        mBinding.btnClear.setVisibility(View.GONE);
        //clear edit text and message
        mBinding.editTextMessage.setVisibility(View.GONE);
        mBinding.promoCode.getText().clear();
        mBinding.promoCode.getBackground()
                .mutate()
                .setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorSantasGray),
                        PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void failPromotionalCode(String errorMessage) {
        //Enable edit text
        mBinding.promoCode.setEnabled(false);
        // hide verify button
        mBinding.btnVerify.setVisibility(View.GONE);
        mBinding.btnClear.setVisibility(View.VISIBLE);
        // if error message is present then show it
        if (!TextUtils.isEmpty(errorMessage)) {
            mBinding.editTextMessage.setText(errorMessage);
        }
        mBinding.editTextMessage.setTextColor(
                ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
        mBinding.promoCode.getBackground()
                .mutate()
                .setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorRed),
                        PorterDuff.Mode.SRC_ATOP);
        mBinding.editTextMessage.setVisibility(View.VISIBLE);
    }
}
