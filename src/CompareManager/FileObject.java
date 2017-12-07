package CompareManager;

import java.io.File;

/**
 * Created by a.peresypkin on 17.11.2017.
 */
public class FileObject implements java.io.Serializable {

    private int ID;
    private File OBJ;
    private String  RELPATH;
    private File    PARENT;
    private boolean ISDIR;
    private int PARENT_ID;
    private String STATUS;

    public FileObject(int ID,File OBJ, String RELPATH, File PARENT, boolean ISDIR, int PARENT_ID, String STATUS){
        this.ID = ID;
        this.OBJ = OBJ;
        this.RELPATH = RELPATH;
        this.PARENT = PARENT;
        this.ISDIR = ISDIR;
        this.PARENT_ID = PARENT_ID;
    }
    public FileObject(){};
    //*****************GET*****************************************
    public int getID(){
        return ID;
    }
    public File getOBJ(){
        return OBJ;
    }
    public String getRELPATH(){
        return RELPATH;
    }
    public File getPARENT(){
        return PARENT;
    }
    public boolean getISDIR(){
        return ISDIR;
    }
    public int getPARENT_ID(){
        return PARENT_ID;
    }
    public String getSTATUS(){
        return STATUS;
    }

    //*****************SET*****************************************
    public void setID(int ID){
        this.ID = ID;
    }
    public void setOBJ(File OBJ){
        this.OBJ = OBJ;
    }
    public void setRELPATH(String RELPATH){
        this.RELPATH = RELPATH;
    }
    public void setPARENT(File PARENT){
        this.PARENT = PARENT;
    }
    public void setISDIR(boolean ISDIR){
        this.ISDIR = ISDIR;
    }
    public void setPARENT_ID(int PARENT_ID){
        this.PARENT_ID = PARENT_ID;
    }
    public void setSTATUS(String STATUS){
        this.STATUS = STATUS;
    }

}
