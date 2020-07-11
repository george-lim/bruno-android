package com.cs446.group7.bruno.ui.routeplanning;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

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

import java.util.List;

public class RoutePlanningFragment extends Fragment {
    private static final int[] DURATION_VALUES = { 15, 30, 45, 60, 75, 90, 105, 120 };
    private static final Capability[] REQUIRED_CAPABILITIES = { Capability.LOCATION, Capability.INTERNET };

    private boolean isRequestingCapability = false;
    private boolean hasDrawnRouteOnce = false;
    private RouteViewModel model;
    private GoogleMap map;
    private Button startBtn;
    private Button walkingModeBtn;
    private Button runningModeBtn;
    private NumberPicker durationPicker;
    private CardView cardView;
    private View mapFragmentView;

    public final String TAG = this.getClass().getSimpleName();

    private OnMapReadyCallback mapCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            map.getUiSettings().setRotateGesturesEnabled(false);
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
        buildCardView(view);

        if (MainActivity.getCapabilityService().isCapabilityEnabled(Capability.LOCATION)) {
            model.initCurrentLocation();
        }

        return view;
    }

    private void buildCardView(final View view) {
        startBtn = view.findViewById(R.id.buttn_start_walking);
        startBtn.setOnClickListener(this::handleStartWalkingClick);
        setStartBtnState(isRoutePlanningComplete());

        walkingModeBtn = view.findViewById(R.id.btn_walking_mode);
        walkingModeBtn.setSelected(true);
        walkingModeBtn.setOnClickListener(this::handleWalkingModeClick);

        runningModeBtn = view.findViewById(R.id.btn_running_mode);
        runningModeBtn.setOnClickListener(this::handleRunningModeClick);

        durationPicker = view.findViewById(R.id.num_picker_exercise_duration);
        durationPicker.setMinValue(0);
        durationPicker.setMaxValue(DURATION_VALUES.length - 1);
        durationPicker.setDisplayedValues(intArrayToStringArray(DURATION_VALUES));
        durationPicker.setOnScrollListener(this::handleDurationScroll);
        durationPicker.setValue(0);

        cardView = view.findViewById(R.id.card_view_route_planning);
        mapFragmentView = view.findViewById(R.id.planning_map);
    }

    private void setStartBtnState(boolean isReady) {
        startBtn.setText(isReady ? "Start" : "Generate Route");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.planning_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(mapCallback);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        model.getRouteResult().removeObservers(getViewLifecycleOwner());
    }

    @Override
    public void onPause() {
        super.onPause();
        MainActivity.getLocationService().removeSubscriber(model);
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.getLocationService().addSubscriber(model);
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
            int duration = DURATION_VALUES[numberPicker.getValue()];
            model.setDuration(duration);
        }
    }

    private boolean isRoutePlanningComplete() {
        return !model.isStartUp() && model.getRouteResult().getValue() != null;
    }

    private void handleStartWalkingClick(final View view) {
        if (isRequestingCapability) return;
        isRequestingCapability = true;

        MainActivity.getCapabilityService().request(REQUIRED_CAPABILITIES, new Callback<Void, Void>() {
            @Override
            public void onSuccess(Void result) {
                if (isRoutePlanningComplete()) {
                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                    navController.navigate(R.id.action_fragmenttoplevel_to_fragmentonroute);
                }
                else if (model.isStartUp()) {
                    // updating UI to be consistent with DURATION_VALUES[0] in case fragment is resumed
                    // after never receiving location updates and user has fiddled with durationPicker
                    durationPicker.setValue(0);
                    model.setDuration(DURATION_VALUES[0]);
                    model.initCurrentLocation();
                }

                isRequestingCapability = false;
            }

            @Override
            public void onFailed(Void result) {
                isRequestingCapability = false;
            }
        });
    }

    private void observeRouteResult() {
        model.getRouteResult().observe(getViewLifecycleOwner(), route -> {
            if (route.getRoute() != null) {
                drawRoute(route.getRoute());
            } else if (route.getError() != null) {
                Log.e(TAG, route.getError().getDescription());

                if (route.getUnderlyingException() != null) {
                    Log.e(TAG, route.getUnderlyingException().getLocalizedMessage());
                }
            }

            setStartBtnState(isRoutePlanningComplete());
        });
    }

    private void drawRoute(final Route route) {
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

        map.addMarker(new MarkerOptions()
                .position(decodedPath.get(0)))
                .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        map.addPolyline(new PolylineOptions().addAll(route.getDecodedPath()));
    }

    private static String[] intArrayToStringArray(int[] intArray) {
        String[] result = new String[intArray.length];
        for (int i = 0; i < intArray.length; ++i) {
            result[i] = Integer.toString(intArray[i]);
        }
        return result;
    }
}
