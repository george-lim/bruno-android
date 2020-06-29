package com.cs446.group7.bruno.routing;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Route {
    private String encodedPath;
    private List<LatLng> decodedPath;

    public Route(final String encodedPath, final List<LatLng> decodedPath) {
        this.encodedPath = encodedPath;
        this.decodedPath = decodedPath;
    }

    public String getEncodedPath() {
        return encodedPath;
    }

    public List<LatLng> getDecodedPath() {
        return decodedPath;
    }

    public static Route parseFromJson(final JSONObject response) throws JSONException {
        final String encodedPath = response.getJSONArray("routes")
                .getJSONObject(0)
                .getJSONObject("overview_polyline")
                .getString("points");

        final List<LatLng> decodedPath = PolyUtil.decode(encodedPath);
        return new Route(encodedPath, decodedPath);
    }
}
