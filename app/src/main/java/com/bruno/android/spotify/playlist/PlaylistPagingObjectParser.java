package com.bruno.android.spotify.playlist;

import com.android.volley.DefaultRetryPolicy;
import com.bruno.android.music.playlist.PlaylistMetadata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Pages the items of a Spotify paging object into a list of PlaylistMetadata.
 */
public class PlaylistPagingObjectParser extends SpotifyPagingObjectParser<PlaylistMetadata> {

    public PlaylistPagingObjectParser(final String token, final DefaultRetryPolicy retryPolicy) {
        super(token, retryPolicy);
    }

    protected List<PlaylistMetadata> parsePagingItems(JSONArray pagingItems) throws JSONException {
        List<PlaylistMetadata> playlists = new ArrayList<>();

        final int itemCount = pagingItems.length();

        // Iterate through the playlists
        for (int i = 0; i < itemCount; ++i) {

            final JSONObject currentPlaylist = pagingItems.getJSONObject(i);

            final String playlistId = currentPlaylist.getString("id");
            final String playlistName = currentPlaylist.getString("name");
            final JSONObject playlistTracks = currentPlaylist.getJSONObject("tracks");
            final int tracksLength = playlistTracks.getInt("total");

            playlists.add(new PlaylistMetadata(playlistId, playlistName, tracksLength));
        }
        return playlists;
    }
}
