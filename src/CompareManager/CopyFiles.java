package CompareManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by a.peresypkin on 17.11.2017.
 */
public class CopyFiles {

    public static void copyFile(File in, File out) throws IOException {
        int pbarcount=0;
        byte[] buffer = new byte[4096];
        int bytesRead;
        FileInputStream inn = new FileInputStream(in);
        FileOutputStream fileOut = new FileOutputStream(out);
        while ((bytesRead = inn.read(buffer)) != -1) fileOut.write(buffer, 0, bytesRead);
        pbarcount++;
        inn.close();
    }

    public static boolean scannerDir(String dir1) {
        try {
            int i = 0;
            File[] list = new File(dir1).listFiles();
            if (list.length > -1){
                for (i = 0; i < list.length; i++) {
                    File f = new File (dir1 +"//"+ list[i].getName());
                    if ((f.toString().contains("server.zip"))
                            ||((f.toString().contains("client.zip")))){
                        return true;
                    };
                }}}
        catch (NullPointerException e){
            System.err.println("Not found zip file in: " + dir1);
        }
        return false;
    }
}
