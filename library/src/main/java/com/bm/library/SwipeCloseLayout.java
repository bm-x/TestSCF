package com.bm.library;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * Created by q2366 on 2015/10/4.
 */
public class SwipeCloseLayout extends FrameLayout {

    public interface OnCloseListener {
        void onClose();
    }

    private GestureDetector mDetector;
    private Scroller mScroller;
    private OnCloseListener mCloseListener;

    private boolean callBackFlag;
    private boolean handleFling;

    private int mWidth;
    private int mHalfWidth;

    private int mScrollX;
    private int mScrollY;

    public SwipeCloseLayout(Context context) {
        super(context);
        init(context);
    }

    public SwipeCloseLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SwipeCloseLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context ctx) {
        mDetector = new GestureDetector(ctx, mListener);
        mScroller = new Scroller(ctx);
    }

    public void setOnCloseListener(OnCloseListener l) {
        mCloseListener = l;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int x = mScroller.getCurrX();
            if (x > 0) x = -x;
            scrollTo(x, 0);
            invalidate();
        } else {
            if (mCloseListener != null && -mScrollX == mWidth && !callBackFlag) {
                mCloseListener.onClose();
                callBackFlag = true;
            }
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        mScrollX = x;
        mScrollY = y;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHalfWidth = w / 2;
        mWidth = w;
    }

    public GestureDetector.OnGestureListener mListener = new GestureDetector.SimpleOnGestureListener() {

        boolean isFirstScroll;
        boolean canScroll;

        @Override
        public boolean onDown(MotionEvent e) {
            mScroller.abortAnimation();
            isFirstScroll = true;
            canScroll = false;
            handleFling = false;
            callBackFlag = false;
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (canScroll) return false;
            if (Math.abs(velocityX) < 400) return false;
            handleFling = true;
            if (velocityX > 0) {
                mScroller.startScroll(mScrollX, 0, -mWidth - mScrollX, 0);
            } else {
                mScroller.startScroll(mScrollX, 0, -mScrollX, 0);
            }
            invalidate();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            if (isFirstScroll) {
                canScroll = canScroll(SwipeCloseLayout.this, false, (int) distanceX, (int) e2.getX(), (int) e2.getY());
                Log.i("bm", "onScroll: " + canScroll);
                isFirstScroll = false;
            }

            if (!canScroll) {
                float deltaX = distanceX;

                if (mScrollX + deltaX > 0) {
                    deltaX = -mScrollX;
                }

                scrollBy((int) deltaX, mScrollY);
            }

            return !canScroll;
        }
    };

    public void onUp(MotionEvent ev) {
        Log.i("bm", "onUp: ");
        if (Math.abs(mScrollX) > mHalfWidth) {
            mScroller.startScroll(mScrollX, 0, -mWidth - mScrollX, 0);
        } else {
            mScroller.startScroll(mScrollX, 0, -mScrollX, 0);
        }

        invalidate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean handle = mDetector.onTouchEvent(ev);

        final int Action = ev.getAction();

        if (!handleFling && (Action == MotionEvent.ACTION_UP || Action == MotionEvent.ACTION_CANCEL)) {
            onUp(ev);
        }

        if (!handle) super.dispatchTouchEvent(ev);
        return true;
    }

    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof ViewGroup) {
            final ViewGroup group = (ViewGroup) v;
            final int scrollX = v.getScrollX();
            final int scrollY = v.getScrollY();
            final int count = group.getChildCount();
            for (int i = count - 1; i >= 0; i--) {
                final View child = group.getChildAt(i);
                if (x + scrollX >= child.getLeft() && x + scrollX < child.getRight() &&
                        y + scrollY >= child.getTop() && y + scrollY < child.getBottom() &&
                        canScroll(child, true, dx, x + scrollX - child.getLeft(),
                                y + scrollY - child.getTop())) {
                    return true;
                }
            }
        }

        return checkV && ViewCompat.canScrollHorizontally(v, -dx);
    }
}
