package com.cs446.group7.bruno.ui.onroute;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.spotify.SpotifyServiceError;
import com.cs446.group7.bruno.utils.Callback;
import com.cs446.group7.bruno.viewmodels.RouteResult;
import com.cs446.group7.bruno.viewmodels.RouteViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

public class OnRouteFragment extends Fragment {

    private GoogleMap map;
    private Marker userMarker;
    private RouteViewModel model;
    private TextView txtSongTitle;
    private TextView txtSongArtistInfo;
    private Button btnExitRoute;
    public final String TAG = this.getClass().getSimpleName();

    private OnMapReadyCallback callback = googleMap -> {
        map = googleMap;
        observeUserLocation();
        drawRoute();
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        model = new ViewModelProvider(requireActivity()).get(RouteViewModel.class);
        return inflater.inflate(R.layout.fragment_on_route, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.walking_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        txtSongTitle = view.findViewById(R.id.text_view_song_title);
        txtSongArtistInfo = view.findViewById(R.id.text_view_song_artist_info);
        btnExitRoute = view.findViewById(R.id.btn_exit_route);
        btnExitRoute.setOnClickListener(view1 -> onExitRouteClicked());

        model = new ViewModelProvider(requireActivity()).get(RouteViewModel.class);
        model.getSpotifyViewModel().getCurrentTrack().observe(getViewLifecycleOwner(), this::onTrackChanged);

        connectToSpotify();
    }

    private void connectToSpotify() {
        ProgressDialog nDialog = new ProgressDialog(getContext());
        nDialog.setMessage("Bruno is preparing your route and music");
        nDialog.setTitle("Hold on tight");
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(false);
        nDialog.show();

        MainActivity.getSpotifyService().connect(getContext(), new Callback<Void, SpotifyServiceError>() {
            @Override
            public void onSuccess(Void result) { // Connection successful
                nDialog.dismiss();
                MainActivity.getSpotifyService().setPlayerPlaylist("7fPwZk4KFD2yfU7J5O1JVz");
                MainActivity.getSpotifyService().play(new Callback<Void, Exception>() {
                    @Override
                    public void onSuccess(Void result) { // playback successful
                        MainActivity.getSpotifyService().addSubscriber(model.getSpotifyViewModel());
                    }

                    @Override
                    public void onFailed(Exception error) { // playback failed
                        Log.e(TAG, "onFailed play: " + error.toString());
                    }
                });
            }

            @Override
            public void onFailed(SpotifyServiceError error) { // received unrecoverable Spotify error
                nDialog.dismiss();
                Log.e(TAG, "onFailed connect: " + error.toString());

                if (getContext() == null) return;

                new AlertDialog.Builder(getContext())
                        .setTitle("Spotify Error")
                        .setMessage(error.getErrorMessage())
                        .setPositiveButton("OK", (dialogInterface, i) -> exitFragment())
                        .setCancelable(false)
                        .create()
                        .show();
            }
        });
    }

    private void onExitRouteClicked() {
        new AlertDialog.Builder(getContext())
                .setTitle("Are you sure?")
                .setMessage("Bruno doesn't like it when you quit in the middle, your current progress will be lost.")
                .setPositiveButton("YES", (dialogInterface, i) -> exitFragment())
                .setNegativeButton("NO", null)
                .create()
                .show();
    }

    private void exitFragment() {
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigateUp();
    }

    private void onTrackChanged(@NonNull final BrunoTrack track) {
        txtSongTitle.setText(track.name);
        txtSongArtistInfo.setText(track.album);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (MainActivity.getSpotifyService().isConnected()) {
            MainActivity.getSpotifyService().pause(new Callback<Void, Exception>() {
                @Override
                public void onSuccess(Void result) {
                    MainActivity.getSpotifyService().disconnect();
                }

                @Override
                public void onFailed(Exception result) {
                    MainActivity.getSpotifyService().disconnect();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.getLocationService().addSubscriber(model);
    }

    @Override
    public void onPause() {
        super.onPause();
        MainActivity.getLocationService().removeSubscriber(model);
    }

    private void observeUserLocation() {
        model.getCurrentLocation().observe(getViewLifecycleOwner(), location -> {

            // TODO: Permissions aren't checked properly, should fix in the flow later to ensure this doesn't happen
            if (location == null) return;
            if (userMarker == null) {
                userMarker = map.addMarker(new MarkerOptions().position(location));
                userMarker.setIcon(model.getAvatarMarker());
            } else {
                userMarker.setPosition(location);
            }

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(location)
                    .tilt(60)
                    .zoom(18)
                    .build();

            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        });
    }

    private void drawRoute() {
        RouteResult route = model.getRouteResult().getValue();

        // TODO: Permissions aren't checked properly, should fix in the flow later to ensure this doesn't happen
        if (route == null) return;
        map.addPolyline(new PolylineOptions().addAll(route.getRoute().getDecodedPath()));
    }

    public void onBackPress() {
        onExitRouteClicked();
    }
}
