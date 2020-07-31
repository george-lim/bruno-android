package com.cs446.group7.bruno.music;

import java.util.List;

public class MergedBrunoPlaylistImpl implements BrunoPlaylist {
    private BrunoPlaylist primaryPlaylist;
    private BrunoPlaylist fallbackPlaylist;
    private BrunoTrack mergeTrack;
    private long mergeTrackPlaylistPosition;

    public MergedBrunoPlaylistImpl(final BrunoPlaylist primaryPlaylist,
                                   final BrunoPlaylist fallbackPlaylist,
                                   final BrunoTrack mergeTrack,
                                   long mergeTrackPlaybackPosition) {
        this.primaryPlaylist = primaryPlaylist;
        this.fallbackPlaylist = fallbackPlaylist;
        this.mergeTrack = mergeTrack;
        this.mergeTrackPlaylistPosition = mergeTrackPlaybackPosition;
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
        return primaryPlaylist.getTracks();
    }
}
