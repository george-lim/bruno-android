package com.cs446.group7.bruno.spotify.playlist;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

// This interface allows us to select the type of parsing we wish to do on the items of a paging object
public interface SpotifyParser {
    <T> List<T> parsePagingItems(JSONArray pagingItems) throws JSONException;
}
