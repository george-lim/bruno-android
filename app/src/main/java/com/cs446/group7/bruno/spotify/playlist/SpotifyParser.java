package com.cs446.group7.bruno.spotify.playlist;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public interface SpotifyParser {
    <T> List<T> parsePagingItems(JSONArray pagingItems) throws JSONException;
}
