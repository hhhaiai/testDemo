package com.test.activity;

import com.test.R;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class LightSensorActivity extends Activity implements SensorEventListener {
    private SensorManager sensorManager = null;
    private Sensor lightSensor = null;
    private TextView tv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_list_activity); // 使用相同的layout
        tv = (TextView) findViewById(R.id.sensor_list_text);

        // 【1】获取Light传感器
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    @Override
    /*
     * 【2】如果有人监听传感器，传感器工作，需要耗电，因此我们应该只在需要的时候进行监听，本
     * 例子如果activity不在前台就不需要监听，因此在onResume()注册监听器，在onPause()中注销监听器
     */
    protected void onResume() {
        /*
         * 第三个参数是指Sensor Event变化通知的频率，有效值为NORMAL，UI，GAME，FASTER。
         * 有些传感器很灵敏，短时间会有大量数据，对内存和垃圾回收造成压力，可能会导致APP的性能问题，因此根据需要选择合适的频率。
         * 对于旋转矢量传感器，通常需要不断地去读取。
         */
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this, lightSensor);
        super.onPause();
    }

    /*
     * 【3】SensorEventListener要实现两个接口onAccuracyChanged()和onSensorChanged()。
     * onAccuracyChanged()会在精度改变或在注册监听器时调用。accuracy分为4档，0（unreliable），1（low），2（
     * medium），3（high） 注意0并不代表有问题，同时是传感器需要校准。
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        showInfo(sensor.getName() + " accuracy changed: " + accuracy);
    }

    @Override
    /*
     * 【3】SensorEventListener要实现两个接口onAccuracyChanged()和onSensorChanged()。
     * onSensorChanged()在传感器数值发生变化已经注册监听器时调用，其更新频率就是注册中的参数三。
     * 对于光传感器，有效数值存放在values[0]中的，单位为SI lunx。光传感器通常位于上方（一般靠左侧），
     * 除了前置摄像头外还有一个孔，一般就是它。遮盖会触发onSensorChanged()
     */
    public void onSensorChanged(SensorEvent event) {
        showInfo("Get Sensor Event: " + event.sensor.getName() + " " + event.values[0]);
    }

    private void showInfo(String info) {
        tv.append("\n" + info);
        Log.d("LightSensor", info);
    }
}
