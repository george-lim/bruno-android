package com.cs446.group7.bruno.ui.onboarding;

public interface OnboardingPermissionViewModelDelegate {
    void updateAllAccessRequestStatus(final boolean accessToLocationPermission,
                                      final boolean accessToLocationService,
                                      final boolean accessToActiveInternet,
                                      final boolean accessToSpotify);
    void showSkipAllowAccessPopUp();
    void updatePrimaryButton(final String text);
    void moveToNextTab();
}
