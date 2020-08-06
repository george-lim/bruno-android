package com.cs446.group7.bruno.sensor;

public interface PedometerSubscriber {
    // Triggers after a step has been detected
    void didStep();
}
