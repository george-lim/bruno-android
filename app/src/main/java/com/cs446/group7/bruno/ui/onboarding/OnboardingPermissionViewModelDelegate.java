package com.cs446.group7.bruno.ui.onboarding;

public interface OnboardingPermissionViewModelDelegate {
    void updateAllAccessRequestStatus(boolean accessToLocationPermission,
                                      boolean accessToLocationService,
                                      boolean accessToActiveInternet,
                                      boolean accessToSpotify);
    void showSkipAllowAccessPopUp();
    void updatePrimaryButton(final String text);
    void moveToNextTab();
}
