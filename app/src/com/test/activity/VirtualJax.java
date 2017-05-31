package com.test.activity;

import java.util.Locale;

import com.test.R;

import android.app.Activity;
import android.content.Context;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class VirtualJax extends Activity implements SensorEventListener {
    private ToggleButton toggleButton = null;
    private TextView oldOne = null, nowOne = null;
    private SensorManager sensorManager = null;
    private Sensor accelSensor = null, compassSensor = null, orientSensor = null, rotVecSensor = null;
    private float[] accelValues = new float[3], compassValues = new float[3], orientValues = new float[3],
            rotVecValues = null;
    private int mRotation;
    private LocationManager locManager = null;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.virtual_jax_layout);
        oldOne = (TextView) findViewById(R.id.orientation);
        nowOne = (TextView) findViewById(R.id.preferred);
        toggleButton = (ToggleButton) findViewById(R.id.toggle);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        compassSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        orientSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        rotVecSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        WindowManager window = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT < 8)
            mRotation = window.getDefaultDisplay().getOrientation();
        else
            mRotation = window.getDefaultDisplay().getRotation();

        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }

    @Override
    protected void onResume() {
        isAllowRemap = toggleButton.isChecked();
        sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, compassSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, orientSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, rotVecSensor, SensorManager.SENSOR_DELAY_GAME);

        super.onResume();
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this, accelSensor);
        sensorManager.unregisterListener(this, compassSensor);
        sensorManager.unregisterListener(this, orientSensor);
        sensorManager.unregisterListener(this, rotVecSensor);
        super.onPause();
    }

    private boolean ready = false; // 检查是否同时具有加速度传感器和磁场传感器
    private float[] inR = new float[9], outR = new float[9];
    private float[] inclineMatrix = new float[9];
    private float[] prefValues = new float[3];
    private double mInclination;
    private int count = 1;
    private float[] rotvecR = new float[9], rotQ = new float[4];
    private float[] rotvecOrientValues = new float[3];

    @SuppressWarnings("deprecation")
    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
        case Sensor.TYPE_ACCELEROMETER:
            for (int i = 0; i < 3; i++) {
                accelValues[i] = event.values[i];
            }
            if (compassValues[0] != 0) // 即accelerator和magnetic传感器都有数值
                ready = true;
            break;

        case Sensor.TYPE_MAGNETIC_FIELD:
            for (int i = 0; i < 3; i++) {
                compassValues[i] = event.values[i];
            }
            if (accelValues[2] != 0) // 即accelerator和magnetic传感器都有数值，换一个轴向检查
                ready = true;
            break;

        case Sensor.TYPE_ORIENTATION:
            for (int i = 0; i < 3; i++) {
                orientValues[i] = event.values[i];
            }
            break;

        case Sensor.TYPE_ROTATION_VECTOR:
            if (rotVecValues == null) {
                rotVecValues = new float[event.values.length];
            }
            for (int i = 0; i < rotVecValues.length; i++) {
                rotVecValues[i] = event.values[i];
            }
            break;
        }

        if (!ready)
            return;

        // 计算:inclination matrix I(inclineMatrix) as well as the rotation matrix
        // R(inR)
        if (SensorManager.getRotationMatrix(inR, inclineMatrix, accelValues, compassValues)) {

            if (isAllowRemap && mRotation == Surface.ROTATION_90) {
                // 参数二表示设备X轴成为新坐标的Y轴，参数三表示设备的Y轴成为新坐标-x轴（方向相反）
                SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, outR);
                SensorManager.getOrientation(outR, prefValues);
            } else {

                /*
                 * Computes the device's orientation based on the rotation
                 * matrix. When it returns, the array values is filled with the
                 * result: values[0]: azimuth, rotation around the Z axis.
                 * values[1]: pitch, rotation around the X axis. values[2]:
                 * roll, rotation around the Y axis.
                 */
                SensorManager.getOrientation(inR, prefValues);
            }
            // 计算磁仰角：地球表面任一点的地磁场总强度的矢量方向与水平面的夹角。
            mInclination = SensorManager.getInclination(inclineMatrix);

            if (count++ % 100 == 0) {
                doUpdate(null);
                count = 1;
            }

        } else {
            Toast.makeText(this, "无法获得矩阵（SensorManager.getRotationMatrix）", Toast.LENGTH_LONG);
            finish();
        }

        if (rotVecValues != null) {
            SensorManager.getQuaternionFromVector(rotQ, rotVecValues);
            SensorManager.getRotationMatrixFromVector(rotvecR, rotVecValues);
            SensorManager.getOrientation(rotvecR, rotvecOrientValues);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void doUpdate(View v) {
        if (!ready)
            return;

        // preValues[0]是方位角，范围是-pi到pi，通过Math.toDegrees转换为角度
        float mAzimuth = (float) Math.toDegrees(prefValues[0]);// 方角位，地平经度
        /*
         * //纠正为orientation的数值。 if(mAzimuth < 0) mAzimuth += 360.0;
         */

        String msg = String.format(Locale.getDefault(),
                "推荐方式：\n方位角：%7.3f\npitch: %7.3f\nroll: %7.3f\n地磁仰角：%7.3f\n重适配坐标=%s\n%s\n", mAzimuth,
                Math.toDegrees(prefValues[1]), Math.toDegrees(prefValues[2]), Math.toDegrees(mInclination),
                (isAllowRemap && mRotation == Surface.ROTATION_90) ? "true" : "false", info);

        if (rotvecOrientValues != null && mRotation == Surface.ROTATION_0) {
            msg += String.format(
                    "Rotation Vector Sensor:\nazimuth %7.3f\npitch %7.3f\nroll %7.3f\nw,x,y,z %6.2f,%6.2f,%6.2f,%6.2f\n",
                    Math.toDegrees(rotvecOrientValues[0]), Math.toDegrees(rotvecOrientValues[1]),
                    Math.toDegrees(rotvecOrientValues[2]), rotQ[0], rotQ[1], rotQ[2], rotQ[3]);
            // Log.d("WEI","Quaternion w,x,y,z=" + rotQ[0] + "," + rotQ[1] + ","
            // + rotQ[2] + "," + rotQ[3]);
        }
        nowOne.setText(msg);

        msg = String.format(Locale.getDefault(), "老方式：\n方位角：%7.3f\npitch: %7.3f\nroll: %7.3f", orientValues[0],
                orientValues[1], orientValues[2]);
        oldOne.setText(msg);

    }

    private boolean isAllowRemap = false;

    public void doToggle(View v) {
        isAllowRemap = ((ToggleButton) v).isChecked();
    }

    private String info = "";

    public void doGeoNorth(View v) {
        if (!ready)
            return;

        String providerName = locManager.getBestProvider(new Criteria(), true);
        Location loc = locManager.getLastKnownLocation(providerName);

        if (loc == null && locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // 请注意，设备要打开网络定位的选项。在室内，由于不容易搜索到GPS，建议采用network方式。
            // 否则，locManager.isProviderEnabled("network")为false，不能使用网络方式，而GPS在室内搜半天卫星都不一定有
            providerName = LocationManager.NETWORK_PROVIDER;
            loc = locManager.getLastKnownLocation(providerName);
        }
        if (loc == null)
            return;

        info = "定位：" + providerName + "\n"
                + String.format(" %9.5f,%9.5f", (float) loc.getLongitude(), (float) loc.getLatitude()) + "\n";
        Log.d("WEI", "" + loc);

        GeomagneticField geo = new GeomagneticField((float) loc.getLatitude(), (float) loc.getLongitude(),
                (float) loc.getAltitude(), System.currentTimeMillis());

        float declination = geo.getDeclination();
        info += String.format("磁偏角：%7.3f\n", declination);
    }

}
