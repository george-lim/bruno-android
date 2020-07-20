package com.cs446.group7.bruno.ui.routeplanning;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.models.RouteModel;
import com.cs446.group7.bruno.routing.Route;
import com.cs446.group7.bruno.utils.BitmapUtils;
import com.cs446.group7.bruno.viewmodels.RoutePlanningViewModel;
import com.cs446.group7.bruno.viewmodels.RoutePlanningViewModelDelegate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class RoutePlanningFragment extends Fragment implements RoutePlanningViewModelDelegate {

    // MARK: - UI components

    private GoogleMap map;
    private Button startBtn;
    private Button walkingModeBtn;
    private Button runningModeBtn;
    private NumberPicker durationPicker;
    private CardView cardView;
    private View mapFragmentView;

    // MARK: - Private members

    private RoutePlanningViewModel viewModel;

    private Marker userMarker;
    private BitmapDescriptor userMarkerIcon;

    private boolean hasDrawnRouteOnce = false;

    // MARK: - Lifecycle methods

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route_planning, container, false);
        startBtn = view.findViewById(R.id.buttn_start_walking);
        walkingModeBtn = view.findViewById(R.id.btn_walking_mode);
        runningModeBtn = view.findViewById(R.id.btn_running_mode);
        durationPicker = view.findViewById(R.id.num_picker_exercise_duration);
        cardView = view.findViewById(R.id.card_view_route_planning);
        mapFragmentView = view.findViewById(R.id.planning_map);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.planning_map);

        mapFragment.getMapAsync(googleMap -> {
            map = googleMap;
            map.getUiSettings().setRotateGesturesEnabled(false);

            RouteModel model = new ViewModelProvider(requireActivity()).get(RouteModel.class);
            viewModel = new RoutePlanningViewModel(getActivity().getApplicationContext(), model, this);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }

    // MARK: - User actions

    private void handleStartWalkingClick(final View view) {
        viewModel.handleStartWalkingClick();
    }

    private void handleWalkingModeClick(final View view) {
        viewModel.handleWalkingModeClick();
    }

    private void handleRunningModeClick(final View view) {
        viewModel.handleRunningModeClick();
    }

    private void handleDurationSelected(final NumberPicker numberPicker, int scrollState) {
        if (scrollState == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE) {
            viewModel.handleDurationSelected(numberPicker.getValue());
        }
    }

    // MARK: - RoutePlanningViewModelDelegate methods

    private BitmapDescriptor getUserMarkerIcon(int avatarResourceId) {
        Drawable avatarDrawable = getResources().getDrawable(avatarResourceId, null);
        return BitmapDescriptorFactory.fromBitmap(BitmapUtils.getBitmapFromVectorDrawable(avatarDrawable));
    }

    public void setupUI(final String startBtnText,
                        boolean isWalkingModeBtnSelected,
                        final String[] durationPickerDisplayedValues,
                        int durationPickerMinValue,
                        int durationPickerMaxValue,
                        int durationPickerValue,
                        int userAvatarDrawableResourceId) {
        startBtn.setOnClickListener(this::handleStartWalkingClick);
        walkingModeBtn.setOnClickListener(this::handleWalkingModeClick);
        runningModeBtn.setOnClickListener(this::handleRunningModeClick);
        durationPicker.setOnScrollListener(this::handleDurationSelected);

        updateStartBtnText(startBtnText);
        updateSelectedModeBtn(isWalkingModeBtnSelected);

        durationPicker.setDisplayedValues(durationPickerDisplayedValues);
        durationPicker.setMinValue(durationPickerMinValue);
        durationPicker.setMaxValue(durationPickerMaxValue);
        durationPicker.setValue(durationPickerValue);

        userMarkerIcon = getUserMarkerIcon(userAvatarDrawableResourceId);
    }

    public void updateStartBtnText(final String text) {
        startBtn.setText(text);
    }

    public void updateSelectedModeBtn(boolean isWalkingModeBtnSelected) {
        walkingModeBtn.setSelected(isWalkingModeBtnSelected);
        runningModeBtn.setSelected(!isWalkingModeBtnSelected);
    }

    public void drawRoute(final Route route, final LatLng location) {
        map.clear();

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        final float cardViewHeightDp = cardView.getHeight() / displayMetrics.density;
        final float mapFragmentHeightDp = mapFragmentView.getHeight() / displayMetrics.density;
        // from tests it seems like we need to add some height to cardView to get a good blockedScreenFraction
        final double blockedScreenFraction = (cardViewHeightDp + 40) / mapFragmentHeightDp;

        final List<LatLng> decodedPath = route.getDecodedPath();

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (final LatLng p : decodedPath) {
            boundsBuilder.include(p);
        }

        LatLngBounds bounds = boundsBuilder.build();
        final LatLng minLat = bounds.southwest, maxLat = bounds.northeast;

        // compute offset
        final double H = maxLat.latitude - minLat.latitude;
        final double T = H / (1 - blockedScreenFraction);
        final double offset = T - 2 * H;

        // find mirror point of maxLat and include in bounds
        final LatLng mirrorMaxLat= new LatLng(2 * minLat.latitude - maxLat.latitude - offset, maxLat.longitude);
        boundsBuilder.include(mirrorMaxLat);

        bounds = boundsBuilder.build();
        final int padding = 200;

        // Do not animate camera on initial route
        if (hasDrawnRouteOnce) {
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
        }
        else {
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
            hasDrawnRouteOnce = true;
        }

        userMarker = null;
        moveUserMarker(location);

        map.addPolyline(new PolylineOptions().addAll(route.getDecodedPath()));
    }

    public void moveUserMarker(final LatLng location) {
        if (userMarker == null) {
            userMarker = map.addMarker(new MarkerOptions().position(location));
            userMarker.setIcon(userMarkerIcon);
        }
        else {
            userMarker.setPosition(location);
        }
    }

    public void showRouteGenerationError(final String errorMessage) {
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    public void navigateToNextScreen() {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_fragmenttoplevel_to_fragmentonroute);
    }
}
