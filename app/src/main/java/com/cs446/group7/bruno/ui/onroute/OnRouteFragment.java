package com.cs446.group7.bruno.ui.onroute;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.viewmodels.RouteViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
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

    private void observeUserLocation() {
        model.getCurrentLocation().observe(getViewLifecycleOwner(), location -> {
            final LatLng locationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (userMarker == null) {
                userMarker = map.addMarker(new MarkerOptions().position(locationLatLng));
                userMarker.setIcon(model.getAvatarMarker());
            } else {
                userMarker.setPosition(locationLatLng);
            }
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 19));
        });
    }
}
