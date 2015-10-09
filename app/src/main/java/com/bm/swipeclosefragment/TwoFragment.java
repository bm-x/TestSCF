package com.bm.swipeclosefragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import com.bm.library.SwipeCloseFragment;
import com.bm.library.SwipeCloseLayout;

/**
 * Created by bm on 15/10/9.
 */
public class TwoFragment extends SwipeCloseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, SwipeCloseLayout container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_two, null);
        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        view.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new TestFgm());
            }
        });
        return view;
    }
}
