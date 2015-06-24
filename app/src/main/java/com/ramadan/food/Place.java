package com.ramadan.food;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Places")
public class Place extends ParseObject {

    public LatLng getLocation() {
        return new LatLng(getNumber("lat").floatValue(),getNumber("long").floatValue());
    }

    public String getTitle() {
        return getString("title");
    }

    public String getDescription() {
        return getString("desc");
    }
}