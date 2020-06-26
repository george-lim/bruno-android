package com.cs446.group7.bruno.routing;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Route {
    public static void run(final GoogleMap map, final Location loc, final String apiKey, Context c) {
        // Sample to draw custom markers
        // Add a marker in Sydney and move the camera
        // LatLng sydney = new LatLng(-34, 151);
        // map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        final double distance = 0.1;
        final double l = distance / 3;
        final double theta = 0;
        final double A = Math.PI / 3;

        final double x1 = loc.getLatitude();
        final double y1 = loc.getLongitude();

        final LatLng p1 = new LatLng(x1,y1);
        final LatLng p2 = new LatLng(
                x1 + l * Math.sin(theta),
                y1 + l * Math.cos(theta)
        );
        final LatLng p3 = new LatLng(
                x1 + l * Math.sin(theta + A),
                y1 + l * Math.cos(theta + A)
        );

        map.addMarker(new MarkerOptions().position(p1));
        map.addMarker(new MarkerOptions().position(p2));
        map.addMarker(new MarkerOptions().position(p3));

        // polyline
//        map.addPolyline(new PolylineOptions()
//                .clickable(true).add(p1, p2, p3, p1));


        // path

        // get directions
        final List<LatLng> decoded = PolyUtil.decode(getEncodedRoute(p1, p1, new ArrayList<LatLng>(){
            {
                add(p2);
                add(p3);
            }
        }, apiKey, c, map));

        // map.addPolyline(new PolylineOptions().addAll(decoded));

//        for (int i = 0; i < 10; ++i) {
//            final LatLng location = new LatLng(i, 0);
//            map.addMarker(new MarkerOptions().position(location).title(String.format("Marker %s", i)));
//        }
        // map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(0, 0)));


    }

    private static String getEncodedRoute(final LatLng start, final LatLng finish,
                                          final List<LatLng> waypoints, final String apiKey, Context c, final GoogleMap map) {
        final String endpoint = "https://maps.googleapis.com/maps/api/directions/";

        String waypointDelimiter = "";
        StringBuilder builder = new StringBuilder();
        for (final LatLng waypoint : waypoints) {
            builder.append(waypointDelimiter);
            waypointDelimiter = "|";
            builder.append(waypoint.latitude)
                    .append(",")
                    .append(waypoint.longitude);
        }

        final String url = endpoint + "json?" +
                "origin=" + start.latitude + "," + start.longitude + "&"+
                "destination=" + finish.latitude + "," + finish.longitude + "&" +
                ("waypoints=" + builder.toString()) + "&" +
                "mode=walking&" +
                "avoid=tolls|highways|ferries&" +
                "key=" + apiKey;
        Log.e("TEST", url);
        final String[] res = {null};

//        // make call
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        RequestQueue requestQueue = Volley.newRequestQueue(c);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("TEST", response.toString());
                        try {
                            res[0] = response.getJSONArray("routes")
                                    .getJSONObject(0)
                                    .getJSONObject("overview_polyline")
                                    .getString("points");

                            Log.i("TEST", res[0]);
                            map.addPolyline(new PolylineOptions().addAll(PolyUtil.decode(res[0])));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("TEST", e.toString());
                        }
                         res[0] = response.toString();
                         Log.e("TEST", response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TEST", error.toString());

                    }
                });
        requestQueue.add(jsonObjectRequest);

        return "";
        //return "k~_iGvkgeNaAkBY_@i@k@a@Wc@OqAY{@Io@Mc@O}@g@eA{@mDwDgC}CKGwBuCs@cAc@gAOm@Is@w@kK_@_B_@}@qBiD_CaEQSkAw@UMb@cB~@eDz@eCf@iAVm@j@iA|@yApBkCx@gAxAoBlBoCrFmIxBkD|C_FbCwDuBiC`A_DeCqDgA_BaCaDm@q@fDuG~BgEh@}@oEqFvAoCdAoBbBoCn@}@f@_AFQJGPS|EaI}E`IQRKFGPg@~@o@|@cBnCeAnBwAnCsJ{LgAuA[l@yB~DWT[Y]U]M[CyCP{@N]NUPc@j@eBbDyCwDs@aAg@Cw@L}@vAgAfBoBjC}@jAoCvDcDbFuExGe@p@wBdDeBlCQWmDbFs@bAkJhNcD`FoAtBmCnEwJhPYf@eBdCo@w@s@}@Ue@yAuD_@m@iBoBm@k@eDoCmGoHOPsAZcAEQBS\\U\\SRm@x@OZN[l@y@RST]R]PCZ@f@BrA[NQf@j@dFbGdDnCl@j@hBnB^l@Vn@`AdCTd@r@|@n@v@dBeCXg@pBgDZ^lB_D|@}AHLb@^\\D`@EVQTWhDzD~J`LlBdCpA`BnE~ElCxC~D|E~DrEtGtHz@fAdF`G|EtFN]t@eAZ_@ZW\\Qd@M^CX?h@DnDb@t@@t@S^U\\]x@oAvBtCJF|AlBh@n@lDvDdAz@|@f@b@Nn@Lz@HpAXb@N`@Vh@j@X^`AjB";
    }
}
