package embedded.kookmin.ac.kr.lifelogger.Activity;

import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import embedded.kookmin.ac.kr.lifelogger.Non_Activity.DbHelper;
import embedded.kookmin.ac.kr.lifelogger.*;

public class LogActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener{

    DbHelper db;
    SQLiteDatabase database;

    EditText editLog;
    TextView editLocation;
    Button bInsert;
    Button bBack;
    RadioGroup rGroup;
    RadioButton doing;
    RadioButton event;

    String type = null;
    String address;
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        findViewById();

        db = new DbHelper(this);
        database = db.getWritableDatabase();
        latitude = getIntent().getExtras().getDouble("lat");
        longitude = getIntent().getExtras().getDouble("lng");

        address = getAddress(latitude, longitude);
        editLocation.setText(address);

        bInsert.setOnClickListener(this);
        bBack.setOnClickListener(this);

        rGroup.setOnCheckedChangeListener(this);
    }

    //Override Button onClick Listener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_ok:
                try {
                    if (type == null) {
                        Toast.makeText(getApplicationContext(), "Choose Act or Event", Toast.LENGTH_SHORT).show();
                    } else {
                        String input = editLog.getText().toString();
                        db.insertLog(input, type, address, latitude, longitude);

                        Toast.makeText(getApplicationContext(), "Storing Life Log is Completed.", Toast.LENGTH_SHORT).show();
                        type = null;
                        finish();
                    }
                }catch (Exception e){}
                break;
            case R.id.button_cancel:
                finish();
                break;
        }
    }

    // Type Setting RadioBox
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radioDoing :
                type = "doing";
                break;
            case R.id.radioEvent :
                type = "event";
                break;
        }
    }

    //Get Address
    private String getAddress(double latitude, double longitude) {
        String str = null;
        Address address = null;
        List<Address> add = null;
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);

        try {
            add = geocoder.getFromLocation(latitude, longitude, 1);
            address = add.get(0);
            str = address.getAddressLine(0);
        } catch (IOException e) {
            Log.d("Address Error : ", "Error occured!! " + e.toString());
        }
        return str;
    }

    //Find all widgets
    private void findViewById() {
        editLog = (EditText) findViewById(R.id.edit_Log);
        editLocation = (TextView) findViewById(R.id.edit_Location);
        bInsert = (Button) findViewById(R.id.button_ok);
        bBack = (Button) findViewById(R.id.button_cancel);
        rGroup = (RadioGroup) findViewById(R.id.radioGroup);
        doing = (RadioButton) findViewById(R.id.radioDoing);
        event = (RadioButton) findViewById(R.id.radioEvent);
    }
}
