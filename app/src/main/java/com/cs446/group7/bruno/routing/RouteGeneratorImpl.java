package com.cs446.group7.bruno.routing;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.location.Coordinate;

import org.json.JSONException;

import java.util.List;

/**
 * {@link RouteGenerator} real implementation that uses Google's Directions API to generate routes.
 */
public class RouteGeneratorImpl extends RouteGenerator {
    private final String TAG = getClass().getSimpleName();
    private String gMapsApiKey;

    public RouteGeneratorImpl(final String gMapsApiKey) {
        this.gMapsApiKey = gMapsApiKey;
    }

    @Override
    public void generateRoute(final OnRouteResponseCallback callback,
                              final Coordinate origin,
                              final double totalDistance,
                              double rotation) {

        // Select waypoints forming an equilateral triangle
        final List<Coordinate> waypoints = generateWaypoints(origin, totalDistance, rotation);

        // Build waypoint string
        String waypointDelimiter = "";
        StringBuilder builder = new StringBuilder();
        for (final Coordinate waypoint : waypoints.subList(1, waypoints.size())) {
            builder.append(waypointDelimiter);
            waypointDelimiter = "|";
            builder.append(waypoint.getLatitude())
                    .append(",")
                    .append(waypoint.getLongitude());
        }

        final String url = DIRECTIONS_ENDPOINT + "json?" +
                "origin=" + origin.getLatitude() + "," + origin.getLongitude() + "&"+
                "destination=" + origin.getLatitude() + "," + origin.getLongitude() + "&" +
                "waypoints=" + builder.toString() + "&" +
                "mode=walking&" +
                "avoid=tolls|highways|ferries&" +
                "key=" + gMapsApiKey;

        Log.i(TAG, url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, (String) null, response -> {
            try {
                callback.onRouteReady(parseRouteSegmentsFromJson(response));
            } catch (JSONException e) {
                callback.onRouteError(new RouteGeneratorException(e));
            }
        }, error -> callback.onRouteError(new RouteGeneratorException(error)));

        MainActivity.getVolleyRequestQueue().add(jsonObjectRequest);
    }
}
