package com.bm.library;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

/**
 * Created by q2366 on 2015/10/4.
 */
public abstract class SwipeCloseFragment extends Fragment {

    private String TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());

    private Animation mIn = null;
    private Animation mOut = null;
    private Animation mPopIn = null;
    private Animation mPopOut = null;

    private Interpolator mInterpolator = new DecelerateInterpolator(0.7f);

    private int mEnterAnim = -1;
    private int mExitAnim = -1;
    private int mPopEnterAnim = -1;
    private int mPopExitAnim = -1;

    private SwipeCloseLayout mSwipeCloseLayout;
    private DecorLayout mDecorLayout;
    private FragmentActivity mAct;

    private boolean isFirstAttach;
    private boolean isRemoving;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        isFirstAttach = true;

        mDecorLayout = (DecorLayout) getActivity().findViewById(R.id.decor);
        mAct = getActivity();
        mDecorLayout.addFragment(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isFirstAttach = false;
        mDecorLayout.removeFragment(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        SwipeCloseLayout layout = new SwipeCloseLayout(mAct);
        layout.setDecorLayout(mDecorLayout);
        layout.setOnCloseListener(mCloseListener);

        View view = onCreateView(inflater, layout, savedInstanceState);
        layout.addView(view);

        mSwipeCloseLayout = layout;
        startInAnimaIfNeed();

        return layout;
    }

    /**
     * 必须重写该方法返回设置Fragment的View，请不要重写原本的onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
     * 注意：仅第二个参数同
     */
    public abstract View onCreateView(LayoutInflater inflater, SwipeCloseLayout container, Bundle saveInstanceState);

    private void ensureEnterAnim() {
        if (mEnterAnim == -1) mEnterAnim = R.anim.sc_n_in;
        if (mExitAnim == -1) mExitAnim = R.anim.sc_n_out;

        Context ctx = getContext();
        if (ctx == null) return;

        if (mEnterAnim != 0) mIn = AnimationUtils.loadAnimation(ctx, mEnterAnim);
        else mIn = new AnimationSet(true);
        if (mExitAnim != 0) mOut = AnimationUtils.loadAnimation(ctx, mExitAnim);
        else mOut = new AnimationSet(true);

        mIn.setInterpolator(mInterpolator);
        mOut.setInterpolator(mInterpolator);
    }

    private void ensurePopAnim() {
        if (mPopEnterAnim == -1) mPopEnterAnim = R.anim.sc_p_in;
        if (mPopExitAnim == -1) mPopExitAnim = R.anim.sc_p_out;

        Context ctx = getContext();
        if (ctx == null) return;

        if (mPopEnterAnim != 0) mPopIn = AnimationUtils.loadAnimation(ctx, mPopEnterAnim);
        else mPopIn = new AnimationSet(true);
        if (mPopExitAnim != 0) mPopOut = AnimationUtils.loadAnimation(ctx, mPopExitAnim);
        else mPopOut = new AnimationSet(true);

        mPopOut.setAnimationListener(mAnimFinshListener);
        mPopIn.setInterpolator(mInterpolator);
        mPopOut.setInterpolator(mInterpolator);
    }

    /**
     * 指定Fragment打开关闭的动画
     *
     * @param enterAnim    当前Fragment进入的动画
     * @param exitAnim     上一个界面退出的动画
     * @param popEnterAnim 当结束的上一个界面进入的动画
     * @param popExitAnim  当结束的时候当前Fragment退出的动画
     */
    public void overridePendingTransition(int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        mEnterAnim = enterAnim;
        mExitAnim = exitAnim;
        mPopEnterAnim = popEnterAnim;
        mPopExitAnim = popExitAnim;
    }

    public void start(FragmentActivity act) {
        act.getSupportFragmentManager().beginTransaction().add(R.id.decor, this).commit();
    }

    public void startFragment(SwipeCloseFragment fgm) {
        mAct.getSupportFragmentManager().beginTransaction().add(R.id.decor, fgm).commit();
    }

    /**
     * 结束并且移除当前Fragment，该方法会带有默认的页面关闭动画效果，可通过overridePendingTransition()自定义动画
     */
    public void finish() {
        FragmentActivity act = getActivity();
        if (act == null || isRemoving) return;
        removeFragmentAfterAnima();
    }

    /**
     * 立即移除当前Fragment，不带有任何的动画效果
     */
    public void finishAtOnce() {
        FragmentActivity act = getActivity();
        if (act == null) return;
        isRemoving = false;
        act.getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    private void removeFragmentAfterAnima() {
        isRemoving = true;
        ensurePopAnim();
        mSwipeCloseLayout.startAnimation(mPopOut);

        DecorLayout decor = (DecorLayout) getActivity().findViewById(R.id.decor);
        int count = decor.getChildCount();
        if (count >= 2) decor.getChildAt(count - 2).startAnimation(mPopIn);
    }

    private void startInAnimaIfNeed() {
        if (isFirstAttach) {
            ensureEnterAnim();
            mSwipeCloseLayout.startAnimation(mIn);

            DecorLayout decor = (DecorLayout) getActivity().findViewById(R.id.decor);
            int count = decor.getChildCount();
            if (count > 0) decor.getChildAt(count - 1).startAnimation(mOut);
        }
    }

    /**
     *  跟Activity一样，当按下返回键的适合会调用改方，
     *  当继承SwipeCloseFragment重写改方法的时候，调用super会默认返回true，并且会结束当前Fragment
     *
     * @return 返回true则代表你需要处理返回键，此时在Activity中onKeyUp,或者OnBackPressed()方法将不会被调用
     *  返回false则不需要处理，Activity中KeyUp,onBackPressed将会被调用
     */
    public boolean onBackPressed() {
        finish();
        return true;
    }

    private Animation.AnimationListener mAnimFinshListener = new Animation.AnimationListener() {
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            finishAtOnce();
        }

        public void onAnimationRepeat(Animation animation) {
        }
    };

    private SwipeCloseLayout.OnCloseListener mCloseListener = new SwipeCloseLayout.OnCloseListener() {
        @Override
        public void onClose() {
            finishAtOnce();
        }
    };
}
