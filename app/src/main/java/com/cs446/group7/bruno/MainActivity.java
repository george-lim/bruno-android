package com.cs446.group7.bruno;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.cs446.group7.bruno.ui.toplevel.TopLevelFragment;
import com.cs446.group7.bruno.utils.NoFailCallback;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements PermissionRequestDelegate, HardwareRequestDelegate {

    // MARK: - Services

    private static CapabilityService capabilityService;

    // MARK: - PermissionRequestDelegate members

    // Counter for permission request codes
    private int currentRequestCode = 0;
    // Map request codes to active permission requests
    private HashMap<Integer, PermissionRequest> activePermissionRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        capabilityService = new CapabilityService(getApplicationContext(), this, this);
        activePermissionRequests = new HashMap<>();
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

    // Creates and shows an alert dialog
    private void showAlertDialog(final String title,
                                 final String message,
                                 final NoFailCallback<Void> callback) {
        DialogInterface.OnDismissListener onDismiss = dialogInterface -> {
            if (callback != null) {
                callback.onSuccess(null);
            }
        };

        String okButtonText = getResources().getString(R.string.ok_button);

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(okButtonText, null)
                .setOnDismissListener(onDismiss)
                .create()
                .show();
    }

    // MARK: - PermissionRequestDelegate methods

    // Show a popup describing permission usage, then request permission
    // NOTE: Requesting a permission will trigger onPause() on MainActivity
    @Override
    public void handlePermissionRequest(@NonNull final PermissionRequest request) {
        // Request permission after showing popup
        NoFailCallback<Void> callback = result -> {
            // Store permission request into active permission requests
            activePermissionRequests.put(currentRequestCode, request);

            ActivityCompat.requestPermissions(MainActivity.this,
                    request.getPermissionNames(),
                    currentRequestCode);

            currentRequestCode++;
        };

        showAlertDialog(
                request.getTitle(),
                request.getPermissionRequestMessage(),
                callback
        );
    }

    // Observe permission request result to either complete request callback or show permission denied message
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Find matching permission request and remove it from active permission requests
        PermissionRequest request = activePermissionRequests.get(requestCode);
        activePermissionRequests.remove(requestCode);

        // Verify permissions from user
        for (int permissionStatus : grantResults) {
            // Show permission denied prompt if any permission is denied
            if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                // Complete initial request callback with failure
                NoFailCallback<Void> callback = result -> request.getCallback().onFailed(null);

                showAlertDialog(
                        request.getTitle(),
                        request.getPermissionDeniedMessage(),
                        callback
                );

                return;
            }
        }

        request.getCallback().onSuccess(null);
    }

    // MARK: - HardwareRequestDelegate methods

    // Show a popup prompting user to enable hardware capability
    @Override
    public void handleHardwareRequest(@NonNull final HardwareRequest request) {
        showAlertDialog(
                request.getTitle(),
                request.getMessage(),
                request.getCallback()
        );
    }
}
