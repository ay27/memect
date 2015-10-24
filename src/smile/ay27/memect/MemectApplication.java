package smile.ay27.memect;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import smile.ay27.memect.network.LruBitmapCache;

/**
 * Created by ay27 on 15/10/23.
 */
public class MemectApplication extends Application {

    private static final String TAG = "Memect";
    public static MemectApplication appContext;
    private static final String prefs = "memect";
    public static SharedPreferences pref;
    public static final String KEY_HAS_PIC = "has_pic";

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        pref = getSharedPreferences(prefs, MODE_PRIVATE);
    }

}
