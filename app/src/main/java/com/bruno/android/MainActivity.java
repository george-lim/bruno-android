package com.bruno.android;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bruno.android.capability.CapabilityService;
import com.bruno.android.capability.hardware.HardwareRequest;
import com.bruno.android.capability.hardware.HardwareRequestDelegate;
import com.bruno.android.capability.permission.PermissionRequest;
import com.bruno.android.capability.permission.PermissionRequestDelegate;
import com.bruno.android.location.LocationService;
import com.bruno.android.location.LocationServiceImpl;
import com.bruno.android.persistence.PersistenceService;
import com.bruno.android.sensor.SensorService;
import com.bruno.android.spotify.SpotifyService;
import com.bruno.android.spotify.auth.SpotifyRequest;
import com.bruno.android.spotify.auth.SpotifyRequestDelegate;
import com.bruno.android.storage.PreferencesStorage;
import com.bruno.android.ui.onboarding.OnboardingFragment;
import com.bruno.android.ui.routenavigation.RouteNavigationFragment;
import com.bruno.android.ui.toplevel.TopLevelFragment;
import com.bruno.android.utils.NoFailCallback;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements PermissionRequestDelegate, HardwareRequestDelegate, SpotifyRequestDelegate {

    // MARK: - Services

    private static CapabilityService capabilityService;
    private static LocationService locationService;
    private static SpotifyService spotifyService;
    private static SensorService sensorService;
    private static PreferencesStorage preferencesStorage;
    private static PersistenceService persistenceService;

    private static RequestQueue volleyRequestQueue;

    // MARK: - PermissionRequestDelegate members

    // Counter for request codes
    private int currentRequestCode = 0;

    // Map request codes to active permission requests
    private HashMap<Integer, PermissionRequest> activePermissionRequests;
    private HashMap<Integer, HardwareRequest> activeHardwareRequests;
    private HashMap<Integer, SpotifyRequest> activeSpotifyRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        capabilityService = new CapabilityService(
                getApplicationContext(),
                this,
                this
        );
        activePermissionRequests = new HashMap<>();
        activeHardwareRequests = new HashMap<>();
        activeSpotifyRequests = new HashMap<>();

        locationService = new LocationServiceImpl(getApplicationContext());
        spotifyService = new SpotifyService(getApplicationContext(), this);
        sensorService = new SensorService(getApplicationContext());
        persistenceService = new PersistenceService(getApplicationContext());
        preferencesStorage = new PreferencesStorage(getApplicationContext());
        volleyRequestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    /**
     * We want the app to exit only if the app is currently on the map tab.
     * The setup of nav_graph will exit the app if it display TopLevelFragment.
     * Hence, overriding the behaviour when pressing the back button will achieve
     * the desired result.
     */
    @Override
    public void onBackPressed() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        int curContainerFragmentId = navController.getCurrentDestination().getId();

        // NOTE: If the back button is pressed in OnboardingFragment, let the fragment handle it unless it delegates back
        if (curContainerFragmentId == R.id.fragment_onboarding) {
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                    .getPrimaryNavigationFragment();
            OnboardingFragment onboardingFragment = (OnboardingFragment) navHostFragment
                    .getChildFragmentManager()
                    .getPrimaryNavigationFragment();
            if (onboardingFragment.onBackPress()) return;
        }

        // NOTE: If the back button is pressed in RouteNavigationFragment, let RouteNavigationFragment handle it
        if (curContainerFragmentId == R.id.fragment_route_navigation) {
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                    .getPrimaryNavigationFragment();
            RouteNavigationFragment routeNavigationFragment = (RouteNavigationFragment) navHostFragment
                    .getChildFragmentManager()
                    .getPrimaryNavigationFragment();
            routeNavigationFragment.onBackPress();
            return;
        }

        if (curContainerFragmentId == R.id.fragment_top_lvl) {
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                    .getPrimaryNavigationFragment();
            TopLevelFragment topLvlFragment = (TopLevelFragment) navHostFragment
                    .getChildFragmentManager()
                    .getPrimaryNavigationFragment();
            if (topLvlFragment.onBackPress()) return;
        }
        super.onBackPressed();
    }

    public static CapabilityService getCapabilityService() {
        return capabilityService;
    }

    public static LocationService getLocationService() {
        return locationService;
    }

    public static SpotifyService getSpotifyService() {
        return spotifyService;
    }

    public static SensorService getSensorService() {
        return sensorService;
    }

    public static PreferencesStorage getPreferencesStorage() {
        return preferencesStorage;
    }

    public static PersistenceService getPersistenceService() {
        return persistenceService;
    }

    public static RequestQueue getVolleyRequestQueue() {
        return volleyRequestQueue;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Find matching hardware request (if exists)
        HardwareRequest hardwareRequest = activeHardwareRequests.get(requestCode);
        SpotifyRequest spotifyRequest = activeSpotifyRequests.get(requestCode);

        // Complete initial hardware request if the activity result is from hardware request
        if (hardwareRequest != null) {
            activeHardwareRequests.remove(requestCode);
            hardwareRequest.getCallback().onSuccess(null);
        }

        if (spotifyRequest != null) {
            activeSpotifyRequests.remove(requestCode);
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

            if (response.getType() == AuthorizationResponse.Type.TOKEN) {
                spotifyRequest.getCallback().onSuccess(response.getAccessToken());
            }
            else {
                spotifyRequest.getCallback().onFailed(null);
            }
        }
    }

    // Creates and shows an alert dialog
    private void showAlertDialog(final String title,
                                 final String message,
                                 final NoFailCallback<Void> callback) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(getResources().getString(R.string.ok_button), null)
                .setOnDismissListener(dialogInterface -> {
                    if (callback != null) {
                        callback.onSuccess(null);
                    }
                })
                .create()
                .show();
    }

    // MARK: - PermissionRequestDelegate methods

    // Show a popup describing permission usage, then request permission
    // NOTE: Requesting a permission will trigger onPause() on MainActivity
    @Override
    public void handlePermissionRequest(@NonNull final PermissionRequest request) {
        showAlertDialog(
                request.getTitle(),
                request.getRequestMessage(),
                // Request permission after showing popup
                result -> {
                    // Store permission request into active permission requests
                    activePermissionRequests.put(currentRequestCode, request);

                    ActivityCompat.requestPermissions(MainActivity.this,
                            request.getPermissionNames(),
                            currentRequestCode);

                    currentRequestCode++;
                }
        );
    }

    // Observe permission request result to either complete request callback or show permission denied message
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Find matching permission request (if exists)
        PermissionRequest request = activePermissionRequests.get(requestCode);

        if (request != null) {
            activePermissionRequests.remove(requestCode);

            // Verify permissions from user
            for (int permissionStatus : grantResults) {
                // Show permission denied prompt if any permission is denied
                if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                    showAlertDialog(
                            request.getTitle(),
                            request.getRejectionMessage(),
                            request.getCallback()::onFailed
                    );

                    return;
                }
            }

            request.getCallback().onSuccess(null);
        }
    }

    // MARK: - HardwareRequestDelegate methods

    // Show a popup describing hardware requirement
    @Override
    public void showHardwareRequestPrompt(@NonNull final HardwareRequest request) {
        showAlertDialog(
                request.getTitle(),
                request.getRequestMessage(),
                request.getCallback()
        );
    }

    // Navigate user to settings
    // NOTE: Navigating to settings will trigger onPause() on MainActivity
    @Override
    public void handleHardwareRequest(@NonNull final HardwareRequest request) {
        Intent intent;

        // Determine which settings page to navigate
        switch (request.getCapability()) {
            case LOCATION:
                intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                break;
            case INTERNET:
                intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                break;
            default:
                return;
        }

        // Store hardware request into active hardware requests
        activeHardwareRequests.put(currentRequestCode, request);

        startActivityForResult(intent, currentRequestCode);

        currentRequestCode++;
    }

    // Show a popup describing hardware request rejection
    @Override
    public void handleHardwareRejection(@NonNull final HardwareRequest request) {
        showAlertDialog(
                request.getTitle(),
                request.getRejectionMessage(),
                request.getCallback()
        );
    }

    @Override
    public void handleSpotifyRequest(@NonNull final SpotifyRequest request) {

        activeSpotifyRequests.put(currentRequestCode, request);
        AuthorizationClient.openLoginActivity(
                this,
                currentRequestCode,
                request.getAuthorizationRequest()
        );
        currentRequestCode++;

    }
}
