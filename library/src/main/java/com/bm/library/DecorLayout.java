package com.bm.library;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.LinkedList;

/**
 * Created by q2366 on 2015/10/8.
 */
public class DecorLayout extends FrameLayout {

    private LinkedList<SwipeCloseFragment> mFgms = new LinkedList<>();

    public DecorLayout(Context context) {
        super(context);
        init();
    }

    public DecorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DecorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setFocusableInTouchMode(true);
    }

    public void addFragment(SwipeCloseFragment fgm) {
        mFgms.add(fgm);
    }

    public void removeFragment(SwipeCloseFragment fgm) {
        int count = getChildCount();

        for (int i = count - 1; i >= 0; i--) {
            SwipeCloseFragment f = mFgms.get(i);

            if (!f.isAdded()) {
                mFgms.remove(i);
                continue;
            }

            if (f == fgm) {
                mFgms.remove(i);
                break;
            }
        }
    }

    /**
     * 获取相对于当前界面的上一个界面
     *
     * @return 上一个界面的View
     */
    public View getPreviousView() {
        final int count = getChildCount();
        if (count < 2) return null;
        return getChildAt(count - 2);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_UP
                && !mFgms.isEmpty()
                && mFgms.get(mFgms.size() - 1).onBackPressed()) {
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }
}
