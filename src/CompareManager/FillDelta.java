package CompareManager;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.Vector;

/**
 * Created by a.peresypkin on 17.11.2017.
 */
public class FillDelta {

    private String SEPARATOR = (File.separatorChar=='\\'?"\\\\":File.separator);

     public void create_deletedLST(File deltaDIR, Vector content) throws Exception{
        File file_lst = new File(deltaDIR.getAbsolutePath()+SEPARATOR+"!deleted.lst");
        File file_cmd = new File(deltaDIR.getAbsolutePath()+SEPARATOR+"/!kill_deleted.cmd");
        if(file_lst.exists()) file_lst.delete();
        //if(!file_lst.exists()){
        try{
            file_lst.createNewFile();
            FileOutputStream fos = new FileOutputStream(file_lst);
            DataOutputStream dos = new DataOutputStream(fos);
            for(int i=0;i<content.size();i++){
                FileObject fobj = (FileObject) content.get(i);
                dos.writeBytes(fobj.getRELPATH());
                dos.writeBytes("\n");
            }

            dos.close();
            fos.close();
        }catch(Exception e){
            //System.out.println(e);
        }
        //}
        if(file_cmd.exists()) file_cmd.delete();
        try{
            file_cmd.createNewFile();
            FileOutputStream fos = new FileOutputStream(file_cmd);
            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeBytes("for /f %%a in (!deleted.lst) do del \"%%a\"");
            dos.close();
            fos.close();
        }catch(Exception e){
            throw e;
        }
    }
    public void copyFile(File in, File out) throws IOException {
        FileChannel inChannel = new FileInputStream(in).getChannel();
        FileChannel outChannel = new FileOutputStream(out).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(),outChannel);
        }
        catch (IOException e) {
            throw e;
        }
        finally {
            if (inChannel != null) inChannel.close();
            if (outChannel != null) outChannel.close();
        }
    }
    public void copyFilesToDelta(Vector content, File delta) throws Exception{
        for(int i=0;i<content.size();i++){
            FileObject fobj = (FileObject) content.get(i);

            if(fobj.getISDIR()) {
                File dir = new File(delta.getAbsolutePath()+"/"+fobj.getRELPATH());
                dir.mkdir();
            }
        }
        for(int i=0;i<content.size();i++){
            FileObject fobj = (FileObject) content.get(i);
            if(!fobj.getISDIR()) {
                try{
                    File file = new File(delta.getAbsolutePath()+"/"+fobj.getRELPATH());
                    copyFile(fobj.getOBJ(),file);
                }catch(Exception e){
                    throw e;
                }
            }
        }
    }
    public void create_sqlLST(File delta_dir, Vector content) throws Exception{
        File sqlDIR = new File(delta_dir.getAbsolutePath()+SEPARATOR+"SQL");
        File perform_lst = new File(sqlDIR.getAbsolutePath()+SEPARATOR+"update_perform.lst");
        content = restoreLst(content,sqlDIR);
        File rollback_lst = new File(sqlDIR.getAbsolutePath()+SEPARATOR+"update_rollback.lst");
        if (!sqlDIR.exists()) {
            sqlDIR.mkdir();
        }
        if (perform_lst.exists()) {
            perform_lst.delete();
        }
        try {
            perform_lst.createNewFile();
            FileOutputStream fos = new FileOutputStream(perform_lst);
            DataOutputStream dos = new DataOutputStream(fos);
//            if(srvsqlb!=null&&srvsqlb.size()>0){
//                for(int i=0;i<srvsqlb.size();i++){
//                    String fname = (String) srvsqlb.get(i);
//                    dos.writeBytes("ServiceScript/"+fname+"\n");
//                }
//            }
            for(int i=0;i<content.size();i++) {
                FileObject fobj = (FileObject) content.get(i);
                dos.writeBytes(fobj.getOBJ().getName());
                if(i<content.size()-1)dos.writeBytes("\n");
            }
//            if(srvsqle!=null&&srvsqle.size()>0){
//                for(int i=0;i<srvsqle.size();i++){
//                    String fname = (String) srvsqle.get(i);
//                    dos.writeBytes("\nServiceScript/"+fname);
//                }
//            }
            dos.close();
            fos.close();
        }catch(Exception e){
            throw e;
        }
        if(rollback_lst.exists()) rollback_lst.delete();
        try{
            rollback_lst.createNewFile();
            FileOutputStream fos = new FileOutputStream(rollback_lst);
            DataOutputStream dos = new DataOutputStream(fos);
            dos.close();
            fos.close();
        }catch(Exception e){
            throw e;
        }
    }
    public void create_xmlLST(File deltaDIR, File targetbuildDIR, Vector content, Vector tfoXMLData,
                              Vector templateXMLData, Vector rolesXMLData, Vector testsXMLData) throws Exception{
        File xmlDIR = new File(deltaDIR.getAbsolutePath()+SEPARATOR+"XML");
        File updateLST = new File(xmlDIR.getAbsolutePath()+SEPARATOR+"@update.lst");
        String targetbuildXMLDIR = targetbuildDIR.getPath()+SEPARATOR+"XML";
        if(!xmlDIR.exists()) xmlDIR.mkdir();
        if(updateLST.exists()) updateLST.delete();
        Vector result = new Vector();
        Vector warn = new Vector();
        Vector xmlLST = getXMLLSTContent(new File(targetbuildDIR+SEPARATOR+"XML"+SEPARATOR+"@azk2_sys.lst"));
        create_tfoLST(deltaDIR,targetbuildDIR,content);
        //if(!mainform.isAZK2SysLst()){
        for(int i=0;i<xmlLST.size();i++){
            File f = (File)xmlLST.get(i);
            if(f.getName().matches(".*lst")){
                Vector lst = getLSTContent(f,new Vector());
                for(int j=0;j<content.size();j++){
                    FileObject fo = (FileObject)content.get(j);
                    File xmlFILE = fo.getOBJ();
                    int k=0;
                    while(k<lst.size()&&!xmlFILE.equals((File)lst.get(k))){
                        k++;
                    }
                    if(k!=lst.size()){
                        result.addElement(fo.getRELPATH().toString().substring(4));
                        fo.setSTATUS("isLIST");
                    }
                }
            }
            else{
                for(int j=0;j<content.size();j++){
                    FileObject fo = (FileObject)content.get(j);
                    File xmlFILE = fo.getOBJ();
                    if(xmlFILE.getName().trim().equals(f.getName().trim())){
                        result.addElement(f.getPath().substring(targetbuildXMLDIR.length()));
                        fo.setSTATUS("isLIST");
                        break;
                    }
                }
            }
        }
        for(int i=0;i<content.size();i++){
            FileObject fo = (FileObject)content.get(i);
            File xmlFILE = fo.getOBJ();
            if(xmlFILE.getAbsolutePath().contains("\\Importer\\")){
                result.addElement(fo.getRELPATH().substring(4));
                fo.setSTATUS("isLIST");
            }
        }
        //Заносим в @warn.lst все ХМЛ, которые не попали в проливку
        for(int i=0;i<content.size();i++){
            FileObject fo = (FileObject)content.get(i);
            if (fo.getSTATUS() != "isLIST") {
                warn.addElement(fo.getOBJ().toString().substring(targetbuildXMLDIR.length()));
            }
        }
        //   }
        try{
            updateLST.createNewFile();
            FileOutputStream fos = new FileOutputStream(updateLST);
            DataOutputStream dos = new DataOutputStream(fos);
            for(int i=0;i<result.size();i++){
                dos.writeBytes((String)result.get(i)+"\r\n");
            }
            if ((warn.size()!=0)||(tfoXMLData.size()!=0)||(templateXMLData.size()!=0)||(rolesXMLData.size()!=0)||(testsXMLData.size()!=0)){
                create_warn_file(xmlDIR, warn, tfoXMLData, templateXMLData, rolesXMLData, testsXMLData);
            }
            dos.writeBytes("@module.lst\r\n");
            dos.writeBytes("@azk2_rep.lst\r\n");
            dos.writeBytes("menu.xml\r\n");

            dos.close();
            fos.close();

        }catch(Exception e){
            //System.out.println(e);
        }
    }

    public void create_tfoLST(File deltaDIR, File targetbuildDIR, Vector content) throws Exception{
        File xmlDIR = new File(deltaDIR.getAbsolutePath()+SEPARATOR+"XML");
        File tfLST = new File(xmlDIR.getAbsolutePath()+SEPARATOR+"@tfoupdate.lst");
        String targetbuildXMLDIR = targetbuildDIR.getPath()+SEPARATOR+"XML";
        if(tfLST.exists()) tfLST.delete();
        Vector result = new Vector();
        Vector tfoLST = getXMLLSTContent(new File(targetbuildDIR+SEPARATOR+"XML"+SEPARATOR+"@tfo_sys.lst"));
        for(int i=0;i<tfoLST.size();i++){
            File f = (File)tfoLST.get(i);
            if(f.getName().matches(".*lst")){
                Vector lst = getLSTContent(f,new Vector());
                for(int j=0;j<content.size();j++){
                    FileObject fo = (FileObject)content.get(j);
                    File xmlFILE = fo.getOBJ();
                    int k=0;
                    while(k<lst.size()&&!xmlFILE.equals((File)lst.get(k))){
                        k++;
                    }
                    if(k!=lst.size()){
                        result.addElement(fo.getRELPATH().toString().substring(4));
                        fo.setSTATUS("isLIST");
                    }
                }
            }
            else{
                for(int j=0;j<content.size();j++){
                    FileObject fo = (FileObject)content.get(j);
                    File xmlFILE = fo.getOBJ();
                    if(xmlFILE.equals(f)){
                        result.addElement(f.getPath().substring(targetbuildXMLDIR.length()));
                        fo.setSTATUS("isLIST");
                        break;
                    }
                }
            }
        }
        try{
            tfLST.createNewFile();
            FileOutputStream fos = new FileOutputStream(tfLST);
            DataOutputStream dos = new DataOutputStream(fos);
            for(int i=0;i<result.size();i++){
                dos.writeBytes((String)result.get(i));
                dos.writeBytes("\n");
            }
            dos.close();
            fos.close();

        }catch(Exception e){
            //System.out.println(e);
        }
    }

    public void create_warn_file(File xmlDIR, Vector warn, Vector tfoXMLData, Vector templateXMLData, Vector rolesXMLData, Vector testsXMLData){
        try{
            File warnLST = new File(xmlDIR.getAbsolutePath()+SEPARATOR+"warning_xml.lst");
            if(warnLST.exists()) warnLST.delete();
            warnLST.createNewFile();
            FileOutputStream fosWarn = new FileOutputStream(warnLST);
            DataOutputStream dosWarn = new DataOutputStream(fosWarn);
            if (templateXMLData.size()!=0){
                dosWarn.writeBytes("------New Template:------");
                dosWarn.writeBytes("\n");
                for(int i=0;i<templateXMLData.size();i++){
                    dosWarn.writeBytes(((FileObject)templateXMLData.get(i)).getRELPATH().toString().substring(4)+"\r\n");
                }
            }
            if (rolesXMLData.size()!=0){
                dosWarn.writeBytes("------New Roles:------");
                dosWarn.writeBytes("\n");
                for(int i=0;i<rolesXMLData.size();i++){
                    dosWarn.writeBytes(((FileObject)rolesXMLData.get(i)).getRELPATH().toString().substring(4)+"\r\n");
                }
            }
            if (testsXMLData.size()!=0){
                dosWarn.writeBytes("------New Tests:------");
                dosWarn.writeBytes("\n");
                for(int i=0;i<testsXMLData.size();i++){
                    dosWarn.writeBytes(((FileObject)testsXMLData.get(i)).getRELPATH().toString().substring(4)+"\r\n");
                }
            }
            if (tfoXMLData.size()!=0){
                dosWarn.writeBytes("------New TFO:------");
                dosWarn.writeBytes("\n");
                for(int i=0;i<tfoXMLData.size();i++){
                    dosWarn.writeBytes(((FileObject)tfoXMLData.get(i)).getRELPATH().toString().substring(4)+"\r\n");
                }
            }
            if (warn.size()!=0){
                dosWarn.writeBytes("------Another files:------");
                dosWarn.writeBytes("\n");
                for(int i=0;i<warn.size();i++){
                    if (!warn.get(i).toString().contains("\\")){
                        dosWarn.writeBytes((String) warn.get(i)+"\r\n");
                    }
                }
            }
            dosWarn.close();
            fosWarn.close();

        }catch (Exception e){
            //System.out.println(e);
        }
    }
    public void create_versionXML(File delta_dir, String oldVERSION, String newVERSION){
        File versionXML = new File(delta_dir.getAbsolutePath()+SEPARATOR+"version.xml");
        if(versionXML.exists()) versionXML.delete();
        try{
            versionXML.createNewFile();
            FileOutputStream fos = new FileOutputStream(versionXML);
            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeBytes("<BUILD source_build=\""+oldVERSION+"\" target_build=\""+newVERSION+"\" model=\"standard\" remark=\"\" />");
            dos.close();
            fos.close();
        }catch(Exception e){
            //System.out.println(e);
        }
    }

    private Vector getXMLLSTContent(File lstfile){
        Vector lst = new Vector();
        try{
            FileInputStream fstream = new FileInputStream(lstfile);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null){
                if ((!strLine.startsWith(";"))&&(strLine.length() > 0)){
                    lst.addElement(new File(lstfile.getParent()+SEPARATOR+strLine.trim()));
                }
                //System.out.println();
            }
            in.close();
        }catch (Exception e){
            //System.err.println("Error: " + e.getMessage());
        }
        return lst;
    }
    public Vector getLSTContent(File f, Vector lst){
        try{
            FileInputStream fstream = new FileInputStream(f);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null){
                if(!strLine.startsWith(";")){
                    if(strLine.matches(".*lst")){
                        if(strLine.startsWith(".")){
                            strLine=strLine.substring(2);
                        }
                        if (!strLine.startsWith(";")){
                            String path = f.getParent()+SEPARATOR+strLine;
                            getLSTContent(new File(path.trim()),lst);
                        }
                    }
                    else{
                        String path = f.getParent()+SEPARATOR+strLine;
                        lst.addElement(new File(path.trim()));
                    }
                }
            }
            in.close();
        }catch (Exception e){
            //System.err.println("Error: " + e.getMessage());
        }
        return  lst;
    }
    //Восстанавливаем нормальный список проливки SQl
    private Vector restoreLst(Vector content, File sqlDIR){
        File performFile = new File(sqlDIR.getAbsolutePath()+SEPARATOR+"perform.lst");
        Vector content_perform = new Vector();
        Vector performLst = getLSTContent(performFile,new Vector());
        for(int i=0;i<performLst.size();i++){
            for(int j=0;j<content.size();j++){
                FileObject fo = (FileObject) content.get(j);
                //Проверяемая SQL
                String fileName = fo.getRELPATH().toString().substring(4);
                //SQL в perform.lst
                if(!performLst.get(i).toString().equals(sqlDIR.toString())) {
                    String fileInPerformList = performLst.get(i).toString().substring(sqlDIR.toString().length()+1);
                    if (fileInPerformList.equals(fileName)){
                        content_perform.add(fo);
                    }
                }
            }
        }
        return content_perform;
    }
}
