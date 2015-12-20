package embedded.kookmin.ac.kr.lifelogger.Non_Activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import embedded.kookmin.ac.kr.lifelogger.Activity.LogActivity;
import embedded.kookmin.ac.kr.lifelogger.R;

/**
 * Created by 승수 on 2015-12-19.
 */
public class CurrentMap extends android.support.v4.app.Fragment{

    DbHelper db;
    SQLiteDatabase database;
    GpsInfo gps;

    MapView mapView;
    GoogleMap map;
    LatLng position;

    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DbHelper(getActivity());
        database = db.getReadableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.currentmap, container, false);
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            Log.d("Map Error :" , "Error is " + e.toString());
        }
        map = mapView.getMap();

        setPosition();

        return view;
    }

    //Get GPS Information
    private void setPosition() {

        //Gps Info
        gps = new GpsInfo(getActivity());
        if(gps.isGetLocation()) {
            Log.d("My Location :", "Gps is working");
            final double latitude = gps.getLatitude();
            final double longitude = gps.getLongitude();

            //Create a LatLng Object for the current location
            position = new LatLng(latitude, longitude);

            //Showing the current location in Google Map
            map.moveCamera(CameraUpdateFactory.newLatLng(position));

            //Map Zoom
            map.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);

            //Maker Setting
            MarkerOptions flag = new MarkerOptions();
            flag.position(position);
            flag.title("You are Here!");
            map.addMarker(flag).showInfoWindow();

            //Maker Click Listener
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Intent intent = new Intent(getActivity(), LogActivity.class);
                    intent.putExtra("lat", latitude);
                    intent.putExtra("lng", longitude);
                    startActivity(intent);

                    return false;
                }
            });
        }
        else {
            Log.e("My Location :", "Gps is not working");
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
