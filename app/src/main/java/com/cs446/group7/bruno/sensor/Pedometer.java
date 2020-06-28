package com.cs446.group7.bruno.sensor;

import android.util.Log;

import com.cs446.group7.bruno.utils.Vector;

import java.util.ArrayList;

public class Pedometer {

    // MARK: - Pedometer data classes

    // Acceleration data class that manages all previously recorded acceleration sensor data
    private class AccelerationData {
        // Maximum data point buffer size
        private static final int MAX_SIZE = 50;
        // Total data points observed
        private int totalCount = 0;
        // Data point queue. Overwrites oldest data points when buffer is full.
        private float[][] queue = new float[3][MAX_SIZE];

        // Add a data point
        void add(final float[] vector) {
            totalCount++;
            for (int i = 0; i < 3; ++i) {
                queue[i][totalCount % MAX_SIZE] = vector[i];
            }
        }

        // Returns the average acceleration vector from buffer data
        float[] getAverage() {
            float[] average = new float[3];
            int storedCount = Math.min(totalCount, MAX_SIZE);
            for (int i = 0; i < 3; ++i) {
                average[i] = Vector.componentSum(queue[i]) / storedCount;
            }
            return average;
        }
    }

    // Velocity data class that manages all previously recorded velocity data
    private class VelocityData {
        // Maximum data point buffer size
        private static final int MAX_SIZE = 10;
        // Total data points observed
        private int totalCount = 0;
        // Data point queue. Overwrites oldest data points when buffer is full.
        private float[] queue = new float[MAX_SIZE];

        // Add a data point
        void add(float value) {
            totalCount++;
            queue[totalCount % MAX_SIZE] = value;
        }

        // Returns an estimate for the total velocity observed over the last MAX_SIZE points
        float getVelocityEstimate() {
            return Vector.componentSum(queue);
        }
    }

    // MARK - Pedometer data properties

    // Total velocity required to register a step.
    // NOTE: - Modify this value to change step sensitivity.
    private static final float STEP_THRESHOLD = 75f;

    // Minimum delay between registered steps
    private static final int STEP_DELAY_NS = 250000000;

    // Last step timestamp
    private long lastTimestamp = 0;

    // Last velocity estimate
    private float lastVelocityEstimate = 0;

    // MARK - General Pedometer properties

    private ArrayList<PedometerSubscriber> subscribers;
    private AccelerationData accelerationData;
    private VelocityData velocityData;

    public Pedometer() {
        subscribers = new ArrayList<>();
        accelerationData = new AccelerationData();
        velocityData = new VelocityData();
    }

    // Adds subscriber to the list of subscribers
    public void addSubscriber(final PedometerSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    // Removes subscriber from the list of subscribers
    public void removeSubscriber(final PedometerSubscriber subscriber) {
        subscribers.remove(subscriber);
    }

    // Calculates whether the added acceleration vector and timestamp are enough to register a step
    public void onAccelerometerChanged(long timestamp, final float[] accelerationVector) {
        accelerationData.add(accelerationVector);
        float[] accelerationAverage = accelerationData.getAverage();
        float[] normalizedAverage = Vector.normalize(accelerationAverage);
        float normalizationFactor = Vector.norm(accelerationAverage);
        try {
            float velocity = Vector.dotProduct(normalizedAverage, accelerationVector) - normalizationFactor;
            velocityData.add(velocity);
            float velocityEstimate = velocityData.getVelocityEstimate();
            if (lastVelocityEstimate <= STEP_THRESHOLD && velocityEstimate > STEP_THRESHOLD
                    && timestamp - lastTimestamp > STEP_DELAY_NS) {
                for (PedometerSubscriber subscriber : subscribers) {
                    subscriber.didStep(timestamp);
                }
                lastTimestamp = timestamp;
            }
            lastVelocityEstimate = velocityEstimate;
        } catch (Vector.UnequalDimensionException e) {
            Log.i(this.getClass().getSimpleName(), e.getMessage());
        }
    }
}
