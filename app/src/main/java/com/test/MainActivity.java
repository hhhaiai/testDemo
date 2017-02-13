package com.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import static com.test.R.*;


public class MainActivity extends AppCompatActivity {
    public static final String T = "sanbo";

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
//        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);


        getBattery();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn1:
                Log.d(T, "A");
                break;
            case R.id.btn2:
                Log.d(T, "B");
                break;
            case R.id.btn3:
                Log.d(T, "C");
                break;
            default:
                break;
        }


    }

    /**************************************************************************************
     * ******************************** 电源相关的 ******************************************
     **************************************************************************************/

    protected static final int MSG_REFRESH_UI = 0x999;
    private TextView tv;

    /**
     * 获取电源相关的,注解广播接收器实现
     */
    private void getBattery() {
        tv = (TextView) findViewById(id.textView);
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
                String s = "";
                s = "状态:" + statusString + "\n"
                        + "健康:" + healthString + "\n"
                        + "是否存在电池:" + String.valueOf(present) + "\n"
                        + "获得当前电量:" + String.valueOf(level) + "\n"
                        + "获得总电量:" + String.valueOf(scale) + "\n"
                        + "图标ID:" + String.valueOf(icon_small) + "\n"
                        + "连接的电源插座:" + acString + "\n"
                        + "电压:" + String.valueOf(voltage) + "\n"
                        + "温度:" + String.valueOf(temperature) + "\n"
                        + "电池类型:" + technology + "\n";
                Message msg = new Message();
                msg.what = MSG_REFRESH_UI;
                msg.obj = s;
                mhHandler.sendMessage(msg);
            }
        }
    };
    Handler mhHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REFRESH_UI:
                    tv.setText(msg.obj.toString());
                    break;
            }
        }
    };

    /**
     * 获取电源相关的. api大于21才可以使用
     */
    public void getBatteryManagerByLargeThan21() {
        BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
        System.out.println(bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER));
        System.out.println(bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE));
        System.out.println(bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW));
    }

}
