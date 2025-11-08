package com.bruno.android.ui.onboarding;

import android.content.DialogInterface;

public interface OnboardingPermissionViewModelDelegate {
    void updateAllAccessRequestStatus(boolean accessToLocationPermission,
                                      boolean accessToLocationService,
                                      boolean accessToActiveInternet,
                                      boolean accessToSpotify);

    void showPopUp(final String title,
                   final String message,
                   final String positiveButtonText,
                   final DialogInterface.OnClickListener positiveButtonClickListener,
                   boolean isCancelable);

    void updatePrimaryButton(String text);

    void moveToNextTab();

    void redirectSpotifyInstallationInGooglePlay();
}
