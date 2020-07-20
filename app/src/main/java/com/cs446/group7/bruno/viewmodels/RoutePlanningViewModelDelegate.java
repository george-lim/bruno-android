package com.cs446.group7.bruno.viewmodels;

import com.cs446.group7.bruno.routing.Route;
import com.google.android.gms.maps.model.LatLng;

public interface RoutePlanningViewModelDelegate {
    void setupUI(final String startBtnText,
                 boolean isWalkingModeBtnSelected,
                 final String[] durationPickerDisplayedValues,
                 int durationPickerMinValue,
                 int durationPickerMaxValue,
                 int durationPickerValue,
                 int userAvatarDrawableResourceId);
    void updateStartBtnText(final String text);
    void updateSelectedModeBtn(boolean isWalkingModeBtnSelected);
    void clearMap();
    void drawRoute(final Route route);
    void moveUserMarker(final LatLng location);
    void showRouteGenerationError(final String errorMessage);
    void navigateToNextScreen();
}
