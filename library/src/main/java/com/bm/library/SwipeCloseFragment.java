package com.bm.library;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by q2366 on 2015/10/4.
 */
public abstract class SwipeCloseFragment extends Fragment {

    private String TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SwipeCloseLayout layout = new SwipeCloseLayout(getContext());
        layout.setOnCloseListener(mCloseListener);
        View view = onCreateView(inflater, layout, savedInstanceState);
        layout.addView(view);
        return layout;
    }

    public abstract View onCreateView(LayoutInflater inflater, SwipeCloseLayout container, Bundle saveInstanceState);


    public void startFragment(FragmentActivity act) {
        View content = act.findViewById(android.R.id.content);

        if (content != null) {
            act.getSupportFragmentManager().beginTransaction().add(android.R.id.content, this).addToBackStack(TAG).commit();
        }
    }

    private SwipeCloseLayout.OnCloseListener mCloseListener = new SwipeCloseLayout.OnCloseListener() {
        @Override
        public void onClose() {
            getActivity().getSupportFragmentManager().beginTransaction().remove(SwipeCloseFragment.this).commit();
        }
    };
}
