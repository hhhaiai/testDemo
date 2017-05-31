package com.test.activity;

import java.util.Locale;

import com.test.R;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MagneticFieldSensorActivity extends Activity implements SensorEventListener {
    private SensorManager sensorManager = null;
    private Sensor sensor = null;
    private TextView tv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_list_activity);
        tv = (TextView) findViewById(R.id.sensor_list_text);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this, sensor);
        super.onPause();
    }

    @Override
    protected void onResume() {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        super.onResume();
    }

    private int count = 1;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (count++ == 20) {
            double value = Math.sqrt(event.values[0] * event.values[0] + event.values[1] * event.values[1]
                    + event.values[2] * event.values[2]);
            String str = String.format(Locale.getDefault(), "X:%8.4f , Y:%8.4f , Z:%8.4f \n总值为：%8.4f", event.values[0],
                    event.values[1], event.values[2], value);

            count = 1;
            tv.setText(str);
            Log.d("磁场感应器", str);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}
