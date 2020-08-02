package com.cs446.group7.bruno.viewmodels;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.cs446.group7.bruno.models.TrackSegment;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface OnRouteViewModelDelegate {
    void setupUI(int userAvatarDrawableResourceId);
    void updateCurrentSongUI(final String name, final String artists);
    void clearMap();
    void drawRoute(@NonNull final List<TrackSegment> trackSegments);
    void updateCheckpointMarker(final LatLng location, final double radius);
    void animateCamera(final LatLng location, float bearing, int cameraTilt, int cameraZoom);
    void showProgressDialog(final String title,
                            final String message,
                            boolean isIndeterminate,
                            boolean isCancelable);
    void dismissProgressDialog();
    void showAlertDialog(final String title,
                         final String message,
                         final String positiveButtonText,
                         final DialogInterface.OnClickListener positiveButtonClickListener,
                         boolean isCancelable);
    void showAlertDialog(final String title,
                         final String message,
                         final String positiveButtonText,
                         final DialogInterface.OnClickListener positiveButtonClickListener,
                         final String negativeButtonText,
                         final DialogInterface.OnClickListener negativeButtonClickListener,
                         boolean isCancelable);
    void navigateToPreviousScreen();
    void updateDistanceBetweenUserAndPlaylist(final String progressText,
                                              final Drawable progressIcon,
                                              int colour);
    void updateDistanceToCheckpoint(final String distanceText);
    void showRouteInfoCard();
}
