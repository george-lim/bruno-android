package com.cs446.group7.bruno.routing;

import android.content.Context;
import android.util.Log;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * {@link RouteGenerator} real implementation that uses Google's Directions API to generate routes.
 */
public class RouteGeneratorImpl extends RouteGenerator {

    public RouteGeneratorImpl(Context context, String gMapsApiKey) {
        super(context, gMapsApiKey);
    }

    @Override
    public void generateRoute(final OnRouteResponseCallback callback, final LatLng start, final double totalDistance, double rotation) {

        // Select waypoints forming an equilateral triangle
        final List<LatLng> waypoints = generateWaypoints(start, totalDistance, rotation);

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
                try {
                    callback.onRouteReady(parseRouteFromJson(response));
                } catch (JSONException e) {
                    callback.onRouteError(RouteGeneratorError.PARSE_ERROR, e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
                if (error instanceof NoConnectionError) {
                    callback.onRouteError(RouteGeneratorError.NO_CONNECTION_ERROR, error);
                } else if (error instanceof ServerError) {
                    callback.onRouteError(RouteGeneratorError.SERVER_ERROR, error);
                } else {
                    callback.onRouteError(RouteGeneratorError.OTHER_ERROR, error);
                }
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}
