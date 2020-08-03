package com.cs446.group7.bruno.ui.onboarding;

import android.content.Context;

import com.cs446.group7.bruno.BuildConfig;
import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.capability.Capability;
import com.cs446.group7.bruno.capability.CapabilityService;
import com.cs446.group7.bruno.music.player.MockMusicPlayerImpl;
import com.cs446.group7.bruno.music.player.MusicPlayer;
import com.cs446.group7.bruno.spotify.SpotifyService;
import com.cs446.group7.bruno.utils.Callback;
import com.cs446.group7.bruno.utils.NoFailClosureQueue;

public class OnboardingPermissionViewModel {

    private Context context;
    private OnboardingPermissionViewModelDelegate delegate;

    private boolean accessToLocationPermission = false;
    private boolean accessToLocationService = false;
    private boolean accessToActiveInternet = false;
    private boolean accessToSpotify = false;


    public OnboardingPermissionViewModel(final Context context, final OnboardingPermissionViewModelDelegate delegate) {
        this.context = context;
        this.delegate = delegate;
    }

    public void handleSkip() {
        if (isAllAccessAllowed()) {
            delegate.moveToNextTab();
        } else if (!accessToSpotify) {
            showSpotifyNotInstallPopUp();
        } else {
            delegate.showPopUp(
                    context.getResources().getString(R.string.onboarding_missing_access_title),
                    context.getResources().getString(R.string.onboarding_missing_access_text),
                    context.getResources().getString(R.string.ok_button),
                    (dialogInterface, i) -> delegate.moveToNextTab(),
                    true
            );
        }
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
        queue.add((result, callback) -> capabilityService.request(Capability.LOCATION, new Callback<Void, Void>() {
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
        queue.add((result, callback) -> capabilityService.request(Capability.INTERNET, new Callback<Void, Void>() {
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

    private MusicPlayer getMusicPlayer() {
        return BuildConfig.DEBUG
                ? new MockMusicPlayerImpl()
                : MainActivity.getSpotifyService().getPlayerService();
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
