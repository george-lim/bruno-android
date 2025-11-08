package com.bruno.android.utils;

import java.util.LinkedList;

// A queue that will execute a bunch of closures sequentially
public class NoFailClosureQueue<Success> implements NoFailClosure<Success> {
    private final LinkedList<NoFailClosure<Success>> steps;

    public NoFailClosureQueue() {
        steps = new LinkedList<>();
    }

    // Add a step to the queue
    public void add(final NoFailClosure<Success> step) {
        steps.add(step);
    }

    // Execute closures sequentially
    @Override
    public void run(Success result, final NoFailCallback<Success> callback) {
        NoFailClosure<Success> nextStep = steps.poll();

        if (nextStep == null) {
            callback.onSuccess(result);
            return;
        }

        nextStep.run(result, nextResult -> run(nextResult, callback));
    }

    // Overload for running a queue without a previous result value
    public void run(final NoFailCallback<Success> callback) {
        run(null, callback);
    }
}
