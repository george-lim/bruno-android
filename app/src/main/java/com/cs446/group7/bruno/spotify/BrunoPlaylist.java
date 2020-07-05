package com.cs446.group7.bruno.spotify;

import org.json.JSONObject;

import java.util.List;

public class BrunoPlaylist {

    public List<BrunoTrack> tracks;

    public BrunoPlaylist(List<BrunoTrack> inputTracks) {
        tracks = inputTracks;
    }

    /*
    public static BrunoPlaylist getPlaylistFromJSON(JSONObject object) {
        List<BrunoTrack> myTracks = new List<BrunoTrack>();
        return new BrunoPlaylist(myTracks);
    }
    */
}
