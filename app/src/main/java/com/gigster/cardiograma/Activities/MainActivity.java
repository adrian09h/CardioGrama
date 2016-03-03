package com.gigster.cardiograma.Activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.apptentive.android.sdk.Apptentive;
import com.gigster.cardiograma.Contollers.ImageProcessing;
import com.gigster.cardiograma.Fragments.MainFragment;
import com.gigster.cardiograma.Models.GConstants;
import com.gigster.cardiograma.Models.HeartBeatDetected;
import com.gigster.cardiograma.Models.HeartBeatMsg;
import com.gigster.cardiograma.R;
import com.mopub.mobileads.MoPubView;

import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.Bind;
import de.greenrobot.event.EventBus;


public class MainActivity extends AppCompatActivity {
    MoPubView moPubView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Configuration dbConfiguration = new Configuration.Builder(this).setDatabaseName("HistoryData.db").create();
//        ActiveAndroid.initialize(dbConfiguration);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        setUpActionBar();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.content_frag, new MainFragment()).commitAllowingStateLoss();
        initSurface();

        moPubView = (MoPubView) findViewById(R.id.adview);
        moPubView.setAdUnitId(getResources().getString(R.string.mopubAdsId)); // Enter your Ad Unit ID from www.mopub.com
        moPubView.loadAd();

//        AdView mAdView = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Apptentive.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Apptentive.onStop(this);
    }

    @Override
    public void onDestroy(){
        moPubView.destroy();
        super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }



    void setUpActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);

        LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflator
                .inflate(R.layout.action_bar, null);
        ImageView imgMore = (ImageView)actionBarView.findViewById(R.id.imgvMore);
        imgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                replaceFragment(new MenuFragment(), GConstants.Anim_None, false);
                showDialog();
            }
        });

        actionBar.setCustomView(actionBarView);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);

        Toolbar parent = (Toolbar) actionBarView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
    }

    public void showDialog() {

        String p_message = "Are you enjoying DartGenie? Please take a moment to rate it!";
        final Dialog dialog = new Dialog(this, R.style.AppTheme);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.fragment_menu);
        android.support.v7.widget.SwitchCompat switchSound = (android.support.v7.widget.SwitchCompat) dialog.findViewById(R.id.switchSounds);
        switchSound.setChecked(GConstants.isSoundOn);
        switchSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                GConstants.isSoundOn = isChecked;
            }
        });
        android.support.v7.widget.SwitchCompat switchAutoStop = (android.support.v7.widget.SwitchCompat) dialog.findViewById(R.id.switchAutoStop);
        switchAutoStop.setChecked(GConstants.isAutoStop);
        switchAutoStop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                GConstants.isAutoStop = isChecked;
            }
        });

        ImageButton imgbSupport = (ImageButton)dialog.findViewById(R.id.imgbInfo_Menu);
        imgbSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportCustomer();
            }
        });
        LinearLayout linEntire = (LinearLayout)dialog.findViewById(R.id.linEntire);
        linEntire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void supportCustomer(){
        Apptentive.showMessageCenter(MainActivity.this);
    }

    public void replaceFragment(Fragment frag, int anim_type, boolean isAddToBackStack) {
        //anim_type = 0: exit: left to right
        //anim_type = 1: enter: right to left
        //anim_type = 2: fade out, fade in
        //anim_type = 3: null
        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();
        if (anim_type == GConstants.Anim_Entry)
            trans.setCustomAnimations(R.anim.sliding_in_mid_to_left, R.anim.sliding_out_right_to_mid);
        else if (anim_type == GConstants.Anim_Exit)
            trans.setCustomAnimations(R.anim.sliding_in_mid_to_right, R.anim.sliding_out_left_to_mid);
        else if (anim_type == GConstants.Anim_Fade)
            trans.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);

        trans.replace(R.id.content_frag, frag);
        if (isAddToBackStack){
            trans.addToBackStack(getResources().getString(R.string.app_name));
        }
        trans.commitAllowingStateLoss();
    }

    public void hideActionBar() {
        ActionBar ab = getSupportActionBar();
        ab.hide();
    }
    public void showActionBar() {
        ActionBar ab = getSupportActionBar();
        ab.show();
    }

    //// Heart Beat

    private static final String TAG = "HeartRateMonitor";
    private static final AtomicBoolean processing = new AtomicBoolean(false);

    private static SurfaceView preview = null;
    private static SurfaceHolder previewHolder = null;
    private static Camera camera = null;

    private static PowerManager.WakeLock wakeLock = null;
    private static int averageIndex = 0;
    private static final int averageArraySize = 4;
    private static final int[] averageArray = new int[averageArraySize];

    private static int beatsIndex = 0;
    private static final int beatsArraySize = 3;
    private static final int[] beatsArray = new int[beatsArraySize];
    private static double beats = 0;
    private static long startTime = 0;

    public static enum TYPE {
        GREEN, RED
    }

    private static TYPE currentType = TYPE.GREEN;

    public static TYPE getCurrent() {
        return currentType;
    }

    void initSurface(){
        preview = (SurfaceView) findViewById(R.id.preview);
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
    }

    public void onResume(){
        super.onResume();
        wakeLock.acquire();
    }
    public void onPause(){
        super.onPause();
        wakeLock.release();
        stopMeasure();

    }

    public void startMeasure(){
        camera = Camera.open();
        preview.setVisibility(View.VISIBLE);
        startTime = System.currentTimeMillis();
    }

    public void stopMeasure(){
        preview.setVisibility(View.GONE);
        if (camera != null){
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }

    }

    private static Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {
            if (data == null) throw new NullPointerException();
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null) throw new NullPointerException();

            if (!processing.compareAndSet(false, true)) return;

            int width = size.width;
            int height = size.height;

            int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(), height, width);
            // Log.i(TAG, "imgAvg="+imgAvg);
            if (imgAvg == 0 || imgAvg == 255) {
                processing.set(false);
                return;
            }

            int averageArrayAvg = 0;
            int averageArrayCnt = 0;
            for (int i = 0; i < averageArray.length; i++) {
                if (averageArray[i] > 0) {
                    averageArrayAvg += averageArray[i];
                    averageArrayCnt++;
                }
            }

            int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt) : 0;
            TYPE newType = currentType;
            if (imgAvg < rollingAverage) {
                newType = TYPE.RED;
                if (newType != currentType) {
                    beats++;
                    // Log.d(TAG, "BEAT!! beats="+beats);
                }
            } else if (imgAvg > rollingAverage) {
                newType = TYPE.GREEN;
            }

            if (averageIndex == averageArraySize) averageIndex = 0;
            averageArray[averageIndex] = imgAvg;
            averageIndex++;

            // Transitioned from one state to another to the same
            boolean isHearBeatDetected = false;
            if (newType != currentType) {
                currentType = newType;
//                image.postInvalidate();
                if (currentType == TYPE.RED){
                    isHearBeatDetected = true;
                    EventBus.getDefault().post(new HeartBeatDetected(true));

                }
            }

            long endTime = System.currentTimeMillis();
            double totalTimeInSecs = (endTime - startTime) / 1000d;
            if (totalTimeInSecs >= 5) {
                double bps = (beats / totalTimeInSecs);
                int dpm = (int) (bps * 60d);
                if (dpm < 30 || dpm > 180) {
                    startTime = System.currentTimeMillis();
                    beats = 0;
                    processing.set(false);
                    return;
                }

                // Log.d(TAG,
                // "totalTimeInSecs="+totalTimeInSecs+" beats="+beats);

                if (beatsIndex == beatsArraySize) beatsIndex = 0;
                beatsArray[beatsIndex] = dpm;
                beatsIndex++;

                int beatsArrayAvg = 0;
                int beatsArrayCnt = 0;
                for (int i = 0; i < beatsArray.length; i++) {
                    if (beatsArray[i] > 0) {
                        beatsArrayAvg += beatsArray[i];
                        beatsArrayCnt++;
                    }
                }
                int beatsAvg = (beatsArrayAvg / beatsArrayCnt);
//                text.setText(String.valueOf(beatsAvg));
                EventBus.getDefault().post(new HeartBeatMsg(beatsAvg));
                startTime = System.currentTimeMillis();
                beats = 0;
            }
            processing.set(false);
        }
    };

    private static SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(previewHolder);
                camera.setPreviewCallback(previewCallback);
            } catch (Throwable t) {
                Log.e("PreviewDemo-surfaceCallback", "Exception in setPreviewDisplay()", t);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                Log.d(TAG, "Using width=" + size.width + " height=" + size.height);
            }
            camera.setParameters(parameters);
            camera.startPreview();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // Ignore
        }
    };

    private static Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea < resultArea) result = size;
                }
            }
        }

        return result;
    }






}
