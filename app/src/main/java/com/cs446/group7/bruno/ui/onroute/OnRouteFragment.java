package com.cs446.group7.bruno.ui.onroute;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.spotify.SpotifyServiceError;
import com.cs446.group7.bruno.utils.Callback;
import com.cs446.group7.bruno.viewmodels.RouteViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

public class OnRouteFragment extends Fragment {

    private RouteViewModel model;
    private TextView txtSongTitle;
    private TextView txtSongArtistInfo;

    private OnMapReadyCallback callback = googleMap -> {

        // Need to pass the gMaps from the previous fragment somehow
        LatLng sydney = new LatLng(-34, 151);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(sydney)
                .tilt(60)
                .zoom(20)
                .build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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

        MainActivity.getSpotifyPlayerService().connect(getContext(), new Callback<Void, SpotifyServiceError>() {
            @Override
            public void onSuccess(Void result) { // Connection successful
                nDialog.dismiss();
                MainActivity.getSpotifyPlayerService().setPlaylist("7fPwZk4KFD2yfU7J5O1JVz");
                MainActivity.getSpotifyPlayerService().play(new Callback<Void, Exception>() {
                    @Override
                    public void onSuccess(Void result) { // playback successful
                        MainActivity.getSpotifyPlayerService().addSubscriber(model.getSpotifyViewModel());
                    }

                    @Override
                    public void onFailed(Exception error) { // playback failed
                        Log.e("SpotifyService", "onFailed play: " + error.toString());
                    }
                });
            }

            @Override
            public void onFailed(SpotifyServiceError error) { // received unrecoverable Spotify error
                nDialog.dismiss();
                Log.e("SpotifyService", "onFailed connect: " + error.toString());

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
        Toast.makeText(getContext(), "onDestroy", Toast.LENGTH_SHORT).show();
        if (MainActivity.getSpotifyPlayerService().isConnected()) {
            MainActivity.getSpotifyPlayerService().disconnect();
        }
    }
}
