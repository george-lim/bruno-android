package com.bruno.android.ui.onboarding;

import android.content.Context;

import com.bruno.android.MainActivity;
import com.bruno.android.R;
import com.bruno.android.capability.Capability;
import com.bruno.android.capability.CapabilityService;
import com.bruno.android.spotify.SpotifyService;
import com.bruno.android.utils.Callback;
import com.bruno.android.utils.NoFailClosureQueue;

public class OnboardingPermissionViewModel {

    private final Context context;
    private final OnboardingPermissionViewModelDelegate delegate;

    private boolean accessToLocationPermission = false;
    private boolean accessToLocationService = false;
    private boolean accessToActiveInternet = false;
    private boolean accessToSpotify = false;

    public OnboardingPermissionViewModel(final Context context, final OnboardingPermissionViewModelDelegate delegate) {
        this.context = context;
        this.delegate = delegate;
    }

    public void handleAllowAccess() {
        // All access are granted by the user, automatically move to next tab
        if (isAllAccessAllowed()) {
            delegate.moveToNextTab();
            return;
        }

        // Request access from the user
        // Capability request are chained instead of requesting in bulk is to update UI in between to match state of
        // granted access to icon UI.
        NoFailClosureQueue<Void> queue = new NoFailClosureQueue<>();
        CapabilityService capabilityService = MainActivity.getCapabilityService();
        queue.add((result, callback) -> capabilityService.request(Capability.LOCATION, new Callback<>() {
            @Override
            public void onSuccess(Void result) {
                updateUserAccess();
                callback.onSuccess(null);
            }

            @Override
            public void onFailed(Void result) {
                callback.onSuccess(null);
            }
        }));
        queue.add((result, callback) -> capabilityService.request(Capability.INTERNET, new Callback<>() {
            @Override
            public void onSuccess(Void result) {
                updateUserAccess();
                callback.onSuccess(null);
            }

            @Override
            public void onFailed(Void result) {
                callback.onSuccess(null);
            }
        }));
        queue.add((result, callback) -> {
            if (SpotifyService.isSpotifyInstalled(context)) {
                accessToSpotify = true;
                updateUserAccess();
            } else {
                showSpotifyNotInstallPopUp();
            }
            callback.onSuccess(null);
        });
        queue.run(result -> { /* NOOP since UI is already updated*/ });
    }

    public void updateUserAccess() {
        CapabilityService capabilityService = MainActivity.getCapabilityService();
        accessToLocationPermission = capabilityService.isPermissionEnabled(Capability.LOCATION);
        accessToLocationService = capabilityService.isHardwareCapabilityEnabled(Capability.LOCATION);
        accessToActiveInternet = capabilityService.isCapabilityEnabled(Capability.INTERNET);
        accessToSpotify = SpotifyService.isSpotifyInstalled(context);
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

    private void showSpotifyNotInstallPopUp() {
        delegate.showPopUp(
                context.getResources().getString(R.string.onboarding_missing_spotify_installation_title),
                context.getResources().getString(R.string.onboarding_missing_spotify_installation_description),
                context.getResources().getString(R.string.install_button),
                (dialogInterface, i) -> delegate.redirectSpotifyInstallationInGooglePlay(),
                true
        );
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
