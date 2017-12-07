package CompareManager;

import java.io.File;

/**
 * Created by a.peresypkin on 17.11.2017.
 */
public class Utils {
    public static void deleteDir(File file) throws Exception{
        if(file.isDirectory()){
            if(file.listFiles().length>0){
                File list[] = file.listFiles();
                for(int i=0;i<list.length;i++) deleteDir(list[i]);

                file.delete();
            }
            else file.delete();
        }
        else file.delete();
    }

}
