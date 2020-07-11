package com.cs446.group7.bruno.ui.onroute;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.spotify.SpotifyServiceError;
import com.cs446.group7.bruno.spotify.SpotifyServiceSubscriber;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class OnRouteFragment extends Fragment implements SpotifyServiceSubscriber {

    private OnMapReadyCallback callback = googleMap -> {

        // Need to pass the gMaps from the previous fragment somehow
        LatLng sydney = new LatLng(-34, 151);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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
        MainActivity.getSpotifyPlayerService().addSubscriber(this);
    }

    @Override
    public void onTrackChanged(@NonNull final BrunoTrack track) {
        // new song starts playing

        if (getContext() == null) return;
        Toast.makeText(getContext(), String.format("Now playing: %s", track.name), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(@NonNull final SpotifyServiceError error) {
        Log.e("SpotifyService", String.format("OnRouteFragment: %s", error.getErrorMessage()));

        if (getActivity() == null) return;
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigateUp();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity.getSpotifyPlayerService().pauseMusic();
        MainActivity.getSpotifyPlayerService().disconnect();
    }
}
