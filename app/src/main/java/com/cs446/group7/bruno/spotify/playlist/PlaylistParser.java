package com.cs446.group7.bruno.spotify.playlist;

import com.cs446.group7.bruno.music.playlist.PlaylistInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// This class is capable of parsing the paging items from the Spotify API into a list of PlaylistInfo
public class PlaylistParser implements SpotifyParser {
    public List<PlaylistInfo> parsePagingItems(JSONArray pagingItems) throws JSONException {
        List<PlaylistInfo> playlists = new ArrayList<>();

        final int itemCount = pagingItems.length();

        // Iterate through the playlists
        for (int i = 0; i < itemCount; ++i) {

            final JSONObject currentPlaylist = pagingItems.getJSONObject(i);

            final String playlistId = currentPlaylist.getString("id");
            final String playlistName = currentPlaylist.getString("name");
            final String playlistDescription = currentPlaylist.getString("description");
            final JSONObject playlistTracks = currentPlaylist.getJSONObject("tracks");
            final int tracksLength = playlistTracks.getInt("total");

            playlists.add(new PlaylistInfo(playlistId, playlistName,
                    playlistDescription, tracksLength));
        }
        return playlists;
    }
}
