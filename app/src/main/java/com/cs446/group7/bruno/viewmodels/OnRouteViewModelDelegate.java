package com.cs446.group7.bruno.viewmodels;

import android.content.DialogInterface;

public interface OnRouteViewModelDelegate {
    void setupUI();
    void showAlertDialog(final String title,
                         final String message,
                         final String positiveButtonText,
                         final DialogInterface.OnClickListener positiveButtonClickListener,
                         final String negativeButtonText,
                         final DialogInterface.OnClickListener negativeButtonClickListener,
                         boolean isCancelable);
    void navigateToPreviousScreen();
}
