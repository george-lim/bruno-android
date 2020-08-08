package com.cs446.group7.bruno.location;

import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

import com.cs446.group7.bruno.models.RouteModel;
import com.cs446.group7.bruno.utils.NoFailCallback;

import java.util.ArrayList;
import java.util.List;

// A dummy route AI used to test route progression
public class BrunoBot implements LocationService {

    // MARK: - Private constants

    // NOTE: Must be greater than zero
    private static final int UPDATE_INTERVAL_MILLISECONDS = 2000;

    // MARK: - Private members

    private RouteModel model;
    private List<LocationServiceSubscriber> subscribers;
    private Thread locationUpdateThread;

    // MARK: - Lifecycle methods

    public BrunoBot(final RouteModel model) {
        this.model = model;
        this.subscribers = new ArrayList<>();
        locationUpdateThread = null;
    }

    // MARK: - Private methods

    // Simulates playing the playlist in the background, with proper track delay.
    private void generateLocationUpdates() {
        try {
            while (true) {
                Coordinate checkpoint = model.getCheckpoint();

                // No more checkpoints
                if (checkpoint == null) {
                    return;
                }

                // Dispatch to UI thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    // Notify all subscribers of next location
                    for (LocationServiceSubscriber subscriber : subscribers) {
                        subscriber.onLocationUpdate(checkpoint.getLocation());
                    }
                });

                Thread.sleep(UPDATE_INTERVAL_MILLISECONDS);
            }
        }
        // Return from the method immediately
        catch (InterruptedException ignored) { }
    }

    // MARK: - LocationService methods

    @Override
    public void startLocationUpdates() {
        locationUpdateThread = new Thread(this::generateLocationUpdates);
        locationUpdateThread.start();
    }

    @Override
    public void startLocationUpdates(final @Nullable NoFailCallback<Location> initialLocationCallback) {
        startLocationUpdates();
        initialLocationCallback.onSuccess(model.getCheckpoint().getLocation());
    }

    @Override
    public void stopLocationUpdates() {
        if (locationUpdateThread == null || !locationUpdateThread.isAlive()) {
            return;
        }

        try {
            locationUpdateThread.interrupt();
            locationUpdateThread.join();
        } catch (InterruptedException e) {
            // NOTE: .join requires a try-catch, even though control will never get here.
            e.printStackTrace();
        }
    }

    @Override
    public void addSubscriber(final LocationServiceSubscriber subscriber) {
        if (subscribers.contains(subscriber)) return;
        subscribers.add(subscriber);
    }

    @Override
    public void removeSubscriber(final LocationServiceSubscriber subscriber) {
        subscribers.remove(subscriber);
    }
}
