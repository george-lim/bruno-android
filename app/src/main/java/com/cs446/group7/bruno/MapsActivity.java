package com.cs446.group7.bruno;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cs446.group7.bruno.routing.OnRouteReadyCallback;
import com.cs446.group7.bruno.routing.Route;
import com.cs446.group7.bruno.routing.RouteGenerator;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.cs446.group7.bruno.sensor.PedometerSubscriber;
import com.cs446.group7.bruno.sensor.SensorService;
import com.cs446.group7.bruno.settings.SettingsService;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, OnRouteReadyCallback, PedometerSubscriber {


    private GoogleMap mMap;
    private LatLng currLocation;
    private RouteGenerator routeGeneratorReal;
    private RouteGenerator routeGeneratorMock;
    private FusedLocationProviderClient fusedLocationClient;


    private static boolean isMock = BuildConfig.DEBUG;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String[] locationPermissions = { Manifest.permission.ACCESS_FINE_LOCATION };
    private static String apiKey;
    private final String TAG = this.getClass().getSimpleName();

    private Button generateRouteButton;
    private Button toggleMockButton;
    private EditText durationInput;

    private SensorService sensorService;
    private int steps = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        durationInput = findViewById(R.id.et_duration);

        generateRouteButton = findViewById(R.id.btn_generate_route);
        generateRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currLocation == null) return;

                int duration;
                try {
                    duration = Integer.parseInt(durationInput.getText().toString());
                } catch (NumberFormatException e) {
                    duration = SettingsService.DEFAULT_EXERCISE_DURATION;
                }

                double totalDistance = duration * SettingsService.PREFERRED_WALKING_SPEED;

                // Note: Spamming the button in non-mock mode will cause crashes due to
                //       the app running out of memory handling multiple concurrent requests
                (isMock ? routeGeneratorMock : routeGeneratorReal).generateRoute(
                        MapsActivity.this,
                        currLocation,
                        totalDistance,
                        Math.random() * 2 * Math.PI
                );
            }
        });

        toggleMockButton = findViewById(R.id.btn_toggle_mock);
        toggleMockButton.setText("Mock: " + (isMock ? "ON" : "OFF"));
        toggleMockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMock = !isMock;
                toggleMockButton.setText("Mock: " + (isMock ? "ON" : "OFF"));
            }
        });

        mapFragment.getMapAsync(this);

        // Enable location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ApplicationInfo app;
        try {
            app = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = app.metaData;

            apiKey = bundle.getString("com.google.android.geo.API_KEY");

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        routeGeneratorReal = RouteGenerator.create(this, apiKey, false);
        routeGeneratorMock = RouteGenerator.create(this, apiKey, true);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // request location permissions
        if (hasLocationPermission()) {
            enableLocation();
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location == null) return;

                            currLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        }
                    });


        } else {
            requestLocationPermission();
        }

        sensorService = new SensorService(getApplicationContext());
        sensorService.addPedometerSubscriber(this);
    }

    @Override
    public void onRouteReady(Route route) {
        if (route == null) {
            Log.e(TAG, "Failed to generate route");
        }

        final List<LatLng> decodedPath = route.getDecodedPath();

        List<LatLng> markers = new ArrayList<>();
        int skipInterval = decodedPath.size() / 5;

        // sample a few points one the route to act as markers
        for (int i = 0; i < decodedPath.size(); i += skipInterval) {
            markers.add(decodedPath.get(i));
        }

        mMap.clear();


        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (final LatLng p : markers) {
            builder.include(p);
        }

        mMap.addMarker(new MarkerOptions()
                    .position(markers.get(0)))
                    .setIcon(BitmapDescriptorFactory.defaultMarker(isMock ? BitmapDescriptorFactory.HUE_RED : BitmapDescriptorFactory.HUE_BLUE));

        int padding = 200;
        mMap.moveCamera(CameraUpdateFactory
                .newLatLngBounds(builder.build(), padding)
        );


        mMap.addPolyline(new PolylineOptions().addAll(route.getDecodedPath()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocation();
            }
        }
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("MissingPermission")
    private void enableLocation() {
        mMap.setMyLocationEnabled(true);
    }

    private void requestLocationPermission() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Needed")
                .setMessage("Bruno needs to know your location to work properly")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(MapsActivity.this, locationPermissions,
                                LOCATION_PERMISSION_REQUEST_CODE);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create()
                .show();
    }

    @Override
    public void didStep(long timestamp) {
        steps++;
        Log.i(this.getClass().getSimpleName(), "Total steps: " + steps);
    }
}
