package com.gigster.cardiograma.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.gigster.cardiograma.Activities.MainActivity;
import com.gigster.cardiograma.Fragments.Adapters.HistoryAdapter;
import com.gigster.cardiograma.Models.ChangedValueMsg;
import com.gigster.cardiograma.Models.HeartBeatMsg;
import com.gigster.cardiograma.Models.HistoryHeartData;
import com.gigster.cardiograma.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * A placeholder fragment containing a simple view.
 */
public class HistoryFragment extends Fragment {

    @Bind(R.id.listView)
    ListView listView;
    HistoryAdapter adapter;

//    MainActivity activity;

    List<HistoryHeartData> listHistoryDate;

    public static int nFirstVisibleItem = 0;
    public static int nVisibleCount = 3;

    public HistoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View frag = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, frag);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                nFirstVisibleItem = firstVisibleItem;
                nVisibleCount = visibleItemCount;
            }
        });
        return frag;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    listHistoryDate = loadData();
                    adapter = new HistoryAdapter(listHistoryDate, (MainActivity)getActivity(), getActivity().getApplicationContext());
                    listView.setAdapter(adapter);
                }
            },300);
        }else{
            Log.d("HistoryFrag", "OnUserVisibleHint false");
        }
    }

    List<HistoryHeartData> loadData(){
        List<HistoryHeartData> queryResults = new Select().from(HistoryHeartData.class).orderBy("saved_date DESC")
                .limit(30).execute();
        return queryResults;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(ChangedValueMsg event) {
        if (event.changed){
            adapter.notifyDataSetChanged();
        }
    }



}
