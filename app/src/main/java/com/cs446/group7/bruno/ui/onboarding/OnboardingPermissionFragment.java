package com.cs446.group7.bruno.ui.onboarding;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cs446.group7.bruno.R;

public class OnboardingPermissionFragment extends Fragment {

    private OnboardingFragment onboardingFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onboardingFragment = (OnboardingFragment) this.getParentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_permission, container, false);
        setupRequestStatus(view);
        Button btnSkip = view.findViewById(R.id.btn_skip);
        btnSkip.setOnClickListener(this::handleSkip);
        Button btnAllowAccess = view.findViewById(R.id.btn_allow_access);
        btnAllowAccess.setOnClickListener(this::handleAllowAccess);
        return view;
    }

    private void setupRequestStatus(final View view) {
        setupRequestUI(view.findViewById(R.id.location_permission_status), true, "Location Permission", "blah");
        setupRequestUI(view.findViewById(R.id.location_hardware_status), false, "Location Service", "blah");
        setupRequestUI(view.findViewById(R.id.active_internet_status), false, "Active Internet", "blah");
        setupRequestUI(view.findViewById(R.id.spotify_status), true, "Spotify", "blah");
    }

    private void setupRequestUI(final View view, final boolean hasTrue, final String title, final String hint) {
        ImageView statusIcon = view.findViewById(R.id.request_status_icon);
        Drawable icon = hasTrue
                ? getResources().getDrawable(R.drawable.ic_check_circle, null)
                : getResources().getDrawable(R.drawable.ic_times_circle, null);
        icon.setColorFilter(hasTrue
            ? getResources().getColor(R.color.colorGood, null)
            : getResources().getColor(R.color.colorError, null), PorterDuff.Mode.SRC_IN);
        statusIcon.setImageDrawable(icon);
        TextView titleView = view.findViewById(R.id.request_title);
        titleView.setText(title);
        TextView hintView = view.findViewById(R.id.request_hint);
        hintView.setText(hint);
    }

    private void handleSkip(final View view) {
        showAlertDialog(
                null,
                "Permission can be added in settings. Please make sure you alos",
                getResources().getString(R.string.ok_button),
                (dialogInterface, i) -> {
                    onboardingFragment.moveToNextTab();
                },
                true
        );
    }

    private void handleAllowAccess(final View view) {
        onboardingFragment.moveToNextTab();
    }

    private void showAlertDialog(final String title,
                                final String message,
                                final String positiveButtonText,
                                final DialogInterface.OnClickListener positiveButtonClickListener,
                                boolean isCancelable) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, positiveButtonClickListener)
                .setCancelable(isCancelable)
                .create()
                .show();
    }
}