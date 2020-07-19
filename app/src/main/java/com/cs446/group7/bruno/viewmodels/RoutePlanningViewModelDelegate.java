package com.cs446.group7.bruno.viewmodels;

import com.cs446.group7.bruno.routing.Route;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;

public interface RoutePlanningViewModelDelegate {
    void setupUI(final String startBtnText,
                 boolean isWalkingModeBtnSelected,
                 final String[] durationPickerDisplayedValues,
                 int durationPickerMinValue,
                 int durationPickerMaxValue,
                 int durationPickerValue);
    void updateStartBtnText(final String text);
    void updateSelectedModeBtn(boolean isWalkingModeBtnSelected);
    void moveUserMarker(final LatLng location, final BitmapDescriptor userMarkerIcon);
    void drawRoute(final Route route,
                   final LatLng location,
                   final BitmapDescriptor userMarkerIcon);
    void showRouteGenerationError(final String errorMessage);
    void navigateToNextScreen();
}
