package com.bruno.android.utils;

// Represents an asynchronous piece of code that will eventually call a callback
public interface NoFailClosure<Success> {
    void run(Success result, NoFailCallback<Success> callback);
}
