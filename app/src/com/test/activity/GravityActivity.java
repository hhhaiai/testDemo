package com.test.activity;

import com.test.R;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class GravityActivity extends Activity implements SensorEventListener {
    private SensorManager sensorManager = null;
    private Sensor sensor = null;
    private TextView tv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_list_activity);
        tv = (TextView) findViewById(R.id.sensor_list_text);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this, sensor);
        super.onPause();
    }

    private float[] gravity = new float[3];
    private float[] motion = new float[3];
    private double ratioY;
    private double angle;
    private int counter = 1;

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        for (int i = 0; i < 3; i++) {
            /*
             * accelermeter是很敏感的，看之前小例子的log就知道。因为重力是恒力，我们移动设备，它的变化不会太快，
             * 不象摇晃手机这样的外力那样突然。 因此通过low-pass filter对重力进行过滤
             */
            gravity[i] = (float) (0.1 * event.values[i] + 0.9 * gravity[i]);
            motion[i] = event.values[i] - gravity[i];
        }

        // 计算重力在Y轴方向的量，即G*cos(α)
        ratioY = gravity[1] / SensorManager.GRAVITY_EARTH;
        if (ratioY > 1.0)
            ratioY = 1.0;
        if (ratioY < -1.0)
            ratioY = -1.0;
        // 获得α的值，根据z轴的方向修正正负值。
        angle = Math.toDegrees(Math.acos(ratioY));
        if (gravity[2] < 0)
            angle = -angle;

        // 避免扫屏，每10次变化显示一次值
        if (counter++ % 10 == 0) {
            tv.setText("Raw Values : \n" + "   x,y,z = " + event.values[0] + "," + event.values[1] + ","
                    + event.values[2] + "\n" + "Gravity values : \n" + "   x,y,z = " + gravity[0] + "," + gravity[1]
                    + "," + gravity[2] + "\n" + "Motion values : \n" + "   x,y,z = " + motion[0] + "," + motion[1] + ","
                    + motion[2] + "\n" + "Y轴角度 :" + angle);
            tv.invalidate();
            counter = 1;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

}
