package com.bm.library;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * Created by q2366 on 2015/10/4.
 */
public class SwipeCloseLayout extends FrameLayout {

    public interface OnCloseListener {
        void onClose();
    }

    private Scroller mScroller;
    private OnCloseListener mCloseListener;
    private VelocityTracker mVelocityTracker;
    private DecorLayout mDecorLayout;
    private SwipeCloseFragment mFragment;
    private View mPreviousView;
    private Interpolator mInterpolator = new DecelerateInterpolator(0.7f);

    private boolean callBackFlag;
    private boolean handleFling;
    private boolean mAlwaysInTapRegion;

    private int mMaximumFlingVelocity;
    private int mTouchSlopSquare;

    private int mWidth;
    private int mHalfWidth;

    private int mScrollX;
    private int mScrollY;

    private float mDownX;
    private float mDownY;
    private float mLastX;
    private float mLastY;

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
        mScroller = new Scroller(ctx, mInterpolator);

        final ViewConfiguration configuration = ViewConfiguration.get(ctx);
        mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();

        int touchSlop = configuration.getScaledTouchSlop();
        mTouchSlopSquare = touchSlop * touchSlop;
    }

    public void setOnCloseListener(OnCloseListener l) {
        mCloseListener = l;
    }

    public void setDecorLayout(DecorLayout decorLayout) {
        mDecorLayout = decorLayout;
    }

    public void setFragment(SwipeCloseFragment fgm) {
        mFragment = fgm;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int x = mScroller.getCurrX();
            scrollTo(x, 0);
            scrollPreviousView();
            invalidate();
        } else {
            if (mCloseListener != null && -mScrollX == mWidth && !callBackFlag) {
                mCloseListener.onClose();
                callBackFlag = true;
            }
            if (mScrollX == 0 && mPreviousView != null) mPreviousView.scrollTo(0, 0);
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
        mPreviousView = mDecorLayout.getPreviousView();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        addVelocityTrackerEvent(event);

        final int Action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();

        switch (Action) {
            case MotionEvent.ACTION_DOWN:
                mAlwaysInTapRegion = true;
                mDownX = mLastX = x;
                mDownY = mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:

                if (mAlwaysInTapRegion) {

                    final int moveX = (int) (x - mDownX);
                    final int moveY = (int) (y - mDownY);
                    int distance = (moveX * moveX) + (moveY * moveY);

                    if (distance > mTouchSlopSquare) {
                        mAlwaysInTapRegion = false;
                    }
                } else {
                    final float deltaX = x - mLastX;

                    if (!canScroll(this, false, deltaX, (int) x, (int) y) || mScrollX != 0) {
                        return true;
                    }
                }

                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                recycleVelocityTracker();
                break;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        addVelocityTrackerEvent(event);

        final int Action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();

        switch (Action) {
            case MotionEvent.ACTION_MOVE:

                final float deltaX = x - mLastX;

                if (!canScroll(this, false, deltaX, (int) x, (int) y) || mScrollX != 0) {
                    doScrollBy(-deltaX);
                }

                mLastX = x;

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:

                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
                final float velocityX = velocityTracker.getXVelocity();
                final float velocityY = velocityTracker.getYVelocity();

                if (Math.abs(velocityX) > Math.abs(velocityY) && Math.abs(velocityX) > 450) {
                    onFling(velocityX);
                } else {
                    onUp(event);
                }

                recycleVelocityTracker();
                break;
        }

        return true;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    private void onFling(float velocityX) {
        if (velocityX > 0) {
            mScroller.startScroll(mScrollX, 0, -mWidth - mScrollX, 0);
        } else {
            mScroller.startScroll(mScrollX, 0, -mScrollX, 0);
        }
        invalidate();
    }

    private void onUp(MotionEvent ev) {
        if (mScrollX > -mHalfWidth) {
            mScroller.startScroll(mScrollX, 0, -mScrollX, 0);
        } else {
            mScroller.startScroll(mScrollX, 0, -mWidth - mScrollX, 0);
        }

        invalidate();
    }

    private void doScrollBy(float x) {
        scrollBy((int) x, 0);
        scrollPreviousView();
    }

    private void scrollPreviousView() {
        float scrollPercent = (float) mScrollX / mWidth;
        int pScroll = (int) (mHalfWidth + mHalfWidth * scrollPercent);
        mPreviousView.scrollTo(pScroll, 0);
    }

    @Override
    public void scrollBy(int x, int y) {
        if (x > 0 && mScrollX + x >= 0) x = -mScrollX;
        if (x < 0 && mScrollX + x <= -mWidth) x = -mWidth - mScrollX;
        super.scrollBy(x, y);
    }

    private void addVelocityTrackerEvent(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    protected boolean canScroll(View v, boolean checkV, float dx, int x, int y) {
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

        return checkV && ViewCompat.canScrollHorizontally(v, (int) -dx);
    }
}