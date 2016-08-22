package com.sunner.hurry;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by sunner on 2015/10/30.
 */
public class HurryLocationListener implements LocationListener {
    @Override
    public void onLocationChanged(Location location) {
        MapsActivity.myLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
