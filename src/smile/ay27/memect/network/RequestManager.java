package smile.ay27.memect.network;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import smile.ay27.memect.MemectApplication;

/**
 * Created by storm on 14-3-25.
 */
public class RequestManager {
    public static RequestQueue mRequestQueue = Volley.newRequestQueue(MemectApplication.appContext);

    private RequestManager() {
        // no instances
    }

    public static void addRequest(Request<?> request, Object tag) {
        if (tag != null) {
            request.setTag(tag);
        }
        mRequestQueue.add(request);
    }

    public static void cancelAll(Object tag) {
        mRequestQueue.cancelAll(tag);
    }
}
