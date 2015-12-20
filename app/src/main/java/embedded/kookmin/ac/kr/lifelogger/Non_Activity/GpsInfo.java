package embedded.kookmin.ac.kr.lifelogger.Non_Activity;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * Created by 승수 on 2015-12-18.
 */
public class GpsInfo extends Service implements LocationListener{

    private final Context context;

    // Current GPS state
    boolean isGPSEnabled = false;

    // Network state
                    boolean isNetworkEnabled = false;

                    // GPS state vlaue
                    boolean isGetLocation = false;

                    Location location;
                    double lat; // Latitude
                    double lon; // Longitude

                    // GPS Data Update Distance 5Meters
                    private static final long MIN_DISTANCE_UPDATES = 5;

                    // GPS Data Update Time 1/1000
                    private static final long MIN_TIME_UPDATES = 1000 * 60 * 1;

                    protected LocationManager locationManager;

                    public GpsInfo(Context context) {
                        this.context = context;
                        getLocation();
                    }

                public Location getLocation() {
                    try {
                        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

                        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                        Log.d("GpsProvider :", "isGPSEnabled = " + isGPSEnabled);
                        Log.d("NetworkProvider :", "isNetworkEnabled : " + isNetworkEnabled);

                        if (!isGPSEnabled && !isNetworkEnabled) {
                            showSettingsAlert();
                        }
                        else {
                            if (isGPSEnabled == false || isNetworkEnabled == false) {
                                showSettingsAlert();
                            } else {
                                this.isGetLocation = true;
                                if (isNetworkEnabled) {
                                    locationManager.requestLocationUpdates(
                                            LocationManager.NETWORK_PROVIDER,
                                            MIN_TIME_UPDATES,
                                            MIN_DISTANCE_UPDATES, this);

                                    if (locationManager != null) {
                                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                        if (location != null) {
                                            //Store Latitude & Longitude
                                            lat = location.getLatitude();
                                            lon = location.getLongitude();
                                        }
                                    }
                                }

                    if (isGPSEnabled) {
                        if (location == null) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_UPDATES,
                                    MIN_DISTANCE_UPDATES,
                                    this);
                            if (locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    lat = location.getLatitude();
                                    lon = location.getLongitude();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    //Close GPS
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GpsInfo.this);
        }
    }
    public double getLatitude() {
        if (location != null) {
            lat = location.getLatitude();
        }
        return lat;
    }
    public double getLongitude() {
        if (location != null) {
            lon = location.getLongitude();
        }
        return lon;
    }

    public boolean isGetLocation() {
        return this.isGetLocation;
    }

    //GPS 정보를 가져오지 못했을때 설정값으로 갈지 물어보는 alert 창
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle("GPS Setting");
        alertDialog.setMessage("GPS 셋팅이 되지 않았을수도 있습니다.\n 설정창으로 가시겠습니까?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }
}
