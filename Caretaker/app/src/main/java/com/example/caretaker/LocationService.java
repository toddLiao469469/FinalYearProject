package com.example.caretaker;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationService extends Service {

    Double getlatitude;
    Double getlongitude;
    String getlocation;
    //是否已開啟定位服務


    private double currentLatitude = 0;
    private double currentLongitude = 0;
    private LocationManager mLocationManager;
    private static final int LOCATION_UPDATE_MIN_DISTANCE = 5;
    private static final int LOCATION_UPDATE_MIN_TIME = 50;


    public class LocalBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }

    private LocalBinder mLocBin = new LocalBinder();

    @Override
    public IBinder onBind(Intent arg0) {
        return mLocBin;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getCurrentLocation();

        return super.onStartCommand(intent, flags, startId);
    }


    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location location = null;
        if (!(isGPSEnabled || isNetworkEnabled)) {
            // location_provider error
        } else {
            if (isNetworkEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_MIN_TIME,
                        LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
                location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            } else if (isGPSEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_MIN_TIME,
                        LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
                location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
        if (location != null) {
            getAddressLocation(location);
        }
    }


    private void getAddressLocation(Location location) {    //將定位資訊顯示在畫面中


        if (location != null) {

            Geocoder gc = new Geocoder(getApplicationContext(), Locale.TRADITIONAL_CHINESE);
            List<Address> addressLocation = null;

            Double longitude = location.getLongitude();    //取得經度
            Double latitude = location.getLatitude();    //取得緯度

            try {
                addressLocation = gc.getFromLocation(latitude, longitude, 1); //放入座標
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.getlatitude = latitude;
            this.getlongitude = longitude;
            //轉換地址
            if (addressLocation != null && addressLocation.size() > 0) {
                Address address = addressLocation.get(0);
                String addressText = String.format("%s-%s%s%s%s",
                        address.getCountryName(), //國家
                        address.getAdminArea(), //城市
                        address.getLocality(), //區
                        address.getThoroughfare(), //路
                        address.getSubThoroughfare() //巷號
                );

                addressText = addressText.replace("null", "");
                this.getlocation = addressText;
            }

            Log.d("LocationServiceSendData", "latitude:" + getlatitude + " longitude:" + getlongitude + " address:" + getlocation);
            sendBroadcast(new Intent().setAction("Latitude_Data_Receive").putExtra("latitude", getlatitude.toString()));
            sendBroadcast(new Intent().setAction("Longitude_Data_Receive").putExtra("longitude",getlongitude.toString()));
            sendBroadcast(new Intent().setAction("Location_Data_Receive").putExtra("location", getlocation));
        } else {
            Toast.makeText(getApplicationContext(), "無法定位座標", Toast.LENGTH_LONG).show();
        }
    }

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                getAddressLocation(location);
                Log.d("8ug", "location Changed");
            } else {
                // Logger.d("Location is null");
                Toast.makeText(getApplicationContext(), "Location is null", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.d("8ug", "Status Changed");
        }

        @Override
        public void onProviderEnabled(String s) {
            Log.d("8ug", "onProviderEnabled Changed");
        }

        @Override
        public void onProviderDisabled(String s) {
            Log.d("8ug", "onProviderDisabled Changed");
        }
    };
}
