package com.gigster.cardiograma.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gigster.cardiograma.Activities.MainActivity;
import com.gigster.cardiograma.R;

import net.yanzm.mth.MaterialTabHost;

import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {
    MainActivity activity;
    ViewPager pager;
    MaterialTabHost tabHost;
    ViewPagerAdapter adapter;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        activity.showActionBar();

        View viewFrag = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, viewFrag);
        tabHost = viewFrag.findViewById(R.id.tabHost);
        pager = viewFrag.findViewById(R.id.pager);
        tabHost.setType(MaterialTabHost.Type.FullScreenWidth);

        adapter = new ViewPagerAdapter(activity.getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(tabHost);

        // insert all tabs from pagerAdapter data
        for (int i = 0; i < adapter.getCount(); i++) {
            tabHost.addTab(adapter.getPageTitle(i));
        }

        tabHost.setOnTabChangeListener(new MaterialTabHost.OnTabChangeListener() {
            @Override
            public void onTabSelected(int position) {
                pager.setCurrentItem(position);
            }
        });
        for (int i = 0; i < tabHost.getTabWidget().getTabCount(); i++) {
            AppCompatTextView appCompatTextView = (AppCompatTextView) tabHost.getTabWidget().getChildAt(i);
            appCompatTextView.setTextColor(getResources().getColor(R.color.colorBrandRed));
            float aa = getResources().getDimension(R.dimen._12sdp);
            appCompatTextView.setTextSize(20.0f);
            appCompatTextView.setGravity(Gravity.CENTER);
            int topPadding = (int)getResources().getDimension(R.dimen._4sdp);
            appCompatTextView.setPadding(0, topPadding, 0,0);
        }






        return viewFrag;
    }

    @Override
    public  void onResume() {
        super.onResume();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                pager.setCurrentItem(0);
//            }
//        }, 1000);
        boolean is = isCurrentActive("", 0);
    }

    private boolean isCurrentActive(String hour, int nWeekday){


        return true;
    }


    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        public Fragment getItem(int num) {
            if (num == 0)
                return new MeasureFragment();
            else
                return new HistoryFragment();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0)
                return "START";
            else
                return "HISTORY";
        }

    }
}
