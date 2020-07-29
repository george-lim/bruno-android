package com.cs446.group7.bruno.auth;

import com.cs446.group7.bruno.utils.Callback;

public interface AuthService {
    // Retrieves access token
    void requestUserAuth(final Callback<String, Void> clientCallback);
}