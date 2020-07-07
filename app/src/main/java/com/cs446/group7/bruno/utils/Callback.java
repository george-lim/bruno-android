package com.cs446.group7.bruno.utils;

public interface Callback<Success, Failure> {
    void onSuccess(Success result);
    void onFailed(Failure result);
}
