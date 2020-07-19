package com.cs446.group7.bruno.ui.onroute;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.models.RouteModel;
import com.cs446.group7.bruno.viewmodels.OnRouteViewModel;
import com.cs446.group7.bruno.viewmodels.OnRouteViewModelDelegate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public class OnRouteFragment extends Fragment implements OnRouteViewModelDelegate {

    // MARK: - UI components

    private GoogleMap map;
    private TextView txtSongTitle;
    private TextView txtSongArtistInfo;
    private Button btnExitRoute;

    // MARK: - Private members

    private OnRouteViewModel viewModel;

    // MARK: - Lifecycle methods

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_on_route, container, false);
        txtSongTitle = view.findViewById(R.id.text_view_song_title);
        txtSongArtistInfo = view.findViewById(R.id.text_view_song_artist_info);
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

    // MARK: - User actions

    private void handleExitRouteClick(final View view) {
        viewModel.handleExitRoute();
    }

    public void onBackPress() {
        viewModel.handleExitRoute();
    }

    // MARK: - OnRouteViewModelDelegate methods

    public void setupUI() {
        btnExitRoute.setOnClickListener(this::handleExitRouteClick);
    }

    public void showAlertDialog(final String title,
                                final String message,
                                final String positiveButtonText,
                                final DialogInterface.OnClickListener positiveButtonClickListener,
                                final String negativeButtonText,
                                final DialogInterface.OnClickListener negativeButtonClickListener,
                                boolean isCancelable) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, positiveButtonClickListener)
                .setNegativeButton(negativeButtonText, negativeButtonClickListener)
                .setCancelable(isCancelable)
                .create()
                .show();
    }

    public void navigateToPreviousScreen() {
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigateUp();
    }
}
