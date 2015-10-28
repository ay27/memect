package smile.ay27.memect.persistent;

import smile.ay27.memect.MemectApplication;

import java.io.*;

/**
 * Created by ay27 on 15/10/28.
 */
public class Utils {



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
