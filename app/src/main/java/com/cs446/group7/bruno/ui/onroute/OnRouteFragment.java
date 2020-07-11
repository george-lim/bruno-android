package com.cs446.group7.bruno.ui.onroute;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.viewmodels.RouteViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class OnRouteFragment extends Fragment {

    private GoogleMap map;
    private Marker userMarker;
    private RouteViewModel model;

    private OnMapReadyCallback callback = googleMap -> {
        map = googleMap;
        map.getUiSettings().setRotateGesturesEnabled(false);
        observeUserLocation();
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
            if (userMarker == null) {
                userMarker = map.addMarker(new MarkerOptions().position(location));
                userMarker.setIcon(model.getAvatarMarker());
            } else {
                userMarker.setPosition(location);
            }
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 19));
        });
    }
}