package com.android.unideal.util;

/**
 * Created by ADMIN on 19-10-2016.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;
import com.android.unideal.R;

/**
 * Activity Animation Utility Helper for open activity in animated form
 */
public class TransitionUtility {

    /**
     * Start activity from
     */
    public static void openRightToLeftTransition(Activity activity) {
        if (activity != null) {
            activity.overridePendingTransition(R.anim.activity_open_translate,
                R.anim.activity_close_scale);
        }
    }

    /**
     * Close activity animated from
     */
    public static void closeRightToLeftTransition(Activity activity) {
        //closing transition animations
        if (activity != null) {
            activity.overridePendingTransition(R.anim.activity_open_scale,
                R.anim.activity_close_translate);
        }
    }

    /**
     * Open activity sliding up from bottom to top position
     * Call this method onResume of your activity
     */
    public static void openBottomToTopTransition(Activity activity) {
        if (activity != null) {
            activity.overridePendingTransition(R.anim.slide_up, R.anim.stay_its);
        }
    }

    /**
     * Closing the activity from top to bottom without effect the existing
     * activity. Call this method inside your #onPause method of activity
     */
    public static void closeBottomToTopTransition(Activity activity) {
        if (activity != null) {
            activity.overridePendingTransition(R.anim.stay_its, R.anim.slide_down);
        }
    }

    public static int getScreenHeight(Context context) {
        WindowManager windowManager =
            (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }
}