package com.android.unideal.questioner.questionhelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.unideal.R;

/**
 * Created by CS02 on 12/26/2016.
 */

public class AwardJobDialogHelper {

    private Activity mActivity;
    private float consignment;
    private JobListener jobListener;


    public AwardJobDialogHelper(Activity activity, float consignment) {
        this.mActivity = activity;
        this.consignment = consignment;
    }

    public void setJobListener(JobListener jobListener) {
        this.jobListener = jobListener;
    }

    public void showDialog() {
        if (mActivity == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage(R.string.award_job_message);
        builder.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showConsignmentSizeDialog();
            }
        });
        builder.setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onNextSteps(-1);
            }
        });
        builder.setNeutralButton(R.string.btn_cancel, null);
        builder.show();
    }

    private void showConsignmentSizeDialog() {
        if (mActivity == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View dialogView = inflater.inflate(R.layout.dialog_consignment_size, null, false);
        final EditText consignmentSize =
                (EditText) dialogView.findViewById(R.id.consignmentSizeEditText);

        consignmentSize.setText(
                mActivity.getString(R.string.budget_price, (consignment)));
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.btn_submit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showDialog();
            }
        });
        Dialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String consignmentString = consignmentSize.getText().toString().trim();
                        if (TextUtils.isEmpty(consignmentString)) {
                            Toast.makeText(mActivity, R.string.error_consignment_size, Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                float newConsignment = Float.parseFloat(consignmentString);
                                if (newConsignment == consignment) {
                                    Toast.makeText(mActivity, R.string.error_consignment_size_diff,
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    dialog.dismiss();
                                    onNextSteps(newConsignment);
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                Toast.makeText(mActivity, R.string.please_try_again, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private void onNextSteps(float consignmentSize) {
        if (jobListener != null) {
            if (consignmentSize == -1) {
                jobListener.onAppliedSuccess(-1);
            } else {
                jobListener.onAppliedSuccess(consignmentSize);
            }
        }
    }


    public interface JobListener {
        void onAppliedSuccess(float consignmentSize);
    }
}
