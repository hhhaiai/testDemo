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

public class ProximitySensorActivity extends Activity implements SensorEventListener {
    private SensorManager sensorManager = null;
    private Sensor sensor = null;
    private TextView tv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_list_activity);
        tv = (TextView) findViewById(R.id.sensor_list_text);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        // 检查解析度和最大值，如果两者一样，说明该近距离传感器智能给出接近和远离这两个状态。
        showInfo("resolution:" + sensor.getResolution());
        showInfo("max value:" + sensor.getMaximumRange());
    }

    @Override
    protected void onResume() {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this, sensor);
        super.onPause();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        showInfo(sensor.getName() + " accuracy changed: " + accuracy);
    }

    @Override
    /* 对于近距离传感器，有效数值存放在values[0]中的，单位为cm。 */
    public void onSensorChanged(SensorEvent event) {
        showInfo("传感器事件： " + event.sensor.getName() + " " + event.values[0]);
    }

    private void showInfo(String info) {
        tv.append("\n" + info);
        Log.d("ProximitySensor", info);
    }

}
