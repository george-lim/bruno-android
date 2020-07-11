package com.cs446.group7.bruno.viewmodels;

import android.app.Application;

import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.spotify.SpotifyServiceError;
import com.cs446.group7.bruno.spotify.SpotifyServiceSubscriber;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class SpotifyViewModel extends AndroidViewModel implements SpotifyServiceSubscriber {

    private MutableLiveData<BrunoTrack> currentTrack = new MutableLiveData<>();
    private MutableLiveData<SpotifyServiceError> currentError = new MutableLiveData<>();

    public SpotifyViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<BrunoTrack> getCurrentTrack() {
        return currentTrack;
    }

    public MutableLiveData<SpotifyServiceError> getCurrentError() {
        return currentError;
    }

    @Override
    public void onTrackChanged(BrunoTrack track) {
        currentTrack.setValue(track);
    }

    @Override
    public void onError(SpotifyServiceError error) {
        currentError.setValue(error);
    }
}
