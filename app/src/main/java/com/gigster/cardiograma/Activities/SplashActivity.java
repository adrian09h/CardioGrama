package com.gigster.cardiograma.Activities;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.gigster.cardiograma.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }


    @Override
    public  void onResume(){
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showNextSceen();
            }
        }, 2000);
    }

    void showNextSceen() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
