package com.example.alfredmuller.parkypig;

import android.annotation.TargetApi;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;

import static com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import android.view.View;
import android.widget.Button;



import org.json.JSONException;
import org.json.JSONObject;
import android.widget.TextView;

import java.util.ArrayList;

import java.text.SimpleDateFormat;
import java.util.List;
import java.lang.String;
import java.util.Date;


import org.json.JSONArray;


public class MapsActivity extends ActionBarActivity {

    // test
    private static GoogleMap mMap; // Might be null if Google Play services APK is not available.



    GPSTracker gps;

    MyDBHandler db;
    //testing 2
    //this is in devel branch
    //did it work?
    //comment
    //test 3
    static LatLng nearest;
    static String name;

    double radius = 0.5;
    double lat;
    double lng;

    String url;
    String url1 = "radius=";
    String url2 = "&response=json&pricing=yes&version=1.0";

    static TextView textView = null;

    Button findNearbyParking, park, history;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        gps = new GPSTracker(MapsActivity.this);
        lat = gps.getLatitude();
        lng = gps.getLongitude();

        db = new MyDBHandler(this);

        textView = (TextView) findViewById(R.id.texting);

        findNearbyParking = (Button) findViewById(R.id.button);
        park = (Button) findViewById(R.id.park);
        history = (Button) findViewById(R.id.pastParking);



        findNearbyParking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createURL();
                httpManager httpManager = new httpManager();
                httpManager.execute(url);
                httpManager.onPostExecute(url);


                /**mMap.addMarker(new MarkerOptions()
                        .position(nearest));**/
            }
        });

        park.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyy HH:mm:ss");
                String s = formatter.format(date);
                try {
                    db.addLocation(new Location(lat, lng, s));


                    //aLocation = new Location();
                    //aLocation.setLatitude(lat);
                    //aLocation.setLatitude(lng);

                    //db.add(lat, lng);
                    //db.addLocation(aLocation);
                    Toast.makeText(getApplicationContext(), "Location Saved" + s, Toast.LENGTH_LONG).show();
                    System.out.println("Here in Park");
                } catch (Exception e) {
                    // do stuff
                }
                //Toast.makeText(getApplicationContext(), "Location Saved", Toast.LENGTH_LONG).show();
                //System.out.println("Location saved");
            }
        });




        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int count = db.getLocationsCount();
                    int tableRow = 1;
                    if(count >10) {
                        tableRow = count - 10;
                    }
                    for(int i=tableRow; i<count; i++) {
                        Location lo = db.getLocation(i);

                        LatLng marker = new LatLng(lo.getLatitude(), lo.getLongitude());
                        String s = lo.getDate();
                        String log = "ID:" + lo.getID() + ", Lat:" + lo.getLatitude() + ", Long:" + lo.getLongitude() + "length: " + count + "Date" + lo.getDate();
                        Toast.makeText(getApplicationContext(), log, Toast.LENGTH_LONG).show();
                        //System.out.println("Lat:"+lo.getLatitude());
                        //Location pastLocation = db.findProduct(0);
                        mMap.addMarker(new MarkerOptions()
                                .position(marker)
                                .title("Last Parked: ")
                                .snippet("" + s));
                    }
                    //textView.setText("hey!");
                } catch (Exception e) {
                    // do stuff
                }

            }
        });


        setUpMapIfNeeded();

    }


    public void createURL() {
        //lat=37.792275;
        //lng=-122.397089;

        url = "http://api.sfpark.org/sfpark/rest/availabilityservice?";
        url = url + "lat=" + lat +"&long=" + lng + "&" + url1 + radius + url2;

        //http://api.sfpark.org/sfpark/rest/availabilityservice?lat=37.792275&long=-122.397089&radius=0.25&uom=mile&response=json
        //http://api.sfpark.org/sfpark/rest/availabilityservice?radius=.05&response=json&pricing=yes&version=1.0
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    public static void addNearbyMarker(LatLng nearby, String name){

            mMap.addMarker(new MarkerOptions()
            .position(nearby)
            .title("Nearest Parking Garage: ")
            .snippet(name + "\n" + nearby));



    }

    @TargetApi(14)
    public static void finallyShow(String response){
        JSONObject rootObject;

        try {
            if(response !=null){
                rootObject = new JSONObject(response);
                JSONArray location = new JSONArray(rootObject.getString("AVL"));
                String message = rootObject.getString("MESSAGE");
                String desc = rootObject.getString("AVAILABILITY_UPDATED_TIMESTAMP");

                List<String> list = new ArrayList<>();


                for (int i = 0; i < location.length(); i++) {
                    list.add(location.getJSONObject(i).getString("NAME"));
                }

                // adds a marker to the nearest available parking location
                name = list.get(5);
                String loc = location.getJSONObject(5).getString("LOC");
                String[] coords = loc.split("\\,");
                nearest = new LatLng(Double.parseDouble(coords[1]), Double.parseDouble(coords[2]));

                addNearbyMarker(nearest, name);

                textView.setText(message + "\n"+ desc + "\n" + name + "\n" + nearest);

            }
            else{
                textView.setText("response is null");
            }


        } catch (JSONException e) {
            textView.setText("No porking found, oink");
            e.printStackTrace();
        }

    }

    @TargetApi(14)
    public static void finallyShowDB(String response){
        JSONObject rootObject;

        try {
            if(response !=null){
                rootObject = new JSONObject(response);
                JSONArray location = new JSONArray(rootObject.getString("AVL"));
                String message = rootObject.getString("MESSAGE");
                String desc = rootObject.getString("AVAILABILITY_UPDATED_TIMESTAMP");
                List<String> list = new ArrayList<>();


                for (int i = 0; i < location.length(); i++) {
                    list.add(location.getJSONObject(i).getString("NAME"));
                }

                // adds a marker to the nearest available parking location
                name = list.get(0);
                String loc = location.getJSONObject(5).getString("LOC");
                String[] coords = loc.split("\\,");
                nearest = new LatLng(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]));


                textView.setText(message + "\n"+ desc + "\n" + name);

            }
            else{
                textView.setText("response is null");
            }


        } catch (JSONException e) {
            textView.setText("No porking found, oink");
            e.printStackTrace();
        }

    }



    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        LatLng current = new LatLng(gps.getLatitude(),gps.getLongitude());
        //String response = httpManager.response;
        mMap.addMarker(new MarkerOptions()
                .position(current)
                .title("Current Porking Location")
                .snippet("lat: " +current.latitude + "\nlong: " + current.longitude)
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pig)));

        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15));

        //addMarker(nearest, name); // add a marker at nearest parking location (Turk St.)

        mMap.getMyLocation();
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng)
                        .title("Your location : ")
                        .snippet("lat : " + latLng.latitude + "\nlng : " + latLng.longitude)
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pig)));
                lat = latLng.latitude;
                lng = latLng.longitude;

                Toast.makeText(getApplicationContext(),
                        "New porking location set!",
                        Toast.LENGTH_LONG).show();
            }
        });

        mMap.setOnMarkerDragListener(new OnMarkerDragListener() {
                                         @Override
                                         public void onMarkerDragStart(Marker marker) {

                                         }

                                         @Override
                                         public void onMarkerDrag(Marker marker) {

                                         }

                                         @Override
                                         public void onMarkerDragEnd(Marker marker) {
                                             mMap.clear();
                                             LatLng latLng = marker.getPosition();
                                             mMap.addMarker(new MarkerOptions().position(latLng)
                                                     .title("Your location : ")
                                                     .draggable(true)
                                                     .snippet("lat : " + latLng.latitude + "\nlng : " + latLng.longitude)
                                                     .icon(BitmapDescriptorFactory.fromResource(R.drawable.pig)));
                                             lat = latLng.latitude;
                                             lng = latLng.longitude;

                                             Toast.makeText(getApplicationContext(),
                                                     "Lat : " + latLng.latitude +" ",
                                                     Toast.LENGTH_LONG).show();


                                         }
                                     }

        );
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maps_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
