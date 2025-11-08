package com.bruno.android.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

// A service that allows classes to subscribe to device sensor events
public class SensorService implements SensorEventListener {

    private final Pedometer pedometer;

    public SensorService(final Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        pedometer = new Pedometer();

        sensorManager.registerListener(SensorService.this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    // Adds subscriber to the list of pedometer subscribers
    public void addPedometerSubscriber(final PedometerSubscriber subscriber) {
        pedometer.addSubscriber(subscriber);
    }

    // Removes subscriber from the list of pedometer subscribers
    public void removePedometerSubscriber(final PedometerSubscriber subscriber) {
        pedometer.removeSubscriber(subscriber);
    }

    // MARK - SensorEventListener methods

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float[] accelerationVector = new float[]{event.values[0], event.values[1], event.values[2]};
            pedometer.onAccelerometerChanged(event.timestamp, accelerationVector);
        }
    }
}
