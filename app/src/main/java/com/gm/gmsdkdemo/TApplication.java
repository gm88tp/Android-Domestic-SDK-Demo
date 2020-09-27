package com.gm.gmsdkdemo;

import android.app.Application;
import android.content.Context;

import com.game.sdk.GMSDK;

/**
 * Created by zhewang on 2020/09/27.
 */

public class TApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        GMSDK.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        GMSDK.initApp(this);
    }
}
