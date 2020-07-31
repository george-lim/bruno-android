package com.cs446.group7.bruno.music;

import java.util.ArrayList;
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
        return primaryPlaylist.getName() + " [Merged]";
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

        BrunoTrack splitMergeTrack = new BrunoTrack(
                mergeTrack.getName(),
                mergeTrack.getAlbum(),
                mergeTrackPlaylistPosition
        );

        mergedTracks.add(splitMergeTrack);
        mergedTracks.addAll(fallbackPlaylist.getTracks());

        return mergedTracks;
    }
}
