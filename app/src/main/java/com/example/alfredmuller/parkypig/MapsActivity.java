package com.example.alfredmuller.parkypig;

import android.annotation.TargetApi;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;

/**
 * <h1>MapsActivity is the main class which displays the Google Maps window for the ParkyPig App.</h1>
 * <p>The class contains various methods used to display the map, custom icons, event listeners, and buttons.
 * Additional methods make calls various java files that assist in the reading and writing to database. The project
 * displays the current gps location on the map. The marker can be manipulated using long clicks or
 * drags. Clicking the Find Nearby Parking button displays a nearby parking garage. Clicking the Park
 * button stores the current marker location in the SQLite database and pressing the history button
 * displays the last 10 parked locations.</p> <b>Authors are defined as anyone who wrote code for the class.</b>
 *
 * @author Alfred Muller
 * @author Steve Pedersen
 * @author Syed Khureshi
 * @author Vince DiCarlo
 * @author Bryan Chen
 * @version Milestone3
 *
 */
public class MapsActivity extends ActionBarActivity {


    private static GoogleMap mMap; // Might be null if Google Play services APK is not available.

    GPSTracker gps;
    MyDBHandler db;

    private double radius = 0.5;
    double lat;
    double lng;

    static LatLng nearest;
    static String name;
    String url;
    String url1 = "radius=";
    String url2 = "&response=json&pricing=yes&version=1.0";

    static TextView textView = null;

    Button findNearbyParking, park, history;


    /**
     * onCreate is the first method called when the app is launched.
     * @param savedInstanceState the instance of the app
     * @return void
     */
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
            }
        });

        park.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyy HH:mm:ss");
                String s = formatter.format(date);
                MediaPlayer mp = MediaPlayer.create(MapsActivity.this, R.raw.snort);
                mp.start();

                try {
                    db.addLocation(new Location(lat, lng, s));
                    Toast.makeText(getApplicationContext(), "Location Saved As Parking Spot On: " + s, Toast.LENGTH_LONG).show();
                    //System.out.println("Here in Park");//Debugging purposes
                } catch (Exception e) {
                    //do we want something here?
                }
                /*Debugging purposes:
                Toast.makeText(getApplicationContext(), "Location Saved", Toast.LENGTH_LONG).show();
                System.out.println("Location saved");
                */
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
                        String log = "ID:" + lo.getID() + ", Lat:" + lo.getLatitude() + ", Long:" + lo.getLongitude()  + "Date" + lo.getDate();
                        //Toast.makeText(getApplicationContext(), log, Toast.LENGTH_LONG).show();

                        mMap.addMarker(new MarkerOptions()
                                .position(marker)
                                .title("Last Parked: ")
                                .snippet("" + s)
                        .icon(BitmapDescriptorFactory.defaultMarker(330)));
                    }

                } catch (Exception e) {
                    // do we want something here?
                }

            }
        });


        setUpMapIfNeeded();

    }

    /**
     * createURL fashions a string based on the latitude and longitude of the user to submit to the SFPark API.
     * @return void
     */
    public void createURL() {

        url = "http://api.sfpark.org/sfpark/rest/availabilityservice?";
        url = url + "lat=" + lat +"&long=" + lng + "&" + url1 + getRadius() + url2;

        //http://api.sfpark.org/sfpark/rest/availabilityservice?lat=37.792275&long=-122.397089&radius=0.25&uom=mile&response=json
        //http://api.sfpark.org/sfpark/rest/availabilityservice?radius=.05&response=json&pricing=yes&version=1.0
    }

    /**
     * onResume is part of the Android activity flow. If the activity is paused, onResume recreates the map
     * if needed.
     * @return void
     */
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

    /**
     * Creates a new marker on the map when {@link #findNearbyParking} is clicked displaying a nearby
     * parking location.
     * @param nearby
     * @param name
     * @return void
     */
    public static void addNearbyMarker(LatLng nearby, String name){
            //Create the marker with passed parameters.
            mMap.addMarker(new MarkerOptions()
            .position(nearby)
            .title("Nearest Parking Garage: ")
            .snippet(name + "\n" + nearby));



    }


    @TargetApi(14)
    public static void dropMarkers(String response){
        JSONObject rootObject;

        try {
            if(response !=null){
                rootObject = new JSONObject(response);
                JSONArray avl = new JSONArray(rootObject.getString("AVL"));

                List<Location> avlParking = new ArrayList<>();

                // http://api.sfpark.org/sfpark/rest/availabilityservice?lat=37.792275&long=-122.397089&radius=0.25&uom=mile&response=json

                // Create Location objects from parsed JSON info and add to avlParking
                for (int i = 0; i < 5; i++) {
                    Location aLocation = new Location();
                    String temp = avl.getJSONObject(i).getString("LOC");
                    String[] coords = temp.split("\\,");

                    aLocation.setLatitude(Double.parseDouble(coords[1]));
                    //System.out.println("lat: " + aLocation.getLatitude());
                    aLocation.setLongitude(Double.parseDouble(coords[0]));
                    //System.out.println("long: " + aLocation.getLongitude());
                    aLocation.setName(avl.getJSONObject(i).getString("NAME"));
                    //System.out.println(aLocation.getName());

                    try {
                        aLocation.setAddress(avl.getJSONObject(i).getString("DESC"));
                    } catch (Exception e) {
                        aLocation.setAddress("Metered Parking");
                    }
                    //System.out.println(aLocation.getAddress() + "\n");
                    avlParking.add(aLocation);
                }


                // drop 10 markers
                for (int i = 4; i >= 0; i--) {
                    LatLng coords = new LatLng(avlParking.get(i).getLatitude(),
                                                avlParking.get(i).getLongitude());
                    String garageName = avlParking.get(i).getName();
                    String address = avlParking.get(i).getAddress();

                    mMap.addMarker(new MarkerOptions()
                        .position(coords)
                        .title(garageName)
                        .snippet(address));
                }


            }
            else{
                textView.setText("response is null");
            }


        } catch (JSONException e) {
            textView.setText("hmm");
            //e.printStackTrace();
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

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
