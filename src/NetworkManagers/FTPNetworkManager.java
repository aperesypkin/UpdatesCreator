package NetworkManagers;

import Helpers.Log;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import java.io.*;

/**
 * Created by a.peresypkin on 16.11.2017.
 */
public class FTPNetworkManager {

    private String server;
    private String user;
    private String password;
    private boolean isFTP;

    public FTPNetworkManager(String server, String user, String password, boolean isFTP) {
        this.server = server;
        this.user = user;
        this.password = password;
        this.isFTP = isFTP;
    }

    public boolean download(String remoteDirPath, String saveDirPath, String build) {

        if (isFTP) {
            FTPClient ftpClient = new FTPClient();

            Log.println("Пытаемся скачать с FTP...");

            try {
                // connect and login to the server
                ftpClient.connect(server);
                ftpClient.login(user, password);

                // use local passive mode to pass firewall
                ftpClient.enterLocalPassiveMode();

                boolean result = FTPNetworkManager.downloadDirectory(ftpClient, remoteDirPath, "", saveDirPath, build);

                // log out and disconnect from the server
                ftpClient.logout();
                ftpClient.disconnect();

                if (!result) {
                    Log.println("Неудачно");
                }

                return result;

            } catch (IOException ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }

        return false;
    }

    private static boolean downloadDirectory(FTPClient ftpClient, String parentDir,
                                         String currentDir, String saveDir, String build) throws IOException {
        String dirToList = parentDir;
        if (!currentDir.equals("")) {
            dirToList += "/" + currentDir;
        }

        FTPFile[] subFiles = ftpClient.listFiles(dirToList);

        if (subFiles != null && subFiles.length > 0) {
            for (FTPFile aFile : subFiles) {
                String currentFileName = aFile.getName();
                if (currentFileName.equals(".") || currentFileName.equals("..")) {
                    // skip parent directory and the directory itself
                    continue;
                }
                String filePath = parentDir + "/" + currentDir + "/" + currentFileName + "/";
                if (currentDir.equals("")) {
                    filePath = parentDir + "/" + currentFileName + "/";
                }

                String buildFolder = build.substring(0, 4);
                String newParentDir = parentDir.replace("/BFT/!filials/!info/azk2/" + buildFolder + "/", "");

                String newDirPath = saveDir + newParentDir + File.separator + currentDir + File.separator + currentFileName;
                if (currentDir.equals("")) {
                    newDirPath = saveDir + newParentDir + File.separator + currentFileName;
                }

                if (aFile.isDirectory()) {
                    // create the directory in saveDir
                    File newDir = new File(newDirPath);
                    boolean created = newDir.mkdirs();
                    if (!created) {
                        System.out.println("COULD NOT create the directory: " + newDirPath);
                        System.exit(1);
                    }

                    // download the sub directory
                    downloadDirectory(ftpClient, dirToList, currentFileName, saveDir, build);
                } else {
                    // download the file
                    boolean success = downloadSingleFile(ftpClient, filePath, newDirPath);
                    if (!success) {
                        System.out.println("COULD NOT download the file: " + filePath);
                        System.exit(1);
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }

    private static boolean downloadSingleFile(FTPClient ftpClient,
                                             String remoteFilePath, String savePath) throws IOException {
        File downloadFile = new File(savePath);

        File parentDir = downloadFile.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdir();
        }

        OutputStream outputStream = new BufferedOutputStream (
                new FileOutputStream(downloadFile));
        try {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            Log.println("Скачиваем " + new File(remoteFilePath).getPath());
            return ftpClient.retrieveFile(remoteFilePath, outputStream);
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

}
