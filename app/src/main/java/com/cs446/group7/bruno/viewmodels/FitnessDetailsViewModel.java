package com.cs446.group7.bruno.viewmodels;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.models.FitnessModel;
import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.playlist.MockPlaylistGeneratorImpl;
import com.cs446.group7.bruno.utils.Callback;

public class FitnessDetailsViewModel {
    // MARK: - Private methods

    private Resources resources;
    // TODO: Replace ViewModel with an implementation when FitnessDetailsModel is available
    private FitnessModel model;
    private FitnessDetailsViewModelDelegate delegate;

    // MARK: - Lifecycle methods

    public FitnessDetailsViewModel(final Context context,
                            final FitnessModel model,
                            final FitnessDetailsViewModelDelegate delegate) {
        this.resources = context.getResources();
        this.model = model;
        this.delegate = delegate;

        setupUI();
    }

    private void setupUI() {
        // TODO: Change these testing values with actual data implementations
        MockPlaylistGeneratorImpl mp = new MockPlaylistGeneratorImpl();
        mp.getPlaylist("test", new Callback<BrunoPlaylist, Exception>() {
            @Override
            public void onSuccess(BrunoPlaylist playlist) {
                delegate.setupUI( 4000, 3900, 500, 300);
                delegate.setupTracklistListView(playlist.tracks);
                delegate.displayCrown(4000, 3900);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(getClass().getSimpleName(), e.getLocalizedMessage());
            }
        });

    }
}
