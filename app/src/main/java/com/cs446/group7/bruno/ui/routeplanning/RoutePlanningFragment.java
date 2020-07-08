package com.cs446.group7.bruno.ui.routeplanning;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.capability.Capability;
import com.cs446.group7.bruno.location.LocationServiceException;
import com.cs446.group7.bruno.location.LocationServiceSubscriber;
import com.cs446.group7.bruno.utils.Callback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class RoutePlanningFragment extends Fragment {

    private GoogleMap googleMap;
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
        LatLng sydney = new LatLng(-34, 151);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        requestLocationUpdates();
    };

    /**
     * Receives location updates periodically.
     */
    private LocationServiceSubscriber onLocationUpdatedCallback = new LocationServiceSubscriber() {
        @Override
        public void onLocationUpdateSuccess(@NonNull Location location) {
            Log.i(TAG, location.toString());
            final LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(newLocation).title("Curr location"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
            Toast.makeText(getContext(), String.format("Location: (%s, %s)", location.getLatitude(), location.getLongitude()), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLocationUpdateFailure(LocationServiceException error) {
            Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, error.toString());
        }
    };

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

    private void requestLocationUpdates() {
        MainActivity.getCapabilityService().request(Capability.LOCATION, new Callback<Void, Void>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(Void result) {
                googleMap.setMyLocationEnabled(true); // Can remove eventually when we no longer use Google map's location
                MainActivity.getLocationService().startLocationUpdates();
                Toast.makeText(getContext(), "Location permission granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(Void result) {
                Toast.makeText(getContext(), "Location permission not granted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleStartWalkingClick(final View view) {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_fragmenttoplevel_to_fragmentonroute);
    }
}
