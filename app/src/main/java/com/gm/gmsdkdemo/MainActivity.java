package com.gm.gmsdkdemo;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.game.sdk.Platform;
import com.game.sdk.ad.callback.AdCallBack;
import com.game.sdk.reconstract.base.Config;
import com.game.sdk.reconstract.listeners.ExitCallback;
import com.game.sdk.reconstract.listeners.InitCallback;
import com.game.sdk.reconstract.listeners.LoginCallback;
import com.game.sdk.reconstract.listeners.LogoutCallback;
import com.game.sdk.reconstract.listeners.PurchaseCallback;
import com.game.sdk.reconstract.listeners.RegistRealNameCallback;
import com.game.sdk.reconstract.manager.ConfigManager;
import com.game.sdk.reconstract.model.Purchase;
import com.game.sdk.reconstract.model.User;
import com.game.sdk.reconstract.utils.GlobalUtil;
import com.game.sdk.reconstract.utils.ULogUtil;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {
  private final static String TAG = "TEST";

  private LoginCallback loginCallback;
  private LogoutCallback logoutCallback;
  private PurchaseCallback purchaseCallback;
  private InitCallback initCallback;
  private RegistRealNameCallback registRealNameCallback;
  private AdCallBack adCallBack;
  private EditText price_ET;
  private ExitCallback exitCallback;

  private EditText gameId_ET , roleName_ET;
  private Button setGameId_BTN, changeDebug_BTN ,creatRole_BTN,beginner_BTN,updateRole_BTN,showad_BTN;

  private int count = 0;

  private boolean initsucc = false;

  private EditText mEtTipsTime,mEtStopGuest,mEtStopChild;

  private View mBtnCommit;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_main_guaimao);
    price_ET = (EditText) findViewById(R.id.et_main_activity_purchase_value_input_gm);
    gameId_ET = (EditText) findViewById(R.id.et_main_activity_set_game_id);
    setGameId_BTN = (Button) findViewById(R.id.btn_main_activity_set);
    changeDebug_BTN = (Button) findViewById(R.id.btn_main_activity_change_debug);
    creatRole_BTN = (Button) findViewById(R.id.btn_main_activity_creatRole);
    beginner_BTN = (Button) findViewById(R.id.btn_main_activity_beginner);
    updateRole_BTN = (Button) findViewById(R.id.btn_main_activity_updata_role);
    roleName_ET = (EditText) findViewById(R.id.et_main_activity_updata_role);
    Config.setIsDebug(false);
    changeDebug_BTN.setText(Config.isDebug() ? "点击切换成正式环境服务" : "点击切换成测试环境服务");

    mEtTipsTime = findViewById(R.id.et_set_tips_time);
    mEtStopGuest = findViewById(R.id.et_set_time_guest_stop);
    mEtStopChild = findViewById(R.id.et_set_time_child_stop);
    mBtnCommit = findViewById(R.id.btn_commit);

    showad_BTN = findViewById(R.id.btn_main_activity_show_ad);

    mBtnCommit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ConfigManager.getInstance().setmTipsTime(Integer.parseInt(mEtTipsTime.getText().toString().trim()));
        ConfigManager.getInstance().setmStopGuest(Integer.parseInt(mEtStopGuest.getText().toString().trim()));
        ConfigManager.getInstance().setmStopChild(Integer.parseInt(mEtStopChild.getText().toString().trim()));
      }
    });

    showad_BTN.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //需配置后方可测试
        Platform.getInstance().showAd(13,"xxx");
      }
    });

    setGameId_BTN.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!TextUtils.isEmpty(gameId_ET.getText().toString())) {
          Config.setGameId(String.valueOf(gameId_ET.getText().toString()));
        } else {
          GlobalUtil.shortToast("请输入正确的游戏id号");
        }
      }
    });

    changeDebug_BTN.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        if (count <= 6) {
//                    GlobalUtil.shortToast("再点击" + (6 - count) + "次，完成切换");
          count++;
        } else {
          Config.setIsDebug(!Config.isDebug());
          changeDebug_BTN.setText(Config.isDebug() ? "点击切换成正式环境服务" : "点击切换成测试环境服务");
          GlobalUtil.shortToast("切换成功");
        }
        ULogUtil.d(TAG, "is Debug :" + Config.isDebug());
      }
    });


    initCallbacks();
//        Platform.getInstance().initPlatform(this, Config.isDebug()?"773":"1628");
    Platform.getInstance().initPlatform(this, "773");
    Platform.getInstance().setShouldShowLoginViewAuto(true);

    creatRole_BTN.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Toast.makeText(MainActivity.this, "点击创建角色~", Toast.LENGTH_SHORT).show();
        Platform.getInstance().creatRole();
        ULogUtil.d(TAG,"UserInfo .... " + Platform.getInstance().getUserInfo());
      }
    });

    beginner_BTN.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Toast.makeText(MainActivity.this, "点击完成新手引导~", Toast.LENGTH_SHORT).show();
        Platform.getInstance().overBeginnerGuide();
      }
    });

    updateRole_BTN.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Toast.makeText(MainActivity.this, "点击更新角色信息~", Toast.LENGTH_SHORT).show();
        Platform.getInstance().updateRole("game_level",roleName_ET.getText().toString().isEmpty()?
                "role_name":roleName_ET.getText().toString(),"server_id");
      }
    });
    ULogUtil.e(TAG,Platform.getInstance().getPhoneInfo());
  }

  private void initCallbacks() {
    loginCallback = new LoginCallback() {
      @Override
      public void loginSuccess(User gameUser) {
        Log.i(TAG, "登录成功：" + gameUser.getSid());
        Toast.makeText(MainActivity.this,"登录成功回调...",Toast.LENGTH_SHORT).show();
      }

      @Override
      public void loginFail(String msg) {
        Log.i(TAG, "登录失败loginFail：" + msg);
        Toast.makeText(MainActivity.this,"登录失败   loginFail...",Toast.LENGTH_SHORT).show();
      }

      @Override
      public void loginCancel(String msg) {
        Log.i(TAG, "登录取消loginCancel：" + msg);
        Toast.makeText(MainActivity.this,"登录取消  loginCancel...",Toast.LENGTH_SHORT).show();

      }

    };

    logoutCallback = new LogoutCallback() {
      @Override
      public void onLogoutSuccess() {
        ULogUtil.d(TAG,"[onLogoutSuccess] ....注销成功");
        Toast.makeText(MainActivity.this, "注销成功~", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onLogoutFail() {
        Toast.makeText(MainActivity.this, "注销失败~", Toast.LENGTH_SHORT).show();
      }
    };

    purchaseCallback = new PurchaseCallback() {

      @Override
      public void onSuccess(String ordeId, Purchase purchase1) {
        Log.i(TAG, "支付成功");
        GlobalUtil.shortToast("当前支付成功的id是：" + ordeId);
      }

      @Override
      public void onFail(String msg) {
        Log.i(TAG, "失败支付 : " + msg);
        GlobalUtil.shortToast("支付失败：" + msg);
      }

      @Override
      public void onCancel(String msg) {
        Log.i(TAG, "取消支付 : " + msg);
        GlobalUtil.shortToast("取消支付：" + msg);
      }
    };
    initCallback = new InitCallback() {
      @Override
      public void initSuccess() {
        initsucc = true;
        Log.i(TAG, "初始化成功");
      }

      @Override
      public void initFail() {

        Log.i(TAG, "初始化失败");
      }
    };
    registRealNameCallback = new RegistRealNameCallback() {
      @Override
      public void registRealNameSuccChild() {
        //0-8
      }

      @Override
      public void registRealNameSuccMinor() {
        //8-18
      }

      @Override
      public void registRealNameSuccAudlt() {
        //18+
      }

      @Override
      public void registRealNameFailed() {
        //未认证，或者认证失败
      }
    };
    adCallBack = new AdCallBack() {
      //广告请联系我方运营，如不需要则无需接入
      @Override
      public void onAdFailed(String errormsg) {
        GlobalUtil.shortToast("onAdFailed：" + errormsg);
      }

      @Override
      public void onVideoComplete(String extra) {
        GlobalUtil.shortToast("onVideoComplete：" + extra);
      }
    };
    exitCallback = new ExitCallback() {
      @Override
      public void onSuccess() {
        //退出游戏回调，此时需要结束游戏进程
        android.os.Process.killProcess(android.os.Process.myPid());
        Log.i(TAG, "退出成功");
      }

      @Override
      public void onCancel() {
        //取消退出回调
        Log.i(TAG, "取消退出");
      }



    };
    Platform.getInstance().setCallbacks(loginCallback, logoutCallback, purchaseCallback,initCallback,registRealNameCallback,adCallBack, exitCallback);
  }

  @Override
  public void onBackPressed() {
    Platform.getInstance().quit();
  }


  public void userLogin(View view) {
    if (Platform.getInstance().isLogin()) {
      Toast.makeText(this, "您已经登录过帐号", Toast.LENGTH_SHORT).show();
      return;
    }
    if (initsucc){
      Platform.getInstance().login();
    }else {
      Toast.makeText(this, "初始化失败", Toast.LENGTH_SHORT).show();
      Platform.getInstance().initPlatform(MainActivity.this,"773");
    }
  }

  public void userLogout(View view) {
    Platform.getInstance().logout();
  }

  public void userLoginSwitch(View view) {
    ULogUtil.d(TAG,"切换账号");
    Platform.getInstance().loginSwitch();
  }

  private void submitRoleInfo() {
    // use map as the param to submitRoleInfo
    Map<String, String> data = new HashMap<String, String>();
    data.put("dataType", "1");
    data.put("roleId", "7845");
    data.put("roleName", "天下第一");
    data.put("roleLevel", "22");
    data.put("zoneId", "1");
    data.put("zoneName", "上海一区");
    data.put("balance", "130");
    data.put("partyName", "青帮");
    data.put("vipLevel", "2");
    data.put("roleCTime", "-1");
    data.put("roleLevelMTime", "-1");
    Platform.getInstance().submitRoleInfo(data);
  }


  public void userPurchase(View view) {
    if (!Platform.getInstance().isLogin()) {
      GlobalUtil.shortToast("请先登录~");
      return;
    }
    if (TextUtils.isEmpty(price_ET.getText().toString().trim())) {
      GlobalUtil.shortToast("请在输入框中填入要消费的金额");
      return;
    }
    Purchase purchase = new Purchase();
    purchase.setCoins(Double.parseDouble(price_ET.getText().toString()));
    purchase.setRoleid("roleid");
    purchase.setServerid("1");
    //服务端会根据这个DeveloperInfo来判断重复，如果一段时间内重复提交会被服务端拒绝，请保持唯一
    purchase.setDeveloperInfo("developerinfo" + System.currentTimeMillis());
    purchase.setProductName("测试商品");
    Platform.getInstance().purchase(purchase);
  }

  public void changePattern(View view) {
    GlobalUtil.shortToast(Platform.getInstance().isHideLogout() ? "切换成正常模式成功" : "切换成无注销按钮模式成功");
    Platform.getInstance().hideLogoutButton(!Platform.getInstance().isHideLogout());
  }


  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

    }
  }

  @Override
  public Resources getResources() {
    Resources res = super.getResources();
    Configuration config = new Configuration();
    config.setToDefaults();
    res.updateConfiguration(config, res.getDisplayMetrics());
    return res;
  }
  @Override
  protected void onStart() {
    super.onStart();
    Platform.getInstance().onStart();
  }

  @Override
  protected void onResume() {
    super.onResume();
    Platform.getInstance().floatResume(this);
  }

  @Override
  protected void onRestart() {
    super.onRestart();
    Platform.getInstance().onRestart();
  }

  @Override
  public void onAttachedToWindow() {
    super.onAttachedToWindow();
    Platform.getInstance().onAttachedToWindow();
  }

  @Override
  protected void onPause() {
    super.onPause();
    Platform.getInstance().onPause(this);
  }

  @Override
  protected void onStop() {
    super.onStop();
    Platform.getInstance().onStop();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Platform.getInstance().exit();
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Platform.getInstance().onNewIntent(intent);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Platform.getInstance().onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    Platform.getInstance().onRequestPermissionsResult(requestCode,permissions,grantResults);
  }

}
