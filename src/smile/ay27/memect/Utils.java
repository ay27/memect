package smile.ay27.memect;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by ay27 on 15/10/23.
 */
public class Utils {

    private void parseCatalog() {
        new AsyncTask<Void, Void, Void>() {

            private StringBuilder sb;
            @Override
            protected Void doInBackground(Void... params) {
                Document doc = null;
                try {
                    doc = Jsoup.connect("http://memect.com/").get();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (doc == null) {
                    return null;
                }

                sb = new StringBuilder();
                Elements content = doc.getElementsByClass("block");
                for (Element block : content) {
                    Elements link = block.getElementsByTag("a");
                    String href = link.attr("href");
                    String title = link.select("span").get(0).text();
                    sb.append("\n").append(href).append(" ").append(title);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Log.i("ssss", sb.toString());
            }
        }.execute();
    }
}
