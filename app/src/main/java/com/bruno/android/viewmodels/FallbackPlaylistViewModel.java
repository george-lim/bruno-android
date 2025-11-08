package com.bruno.android.viewmodels;

import android.content.Context;
import android.util.Log;

import com.bruno.android.BuildConfig;
import com.bruno.android.MainActivity;
import com.bruno.android.R;
import com.bruno.android.capability.Capability;
import com.bruno.android.music.BrunoPlaylist;
import com.bruno.android.music.BrunoPlaylistImpl;
import com.bruno.android.music.playlist.PlaylistMetadata;
import com.bruno.android.spotify.SpotifyService;
import com.bruno.android.spotify.playlist.DynamicSpotifyPlaylistAPIImpl;
import com.bruno.android.spotify.playlist.MockSpotifyPlaylistAPIImpl;
import com.bruno.android.spotify.playlist.SpotifyPlaylistAPI;
import com.bruno.android.storage.FileStorage;
import com.bruno.android.storage.PreferencesStorage;
import com.bruno.android.ui.shared.FallbackPlaylistAction;
import com.bruno.android.utils.Callback;
import com.bruno.android.utils.ClosureQueue;

import java.util.List;

public class FallbackPlaylistViewModel {

    private final Context context;
    private final FallbackPlaylistAction wrapperDelegate;
    private final FallbackPlaylistViewModelDelegate delegate;
    private String token;
    private boolean ongoingRequest;
    private PlaylistMetadata fallbackPlaylist;
    public final String TAG = this.getClass().getSimpleName();
    private boolean hasShownSpotifyError;

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
        if (hasShownSpotifyError) {
            return;
        }

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

        boolean hasInternet = MainActivity
                .getCapabilityService()
                .isCapabilityEnabled(Capability.INTERNET);

        if (!hasInternet) {
            showSpotifyError(context.getResources().getString(R.string.onboarding_missing_internet_text));
            return;
        }

        // Get user to authorize if not done, otherwise fetch user playlist library for fallback playlist
        SpotifyService spotifyService = MainActivity.getSpotifyService();
        ClosureQueue<Void, Void> queue = new ClosureQueue<>();

        if (token == null) {
            queue.add((result, callback) -> spotifyService
                    .getAuthService()
                    .requestUserAuth(new Callback<>() {
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
                    }));
        }

        queue.add((result, callback) -> spotifyService
                .getAuthService()
                .checkIfUserIsPremium(token, new Callback<>() {
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
                }));

        queue.add((result, callback) -> getSpotifyPlaylistAPI()
                .getUserPlaylistLibrary(token, new Callback<>() {
                    @Override
                    public void onSuccess(List<PlaylistMetadata> playlistMetadata) {
                        if (playlistMetadata.isEmpty()) {
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
                }));

        queue.run(new Callback<>() {
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
                ? new DynamicSpotifyPlaylistAPIImpl(
                MainActivity.getSpotifyService().getPlaylistService(),
                new MockSpotifyPlaylistAPIImpl()
        )
                : MainActivity.getSpotifyService().getPlaylistService();
    }

    private void showSelectPlaylist(List<PlaylistMetadata> playlists) {
        int index = 0;
        String savedFallbackPlaylistId = MainActivity
                .getPreferencesStorage()
                .getString(PreferencesStorage.KEYS.FALLBACK_PLAYLIST_ID, null);
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
        hasShownSpotifyError = true;
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
        getSpotifyPlaylistAPI().getPlaylist(
                token,
                fallbackPlaylist.getId(),
                new Callback<>() {
                    @Override
                    public void onSuccess(BrunoPlaylist playlist) {
                        try {
                            Log.d(TAG, playlist.getName());
                            FileStorage.writeSerializableToFile(
                                    context,
                                    FileStorage.KEYS.FALLBACK_PLAYLIST,
                                    (BrunoPlaylistImpl) playlist);
                            MainActivity
                                    .getPreferencesStorage()
                                    .putString(
                                            PreferencesStorage.KEYS.FALLBACK_PLAYLIST_ID,
                                            playlist.getId()
                                    );
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
                }
        );
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
