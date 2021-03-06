package com.gm.gmsdkdemo;

import android.content.Context;

import com.game.sdk.GMApplication;
import com.gm88.gmcore.GM;

/**
 * Created by zhewang on 2020/09/27.
 */

public class TApplication extends GMApplication {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        GM.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        GM.initApplication(TApplication.this);
    }
}
