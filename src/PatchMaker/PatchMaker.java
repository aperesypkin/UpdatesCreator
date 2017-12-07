package PatchMaker;

import Helpers.Log;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * Created by a.peresypkin on 18.11.2017.
 */
public class PatchMaker {

    public static void make(String updateDir, String patchBuild, String buildsDir) {
        Log.println("Запуск процедуры подготовки патча\n");

        updateDir = updateDir + "\\updates";

        try {
            Log.println("Копируем " + buildsDir + "\\" + patchBuild + " в " + updateDir);
            FileUtils.copyDirectoryToDirectory(new File(buildsDir + "\\" + patchBuild), new File(updateDir));
        } catch (IOException exception) {
            exception.printStackTrace();
            System.exit(1);
        }

        File patchFolder = new File(updateDir + "\\patch v." + patchBuild);
        Log.println("Создаем директорию " + patchFolder.getPath());
        if (!patchFolder.exists()) {
            patchFolder.mkdirs();
        } else {
            Log.println("Директория " + patchFolder.getPath() + " уже существует");
        }

        File[] updates = new File(updateDir).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.matches("([^\\s]+(?=\\.(rar))\\.\\2)");
            }
        });

        for (File update : updates) {
            Log.println("Перемещаем " + update.getPath() + " в " + patchFolder.getPath());
            try {
                FileUtils.moveFileToDirectory(update, patchFolder, false);
            }  catch (IOException exception) {
                exception.printStackTrace();
                System.exit(1);
            }
        }

        File webFolder = new File(updateDir + "\\patch v." + patchBuild + "\\web_" + patchBuild);
        Log.println("Создаем директорию " + webFolder.getPath());
        if (!webFolder.exists()) {
            webFolder.mkdirs();
        } else {
            Log.println("Директория " + webFolder.getPath() + " уже существует");
        }

        try {
            Log.println("Копируем " + buildsDir + "\\" + patchBuild + "\\azk.war" + " в " + webFolder.getPath());
            FileUtils.copyFileToDirectory(new File(buildsDir + "\\" + patchBuild + "\\azk.war"), new File(webFolder.getPath()));
        } catch (IOException exception) {
            exception.printStackTrace();
            System.exit(1);
        }

        File termitFolder = new File(updateDir + "\\patch v." + patchBuild + "\\termit_" + patchBuild);
        Log.println("Создаем директорию " + termitFolder.getPath());
        if (!termitFolder.exists()) {
            termitFolder.mkdirs();
        } else {
            Log.println("Директория " + termitFolder.getPath() + " уже существует");
        }

        try {
            Log.println("Копируем " + buildsDir + "\\" + patchBuild + "\\ws.war" + " в " + termitFolder.getPath() + "\n");
            FileUtils.copyFileToDirectory(new File(buildsDir + "\\" + patchBuild + "\\ws.war"), new File(termitFolder.getPath()));
        } catch (IOException exception) {
            exception.printStackTrace();
            System.exit(1);
        }

        Log.println("Процедура подготовки патча завершена\n");
    }

}
