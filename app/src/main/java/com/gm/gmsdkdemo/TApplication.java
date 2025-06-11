package com.gm.gmsdkdemo;

import static com.gm.gmsdkdemo.UseBD.checkOpenDatasdkWithBd;
import static com.gm.gmsdkdemo.UseBD.extractBdFields;

import android.content.Context;

import com.baidu.mobads.action.BaiduAction;
import com.game.sdk.GMApplication;
import com.game.sdk.reconstract.model.DataPluginEntity;
import com.game.sdk.reconstract.utils.FileUtils;
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

        //ToDo 接入百度投放需要主动初始化配置
        boolean containsOpenDatasdkWithBd = checkOpenDatasdkWithBd(FileUtils.getMETAFileContent(TApplication.this, "third_sdk"));
        if (containsOpenDatasdkWithBd) {
            UseBD.BdBean bdBean = extractBdFields(FileUtils.getMETAFileContent(TApplication.this, "third_sdk"));
            BaiduAction.init(TApplication.this, Long.parseLong(bdBean.bdAppId), bdBean.bdAppSecret);
        }
    }
}
