package com.cs446.group7.bruno.routing;

import androidx.annotation.Nullable;

import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;

import org.json.JSONException;

public class RouteGeneratorException extends RuntimeException {
    public RouteGeneratorException(Throwable throwable) {
        super(throwable);
    }

    @Nullable
    @Override
    public String getMessage() {
        Throwable throwable = getCause();

        if (throwable instanceof JSONException) {
            return "Error parsing route, please try again!";
        }
        else if (throwable instanceof NoConnectionError) {
            return "No network, please enable internet access!";
        }
        else if (throwable instanceof ServerError) {
            return "A server error occurred, please try again!";
        }
        else {
            return "Something went wrong, please try again!";
        }
    }
}
