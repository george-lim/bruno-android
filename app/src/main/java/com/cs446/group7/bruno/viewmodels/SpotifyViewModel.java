package com.cs446.group7.bruno.viewmodels;

import android.app.Application;

import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.spotify.SpotifyServiceSubscriber;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

// Keeps track of Spotify data during the run
// Could be extended to include other values needed during the run
public class SpotifyViewModel extends AndroidViewModel implements SpotifyServiceSubscriber {

    private MutableLiveData<BrunoTrack> currentTrack = new MutableLiveData<>();

    public SpotifyViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<BrunoTrack> getCurrentTrack() {
        return currentTrack;
    }

    @Override
    public void onTrackChanged(BrunoTrack track) {
        currentTrack.setValue(track);
    }
}
