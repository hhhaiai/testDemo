package com.test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import com.test.utils.AppUtils;
import com.test.utils.CTelephoneInfo;
import com.test.utils.QueuedWork;
import com.test.utils.SafeRunnable;

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
import android.content.res.Resources;
import android.database.Cursor;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final String T = "sanbo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
        case R.id.btn1:
            /**
             * 获取电源信息
             */
            getBattery();
            // API大于21的时候获取电源信息
            // getBatteryManagerByLargeThan21();
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
            getOtherInfo();
            break;
        case R.id.btn8:
            /**
             * 是否双卡,双卡信息获取
             */
            getDoubelCard();
            break;
        case R.id.btn9:
            /**
             * 相机参数
             */
            getCameraInfo();
            break;
        case R.id.btn10:
            /**
             * 分辨率信息
             */
            getDisPlayInfo();
            break;
        case R.id.btn11:
            /**
             * 硬件信息
             */
            getDeviceInfo();
            break;
        case R.id.btn12:
            /**
             * 环境变量信息
             */
            getEnvInfo();
            break;
        case R.id.btn13:
            /**
             * NFC信息
             */
            getNFCInfo();
            break;
        case R.id.btn14:
            /**
             * 模拟器识别相关
             */
            getEmuInfo();
            break;
        case R.id.btn15:
            break;
        default:
            break;
        }

    }

    /**************************************************************************************
     * ************************************* 模拟器识别
     * ***********************************
     **************************************************************************************/

    /**
     * 模拟器识别,SDK识别要点: 1.厂商判断是否为Vbox(window
     * 部分QT写的可能是比较特殊,待进一步确定)对应字段:ro.product.manufacturer
     * 2.判断是否为x86机型.(需要服务端维护设备表对应,eg:三星notex,肯定不是x86的) 对应字段:ro.product.cpu.abi
     * 3.查看有线还是无线 4.adb状态
     * ro.secure设为0，persist.service.adb.enable设为1,adbd就以为root权限运行了.还有说法ro.adb.secure=0就可以随便玩了
     * 5.关键字判断模拟器.microvirtd是逍遥模拟器/genyd是genymotion模拟器 gps逍遥模拟器
     * ro.microvirtd.caps.gps=on ? genymition模拟器 ro.genyd.caps.gps=on? 天天模拟器
     * ro.ttvmd.caps.gps=on
     */
    private void getEmuInfo() {
        try {
            Properties prop = getBuildProp();
            sb = new StringBuilder();
            sb.append("**************************************\n").append("********模拟器识别case********\n")
                    .append("***************************************\n\n")
                    .append("\n==========关键字判断模拟器==============\n");

            /**
             * 海马玩模拟器 ro.product.name/device (ro.build.prodyct)都是Droid4X
             * 蓝叠(Bluestack2)模拟器 ro.product.name kletxx
             * ro.product.brand/ro.board.platform msm8974
             */
            String s = getSystemBuildProp();
            String res = "未知";
            if (s.contains("microvirtd")) {
                res = "逍遥模拟器";
            } else if (s.contains("genyd")) {
                res = "Genymotion模拟器";
            } else if (s.contains("ttvmd")) {
                res = "天天模拟器";
            } else if (s.contains("都是Droid4X")) {
                res = "海马玩模拟器";
            }
            sb.append("关键字识别[文件]:").append(res).append("\n");
            s = "";
            res = "未知";
            s = shell("getprop");
            if (s.contains("microvirtd")) {
                res = "逍遥模拟器";
            } else if (s.contains("genyd")) {
                res = "Genymotion模拟器";
            } else if (s.contains("ttvmd")) {
                res = "天天模拟器";
            } else if (s.contains("都是Droid4X")) {
                res = "海马玩模拟器";
            }
            sb.append("关键字识别[runtime]:").append(res).append("\n");

            sb.append("\n==========制造商==============\n").append("制造商[manufacturer]:").append(Build.MANUFACTURER)
                    .append("\n").append("制造商[runtime获取]:").append(shell("getprop ro.product.manufacturer"))
                    .append("\n").append("制造商[build.prop获取]:").append(prop.get("ro.product.manufacturer")).append("\n")
                    .append("\n========网络(有线/无线)=========\n").append("wifi[runtime获取]:")
                    .append(shell("getprop wifi.interface")).append("\n").append("wifi[build.prop获取]:")
                    .append(prop.get("wifi.interface")).append("\n").append("\n===========ADB状态=============\n")
                    .append("ro.secure[shell获取]:").append(shell("getprop ro.secure")).append("\n")
                    .append("ro.secure[build.prop获取]:").append(prop.get("ro.secure")).append("\n")
                    .append("persist.service.adb.enable[shell获取]:").append(shell("getprop persist.service.adb.enable"))
                    .append("\n").append("persist.service.adb.enable[build.prop]:")
                    .append(prop.get("persist.service.adb.enable")).append("\n").append("ro.adb.secure[shell获取]:")
                    .append(shell("getprop ro.adb.secure")).append("\n").append("ro.adb.secure[build.prop]:")
                    .append(prop.get("ro.adb.secure")).append("\n").append("\n===========CPU情况=============\n")
                    .append("cpu[CPU_ABI]:").append(Build.CPU_ABI).append("\n").append("cpu[runtime获取]:")
                    .append(shell("getprop ro.product.cpu.abi")).append("\n")
                    .append("********cpu详情[文件获取/proc/cpuinfo]***************\n").append(getCPU()).append("\n");
            if (Build.VERSION.SDK_INT > 20) {
                sb.append("cpu>21[supported_abis]:").append(Arrays.asList(Build.SUPPORTED_ABIS)).append("\n");
            }
            showMessage(sb.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static String shell(String shell) {
        if (TextUtils.isEmpty(shell)) {
            System.err.println("命令是空的,亲~");
            return null;
        }
        Process proc = null;
        BufferedInputStream in = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            proc = Runtime.getRuntime().exec(shell);
            in = new BufferedInputStream(proc.getInputStream());
            br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {

                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    public String getCPU() {
        return getInfo("/proc/cpuinfo");
    }

    public String getSystemBuildProp() {
        return getInfo("/system/build.prop");
    }

    public String getInfo(String filePath) {
        String cpuInfo = null;

        StringBuffer sb = new StringBuffer();
        FileReader fstream = null;
        BufferedReader in = null;

        try {
            fstream = new FileReader(filePath);
            if (fstream != null) {
                try {
                    in = new BufferedReader(fstream, 1024);
                    // cpuInfo = in.readLine();
                    while ((cpuInfo = in.readLine()) != null) {
                        sb.append("\t\t").append(cpuInfo).append("\n");
                    }
                } catch (Throwable e) {
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (Exception e) {
                        }
                    }
                    if (fstream != null) {
                        try {
                            fstream.close();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        } catch (Exception e) {
        }

        return sb.toString();
    }

    /**************************************************************************************
     * *************************************NFC信息
     * ***********************************
     **************************************************************************************/
    /**
     * 获取NFC信息. 需要权限:<uses-permission android:name="android.permission.NFC"/>
     * 限制最低版本:14
     * 限制安装硬件,要求当前设备必须要有NFC芯片:<uses-feature android:name="android.hardware.nfc"
     * android:required="true" />
     */
    private void getNFCInfo() {
        sb = new StringBuilder();
        sb.append("**************************************\n").append("********NFC信息********\n")
                .append("***************************************\n");
        NfcAdapter mAdapter = NfcAdapter.getDefaultAdapter(this);
        try {
            sb.append("NFC功能是否打开:").append(mAdapter.isEnabled()).append("\n");
            // 需要权限ACTION_NFCSHARING_SETTINGS
            if (Build.VERSION.SDK_INT > 15) {
                sb.append("NDEF push功能是否开启:").append(mAdapter.isNdefPushEnabled());
            }
        } catch (Throwable e) {
        }
        showMessage(sb.toString());

    }

    /**************************************************************************************
     * ************************************* 环境变量信息
     * ***********************************
     **************************************************************************************/

    private void getEnvInfo() {
        sb = new StringBuilder();
        sb.append("**************************************\n").append("******** getprop获取信息********\n")
                .append("***************************************\n");
        try {
            Process proc = Runtime.getRuntime().exec("getprop");
            sb.append(convertStreamToString(proc.getInputStream()));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        sb.append("************************************\n").append("/system/build.prop获取信息\n")
                .append("************************************\n");
        try {
            Properties buildProp = getBuildProp();
            sb.append(buildProp.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        sb.append("************************************\n").append("Settings.System获取信息\n")
                .append("************************************\n");
        Cursor cur = null;
        Uri uri = Uri.parse("content://settings/system");
        try {
            // cur = managedQuery(uri, null, null, null, null);
            cur = getContentResolver().query(uri, null, null, null, null);
            if (cur != null) {
                while (cur.moveToNext()) {
                    sb.append(cur.getString(cur.getColumnIndex("name"))).append(":")
                            .append(cur.getString(cur.getColumnIndex("value"))).append("\n");
                }
            }
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
        sb.append("************************************\n").append("Settings.Secure获取信息\n")
                .append("************************************\n");
        try {
            uri = Uri.parse("content://settings/secure");
            cur = getContentResolver().query(uri, null, null, null, null);
            if (cur != null) {
                while (cur.moveToNext()) {
                    sb.append(cur.getString(cur.getColumnIndex("name"))).append(":")
                            .append(cur.getString(cur.getColumnIndex("value"))).append("\n");
                }
            }
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
        sb.append("************************************\n").append("Settings.Global获取信息\n")
                .append("************************************\n");
        try {
            uri = Uri.parse("content://settings/global");
            cur = getContentResolver().query(uri, null, null, null, null);
            if (cur != null) {
                while (cur.moveToNext()) {
                    sb.append(cur.getString(cur.getColumnIndex("name"))).append(":")
                            .append(cur.getString(cur.getColumnIndex("value"))).append("\n");
                }
            }
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
        showMessage(sb.toString());
    }

    /**
     * 获取系统的build.prop文件内容
     *
     * @return
     */

    private Properties getBuildProp() {
        Properties prop = new Properties();
        FileInputStream in = null;
        try {
            in = new FileInputStream(new File(Environment.getRootDirectory(), "build.prop"));
            prop.load(in);
        } catch (Throwable e) {

        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Throwable e) {
                }
            }
        }
        return prop;
    }

    /**************************************************************************************
     * ************************************* 硬件设备信息
     * ***********************************
     **************************************************************************************/

    private void getDeviceInfo() {
        sb = new StringBuilder();
        sb.append("*******************************\n").append("******** 硬件设备信息 *********\n")
                .append("********************************\n");
        sb.append("Mac[Java-API获取]:").append(getMacByJavaAPI()).append("\n");
        sb.append("Mac[系统API获取]:").append(getMacBySystemInterface(this)).append("\n");
        sb.append("Mac[Shell获取]:").append(getMacShell()).append("\n");
        sb.append("Android_id:").append(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID))
                .append("\n");
        sb.append("SerialNo:").append(getSerialNo()).append("\n");

        sb.append("========Build获取信息=======").append("\n");
        sb.append("BOARD:").append(Build.BOARD).append("\n");
        sb.append("BOOTLOADER:").append(Build.BOOTLOADER).append("\n");
        sb.append("BRAND:").append(Build.BRAND).append("\n");
        sb.append("DEVICE:").append(Build.DEVICE).append("\n");
        sb.append("DISPLAY:").append(Build.DISPLAY).append("\n");
        sb.append("FINGERPRINT:").append(Build.FINGERPRINT).append("\n");
        sb.append("HARDWARE:").append(Build.HARDWARE).append("\n");
        sb.append("HOST:").append(Build.HOST).append("\n");
        sb.append("ID:").append(Build.ID).append("\n");
        sb.append("MANUFACTURER:").append(Build.MANUFACTURER).append("\n");
        sb.append("MODEL:").append(Build.MODEL).append("\n");
        sb.append("PRODUCT:").append(Build.PRODUCT).append("\n");
        sb.append("SERIAL:").append(Build.SERIAL).append("\n");
        if (Build.VERSION.SDK_INT > 20) {
            sb.append("SUPPORTED_32_BIT_ABIS:").append(Build.SUPPORTED_32_BIT_ABIS).append("\n");
            sb.append("SUPPORTED_64_BIT_ABIS:").append(Build.SUPPORTED_64_BIT_ABIS).append("\n");
            sb.append("SUPPORTED_ABIS:").append(Build.SUPPORTED_ABIS).append("\n");
        }
        sb.append("TAGS:").append(Build.TAGS).append("\n");
        sb.append("USER:").append(Build.USER).append("\n");
        sb.append("CPU_ABI:").append(Build.CPU_ABI).append("\n");
        sb.append("CPU_ABI2:").append(Build.CPU_ABI2).append("\n");
        sb.append("RADIO:").append(Build.RADIO).append("\n");
        showMessage(sb.toString());
    }

    /**
     * 获取序列号.2.3以上版本支持
     *
     * @return
     */
    @TargetApi(9)
    private static String getSerialNo() {
        String serialNo = "";
        if (android.os.Build.VERSION.SDK_INT >= 9) {
            serialNo = android.os.Build.SERIAL;
        }
        return serialNo;
    }

    /**
     * 通过系统api获取mac地址.
     *
     * @param context
     * @return
     */
    private static String getMacBySystemInterface(Context context) {
        try {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (AppUtils.checkPermission(context, Manifest.permission.ACCESS_WIFI_STATE)) {
                WifiInfo info = wifi.getConnectionInfo();
                return info.getMacAddress();
            } else {
                return "";
            }
        } catch (Throwable e) {
            return "";
        }
    }

    /**
     * 通过读取文件,获取设备mac信息
     * </p>
     * 不需要权限
     *
     * @return
     */
    private static String getMacShell() {
        try {
            String[] urls = new String[] { "/sys/class/net/wlan0/address", "/sys/class/net/eth0/address",
                    "/sys/devices/virtual/net/wlan0/address" };
            String mc;
            for (int i = 0; i < urls.length; i++) {
                try {
                    mc = reaMac(urls[i]);
                    if (mc != null) {
                        return mc;
                    }
                } catch (Throwable e) {
                }
            }
        } catch (Throwable e) {
        }

        return null;
    }

    private static String reaMac(String url) {
        String macInfo = null;
        try {
            FileReader fstream = new FileReader(url);
            BufferedReader in = null;
            if (fstream != null) {
                try {
                    in = new BufferedReader(fstream, 1024);
                    macInfo = in.readLine();
                } finally {
                    if (fstream != null) {
                        try {
                            fstream.close();
                        } catch (Throwable e) {

                        }
                    }
                    if (in != null) {
                        try {
                            in.close();
                        } catch (Throwable e) {

                        }
                    }
                }
            }
        } catch (Throwable e) {
        }
        return macInfo;
    }

    /**************************************************************************************
     * *********************************** 传感器／重力感应等
     * ********************************
     **************************************************************************************/

    private void getOtherInfo() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // 从系统服务中获得传感器管理器

        List<Sensor> allSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        // 从传感器管理器中获得全部的传感器列表
        sb = new StringBuilder();
        sb.append("==================================").append("\n");
        sb.append("========传感器品牌[").append(allSensors.size()).append("]=============").append("\n");
        sb.append("===================================").append("\n");
        for (Sensor s : allSensors) {// 显示每个传感器的具体信息
            switch (s.getType()) {

            case Sensor.TYPE_ACCELEROMETER:
                sb.append(s.getType() + " 加速度传感器accelerometer").append("\n");
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                sb.append(s.getType() + " 温度传感器temperature").append("\n");
                break;
            case Sensor.TYPE_GRAVITY:
                sb.append(s.getType() + " 重力传感器gravity").append("\n");
                break;
            case Sensor.TYPE_GYROSCOPE:
                sb.append(s.getType() + " 陀螺仪传感器gyroscope").append("\n");
                break;
            case Sensor.TYPE_LIGHT:
                sb.append(s.getType() + " 环境光线传感器light").append("\n");
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                sb.append(s.getType() + " 线性加速度传感器linear_accelerometer").append("\n");
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                sb.append(s.getType() + " 电磁场传感器magnetic").append("\n");
                break;
            case Sensor.TYPE_ORIENTATION:
                sb.append(s.getType() + " 方向传感器orientation").append("\n");
                break;
            case Sensor.TYPE_PRESSURE:
                sb.append(s.getType() + " 压力传感器pressure").append("\n");
                break;
            case Sensor.TYPE_PROXIMITY:
                sb.append(s.getType() + " 距离传感器proximity").append("\n");
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                sb.append(s.getType() + " 湿度传感器relative_humidity").append("\n");
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                sb.append(s.getType() + " 旋转矢量传感器rotation_vector").append("\n");
                break;
            case Sensor.TYPE_TEMPERATURE:
                sb.append(s.getType() + " 温度传感器temperature").append("\n");
                break;
            default:
                sb.append(s.getType() + " 未知传感器").append("\n");
                break;
            }
            sb.append("设备名称: ").append(s.getName()).append("\n").append("设备版本: ").append(s.getVersion()).append("\n")
                    .append("供应商: ").append(s.getVendor()).append("\n");
        }
        showMessage(sb.toString());
    }

    /**************************************************************************************
     * ************************************ 双卡信息
     * ***************************************
     **************************************************************************************/
    private void getDoubelCard() {
        try {
            CTelephoneInfo telephonyInfo = CTelephoneInfo.getInstance(this);
            telephonyInfo.setCTelephoneInfo(this);
            String imeiSIM1 = telephonyInfo.getImeiSIM1();
            String imeiSIM2 = telephonyInfo.getImeiSIM2();
            String iNumeric1 = telephonyInfo.getINumeric1();
            String iNumeric2 = telephonyInfo.getINumeric2();
            boolean network1 = telephonyInfo.isDataConnected1();
            boolean network2 = telephonyInfo.isDataConnected2();
            boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
            boolean isSIM2Ready = telephonyInfo.isSIM2Ready();
            boolean isDualSIM = telephonyInfo.isDualSim();
            StringBuilder sb = new StringBuilder();
            sb.append("============双卡信息=========").append("\n");
            if (TextUtils.equals(imeiSIM1, imeiSIM2)) {
                sb.append("不是双卡手机").append("\n");
            } else {
                sb.append("imeiSIM1").append(imeiSIM2).append("\n").append("imeiSIM2").append(imeiSIM2).append("\n")
                        .append("iNumeric1").append(iNumeric1).append("\n").append("iNumeric2").append(iNumeric2)
                        .append("\n").append("network1").append(network1).append("\n").append("network2")
                        .append(network2).append("\n").append("isSIM1Ready").append(isSIM1Ready).append("\n")
                        .append("isSIM2Ready").append(isSIM2Ready).append("\n").append("isDualSIM").append(isDualSIM)
                        .append("\n");
            }
            showMessage(sb.toString());

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**************************************************************************************
     * ************************************ 获取相机信息
     * ************************************
     **************************************************************************************/

    /**
     * 获取相机信息.需要权限 <uses-permission android:name="android.permission.CAMERA"/>
     */
    private void getCameraInfo() {
        try {
            if (Build.VERSION.SDK_INT > 22) {
                ActivityCompat.requestPermissions(this,
                        new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
            }
            StringBuilder sb = new StringBuilder();
            int num = Camera.getNumberOfCameras();
            sb.append("本部手机有").append(num).append("个摄像头").append(":\n");
            for (int i = 0; i < num; i++) {
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(i, info);
                // 前置摄像头
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    // stopFaceDetection();
                    sb.append("=============前置摄像头==========").append(":\n");
                } else {
                    sb.append("=============后置摄像头==========").append(":\n");
                }

                Camera camera = Camera.open(i);
                Camera.Parameters param = camera.getParameters();
                List<Camera.Size> preSizes = param.getSupportedPreviewSizes();
                sb.append("\t").append("支持预览分辨率:").append("\n");
                for (int j = 0; j < preSizes.size(); j++) {
                    sb.append("\t\t").append(preSizes.get(j).width).append("*").append(preSizes.get(j).height)
                            .append("===>").append(preSizes.get(j).width * preSizes.get(j).height / 10000).append("\n");
                }
                List<Camera.Size> picSizes = param.getSupportedPictureSizes();
                sb.append("\t").append("支持图片分辨率:").append("\n");
                for (int j = 0; j < picSizes.size(); j++) {
                    sb.append("\t\t").append(picSizes.get(j).width).append("*").append(picSizes.get(j).height)
                            .append("===>").append(picSizes.get(j).width * picSizes.get(j).height / 10000).append("\n");
                }
            }
            showMessage(sb.toString());
        } catch (Throwable e) {

        }
    }

    /**************************************************************************************
     * ************************************ 获取分辨率信息
     * ************************************
     **************************************************************************************/

    /**
     * 获取分辨率信息
     */
    private void getDisPlayInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("========").append("WindowManager.getDefaultDisplay().getMetrics(DisplayMetrics)获取")
                .append("=======\n");
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
     * ************************************ 运行软件列表
     * ************************************
     **************************************************************************************/
    /**
     * 运行时软件列表入口
     */
    private void getRunningProcess() {
        // getRunProcessByRuntime();
        // getRunProcessBySystemAPI();
        // getRunProcessLargeThen5BySystemAPI();
        if (Build.VERSION.SDK_INT < 21) {
            getRunProcessBySystemAPI();
        } else {
            getRunProcessLargeThen5BySystemAPI();
        }
    }

    /**
     * 1. 此方法只在android5.0以上有效 2.
     * AndroidManifest中加入此权限<uses-permission xmlns:tools=
     * "http://schemas.android.com/tools" android:name=
     * "android.permission.PACKAGE_USAGE_STATS" tools:ignore=
     * "ProtectedPermissions" /> 3. 打开手机设置，点击安全-高级，在有权查看使用情况的应用中，为这个App打上勾
     */
    @TargetApi(21)
    private void getRunProcessLargeThen5BySystemAPI() {
        long ts = System.currentTimeMillis();
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) getApplicationContext()
                .getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> usageStats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST,
                ts - 1000 * 10, ts);
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
        // class RecentUseComparator implements Comparator<UsageStats> {
        // @Override
        // public int compare(UsageStats lhs, UsageStats rhs) {
        // return (lhs.getLastTimeUsed() > rhs.getLastTimeUsed()) ? -1 :
        // (lhs.getLastTimeUsed() == rhs.getLastTimeUsed()) ? 0 : 1;
        // }
        // }
        // RecentUseComparator mRecentComp = new RecentUseComparator();
        // Collections.sort(usageStats, mRecentComp);
        // String currentTopPackage = usageStats.get(0).getPackageName();
        // Log.d(T,"currentTopPackage:"+currentTopPackage);
    }

    /**
     * 判断是否有用权限
     *
     * @param context
     *            上下文参数
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean HavaPermissionForTest(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid,
                    applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
    }

    /**
     * 在新版本也不稳定 不能用了
     */
    private void getRunProcessBySystemAPI() {
        // ActivityManager am = (ActivityManager)
        // getSystemService(Context.ACTIVITY_SERVICE);
        // List<ActivityManager.AppTask> ats = am.getAppTasks();
        // for (ActivityManager.AppTask at : ats) {
        // Log.e(T, at.getTaskInfo().baseActivity.toString());
        // }
        // List<ActivityManager.RunningAppProcessInfo> rps =
        // am.getRunningAppProcesses();
        // for (ActivityManager.RunningAppProcessInfo rp : rps) {
        // Log.d(T, "getRunningAppProcesses===>" + rp.processName);
        // }
        // List<ActivityManager.RunningServiceInfo> rss =
        // am.getRunningServices(0);
        // for (ActivityManager.RunningServiceInfo rs : rss) {
        // Log.d(T, "getRunningServices===>" + rs.process);
        // }

        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        // 获取正在运行的应用
        List<ActivityManager.RunningAppProcessInfo> run = am.getRunningAppProcesses();
        // 获取包管理器，在这里主要通过包名获取程序的图标和程序名
        PackageManager pm = this.getPackageManager();

        StringBuilder sb = new StringBuilder();
        sb.append("===========运行软件列表========");
        for (ActivityManager.RunningAppProcessInfo ra : run) {
            // 这里主要是过滤系统的应用和电话应用，当然你也可以把它注释掉。
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
         * To convert the InputStream to String we use the
         * BufferedReader.readLine() method. We iterate until the BufferedReader
         * return null which means there's no more data to read. Each line will
         * appended to a StringBuilder and returned as String.
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
     * ************************************ 安装软件列表
     * ************************************
     **************************************************************************************/
    private void getAppList() {
        PackageManager pm = this.getPackageManager();
        // 和tag PackageManager.GET_UNINSTALLED_PACKAGES功能一样
        List<PackageInfo> pakageinfos = pm.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES);
        // List<PackageInfo> pakageinfos = pm.getInstalledPackages(0);
        sb = new StringBuilder();
        sb.append("*************************************************\n")
                .append("***************** 安装软件列表 *****************\n")
                .append("*************************************************\n");
        for (PackageInfo packageInfo : pakageinfos) {
            // 获取应用程序的名称，不是包名，而是清单文件中的labelname
            String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
            String packageName = packageInfo.packageName;
            // 给一同程序设置包名
            sb.append("\n").append("=====>>>").append(appName.trim()).append("<<<=====\n").append("PackageName:  " + "")
                    .append(packageName).append("\n");
        }
        String result = sb.toString().trim();
        if (TextUtils.isEmpty(result)) {
            result = "获取失败";
        }
        showMessage(result);
    }

    /**************************************************************************************
     * ********************************** 定位信息,需要部分权限
     * ******************************
     **************************************************************************************/
    StringBuilder sb = null;

    private void getlocation() {
        /**
         * http://blog.csdn.net/jiangwei0910410003/article/details/52836241
         * 第一、卫星定位 GPS（Global Positioning
         * System）即全球定位系统，是由美国建立的一个卫星导航定位系统，利用该系统，
         * 用户可以在全球范围内实现全天候、连续、实时的三维导航定位和测速；另外，利用该系统，用户还能够进行高精度的时间传递和高精度的精密定位。
         * 
         * 第二、基站定位 移动电话测量不同基站的下行导频信号，得到不同基站下行导频的TOA（到达时刻）或 TDOA(到达时间差)，
         * 根据该测量结果并结合基站的坐标，一般采用三角公式估计算法，就能够计算出移动电话的位置。
         * 实际的位置估计算法需要考虑多基站(3个或3个以上)定位的情况，因此算法要复杂很多。
         * 一般而言，移动台测量的基站数目越多，测量精度越高，定位性能改善越明显。
         * 
         * 第三、WiFi定位 每一个无线AP（路由器）都有一个全球唯一的MAC地址，并且一般来说无线AP在一段时间内不会移动；
         * 设备在开启Wi-Fi的情况下，无线路由器默认都会进行SSID广播（除非用户手动配置关闭该功能），
         * 在广播帧包含了该路由器的MAC地址；采集装置可以通过接收周围AP发送的广播信息获取周围AP的MAC信息和信号强度信息，
         * 将这些信息上传到服务器，经过服务器的计算，保存为“MAC-经纬度”的映射，当采集的信息足够多时候就在服务器上建立了一张巨大的WiFi信息网络；
         * 当一个设备处在这样的网络中时，可以将收集到的这些能够标示AP的数据发送到位置服务器，服务器检索出每一个AP的地理位置，
         * 并结合每个信号的强弱程度，计算出设备的地理位置并返回到用户设备，其计算方式和基站定位位置计算方式相似，
         * 也是利用三点定位或多点定位技术；位置服务商要不断更新、补充自己的数据库，以保证数据的准确性。
         * 当某些WiFi信息不在数据库中时，可以根据附近其他的WiFi位置信息推断出未知WiFi的位置信息，并上传服务器。
         * 
         * 第四、AGPS定位
         * AGPS（AssistedGPS：辅助全球卫星定位系统）是结合GSM/GPRS与传统卫星定位，利用基地台代送辅助卫星信息，
         * 以缩减GPS芯片获取卫星信号的延迟时间，受遮盖的室内也能借基地台讯号弥补，减轻GPS芯片对卫星的依赖度。AGPS利用手机基站的信号，
         * 辅以连接远程定位服务器的方式下载卫星星历 (英语：Almanac Data)，再配合传统的GPS卫星接受器，让定位的速度更快。
         * 是一种结合网络基站信息和GPS信息对移动台进行定位的技术，既利用全球卫星定位系统GPS，又利用移动基站，解决了GPS覆盖的问题，
         * 可以在2代的G、C网络和3G网络中使用。
         * 
         * 在Android中关于这几种定位都有具体的调用方法，所以如果想修改系统的定位信息，那么就必须先了解这几种调用方式，
         * 在之前的一篇文章中也说到了，Hook的最关键一点就是需要找到Hook的地方，这个就需要去阅读源码来查找了。
         * 在Android中一般获取位置信息就涉及到下面的几个类和方法：
         */
        sb = new StringBuilder();

        /**
         * 第一个：采用基站定位信息 android.telephony.TelephonyManager +getCellLocation
         * +getPhoneCount +getNeighboringCellInfo +getAllCellInfo
         * android.telephony.PhoneStateListener +onCellLocationChanged
         * +onCellInfoChanged
         */
        sb.append("=============采用基站定位信息=========").append("\n");
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        /**
         * 当前设备位置。 需要权限 <uses-permission android:name=
         * "android.permission.ACCESS_COARSE_LOCATION" />
         * <uses-permission android:name=
         * "android.permission.ACCESS_FINE_LOCATION" />
         */
        if (AppUtils.checkPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            CellLocation c = tm.getCellLocation();
            sb.append("设备的当前位置: ").append(String.valueOf(c)).append("\n");
        }

        /**
         * 单双卡模式判断，6.0后api支持
         */
        if (Build.VERSION.SDK_INT > 22) {
            sb.append("手机支持数量[6.0以上api]: ");
            switch (tm.getPhoneCount()) {
            case 0:
                sb.append("不支持语音／短信和数据");
                break;
            case 1:
                sb.append("单待机模式(单sim卡功能)");
                break;
            case 2:
                sb.append("双待机模式");
                break;
            default:
                break;
            }
            sb.append("\n");
        }
        /**
         * 设备的邻居单元信息。需要权限 貌似新版本不好用 <uses-permission android:name=
         * "android.permission.ACCESS_COARSE_UPDATES" />
         */
        if (AppUtils.checkPermission(this, "android.permission.ACCESS_COARSE_LOCATION")) {
            Log.e(T, "申请了权限");
            List<NeighboringCellInfo> ncs = tm.getNeighboringCellInfo();
            Log.e(T, "附近设备单元信息－－－》" + ncs.size());
            sb.append("--------------设备附近的单元信息[").append(ncs.size()).append("]--------------\n").append(ncs.toString())
                    .append("\n");
        } else {
            Log.e(T, "没有权限");
        }

        /**
         * 需要权限。 <uses-permission android:name=
         * "android.permission.ACCESS_COARSE_LOCATION" />
         */
        if (Build.VERSION.SDK_INT > 16) {
            if (AppUtils.checkPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                List<CellInfo> cis = tm.getAllCellInfo();
                if (cis != null) {
                    sb.append("--------------主小区和附近小区信息[").append(cis.size()).append("]--------------\n")
                            .append(cis.toString()).append("\n");
                }
            }
        }

        if (AppUtils.checkPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

            tm.listen(new PhoneStateListener() {
                @Override
                public void onCellInfoChanged(List<CellInfo> cellInfo) {
                    super.onCellInfoChanged(cellInfo);
                    sb.append("--------------PhoneStateListener------------\n").append("onCellInfoChanged======>")
                            .append(cellInfo.toString()).append("\n");
                }

                @Override
                public void onCellLocationChanged(CellLocation location) {
                    super.onCellLocationChanged(location);
                    sb.append("--------------PhoneStateListener------------\n").append("onCellLocationChanged======>")
                            .append(location.toString()).append("\n");
                }
            }, PhoneStateListener.LISTEN_CELL_LOCATION);
        }

        /**
         * 第二个：采用Wifi定位信息 android.net.wifi.WifiManager +getScanResults
         * +getWifiState +isWifiEnabled android.net.wifi.WifiInfo +getMacAddress
         * +getSSID +getBSSID android.net.NetworkInfo +getTypeName
         * +isConnectedOrConnecting +isConnected +isAvailable
         * android.telephony.CellInfo +isRegistered
         */
        sb.append("=============WIFI定位信息=========").append("\n");
        /**
         * android N 以后推荐这么使用
         */
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        /**
         * 获取扫描到的wifi列表 需要权限 <uses-permission android:name=
         * "android.permission.ACCESS_COARSE_LOCATION" />
         * <uses-permission android:name=
         * "android.permission.ACCESS_FINE_LOCATION" />
         */
        if (AppUtils.checkPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            List<ScanResult> srs = wm.getScanResults();
            sb.append("wifi列表：").append(srs.toString()).append("\n");
        }

        sb.append("是否打开Wi-Fi：").append(wm.isWifiEnabled()).append("\n");
        sb.append("Wi-Fi状态：");
        switch (wm.getWifiState()) {
        case WifiManager.WIFI_STATE_DISABLED:
            sb.append("wifi_state_disabled");
            break;
        case WifiManager.WIFI_STATE_DISABLING:
            sb.append("wifi_state_disabling");
            break;
        case WifiManager.WIFI_STATE_ENABLED:
            sb.append("wifi_state_enabled");
            break;
        case WifiManager.WIFI_STATE_ENABLING:
            sb.append("wifi_state_enabling");
            break;
        case WifiManager.WIFI_STATE_UNKNOWN:
            sb.append("wifi_state_unknown");
            break;
        default:
            sb.append("未知状态");
            break;

        }
        sb.append("\n");
        if (Build.VERSION.SDK_INT > 20) {
            sb.append("是否支持5gWi-Fi：").append(wm.is5GHzBandSupported()).append("\n");
        }
        WifiInfo wifiInfo = wm.getConnectionInfo();
        // 连接wifi的地址
        String bssid = wifiInfo.getBSSID();
        // 连接wifi名字
        String ssid = wifiInfo.getSSID();
        // mac地址
        String macAddr = wifiInfo.getMacAddress();
        sb.append("连接wifi的地址:").append(bssid).append("\n");
        sb.append("连接wifi名字:").append(ssid).append("\n");
        sb.append("mac地址").append(macAddr).append("\n");

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            if (AppUtils.checkPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)) {
                NetworkInfo ni = cm.getActiveNetworkInfo();
                sb.append("网络连接类型:").append(ni.getTypeName()).append("\n");
                sb.append("是否网络连接中:").append(ni.isConnectedOrConnecting()).append("\n");
                sb.append("已经连接网络:").append(ni.isConnected()).append("\n");
                sb.append("是否可以连接网络:").append(ni.isAvailable()).append("\n");
            }

        }

        /**
         * 第三个：采用GPS定位 android.location.LocationManager +getGpsStatus
         * +getLastLocation 貌似已经废弃 +getLastKnownLocation +getProviders
         * +getBestProvider +addGpsStatusListener 替换方法registerGnssStatusCallback
         * +addNmeaListener
         */
        sb.append("=============GPS定位信息=========").append("\n");
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (AppUtils.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // 检索有关GPS引擎当前状态的信息。
            GpsStatus gs = lm.getGpsStatus(null);
            sb.append("GPS引擎当前状态:").append(gs.toString()).append("\n");
        }
        /**
         * 最后位置
         */
        if (AppUtils.checkPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            try {
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    sb.append("最后位置经度:").append(location.getLatitude()).append("\n");
                    sb.append("最后位置纬度:").append(location.getLongitude()).append("\n");
                    sb.append("最后位置海拔:").append(location.getAltitude()).append("\n");
                    sb.append("最后速度(米/秒):").append(location.getSpeed()).append("\n");
                    sb.append("最后时间(1970.1.1到现在的毫秒):").append(location.getTime()).append("\n");
                    sb.append("最后Accuracy:").append(location.getAccuracy()).append("\n");
                    sb.append("最后Bearing:").append(location.getBearing()).append("\n");
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        List<String> ss = lm.getProviders(true);
        sb.append("附近名称列表:").append(ss.toString()).append("\n");
        // lm.getBestProvider(Criteria.ACCURACY_HIGH,false);
        if (AppUtils.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            lm.addGpsStatusListener(new GpsStatus.Listener() {
                @Override
                public void onGpsStatusChanged(int event) {
                    sb.append("GpsStatus.Listener onGpsStatusChanged:").append(event).append("\n");
                }
            });
            lm.addNmeaListener(new GpsStatus.NmeaListener() {

                @Override
                public void onNmeaReceived(long timestamp, String nmea) {
                    sb.append("---------GpsStatus.NmeaListener onNmeaReceived---------\n").append("timestamp:")
                            .append(timestamp).append("\n").append("nmea:").append(nmea).append("\n");
                }
            });
        }

        showMessage(sb.toString());
    }

    /**************************************************************************************
     * ******************************* SIM卡相关信息获取,需要部分权限
     * ***************************
     **************************************************************************************/

    private void getSIMInfo() {
        /**
         * 需要权限 android.permission.READ_PHONE_STATE
         */
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String simState = "unknow";
        // 卡状态
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
        // sim卡国家码
        String simCountryIso = tm.getSimCountryIso();
        // MCC+MNC
        String operator = tm.getSimOperator();
        // 服务提供名字(SPN)
        String simOperatorName = tm.getSimOperatorName();
        String simSerialNumber = "unknow";
        // SIM卡序列号,需要权限android.Manifest.permission.READ_PHONE_STATE
        if (AppUtils.checkPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            simSerialNumber = tm.getSimSerialNumber();
        }
        // 订阅ID.
        /**
         * 获取imsi(MCC+MNC+MIN) MCC：Mobile Country Code,移动国家码, 一般3位:中国为460
         * MNC:Mobile Network Code,移动网络码,
         * 一般为2位,也有1位(小于10的都是1位),也有3位(美国卡):电信03,移动02,联通GSM01
         * MIN共有10位其结构如下:09+M0M1M2M3+ABCD
         * 其中的M0M1M2M3和MDN号码中的H0H1H2H3可存在对应关系,ABCD四位为自由分配.
         * 可以看出IMSI在MIN号码前加了MCC,可以区别出每个用户的来自的国家,因此可以实现国际漫游.
         * 在同一个国家内,如果有多个CDMA运营商,可以通过MNC来进行区别.
         */
        String subscriberId = "unknow";
        if (AppUtils.checkPermission(this, Manifest.permission.READ_PHONE_STATE)) {
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
         * 电话方位 需要权限 android.permission.ACCESS_COARSE_LOCATION
         * </p>
         * 请求位置更新，如果更新将产生广播，接收对象为注册LISTEN_CELL_LOCATION的对象，需要的permission名称为ACCESS_COARSE_LOCATION。
         * </p>
         * location.requestLocationUpdate();
         */

        String cellLoc = "获取失败";
        if (AppUtils.checkPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            CellLocation loc = tm.getCellLocation();
            if (loc != null)
                cellLoc = loc.toString();
        }
        // device id
        String imei = "unknow";
        if (AppUtils.checkPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            imei = tm.getDeviceId();
        }
        // 设备软件版本号
        String softwareVersion = "unknow";
        if (AppUtils.checkPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            softwareVersion = tm.getDeviceSoftwareVersion();
        }
        // 手机号获取
        String lineNum = "unknow";
        if (AppUtils.checkPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            lineNum = tm.getLine1Number();
        }
        /**
         * 当前移动终端附近移动终端的信息 需要权限android.permission.ACCESS_COARSE_LOCATION
         */
        StringBuilder sb = new StringBuilder();
        if (AppUtils.checkPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            try {
                List<NeighboringCellInfo> infos = tm.getNeighboringCellInfo();
                for (NeighboringCellInfo info : infos) {
                    if (info != null) {
                        // 获取小区号
                        int cid = info.getCid();
                        // 获取邻居小区LAC
                        // LAC:
                        // 位置区域码。为了确定移动台的位置，每个GSM/PLMN的覆盖区都被划分成许多位置区，LAC则用于标识不同的位置区。
                        int lac = info.getLac();
                        int networkType = info.getNetworkType();
                        int mPsc = info.getPsc();
                        // 获取邻居小区信号强度
                        int rssi = info.getRssi();
                        sb.append("小区号:").append(cid).append(";LAC:").append(lac).append(";networkType:")
                                .append(networkType).append(";mPsc:").append(mPsc).append(";小区信号强度:").append(rssi);
                    }
                }
                sb.append("\n");
            } catch (Throwable e) {
            }
        }

        // 注册的ISO国家注册码
        String networkCountryIso = tm.getNetworkCountryIso();
        // MCC+MNC
        String networkOperator = tm.getNetworkOperator();
        // (当前已注册的用户)的名字
        String networkOperatorName = tm.getNetworkOperatorName();
        // 当前使用的网络类型
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
        // 手机类型
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
        // 语音邮件号码
        String VoiceMailNumber = "unknow";
        if (AppUtils.checkPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            VoiceMailNumber = tm.getVoiceMailNumber();
        }
        // ICC卡是否存在
        boolean hasIccCard = tm.hasIccCard();
        // 是否漫游
        boolean isNetworkRoaming = false;
        if (AppUtils.checkPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            isNetworkRoaming = tm.isNetworkRoaming();
        }

        // 数据活动状态
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
        // 数据连接状态
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
        String s = "================SIM卡相关信息==================" + "\nSIM状态:" + simState + "\nSIM国家码:" + simCountryIso
                + "\nSIM[mcc+mnc]:" + operator + "\n服务提供名字(SPN):" + simOperatorName + "\nSIM卡序列号(ICCID):"
                + simSerialNumber + "\nIMSI:" + subscriberId + "\nMCC[resources获取]:" + mcc + "\nMNC[resources获取]:" + mnc
                + "\n================手机其他信息===================" + "\n电话状态:" + callState + "\n电话方位:" + cellLoc
                + "\nIMEI:" + imei + "\n设备软件版本号:" + softwareVersion + "\n手机号:" + lineNum
                // + "\n附近电话信息:" + sb.toString()
                + "\n注册的ISO国家注册码:" + networkCountryIso + "\nnetworkOperator(MCC+MNC):" + networkOperator
                + "\n(当前已注册的用户)的名字:" + networkOperatorName + "\n当前使用的网络类型:" + networkType + "\n手机类型:" + phoneType
                + "\n语音邮件号码:" + VoiceMailNumber + "\nICC卡是否存在:" + hasIccCard + "\n是否漫游:" + isNetworkRoaming
                + "\n数据活动状态:" + dataActivity + "\n数据连接状态:" + dataState;
        showMessage(s);

    }

    /**************************************************************************************
     * ******************************* 蓝牙/无线相关的,需要部分权限
     * ***************************
     **************************************************************************************/
    private void getBluetoothAndWifoInfo() {

        /**
         * 需要权限 <uses-permission android:name="android.permission.BLUETOOTH" />
         */
        BluetoothAdapter bAdapt = BluetoothAdapter.getDefaultAdapter();
        // 蓝牙地址
        String bluetoothAddr = bAdapt.getAddress();
        // Bluetooth name
        String bluetoothName = bAdapt.getName();

        // 蓝牙状态
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
         * 需要权限
         * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"
         * />
         */
        WifiManager mWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifi.getConnectionInfo();
        // 连接wifi的地址
        String bssidName = wifiInfo.getBSSID();
        // 连接wifi名字
        String ssidName = wifiInfo.getSSID();
        // mac地址
        String wifiMac = wifiInfo.getMacAddress();

        // 当前频率
        int frequency = -1;
        if (Build.VERSION.SDK_INT > 20) {
            try {
                frequency = wifiInfo.getFrequency();
            } catch (Throwable e) {

            }
        }
        // int ip值
        int ip = wifiInfo.getIpAddress();
        // 当前链接速度
        int linkSpeed = wifiInfo.getLinkSpeed();
        // 是否为隐藏wifi
        boolean isHiddenSSID = wifiInfo.getHiddenSSID();
        // 当前网络信号,单位dBm
        int rssi = wifiInfo.getRssi();
        // 网络ID
        int networkID = wifiInfo.getNetworkId();

        String macAddress = android.provider.Settings.Secure.getString(getContentResolver(), "bluetooth_address");

        /**
         * 结果拼接
         */
        String s = "===============蓝牙信息===============" + "\n蓝牙名字:" + bluetoothName + "\n蓝牙MAC[官方API]:" + bluetoothAddr
                + "\n蓝牙MAC[系统数据库方式获取]:" + macAddress + "\n蓝牙状态:" + bluetoothState
                + "\n===============无线信息===============" + "\n无线名字:" + ssidName + "\n无线MAC[官方API获取]:" + wifiMac
                + "\n无线MAC[javaAPI获取]:" + getMacByJavaAPI() + "\nBssid:" + bssidName + "\n当前频率:" + frequency
                + "\nWIFI信道[系统API获取]:" + getCurrentChannel() + "\nWIFI信道[根据频率获取]:" + getChannelByFrequency(frequency)
                + "\nip:" + longToIp(ip) + "\n当前链接速度:" + linkSpeed + "\n是否为隐藏wifi:" + isHiddenSSID + "\n信号强度:" + rssi
                + "dBm\n网络ID:" + networkID;
        showMessage(s);

    }

    /**
     * 获取系统MAC地址.需要权限<uses-permission android:name="android.permission.INTERNET"
     * />
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

        return ((i >> 24) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + (i & 0xFF);

    }

    public int getCurrentChannel() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();// 当前wifi连接信息
        List<ScanResult> scanResults = wifiManager.getScanResults();
        for (ScanResult result : scanResults) {
            if (result.BSSID.equalsIgnoreCase(wifiInfo.getBSSID())
                    && result.SSID.equalsIgnoreCase(wifiInfo.getSSID().substring(1, wifiInfo.getSSID().length() - 1))) {
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
     * ****************************** 电源相关的,不需要任何权限
     * *******************************
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
                String s = "状态:" + statusString + "\n" + "健康:" + healthString + "\n" + "是否存在电池:"
                        + String.valueOf(present) + "\n" + "获得当前电量:" + String.valueOf(level) + "\n" + "获得总电量:"
                        + String.valueOf(scale) + "\n" + "图标ID:" + String.valueOf(icon_small) + "\n" + "连接的电源插座:"
                        + acString + "\n" + "电压:" + String.valueOf(voltage) + "\n" + "温度:" + String.valueOf(temperature)
                        + "\n" + "电池类型:" + technology + "\n";
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
     * *************************** UI展示机制,外部调用showMessage即可
     * ***********************
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
                tv = (TextView) findViewById(R.id.textView);
                tv.setText(msg.obj.toString());
                break;
            }
        }
    };

}
