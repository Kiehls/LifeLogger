package embedded.kookmin.ac.kr.lifelogger.Non_Activity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by 승수 on 2015-12-18.
 */
public class DbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "LifeLogger.sqlite";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "DailyLog";

    public DbHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( " +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT , " +
                "log TEXT, " +
                "date DATETIME NOT NULL DEFAULT getdate() , " +
                "type TEXT, " +
                "address TEXT, " +
                "lat REAL, " +
                "lng REAL);");

        Log.d("Table Created: ", "Main Table is created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //Insert Log into DB
    public void insertLog(String log, String type, String address, double lat, double lng) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("log", log);
        values.put("type", type);
        values.put("address", address);
        values.put("lat", lat);
        values.put("lng", lng);

        database.insert(TABLE_NAME, null, values);
    }

    public void changeLog(String changedLog) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE " + TABLE_NAME + " SET log = " + changedLog + ";";

        database.execSQL(sql);
    }

    public void deleteLog(int index) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "DELETE FROM DailyLog WHERE id = " + index;

        database.rawQuery(sql, null);
    }

    //Show stored DB in Android Monitor
    public void showDB() {
        String sql = "SELECT * FROM DailyLog";
        SQLiteDatabase database = getReadableDatabase();
        try {
            Cursor cursor = database.rawQuery(sql, null);
            cursor.moveToFirst();
            if(cursor != null && cursor.getCount() != 0) {
                while (!cursor.isAfterLast()) {
                    int id = cursor.getInt(0);
                    String log = cursor.getString(1);
                    String date = cursor.getString(2);
                    String type = cursor.getString(3);
                    String address = cursor.getString(4);
                    double lat = cursor.getDouble(5);
                    double lng = cursor.getDouble(6);
                    Log.d("Log List :", "ID = " + id + " Log : " + log + " Date : " + date + " type : " + type + " address : " + address + " Lat : " + lat + " Lng : " + lng);
                    cursor.moveToNext();
                }
            }
            else {
//                Toast.makeText(MainActivity.this, "No Logger", Toast.LENGTH_SHORT).show();
                Log.d("Database :", "No Database In DB");
            }
            cursor.close();
        } catch (Exception e) {
            Log.d("Log List Exception :", "Error in setAddress : " + e.toString());
        }
    }
}

