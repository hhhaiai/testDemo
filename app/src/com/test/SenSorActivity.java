package com.test;

import com.test.activity.AccelerometerSensorActivity;
import com.test.activity.GravityActivity;
import com.test.activity.GyroscopeSensorActivity;
import com.test.activity.LightSensorActivity;
import com.test.activity.MagneticFieldSensorActivity;
import com.test.activity.ProximitySensorActivity;
import com.test.activity.SensorListActivity;
import com.test.activity.VirtualJax;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SenSorActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_main);
    }

    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
        case R.id.test_sensor_list:
            startActivity(new Intent(this, SensorListActivity.class));
            break;
        case R.id.test_light_sensor:
            startActivity(new Intent(this, LightSensorActivity.class));
            break;
        case R.id.test_proximity_sensor:
            startActivity(new Intent(this, ProximitySensorActivity.class));
            break;
        case R.id.test_gyroscope_sensor:
            startActivity(new Intent(this, GyroscopeSensorActivity.class));
            break;
        case R.id.test_accelerometer_sensor:
            startActivity(new Intent(this, AccelerometerSensorActivity.class));
            break;
        case R.id.test_accelerometer_2_sensor:
            startActivity(new Intent(this, GravityActivity.class));
            break;
        case R.id.test_magnetic_sensor:
            startActivity(new Intent(this, MagneticFieldSensorActivity.class));
            break;
        case R.id.test_orientation:
            startActivity(new Intent(this, VirtualJax.class));
            break;
        default:
            break;
        }
    }

}
