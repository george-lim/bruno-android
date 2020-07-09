package com.cs446.group7.bruno.utils;

// Represents an asynchronous piece of code that will eventually call a callback
public interface Closure<Success, Failure> {
    void run(Callback<Success, Failure> callback);
}
