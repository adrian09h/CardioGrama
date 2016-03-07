package com.gigster.cardiograma.App;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;




/**
 * Created by star1 on 11/13/2015.
 */
public class MainApp extends com.activeandroid.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

//        Intercom.initialize(this, "android_sdk-39f682d328be85aae47b6aa17d9fea24c210171b", "hafx61ha");
//        Intercom.client().registerUnidentifiedUser();

    }
}
