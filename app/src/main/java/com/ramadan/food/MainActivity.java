package com.ramadan.food;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final LatLng BANGALORE = new LatLng(12.972127, 77.593925);
    private GoogleMap map;
    private ArrayList<Place> mPlaces = new ArrayList<Place>();
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ramadan Food");
        toolbar.setTitleTextColor(Color.WHITE);

        ParseObject.registerSubclass(Place.class);
        Parse.initialize(this, "Fnl9PAspRVWFfeFo8YrO5tygcgUBKvROSe0BeUgz", "BsyXszdwHaxVdHl8o5iLqH6wPQdNZxP7Y6BF4eoX");

        GoogleMapOptions options = new GoogleMapOptions();
        options.compassEnabled(true);
        options.zoomControlsEnabled(true);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        if (map!=null){

            map.setMyLocationEnabled(true);
            UiSettings settings = map.getUiSettings();
            settings.setCompassEnabled(true);
            settings.setZoomControlsEnabled(true);
            settings.setMyLocationButtonEnabled(true);

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(BANGALORE, 12));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPlaces();
    }

    public void getPlaces(){

        if(Utils.isNetworkAvailable(this)) {

            Utils.showProgressDialogue(this, "Getting Locations");

            ParseQuery<Place> placeParseQuery = ParseQuery.getQuery(Place.class);
            placeParseQuery.findInBackground(new FindCallback<Place>() {
                @Override
                public void done(List<Place> list, ParseException e) {
                    if (e == null) {
                        mPlaces.clear();
                        for (Place place : list)
                            mPlaces.add(place);

                        for (Place place : mPlaces) {
                            map.addMarker(new MarkerOptions().position(place.getLocation())
                                    .title(place.getTitle())
                                    .snippet(place.getDescription()));
                        }
//                    map.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
                        Utils.dismissProgressDialogue();
                    } else {
                        Utils.dismissProgressDialogue();
                        Toast.makeText(MainActivity.this, "Data Error Occurred", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            Toast.makeText(MainActivity.this, "No Internet Available",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_location) {
            Intent intent = new Intent(MainActivity.this, AddLocationActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
