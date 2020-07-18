package com.cs446.group7.bruno.utils;

import java.util.LinkedList;

/*
    A queue that will execute a bunch of closures sequentially
    NOTE: If any closure step fails, the client callback will fail
 */
public class ClosureQueue<Success, Failure> implements Closure<Success, Failure> {
    private LinkedList<Closure<Success, Failure>> steps;

    public ClosureQueue() {
        steps = new LinkedList<>();
    }

    // Add a step to the queue
    public void add(final Closure<Success, Failure> step) {
        steps.add(step);
    }

    // Execute closures sequentially
    @Override
    public void run(Success result, final Callback<Success, Failure> callback) {
        if (steps.isEmpty()) {
            callback.onSuccess(result);
            return;
        }

        Closure<Success, Failure> nextStep = steps.poll();

        nextStep.run(result, new Callback<Success, Failure>() {
            @Override
            public void onSuccess(Success result) {
                run(result, callback);
            }

            @Override
            public void onFailed(Failure result) {
                callback.onFailed(result);
            }
        });
    }

    // Overload for running a queue without a previous result value
    public void run(final Callback<Success, Failure> callback) {
        run(null, callback);
    }
}
