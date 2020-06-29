package com.cs446.group7.bruno.routing;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.List;

/**
 * {@link RouteGenerator} real implementation that uses Google's Directions API to generate routes.
 */
class RouteGeneratorRealImpl extends RouteGenerator {

    RouteGeneratorRealImpl(Context context, String gMapsApiKey) {
        super(context, gMapsApiKey);
    }

    @Override
    public void generateRoute(final OnRouteReadyCallback callback, final LatLng start, int numPoints, final double totalDistance, double rotation) {
        if (numPoints < 3) {
            throw new IllegalArgumentException(String.format("numPoints must be at least 3, %s given", numPoints));
        }

        // Select waypoints forming a regular numPointed-polygon anchored
        final List<LatLng> waypoints = generateWaypoints(start, numPoints, totalDistance, rotation);

        // Build waypoint string
        String waypointDelimiter = "";
        StringBuilder builder = new StringBuilder();
        for (final LatLng waypoint : waypoints.subList(1, waypoints.size())) {
            builder.append(waypointDelimiter);
            waypointDelimiter = "|";
            builder.append(waypoint.latitude)
                    .append(",")
                    .append(waypoint.longitude);
        }

        final String url = DIRECTIONS_ENDPOINT + "json?" +
                "origin=" + start.latitude + "," + start.longitude + "&"+
                "destination=" + start.latitude + "," + start.longitude + "&" +
                "waypoints=" + builder.toString() + "&" +
                "mode=walking&" +
                "avoid=tolls|highways|ferries&" +
                "key=" + gMapsApiKey;

        Log.i(TAG, url);

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onRouteReady(parseRouteFromJson(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}
