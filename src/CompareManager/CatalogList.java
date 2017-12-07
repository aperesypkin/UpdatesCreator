package CompareManager;

import java.io.File;
import java.util.Vector;

/**
 * Created by a.peresypkin on 17.11.2017.
 */
public class CatalogList {

    public Vector v;
    public int beginindex;
    public int ID=-1;
    public int DIRS=0;
    public int FILES=0;

    //*************Конструкторы************************************************************
    public CatalogList(File source){
        v = new Vector();
        beginindex = source.getAbsolutePath().length()+File.separator.length();
    }

    public CatalogList(){}

    //*************Функция получения списка объектов - содержимого, указанного в path******
    public Vector getFileList(File path,int PARENT){
        //создаем массив объектов-файлов
        File[] filelist;
        //заполняем его объектами - файлами и каталогами
        filelist = path.listFiles();
        /*рекурсия - заполняем вектор объектами FileObject(абсолютный_путь, отн_путь, родительский_каталог, признак_директории)*/
        for(int i=0;i<filelist.length;i++){
            if(filelist[i].isDirectory()){
                v.add(new FileObject(++ID,filelist[i],filelist[i].getAbsolutePath().substring(beginindex),
                        filelist[i].getParentFile(), filelist[i].isDirectory(),PARENT,null));
                DIRS++;
                getFileList(filelist[i], ID);
            }
            else {
                v.add(new FileObject(++ID,filelist[i],filelist[i].getAbsolutePath().substring(beginindex),
                        filelist[i].getParentFile(), filelist[i].isDirectory(),PARENT,null));
                FILES++;
            }
        }
        return v;
    }
    //*************Функция восстановления корректной структуры дельты(для сборки обновления
    public void RepairDelta(Vector src, Vector fordel){
        for(int i=0;i<src.size();i++){
            FileObject fo_src = (FileObject) src.get(i);
            if(fo_src.getPARENT_ID()>-1){
                boolean is_exist = false;
                for(int k=0;k<src.size();k++){
                    FileObject fo_src_prt = (FileObject) src.get(k);
                    if(fo_src_prt.getID()==fo_src.getPARENT_ID()){
                        is_exist=true;
                        break;
                    }
                }
                if(!is_exist){
                    for(int j=0;j<fordel.size();j++){
                        FileObject fo2 = (FileObject) fordel.get(j);
                        if(fo2.getID()==fo_src.getPARENT_ID()){
                            src.add(fo2);
                            fordel.remove(fo2);
                            break;
                        }
                    }
                    RepairDelta(src, fordel);
                }
            }
        }
        SortDelta(src);
    }
    //*************Функция сортировки дельты по ID элементов - для корректной сборки обновления
    public void SortDelta(Vector src){
        for(int i=src.size()-1;i>=0;i--){
            FileObject fo_src_i = (FileObject) src.get(i);
            for(int j=0;j<i;j++){
                FileObject fo_src_j_1 = (FileObject) src.get(j);
                FileObject fo_src_j_2 = (FileObject) src.get(j+1);
                if (fo_src_j_1.getID()>fo_src_j_2.getID()){
                    FileObject fobj_tmp = fo_src_j_1;
                    src.setElementAt(fo_src_j_2,j);
                    src.setElementAt(fobj_tmp,j+1);
                }
            }
        }
    }

}
