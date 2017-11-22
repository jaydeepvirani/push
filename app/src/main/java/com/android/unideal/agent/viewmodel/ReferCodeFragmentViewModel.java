package com.android.unideal.agent.viewmodel;

import android.content.Context;

/**
 * Created by MRUGESH on 10/8/2016.
 */

public class ReferCodeFragmentViewModel {
    private ReferCodeListener mListener;
    private Context context;

    public ReferCodeFragmentViewModel(Context context, ReferCodeListener mListener) {
        this.context = context;
        this.mListener = mListener;
    }

    public interface ReferCodeListener {

    }
}
