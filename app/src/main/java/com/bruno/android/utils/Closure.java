package com.bruno.android.utils;

// Represents an asynchronous piece of code that will eventually call a callback
public interface Closure<Success, Failure> {
    void run(Success result, Callback<Success, Failure> callback);
}
