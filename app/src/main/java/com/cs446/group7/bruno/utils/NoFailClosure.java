package com.cs446.group7.bruno.utils;

// Represents an asynchronous piece of code that will eventually call a callback
public interface NoFailClosure<Success> {
    void run(NoFailCallback<Success> callback);
}
