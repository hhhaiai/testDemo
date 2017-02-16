package com.test;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.test.utils.AppUtils;
import com.test.utils.QueuedWork;
import com.test.utils.SafeRunnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.test.R.id;
import static com.test.R.layout;


public class MainActivity extends AppCompatActivity {
    public static final String T = "sanbo";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
    }


    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn1:
                /**
                 * 获取电源信息
                 */
                getBattery();
                //API大于21的时候获取电源信息
                //getBatteryManagerByLargeThan21();
                break;
            case R.id.btn2:
                /**
                 * 获取蓝牙/wifi信息
                 */
                getBluetoothAndWifoInfo();
                break;
            case R.id.btn3:
                /**
                 * 获取SIM信息
                 */
                getSIMInfo();
                break;
            case R.id.btn4:
                /**
                 * 定位信息
                 */
                getlocation();
                break;
            case R.id.btn5:
                /**
                 * 安装软件信息
                 */
                getAppList();
                break;
            case R.id.btn6:
                /**
                 * 运行进程列表
                 */
                getRunningProcess();
                break;
            case R.id.btn7:
                /**
                 * 传感器 陀螺仪相关参数获取
                 */
                break;
            case R.id.btn8:
                /**
                 * 是否双卡,双卡信息获取
                 */
                break;
            case R.id.btn9:
                /**
                 * 相机参数
                 */
                break;
            case R.id.btn10:
                /**
                 * 分辨率信息
                 */
                getDisPlayInfo();
                break;

            default:
                break;
        }


    }

    /**************************************************************************************
     * ************************************ 获取分辨率信息 ************************************
     **************************************************************************************/

    /**
     * 获取分辨率信息
     */
    private void getDisPlayInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("========").append("WindowManager.getDefaultDisplay().getMetrics(DisplayMetrics)获取").append("=======\n");
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        sb.append("heightPixels:").append(dm.heightPixels).append("\n");
        sb.append("widthPixels:").append(dm.widthPixels).append("\n");
        sb.append("density:").append(dm.density).append("\n");
        sb.append("densityDpi:").append(dm.densityDpi).append("\n");
        sb.append("scaledDensity:").append(dm.scaledDensity).append("\n");
        sb.append("xdpi:").append(dm.xdpi).append("\n");
        sb.append("ydpi:").append(dm.ydpi).append("\n");
        sb.append("========").append("Display获取的分辨率").append("=======\n");
        Display display = getWindowManager().getDefaultDisplay();
        sb.append("Width:").append(display.getWidth()).append("\n");
        sb.append("Height:").append(display.getHeight()).append("\n");
        sb.append("Rotation:").append(display.getRotation()).append("\n");
        sb.append("Orientation:").append(display.getOrientation()).append("\n");
        sb.append("RefreshRate:").append(display.getRefreshRate()).append("\n");
        sb.append("========").append("通过Resources获取").append("=======\n");
        Resources resources = this.getResources();
        DisplayMetrics dmqq = resources.getDisplayMetrics();
        sb.append("widthPixels:").append(dmqq.widthPixels).append("\n");
        sb.append("heightPixels:").append(dmqq.heightPixels).append("\n");
        sb.append("densityDpi:").append(dmqq.densityDpi).append("\n");
        sb.append("density:").append(dmqq.density).append("\n");
        sb.append("scaledDensity:").append(dmqq.scaledDensity).append("\n");
        sb.append("xdpi:").append(dmqq.xdpi).append("\n");
        sb.append("ydpi:").append(dmqq.ydpi).append("\n");
        sb.append("DisplayMetrics:").append(dmqq.toString()).append("\n");

        showMessage(sb.toString());
    }


    /**************************************************************************************
     * ************************************ 运行软件列表 ************************************
     **************************************************************************************/
    /**
     * 运行时软件列表入口
     */
    private void getRunningProcess() {
        //getRunProcessByRuntime();
        //getRunProcessBySystemAPI();
        //getRunProcessLargeThen5BySystemAPI();
        if (Build.VERSION.SDK_INT < 21) {
            getRunProcessBySystemAPI();
        } else {
            getRunProcessLargeThen5BySystemAPI();
        }
    }

    /**
     * 1. 此方法只在android5.0以上有效
     * 2. AndroidManifest中加入此权限<uses-permission xmlns:tools="http://schemas.android.com/tools" android:name="android.permission.PACKAGE_USAGE_STATS"
     * tools:ignore="ProtectedPermissions" />
     * 3. 打开手机设置，点击安全-高级，在有权查看使用情况的应用中，为这个App打上勾
     */
    private void getRunProcessLargeThen5BySystemAPI() {
        long ts = System.currentTimeMillis();
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService("usagestats");
        List<UsageStats> usageStats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, ts - 1000 * 10, ts);
        if (usageStats == null || usageStats.size() == 0) {
            if (HavaPermissionForTest(this) == false) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(this, "权限不够\n请打开手机设置，点击安全-高级，在有权查看使用情况的应用中，为这个App打上勾", Toast.LENGTH_SHORT).show();
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("===========运行软件列表========");
        for (UsageStats us : usageStats) {
            Log.d(T, "--->" + us.getPackageName());
            sb.append("\n").append(us.getPackageName());
        }
        showMessage(sb.toString());
//        class RecentUseComparator implements Comparator<UsageStats> {
//            @Override
//            public int compare(UsageStats lhs, UsageStats rhs) {
//                return (lhs.getLastTimeUsed() > rhs.getLastTimeUsed()) ? -1 : (lhs.getLastTimeUsed() == rhs.getLastTimeUsed()) ? 0 : 1;
//            }
//        }
//        RecentUseComparator mRecentComp = new RecentUseComparator();
//        Collections.sort(usageStats, mRecentComp);
//        String currentTopPackage = usageStats.get(0).getPackageName();
//        Log.d(T,"currentTopPackage:"+currentTopPackage);
    }

    /**
     * 判断是否有用权限
     *
     * @param context 上下文参数
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean HavaPermissionForTest(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
    }

    /**
     * 在新版本也不稳定 不能用了
     */
    private void getRunProcessBySystemAPI() {
//        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.AppTask> ats = am.getAppTasks();
//        for (ActivityManager.AppTask at : ats) {
//            Log.e(T, at.getTaskInfo().baseActivity.toString());
//        }
//        List<ActivityManager.RunningAppProcessInfo> rps = am.getRunningAppProcesses();
//        for (ActivityManager.RunningAppProcessInfo rp : rps) {
//            Log.d(T, "getRunningAppProcesses===>" + rp.processName);
//        }
//        List<ActivityManager.RunningServiceInfo> rss = am.getRunningServices(0);
//        for (ActivityManager.RunningServiceInfo rs : rss) {
//            Log.d(T, "getRunningServices===>" + rs.process);
//        }

        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        //获取正在运行的应用
        List<ActivityManager.RunningAppProcessInfo> run = am.getRunningAppProcesses();
        //获取包管理器，在这里主要通过包名获取程序的图标和程序名
        PackageManager pm = this.getPackageManager();

        StringBuilder sb = new StringBuilder();
        sb.append("===========运行软件列表========");
        for (ActivityManager.RunningAppProcessInfo ra : run) {
            //这里主要是过滤系统的应用和电话应用，当然你也可以把它注释掉。
            if (ra.processName.equals("system")) {
            }

            sb.append("\n").append(ra.processName);
        }
        showMessage(sb.toString());
    }


    /**
     * 这种方法不好用
     */
    private void getRunProcessByRuntime() {
        for (int i = 0; i < 10000; i++) {
            QueuedWork.execute(new SafeRunnable() {
                public void safeRun() {
                    try {
                        Thread.sleep(2 * 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    runPS();
                }
            });
        }
    }

    private void runPS() {
        try {
            Process proc = Runtime.getRuntime().exec("ps");
            showMessage(convertStreamToString(proc.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String convertStreamToString(InputStream is) {
        /*
          * To convert the InputStream to String we use the BufferedReader.readLine()
          * method. We iterate until the BufferedReader return null which means
          * there's no more data to read. Each line will appended to a StringBuilder
          * and returned as String.
          */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    /**************************************************************************************
     * ************************************ 安装软件列表 ************************************
     **************************************************************************************/
    private void getAppList() {
        PackageManager pm = this.getPackageManager();
        //和tag PackageManager.GET_UNINSTALLED_PACKAGES功能一样
        List<PackageInfo> pakageinfos = pm.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES);
//        List<PackageInfo> pakageinfos = pm.getInstalledPackages(0);
        StringBuilder sb = new StringBuilder();
        sb.append("*************************************************\n")
                .append("***************** 安装软件列表 *****************\n")
                .append("*************************************************\n");
        for (PackageInfo packageInfo : pakageinfos) {
            // 获取应用程序的名称，不是包名，而是清单文件中的labelname
            String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
            String packageName = packageInfo.packageName;
            //给一同程序设置包名
            sb.append("\n").append("=====>>>").append(appName.trim()).append("<<<=====\n")
                    .append("PackageName:  " +
                            "").append(packageName).append("\n");
        }
        String result = sb.toString().trim();
        if (TextUtils.isEmpty(result)) {
            result = "获取失败";
        }
        showMessage(result);
    }

    /**************************************************************************************
     * ********************************** 定位信息,需要部分权限 ******************************
     **************************************************************************************/
    private void getlocation() {
        /**
         * http://blog.csdn.net/jiangwei0910410003/article/details/52836241
         第一、卫星定位
         GPS（Global Positioning System）即全球定位系统，是由美国建立的一个卫星导航定位系统，利用该系统，
         用户可以在全球范围内实现全天候、连续、实时的三维导航定位和测速；另外，利用该系统，用户还能够进行高精度的时间传递和高精度的精密定位。

         第二、基站定位
         移动电话测量不同基站的下行导频信号，得到不同基站下行导频的TOA（到达时刻）或 TDOA(到达时间差)，
         根据该测量结果并结合基站的坐标，一般采用三角公式估计算法，就能够计算出移动电话的位置。
         实际的位置估计算法需要考虑多基站(3个或3个以上)定位的情况，因此算法要复杂很多。
         一般而言，移动台测量的基站数目越多，测量精度越高，定位性能改善越明显。

         第三、WiFi定位
         每一个无线AP（路由器）都有一个全球唯一的MAC地址，并且一般来说无线AP在一段时间内不会移动；
         设备在开启Wi-Fi的情况下，无线路由器默认都会进行SSID广播（除非用户手动配置关闭该功能），
         在广播帧包含了该路由器的MAC地址；采集装置可以通过接收周围AP发送的广播信息获取周围AP的MAC信息和信号强度信息，
         将这些信息上传到服务器，经过服务器的计算，保存为“MAC-经纬度”的映射，当采集的信息足够多时候就在服务器上建立了一张巨大的WiFi信息网络；
         当一个设备处在这样的网络中时，可以将收集到的这些能够标示AP的数据发送到位置服务器，服务器检索出每一个AP的地理位置，
         并结合每个信号的强弱程度，计算出设备的地理位置并返回到用户设备，其计算方式和基站定位位置计算方式相似，
         也是利用三点定位或多点定位技术；位置服务商要不断更新、补充自己的数据库，以保证数据的准确性。
         当某些WiFi信息不在数据库中时，可以根据附近其他的WiFi位置信息推断出未知WiFi的位置信息，并上传服务器。

         第四、AGPS定位
         AGPS（AssistedGPS：辅助全球卫星定位系统）是结合GSM/GPRS与传统卫星定位，利用基地台代送辅助卫星信息，
         以缩减GPS芯片获取卫星信号的延迟时间，受遮盖的室内也能借基地台讯号弥补，减轻GPS芯片对卫星的依赖度。AGPS利用手机基站的信号，
         辅以连接远程定位服务器的方式下载卫星星历 (英语：Almanac Data)，再配合传统的GPS卫星接受器，让定位的速度更快。
         是一种结合网络基站信息和GPS信息对移动台进行定位的技术，既利用全球卫星定位系统GPS，又利用移动基站，解决了GPS覆盖的问题，
         可以在2代的G、C网络和3G网络中使用。

         在Android中关于这几种定位都有具体的调用方法，所以如果想修改系统的定位信息，那么就必须先了解这几种调用方式，
         在之前的一篇文章中也说到了，Hook的最关键一点就是需要找到Hook的地方，这个就需要去阅读源码来查找了。
         在Android中一般获取位置信息就涉及到下面的几个类和方法：
         */

        /**
         *第一个：采用基站定位信息
         android.telephony.TelephonyManager
         +getCellLocation
         +getPhoneCount
         +getNeighboringCellInfo
         +getAllCellInfo
         android.telephony.PhoneStateListener
         +onCellLocationChanged
         +onCellInfoChanged
         */
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        /**
         * 第二个：采用Wifi定位信息
         android.net.wifi.WifiManager
         +getScanResults
         +getWifiState
         +isWifiEnabled
         android.net.wifi.WifiInfo
         +getMacAddress
         +getSSID
         +getBSSID
         android.net.NetworkInfo
         +getTypeName
         +isConnectedOrConnecting
         +isConnected
         +isAvailable
         android.telephony.CellInfo
         +isRegistered
         */

        /**
         * 第三个：采用GPS定位
         android.location.LocationManager
         +getGpsStatus
         +getLastLocation
         +getLastKnownLocation
         +getProviders
         +getBestProvider
         +addGpsStatusListener
         +addNmeaListener
         */
    }

    /**************************************************************************************
     * ******************************* SIM卡相关信息获取,需要部分权限 ***************************
     **************************************************************************************/
    private void getSIMInfo() {
        /**
         * 需要权限 android.permission.READ_PHONE_STATE
         */
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String simState = "unknow";
        //卡状态
        switch (tm.getSimState()) {
            case TelephonyManager.SIM_STATE_READY:
                simState = "sim_state_ready";
                break;
            case TelephonyManager.SIM_STATE_ABSENT:
                simState = "sim_state_absent";
                break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                simState = "sim_state_pin_required";
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                simState = "sim_state_network_locked";
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                simState = "sim_state_puk_required";
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                simState = "sim_state_unknown";
                break;
            default:
                simState = "unknow";
                break;
        }
        //sim卡国家码
        String simCountryIso = tm.getSimCountryIso();
        // MCC+MNC
        String operator = tm.getSimOperator();
        //服务提供名字(SPN)
        String simOperatorName = tm.getSimOperatorName();
        String simSerialNumber = "unknow";
        //SIM卡序列号,需要权限android.Manifest.permission.READ_PHONE_STATE
        if (checkPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            simSerialNumber = tm.getSimSerialNumber();
        }
        //订阅ID.
        /**
         *  获取imsi(MCC+MNC+MIN)
         *      MCC：Mobile Country Code,移动国家码, 一般3位:中国为460
         *      MNC:Mobile Network Code,移动网络码, 一般为2位,也有1位(小于10的都是1位),也有3位(美国卡):电信03,移动02,联通GSM01
         *      MIN共有10位其结构如下:09+M0M1M2M3+ABCD 其中的M0M1M2M3和MDN号码中的H0H1H2H3可存在对应关系,ABCD四位为自由分配.
         *          可以看出IMSI在MIN号码前加了MCC,可以区别出每个用户的来自的国家,因此可以实现国际漫游.
         *          在同一个国家内,如果有多个CDMA运营商,可以通过MNC来进行区别.
         */
        String subscriberId = "unknow";
        if (checkPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            subscriberId = tm.getSubscriberId();
        }
        int mcc = getResources().getConfiguration().mcc;
        int mnc = getResources().getConfiguration().mnc;

        /**
         * 手机其他信息采集
         */

        String callState = "无活动";
        switch (tm.getCallState()) {
            case TelephonyManager.CALL_STATE_RINGING:
                callState = "响铃";
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                callState = "摘机";
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                callState = "无活动";
                break;
            default:
                callState = "无活动";
                break;
        }
        /**
         * 电话方位 需要权限 android.permission.ACCESS_COARSE_LOCATION</p>
         * 请求位置更新，如果更新将产生广播，接收对象为注册LISTEN_CELL_LOCATION的对象，需要的permission名称为ACCESS_COARSE_LOCATION。</p>
         * location.requestLocationUpdate();
         */

        String cellLoc = "获取失败";
        if (checkPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            CellLocation loc = tm.getCellLocation();
            cellLoc = loc.toString();
        }
        //device id
        String imei = "unknow";
        if (checkPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            imei = tm.getDeviceId();
        }
        //设备软件版本号
        String softwareVersion = "unknow";
        if (checkPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            softwareVersion = tm.getDeviceSoftwareVersion();
        }
        //手机号获取
        String lineNum = "unknow";
        if (checkPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            lineNum = tm.getLine1Number();
        }
        /**
         * 当前移动终端附近移动终端的信息 需要权限android.permission.ACCESS_COARSE_LOCATION
         */
//        StringBuilder sb = new StringBuilder();
//        if (checkPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
//            List<NeighboringCellInfo> infos = tm.getNeighboringCellInfo();
//            for (NeighboringCellInfo info : infos) {
//                // 获取小区号
//                int cid = info.getCid();
//                // 获取邻居小区LAC
//                // LAC:
//                // 位置区域码。为了确定移动台的位置，每个GSM/PLMN的覆盖区都被划分成许多位置区，LAC则用于标识不同的位置区。
//                int lac = info.getLac();
//                int networkType = info.getNetworkType();
//                int mPsc = info.getPsc();
//                // 获取邻居小区信号强度
//                int rssi = info.getRssi();
//                sb.append("小区号:").append(cid)
//                        .append(";LAC:").append(lac)
//                        .append(";networkType:").append(networkType)
//                        .append(";mPsc:").append(mPsc)
//                        .append(";小区信号强度:").append(rssi);
//            }
//        }

        //注册的ISO国家注册码
        String networkCountryIso = tm.getNetworkCountryIso();
        //MCC+MNC
        String networkOperator = tm.getNetworkOperator();
        //(当前已注册的用户)的名字
        String networkOperatorName = tm.getNetworkOperatorName();
        //当前使用的网络类型
        String networkType = "unknow";

        switch (tm.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                networkType = "network_type_1xrtt";
                break;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                networkType = "network_type_cdma";
                break;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                networkType = "network_type_edge";
                break;
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                networkType = "network_type_ehrpd";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                networkType = "network_type_evdo_0";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                networkType = "network_type_evdo_a";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                networkType = "network_type_evdo_b";
                break;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                networkType = "network_type_gprs";
                break;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                networkType = "network_type_hsdpa";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                networkType = "network_type_hspa";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                networkType = "network_type_hspap";
                break;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                networkType = "network_type_hsupa";
                break;
            case TelephonyManager.NETWORK_TYPE_IDEN:
                networkType = "network_type_iden";
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                networkType = "network_type_lte";
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                networkType = "network_type_umts";
                break;
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                networkType = "network_type_unknown";
                break;
            default:
                break;
        }
        //手机类型
        String phoneType = "unknow";
        switch (tm.getPhoneType()) {
            case TelephonyManager.PHONE_TYPE_NONE:
                phoneType = "phone_type_none";
                break;
            case TelephonyManager.PHONE_TYPE_GSM:
                phoneType = "phone_type_gsm";
                break;
            case TelephonyManager.PHONE_TYPE_CDMA:
                phoneType = "phone_type_cdma";
                break;
            case TelephonyManager.PHONE_TYPE_SIP:
                phoneType = "phone_type_sip";
                break;
            default:
                break;
        }
        //语音邮件号码
        String VoiceMailNumber = "unknow";
        if (checkPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            VoiceMailNumber = tm.getVoiceMailNumber();
        }
        //ICC卡是否存在
        boolean hasIccCard = tm.hasIccCard();
        //是否漫游
        boolean isNetworkRoaming = false;
        if (checkPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            isNetworkRoaming = tm.isNetworkRoaming();
        }

        //数据活动状态
        String dataActivity = "unknow";
        switch (tm.getDataActivity()) {
            case TelephonyManager.DATA_ACTIVITY_NONE:
                phoneType = "data_activity_none";
                break;
            case TelephonyManager.DATA_ACTIVITY_IN:
                phoneType = "data_activity_in";
                break;
            case TelephonyManager.DATA_ACTIVITY_OUT:
                phoneType = "data_activity_out";
                break;
            case TelephonyManager.DATA_ACTIVITY_INOUT:
                phoneType = "data_activity_inout";
                break;
            case TelephonyManager.DATA_ACTIVITY_DORMANT:
                phoneType = "data_activity_dormant";
                break;
            default:
                break;
        }
        //数据连接状态
        String dataState = "unknow";
        switch (tm.getDataState()) {
            case TelephonyManager.DATA_DISCONNECTED:
                phoneType = "data_disconnected";
                break;
            case TelephonyManager.DATA_CONNECTING:
                phoneType = "data_connecting";
                break;
            case TelephonyManager.DATA_CONNECTED:
                phoneType = "data_connected";
                break;
            case TelephonyManager.DATA_SUSPENDED:
                phoneType = "data_suspended";
                break;
            default:
                break;
        }
        String s = "================SIM卡相关信息=================="
                + "\nSIM状态:" + simState
                + "\nSIM国家码:" + simCountryIso
                + "\nSIM[mcc+mnc]:" + operator
                + "\n服务提供名字(SPN):" + simOperatorName
                + "\nSIM卡序列号(ICCID):" + simSerialNumber
                + "\nIMSI:" + subscriberId
                + "\nMCC[resources获取]:" + mcc
                + "\nMNC[resources获取]:" + mnc
                + "\n================手机其他信息==================="
                + "\n电话状态:" + callState
                + "\n电话方位:" + cellLoc
                + "\nIMEI:" + imei
                + "\n设备软件版本号:" + softwareVersion
                + "\n手机号:" + lineNum
//                + "\n附近电话信息:" + sb.toString()
                + "\n注册的ISO国家注册码:" + networkCountryIso
                + "\nnetworkOperator(MCC+MNC):" + networkOperator
                + "\n(当前已注册的用户)的名字:" + networkOperatorName
                + "\n当前使用的网络类型:" + networkType
                + "\n手机类型:" + phoneType
                + "\n语音邮件号码:" + VoiceMailNumber
                + "\nICC卡是否存在:" + hasIccCard
                + "\n是否漫游:" + isNetworkRoaming
                + "\n数据活动状态:" + dataActivity
                + "\n数据连接状态:" + dataState;
        showMessage(s);

    }

    /**************************************************************************************
     * ******************************* 蓝牙/无线相关的,需要部分权限 ***************************
     **************************************************************************************/
    private void getBluetoothAndWifoInfo() {


        /**
         * 需要权限  <uses-permission android:name="android.permission.BLUETOOTH" />
         */
        BluetoothAdapter bAdapt = BluetoothAdapter.getDefaultAdapter();
        //蓝牙地址
        String bluetoothAddr = bAdapt.getAddress();
        //Bluetooth name
        String bluetoothName = bAdapt.getName();

        //蓝牙状态
        String bluetoothState = "idle";
        switch (bAdapt.getState()) {
            case BluetoothAdapter.STATE_ON:
                bluetoothState = "state_on";
                break;
            case BluetoothAdapter.STATE_OFF:
                bluetoothState = "state_off";
                break;
            case BluetoothAdapter.STATE_CONNECTED:
                bluetoothState = "state_connected";
                break;
            case BluetoothAdapter.STATE_CONNECTING:
                bluetoothState = "state_connecting";
                break;
            case BluetoothAdapter.STATE_DISCONNECTED:
                bluetoothState = "state_disconnected";
                break;
            case BluetoothAdapter.STATE_DISCONNECTING:
                bluetoothState = "state_disconnecting";
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                bluetoothState = "state_turning_off";
                break;
            case 14:
                bluetoothState = "state_ble_turning_on";
                break;
            case 15:
                bluetoothState = "state_ble_on";
                break;
            case 16:
                bluetoothState = "state_ble_turning_off";
                break;
            default:
                break;
        }

        /**
         * 需要权限 <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
         */
        WifiManager mWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifi.getConnectionInfo();
        //连接wifi的地址
        String bssidName = wifiInfo.getBSSID();
        //连接wifi名字
        String ssidName = wifiInfo.getSSID();
        //mac地址
        String wifiMac = wifiInfo.getMacAddress();

        //当前频率
        int frequency = -1;
        if (Build.VERSION.SDK_INT > 20) {
            try {
                frequency = wifiInfo.getFrequency();
            } catch (Throwable e) {

            }
        }
        //int ip值
        int ip = wifiInfo.getIpAddress();
        //当前链接速度
        int linkSpeed = wifiInfo.getLinkSpeed();
        //是否为隐藏wifi
        boolean isHiddenSSID = wifiInfo.getHiddenSSID();
        //当前网络信号,单位dBm
        int rssi = wifiInfo.getRssi();
        //网络ID
        int networkID = wifiInfo.getNetworkId();

        String macAddress = android.provider.Settings.Secure.getString(getContentResolver(), "bluetooth_address");

        /**
         * 结果拼接
         */
        String s = "===============蓝牙信息==============="
                + "\n蓝牙名字:" + bluetoothName
                + "\n蓝牙MAC[官方API]:" + bluetoothAddr
                + "\n蓝牙MAC[系统数据库方式获取]:" + macAddress
                + "\n蓝牙状态:" + bluetoothState
                + "\n===============无线信息==============="
                + "\n无线名字:" + ssidName
                + "\n无线MAC[官方API获取]:" + wifiMac
                + "\n无线MAC[javaAPI获取]:" + getMacByJavaAPI()
                + "\nBssid:" + bssidName
                + "\n当前频率:" + frequency
                + "\nWIFI信道[系统API获取]:" + getCurrentChannel()
                + "\nWIFI信道[根据频率获取]:" + getChannelByFrequency(frequency)
                + "\nip:" + longToIp(ip)
                + "\n当前链接速度:" + linkSpeed
                + "\n是否为隐藏wifi:" + isHiddenSSID
                + "\n信号强度:" + rssi + "dBm\n网络ID:" + networkID;
        showMessage(s);

    }


    /**
     * 获取系统MAC地址.需要权限<uses-permission android:name="android.permission.INTERNET" />
     *
     * @return
     */
    @TargetApi(9)
    private String getMacByJavaAPI() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface netInterface = interfaces.nextElement();
                if ("wlan0".equals(netInterface.getName()) || "eth0".equals(netInterface.getName())) {
                    byte[] addr = netInterface.getHardwareAddress();
                    if (addr == null || addr.length == 0) {
                        return null;
                    }
                    StringBuilder buf = new StringBuilder();
                    for (byte b : addr) {
                        buf.append(String.format("%02X:", b));
                    }
                    if (buf.length() > 0) {
                        buf.deleteCharAt(buf.length() - 1);
                    }
                    return buf.toString().toLowerCase(Locale.getDefault());
                }
            }
        } catch (Throwable e) {
        }
        return null;
    }

    public String longToIp(long i) {

        return ((i >> 24) & 0xFF) +
                "." + ((i >> 16) & 0xFF) +
                "." + ((i >> 8) & 0xFF) +
                "." + (i & 0xFF);

    }


    public int getCurrentChannel() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();// 当前wifi连接信息
        List<ScanResult> scanResults = wifiManager.getScanResults();
        for (ScanResult result : scanResults) {
            if (result.BSSID.equalsIgnoreCase(wifiInfo.getBSSID())
                    && result.SSID.equalsIgnoreCase(wifiInfo.getSSID()
                    .substring(1, wifiInfo.getSSID().length() - 1))) {
                return getChannelByFrequency(result.frequency);
            }
        }

        return -1;
    }

    /**
     * 根据频率获得信道
     *
     * @param frequency
     * @return
     */
    public int getChannelByFrequency(int frequency) {
        int channel = -1;
        switch (frequency) {
            case 2412:
                channel = 1;
                break;
            case 2417:
                channel = 2;
                break;
            case 2422:
                channel = 3;
                break;
            case 2427:
                channel = 4;
                break;
            case 2432:
                channel = 5;
                break;
            case 2437:
                channel = 6;
                break;
            case 2442:
                channel = 7;
                break;
            case 2447:
                channel = 8;
                break;
            case 2452:
                channel = 9;
                break;
            case 2457:
                channel = 10;
                break;
            case 2462:
                channel = 11;
                break;
            case 2467:
                channel = 12;
                break;
            case 2472:
                channel = 13;
                break;
            case 2484:
                channel = 14;
                break;
            case 5745:
                channel = 149;
                break;
            case 5765:
                channel = 153;
                break;
            case 5785:
                channel = 157;
                break;
            case 5805:
                channel = 161;
                break;
            case 5825:
                channel = 165;
                break;
        }
        return channel;
    }

    /**************************************************************************************
     * ****************************** 电源相关的,不需要任何权限 *******************************
     **************************************************************************************/

    protected static final int MSG_REFRESH_UI = 0x999;
    private TextView tv;

    /**
     * 获取电源相关的,注解广播接收器实现
     */
    private void getBattery() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        this.registerReceiver(mBroadcastReceiver, filter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                int status = intent.getIntExtra("status", 0);
                int health = intent.getIntExtra("health", 0);
                boolean present = intent.getBooleanExtra("present", false);
                int level = intent.getIntExtra("level", 0);
                int scale = intent.getIntExtra("scale", 0);
                int icon_small = intent.getIntExtra("icon-small", 0);
                int plugged = intent.getIntExtra("plugged", 0);
                int voltage = intent.getIntExtra("voltage", 0);
                int temperature = intent.getIntExtra("temperature", 0);
                String technology = intent.getStringExtra("technology");

                String statusString = "";
                switch (status) {
                    case BatteryManager.BATTERY_STATUS_UNKNOWN:
                        statusString = "unknown";
                        break;
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        statusString = "charging";
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        statusString = "discharging";
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        statusString = "not charging";
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        statusString = "full";
                        break;
                }

                String healthString = "";
                switch (health) {
                    case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                        healthString = "unknown";
                        break;
                    case BatteryManager.BATTERY_HEALTH_GOOD:
                        healthString = "good";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                        healthString = "overheat";
                        break;
                    case BatteryManager.BATTERY_HEALTH_DEAD:
                        healthString = "dead";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                        healthString = "voltage";
                        break;
                    case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                        healthString = "unspecified failure";
                        break;
                }

                String acString = "";

                switch (plugged) {
                    case BatteryManager.BATTERY_PLUGGED_AC:
                        acString = "plugged ac";
                        break;
                    case BatteryManager.BATTERY_PLUGGED_USB:
                        acString = "plugged usb";
                        break;
                }
                String s = "状态:" + statusString + "\n"
                        + "健康:" + healthString + "\n"
                        + "是否存在电池:" + String.valueOf(present) + "\n"
                        + "获得当前电量:" + String.valueOf(level) + "\n"
                        + "获得总电量:" + String.valueOf(scale) + "\n"
                        + "图标ID:" + String.valueOf(icon_small) + "\n"
                        + "连接的电源插座:" + acString + "\n"
                        + "电压:" + String.valueOf(voltage) + "\n"
                        + "温度:" + String.valueOf(temperature) + "\n"
                        + "电池类型:" + technology + "\n";
                showMessage(s);
            }
        }
    };


    /**
     * 获取电源相关的. api大于21才可以使用
     */
    @TargetApi(21)
    public void getBatteryManagerByLargeThan21() {
        BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
        System.out.println(bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER));
        System.out.println(bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE));
        System.out.println(bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW));
    }

    /**************************************************************************************
     * *************************** UI展示机制,外部调用showMessage即可 ***********************
     **************************************************************************************/

    /**
     * 告诉UI需要展示信息
     *
     * @param s
     */
    private void showMessage(String s) {
        Message msg = new Message();
        msg.what = MSG_REFRESH_UI;
        msg.obj = s;
        mhHandler.sendMessage(msg);
    }

    /**
     * UI展示信息
     */
    private Handler mhHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REFRESH_UI:
                    tv = (TextView) findViewById(id.textView);
                    tv.setText(msg.obj.toString());
                    break;
            }
        }
    };


    /**
     * 判断当前应用是否具有指定的权限
     *
     * @param context
     * @param permission 权限信息的完整名称 如：<code>android.permission.INTERNET</code>
     * @return 当前仅当宿主应用含有 参数 permission 对应的权限 返回true 否则返回 false
     */
    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class<?> clazz = Class.forName("android.content.Context");
                Method method = clazz.getMethod("checkSelfPermission", String.class);
                int rest = (Integer) method.invoke(context, permission);
                if (rest == PackageManager.PERMISSION_GRANTED) {
                    result = true;
                } else {
                    result = false;
                }
            } catch (Throwable e) {
                result = false;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }

}
