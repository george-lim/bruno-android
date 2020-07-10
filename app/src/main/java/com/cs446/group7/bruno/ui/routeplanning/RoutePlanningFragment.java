package com.cs446.group7.bruno.ui.routeplanning;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.capability.Capability;
import com.cs446.group7.bruno.routing.Route;
import com.cs446.group7.bruno.utils.Callback;
import com.cs446.group7.bruno.viewmodels.RouteViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class RoutePlanningFragment extends Fragment {
    private final String[] DURATION_VALUES = { "15", "30", "45", "60", "75", "90", "105", "120" };
    // TODO: mechanism to maintain consistency between DEFAULT_DURATION and DURATION_VALUES[0]
    private final int DEFAULT_DURATION = 15;
    private final String TAG = getClass().getSimpleName();

    private RouteViewModel model;
    private GoogleMap map;
    private Button walkingModeBtn;
    private Button runningModeBtn;
    private NumberPicker durationPicker;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            observeRouteResult();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route_planning, container, false);

        model = new ViewModelProvider(requireActivity()).get(RouteViewModel.class);

        Button startBtn = view.findViewById(R.id.buttn_start_walking);
        startBtn.setOnClickListener(this::handleStartWalkingClick);

        walkingModeBtn = view.findViewById(R.id.btn_walking_mode);
        walkingModeBtn.setSelected(true);
        walkingModeBtn.setOnClickListener(this::handleWalkingModeClick);

        runningModeBtn = view.findViewById(R.id.btn_running_mode);
        runningModeBtn.setOnClickListener(this::handleRunningModeClick);

        durationPicker = view.findViewById(R.id.num_picker_exercise_duration);
        durationPicker.setMinValue(0);
        durationPicker.setMaxValue(DURATION_VALUES.length - 1);
        durationPicker.setDisplayedValues(DURATION_VALUES);
        durationPicker.setOnScrollListener(this::handleDurationScroll);
        durationPicker.setValue(0);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.planning_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Capability[] capabilities = { Capability.LOCATION, Capability.INTERNET };
        MainActivity.getCapabilityService().request(capabilities, new Callback<Void, Void>() {
            @Override
            public void onSuccess(Void result) {
                MainActivity.getLocationService().addSubscriber(model);
                if (model.isStartUp()) {
                    // updating UI to be consistent with DEFAULT_DURATION in case fragment is resumed
                    // after never receiving location updates and user has fiddled with durationPicker
                    durationPicker.setValue(0);
                    model.setDuration(DEFAULT_DURATION);
                    model.initCurrentLocation();
                }
            }

            @Override
            public void onFailed(Void result) { }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        MainActivity.getLocationService().removeSubscriber(model);
    }

    private void handleWalkingModeClick(final View view) {
        if (!walkingModeBtn.isSelected()) {
            model.setWalkingMode(true);
            walkingModeBtn.setSelected(true);
            runningModeBtn.setSelected(false);
        }
    }

    private void handleRunningModeClick(final View view) {
        if (!runningModeBtn.isSelected()) {
            model.setWalkingMode(false);
            walkingModeBtn.setSelected(false);
            runningModeBtn.setSelected(true);
        }
    }

    private void handleDurationScroll(final NumberPicker numberPicker, int scrollState) {
        if (scrollState == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE) {
            try {
                int duration = Integer.parseInt(DURATION_VALUES[numberPicker.getValue()]);
                model.setDuration(duration);
            } catch (NumberFormatException e) {
                Log.e(TAG, "There's an invalid integer in DURATION_VALUES");
                // TODO: do we want this fallback?
                numberPicker.setValue(0);
                model.setDuration(DEFAULT_DURATION);
            }
        }
    }

    private void handleStartWalkingClick(final View view) {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_fragmenttoplevel_to_fragmentonroute);
    }

    private void observeRouteResult() {
        model.getRouteResult().observe(getViewLifecycleOwner(), route -> {
            if (route.getRoute() != null) {
                drawRoute(route.getRoute());
            } else if (route.getError() != null) {
                String errorMessage = "";
                switch (route.getError()) {
                    case PARSE_ERROR:
                        errorMessage = "Error parsing route, please try again!";
                        break;
                    case SERVER_ERROR:
                        errorMessage = "A server error occurred, please try again!";
                        break;
                    case NO_CONNECTION_ERROR:
                        errorMessage = "No network, please enable internet access!";
                        break;
                    case OTHER_ERROR:
                        errorMessage = "Something went wrong, please try again!";
                        break;
                }

                Toast errorNotification = Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG);
                errorNotification.show();
            }
        });
    }

    private void drawRoute(Route route) {
        final List<LatLng> decodedPath = route.getDecodedPath();

        List<LatLng> markers = new ArrayList<>();
        int skipInterval = decodedPath.size() / 5;
        // sample a few points on the route to act as markers
        for (int i = 0; i < decodedPath.size(); i += skipInterval) {
            markers.add(decodedPath.get(i));
        }

        map.clear();

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (final LatLng p : markers) {
            boundsBuilder.include(p);
        }

        map.addMarker(new MarkerOptions()
                .position(markers.get(0)))
                .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        // centering on southernmost latitude and center longitude and then manually zooming
        // normally we'd just let the LatLngBounds automatically determine the center and zoom level,
        // but the route planning overlay on the map means we need to offset the route upwards
        LatLngBounds bounds = boundsBuilder.build();
        map.moveCamera(CameraUpdateFactory
                .newLatLngZoom(new LatLng(bounds.southwest.latitude, bounds.getCenter().longitude), zoom(route.getTotalDuration()))
        );

        map.addPolyline(new PolylineOptions().addAll(route.getDecodedPath()));
    }


    // this is a rough algorithm to determine zoom level based on totalDuration of exercise
    // all numbers chosen are based on trial and error, may need to be tweaked
    private float zoom(int totalDuration) {
        final float maxZoom = 18;
        final double durationUnit = 450d;
        // approximating run duration as double walk duration for now
        totalDuration = runningModeBtn.isSelected() ? totalDuration * 2 : totalDuration;
        float diff = (float) (Math.log(totalDuration / durationUnit) / Math.log(2));
        return maxZoom - diff;
    }
}