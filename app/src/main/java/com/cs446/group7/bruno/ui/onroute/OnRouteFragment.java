package com.cs446.group7.bruno.ui.onroute;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.models.RouteModel;
import com.cs446.group7.bruno.models.TrackSegment;
import com.cs446.group7.bruno.utils.BitmapUtils;
import com.cs446.group7.bruno.viewmodels.OnRouteViewModel;
import com.cs446.group7.bruno.viewmodels.OnRouteViewModelDelegate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class OnRouteFragment extends Fragment implements OnRouteViewModelDelegate {

    // MARK: - UI components

    private GoogleMap map;
    private CardView trackInfoCardView;
    private CardView routeInfoCardView;
    private TextView txtSongTitle;
    private TextView txtSongArtistInfo;
    private ImageView progressIndicator;
    private TextView txtUserPlaylistDistance;
    private TextView txtCheckpointDistance;
    private ImageButton btnExitRoute;

    // MARK: - Private members

    private OnRouteViewModel viewModel;

    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;
    private Marker userMarker;
    private Marker checkpointMarker;
    private Circle checkpointCircle;
    private BitmapDescriptor userMarkerIcon;

    private boolean hasDrawnRouteOnce = false;

    // MARK: - Lifecycle methods

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_on_route, container, false);
        trackInfoCardView = view.findViewById(R.id.card_view_track_info);
        routeInfoCardView = view.findViewById(R.id.card_view_route_info);
        txtSongTitle = view.findViewById(R.id.text_view_song_title);
        txtSongArtistInfo = view.findViewById(R.id.text_view_song_artist_info);
        progressIndicator = view.findViewById(R.id.image_view_user_playlist_distance);
        txtUserPlaylistDistance = view.findViewById(R.id.text_view_user_playlist_distance);
        txtCheckpointDistance = view.findViewById(R.id.text_view_checkpoint_distance);
        btnExitRoute = view.findViewById(R.id.btn_exit_route);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.walking_map);

        mapFragment.getMapAsync(googleMap -> {
            map = googleMap;

            RouteModel model = new ViewModelProvider(requireActivity()).get(RouteModel.class);
            viewModel = new OnRouteViewModel(getActivity().getApplicationContext(), model, this);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }

    // MARK: - User actions

    private void handleExitRouteClick(final View view) {
        viewModel.handleExitRoute();
    }

    public void onBackPress() {
        viewModel.handleExitRoute();
    }

    // MARK: - OnRouteViewModelDelegate methods

    private BitmapDescriptor getUserMarkerIcon(int avatarResourceId) {
        Drawable avatarDrawable = getResources().getDrawable(avatarResourceId, null);
        return BitmapDescriptorFactory.fromBitmap(BitmapUtils.getBitmapFromVectorDrawable(avatarDrawable));
    }

    @Override
    public void setupUI(int userAvatarDrawableResourceId) {
        btnExitRoute.setOnClickListener(this::handleExitRouteClick);
        userMarkerIcon = getUserMarkerIcon(userAvatarDrawableResourceId);

        map.getUiSettings().setCompassEnabled(false);
    }

    @Override
    public void updateCurrentSongUI(final String name, final String artists) {
        trackInfoCardView.setVisibility(View.VISIBLE);
        txtSongTitle.setText(name);
        txtSongArtistInfo.setText(artists);
    }

    @Override
    public void clearMap() {
        map.clear();
        userMarker = null;
        checkpointMarker = null;
    }

    @Override
    public void drawRoute(@NonNull final List<TrackSegment> trackSegments) {
        final float routeWidth = 14;

        for (TrackSegment trackSegment : trackSegments) {
            map.addPolyline(new PolylineOptions()
                    .addAll(trackSegment.getLatLngs())
                    .color(trackSegment.getRouteColour())
                    .width(routeWidth));
        }
    }

    @Override
    public void updateCheckpointMarker(final LatLng latLng, final double radius) {
        if (checkpointMarker == null) {
            checkpointMarker = map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            );

            checkpointCircle = map.addCircle(new CircleOptions().center(latLng).radius(radius).strokeColor(Color.RED));

        } else if (!checkpointMarker.getPosition().equals(latLng)) { // only draw if it's different
            checkpointMarker.setPosition(latLng);
            checkpointCircle.setCenter(latLng);
        }
    }

    @Override
    public void animateCamera(final LatLng latLng, float bearing, int cameraTilt, int cameraZoom) {
        if (userMarker == null) {
            userMarker = map.addMarker(new MarkerOptions().position(latLng));
            userMarker.setIcon(userMarkerIcon);
        }
        else {
            userMarker.setPosition(latLng);
        }

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .bearing(bearing)
                .tilt(cameraTilt)
                .zoom(cameraZoom)
                .build();

        if (hasDrawnRouteOnce) {
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        else {
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            hasDrawnRouteOnce = true;
        }
    }

    @Override
    public void showProgressDialog(final String title,
                                   final String message,
                                   boolean isIndeterminate,
                                   boolean isCancelable) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(isIndeterminate);
        progressDialog.setCancelable(isCancelable);
        progressDialog.show();
    }

    @Override
    public void dismissProgressDialog() {
        if (progressDialog == null) {
            return;
        }

        progressDialog.dismiss();
    }

    @Override
    public void showAlertDialog(final String title,
                                final String message,
                                final String positiveButtonText,
                                final DialogInterface.OnClickListener positiveButtonClickListener,
                                boolean isCancelable) {

        // We don't want multiple, overlapping dialogues
        // it's okay if the dialogue is already dismissed and we re-dismiss it, no need to set to null once dismissed
        if (alertDialog != null) {
            alertDialog.dismiss();
        }

        alertDialog = new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, positiveButtonClickListener)
                .setCancelable(isCancelable)
                .create();

        alertDialog.show();
    }

    @Override
    public void showAlertDialog(final String title,
                                final String message,
                                final String positiveButtonText,
                                final DialogInterface.OnClickListener positiveButtonClickListener,
                                final String negativeButtonText,
                                final DialogInterface.OnClickListener negativeButtonClickListener,
                                boolean isCancelable) {

        if (alertDialog != null) {
            alertDialog.dismiss();
        }

        alertDialog = new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, positiveButtonClickListener)
                .setNegativeButton(negativeButtonText, negativeButtonClickListener)
                .setCancelable(isCancelable)
                .create();

        alertDialog.show();
    }

    @Override
    public void navigateToPreviousScreen() {
        if (getActivity() != null) {
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigateUp();
        }
        else {
            Log.w(getClass().getSimpleName(), "Detected race condition where navigateToNextScreen was called after already navigating to next screen.");
        }
    }

    @Override
    public void updateDistanceBetweenUserAndPlaylist(final String progressText,
                                                     final Drawable progressIcon,
                                                     int colour) {
        txtUserPlaylistDistance.setText(progressText);
        progressIndicator.setImageDrawable(progressIcon);
        progressIndicator.setColorFilter(colour);
        progressIndicator.setVisibility(View.VISIBLE);
        txtUserPlaylistDistance.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateDistanceToCheckpoint(final String distanceText) {
        txtCheckpointDistance.setText(distanceText);
        txtCheckpointDistance.setVisibility(View.VISIBLE);
    }

    @Override
    public void showRouteInfoCard() {
        routeInfoCardView.setVisibility(View.VISIBLE);
    }
}
