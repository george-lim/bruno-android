package com.cs446.group7.bruno.auth;

import com.cs446.group7.bruno.utils.Callback;

public class MockAuthService implements AuthService {
    public void requestUserAuth(final Callback<String, Void> clientCallback) {
        clientCallback.onSuccess("token");
    }
}
