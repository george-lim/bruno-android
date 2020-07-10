package com.cs446.group7.bruno.utils;

import java.util.LinkedList;

// A queue that will execute a bunch of closures sequentially
public class NoFailClosureQueue<Success> implements Closure<Void, Void> {
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
    public void run(final Callback<Void, Void> callback) {
        if (steps.isEmpty()) {
            callback.onSuccess(null);
            return;
        }

        NoFailClosure<Success> nextStep = steps.poll();
        nextStep.run(result -> run(callback));
    }
}
