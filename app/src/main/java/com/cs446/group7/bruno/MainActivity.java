package com.cs446.group7.bruno;

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

import com.cs446.group7.bruno.capability.CapabilityService;
import com.cs446.group7.bruno.capability.hardware.HardwareRequest;
import com.cs446.group7.bruno.capability.hardware.HardwareRequestDelegate;
import com.cs446.group7.bruno.capability.permission.PermissionRequest;
import com.cs446.group7.bruno.capability.permission.PermissionRequestDelegate;
import com.cs446.group7.bruno.location.LocationService;
import com.cs446.group7.bruno.spotify.SpotifyService;
import com.cs446.group7.bruno.ui.onroute.OnRouteFragment;
import com.cs446.group7.bruno.ui.toplevel.TopLevelFragment;
import com.cs446.group7.bruno.utils.NoFailCallback;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements PermissionRequestDelegate, HardwareRequestDelegate {

    // MARK: - Services

    private static CapabilityService capabilityService;
    private static LocationService locationService;
    private static SpotifyService spotifyService;

    // MARK: - PermissionRequestDelegate members

    // Counter for permission request codes
    private int currentRequestCode = 0;
    // Map request codes to active permission requests
    private HashMap<Integer, PermissionRequest> activePermissionRequests;
    private HashMap<Integer, HardwareRequest> activeHardwareRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        capabilityService = new CapabilityService(getApplicationContext(), this, this);
        activePermissionRequests = new HashMap<>();
        activeHardwareRequests = new HashMap<>();

        locationService = new LocationService(getApplicationContext());
        spotifyService = new SpotifyService(getApplicationContext());
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

        // NOTE: If the back button is pressed in OnRouteFragment, let OnRouteFragment handle it
        if (curContainerFragmentId == R.id.fragment_on_route) {
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().getPrimaryNavigationFragment();
            OnRouteFragment onRouteFragment = (OnRouteFragment) navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();
            onRouteFragment.onBackPress();
            return;
        }

        if (curContainerFragmentId == R.id.fragment_top_lvl) {
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().getPrimaryNavigationFragment();
            TopLevelFragment topLvlFragment = (TopLevelFragment) navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Find matching hardware request (if exists)
        HardwareRequest request = activeHardwareRequests.get(requestCode);

        // Complete initial hardware request if the activity result is from hardware request
        if (request != null) {
            activePermissionRequests.remove(requestCode);
            request.getCallback().onSuccess(null);
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
}
