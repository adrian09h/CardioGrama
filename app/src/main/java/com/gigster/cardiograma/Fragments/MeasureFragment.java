package com.gigster.cardiograma.Fragments;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.gigster.cardiograma.Activities.MainActivity;
import com.gigster.cardiograma.Models.GConstants;
import com.gigster.cardiograma.Models.HeartBeatDetected;
import com.gigster.cardiograma.Models.HeartBeatMsg;
import com.gigster.cardiograma.Models.HistoryHeartData;
import com.gigster.cardiograma.R;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * A placeholder fragment containing a simple view.
 */
public class MeasureFragment extends Fragment {
    @Bind(com.gigster.cardiograma.R.id.graph2)
    GraphView graph2;
    @Bind(R.id.imgvStart)
    ImageView imgvStart;
    @Bind(R.id.relHint)
    RelativeLayout relHint;
    @Bind(R.id.relGraph)
    RelativeLayout relGraph;
    @Bind(R.id.txtHeartBeat)
    TextView txtHeartBeat;
    @Bind(R.id.txtbpm)
    TextView txtbpm;
    @Bind(R.id.relHintMeausre)
    RelativeLayout relHintMeasure;
    @Bind(R.id.relStartButton)
    RelativeLayout relStartButton;
    @Bind(R.id.relSaveMode)
    RelativeLayout relSaveMode;
    @Bind(R.id.imgvRest)
    ImageView imgvRest;
    @Bind(R.id.imgvWarmup)
    ImageView imgvWarmUp;
    @Bind(R.id.imgvCardio)
    ImageView imgvCardio;
    @Bind(R.id.imgvExtreme)
    ImageView imgvExtreme;
    @Bind(R.id.txtHeartBeat_SaveMode)
    TextView txtHeartBeatSaveMode;


    final String TAG = "MeasureFrag";
    MainActivity activity;

//    private static final Random RANDOM = new Random();
//    private LineGraphSeries<DataPoint> series;
//    private int lastX = 0;

    private final Handler mHandler = new Handler();
    private Runnable mTimer2;

    private LineGraphSeries<DataPoint> mSeries2;
    private double graph2LastXValue = 0d;
    public boolean isHeartBeatDetected = false;
    private int nCount = 0;

    private String motion_state;

    public static enum WORKING_MODE {
        Hint_Mode, Measuring_Mode, Save_Mode
    }

    private WORKING_MODE working_mode = WORKING_MODE.Hint_Mode;

    public MeasureFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View frag = inflater.inflate(R.layout.fragment_measure, container, false);
        ButterKnife.bind(this, frag);
        activity = (MainActivity) this.getActivity();
        mSeries2 = new LineGraphSeries<DataPoint>();
        mSeries2.setColor(Color.RED);
        graph2.addSeries(mSeries2);
        graph2.getViewport().setXAxisBoundsManual(true);
        graph2.getViewport().setMinX(0);
        graph2.getViewport().setMaxX(40);
        graph2.getViewport().setYAxisBoundsManual(true);
        graph2.getViewport().setMaxY(3);
        graph2.getViewport().setMinY(-3);
        graph2.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE); // get rid of grid
        graph2.getGridLabelRenderer().setHorizontalLabelsVisible(false);// hide x axis
        graph2.getGridLabelRenderer().setVerticalLabelsVisible(false);// hide y axis
        motion_state = getResources().getString(R.string.WARM_UP);
        working_mode = WORKING_MODE.Hint_Mode;

        return frag;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        updateUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        stopPlot();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            motion_state = getResources().getString(R.string.WARM_UP);
            working_mode = WORKING_MODE.Hint_Mode;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateUI();
                }
            }, 300);
        } else {
            if (working_mode == WORKING_MODE.Measuring_Mode) {
                stopPlot();
                activity.stopMeasure();
            }
        }
    }

    void stopPlot() {
        mHandler.removeCallbacks(mTimer2);
    }


    public void onEvent(HeartBeatMsg event) {
        if (txtHeartBeat != null) {
            txtHeartBeat.setVisibility(View.VISIBLE);
            txtbpm.setVisibility(View.VISIBLE);
            txtHeartBeat.setText(String.valueOf(event.hearbeat));
        }
    }

    public void onEvent(HeartBeatDetected event) {
        isHeartBeatDetected = event.isDetected;
    }

    void startPlot() {

        DataPoint[] values = new DataPoint[1];
        values[0] = new DataPoint(0, 0);
        graph2LastXValue = 0d;
        mSeries2.resetData(values);
        mTimer2 = new Runnable() {
            @Override
            public void run() {
                graph2LastXValue += 1d;
                double data = getRandom();
                mSeries2.appendData(new DataPoint(graph2LastXValue, data), true, 100);
                if (GConstants.isAutoStop && graph2LastXValue > 150){
                    onClickStart();
                }else{
                    mHandler.postDelayed(this, 100);
                }
            }
        };
        mHandler.postDelayed(mTimer2, 3000);
    }

    private DataPoint[] generateData() {
        int count = 30;
        DataPoint[] values = new DataPoint[count];
        for (int i = 0; i < count; i++) {
            double x = i;
            double f = mRand.nextDouble() * 0.15 + 0.3;
            double y = Math.sin(i * f + 2) + mRand.nextDouble() * 0.3;
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }

    double mLastRandom = 2;
    Random mRand = new Random();

    private double getRandom() {
        double updatedYValue = 0;
        if (isHeartBeatDetected) {
            nCount++;
            switch (nCount) {
                case 1:
                    updatedYValue = 2;
                    if (GConstants.isSoundOn){
                        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                    }
                    break;
                case 2:
                    updatedYValue = -2;
                    break;
                case 3:
                    updatedYValue = 0.5;
                    break;
                case 4:
                    updatedYValue = 0;
                    nCount = 0;
                    isHeartBeatDetected = false;
                    break;
                default:
                    break;
            }
        } else {
            updatedYValue = 0;
        }
        return updatedYValue;
    }

    @OnClick(R.id.imgvStart)
    void onClickStart() {
        if (working_mode == WORKING_MODE.Hint_Mode) {
            activity.startMeasure();
            working_mode = WORKING_MODE.Measuring_Mode;
        } else if (working_mode == WORKING_MODE.Measuring_Mode) {
            working_mode = WORKING_MODE.Save_Mode;
            activity.stopMeasure();
        } else {

        }
        updateUI();
    }

    @OnClick(R.id.relRest)
    void onClickRelRest() {
        if (!motion_state.equals(getResources().getString(R.string.REST))) {
            motion_state = getResources().getString(R.string.REST);
            imgvRest.setImageResource(R.drawable.rest_selected);
            imgvWarmUp.setImageResource(R.drawable.warmup_deselected);
            imgvCardio.setImageResource(R.drawable.cardiounselected);
            imgvExtreme.setImageResource(R.drawable.extremeunselected);
        }

    }

    @OnClick(R.id.relWarmUp)
    void onClickRelWarmUp() {
        if (!motion_state.equals(getResources().getString(R.string.WARM_UP))) {
            motion_state = getResources().getString(R.string.WARM_UP);
            imgvRest.setImageResource(R.drawable.rest_deselected);
            imgvWarmUp.setImageResource(R.drawable.warmup_selected);
            imgvCardio.setImageResource(R.drawable.cardiounselected);
            imgvExtreme.setImageResource(R.drawable.extremeunselected);
        }
    }

    @OnClick(R.id.relCardio)
    void onClickRelCardio() {
        if (!motion_state.equals(getResources().getString(R.string.CARDIO))) {
            motion_state = getResources().getString(R.string.CARDIO);
            imgvRest.setImageResource(R.drawable.rest_deselected);
            imgvWarmUp.setImageResource(R.drawable.warmup_deselected);
            imgvCardio.setImageResource(R.drawable.cardioselected);
            imgvExtreme.setImageResource(R.drawable.extremeunselected);
        }
    }

    @OnClick(R.id.relExtreme)
    void onClickRelExtreme() {
        if (!motion_state.equals(getResources().getString(R.string.EXTREME))) {
            motion_state = getResources().getString(R.string.EXTREME);
            imgvRest.setImageResource(R.drawable.rest_deselected);
            imgvWarmUp.setImageResource(R.drawable.warmup_deselected);
            imgvCardio.setImageResource(R.drawable.cardiounselected);
            imgvExtreme.setImageResource(R.drawable.extremeselected);
        }
    }

    @OnClick(R.id.txtSave)
    void onClickSave() {
        saveData( Integer.parseInt(txtHeartBeat.getText().toString()), motion_state);
        onClickDiscard();
    }

    @OnClick(R.id.txtDiscard)
    void onClickDiscard() {
        working_mode = WORKING_MODE.Hint_Mode;
        updateUI();
    }

    void updateUI() {
        if (working_mode == WORKING_MODE.Hint_Mode) {
            relHintMeasure.setVisibility(View.VISIBLE);
            relStartButton.setVisibility(View.VISIBLE);
            relSaveMode.setVisibility(View.GONE);
            relHint.setVisibility(View.VISIBLE);
            imgvStart.setImageResource(R.drawable.start);
            relGraph.setVisibility(View.GONE);
        } else if (working_mode == WORKING_MODE.Measuring_Mode) {
            relHint.setVisibility(View.GONE);
            relGraph.setVisibility(View.VISIBLE);
            imgvStart.setImageResource(R.drawable.stop);
            txtHeartBeat.setVisibility(View.INVISIBLE);
            txtbpm.setVisibility(View.INVISIBLE);
            startPlot();
        } else {
            stopPlot();
            relHint.setVisibility(View.VISIBLE);
            relGraph.setVisibility(View.GONE);
            imgvStart.setImageResource(R.drawable.start);
            relHintMeasure.setVisibility(View.GONE);
            relStartButton.setVisibility(View.GONE);
            relSaveMode.setVisibility(View.VISIBLE);
            txtHeartBeatSaveMode.setText(txtHeartBeat.getText().toString());
        }
    }

    void saveData(int heart_beat, String motion_state) {
        double maxX = mSeries2.getHighestValueX();
        Iterator<DataPoint> aa = mSeries2.getValues(maxX - 40, maxX);
        StringBuilder sb = new StringBuilder();
        while (aa.hasNext()) {
            String strDat =String.valueOf(aa.next().getY());
            sb.append(strDat).append(",");
        }
        Log.i(TAG,"update array with: "+sb.toString());
        Date today = new Date();
        Log.i(TAG, "data: " + sb.toString());
        HistoryHeartData historyHeartData = new HistoryHeartData(sb.toString(), heart_beat, motion_state, today);
        historyHeartData.save();
    }


}
