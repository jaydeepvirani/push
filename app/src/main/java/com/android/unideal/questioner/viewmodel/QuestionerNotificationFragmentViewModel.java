package com.android.unideal.questioner.viewmodel;

import android.content.Context;

/**
 * Created by MRUGESH on 10/16/2016.
 */

public class QuestionerNotificationFragmentViewModel {
    private Context context;
    private QuestionerNotificationListener mListener;

    public QuestionerNotificationFragmentViewModel(Context context,QuestionerNotificationListener mListener)
    {
        this.context = context;
        this.mListener = mListener;
    }

    public interface QuestionerNotificationListener
    {

    }
}
