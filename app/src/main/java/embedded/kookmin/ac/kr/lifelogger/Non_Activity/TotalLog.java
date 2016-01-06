package embedded.kookmin.ac.kr.lifelogger.Non_Activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

import embedded.kookmin.ac.kr.lifelogger.Activity.ShowActivity;
import embedded.kookmin.ac.kr.lifelogger.R;

/**
 * Created by 승수 on 2015-12-19.
 */
public class TotalLog extends android.support.v4.app.Fragment implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener, AdapterView.OnItemClickListener{

    DbHelper db;
    SQLiteDatabase database;
    Cursor cursor;
    GpsInfo gps;

    View view;
    ListView logView;
    ArrayList<String> logList;
    ArrayList<Integer> logId;
    ArrayList<Double> logDist;
    ArrayAdapter<String> logAdapter;

    Button bDoing;
    Button bEvents;
    RadioGroup radioGroup;
    RadioButton recent;
    RadioButton distance;

    String type;
    double cLatitude;
    double cLongitude;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DbHelper(getActivity());
        database = db.getReadableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.totallog, container, false);

        findViewById();

        logList = new ArrayList<String>();
        logId = new ArrayList<Integer>();
        logDist = new ArrayList<Double>();
        logAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, logList);
        logView.setOnItemClickListener(this);

        bDoing.setOnClickListener(this);
        bEvents.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(this);

        return view;
    }

    //Override Item onClick Listener
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), ShowActivity.class);
        int index = logId.get(position);
        Log.d("index id :", "" + index);
        intent.putExtra("index", index);
        startActivity(intent);
    }

    //Override Button onClick Listener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tDoing :
                logList.clear();
                logId.clear();
                logDist.clear();
                if(type == null) {
                    Toast.makeText(getActivity(), "Choose Recent or Distance Button", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (type.equals("recent")) { //시간순 + 행동
                        String query = "SELECT * FROM DailyLog WHERE type = 'doing' ORDER BY id DESC";
                        Log.d("Log Type : ", "Doing Recent");
                        insertLogRecent(query);
                    } else { //거리순 + 행동
                        String query = "SELECT * FROM DailyLog WHERE type = 'doing' "; //fix
                        Log.d("Log Type : ", "Doing Distance");
                        insertLogDistance(query);
                    }
                }
                logView.setAdapter(logAdapter);
                break;
            case R.id.tEvents :
                logList.clear();
                logId.clear();
                logDist.clear();
                if(type == null) {
                    Toast.makeText(getActivity(), "Choose Recent or Distance Button", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (type.equals("recent")) { //시간순 + 사건
                        String query = "SELECT * FROM DailyLog WHERE type = 'event' ORDER BY id DESC";
                        Log.d("Log Type :", "Events Recent");
                        insertLogRecent(query);
                    } else { //거리순 + 사건
                        String query = "SELECT * FROM DailyLog WHERE type = 'event' "; //fix
                        Log.d("Log Type : ", "Events Distance");
                        insertLogDistance(query);
                    }
                }
                logView.setAdapter(logAdapter);
                break;
        }
    }

    //Override Radio onChecked Listener
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.tRecent: //시간순
                type = "recent";
                break;
            case R.id.tDistance: //현재 거리 비교 거리순
                type = "distance";
                break;
        }
    }

    public void insertLogRecent(String query) {
        try {
            cursor = database.rawQuery(query, null);
            cursor.moveToFirst();

            while(!cursor.isAfterLast()) {
                String log = cursor.getString(1);
                int id = cursor.getInt(0);
                Log.d("Selectd Log List : ", "id :" + id + ", " + "log :" + log);

                logId.add(id);
                logList.add(log);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.d("Log Error : ", "cursor Error " + e.toString());
        }
    }

    public void insertLogDistance(String query) { //fix
        try {
            cursor = database.rawQuery(query, null);
            cursor.moveToFirst();

            gps = new GpsInfo(getActivity()); //Get current location
            if(gps.isGetLocation()) {
                Log.d("i have location", "success");
                cLatitude = gps.getLatitude();
                cLongitude = gps.getLongitude();
            }
            Location locationCurrent = new Location("current location");
            locationCurrent.setLatitude(cLatitude);
            locationCurrent.setLongitude(cLongitude);

            while(!cursor.isAfterLast()) {
                String log = cursor.getString(1);
                int id = cursor.getInt(0);
                double pLatitude = cursor.getDouble(5);
                double pLongitude = cursor.getDouble(6);
                Log.d("Log List : ", "id :" + id + ", " + "log :" + log + ", " + "lat :" + pLatitude + ", " + "lng :" + pLongitude);

                Location locationPast = new Location("past location"); //Get past location
                locationPast.setLatitude(pLatitude);
                locationPast.setLongitude(pLongitude);

                double distance = locationCurrent.distanceTo(locationPast); //거리는 계산 잘 되서 저장됨. proble is how to sort that distance with id?

                logDist.add(distance);
                logId.add(id);
                cursor.moveToNext();
            }
            sort(logDist, logId);// problem solved!
            String sql = "SELECT * FROM DailyLog";
            cursor = database.rawQuery(sql, null);
            cursor.moveToFirst();
            for(int i = 0; i < logId.size(); i++) {
                cursor.moveToPosition(logId.get(i) - 1);
                String log = cursor.getString(1);
                Log.d("in Log : ", log);

                logList.add(log);
            }
            cursor.close();

        } catch (Exception e) {
            Log.d("Log Error : ", e.toString());
        }
    }

    public void sort(ArrayList<Double> dist, ArrayList<Integer> id) { //confirm that  sorting is working rightly
        for(int i = 0; i < dist.size(); i++) {
            Log.e("sort :", "" + dist.get(i) + ", " + id.get(i));
        }
        for(int i = dist.size() - 1; i > 0; i--) {
            for(int j = 0; j < i ; j++) {
                if(dist.get(j) > dist.get(j + 1))
                    swap(dist, id, j, j + 1);
            }
        }
        System.out.println("After Sorting : ");
        for(int v = 0; v < dist.size(); v++) {
            System.out.println(dist.get(v) + ", " + id.get(v));
        }
    }

    public void swap(ArrayList<Double> dist, ArrayList<Integer> id, int indx1, int indx2) {
        double tempDist = dist.get(indx1);
        int tempId = id.get(indx1);

        dist.set(indx1, dist.get(indx2));
        dist.set(indx2, tempDist);
        id.set(indx1, id.get(indx2));
        id.set(indx2, tempId);
    }

    //Find all widgets
    public void findViewById() {
        bDoing = (Button) view.findViewById(R.id.tDoing);
        bEvents = (Button) view.findViewById(R.id.tEvents);
        radioGroup = (RadioGroup) view.findViewById(R.id.tRadioGroup);
        recent = (RadioButton) view.findViewById(R.id.tRecent);
        distance = (RadioButton) view.findViewById(R.id.tDistance);
        logView = (ListView) view.findViewById(R.id.logList);
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//
//        if(type == "recent") {
//            String query = "SELECT * FROM DailyLog WHERE type = 'doing' ORDER BY id DESC";
//            insertLogRecent(query);
//        }
//        else {
//            String query = "SELECT * FROM DailyLog WHERE type = 'doing' ORDER BY id ASC";
//            insertLogDistance(query);
//        }
//        logAdapter.notifyDataSetChanged();
//    }
}