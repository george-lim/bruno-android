package com.cs446.group7.bruno.utils;

import java.util.ArrayList;

/*
    A queue that will execute a bunch of closures sequentially
    NOTE: If any closure step fails, the client callback will fail
 */
public class ClosureQueue<Success, Failure> implements Closure<Void, Void> {
    private ArrayList<Closure<Success, Failure>> steps;

    public ClosureQueue() {
        steps = new ArrayList<>();
    }

    // Add a step to the queue
    public void add(Closure<Success, Failure> step) {
        steps.add(step);
    }

    // Execute closures sequentially
    @Override
    public void run(final Callback<Void, Void> callback) {
        if (steps.isEmpty()) {
            callback.onSuccess(null);
            return;
        }

        Closure<Success, Failure> nextStep = steps.remove(0);

        nextStep.run(new Callback<Success, Failure>() {
            @Override
            public void onSuccess(Success result) {
                run(callback);
            }

            @Override
            public void onFailed(Failure result) {
                callback.onFailed(null);
            }
        });
    }
}
