package com.appone.appone;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.EditText;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SharedPreferences sharedPreferences;
    int locationCount;
    String title_ = "";
    String snippet_ = "";



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        //http://stackoverflow.com/questions/35868807/saving-google-map-markers-into-sharedpreferences-in-android-studios

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
            }

        }

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {

                EditText text_ = (EditText) findViewById(R.id.editText);
                String strText = text_.getText().toString();

                mMap.addMarker(new MarkerOptions()
                        .title("Marker description: ")
                        .snippet(strText)
                        .position(latLng));

                locationCount++;

                /** Opening the editor object to write data to sharedPreferences */
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




        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);



    }
}
