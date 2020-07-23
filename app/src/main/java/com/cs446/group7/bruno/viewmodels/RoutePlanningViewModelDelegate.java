package com.cs446.group7.bruno.viewmodels;

import com.cs446.group7.bruno.routing.RouteTrackMapping;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface RoutePlanningViewModelDelegate {
    void setupUI(final String startBtnText,
                 boolean isWalkingModeBtnSelected,
                 final String[] durationPickerDisplayedValues,
                 int durationPickerMinValue,
                 int durationPickerMaxValue,
                 int durationPickerValue,
                 int userAvatarDrawableResourceId);
    void updateStartBtnEnabled(boolean isEnabled);
    void updateStartBtnText(final String text);
    void updateSelectedModeBtn(boolean isWalkingModeBtnSelected);
    void clearMap();
    void drawRoute(final List<RouteTrackMapping> routeTrackMappings,
                   final int[] colours);
    void moveUserMarker(final LatLng location);
    void showRouteGenerationError(final String errorMessage);
    void navigateToNextScreen();
}
