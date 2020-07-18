package com.cs446.group7.bruno.utils;

import java.util.LinkedList;

// A queue that will execute a bunch of closures sequentially
public class NoFailClosureQueue<Success> implements NoFailClosure<Success> {
    private LinkedList<NoFailClosure<Success>> steps;

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
        if (steps.isEmpty()) {
            callback.onSuccess(result);
            return;
        }

        NoFailClosure<Success> nextStep = steps.poll();
        nextStep.run(result, nextResult -> run(nextResult, callback));
    }

    public void run(final NoFailCallback<Success> callback) {
        run(null, callback);
    }
}
