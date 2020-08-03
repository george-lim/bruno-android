package com.cs446.group7.bruno.viewmodels;

import android.content.DialogInterface;

import com.cs446.group7.bruno.music.playlist.PlaylistMetadata;

import java.util.List;

public interface FallbackPlaylistViewModelDelegate {
    void showPlaylistSelectionView(List<PlaylistMetadata> playlists, int selectedIndex);
    void showNoPlaylistsView();
    void showSpotifyErrorView(final String errorText);
    void quitApp();
    void showProgressDialog();
    void dismissProgressDialog();
    void showAlertDialog(final String title,
                         final String message,
                         final String positiveButtonText,
                         final DialogInterface.OnClickListener positiveButtonClickListener,
                         boolean isCancelable);
}
