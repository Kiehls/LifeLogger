package embedded.kookmin.ac.kr.lifelogger.Activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import embedded.kookmin.ac.kr.lifelogger.Non_Activity.CurrentMap;
import embedded.kookmin.ac.kr.lifelogger.Non_Activity.DbHelper;
import embedded.kookmin.ac.kr.lifelogger.Non_Activity.TotalLog;
import embedded.kookmin.ac.kr.lifelogger.R;

public class MainActivity extends FragmentActivity {

    public DbHelper db;
    public SQLiteDatabase database;

    int MAX_PAGE = 2;
    Fragment cur_fragment = new Fragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(this, SplashActivity.class)); //Loading page

        //DB Open
        db = new DbHelper(this);
        db.showDB();

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new FragmentAdapter((getSupportFragmentManager())));
    }
    //Use Fragment to Slide (CurrentMap or TotalLog)
    protected class FragmentAdapter extends FragmentPagerAdapter {
        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position < 0 || MAX_PAGE <= position)
                return null;
            switch (position) {
                case 0:
                    cur_fragment = new CurrentMap();
                    break;
                case 1:
                    cur_fragment = new TotalLog();
                    break;
            }
            return cur_fragment;
        }

        public int getCount() {
            return MAX_PAGE;
        }
    }



    @Override
    protected void onRestart() {
        super.onRestart();
        db.showDB();
        Log.d("Restart :", "MainActivity Restart!");
    }
}
