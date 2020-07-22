package com.gigster.cardiograma.Fragments;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.SyncStateContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gigster.cardiograma.Activities.MainActivity;
import com.gigster.cardiograma.Models.GConstants;
import com.gigster.cardiograma.Models.HeartBeatDetected;
import com.gigster.cardiograma.Models.HeartBeatMsg;
import com.gigster.cardiograma.Models.HistoryHeartData;
import com.gigster.cardiograma.R;

import java.util.Date;
import java.util.Iterator;
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
    @Bind(R.id.editREST)
    EditText editRest;
    @Bind(R.id.editCardio)
    EditText editCardio;
    @Bind(R.id.editWarmup)
    EditText editWarmup;
    @Bind(R.id.editExtreme)
    EditText editExtreme;
    @Bind(R.id.txtCount)
    TextView txtCount;



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

    private boolean isPassedTime = false;
    private long startTime = 0L;
    private Handler customHandler = new Handler();


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
        graph2 = frag.findViewById(R.id.graph2);
        imgvStart = frag.findViewById(R.id.imgvStart);
        relHint = frag.findViewById(R.id.relHint);
        relGraph = frag.findViewById(R.id.relGraph);
        txtHeartBeat = frag.findViewById(R.id.txtHeartBeat);
        txtbpm = frag.findViewById(R.id.txtbpm);
        relHintMeasure = frag.findViewById(R.id.relHintMeausre);
        relStartButton = frag.findViewById(R.id.relStartButton);
        relSaveMode = frag.findViewById(R.id.relSaveMode);
        imgvRest = frag.findViewById(R.id.imgvRest);
        imgvWarmUp = frag.findViewById(R.id.imgvWarmup);
        imgvCardio = frag.findViewById(R.id.imgvCardio);
        imgvExtreme = frag.findViewById(R.id.imgvExtreme);
        txtHeartBeatSaveMode = frag.findViewById(R.id.txtHeartBeat_SaveMode);
        editRest = frag.findViewById(R.id.editREST);
        editCardio = frag.findViewById(R.id.editCardio);
        editWarmup = frag.findViewById(R.id.editWarmup);
        editExtreme = frag.findViewById(R.id.editExtreme);
        txtCount = frag.findViewById(R.id.txtCount);

        frag.findViewById(R.id.relRest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRelRest();
            }
        });

        frag.findViewById(R.id.relWarmUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRelWarmUp();
            }
        });

        frag.findViewById(R.id.relCardio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRelCardio();
            }
        });

        imgvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickStart();
            }
        });

        frag.findViewById(R.id.relExtreme).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRelExtreme();
            }
        });

        frag.findViewById(R.id.txtSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSave();
            }
        });

        frag.findViewById(R.id.txtDiscard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickDiscard();
            }
        });


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
    boolean firstLaunch = false;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (!firstLaunch) {
                firstLaunch = true;
                return;
            }
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

    void updateTimer(){
        if (GConstants.isAutoStop){
            CountDownTimer timer = new CountDownTimer(30000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    txtCount.setText("00:" + String.format("%02d", (int)(millisUntilFinished / 1000)));
                }

                @Override
                public void onFinish() {
                    txtCount.setText("00:00");
                    isPassedTime = true;
                }
            }.start();
        }else{
            startTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 0);
        }
    }
    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            long timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            int secs = (int) (timeInMilliseconds / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (timeInMilliseconds % 1000);
            txtCount.setText(String.format("%02d", mins) + ":" + String.format("%02d", secs));
            if (working_mode == WORKING_MODE.Measuring_Mode){
                customHandler.postDelayed(this, 0);
            }

        }
    };


    void startPlot() {
        updateTimer();
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
                if (GConstants.isAutoStop && isPassedTime){
                    onClickStart();
                    isPassedTime = false;
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


    void onClickRelRest() {
        if (!motion_state.equals(getResources().getString(R.string.REST))) {
            motion_state = getResources().getString(R.string.REST);
            imgvRest.setImageResource(R.drawable.rest_selected);
            imgvWarmUp.setImageResource(R.drawable.warmup_deselected);
            imgvCardio.setImageResource(R.drawable.cardiounselected);
            imgvExtreme.setImageResource(R.drawable.extremeunselected);
            requestFocusAndShowKeyboard(editRest);
        }

    }


    void onClickRelWarmUp() {
        if (!motion_state.equals(getResources().getString(R.string.WARM_UP))) {
            motion_state = getResources().getString(R.string.WARM_UP);
            imgvRest.setImageResource(R.drawable.rest_deselected);
            imgvWarmUp.setImageResource(R.drawable.warmup_selected);
            imgvCardio.setImageResource(R.drawable.cardiounselected);
            imgvExtreme.setImageResource(R.drawable.extremeunselected);
            requestFocusAndShowKeyboard(editWarmup);
        }
    }


    void onClickRelCardio() {
        if (!motion_state.equals(getResources().getString(R.string.CARDIO))) {
            motion_state = getResources().getString(R.string.CARDIO);
            imgvRest.setImageResource(R.drawable.rest_deselected);
            imgvWarmUp.setImageResource(R.drawable.warmup_deselected);
            imgvCardio.setImageResource(R.drawable.cardioselected);
            imgvExtreme.setImageResource(R.drawable.extremeunselected);
            requestFocusAndShowKeyboard(editCardio);
        }
    }

    void onClickRelExtreme() {
        if (!motion_state.equals(getResources().getString(R.string.EXTREME))) {
            motion_state = getResources().getString(R.string.EXTREME);
            imgvRest.setImageResource(R.drawable.rest_deselected);
            imgvWarmUp.setImageResource(R.drawable.warmup_deselected);
            imgvCardio.setImageResource(R.drawable.cardiounselected);
            imgvExtreme.setImageResource(R.drawable.extremeselected);
            requestFocusAndShowKeyboard(editExtreme);
        }
    }

    void onClickSave() {
        String strMotionStateNote = "";
        if (motion_state.equals(getResources().getString(R.string.REST))){
            strMotionStateNote = editRest.getText().toString();
        } else if (motion_state.equals(getResources().getString(R.string.WARM_UP))){
            strMotionStateNote = editWarmup.getText().toString();
        } else if (motion_state.equals(getResources().getString(R.string.CARDIO))){
            strMotionStateNote = editCardio.getText().toString();
        } else if (motion_state.equals(getResources().getString(R.string.EXTREME))){
            strMotionStateNote = editExtreme.getText().toString();
        }
        saveData( Integer.parseInt(txtHeartBeat.getText().toString()), motion_state,strMotionStateNote);
        onClickDiscard();
    }


    void onClickDiscard() {
        working_mode = WORKING_MODE.Hint_Mode;
        updateUI();
    }

    void requestFocusAndShowKeyboard(EditText editText){
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
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
//            txtHeartBeat.setVisibility(View.INVISIBLE);
//            txtbpm.setVisibility(View.INVISIBLE);
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

    void saveData(int heart_beat, String motion_state, String motion_state_note) {
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
        HistoryHeartData historyHeartData = new HistoryHeartData(sb.toString(), heart_beat, motion_state, today, motion_state_note);
        historyHeartData.save();
    }
}
