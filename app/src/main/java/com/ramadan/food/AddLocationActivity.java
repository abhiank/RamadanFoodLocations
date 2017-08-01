package com.ramadan.food;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class AddLocationActivity extends AppCompatActivity {

    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private EditText mTitleEditText;
    private EditText mDescEditText;
    private Button mSaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        GoogleMapOptions options = new GoogleMapOptions();
        options.compassEnabled(true);
        options.zoomControlsEnabled(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Add new Location");
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        mTitleEditText = (EditText) findViewById(R.id.titleEditText);
        mDescEditText = (EditText) findViewById(R.id.descEditText);
        mSaveButton = (Button) findViewById(R.id.saveButton);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCurrentLocation==null){
                    return;
                }
                if(mTitleEditText.getText().toString().trim().equals("")){
                    mTitleEditText.setError("No title Entered");
                    return;
                }
                if(mDescEditText.getText().toString().trim().equals("")){
                    mDescEditText.setError("No description Entered");
                    return;
                }
                ParseObject parseObject = new Place();
                parseObject.put("lat",mCurrentLocation.getLatitude());
                parseObject.put("long",mCurrentLocation.getLongitude());
                parseObject.put("title",mTitleEditText.getText().toString());
                parseObject.put("desc",mDescEditText.getText().toString());

                if(Utils.isNetworkAvailable(AddLocationActivity.this)) {

                    Utils.showProgressDialogue(AddLocationActivity.this, "Saving Location");
                    parseObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Utils.dismissProgressDialogue();
                            Toast.makeText(AddLocationActivity.this , "Added Location", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                            }
                    });
                }
                else{
                    Toast.makeText(AddLocationActivity.this, "No Internet Available",Toast.LENGTH_LONG).show();
                }
            }
        });

        if (map!=null){

            map.setMyLocationEnabled(true);
            float scale = getResources().getDisplayMetrics().density;
            int dpAsPixels = (int) (205*scale + 0.5f);
            map.setPadding(0,0,0,dpAsPixels);
            UiSettings settings = map.getUiSettings();
            settings.setCompassEnabled(true);
            settings.setZoomControlsEnabled(true);
            settings.setMyLocationButtonEnabled(true);

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(MainActivity.BANGALORE, 12));

            buildGoogleApiClient();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        LocationServices.FusedLocationApi.requestLocationUpdates(
                                mGoogleApiClient, createLocationRequest(), new LocationListener() {
                                    @Override
                                    public void onLocationChanged(Location location) {
                                        if(location.hasAccuracy()) {
                                            mCurrentLocation = location;
                                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 12));
                                            map.animateCamera(CameraUpdateFactory.zoomTo(15), 1500, null);
                                            Toast.makeText(AddLocationActivity.this,
                                                    "Lat - " + location.getLatitude() +
                                                    " long - " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                                            mSaveButton.setEnabled(true);
                                            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                                        }
                                    }
                                });
                    }
                    @Override
                    public void onConnectionSuspended(int i) {
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                    }
                })
                .addApi(LocationServices.API)
                .build();
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1500);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }
}
