package smile.ay27.memect;

import android.app.Application;
import android.content.SharedPreferences;

/**
 * Created by ay27 on 15/10/23.
 */
public class MemectApplication extends Application {

    public static final String KEY_HAS_PIC = "has_pic";
    private static final String TAG = "Memect";
    private static final String prefs = "memect";
    public static MemectApplication appContext;
    public static SharedPreferences pref;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        pref = getSharedPreferences(prefs, MODE_PRIVATE);
    }

}
