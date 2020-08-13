package com.bruno.android.sensor;

public interface PedometerSubscriber {
    // Triggers after a step has been detected
    void didStep();
}
