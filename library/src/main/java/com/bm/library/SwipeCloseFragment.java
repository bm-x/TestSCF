package com.bm.library;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
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

    private int mEnterAnim = -1;
    private int mExitAnim = -1;
    private int mPopEnterAnim = -1;
    private int mPopExitAnim = -1;

    private SwipeCloseLayout mSwipeCloseLayout;

    private boolean isFirstAttach;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        isFirstAttach = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isFirstAttach = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SwipeCloseLayout layout = new SwipeCloseLayout(getContext());
        layout.setOnCloseListener(mCloseListener);
        View view = onCreateView(inflater, layout, savedInstanceState);
        layout.addView(view);
        mSwipeCloseLayout = layout;
        startViewInAnimaIfNeed();
        return layout;
    }

    private void startViewInAnimaIfNeed() {
        if (isFirstAttach) {
            ensureEnterAnim();
            mSwipeCloseLayout.startAnimation(mIn);

            DecorLayout decor = (DecorLayout) getActivity().findViewById(R.id.decor);
            int count = decor.getChildCount();
            if (count > 0) decor.getChildAt(count - 1).startAnimation(mOut);
        }
    }

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

    }

    public void overridePendingTransition(int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        mEnterAnim = enterAnim;
        mExitAnim = exitAnim;
        mPopEnterAnim = popEnterAnim;
        mPopExitAnim = popExitAnim;
    }

    public void startFragment(FragmentActivity act) {
        ViewGroup content = (ViewGroup) act.findViewById(android.R.id.content);

        if (content != null) {
            act.getSupportFragmentManager().beginTransaction().add(R.id.decor, this).commit();
    }
    }

    private SwipeCloseLayout.OnCloseListener mCloseListener = new SwipeCloseLayout.OnCloseListener() {
        @Override
        public void onClose() {
            getActivity().getSupportFragmentManager().beginTransaction().remove(SwipeCloseFragment.this).commit();
        }
    };
}
