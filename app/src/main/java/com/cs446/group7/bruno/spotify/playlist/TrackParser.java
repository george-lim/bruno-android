package com.cs446.group7.bruno.spotify.playlist;

import com.cs446.group7.bruno.music.BrunoTrack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// This class is capable of parsing the paging items from the Spotify API into a list of BrunoTrack
public class TrackParser implements SpotifyParser {
    public List<BrunoTrack> parsePagingItems(JSONArray pagingItems) throws JSONException {
        List<BrunoTrack> tracks = new ArrayList<>();

        final int itemCount = pagingItems.length();

        // Iterate through the tracks
        for (int i = 0; i < itemCount; ++i) {
            final JSONObject responseTrack = pagingItems.getJSONObject(i).getJSONObject("track");
            final String outputAlbum = responseTrack.getJSONObject("album").getString("name");

            final ArrayList<String> outputArtists = new ArrayList<String>();
            final JSONArray responseArtists = responseTrack.getJSONArray("artists");

            // Iterate through the artists of each track
            for (int j = 0; j < responseArtists.length(); ++j) {
                outputArtists.add(responseArtists.getJSONObject(j).getString("name"));
            }

            // implicit int to long conversion - harmless
            final long outputDuration = responseTrack.getInt("duration_ms");
            String outputTrackName = responseTrack.getString("name");

            BrunoTrack currentTrack = new BrunoTrack(outputTrackName, outputAlbum,
                    outputDuration, outputArtists);
            tracks.add(currentTrack);
        }
        return tracks;
    }
}
