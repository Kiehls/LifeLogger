package embedded.kookmin.ac.kr.lifelogger.Activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import embedded.kookmin.ac.kr.lifelogger.Non_Activity.DbHelper;
import embedded.kookmin.ac.kr.lifelogger.R;

public class ShowActivity extends AppCompatActivity implements View.OnClickListener{

    DbHelper db;
    SQLiteDatabase database;
    Cursor cursor;

    GoogleMap map;
    LatLng position;

    TextView sLog;
    TextView sDate;
    TextView sLocation;
    Button sBack;
    Button sDelete;

    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        findViewById();

        db = new DbHelper(this);
        database = db.getReadableDatabase();
        settings();

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        //Showing the current location in Google Map
        map.moveCamera(CameraUpdateFactory.newLatLng(position));

        //Map Zoom
        map.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);

        //Maker Setting
        MarkerOptions flag = new MarkerOptions();
        flag.position(position);
        flag.title("You were Here!");
        map.addMarker(flag).showInfoWindow();
    }

    //Override Button onClick Listener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sDelete :
                db.deleteLog(getIntent().getIntExtra("index", 0));
                finish();
                break;
            case R.id.sBack :
                finish();
                break;
        }
    }

    //Set Texts & Location
    public void settings() {
        String query = "SELECT * FROM DailyLog";
        cursor = database.rawQuery(query, null);
        cursor.moveToPosition(getIntent().getIntExtra("index", 0) - 1);
        Log.e("cursor position :", "" + cursor.getPosition() + 1);

        sLog.setText(cursor.getString(1));
        sDate.setText(cursor.getString(2));
        sLocation.setText(cursor.getString(4));

        latitude = cursor.getDouble(5);
        longitude = cursor.getDouble(6);
        position = new LatLng(latitude, longitude);
    }

    //Find all widgets
    public void findViewById() {
        sLog = (TextView) findViewById(R.id.sLog);
        sDate = (TextView) findViewById(R.id.sDate);
        sLocation = (TextView) findViewById(R.id.sLocation);
        sBack = (Button) findViewById(R.id.sBack);
        sDelete = (Button) findViewById(R.id.sDelete);
    }
}
