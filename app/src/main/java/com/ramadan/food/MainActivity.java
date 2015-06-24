package com.ramadan.food;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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

    static final LatLng HAMBURG = new LatLng(53.558, 9.927);
    private GoogleMap map;
    private ArrayList<Place> places = new ArrayList<Place>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Place.class);
        Parse.initialize(this, "Fnl9PAspRVWFfeFo8YrO5tygcgUBKvROSe0BeUgz", "BsyXszdwHaxVdHl8o5iLqH6wPQdNZxP7Y6BF4eoX");

//        places.add(new Place(new LatLng(53.558, 9.927),"Hamburg","Gotta go here sometime"));
//        places.add(new Place(new LatLng(53.551, 9.993),"Kiel","Keil is cool"));

        final ProgressDialog pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Getting locations");
        pd.show();

        ParseQuery<Place> placeParseQuery = ParseQuery.getQuery(Place.class);
        placeParseQuery.findInBackground(new FindCallback<Place>() {
            @Override
            public void done(List<Place> list, ParseException e) {
                if(e==null) {
                    places.clear();
                    for (Place place : list)
                        places.add(place);

                    for (Place place : places) {
                        map.addMarker(new MarkerOptions().position(place.getLocation())
                                .title(place.getTitle())
                                .snippet(place.getDescription()));
                    }

                    pd.hide();
                }
                else{
                    pd.hide();
                    Toast.makeText(MainActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });

        GoogleMapOptions options = new GoogleMapOptions();
        options.compassEnabled(true);
        options.zoomControlsEnabled(true);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        if (map!=null){

            UiSettings settings = map.getUiSettings();
            settings.setCompassEnabled(true);
            settings.setZoomControlsEnabled(true);
            settings.setMyLocationButtonEnabled(true);

            // Move the camera instantly to hamburg with a zoom of 15.
//            map.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));

            // Zoom in, animating the camera.
//            map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
