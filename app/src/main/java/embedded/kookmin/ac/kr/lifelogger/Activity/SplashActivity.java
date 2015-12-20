package embedded.kookmin.ac.kr.lifelogger.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import embedded.kookmin.ac.kr.lifelogger.R;

/**
 * Created by 승수 on 2015-12-19.
 */
public class SplashActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler hd = new Handler();
        hd.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish(); //After 5seconds move to MainAcitivity
            }
        }, 3000);
    }
}
