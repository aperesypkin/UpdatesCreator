package BuildChecker;

import Helpers.Log;
import NetworkManagers.FTPNetworkManager;

import java.io.File;

/**
 * Created by a.peresypkin on 18.11.2017.
 */
public class BuildChecker {

    public static void check(String path, String[] fromBuilds, String toBuild, FTPNetworkManager ftp, String ftpPath) {
        Log.println("Начинаем проверку наличия билдов в " + new File(path).getPath());

        boolean isBuildsReady = true;

        for (String fromBuild : fromBuilds) {
            File build = new File(path + "\\" + fromBuild);
            if (build.exists()) {
                Log.println(fromBuild + " +");
            } else {
                Log.println(fromBuild + " -");

                String buildFolder = fromBuild.substring(0, 4);

                String remoteDirPath = ftpPath + buildFolder + "/" + fromBuild + "/";
                boolean result = ftp.download(remoteDirPath, path + "/", fromBuild);
                if (isBuildsReady) {
                    isBuildsReady = result;
                }
            }
        }

        File build = new File(path + "\\" + toBuild);
        if (build.exists()) {
            Log.println(toBuild + " +");
        } else {
            Log.println(toBuild + " -");

            String buildFolder = toBuild.substring(0, 4);

            String remoteDirPath = ftpPath + buildFolder + "/" + toBuild + "/";
            boolean result = ftp.download(remoteDirPath, path + "/", toBuild);
            if (isBuildsReady) {
                isBuildsReady = result;
            }
        }

        if (!isBuildsReady) {
            Log.println("Отсутствует один или несколько билдов, скачайте недостающие билды и повторите попытку");
            System.exit(1);
        }

        System.out.print("\n");
    }
}
