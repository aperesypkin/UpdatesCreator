package CompareManager;

import java.io.*;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Created by a.peresypkin on 18.11.2017.
 */
public class UtilsZIP {

    public static void createZIP(String path, String zipfile) throws IOException, IllegalArgumentException{
        File dir = new File(path);
        createZIP(dir, zipfile);
    }
/*
*   Create ZIP using File with setting value in progressbar
* */

    public static void createZIP(File dir, String zipfile) throws IOException, IllegalArgumentException{
        String parentDIR = dir.getParent();
        if (!dir.isDirectory()) throw new IllegalArgumentException("Not a directory:  "+ dir);
        Vector v = getList(dir,null);
        byte[] buffer = new byte[4096]; // Create a buffer for copying
        int bytesRead;

        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));

        for (int i = 0; i < v.size(); i++) {
            File f = (File) v.get(i);
            if(f.isFile()){
                FileInputStream in = new FileInputStream(f); // Stream to read file
                ZipEntry entry = new ZipEntry(f.getPath().substring(parentDIR.length())); // Make a ZipEntry
                out.putNextEntry(entry); // Store entry
                while ((bytesRead = in.read(buffer)) != -1) out.write(buffer, 0, bytesRead);
                in.close();
            }
        }
        out.close();
    }

    /*
    *   Get list of files for zipping
    * */
    private static Vector getList(File dir, Vector list){
        File[] filelist = dir.listFiles();
        if(list==null) list = new Vector();
        for(int i=0;i<filelist.length;i++){
            list.addElement(filelist[i]);
            if(filelist[i].isDirectory()) getList(filelist[i],list);
        }
        return list;
    }

    public static void unpack(String path, String dir_to) throws IOException {
        int pbarcount=0;
        ZipFile zip = new ZipFile(path);
        Enumeration entries = zip.entries();
        LinkedList<ZipEntry> zfiles = new LinkedList<ZipEntry>();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            if (entry.isDirectory()) {
                new File(dir_to+"/"+entry.getName()).mkdir();
            } else {
                zfiles.add(entry);
            }
        }
        for (ZipEntry entry : zfiles) {
            InputStream in = zip.getInputStream(entry);
            OutputStream out = new FileOutputStream(dir_to+"/"+entry.getName());
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) >= 0){
                out.write(buffer, 0, len);
                pbarcount++;
            }
            in.close();
            out.close();
        }
        zip.close();
    }
}
