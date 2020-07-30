package com.cs446.group7.bruno.viewmodels;

import androidx.annotation.NonNull;

import com.cs446.group7.bruno.colourizedroute.ColourizedRoute;
import com.google.android.gms.maps.model.LatLng;

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
    void drawRoute(@NonNull final ColourizedRoute colourizedRoute);
    void moveUserMarker(final LatLng location);
    void showRouteProcessingError(final String errorMessage);
    void navigateToNextScreen();
}
