package com.cs446.group7.bruno.viewmodels;

import android.content.DialogInterface;

import com.cs446.group7.bruno.routing.Route;
import com.google.android.gms.maps.model.LatLng;

public interface OnRouteViewModelDelegate {
    void setupUI(int userAvatarDrawableResourceId);
    void updateCurrentSongUI(final String name, final String album);
    void drawRoute(final Route route);
    void animateCamera(final LatLng location,
                       int cameraTilt,
                       int cameraZoom);
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
}
