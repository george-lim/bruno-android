package com.bruno.android.routing;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bruno.android.BuildConfig;
import com.bruno.android.MainActivity;
import com.bruno.android.location.Coordinate;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link RouteGenerator} real implementation that uses Google's Directions API to generate routes.
 */
public class RouteGeneratorImpl extends RouteGenerator {
    private final String TAG = getClass().getSimpleName();

    @Override
    public void generateRoute(final OnRouteResponseCallback callback,
                              final Coordinate origin,
                              final double totalDistance,
                              double rotation) {

        // Select waypoints forming an equilateral triangle
        final List<Coordinate> waypoints = generateWaypoints(origin, totalDistance, rotation);

        ComputeRoutesRequest request = new ComputeRoutesRequest(origin, origin, waypoints);

        String json = new Gson().toJson(request);
        Log.i(TAG, "Requesting route: " + json);

        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            callback.onRouteError(new RouteGeneratorException(e));
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ROUTES_ENDPOINT, jsonObject, response -> {
            try {
                callback.onRouteReady(parseRouteSegmentsFromJson(response));
            } catch (JSONException e) {
                callback.onRouteError(new RouteGeneratorException(e));
            }
        }, error -> callback.onRouteError(new RouteGeneratorException(error))) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("X-Goog-Api-Key", BuildConfig.MAPS_API_KEY);
                headers.put("X-Goog-FieldMask", "routes.polyline");
                return headers;
            }
        };

        MainActivity.getVolleyRequestQueue().add(jsonObjectRequest);
    }
}
