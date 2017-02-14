package com.test;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import static com.test.R.*;


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
                Log.d(T, "B");
                getBluetoothAndWifoInfo();
                break;
            case R.id.btn3:
                Log.d(T, "C");
                break;
            default:
                break;g
        }


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

}
