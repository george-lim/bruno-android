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

import com.cs446.group7.bruno.capability.Capability;
import com.cs446.group7.bruno.capability.CapabilityService;
import com.cs446.group7.bruno.capability.PermissionRequest;
import com.cs446.group7.bruno.capability.PermissionRequestDelegate;
import com.cs446.group7.bruno.ui.toplevel.TopLevelFragment;
import com.cs446.group7.bruno.utils.Callback;
import com.cs446.group7.bruno.utils.NoFailCallback;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements PermissionRequestDelegate {

    // MARK: - Singletons

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

        capabilityService = new CapabilityService(this, this);
        activePermissionRequests = new HashMap<>();
    }

    /**
     * We want the app to exit only if the app is currently on the map tab.
     * The setup of nav_graph will exit the app if it display TopLevelFragment.
     * Hence, overriding the back pressed behaviour to achieve desire result.
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

    // MARK: - PermissionRequestDelegate methods

    // Creates and shows an alert dialog
    private void showAlertDialog(final String title,
                                 final String message,
                                 final NoFailCallback<Void> callback) {
        DialogInterface.OnDismissListener onDismiss = new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (callback != null) {
                    callback.onSuccess(null);
                }
            }
        };

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setOnDismissListener(onDismiss)
                .create()
                .show();
    }

    // Show a popup describing permission usage, then request permission
    @Override
    public void handlePermissionRequest(final PermissionRequest request) {
        // Request permission after showing popup
        NoFailCallback<Void> callback = new NoFailCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                activePermissionRequests.put(currentRequestCode, request);

                ActivityCompat.requestPermissions(MainActivity.this,
                        request.getPermissionNames(),
                        currentRequestCode);

                currentRequestCode++;
            }
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
                                           @NonNull final String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionRequest request = activePermissionRequests.get(requestCode);
        activePermissionRequests.remove(requestCode);

        for (int permissionStatus : grantResults) {
            if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                // After showing permission denied message, complete initial request callback with failure
                NoFailCallback<Void> callback = new NoFailCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        request.getCallback().onFailed(null);
                    }
                };

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
}
