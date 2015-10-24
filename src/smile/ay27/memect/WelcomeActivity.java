package smile.ay27.memect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-17.
 */
public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.welcome);


        Timer time = new Timer();
        final Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        };
        time.schedule(task, 2000);
    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        int width = (findViewById(R.id.imageView)).getWidth();
//        int height = (findViewById(R.id.imageView)).getHeight();
//
//        Log.i("width", ""+width);
//        Log.i("height", ""+height);
//    }
}
