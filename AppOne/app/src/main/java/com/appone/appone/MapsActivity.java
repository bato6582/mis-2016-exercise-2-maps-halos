package com.appone.appone;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SharedPreferences sharedPreferences;
    int locationCount;
    String title_ = "";
    String snippet_ = "";
    final List<Circle> circleList = new ArrayList<>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public static void hideSoftKeyboard(MapsActivity activity) {
        // http://developer.android.com/reference/android/view/inputmethod/InputMethodManager.html
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Toast.makeText(getApplicationContext(), "Type in your description and do a longclick at a place to add your text there.",
                Toast.LENGTH_LONG).show();


        //source: http://stackoverflow.com/questions/35868807/saving-google-map-markers-into-sharedpreferences-in-android-studios

        // Opening the sharedPreferences object
        sharedPreferences = getSharedPreferences("location", 0);


        //Deleting all markers
        //SharedPreferences.Editor editor = sharedPreferences.edit();
        //editor.clear().commit();



        // Getting number of locations already stored
        locationCount = sharedPreferences.getInt("locationCount", 0);


        // If locations are already saved
        if (locationCount != 0) {

            String lat = "";
            String lng = "";



            // Iterating through all the locations stored
            for (int i = 0; i < locationCount; i++) {

                title_ = sharedPreferences.getString("title" +i, "0");

                snippet_ = sharedPreferences.getString("snippet" +i, "0");

                // Getting the latitude of the i-th location
                lat = sharedPreferences.getString("lat" + i, "0");

                // Getting the longitude of the i-th location
                lng = sharedPreferences.getString("lng" + i, "0");


                double lat3 = Double.valueOf(lat).doubleValue();
                double lng3 = Double.valueOf(lng).doubleValue();


                LatLng lalo = new LatLng(lat3,lng3);

                mMap.addMarker(new MarkerOptions()
                        .title(title_)
                        .snippet(snippet_)
                        .position(lalo));

                // Instantiates a new CircleOptions object and defines the center and radius
                CircleOptions circleOptions = new CircleOptions()
                        .center(lalo)
                        .radius(0)
                        .strokeColor(Color.RED);


                // Get back the mutable Circle
                Circle circle = mMap.addCircle(circleOptions);
                circleList.add(circle);
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(lalo));



            }

        }


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {


            @Override
            public void onMapLongClick(LatLng latLng) {

                hideSoftKeyboard(MapsActivity.this);

                EditText text_ = (EditText) findViewById(R.id.editText);
                String strText = text_.getText().toString();

                mMap.addMarker(new MarkerOptions()
                        .title("Marker description: ")
                        .snippet(strText)
                        .position(latLng));

                // Instantiates a new CircleOptions object and defines the center and radius
                CircleOptions circleOptions = new CircleOptions()
                        .center(latLng)
                        .radius(0)
                        .strokeColor(Color.RED);// In meters

                // Get back the mutable Circle
                Circle circle = mMap.addCircle(circleOptions);

                circleList.add(circle);

                locationCount++;

                // Opening the editor to write data to sharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("title" + Integer.toString((locationCount - 1)), "Marker description");
                editor.putString("snippet" + Integer.toString((locationCount - 1)), strText);

                // Storing the latitude for the i-th location
                editor.putString("lat" + Integer.toString((locationCount - 1)), Double.toString(latLng.latitude));

                // Storing the longitude for the i-th location
                editor.putString("lng" + Integer.toString((locationCount - 1)), Double.toString(latLng.longitude));

                // Storing the count of locations or marker count
                editor.putInt("locationCount", locationCount);


                // Saving the values stored in the shared preferences
                editor.commit();
            }
        });


        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                // Iterating through all circles
                for (Circle circle : circleList) {
                    LatLng cameraPos = cameraPosition.target;
                    LatLng markerPos = circle.getCenter();

                    // http://stackoverflow.com/questions/2741403/get-the-distance-between-two-geo-points

                    Location marker_loc = new Location("");
                    marker_loc.setLatitude(markerPos.latitude);
                    marker_loc.setLongitude(markerPos.longitude);



                    VisibleRegion vr = mMap.getProjection().getVisibleRegion();
                    double left = vr.latLngBounds.southwest.longitude;
                    double top = vr.latLngBounds.northeast.latitude;
                    double right = vr.latLngBounds.northeast.longitude;
                    double bottom = vr.latLngBounds.southwest.latitude;

                    Location center = new Location("center");
                    center.setLatitude(vr.latLngBounds.getCenter().latitude);
                    //center.setLongitude(vr.latLngBounds.getCenter().longitude);

                    Location middleLeftLocation = new Location("middleLeft");
                    middleLeftLocation.setLatitude(center.getLatitude());
                    middleLeftLocation.setLongitude(left);

                    Location middleRightLocation = new Location("middleRight");
                    middleRightLocation.setLatitude(center.getLatitude());
                    middleRightLocation.setLongitude(right);

                    Location middleTopLocation = new Location("middleTop");
                    middleTopLocation.setLatitude(top);
                    middleTopLocation.setLongitude(center.getLongitude());

                    Location middleBottomLocation = new Location("middleBottom");
                    middleBottomLocation.setLatitude(bottom);
                    middleBottomLocation.setLongitude(center.getLongitude());

                    //source: https://recalll.co/app/?q=How%20to%20get%20Latitude%2FLongitude%20span%20in%20Google%20Map%20V2%20for%20Android%20-%20Stack%20Overflow

                    // Get the bounds for the visible region on the display
                    LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;


                    //http://stackoverflow.com/questions/20422701/retrieve-distance-from-visible-part-of-google-map
                    float height = middleTopLocation.distanceTo(middleBottomLocation);


                    if(bounds.contains(markerPos)){
                        circle.setRadius(0);
                    }else if(circle.getRadius() <= (height/2)){

                        Vector<Location> middle_locations = new Vector<>();

                        middle_locations.add(middleLeftLocation);
                        middle_locations.add(middleRightLocation);
                        middle_locations.add(middleTopLocation);
                        middle_locations.add(middleBottomLocation);

                        float distanceClosest = middleLeftLocation.distanceTo(marker_loc);
                        Location Closest = middleLeftLocation;
                        Location BorderLocation = new Location("BorderLocation");

                        for (int i = 1; i < 4; i++) {

                            if (middle_locations.get(i).distanceTo(marker_loc) < distanceClosest) {

                                distanceClosest = middle_locations.get(i).distanceTo(marker_loc);
                                Closest = middle_locations.get(i);
                            }
                        }

                        // Finding the closest middle location of the border to the marker


                        if (Closest == middleLeftLocation) {

                            BorderLocation.setLatitude(markerPos.latitude);
                            BorderLocation.setLongitude(left);
                        } else if (Closest == middleRightLocation) {

                            BorderLocation.setLatitude(markerPos.latitude);
                            BorderLocation.setLongitude(right);
                        } else if (Closest == middleTopLocation) {

                            BorderLocation.setLatitude(top);
                            BorderLocation.setLongitude(markerPos.longitude);
                        } else if (Closest == middleBottomLocation) {

                            BorderLocation.setLatitude(bottom);
                            BorderLocation.setLongitude(markerPos.longitude);
                        }


                        float newRadiusDis = marker_loc.distanceTo(BorderLocation);
                        circle.setRadius(newRadiusDis+800000); //setting the radius to the border plus a bit more
                    }else{
                        circle.setRadius(0);
                    }
                }
            }
        });




        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            // public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getApplicationContext(), "No Permission", Toast.LENGTH_LONG).show();
            return;
        }
        mMap.setMyLocationEnabled(true);



    }
}
