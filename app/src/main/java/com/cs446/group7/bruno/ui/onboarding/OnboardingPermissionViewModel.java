package com.cs446.group7.bruno.ui.onboarding;

import android.content.Context;

import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.capability.Capability;
import com.cs446.group7.bruno.capability.CapabilityService;
import com.cs446.group7.bruno.spotify.SpotifyService;
import com.cs446.group7.bruno.spotify.SpotifyServiceError;
import com.cs446.group7.bruno.utils.Callback;
import com.cs446.group7.bruno.utils.ClosureQueue;

public class OnboardingPermissionViewModel {

    private Context context;
    private OnboardingPermissionViewModelDelegate delegate;
    private CapabilityService capability;
    private SpotifyService spotify;

    private boolean accessToLocationPermission = false;
    private boolean accessToLocationService = false;
    private boolean accessToActiveInternet = false;
    private boolean accessToSpotify = false;


    public OnboardingPermissionViewModel(final Context context, final OnboardingPermissionViewModelDelegate delegate) {
        this.context = context;
        this.delegate = delegate;
        capability = MainActivity.getCapabilityService();
        spotify = MainActivity.getSpotifyService();
    }

    public void handleSkip() {
        delegate.showSkipAllowAccessPopUp();
    }

    public void handleAllowAccess() {
        // All access are granted by the user, automatically move to next tab
        if (isAllAccessAllowed()) {
            delegate.moveToNextTab();
            return;
        }

        // Request access from the user
        ClosureQueue<Void, Void> queue = new ClosureQueue<>();
        queue.add((result, callback) -> {
            capability.request(Capability.LOCATION, new Callback<Void, Void>() {
                @Override
                public void onSuccess(Void result) {
                    updateUserAccess();
                    callback.onSuccess(null);
                }

                @Override
                public void onFailed(Void result) {
                    callback.onSuccess(null);
                }
            });
        });
        queue.add((result, callback) -> {
            capability.request(Capability.INTERNET, new Callback<Void, Void>() {
                @Override
                public void onSuccess(Void result) {
                    updateUserAccess();
                    callback.onSuccess(null);
                }

                @Override
                public void onFailed(Void result) {
                    callback.onSuccess(null);
                }
            });
        });
        queue.add((result, callback) -> {
            spotify.connect(context, new Callback<Void, SpotifyServiceError>() {
                @Override
                public void onSuccess(Void result) {
                    accessToSpotify = true;
                    updateAccessRequestStatus();
                    spotify.disconnect();
                    callback.onSuccess(null);
                }

                @Override
                public void onFailed(SpotifyServiceError result) {
                    callback.onSuccess(null);
                }
            });
        });
        queue.run(new Callback<Void, Void>() {
            @Override
            public void onSuccess(Void result) {
                updateUserAccess();
            }

            @Override
            public void onFailed(Void result) {
                // NOOP
            }
        });
    }

    public void updateUserAccess() {
        accessToLocationPermission = capability.isPermissionEnabled(Capability.LOCATION);
        accessToLocationService = capability.isHardwareCapabilityEnabled(Capability.LOCATION);
        accessToActiveInternet = capability.isCapabilityEnabled(Capability.INTERNET);
        updateAccessRequestStatus();
        updatePrimaryAction();
    }

    private void updateAccessRequestStatus() {
        delegate.updateAllAccessRequestStatus(
                accessToLocationPermission,
                accessToLocationService,
                accessToActiveInternet,
                accessToSpotify);
    }

    private void updatePrimaryAction() {
        if (isAllAccessAllowed()) {
            delegate.updatePrimaryButton(context.getResources().getString(R.string.next_button));
        } else {
            delegate.updatePrimaryButton(context.getResources().getString(R.string.allow_access_button));
        }
    }

    private boolean isAllAccessAllowed() {
        return accessToLocationPermission &&
                accessToLocationService &&
                accessToActiveInternet &&
                accessToSpotify;
    }
}
