package com.bm.library;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by q2366 on 2015/10/8.
 */
public class SwipeCloseUtils {
    public static void init(FragmentActivity act) {
        DecorLayout mDecorView = new DecorLayout(act);
        ViewGroup decorView = (ViewGroup) act.getWindow().getDecorView();

        int count = decorView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = decorView.getChildAt(0);
            decorView.removeViewAt(0);
            mDecorView.addView(child);
        }
        mDecorView.setId(R.id.decor);
        decorView.addView(mDecorView);
    }
}
