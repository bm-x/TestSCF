package com.bm.library;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * Created by q2366 on 2015/10/8.
 */
public class DecorLayout extends FrameLayout {

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

    private void init(){
        setFocusableInTouchMode(true);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.i("bm", "onKeyUp: 5");
        super.onKeyUp(keyCode, event);
        return true;
    }
}
