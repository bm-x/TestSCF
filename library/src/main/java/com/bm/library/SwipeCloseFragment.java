package com.bm.library;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by q2366 on 2015/10/4.
 */
public class SwipeCloseFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        SwipeRefreshLayout layout = new SwipeRefreshLayout(getContext());
        layout.addView(view);
        return view;
    }
}
