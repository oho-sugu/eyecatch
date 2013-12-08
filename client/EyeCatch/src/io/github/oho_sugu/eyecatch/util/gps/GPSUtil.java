package io.github.oho_sugu.eyecatch.util.gps;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

public class GPSUtil implements LocationListener {
	private LocationManager mLocationManager;
	private boolean getGPSValue = false;
	private double lat, lon;

	public boolean isTookCoord() {
		return getGPSValue;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public void onCreate(Activity parent) {
		mLocationManager = (LocationManager) parent
				.getSystemService(Activity.LOCATION_SERVICE);
	}

	public void onResume() {
		if (mLocationManager != null) {
			mLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, this);
		}
	}

	public void onPause() {
		if (mLocationManager != null) {
			mLocationManager.removeUpdates(this);
		}
	}

	public void onLocationChanged(Location location) {
		lat = Double.valueOf(location.getLatitude());
		lon = Double.valueOf(location.getLongitude());
		getGPSValue = true;
	}

	public void onProviderEnabled(String provider) {

	}

	public void onProviderDisabled(String provider) {

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
}
