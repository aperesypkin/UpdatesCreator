package CompareManager;

import java.io.File;

/**
 * Created by a.peresypkin on 17.11.2017.
 */
public class Precompile {
    public int getOperations(File f1, File f2, boolean isArc) throws Exception{
        int count=getFilesNumber(f1,0);
        count+=getFilesNumber(f2,0);
        return (int) (count*(1.5));
    }
    public int getFilesNumber(File path,int count){
        //создаем массив объектов-файлов
        File[] filelist;
        //заполняем его объектами - файлами и каталогами
        filelist = path.listFiles();
        /*рекурсия - считаем количество объектов в каталоге*/
        for(int i=0;i<filelist.length;i++){
            if(filelist[i].isDirectory()){
                count++;
                count = getFilesNumber(filelist[i],count);
            }
            else count++;
        }
        return count;
    }
}
