package com.gm.gmsdkdemo;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.game.sdk.Platform;
import com.game.sdk.reconstract.base.Config;
import com.game.sdk.reconstract.utils.GlobalUtil;
import com.game.sdk.reconstract.utils.ULogUtil;
import com.gm88.gmcore.GM;
import com.gm88.gmcore.GmListener;
import com.gm88.gmcore.GmStatus;
import com.gm88.gmutils.SDKLog;
import com.gm88.gmutils.ToastHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    private final static String TAG = "TEST";

    private EditText price_ET;

    private EditText gameId_ET, roleName_ET;
    private Button setGameId_BTN, changeDebug_BTN, creatRole_BTN, beginner_BTN, updateRole_BTN;
    private Button mRealNameCheck;
    private int count = 0;

    private boolean initsucc = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        init();
        initClick();
    }

    private void init() {
      GM.setListener(new GmListener() {
        @Override
        public void onCallBack(Message mMessage) {
          // TODO Auto-generated method stub
          switch (mMessage.what) {
              case GmStatus.INIT_SUCCESS:// 初始化sdk成功回调
                  String s = (String) mMessage.obj;
                  SDKLog.d(TAG, "初始化sdk成功回调  " + s);

                  initsucc = true;
                  Log.i(TAG, "初始化成功");
                  toast(s);
                  break;
               case GmStatus.INIT_FALIED:// 初始化sdk失败回调
                  String s1 = (String) mMessage.obj;
                  SDKLog.d(TAG, "初始化sdk失败回调  " + s1);
                  toast(s1);
                  break;
              case GmStatus.LOGIN_SUCCESS:// 登陆成功回调
                  String res = (String) mMessage.obj;
                  try {
                      JSONObject object = new JSONObject(res);
                      object.getString("token");
                      object.getString("uid");
                  } catch (JSONException e) {
                      e.printStackTrace();
                  }
                  break;
              case GmStatus.LOGIN_FALIED:// 登陆失败回调
                  toast("登陆失败回调");
                  break;
              case GmStatus.LOGIN_CANCEL:// 登陆取消回调
                  toast("登陆取消回调");
                  break;
              case GmStatus.LOGOUT_SUCCESS:// 注销账号成功回调
                  toast("注销账号成功回调");
                  break;
              case GmStatus.LOGOUT_FALIED:// 注销账号失败回调
                  toast("注销账号失败回调");
                  break;
              case GmStatus.PAY_SUCCESS:// 支付成功回调
                  toast("支付成功回调");
                  break;
              case GmStatus.PAY_FALIED:// 支付失败回调
                  toast("支付失败回调");
                  break;
              case GmStatus.PAY_CANCEL:// 支付取消回调
                  toast("支付取消回调");
                  break;
              case GmStatus.GAME_EXIT:// 退出游戏回调
                  MainActivity.this.finish();
                  break;
              case GmStatus.REALNAME_CHECK:
                  int realNameType = (int) mMessage.obj;
                  switch (realNameType) {
                      //0-7岁
                      case GmStatus.ANTI_CHILD:
                        toast("0-7岁");
                        break;
                      //8-17岁
                      case GmStatus.ANTI_MINOR:
                          toast("8-17岁");
                          break;
                      //成年
                      case GmStatus.ANTI_AUDLT:
                          toast("成年");
                          break;
                      //查询失败或者是未实名
                      case GmStatus.ANTI_UNREGISTER:
                          toast("查询失败或者是未实名");
                          break;
                }
                  break;
              default:
                  break;
          }
        }
      });
      GM.init(this);
    }


    private void initClick() {
        price_ET = (EditText) findViewById(R.id.et_main_activity_purchase_value_input_gm);
        mRealNameCheck = findViewById(R.id.btn_main_activity_check_realname);
        gameId_ET = (EditText) findViewById(R.id.et_main_activity_set_game_id);
        setGameId_BTN = (Button) findViewById(R.id.btn_main_activity_set);
        changeDebug_BTN = (Button) findViewById(R.id.btn_main_activity_change_debug);
        creatRole_BTN = (Button) findViewById(R.id.btn_main_activity_creatRole);
        beginner_BTN = (Button) findViewById(R.id.btn_main_activity_beginner);
        updateRole_BTN = (Button) findViewById(R.id.btn_main_activity_updata_role);
        roleName_ET = (EditText) findViewById(R.id.et_main_activity_updata_role);
        changeDebug_BTN.setText(Config.isDebug() ? "点击切换成正式环境服务" : "点击切换成测试环境服务");

        mRealNameCheck.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            GM.antiAddiction();
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

        updateRole_BTN.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Toast.makeText(MainActivity.this, "点击更新角色信息~", Toast.LENGTH_SHORT).show();
            Platform.getInstance().updateRole("game_level", roleName_ET.getText().toString().isEmpty() ?
                    "role_name" : roleName_ET.getText().toString(), "server_id");
          }
        });
    }

    public void userLogin(View view) {

        if (initsucc) {
            SDKLog.d(TAG, "demo 点击了登陆");
            if (GM.isLogin()) {
                Toast.makeText(this, "您已经登录过帐号", Toast.LENGTH_SHORT).show();
                return;
            }
                GM.login();
            } else {
                GM.init(this);
        }
    }


    public void userLogout(View view) {
        if (!GM.isLogin()) {
            Toast.makeText(this, "您还没登录账号呢~", Toast.LENGTH_SHORT).show();
            return;
        }

          SDKLog.d(TAG, "demo 点击了登出");
          GM.logout();
    }

    public void userLoginSwitch(View view) {
  //        Platform.getInstance().loginSwitch();
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
        SDKLog.d(TAG, "demo 点击了支付");
        this.pay(price_ET.getText().toString().trim());
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
        GM.submitRoleInfo(data);
    }

    // 测试支付
    private void pay(String price) {
        // use map as the param to pay
        Map<String, String> payInfo = new HashMap<String, String>();
        payInfo.put("productId", "1");
        payInfo.put("productName", "月卡");
        payInfo.put("productPrice", price);
        payInfo.put("productCount", "1");
        payInfo.put("productDesc", "月卡");
        payInfo.put("coinName", "钻石");
        payInfo.put("coinRate", "10");
        payInfo.put("roleId", "78713");
        payInfo.put("roleName", "天下第一");
        payInfo.put("roleGrade", "69");
        payInfo.put("roleBalance", "120");
        payInfo.put("vipLevel", "3");
        payInfo.put("partyName", "青帮");
        payInfo.put("zoneId", "1");
        payInfo.put("zoneName", "上海一区");
        payInfo.put("gameReceipts", System.currentTimeMillis() + "");
        GM.pay(payInfo);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GM.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GM.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        GM.onRestart();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        GM.onAttachedToWindow();
    }

    @Override
    protected void onPause() {
        super.onPause();
        GM.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        GM.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GM.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        GM.onNewIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GM.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        GM.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onBackPressed() {
        GM.onBackPressed();
    }

    /**
     * 提示
     */
    private void toast(final String str) {
        ToastHelper.toast(this, str);
    }

    public void userInit(View view) {
        SDKLog.d(TAG, "demo 点击了初始化");
      //  init();
    }

    public void userLoginCheck(View view) {
        ToastHelper.toast(MainActivity.this, GM.isLogin() + "");
    }
}