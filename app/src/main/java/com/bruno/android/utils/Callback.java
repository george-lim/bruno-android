package com.bruno.android.utils;

public interface Callback<Success, Failure> {
    void onSuccess(Success result);

    void onFailed(Failure result);
}
