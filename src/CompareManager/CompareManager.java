package CompareManager;

import Helpers.Log;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by a.peresypkin on 17.11.2017.
 */
public class CompareManager {

    private String SEPARATOR = (File.separatorChar=='\\'?"\\\\":File.separator);
    private String FROM;
    private String TO;
    private String UPDATE_DIR;

    public void compare(String[] fromBuilds, String toBuild, String fromPath, String toPath) {

        Log.println("Будут созданы следующие обновления:");
        for (String fromBuild : fromBuilds) {
            Log.println(fromBuild + "_to_" + toBuild);
        }
        System.out.print("\n");

        toPath = toPath + "\\updates";

        File toPathDir = new File(toPath);

        if (toPathDir.exists()) {
            try {
                Log.println("Удаляем директорию " + toPathDir.getPath() + "\n");
                Utils.deleteDir(toPathDir);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        try {
            Log.println("Создаем директорию " + toPathDir.getPath() + "\n");
            toPathDir.mkdirs();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        this.UPDATE_DIR = toPath;

        for (String fromBuild : fromBuilds) {
            this.FROM = fromPath + "\\" + fromBuild;
            this.TO = fromPath + "\\" + toBuild;

            Log.println("Начинаем создание обновления " + fromBuild + "_to_" + toBuild);
            buildUpdate(this.FROM, this.TO, this.UPDATE_DIR);
            Log.println("Обновление " + fromBuild + "_to_" + toBuild + " создано\n");
        }

        File tempDir = new File(this.UPDATE_DIR + "\\Builds");
        try {
            Log.println("Удаляем директорию " + tempDir.getPath() + "\n");
            Utils.deleteDir(tempDir);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void buildUpdate(String FROM, String TO, String UPDATE_DIR){
        try {
            //Если билды в архивах, то нужно сначала распаковать билды и перопределить папки
            if (CopyFiles.scannerDir(FROM) && (CopyFiles.scannerDir(TO))){
                copyBuildFiles(FROM, TO, UPDATE_DIR);
            } else {
                Log.println("Не найдены директории " + FROM + " или " + TO);
                System.exit(1);
            }
            //путь до каталога со старой сборкой
            File f1 = new File(this.FROM); //!!!Здесь путь с какого билда собирать
            //путь до каталога с новой сборкой
            File f2 = new File(this.TO); //!!!Здесь путь на какой билд собирать
            //приблизительный подсчет количества выполняемых операций сборки обновления
            //необходимо для прогрессбара
            Precompile pc = new Precompile();
            //Объявление векторов с содержимым каталогов
            Vector old_dir, new_dir;
            //Объявление и создание векторов, куда будем складывать объекты для удаления из векторов old_dir, new_dir
            Vector del_from_old = new Vector();
            Vector del_from_new = new Vector();
            int edit_sql = 0;
            int servicesql = 0;
            int servicexml = 0;
            Vector editSQLData = new Vector();
            Vector srvSQLData = new Vector();
            Vector srvXMLData = new Vector();
            Vector templateXMLData = new Vector();
            Vector rolesXMLData = new Vector();
            Vector testsXMLData = new Vector();
            Vector tfoXMLData = new Vector();
            //Получаем содержимое каталогов старой и новой версий
            CatalogList old_clist = new CatalogList(f1);
            old_dir = old_clist.getFileList(f1,-1);
            CatalogList new_clist = new CatalogList(f2);
            new_dir = new_clist.getFileList(f2,-1);

            //Запускаем пошаговое сравнение содержимого каталогов
            Log.println("Начинаем сравнение билдов перед созданием дельты");
            CompareTwoFiles ctf = new CompareTwoFiles();
            //System.out.println(res1.size()+" "+res2.size());
            for (int i = 0; i < new_dir.size(); i++) {
                FileObject new_fo = (FileObject) new_dir.get(i);
                for (int j = 0; j < old_dir.size(); j++) {
                    FileObject old_fo = (FileObject) old_dir.get(j);
                    //Проверяем равенство относительных путей объектов-файлов(включая название объекта)
                    if (new_fo.getRELPATH().equalsIgnoreCase(old_fo.getRELPATH())) {
                        //1.Если относительные пути и названия равны, проверяем признак директории
                        if (!(new_fo.getISDIR() || old_fo.getISDIR())) {
                            //1.1.Если оба объекта - файлы, то запускаем проверку содержимого
                            if (ctf.compare(new_fo.getOBJ().getAbsolutePath(), old_fo.getOBJ().getAbsolutePath())) {
                                //1.1.1.Если файлы равны, то добавляем их в коллекции для удаления
                                del_from_old.add(old_fo);
                                del_from_new.add(new_fo);
                            } else {
                                //1.1.2.Если файлы не равны, то файл в старой сборке удаляем
                                del_from_old.add(old_fo);
                                new_fo.setSTATUS("isDIFF");
                                new_dir.set(i,new_fo);
                            }
                        } else if (new_fo.getISDIR() && old_fo.getISDIR()) {
                            //1.2.Если оба объекта - каталоги, то оба каталога добавляем в коллекции для удаления
                            del_from_old.add(old_fo);
                            del_from_new.add(new_fo);
                        } else {
                            //1.3.Если один из объектов каталог, а другой - файл, то объект в новой версии оставляем, в старой удаляем
                            del_from_old.add(old_fo);
                        }
                    }
                }
            }

            //Удаляем из векторов старой и новой версий объекты, помеченные для удаления в коллекциях del_from_old и del_from_new
            old_dir.removeAll(del_from_old);
            new_dir.removeAll(del_from_new);
            //Присваиваем файлам в new_dir, не имеющим статуса, статус isNEW
            for (int i = 0; i < new_dir.size(); i++){
                FileObject fo = (FileObject) new_dir.get(i);
                if (fo.getSTATUS() == null) {
                    fo.setSTATUS("isNEW");
                }
            }
            //Восстанавливаем корректную структуру дельты(дельтой является содержимое вектора нового каталога)
            new_clist.RepairDelta(new_dir, del_from_new);
            int new_files = 0;
            Vector sqlLST = new Vector();
            Vector xmlLST = new Vector();
            for (int i = 0; i < new_dir.size(); i++){
                FileObject fo = (FileObject) new_dir.get(i);
                if (fo.getRELPATH().matches("SQL" + SEPARATOR + "\\d*.*sql") &&
                        !fo.getRELPATH().matches("SQL" + SEPARATOR +"_" + "\\d*.*sql") &&
                        !fo.getISDIR() &&
                        !fo.getRELPATH().matches("SQL" + SEPARATOR + "001_export*.*sql") &&
                        !fo.getRELPATH().matches("SQL" + SEPARATOR + "accounting" + SEPARATOR + ".*")) {
                    if (fo.getSTATUS().equalsIgnoreCase("isNEW")) {
                        sqlLST.addElement(fo);
                    } else {
                        editSQLData.addElement(fo);
                        edit_sql++;
                    }
                }

                if (fo.getRELPATH().matches("XML" + "." + "*.xml") && !fo.getRELPATH().matches("XML\\\\module" + "." + "*.xml") && !fo.getISDIR()) {
                    xmlLST.addElement(fo);
                }
                if (!fo.getISDIR()) {
                    new_files++;
                }
                if ((fo.getRELPATH().matches("SQL" + SEPARATOR + "ServiceScript" + SEPARATOR + ".*") ||
                        fo.getRELPATH().matches("SQL" + SEPARATOR + "accounting" + SEPARATOR + ".*")) && !fo.getISDIR()) {
                    srvSQLData.addElement(fo);
                    servicesql++;

                }
                if (fo.getRELPATH().matches("XML" + SEPARATOR + "ServiceScript" + SEPARATOR + ".*") && !fo.getISDIR()) {
                    srvXMLData.addElement(fo);
                    servicexml++;
                }
                if (fo.getRELPATH().matches("XML" + SEPARATOR + "Template" + SEPARATOR + ".*") && !fo.getISDIR()) {
                    templateXMLData.addElement(fo);
                    servicexml++;
                }
                if (fo.getRELPATH().matches("XML" + SEPARATOR + "Roles" + SEPARATOR + ".*") && !fo.getISDIR()) {
                    rolesXMLData.addElement(fo);
                    servicexml++;
                }
                if (fo.getRELPATH().matches("XML" + SEPARATOR + "Tests" + SEPARATOR + ".*") && !fo.getISDIR()) {
                    testsXMLData.addElement(fo);
                    servicexml++;
                }
                if (fo.getRELPATH().matches("XML" + SEPARATOR + "TFO" + SEPARATOR + ".*") && !fo.getISDIR()) {
                    tfoXMLData.addElement(fo);
                    servicexml++;
                }
            }
            int old_files = 0;
            for (int i = 0; i < old_dir.size(); i++) {
                FileObject fo = (FileObject) old_dir.get(i);
                if (!fo.getISDIR()) {
                    old_files++;
                }
            }

            File updt = new File(this.UPDATE_DIR);
            if (!updt.exists()) {
                Log.println("Создаем директорию " + updt.getPath());
                updt.mkdirs();
            }
            String delta_name = f1.getName() + "_to_" + f2.getName();

            File delta_dir = new File(UPDATE_DIR + SEPARATOR + delta_name);
            if(delta_dir.exists()) {
                Log.println("Удаляем директорию " + delta_dir.getPath() + " так как она уже существует");
                Utils.deleteDir(delta_dir);
            }

            Log.println("Создаем директорию " + delta_dir.getPath());
            delta_dir.mkdir();
            FillDelta filldelta = new FillDelta();

            //Запись новых/измененных файлов
            if (new_dir.size() > 0) {
                Log.println("Сохраняем дельту в " + delta_dir.getPath());
                filldelta.copyFilesToDelta(new_dir, delta_dir);
            }
            //Создание SQL и XML каталогов и файлов *.lst, если это необходимо
            filldelta.create_sqlLST(delta_dir, sqlLST);
            filldelta.create_xmlLST(delta_dir, f2 ,xmlLST, tfoXMLData, templateXMLData, rolesXMLData, testsXMLData);

            Log.println("Удаляем директорию " + f1.getPath());
            Utils.deleteDir(f1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void copyBuildFiles(String FROM, String TO, String UPDATE_DIR) {
        //Создаем папки для распаковки
        Pattern pattern = Pattern.compile("[A-Za-z0-9]+\\.[A-Za-z0-9]+\\.[A-Za-z0-9]+\\.[A-Za-z0-9]+");
        Matcher buildFrom = pattern.matcher(FROM);
        Matcher buildTO = pattern.matcher(TO);
        buildFrom.find();
        buildTO.find();
        if (!new File(UPDATE_DIR + "\\Builds\\").exists()) {
            Log.println("Создаем директорию " + new File(UPDATE_DIR + "\\Builds\\").getPath());
            new File(UPDATE_DIR + "\\Builds\\").mkdirs();
        }
        String pathWithPrefix[] = {UPDATE_DIR + "\\Builds\\" + buildFrom.group().toString(), UPDATE_DIR + "\\Builds\\" + buildTO.group().toString()};
        String pathToBuild[] = {FROM, TO};
        //Для каждого из путей pathToBuild создам структуру папок и распаковываем туда билд
        for (int j = 0; j < pathToBuild.length; j++) {
            if (!new File(pathWithPrefix[j]).exists()) {
                Log.println("Создаем директорию " + new File(pathWithPrefix[j]).getPath());
                new File(pathWithPrefix[j]).mkdir();
                new File(pathWithPrefix[j] + "\\client").mkdir();
                String pathToServer = pathToBuild[j] + "\\server.zip";
                String pathToClient = pathToBuild[j] + "\\client.zip";
                try {
                    Log.println("Распаковываем " + new File(pathToServer).getPath() + " в " + new File(pathWithPrefix[j]).getPath());
                    UtilsZIP.unpack(pathToServer, pathWithPrefix[j]);

                    Log.println("Распаковываем " + new File(pathToClient).getPath() + " в " + new File(pathWithPrefix[j] + "\\client").getPath());
                    UtilsZIP.unpack(pathToClient, pathWithPrefix[j] + "\\client");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //Переопределяем FROM и TO, т.к. билды уже распакованы именно в эти папки
        this.FROM = pathWithPrefix[0];
        this.TO = pathWithPrefix[1];
    }
}
