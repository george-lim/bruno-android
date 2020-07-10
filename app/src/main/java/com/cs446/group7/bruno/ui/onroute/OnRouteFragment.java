package com.cs446.group7.bruno.ui.onroute;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.spotify.SpotifyServiceSubscriber;
import com.cs446.group7.bruno.utils.Callback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp;
import com.spotify.android.appremote.api.error.NotLoggedInException;
import com.spotify.android.appremote.api.error.UserNotAuthorizedException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class OnRouteFragment extends Fragment implements SpotifyServiceSubscriber {

    private OnMapReadyCallback callback = googleMap -> {
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

        MainActivity.getSpotifyPlayerService().connect(new Callback<Void, Exception>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(getContext(), "Spotify Connected!", Toast.LENGTH_SHORT).show();
                MainActivity.getSpotifyPlayerService().addSubscriber(OnRouteFragment.this);
                MainActivity.getSpotifyPlayerService().playMusic("7fPwZk4KFD2yfU7J5O1JVz");
            }

            @Override
            public void onFailed(Exception result) {
                Toast.makeText(getContext(), result.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onServiceReady() {

    }

    @Override
    public void onError(Exception exception) {
        Toast.makeText(getContext(), exception.toString(), Toast.LENGTH_SHORT).show();
//        if (exception instanceof NotLoggedInException || exception instanceof UserNotAuthorizedException) {
//            // Show login button and trigger the login flow from auth library when clicked
//        } else if (exception instanceof CouldNotFindSpotifyApp) {
//            // Show button to download Spotify
//        }
    }
}
