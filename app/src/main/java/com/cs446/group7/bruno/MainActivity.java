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
import com.cs446.group7.bruno.utils.CompletionHandler;
import com.cs446.group7.bruno.utils.NoFailCompletionHandler;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements PermissionRequestDelegate {
    private CapabilityService capabilityService;
    private int currentRequestCode = 0;
    private HashMap<Integer, PermissionRequest> activePermissionRequests;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        capabilityService = new CapabilityService(this, this);
        activePermissionRequests = new HashMap<>();

        capabilityService.request(Capability.LOCATION, new CompletionHandler<Void, Void>() {
            @Override
            public void onSuccess(Void result) {
                presentAlertDialog("Location Status", "Enabled", null);
            }

            @Override
            public void onFailed(Void result) {
                presentAlertDialog("Location Status", "Disabled", null);
            }
        });
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

    public CapabilityService getCapabilityService() {
        return capabilityService;
    }

    private void presentAlertDialog(String title,
                                    String message,
                                    NoFailCompletionHandler<Void> completion) {
        DialogInterface.OnDismissListener onDismiss = new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (completion != null) {
                    completion.onSuccess(null);
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

    // MARK: - PermissionRequestDelegate methods

    @Override
    public void handlePermissionRequest(PermissionRequest request) {
        NoFailCompletionHandler<Void> completion = new NoFailCompletionHandler<Void>() {
            @Override
            public void onSuccess(Void result) {
                activePermissionRequests.put(currentRequestCode, request);
                ActivityCompat.requestPermissions(MainActivity.this,
                        request.getPermissionNames(),
                        currentRequestCode);
                currentRequestCode++;
            }
        };

        presentAlertDialog(
                request.getTitle(),
                request.getPermissionRequestMessage(),
                completion
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull final String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionRequest request = activePermissionRequests.get(requestCode);
        activePermissionRequests.remove(requestCode);

        for (int permissionStatus : grantResults) {
            if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                NoFailCompletionHandler<Void> completion = new NoFailCompletionHandler<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        request.getCompletion().onFailed(null);
                    }
                };

                presentAlertDialog(
                        request.getTitle(),
                        request.getPermissionDeniedMessage(),
                        completion
                );

                return;
            }
        }

        request.getCompletion().onSuccess(null);
    }
}
