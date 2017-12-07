package CompareManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created by a.peresypkin on 17.11.2017.
 */
public class CompareTwoFiles {
    int count=0;

    ////////////////////////////////////////////////////////////////////////////////////////
    public boolean compare(String path1,String path2) throws Exception{
        boolean     equal;
        int         bytefile1,bytefile2;
//*************Initialization****************************
        equal = false;
//*************Open files**************************************************************
        try{
            File f1 = new File(path1);
            File f2 = new File(path2);
            long filesize1 = f1.length();
            long filesize2 = f2.length();
//*************Step1 - Compare by content**********************************************
            if((f1.isFile()&&f2.isFile())&&(filesize1==filesize2)){
                FileInputStream fis1 = new FileInputStream(f1);
                FileInputStream fis2 = new FileInputStream(f2);
                BufferedInputStream bis1 = new BufferedInputStream(fis1,1024);
                BufferedInputStream bis2 = new BufferedInputStream(fis2,1024);
                do{
                    bytefile1 = bis1.read();
                    bytefile2 = bis2.read();
                }while(!(bytefile1==-1&&bytefile2==-1)&&bytefile1==bytefile2);
                if(!(bytefile1==-1&&bytefile2==-1)) equal=false;
                else equal=true;
                bis1.close();
                bis2.close();
                fis1.close();
                fis2.close();
            }
//*************Close files*************************************************************
        }catch(Exception e){
            throw e;
        }
        return equal;
    }
}
