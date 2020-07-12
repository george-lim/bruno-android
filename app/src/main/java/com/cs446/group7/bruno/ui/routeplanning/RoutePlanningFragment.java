package com.cs446.group7.bruno.ui.routeplanning;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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
    private int[] routeColours;

    public final String TAG = this.getClass().getSimpleName();

    private OnMapReadyCallback mapCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            map.getUiSettings().setRotateGesturesEnabled(false);
            observeRouteResult();

            if (MainActivity.getCapabilityService().isEveryCapabilityEnabled(REQUIRED_CAPABILITIES)) {
                startRouteGeneration();
            } else {
                updateUI();
            }
        }
    };

    private void startRouteGeneration() {
        int duration = DURATION_VALUES[durationPicker.getValue()];
        model.startRouteGeneration(duration);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route_planning, container, false);
        model = new ViewModelProvider(requireActivity()).get(RouteViewModel.class);
        buildCardView(view);
        routeColours = getColours();
        return view;
    }

    private void buildCardView(final View view) {
        startBtn = view.findViewById(R.id.buttn_start_walking);
        startBtn.setOnClickListener(this::handleStartWalkingClick);

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

    private void updateUI() {
        String startBtnText = model.isRoutePlanningComplete()
                ? getResources().getString(R.string.route_planning_start)
                : getResources().getString(R.string.route_planning_generate_route);

        startBtn.setText(startBtnText);
        cardView.setVisibility(View.VISIBLE);
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

    private void handleStartWalkingClick(final View view) {
        if (isRequestingCapability) return;
        isRequestingCapability = true;

        MainActivity.getCapabilityService().request(REQUIRED_CAPABILITIES, new Callback<Void, Void>() {
            @Override
            public void onSuccess(Void result) {
                if (model.isRoutePlanningComplete()) {
                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                    navController.navigate(R.id.action_fragmenttoplevel_to_fragmentonroute);
                }
                else if (!model.hasGeneratedRouteOnce()) {
                    startRouteGeneration();
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
                Toast errorNotification = Toast.makeText(getContext(),
                        route.getError().getDescription(), Toast.LENGTH_LONG);
                errorNotification.show();

                if (route.getUnderlyingException() != null) {
                    Log.e(TAG, route.getUnderlyingException().getLocalizedMessage());
                }
            }

            updateUI();
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
                .setIcon(model.getAvatarMarker());

        int numPoints = route.getDecodedPath().size();
        final float lineWidth = 15;

        if (routeColours == null || routeColours.length < 1 || numPoints < routeColours.length) {
            map.addPolyline(new PolylineOptions()
                    .addAll(route.getDecodedPath())
                    .width(lineWidth));
            return;
        }

        int interval = numPoints / routeColours.length;

        int pointInd = 0;
        for (int i = 0; i < routeColours.length; ++i) {
            map.addPolyline(new PolylineOptions()
                    .addAll(route.getDecodedPath().subList(pointInd, pointInd + interval))
                    .color(routeColours[i])
                    .width(lineWidth));
            pointInd += interval;

            // fill in breaks between segments
            if (pointInd < numPoints) {
                map.addPolyline(new PolylineOptions()
                        .addAll(route.getDecodedPath().subList(pointInd - 1, pointInd + 1))
                        .color(routeColours[i])
                        .width(lineWidth));
            }
        }
    }

    private static String[] intArrayToStringArray(int[] intArray) {
        String[] result = new String[intArray.length];
        for (int i = 0; i < intArray.length; ++i) {
            result[i] = Integer.toString(intArray[i]);
        }
        return result;
    }

    private int[] getColours() {
        int[] colours;
        try {
            colours = getResources().getIntArray(R.array.colorRouteList);
        } catch (Resources.NotFoundException e) {
            colours = null;
        }
        return  colours;
    }

}