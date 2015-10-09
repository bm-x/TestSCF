package com.bm.library;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by q2366 on 2015/10/8.
 */
public class SwipeCloseUtils {
    public static void init(final FragmentActivity act) {
        DecorLayout mDecorView = new DecorLayout(act);
        ViewGroup content = (ViewGroup) act.findViewById(android.R.id.content);
        ViewGroup parent = (ViewGroup) content.getParent();

        while (!(parent instanceof FrameLayout)) {
            parent = (ViewGroup) parent.getParent();
        }

        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = parent.getChildAt(0);
            parent.removeViewAt(0);
            mDecorView.addView(child);
        }
        mDecorView.setId(R.id.decor);
        parent.addView(mDecorView);
    }
}
