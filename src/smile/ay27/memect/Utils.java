package smile.ay27.memect;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;

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


    public static boolean saveObject(Serializable ser, String fileName) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        File cacheDir = MemectApplication.appContext.getCacheDir();
        String filePath = cacheDir+"/"+fileName;

        try {
            fos = new FileOutputStream(filePath);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(ser);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                oos.close();
            } catch (Exception e) {
            }
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    public static Serializable loadObject(String fileName) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;

        File cacheDir = MemectApplication.appContext.getCacheDir();
        String filePath = cacheDir+"/"+fileName;

        try {
            fis = new FileInputStream(filePath);
            ois = new ObjectInputStream(fis);
            return (Serializable) ois.readObject();
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                ois.close();
            } catch (Exception e) {
            }
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return null;
    }

}
