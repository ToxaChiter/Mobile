package com.example.sensors_serko;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Surface;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private boolean showingGyroscopeData = false;
    private boolean showingListData = false;
    private boolean showingLightData = false;
    private boolean showingAccelerationData = false;
    private boolean showingOrientationData = false;
    TextView tvText;
    SensorManager sensorManager;
    List<Sensor> sensors;
    private Sensor proximitySensor;
    Sensor sensorLight, sensorAccel, sensorLinAccel, sensorGravity, sensorMagnet, sensorGyroscope;

    StringBuilder sb = new StringBuilder();
    Timer timer;
    ImageView palm;
    int rotation;
    private boolean isMenuOpen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        palm =  findViewById(R.id.palm);
        tvText = findViewById(R.id.tvText);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        sensorGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE); // Initialize gyroscope sensor
        sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorLinAccel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorMagnet = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        final ImageButton radialMenuButton = findViewById(R.id.radialMenuButton);
        if (proximitySensor == null) {
            tvText.append("No proximity sensor found!");
        }
        final List<ImageButton> buttons = new ArrayList<>();
        buttons.add(findViewById(R.id.btnSensList));
        buttons.add(findViewById(R.id.btnSensLight));
        buttons.add(findViewById(R.id.btnSensAccelerometer));
        buttons.add(findViewById(R.id.btnSensOrientation));
        buttons.add(findViewById(R.id.btnSensGyroscope));
        buttons.add(findViewById(R.id.btnSensProximity));

        radialMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMenuOpen) {
                    openMenu(buttons);
                } else {
                    closeMenu(buttons);
                }
                isMenuOpen = !isMenuOpen;
            }
        });

    }
    private void openMenu(List<ImageButton> buttons) {
        AnimatorSet animatorSet = new AnimatorSet();
        List<Animator> animators = new ArrayList<>();

        for (int i = 0; i < buttons.size(); i++) {
            ImageButton button = buttons.get(i);
            float translationX = (float) (Math.cos(Math.toRadians(i * (360.0 / buttons.size()))) * 300);
            float translationY = (float) (Math.sin(Math.toRadians(i * (360.0 / buttons.size()))) * 300);

            button.setVisibility(View.VISIBLE);
            ObjectAnimator translationXAnimator = ObjectAnimator.ofFloat(button, "translationX", 0f, translationX);
            ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(button, "translationY", 0f, translationY);

            animators.add(translationXAnimator);
            animators.add(translationYAnimator);
        }

        animatorSet.playTogether(animators);
        animatorSet.start();
    }
    private void unregisterAllListeners() {
        sensorManager.unregisterListener(listenerLight);
        sensorManager.unregisterListener(accelListener);
        sensorManager.unregisterListener(gyroscopeListener);
        sensorManager.unregisterListener(orientationListener);
        sensorManager.unregisterListener(proximityListener);

    }

    private void closeMenu(List<ImageButton> buttons) {
        AnimatorSet animatorSet = new AnimatorSet();
        List<Animator> animators = new ArrayList<>();
        for (int i = 0; i < buttons.size(); i++) {
            ImageButton button = buttons.get(i);
            ObjectAnimator translationXAnimator = ObjectAnimator.ofFloat(button, "translationX", button.getTranslationX(), 0f);
            ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(button, "translationY", button.getTranslationY(), 0f);
            animators.add(translationXAnimator);
            animators.add(translationYAnimator);
        }
        animatorSet.playTogether(animators);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                for (ImageButton button : buttons) {
                    button.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        animatorSet.start();

}

    public void onClickSensList(View v) {
        unregisterAllListeners();
        showingListData = toggleTextView(v);
        if (showingListData){
        StringBuilder sb = new StringBuilder();
        for (Sensor sensor : sensors) {
            sb.append("name = ").append(sensor.getName())
                    .append(", type = ").append(sensor.getType())
                    .append("\nvendor = ").append(sensor.getVendor())
                    .append(" ,version = ").append(sensor.getVersion())
                    .append("\nmax = ").append(sensor.getMaximumRange())
                    .append(", resolution = ").append(sensor.getResolution())
                    .append("\n--------------------------------------\n");
        }
        tvText.setText(sb);
        tvText.append("\nDone by A Serko");

        }
    }

    public boolean toggleTextView(View v) {
        if (tvText.getVisibility() == View.VISIBLE) {
            tvText.setVisibility(View.GONE);
            return false;
        } else {
            tvText.setVisibility(View.VISIBLE);
            return true;
        }
    }
    public void onClickSensGyroscope(View v) {
       unregisterAllListeners();
       showingGyroscopeData = toggleTextView(v);
       if (showingGyroscopeData){
           sensorManager.registerListener(gyroscopeListener, sensorGyroscope, SensorManager.SENSOR_DELAY_NORMAL);

       }


    }

    SensorEventListener proximityListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {


            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                float distance = event.values[0];
                if (distance < proximitySensor.getMaximumRange()) {
                    tvText.setText("Object is nearby");

                    palm.setVisibility(View.VISIBLE);
                } else {
                    tvText.setText("Object is far away");
                    palm.setVisibility(View.GONE);

                }
                tvText.append("\nDone by A Serko");

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };


    public void onClickProximityData(View view) {
        unregisterAllListeners();
        toggleTextView(view);
        sensorManager.registerListener(proximityListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    SensorEventListener gyroscopeListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (showingGyroscopeData) {
                float[] values = event.values;
                String gyroscopeData = "Gyroscope:\n"
                        + "X: " + values[0] + "\n"
                        + "Y: " + values[1] + "\n"
                        + "Z: " + values[2];
                tvText.setText(gyroscopeData);
                tvText.append("\nDone by A Serko");

            }

        }
    };

    public void onClickShowAccelData(View v) {
     unregisterAllListeners();
        showingAccelerationData = toggleTextView(v);
        if (showingAccelerationData) {
            sensorManager.registerListener(accelListener, sensorAccel, SensorManager.SENSOR_DELAY_NORMAL);

            timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showAccelData();
                        }
                    });
                }
            };
            timer.schedule(task, 0, 400);
        } else {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        }
    }

    public void onClickSensLight(View v) {
       unregisterAllListeners();
       showingLightData = toggleTextView(v);
       if (showingLightData){
           sensorManager.registerListener(listenerLight, sensorLight, SensorManager.SENSOR_DELAY_NORMAL);

       }

    }
    public void onClickShowOrientationData(View v) {
        unregisterAllListeners();
        if (showingOrientationData) {
            sensorManager.unregisterListener(orientationListener);
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        } else {
            sensorManager.registerListener(orientationListener, sensorMagnet, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(orientationListener, sensorAccel, SensorManager.SENSOR_DELAY_NORMAL);
            timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showOrientationData();
                        }
                    });
                }
            };
            timer.schedule(task, 0, 400);
        }
        showingOrientationData = toggleTextView(v);
    }


    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(accelListener, sensorAccel, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(orientationListener, sensorMagnet, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(orientationListener, sensorAccel, SensorManager.SENSOR_DELAY_NORMAL);
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (showingAccelerationData) showAccelData();
                        else if (showingOrientationData) showOrientationData();
                    }
                });
            }
        };
        timer.schedule(task, 0, 400);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listenerLight);
        sensorManager.unregisterListener(accelListener);
        sensorManager.unregisterListener(orientationListener);
        sensorManager.unregisterListener(gyroscopeListener);

    }

    String format(float values[]) {
        return String.format("%1$.1f\t\t%2$.1f\t\t%3$.1f", values[0], values[1], values[2]);
    }

    void showAccelData() {
        sb.setLength(0);
        sb.append("Accelerometer: ").append(format(valuesAccel))
                .append("\n\nAccel motion: ").append(format(valuesAccelMotion))
                .append("\nAccel gravity : ").append(format(valuesAccelGravity))
                .append("\n\nLin accel : ").append(format(valuesLinAccel))
                .append("\nGravity : ").append(format(valuesGravity));
        tvText.setText(sb.toString());
        tvText.append("\nDone by A Serko");

    }

    void showOrientationData() {
        getDeviceOrientation();
        getActualDeviceOrientation();
        sb.setLength(0);
        sb.append("Orientation : ").append(format(valuesResult))
                .append("\nOrientation 2: ").append(format(valuesResult2));
        tvText.setText(sb);
        tvText.append("\nDone by A Serko");

    }

    float[] valuesAccel = new float[3];
    float[] valuesAccelMotion = new float[3];
    float[] valuesAccelGravity = new float[3];
    float[] valuesLinAccel = new float[3];
    float[] valuesGravity = new float[3];
    float[] valuesMagnet = new float[3];
    float[] valuesResult = new float[3];
    float[] valuesResult2 = new float[3];

    SensorEventListener accelListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    for (int i = 0; i < 3; i++) {
                        valuesAccel[i] = event.values[i];
                        valuesAccelGravity[i] = (float) (0.1 * event.values[i] + 0.9 * valuesAccelGravity[i]);
                        valuesAccelMotion[i] = event.values[i] - valuesAccelGravity[i];
                    }
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    for (int i = 0; i < 3; i++) {
                        valuesLinAccel[i] = event.values[i];
                    }
                    break;
                case Sensor.TYPE_GRAVITY:
                    for (int i = 0; i < 3; i++) {
                        valuesGravity[i] = event.values[i];
                    }
                    break;
            }
        }
    };

    SensorEventListener orientationListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_MAGNETIC_FIELD:
                    for (int i = 0; i < 3; i++) {
                        valuesMagnet[i] = event.values[i];
                    }
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    for (int i = 0; i < 3; i++) {
                        valuesAccel[i] = event.values[i];
                    }
                    break;
            }
            calculateDeviceOrientation();
        }
    };

    private void calculateDeviceOrientation() {
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrix(rotationMatrix, null, valuesAccel, valuesMagnet);
        SensorManager.getOrientation(rotationMatrix, valuesResult);
        valuesResult[0] = (float) Math.toDegrees(valuesResult[0]);
        valuesResult[1] = (float) Math.toDegrees(valuesResult[1]);
        valuesResult[2] = (float) Math.toDegrees(valuesResult[2]);
    }
    ;
    SensorEventListener listenerLight = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            tvText.setText(String.valueOf(event.values[0]));
            tvText.append("\nDone by A Serko");


        }
    };

    void getDeviceOrientation() {
        float[] r = new float[9];
        SensorManager.getRotationMatrix(r, null, valuesAccel, valuesMagnet);
        SensorManager.getOrientation(r, valuesResult);
        valuesResult[0] = (float) Math.toDegrees(valuesResult[0]);
        valuesResult[1] = (float) Math.toDegrees(valuesResult[1]);
        valuesResult[2] = (float) Math.toDegrees(valuesResult[2]);
    }

    void getActualDeviceOrientation() {
        float[] inR = new float[9];
        float[] outR = new float[9];
        SensorManager.getRotationMatrix(inR, null, valuesAccel, valuesMagnet);
        int x_axis = SensorManager.AXIS_X;
        int y_axis = SensorManager.AXIS_Y;
        switch (rotation) {
            case (Surface.ROTATION_0):
                break;
            case (Surface.ROTATION_90):
                x_axis = SensorManager.AXIS_Y;
                y_axis = SensorManager.AXIS_MINUS_X;
                break;
            case (Surface.ROTATION_180):
                y_axis = SensorManager.AXIS_MINUS_Y;
                break;
            case (Surface.ROTATION_270):
                x_axis = SensorManager.AXIS_MINUS_Y;
                y_axis = SensorManager.AXIS_X;
                break;
            default:
                break;
        }
        SensorManager.remapCoordinateSystem(inR, x_axis, y_axis, outR);
        SensorManager.getOrientation(outR, valuesResult2);
        valuesResult2[0] = (float) Math.toDegrees(valuesResult2[0]);
        valuesResult2[1] = (float) Math.toDegrees(valuesResult2[1]);
        valuesResult2[2] = (float) Math.toDegrees(valuesResult2[2]);
    }
}