package com.cs446.group7.bruno.music;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MergedBrunoPlaylistImpl extends BrunoPlaylist implements Serializable {
    private BrunoPlaylist primaryPlaylist;
    private BrunoPlaylist secondaryPlaylist;
    private BrunoTrack mergeTrack;
    private long mergeTrackPlaybackPosition;

    public MergedBrunoPlaylistImpl(final BrunoPlaylist primaryPlaylist,
                                   final BrunoPlaylist secondaryPlaylist,
                                   final BrunoTrack mergeTrack,
                                   long mergeTrackPlaybackPosition) {
        this.primaryPlaylist = primaryPlaylist;
        this.secondaryPlaylist = secondaryPlaylist;
        this.mergeTrack = mergeTrack;
        this.mergeTrackPlaybackPosition = mergeTrackPlaybackPosition;
    }

    @Override
    public String getId() {
        return primaryPlaylist.getId();
    }

    @Override
    public String getName() {
        return primaryPlaylist.getName();
    }

    @Override
    public List<BrunoTrack> getTracks() {
        List<BrunoTrack> mergedTracks = new ArrayList<>();

        for (BrunoTrack track : primaryPlaylist.getTracks()) {
            if (track == mergeTrack) {
                break;
            }

            mergedTracks.add(track);
        }

        mergedTracks.add(mergeTrack.split(mergeTrackPlaybackPosition));
        mergedTracks.addAll(secondaryPlaylist.getTracks());

        return mergedTracks;
    }
}
