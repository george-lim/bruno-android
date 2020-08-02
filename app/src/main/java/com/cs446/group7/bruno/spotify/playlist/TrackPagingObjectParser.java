package com.cs446.group7.bruno.spotify.playlist;

import com.android.volley.DefaultRetryPolicy;
import com.cs446.group7.bruno.music.BrunoTrack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Pages the items of a Spotify paging object into a list of BrunoTrack.
 */
public class TrackPagingObjectParser extends SpotifyPagingObjectParser<BrunoTrack> {

    public TrackPagingObjectParser(final String token, final DefaultRetryPolicy retryPolicy) {
        super(token, retryPolicy);
    }

    protected List<BrunoTrack> parsePagingItems(JSONArray pagingItems) throws JSONException {
        List<BrunoTrack> tracks = new ArrayList<>();

        final int itemCount = pagingItems.length();

        // Iterate through the tracks
        for (int i = 0; i < itemCount; ++i) {
            final JSONObject responseTrack = pagingItems.getJSONObject(i).getJSONObject("track");

            StringBuilder outputArtists = new StringBuilder();
            final JSONArray responseArtists = responseTrack.getJSONArray("artists");
            // Iterate through the artists of each track
            for (int j = 0; j < responseArtists.length(); ++j) {
                outputArtists.append(responseArtists.getJSONObject(j).getString("name"));
                if (j + 1 < responseArtists.length()) {
                    outputArtists.append(", ");
                }
            }

            // implicit int to long conversion - harmless
            final long outputDuration = responseTrack.getInt("duration_ms");
            String outputTrackName = responseTrack.getString("name");

            BrunoTrack currentTrack = new BrunoTrack(outputTrackName, outputArtists.toString(), outputDuration);
            tracks.add(currentTrack);
        }
        return tracks;
    }
}
