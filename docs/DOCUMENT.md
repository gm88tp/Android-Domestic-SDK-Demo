# 怪猫SDK 接入文档 2021/08/23

## SDK **资源引用**

1. 将 resource/libs 下的 jar 文件复制到项目的 libs 目录。

2. 将 resource/so 下的 so文件复制到项目的 so 存放目录（Eclipse 的 so 存放目录在 libs 下，AndroidStudio 的 so存放目录在 /src/main/jniLibs 下）

3. 将 resource/res 下的资源文件复制到项目的 res 目录，如果有如果有重复的文件，将文件内容进行合并（合并一般会出现在 res/values/strings.xml 文件中）。

4. 将 resource/assets 下的文件复制到项目的 assets 目录下，如果项目没有 assets目录，需手动创建（Eclipse 的 assets 目录在项目根目录下创建，AndroidStudio 的assets 目录在 /src/main/ 下创建）

5. 将 resource/AndroidManifest.xml 内注册的 activity、server、provider、meta-data 和 uses-permission 等复制到游戏工程的AndroidManifest.xml文件中。(详细请参考demo)

6. 如果项目使用Eclipse集成，需要将清单文件中 ${applicationId}替换成游戏本身的包名。

7. 在清单文件中的application节点中加上 
   
   ```java
    android:usesCleartextTraffic="true"
   ```

8. targetSdkVersion 设置成22及以上(最高可支持到28)

9. resource/addition 下的zip文件为选接功能相关的接入文档、demo，正常情况下可以不进行接入，如有相关功能需求再进行接入。相关SDK均为独立SDK，接入时不会影响已接入的主体SDK。

10. 注意请在清单文件中更新当前SDK引用的版本号。否则在开放平台的自测流程中，上传apk时会无法通过检测（需要3.9以上版本）。
```xml
    <meta-data
                android:name="game_sdk_version_guaimao"
                android:value="3.9.2" />
   ```

## SDK 接入相关

怪猫 SDK 开放接口，对外提供的接口方法都是静态的，直接通过 GM 进行调用即可。

### 接入 SDK 基本顺序

1. 第一步需要在 Application 的对应方法内进行初始化，Application需要继承GMApplication

```java
public class MyApplication extends GMApplication {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        GM.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        GM.initApplication(MyApplication.this);
    }
}
```

2. 第二步需要在游戏主 Activity 的onCreate()方法内设置回调函数，否则游戏将收不到任何回调信息（登陆成功,登陆失败,支付成功,支付失败等...）
   
   ```java
        GM.setListener(new GmListener() {
            @Override
            public void onCallBack(Message mMessage) {
                // TODO Auto-generated method stub
                switch (mMessage.what) {
                    case GmStatus.INIT_SUCCESS:// 初始化sdk成功回调
                        String s = (String) mMessage.obj;
                        SDKLog.d(TAG,"初始化sdk成功回调  " +  s);
                        toast(s);
                        break;
                    case GmStatus.INIT_FALIED:// 初始化sdk失败回调
                        String s1 = (String) mMessage.obj;
                        SDKLog.d(TAG,"初始化sdk失败回调  " +  s1);
                        toast(s1);
                        break;
                    case GmStatus.LOGIN_SUCCESS:// 登录成功回调
                        String res = (String) mMessage.obj;
                        try {
                            JSONObject object = new JSONObject(res);
                            object.getString("token");
                            object.getString("uid");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case GmStatus.LOGIN_FALIED:// 登录失败回调
                        toast("登录失败回调");
                        break;
                    case GmStatus.LOGIN_CANCEL:// 登录取消回调
                        toast("登录取消回调");
                        break;
                    case GmStatus.LOGOUT_SUCCESS:// 注销账号成功回调，收到回调后游戏需返回主界面
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
                    case GmStatus.REALNAME_CHECK:// 实名认证回调
                        int realNameType = (int) mMessage.obj;
                        switch (realNameType){
                            //0-7岁
                            case GmStatus.ANTI_CHILD:
                                toast("0-7岁");
                                break;
                            //8-15岁
                            case GmStatus.ANTI_MINOR:
                                toast("8-15岁");
                                break;
                            //16-17岁
                           case GmStatus.ANTI_MINOR2:
                               toast("16-17岁");
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
   ```

3. 第三步需要在游戏主 Activity 的 onCreate 方法内进行初始化(如上面代码所示)
   
   ```java
   GM.init(this);
   ```

4. 商务给您的appid并不在这个初始化中传入，只需要在 assests 的 GMConfig.xml 中去修改gssAppId 即可

5. 需要重写 Activity 的一些生命周期方法，并调用怪猫 SDK 的相关方法。后续有详细说明。

### SDK 重写生命周期方法

游戏需要在游戏主 Activity 里的各个生命周期内，重写以下生命周期的方法，并在方法内调用 GM SDK 对应的生命周期方法

注意：onBackPressed()方法是否重写，需要根据游戏是否能主动监听用户退出游戏的操作来决定。游戏能够监听用户退出操作，就不需要重写 onBackPressed() 方法了。反之，则需要重写。

生命周期内需要重写的方法

以下方法都需要重写，并调用 GMSDK 对应的生命周期方法

```java
onStart()
onResume()
onRestart()
onAttachedToWindow()
onPause()
onStop()
onDestroy()
onNewIntent(Intent intent)
onActivityResult(int requestCode, int resultCode, Intent data)
onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
```

```java
GM.onStart()
GM.onResume()
GM.onRestart()
GM.onAttachedToWindow()
GM.onPause()
GM.onStop()
GM.onDestroy()
GM.onNewIntent(Intent intent)
GM.onActivityResult(int requestCode, int resultCode, Intent data)
GM.onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
```

### 登录

```java
GM.login()
```

一定要接到初始化 SDK 成功回调，才可以调登录接口的函数。
在点击进入游戏时，游戏需要判断用户是否登陆，若未登录，需要调用登录方法。
登录结果请在相应回调中进行获取
登录成功的回调信息如下：

```java
{"token": "token","uid": "xxxx"}
```

注意：如何获取res？ 

```java
 String res = (String) mMessage.obj;
  try {
  JSONObject object = new JSONObject(res);
  object.getString("token");
  object.getString("uid");
  } catch (JSONException e) {
    e.printStackTrace();
  }
```

### 查看用户是否登录

```java
GM.isLogin()
```

| 返回值   | 描述       |
| ----- | -------- |
| true  | 当前为已登陆状态 |
| false | 当前为未登陆状态 |

### 登出

```java
GM.logout
```

调用该方法后，游戏会收到登出回调。此时请游戏退回到游戏主界面，如果想重新拉起登录，请再次调用登录方法

### 提交角色信息

```java
GM.submitRoleInfo(Map<String,String> roleInfo)
```

| key            | value                                   |
| -------------- | --------------------------------------- |
| dataType       | 数据类型，1 为进入游戏，2 为创建角色，3 为角色升级，4 为退出，5 为完成新手引导（若无新手引导可以忽略）     |
| roleId         | 角色 ID                                   |
| roleName       | 角色名称                                    |
| roleLevel      | 角色等级                                    |
| zoneId         | 服务器 ID                                  |
| zoneName       | 服务器名称                                   |
| balance        | 用户余额 （ RMB 购买的游戏币）                      |
| partyName      | 帮派、公会等，没有填空字符串                          |
| vipLevel       | VIP 等级，没有 VIP 系统的传 0                    |
| roleCTime      | 角色创建时间(单位：秒)（历史角色没记录时间的传 -1 ，新创建的角色必须要） |
| roleLevelMTime | 角色等级变化时间(单位：秒)（创建角色和进入游戏时传 -1 ）         |

示例

```java
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
```

### 支付

```java
GM.pay(Map<String,String> payInfo)
```

| key          | value                            |
| ------------ | -------------------------------- |
| productId    | 商品 ID                            |
| productName  | 商品名                              |
| productPrice | 商品价格(元)（无小数点）                    |
| productCount | 商品份数(除非游戏需要支持一次购买多份商品，否则传 1 即可)  |
| productDesc  | 商品描述（不传则使用 product_Name ）        |
| coinName     | 虚拟币名称（如金币、元宝）                    |
| coinRate     | 虚拟币兑换比例（例如 100，表示 1 元购买 100 虚拟币） |
| roleId       | 游戏角色 ID                          |
| roleName     | 游戏角色名                            |
| roleGrade    | 游戏角色等级                           |
| roleBalance  | 用户游戏内虚拟币余额，如元宝，金币，符石             |
| vipLevel     | VIP 等级                           |
| partyName    | 帮派、公会等                           |
| zoneId       | 服务器 ID，若无填“ 1 ”                  |
| zoneName     | 服务器名                             |
| gameReceipts | 游戏客户端内生成的用户身份加密票据，订单号等（透传）       |

示例

```java
Map<String, String> payInfo = new HashMap<String, String>();
payInfo.put("productId", "1");
payInfo.put("productName", "月卡");
payInfo.put("productPrice", "30");
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
payInfo.put("gameReceipts", "test info");
GM.pay(payInfo);
```

支付信息可以在回调当中获取

### 退出游戏

```java
GM.quit()
```

退出游戏的接入流程说明：

1. 首先根据游戏是否会自己监听游戏的退出来分：
   
   游戏自己监听：当游戏监听到用户退出游戏的意图时，需要调用 GM.quit() 。此时游戏不需要重写 onBackPress()方法，否则可能会产生异常
   
   游戏不自己监听：则需要重写 onBackPress()方法，sdk 将帮助游戏来监听用户退出意图

2. 游戏需要对退出游戏的回调做出相应的处理
   
   GmStatus.GAME_EXIT --- 游戏此时需要结束游戏进程
   
   
### 实名认证相关


实名认证相关接入流程说明：
相关主动接口均需在用户登录后调用，如游戏或我方运营有相关需求可以选择接入，如无要求可不接入

1. SDK会在登录游戏以及用户实名状态改变时回调实名认证回调，同时SDK提供接口GM.antiAddiction()，调用后也可收到实名认证回调

```java
GM.antiAddiction()
```

2. 根据防沉迷相关要求，SDK提供查询用户当天剩余游玩时间接口，返回为剩余游玩分钟
   
```java
GM.getPlayTimeLeft()
```

当GM.getPlayTimeLeft()接口返回为Integer.MAX_VALUE时，用户已成年无游玩时间限制，返回为Integer.MIN_VALUE时为我方后台未开启实名游玩限制，其余返回为用户剩余游玩分钟，详情可参考demo

### 其他外部接口，可根据游戏自身需要选接

1. 打开外部链接接口，调用此接口后，SDK会跳转外部链接url

```java
Platform.getInstance().openUrlWithWeb(String url);
```


2. 打开webview链接接口，调用此接口后，SDK会跳转webview

```java
Platform.getInstance().doOpenURLbyWebView(String url);
```

3. 打开怪猫客服界面接口，调用此接口后，会进入客服界面

```java
Platform.getInstance().openCustomer(Context context);
```

4. 打开怪猫个人中心界面接口，调用此接口后，会进入个人中心界面

```java
Platform.getInstance().showUserCenter(Context context);
```

5. 打开怪猫手机绑定界面接口，调用此接口后，会进入手机绑定界面

```java
Platform.getInstance().showBindPhone();
```


6. 打开怪猫实名认证界面接口，调用此接口后，会进入实名认证界界面

```java
Platform.getInstance().showBindId();
```

### 广告相关（选接）


广告相关接入流程说明：
广告相关为选接SDK，如游戏或我方运营有相关需求可以选择接入，如无要求可不接入，广告SDK为独立SDK，接入时不会影响已接入的主体SDK。相关文档、demo见resource/addition下的广告SDK.zip，接入时如果遇到已使用的jar文件或so库等，无需重复引入

### 分享相关（选接）


分享相关接入流程说明：
分享相关为选接SDK，如游戏或我方运营有相关需求可以选择接入，如无要求可不接入，分享SDK为独立SDK，接入时不会影响已接入的主体SDK。相关文档、demo见resource/addition下的分享SDK.zip，接入时如果遇到已使用的jar文件或so库等，无需重复引入
