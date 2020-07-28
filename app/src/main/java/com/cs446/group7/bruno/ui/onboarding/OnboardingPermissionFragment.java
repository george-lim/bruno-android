package com.cs446.group7.bruno.ui.onboarding;

import android.app.AlertDialog;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cs446.group7.bruno.R;

public class OnboardingPermissionFragment extends Fragment implements OnboardingPermissionViewModelDelegate {

    private OnboardingFragment onboardingFragment;
    private OnboardingPermissionViewModel viewModel;
    private Button btnAllowAccess;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onboardingFragment = (OnboardingFragment) this.getParentFragment();
        viewModel = new OnboardingPermissionViewModel(getActivity().getApplicationContext(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_permission, container, false);
        Button btnSkip = view.findViewById(R.id.btn_skip);
        btnSkip.setOnClickListener(this::handleSkip);
        btnAllowAccess = view.findViewById(R.id.btn_allow_access);
        btnAllowAccess.setOnClickListener(this::handleAllowAccess);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.updateUserAccess();
    }

    private void handleSkip(final View view) {
        viewModel.handleSkip();
    }

    private void handleAllowAccess(final View view) {
        viewModel.handleAllowAccess();
    }


    public void updatePrimaryButton(final String text) {
        btnAllowAccess.setText(text);
    }

    public void updateAllAccessRequestStatus(final boolean accessToLocationPermission,
                                             final boolean accessToLocationService,
                                             final boolean accessToActiveInternet,
                                             final boolean accessToSpotify) {
        updateAccessRequestStatus(
                getView().findViewById(R.id.location_permission_status),
                accessToLocationPermission,
                getResources().getString(R.string.onboarding_request_location_permission_title),
                getResources().getString(R.string.onboarding_request_location_permission_hint));
        updateAccessRequestStatus(
                getView().findViewById(R.id.location_hardware_status),
                accessToLocationService,
                getResources().getString(R.string.onboarding_request_location_service_title),
                getResources().getString(R.string.onboarding_request_location_service_hint));
        updateAccessRequestStatus(
                getView().findViewById(R.id.active_internet_status),
                accessToActiveInternet,
                getResources().getString(R.string.onboarding_request_active_internet_title),
                getResources().getString(R.string.onboarding_request_active_internet_hint));
        updateAccessRequestStatus(
                getView().findViewById(R.id.spotify_status),
                accessToSpotify,
                getResources().getString(R.string.onboarding_request_spotify_title),
                getResources().getString(R.string.onboarding_request_spotify_hint));
    }

    private void updateAccessRequestStatus(final View view, final boolean enabled, final String title, final String hint) {
        ImageView statusIcon = view.findViewById(R.id.request_status_icon);
        Drawable icon = enabled
                ? getResources().getDrawable(R.drawable.ic_check_circle, null)
                : getResources().getDrawable(R.drawable.ic_times_circle, null);
        icon.setColorFilter(enabled
                ? getResources().getColor(R.color.colorGood, null)
                : getResources().getColor(R.color.colorError, null), PorterDuff.Mode.SRC_IN);
        statusIcon.setImageDrawable(icon);
        TextView titleView = view.findViewById(R.id.request_title);
        titleView.setText(title);
        TextView hintView = view.findViewById(R.id.request_hint);
        hintView.setText(hint);
    }

    public void showSkipAllowAccessPopUp() {
        new AlertDialog.Builder(getContext())
                .setTitle(getResources().getString(R.string.onboarding_missing_access_title))
                .setMessage(getResources().getString(R.string.onboarding_missing_access_text))
                .setPositiveButton(
                        getResources().getString(R.string.ok_button),
                        (dialogInterface, i) -> {
                            moveToNextTab();
                        })
                .setCancelable(true)
                .create()
                .show();
    }

    public void moveToNextTab() {
        onboardingFragment.moveToNextTab();
    }
}