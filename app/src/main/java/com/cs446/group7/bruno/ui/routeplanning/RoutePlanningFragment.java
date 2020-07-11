package com.cs446.group7.bruno.ui.routeplanning;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.capability.Capability;
import com.cs446.group7.bruno.location.LocationServiceSubscriber;
import com.cs446.group7.bruno.spotify.SpotifyService;
import com.cs446.group7.bruno.ui.onroute.OnRouteFragment;
import com.cs446.group7.bruno.utils.Callback;
import com.cs446.group7.bruno.utils.NoFailCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class RoutePlanningFragment extends Fragment {

    private GoogleMap googleMap;
    private Marker currentLocationMarker;
    private boolean isRequestingCapability = false;
    private final String TAG = getClass().getSimpleName();

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this case, we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to
     * install it inside the SupportMapFragment. This method will only be triggered once the
     * user has installed Google Play services and returned to the app.
     */
    private OnMapReadyCallback onMapReadyCallback = googleMap -> {
        this.googleMap = googleMap;

        initMarkers();
    };

    /**
     * Receives location updates periodically.
     */
    private LocationServiceSubscriber onLocationUpdatedCallback = new LocationServiceSubscriber() {
        @Override
        public void onLocationUpdate(@NonNull final Location location) {
            Log.i(TAG, location.toString());
            final LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());
            // TODO: Remove
            //Toast.makeText(getContext(), String.format("Location: (%s, %s)", location.getLatitude(), location.getLongitude()), Toast.LENGTH_SHORT).show();

            if (currentLocationMarker == null) return;

            currentLocationMarker.setPosition(newLocation);

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 15));
        }
    };

    /**
     * Initial location logic here.
     */
    private void initMarkers() {
        if (googleMap == null) return;
        requestLocationUpdates(location -> {
            final LatLng initialLocation = new LatLng(location.getLatitude(), location.getLongitude());
            final String msg = String.format("Initial Location: (%s, %s)", initialLocation.latitude, initialLocation.longitude);

            Log.i(TAG, msg);

            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            currentLocationMarker = googleMap.addMarker(new MarkerOptions().position(initialLocation).title("Your location"));
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route_planning, container, false);
        Button btn = view.findViewById(R.id.buttn_start_walking);
        btn.setOnClickListener(this::handleStartWalkingClick);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.planning_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(onMapReadyCallback);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.getLocationService().addSubscriber(onLocationUpdatedCallback);
        requestLocationUpdates();

        if (currentLocationMarker == null) {
            initMarkers();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MainActivity.getLocationService().removeSubscriber(onLocationUpdatedCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity.getLocationService().stopLocationUpdates();
    }

    /**
     * Requests location permissions from Capability service. Requires mutex to prevent duplicate requests
     */
    private void requestLocationUpdates() {
        requestLocationUpdates(null);
    }

    /**
     * Requests location permissions from Capability service and takes in a callback that returns the initial position
     */
    private void requestLocationUpdates(@Nullable final NoFailCallback<Location> callback) {
        if (isRequestingCapability) return;
        isRequestingCapability = true;

        Capability[] capabilities = new Capability[] {
                Capability.LOCATION,
                Capability.INTERNET
        };

        MainActivity.getCapabilityService().request(capabilities, new Callback<Void, Void>() {
            @Override
            public void onSuccess(Void result) {
                MainActivity.getLocationService().startLocationUpdates(callback);
                isRequestingCapability = false;
            }

            @Override
            public void onFailed(Void result) {
                isRequestingCapability = false;
            }
        });
    }

    private void handleStartWalkingClick(final View view) {

        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        ProgressDialog nDialog = new ProgressDialog(getContext());
        nDialog.setMessage("Preparing Spotify...");
        nDialog.setTitle("Get Ready");
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(false);
        nDialog.show();

        // Ready to start walking, so try to connect to Spotify
        MainActivity.getSpotifyPlayerService().connect(new Callback<Void, Exception>() {

            // Success! Start playing music
            @Override
            public void onSuccess(Void result) {
                MainActivity.getSpotifyPlayerService().playMusic("7fPwZk4KFD2yfU7J5O1JVz");
                navController.navigate(R.id.action_fragmenttoplevel_to_fragmentonroute);
                nDialog.dismiss();
            }

            @Override
            public void onFailed(Exception result) {
                Log.e("SpotifyService", "RoutePlanningFragment: " + result.toString());
                nDialog.dismiss();

                new AlertDialog.Builder(getContext())
                        .setTitle("Spotify stopped")
                        .setMessage(result.toString())
                        .setPositiveButton("OK", null)
                        .setOnDismissListener(null)
                        .create()
                        .show();
            }
        });
    }
}
