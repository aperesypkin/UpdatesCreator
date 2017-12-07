package Archiver;

import Helpers.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by a.peresypkin on 18.11.2017.
 */
public class Archiver {

    public static void startRar(String updateDir) {
        Log.println("Запуск процедуры архивации\n");

        File updatesDir = new File(updateDir + "\\updates");

        for (File update : updatesDir.listFiles()) {
            if (update.isDirectory()) {
                try {
                    Log.println("Начинаем архивировать обновление " + update.getPath());
                    Process process = Runtime.getRuntime().exec("cmd /c start /wait C:\\rar a -ep1 " + update.getPath() + ".rar " + update.getPath());
                    process.waitFor();
                    Log.println("Создан архив " + update.getPath() + ".rar\n");
                } catch (IOException exception) {
                    exception.printStackTrace();
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }
        }

        Log.println("Процедура архивации завершена\n");
    }

}
