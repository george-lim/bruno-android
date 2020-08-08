package com.cs446.group7.bruno.ui.routeplanning;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.cs446.group7.bruno.models.TrackSegment;
import com.cs446.group7.bruno.utils.BitmapUtils;
import com.cs446.group7.bruno.viewmodels.RoutePlanningViewModel;
import com.cs446.group7.bruno.viewmodels.RoutePlanningViewModelDelegate;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
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
    private int startBtnEnableColour = 0;
    private int startBtnDisableColour = 0;

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

            RouteModel model = new ViewModelProvider(requireActivity()).get(RouteModel.class);
            viewModel = new RoutePlanningViewModel(
                    requireActivity().getApplicationContext(),
                    model,
                    this);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.onDestroyView();
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

    private void handleDurationSelected(int durationIndex) {
        viewModel.handleDurationSelected(durationIndex);
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
        durationPicker.setOnScrollListener((numberPicker, scrollState) -> {
            if (scrollState == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE) {
                handleDurationSelected(numberPicker.getValue());
            }
        });

        startBtnEnableColour = getResources().getColor(R.color.colorPrimary, null);
        startBtnDisableColour = getResources().getColor(R.color.colorDisable, null);

        updateStartBtnEnabled(true);
        updateStartBtnText(startBtnText);
        updateSelectedModeBtn(isWalkingModeBtnSelected);

        map.getUiSettings().setRotateGesturesEnabled(false);

        durationPicker.setDisplayedValues(durationPickerDisplayedValues);
        durationPicker.setMinValue(durationPickerMinValue);
        durationPicker.setMaxValue(durationPickerMaxValue);
        durationPicker.setValue(durationPickerValue);

        userMarkerIcon = getUserMarkerIcon(userAvatarDrawableResourceId);
    }

    public void updateStartBtnEnabled(boolean isEnabled) {
        startBtn.setEnabled(isEnabled);

        if (isEnabled) {
            startBtn.getBackground().setTint(startBtnEnableColour);
        }
        else {
            startBtn.getBackground().setTint(startBtnDisableColour);
        }
    }

    public void updateStartBtnText(final String text) {
        startBtn.setText(text);
    }

    public void updateSelectedModeBtn(boolean isWalkingModeBtnSelected) {
        walkingModeBtn.setSelected(isWalkingModeBtnSelected);
        runningModeBtn.setSelected(!isWalkingModeBtnSelected);
    }

    public void clearMap() {
        map.clear();
        userMarker = null;
    }

    public void drawRoute(@NonNull final List<TrackSegment> trackSegments, float routeWidth) {
        for (TrackSegment trackSegment : trackSegments) {
            List<LatLng> trackSegmentLatLngs = trackSegment.getLatLngs();
            map.addPolyline(new PolylineOptions()
                    .addAll(trackSegmentLatLngs)
                    .color(trackSegment.getRouteColour())
                    .width(routeWidth));
        }
    }

    public void animateCamera(final CameraUpdate cameraUpdate) {
        // Do not animate camera on initial route
        if (hasDrawnRouteOnce) {
            map.animateCamera(cameraUpdate);
        }
        else {
            map.moveCamera(cameraUpdate);
            hasDrawnRouteOnce = true;
        }
    }

    public void moveUserMarker(final LatLng latLng) {
        if (latLng == null) {
            return;
        }

        if (userMarker == null) {
            userMarker = map.addMarker(new MarkerOptions().position(latLng));
            userMarker.setIcon(userMarkerIcon);
        }
        else {
            userMarker.setPosition(latLng);
        }
    }

    public void showRouteProcessingError(final String errorMessage) {
        if (getActivity() != null) {
            Toast toast = Toast.makeText(
                    getActivity().getApplicationContext(),
                    errorMessage,
                    Toast.LENGTH_LONG
            );
            toast.show();
        }
    }

    public void navigateToNextScreen() {
        if (getActivity() != null) {
            NavController navController = Navigation.findNavController(
                    getActivity(),
                    R.id.nav_host_fragment
            );
            navController.navigate(R.id.action_fragmenttoplevel_to_fragmentonroute);
        }
    }

    public float getCardViewHeight() {
        return cardView.getHeight();
    }

    public float getMapViewHeight() {
        return mapFragmentView.getHeight();
    }
}
