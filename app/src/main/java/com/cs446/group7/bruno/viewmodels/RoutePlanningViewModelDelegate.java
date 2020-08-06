package com.cs446.group7.bruno.viewmodels;

import androidx.annotation.NonNull;

import com.cs446.group7.bruno.models.TrackSegment;
import com.google.android.gms.maps.CameraUpdate;
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
    void drawRoute(@NonNull final List<TrackSegment> trackSegments, float routeWidth);
    void animateCamera(final CameraUpdate cameraUpdate);
    void moveUserMarker(final LatLng latlng);
    void showRouteProcessingError(final String errorMessage);
    void navigateToNextScreen();
    float getCardViewHeight();
    float getMapViewHeight();
}
