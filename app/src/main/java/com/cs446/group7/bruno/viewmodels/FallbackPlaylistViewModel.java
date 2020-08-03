package com.cs446.group7.bruno.viewmodels;

import android.content.Context;
import android.util.Log;

import com.cs446.group7.bruno.BuildConfig;
import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.capability.Capability;
import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoPlaylistImpl;
import com.cs446.group7.bruno.music.playlist.PlaylistMetadata;
import com.cs446.group7.bruno.spotify.SpotifyService;
import com.cs446.group7.bruno.spotify.playlist.MockSpotifyPlaylistAPIImpl;
import com.cs446.group7.bruno.spotify.playlist.SpotifyPlaylistAPI;
import com.cs446.group7.bruno.storage.FileStorage;
import com.cs446.group7.bruno.storage.PreferencesStorage;
import com.cs446.group7.bruno.ui.shared.FallbackPlaylistAction;
import com.cs446.group7.bruno.utils.Callback;
import com.cs446.group7.bruno.utils.ClosureQueue;

import java.util.List;

public class FallbackPlaylistViewModel {

    private Context context;
    private FallbackPlaylistAction wrapperDelegate;
    private FallbackPlaylistViewModelDelegate delegate;
    private String token;
    private boolean ongoingRequest;
    private PlaylistMetadata fallbackPlaylist;
    public final String TAG = this.getClass().getSimpleName();

    public FallbackPlaylistViewModel(final Context context,
                                     final FallbackPlaylistAction wrapperDelegate,
                                     final FallbackPlaylistViewModelDelegate delegate) {
        this.context = context;
        this.wrapperDelegate = wrapperDelegate;
        this.delegate = delegate;
    }

    public void setCurrentPlaylistAsFallBack(PlaylistMetadata playlist) {
        fallbackPlaylist = playlist;
    }

    public void getUserPlaylistLibrary() {
        // Check if there is an ongoing process
        // Since this is call by onResume, when auth view return, we don't want duplicate request.
        if (ongoingRequest) {
            Log.d(TAG, "ongoing request, early return");
            return;
        }
        ongoingRequest = true;

        // Make sure have necessary capabilities
        boolean isSpotifyInstalled = SpotifyService.isSpotifyInstalled(context);
        if (!isSpotifyInstalled) {
            showSpotifyError(context.getResources().getString(R.string.onboarding_missing_spotify_installation_description));
            return;
        }
        boolean hasInternet = MainActivity.getCapabilityService().isCapabilityEnabled(Capability.INTERNET);
        if (!hasInternet) {
            showSpotifyError(context.getResources().getString(R.string.onboarding_missing_internet_text));
            return;
        }

        // Get user to authorize if not done, otherwise fetch user playlist library for fallback playlist
        SpotifyService spotifyService = MainActivity.getSpotifyService();
        ClosureQueue<Void, Void> queue = new ClosureQueue<>();
        if (token == null) {
            queue.add((result, callback) -> {
                spotifyService.getAuthService().requestUserAuth(new Callback<String, Void>() {
                    @Override
                    public void onSuccess(String resultToken) {
                        token = resultToken;
                        callback.onSuccess(null);
                    }

                    @Override
                    public void onFailed(Void result) {
                        Log.d(TAG, "Get user auth fail");
                        showSpotifyError(context.getResources().getString(R.string.onboarding_fallback_playlist_fail));
                        callback.onFailed(null);
                    }
                });
            });
        }
        queue.add((result, callback) -> {

            spotifyService.getAuthService().checkIfUserIsPremium(token, new Callback<Boolean, Exception>() {
                @Override
                public void onSuccess(Boolean isPremium) {
                    if (!isPremium) {
                        showNotPremiumUser();
                        callback.onFailed(null);
                    } else {
                        callback.onSuccess(null);
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    Log.d(TAG, "Check if user is premium fail");
                    showSpotifyError(context.getResources().getString(R.string.onboarding_fallback_playlist_fail));
                    callback.onFailed(null);
                }
            });
        });
        queue.add((result, callback) -> {
            getSpotifyPlaylistAPI().getUserPlaylistLibrary(token, new Callback<List<PlaylistMetadata>, Exception>() {
                @Override
                public void onSuccess(List<PlaylistMetadata> playlistMetadata) {
                    if (playlistMetadata.size() == 0) {
                        showNoPlaylist();
                    } else {
                        showSelectPlaylist(playlistMetadata);
                    }
                    callback.onSuccess(null);
                }

                @Override
                public void onFailed(Exception result) {
                    Log.d(TAG, "Get user playlists fail");
                    showSpotifyError(context.getResources().getString(R.string.onboarding_fallback_playlist_fail));
                    callback.onFailed(null);
                }
            });
        });
        queue.run(new Callback<Void, Void>() {
            @Override
            public void onSuccess(Void result) {
                ongoingRequest = false;
            }

            @Override
            public void onFailed(Void result) {
                ongoingRequest = false;
            }
        });
    }


    private SpotifyPlaylistAPI getSpotifyPlaylistAPI() {
        return BuildConfig.DEBUG
                ? new MockSpotifyPlaylistAPIImpl()
                : MainActivity.getSpotifyService().getPlaylistService();
    }

    private void showSelectPlaylist(List<PlaylistMetadata> playlists) {
        int index = 0;
        String savedFallbackPlaylistId =
                MainActivity.getPreferencesStorage().getString(PreferencesStorage.FALLBACK_PLAYLIST_ID, null);
        if (savedFallbackPlaylistId != null) {
            for (int i = 0; i < playlists.size(); ++i) {
                if (playlists.get(i).getId().equals(savedFallbackPlaylistId)) {
                    index = i;
                    break;
                }
            }
        }
        wrapperDelegate.updatePrimaryAction(
                FallbackPlaylistAction.ActionType.SELECT_PLAYLIST,
                view -> saveFallbackPlaylist());
        delegate.showPlaylistSelectionView(playlists, index);
    }

    private void showNoPlaylist() {
        wrapperDelegate.updatePrimaryAction(
                FallbackPlaylistAction.ActionType.NO_PLAYLIST,
                view -> wrapperDelegate.onNoPlaylistPressed());
        delegate.showNoPlaylistsView();
    }

    private void showNotPremiumUser() {
        wrapperDelegate.updatePrimaryAction(
                FallbackPlaylistAction.ActionType.QUIT,
                view -> delegate.quitApp());
        delegate.showSpotifyErrorView(
                context.getResources().getString(R.string.onboarding_fallback_playlist_not_premium_user));
    }

    private void showSpotifyError(final String errorText) {
        wrapperDelegate.updatePrimaryAction(
                FallbackPlaylistAction.ActionType.QUIT,
                view -> delegate.quitApp());
        delegate.showSpotifyErrorView(errorText);
    }

    private void saveFallbackPlaylist() {
        if (fallbackPlaylist == null) {
            Log.e(TAG, "fallback playlist should never be null here");
            return;
        }

        delegate.showProgressDialog();
        getSpotifyPlaylistAPI().getPlaylist(token, fallbackPlaylist.getId(), new Callback<BrunoPlaylist, Exception>() {
            @Override
            public void onSuccess(BrunoPlaylist result) {
                BrunoPlaylistImpl playlist = new BrunoPlaylistImpl(result.getId(), result.getName(), result.getTracks());
                try {
                    Log.d(TAG, playlist.getName());
                    FileStorage.writeSerializableToFile(context, FileStorage.FALLBACK_PLAYLIST, playlist);
                    MainActivity.getPreferencesStorage().putString(PreferencesStorage.FALLBACK_PLAYLIST_ID, playlist.getId());
                    wrapperDelegate.onSelectPlaylistPressed();
                } catch (Exception e) {
                    Log.d(TAG, e.getCause() + ": " + e.getMessage());
                    showErrorDialog();
                }
                delegate.dismissProgressDialog();
            }

            @Override
            public void onFailed(Exception e) {
                Log.d(TAG, e.getCause() + ": " + e.getMessage());
                delegate.dismissProgressDialog();
                showErrorDialog();
            }
        });
    }

    private void showErrorDialog() {
        delegate.showAlertDialog(
                null,
                context.getString(R.string.onboarding_fallback_playlist_fail),
                context.getString(R.string.ok_button),
                ((dialogInterface, i) -> dialogInterface.dismiss()),
                false
        );
    }
}
