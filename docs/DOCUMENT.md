# GM88 Android国内游戏3.7.2SDK 对接文档

***请注意：demo内的所有参数均是为了方便展示，接入时请使用运营提供的参数进行接入***

## 1.SDK引入

1.将 resource/libs 下的 jar 文件复制到项目的 libs 目录
2.将 resource/so 下的 so文件复制到项目的 so 存放目录（Eclipse 的 so 存放目录在 libs 下，AndroidStudio 的 so存放目录在 /src/main/jniLibs 下）
3.将 resource/res 下的资源文件复制到项目的 res 目录，如果有如果有重复的文件，将文件内容进行合并（合并一般会出现在 res/values/strings.xml 文件中）。
4.将 resource/assets 下的文件复制到项目的 assets 目录下，如果项目没有 assets目录，需手劢创建（Eclipse 的 assets 目录在项目根目录下创建，AndroidStudio 的assets 目录在 /src/main/ 下创建）
5.将resource/AndroidManifest.xml内注册的activity、meta-data和uses-permission复制到游戏工程的AndroidManifest.xml文件中。
6.targetSdkVersion 设置成26及以上（最高可到28）


## 2.初始化SDK

1.在Application的对应方法内进行初始化（如无Application类，则新建一个，同时修改清单文件指定该类）：
代码示例
```
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
```
**记得在清单文件Manifest中进行声明**
2.在游戏的Activity的onCreate方法内调用如下方法：
代码示例
```
Platform.getInstance().initPlatform(activity, gameid);
```
**重要:gameid需要找对接的运营申请**

3.初始化SDK需加上如下代码，丌然可能会导致个人中心点击切换账号后页面退出的情况:
代码示例
```
Platform.getInstance().setShouldShowLoginViewAuto(true);
``` 

4.在onCreate方法内初始化登录回调，注销回调，支付回调等：
代码示例
```
@Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initCallbacks();
  }
```  
InitCallback方法中初始化登录回调，注销回调以及支付回调等回调． 并通过
代码示例
```
Platform.getInstance().setCallbacks(loginCallback, logoutCallback, purchaseCallback,initCallback,registRealNameCallback,adCallBack, exitCallback);
```
设置好回调

5.正式出包时，请在onCreate方法内设置为正式环境：
代码示例
```
Config.setIsDebug(false);
```

## 2.生命周期重写

以下方法都要重写，并调用Platform对应的生命周期方法
onStart(), onResume(), onRestart(), onAttatchedToWindow(), onPause(), onStop(), onDestory(), onNewIntent(), onActivityResult(), onRequestPermissionsResult()

代码示例
```
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
```

## 3.SDK接口及回调说明

3.1初始化回调
接口定义：
```
public interface InitCallback { 
    void initSuccess(); 
    void initFail(); 
}
```
调用示例：
```
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
```  
3.2.1登录接口
**一定要接到初始化SDK成功回调，才可调用，可以用isLogin()来查看用户是否登录**
调用示例：
```
if (Platform.getInstance().isLogin()) {
    Toast.makeText(this, "您已经登录过帐号", Toast.LENGTH_SHORT).show();
    return;
}
//初始化成功调用登录，失败再次初始化
if (initsucc){
    Platform.getInstance().login();
}else {
    Toast.makeText(this, "初始化失败", Toast.LENGTH_SHORT).show();
    Platform.getInstance().initPlatform(activity, gameid);
}
```   
3.2.2登录回调

接口定义：
```
public interface LoginCallback { 
    void loginSuccess(User user); 
    void loginFail(String msg);
    void loginCancel(String msg); 
}
```
调用示例：
```
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
```
需要在loginCallback 的 loginSuccess 回调里, 保存用户信息

3.3.1登出接口
调用示例：
```   
Platform.getInstance().logout();
```   

3.3.2登出回调
接口定义：
```
public interface LogoutCallback { 
    void onLogoutSuccess(); 
    void onLogoutFail(); 
}
```
如果是需要监听sdk方的注销事件，请通过Platform.setLogoutCallback(new LogoutCallback)即可。如果注销成功之后需要弹出提示，请在代码中给出提示
调用示例：
```   
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
```
需要在logoutCallback的onLogoutSuccess回调里, 清理用户信息, 并返回游戏登录界面.(不用再调Sdk中的任何接口)

3.4.1支付接口
调用示例：
```
Purchase purchase = new Purchase();
purchase.setCoins(Double.parseDouble(price_ET.getText().toString()));
purchase.setRoleid("roleid");
purchase.setServerid("1");
//服务端会根据这个DeveloperInfo来判断重复，如果一段时间内重复提交会被服务端拒绝，请保持唯一
purchase.setDeveloperInfo("developerinfo" + System.currentTimeMillis());
purchase.setProductName("测试商品");
Platform.getInstance().purchase(purchase);
```

**重要：请一定要写入对应的商品名称，purchase.setProductName("测试商品"), 否则在支付的时候无法正确显示商品名称**
**Purchase.setDeveloperInfo() 设置进去的订单信息请保持唯一，服务单会根据这个字段来判断一定时间内的订单是否重复提交，如果重复会直接提示支付失败**

3.4.2支付回调
接口定义：
```   
public interface PurchaseCallback {
    void onSuccess(String ordeId, Purchase purchase);
    void onFail(String msg);
    void onCancel(String msg);
}
```   

调用示例：
```   
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
```   

3.5实名认证回调
接口定义：
```   
public interface RegistRealNameCallback {
    void registRealNameSuccChild();
    void registRealNameSuccMinor();
    void registRealNameSuccAudlt();
    void registRealNameFailed();
}
```   

调用示例：
```   
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
```   

3.6.1退出游戏接口
重写onbackPressed()方法，调用quit接口：
调用示例：
```   
@Override
public void onBackPressed() {
    Platform.getInstance().quit();
}
```   
3.6.2退出游戏回调
调用示例：
```  
exitCallback = new ExitCallback() {
      @Override
      public void onSuccess() {
          //To-do退出游戏回调，此时需要结束游戏进程
          Log.i(TAG, "退出成功");
      }

      @Override
      public void onCancel() {
          //取消退出回调
          Log.i(TAG, "取消退出");
      }
```  


3.7.1广告接口
**重要：此接口为选接接口，根据运营要求是否接入，不接入时无法调起广告**
当游戏需要拉起广告的时候，应调用此接口，目前仅支持激励视频，请直接传int型13
调用示例：
```  
Platform.getInstance().showAd(13, extra);
```  
| 字段名称        | 类型     | 属性           |
| ----------- | ------ | ------------ |
| adType       | int |  广告形式(目前仅支持激励视频，请直接传int型13)  |
| extra      | String |广告透传参数，在成功的回调内，会原样返回    |


3.7.2播放广告回调
调用示例：
```  
adCallBack = new AdCallBack() {
      @Override
      public void onAdFailed(String errormsg) {
        GlobalUtil.shortToast("onAdFailed：" + errormsg);
      }
      @Override
      public void onVideoComplete(String extra) {
        GlobalUtil.shortToast("onVideoComplete：" + extra);
      }
};
```


3.8创建角色接口
此接口在创建角色后调用，为必接接口
调用示例：
```  
Platform.getInstance().creatRole();
```  

3.9切换账号接口
调用示例：
```  
Platform.getInstance().loginSwitch();
```  

3.10提交角色信息接口
当游戏内角色状态变化时，应调用此接口
接口定义：
```  
Platform.getInstance().submitRoleInfo(Map<String, String> roleInfo);
```  

| KEY        |  说明           |
| -----------  | ------------ |
| datatype     |  数据类型，1为进入游戏，2为创建角色，3为角色升级，4为退出  |
| roleId       | 角色ID    |
| roleName     |  角色名称  |
| roleLevel       | 角色等级    |
| zoneId     |  服务器ID  |
| zoneName       | 服务器名称    |
| balance     |  用户余额（RMB购买的游戏币）  |
| partyName       | 帮派、工会等，没有填空字符串   |
| vipLevel     |  VIP等级，没有VIP系统传0  |
| roleCTime       | 角色创建时间（单位秒，历史角色没记录时间传-1，新建角色必须要）    |
| roleLevelMTime     |  角色等级变化时间（单位秒，进入游戏和创建角色传-1）  |

调用示例：
```  
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
``` 