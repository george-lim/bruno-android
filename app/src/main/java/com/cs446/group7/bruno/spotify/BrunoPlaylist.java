package com.cs446.group7.bruno.spotify;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// Class which encapsulates all playlist information used by our app
// Contains track information through a list of BrunoTracks
public class BrunoPlaylist {

    public String name;
    public String description;
    int totalTracks;
    long totalDuration;
    public List<BrunoTrack> tracks;

    public BrunoPlaylist(String inputName, String inputDescription, int inputTotalTracks,
                         long inputTotalDuration, List<BrunoTrack> inputTracks) {
        name = inputName;
        description = inputDescription;
        totalTracks = inputTotalTracks;
        totalDuration = inputTotalDuration;
        tracks = inputTracks;
    }

    // Parses a BrunoPlaylist by reading a response JSON from Spotify's Playlist endpoint
    public static BrunoPlaylist getPlaylistFromJSON(JSONObject responseJson) throws JSONException {
        try {
            String outputPlaylistName = responseJson.getString("name");
            String outputDescription = responseJson.getString("description");
            JSONObject pagingObject = responseJson.getJSONObject("tracks");

            int outputTotalTracks = pagingObject.getInt("total");
            JSONArray responseTracks = pagingObject.getJSONArray("items");
            long outputPlaylistDuration = 0;
            List<BrunoTrack> outputTracks = new ArrayList<BrunoTrack>();

            // Iterate through the tracks
            for (int i = 0; i < outputTotalTracks; ++i) {
                JSONObject responseTrack = responseTracks.getJSONObject(i).getJSONObject("track");
                String outputAlbum = responseTrack.getJSONObject("album").getString("name");

                ArrayList<String> outputArtists = new ArrayList<String>();
                JSONArray responseArtists = responseTrack.getJSONArray("artists");

                // Iterate through the artists of each track
                for (int j = 0; j < responseArtists.length(); ++j) {
                    outputArtists.add(responseArtists.getJSONObject(j).getString("name"));
                }

                // implicit int to long conversion - harmless
                long outputDuration = responseTrack.getInt("duration_ms");
                outputPlaylistDuration += outputDuration;
                String outputTrackName = responseTrack.getString("name");

                BrunoTrack currentTrack = new BrunoTrack(outputTrackName, outputAlbum,
                        outputDuration, outputArtists);
                outputTracks.add(currentTrack);
            }

            BrunoPlaylist outputPlaylist = new BrunoPlaylist(outputPlaylistName, outputDescription,
                    outputTotalTracks, outputPlaylistDuration, outputTracks);
            return outputPlaylist;

        } catch(JSONException e) {
            throw e;
        }
    }

}
