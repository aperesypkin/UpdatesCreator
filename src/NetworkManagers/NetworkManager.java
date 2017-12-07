package NetworkManagers;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by a.peresypkin on 16.11.2017.
 */
public class NetworkManager {

    public void downloadBuilds(String[] fromBuilds, String toBuild, String fromPath, String toPath) {

        for (String build : fromBuilds) {
            String sourceBuildPath = fromPath + "\\" + build;
            String destionationBuildPath = toPath + "\\fromBuilds\\" + build;
            File sourceDirection = new File(sourceBuildPath);
            File destionationDirection = new File(destionationBuildPath);

            if (!sourceDirection.exists()) {
                System.out.println("Путь " + sourceDirection + " не существует");
                System.exit(1);
            }

            if (destionationDirection.mkdirs()) {
                System.out.println("Копируем билд " + build + " из " + fromPath + " в " + toPath + "\\fromBuilds");

                File sourceFileClient = new File(sourceBuildPath + "\\client.zip");
                File sourceFileServer = new File(sourceBuildPath + "\\server.zip");
                File sourceFolderServer = new File(sourceBuildPath + "\\server");
                try {
                    FileUtils.copyFileToDirectory(sourceFileClient, destionationDirection);
                    FileUtils.copyFileToDirectory(sourceFileServer, destionationDirection);

                    if (sourceFolderServer.exists()) {
                        FileUtils.copyDirectoryToDirectory(sourceFolderServer, destionationDirection);
                    }

                } catch (IOException exception) {
                    exception.printStackTrace();
                    System.exit(1);
                }
            }

        }

        String sourceBuildPath = fromPath + "\\" + toBuild;
        String destionationBuildPath = toPath + "\\toBuild\\" + toBuild;
        File sourceDirection = new File(sourceBuildPath);
        File destionationDirection = new File(destionationBuildPath);

        if (!sourceDirection.exists()) {
            System.out.println("Путь " + sourceDirection + " не существует");
            System.exit(1);
        }

        try {
            System.out.println("Копируем билд " + toBuild + " из " + fromPath + " в " + toPath + "\\toBuild");
            FileUtils.copyDirectory(sourceDirection, destionationDirection);
        } catch (IOException exception) {
            exception.printStackTrace();
            System.exit(1);
        }

    }

}
