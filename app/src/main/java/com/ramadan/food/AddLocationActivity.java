package com.ramadan.food;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class AddLocationActivity extends AppCompatActivity {

    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;
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

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        mLatitudeTextView = (TextView) findViewById(R.id.latitudeTextView);
        mLongitudeTextView = (TextView) findViewById(R.id.longitudeTextView);
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

                final ProgressDialog progressDialog = new ProgressDialog(AddLocationActivity.this);
                progressDialog.setTitle("Saving Location");
                progressDialog.setCancelable(false);
                progressDialog.show();
                parseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        progressDialog.hide();
                        Toast.makeText(AddLocationActivity.this , "Added Location", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        if (map!=null){

            map.setMyLocationEnabled(true);
            UiSettings settings = map.getUiSettings();
            settings.setCompassEnabled(true);
            settings.setZoomControlsEnabled(true);
            settings.setMyLocationButtonEnabled(true);

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(MainActivity.BANGALORE, 12));

            Log.i("location","init api client");
            buildGoogleApiClient();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i("location","init api client2");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
//                        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
//                                mGoogleApiClient);
//                        if (mLastLocation != null) {
//                            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
//                            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
//                        }
                        Log.i("location","connected");
                        Toast.makeText(AddLocationActivity.this,"Connected",Toast.LENGTH_SHORT).show();
                        LocationServices.FusedLocationApi.requestLocationUpdates(
                                mGoogleApiClient, createLocationRequest(), new LocationListener() {
                                    @Override
                                    public void onLocationChanged(Location location) {
                                        //if(location.hasAccuracy()) {
                                            mCurrentLocation = location;
                                            Toast.makeText(AddLocationActivity.this,
                                                    "Lat - " + location.getLatitude() +
                                                    " long - " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                                            mLatitudeTextView.setText("Lat - " + location.getLatitude());
                                            mLongitudeTextView.setText("long - " + location.getLongitude());
                                            mSaveButton.setEnabled(true);
                                            LocationServices.FusedLocationApi.removeLocationUpdates(
                                                    mGoogleApiClient, this);
                                        //}
                                    }
                                });
                    }
                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.i("location","suspended");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.i("location","failed");
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
