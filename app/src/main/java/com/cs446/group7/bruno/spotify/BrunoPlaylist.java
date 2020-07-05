package com.cs446.group7.bruno.spotify;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// Class which encapsulates all playlist information used by our app
// Contains track information through a list of BrunoTracks
public class BrunoPlaylist {

    final public String name;
    final public String description;
    final int totalTracks;
    final long totalDuration;
    final public List<BrunoTrack> tracks;

    public BrunoPlaylist(final String inputName, final String inputDescription, final int inputTotalTracks,
                         final long inputTotalDuration, final List<BrunoTrack> inputTracks) {
        name = inputName;
        description = inputDescription;
        totalTracks = inputTotalTracks;
        totalDuration = inputTotalDuration;
        tracks = inputTracks;
    }

    // Parses a BrunoPlaylist by reading a response JSON from Spotify's Playlist endpoint
    public static BrunoPlaylist getPlaylistFromJSON(JSONObject responseJson) throws JSONException {
        final String outputPlaylistName = responseJson.getString("name");
        final String outputDescription = responseJson.getString("description");
        final JSONObject pagingObject = responseJson.getJSONObject("tracks");

        final int outputTotalTracks = pagingObject.getInt("total");
        final JSONArray responseTracks = pagingObject.getJSONArray("items");
        long outputPlaylistDuration = 0;
        final List<BrunoTrack> outputTracks = new ArrayList<BrunoTrack>();

        // Iterate through the tracks
        for (int i = 0; i < outputTotalTracks; ++i) {
            final JSONObject responseTrack = responseTracks.getJSONObject(i).getJSONObject("track");
            final String outputAlbum = responseTrack.getJSONObject("album").getString("name");

            final ArrayList<String> outputArtists = new ArrayList<String>();
            final JSONArray responseArtists = responseTrack.getJSONArray("artists");

            // Iterate through the artists of each track
            for (int j = 0; j < responseArtists.length(); ++j) {
                outputArtists.add(responseArtists.getJSONObject(j).getString("name"));
            }

            // implicit int to long conversion - harmless
            final long outputDuration = responseTrack.getInt("duration_ms");
            outputPlaylistDuration += outputDuration;
            String outputTrackName = responseTrack.getString("name");

            BrunoTrack currentTrack = new BrunoTrack(outputTrackName, outputAlbum,
                    outputDuration, outputArtists);
            outputTracks.add(currentTrack);
        }

        final BrunoPlaylist outputPlaylist = new BrunoPlaylist(outputPlaylistName, outputDescription,
                outputTotalTracks, outputPlaylistDuration, outputTracks);
        return outputPlaylist;
    }

}
