package com.gigster.cardiograma.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.gigster.cardiograma.Activities.MainActivity;
import com.gigster.cardiograma.Fragments.Adapters.HistoryAdapter;
import com.gigster.cardiograma.Models.GConstants;
import com.gigster.cardiograma.Models.HistoryHeartData;
import com.gigster.cardiograma.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class HistoryFragment extends Fragment {

    @Bind(R.id.listView)
    ListView listView;
    HistoryAdapter adapter;

    MainActivity activity;


    public HistoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View frag = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, frag);

        activity = (MainActivity)this.activity;

        return frag;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapter = new HistoryAdapter(loadData(), getActivity().getApplicationContext());
                    listView.setAdapter(adapter);
                }
            },300);
        }
    }


    @Override
    public  void onResume(){
        super.onResume();
        Log.i("History", "onResume");
    }

    List<HistoryHeartData> loadData(){
        List<HistoryHeartData> queryResults = new Select().from(HistoryHeartData.class).orderBy("saved_date DESC")
                .limit(30).execute();
        return queryResults;
    }


}
