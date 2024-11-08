package com.bruno.android.music;

public class MergedBrunoPlaylistImpl extends BrunoPlaylist {
    private BrunoPlaylist primaryPlaylist;
    private BrunoPlaylist secondaryPlaylist;
    private int mergeTrackIndex;
    private long mergePlaybackPosition;

    public MergedBrunoPlaylistImpl(final BrunoPlaylist primaryPlaylist,
                                   final BrunoPlaylist secondaryPlaylist,
                                   int mergeTrackIndex,
                                   long mergePlaybackPosition) {
        this.primaryPlaylist = primaryPlaylist;
        this.secondaryPlaylist = secondaryPlaylist;
        this.mergeTrackIndex = mergeTrackIndex;
        this.mergePlaybackPosition = mergePlaybackPosition;
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
    public BrunoTrack getTrack(int index) {
        if (index < mergeTrackIndex) {
            return primaryPlaylist.getTrack(index);
        }
        else if (index == mergeTrackIndex && mergePlaybackPosition == 0) {
            return secondaryPlaylist.getTrack(0);
        }
        else if (index == mergeTrackIndex) {
            return primaryPlaylist.getTrack(index).split(mergePlaybackPosition);
        }
        else {
            return secondaryPlaylist.getTrack((index - mergeTrackIndex) - 1);
        }
    }

    @Override
    public boolean isEmpty() {
        return primaryPlaylist.isEmpty() && secondaryPlaylist.isEmpty();
    }
}
