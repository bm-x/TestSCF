package com.bm.swipeclosefragment;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bm.library.SwipeCloseFragment;
import com.bm.library.SwipeCloseLayout;

/**
 * Created by bm on 15/10/6.
 */
public class TestFgm extends SwipeCloseFragment {

    private ViewPager mViewPager;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewPager = (ViewPager) getView().findViewById(R.id.pager);

        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 6;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                TextView tv = new TextView(getActivity());
                tv.setText(String.valueOf(position));
                tv.setGravity(Gravity.CENTER);
                tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                container.addView(tv);
                return tv;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, SwipeCloseLayout container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, null);
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        view.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new TwoFragment());
            }
        });
        return view;
    }
}
