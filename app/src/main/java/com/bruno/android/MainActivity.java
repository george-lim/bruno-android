package com.bruno.android;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    private HashMap<Integer, SpotifyRequest> activeSpotifyRequests;

    // ActivityResultLaunchers for hardware requests
    private ActivityResultLauncher<Intent> locationSettingsLauncher;
    private ActivityResultLauncher<Intent> internetSettingsLauncher;

    // Current hardware request being processed (one per launcher)
    private HardwareRequest currentLocationRequest;
    private HardwareRequest currentInternetRequest;

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
        activeSpotifyRequests = new HashMap<>();

        // Register activity result launchers for hardware requests
        locationSettingsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Hardware request completed when user returns from settings
                    if (currentLocationRequest != null) {
                        HardwareRequest request = currentLocationRequest;
                        currentLocationRequest = null;
                        request.getCallback().onSuccess(null);
                    }
                }
        );

        internetSettingsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Hardware request completed when user returns from settings
                    if (currentInternetRequest != null) {
                        HardwareRequest request = currentInternetRequest;
                        currentInternetRequest = null;
                        request.getCallback().onSuccess(null);
                    }
                }
        );

        locationService = new LocationServiceImpl(getApplicationContext());
        spotifyService = new SpotifyService(this);
        sensorService = new SensorService(getApplicationContext());
        persistenceService = new PersistenceService(getApplicationContext());
        preferencesStorage = new PreferencesStorage(getApplicationContext());
        volleyRequestQueue = Volley.newRequestQueue(getApplicationContext());

        // Set up back press handling using OnBackPressedDispatcher
        setupBackPressHandling();
    }

    /**
     * We want the app to exit only if the app is currently on the map tab.
     * The setup of nav_graph will exit the app if it display TopLevelFragment.
     * Hence, setting up the back press callback to achieve the desired result.
     */
    private void setupBackPressHandling() {
        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
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

                // If none of the fragments handled it, allow default back behavior
                setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
                setEnabled(true);
            }
        };

        getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
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

        // Handle Spotify requests (still using old API)
        SpotifyRequest spotifyRequest = activeSpotifyRequests.get(requestCode);

        if (spotifyRequest != null) {
            activeSpotifyRequests.remove(requestCode);
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

            if (response.getType() == AuthorizationResponse.Type.TOKEN) {
                spotifyRequest.getCallback().onSuccess(response.getAccessToken());
            } else {
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

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
        ActivityResultLauncher<Intent> launcher;

        // Determine which settings page to navigate and which launcher to use
        switch (request.getCapability()) {
            case LOCATION:
                intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                launcher = locationSettingsLauncher;
                currentLocationRequest = request;
                break;
            case INTERNET:
                intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                launcher = internetSettingsLauncher;
                currentInternetRequest = request;
                break;
            default:
                return;
        }

        // Launch activity for result - callback will be invoked when user returns from settings
        launcher.launch(intent);
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
