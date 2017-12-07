import Archiver.Archiver;
import BuildChecker.BuildChecker;
import CompareManager.CompareManager;
import Helpers.Log;
import Helpers.OptionsBuilder;
import NetworkManagers.FTPNetworkManager;
import Parser.ArgsParser;
import PatchMaker.PatchMaker;
import org.apache.commons.cli.*;

/**
 * Created by a.peresypkin on 14.11.2017.
 */
public class Creator {

    private Options options = new Options();
    private String[] fromBuilds;
    private String toBuild;
    private String fromPath;
    private String toPath;
    private boolean isArchive;
    private boolean isPatch;

    private boolean isFTP;
    private String ftpServer;
    private String ftpUser;
    private String ftpPassword;
    private String ftpPath;


    public void startCreatingUpdates(String[] args) {
        OptionsBuilder.setupOptions(options);

        parseParametersWithArgs(args);

        Log.println("UpdatesCreator started\n");

        FTPNetworkManager ftp = new FTPNetworkManager(ftpServer, ftpUser, ftpPassword, isFTP);

        BuildChecker.check(fromPath, fromBuilds, toBuild, ftp, ftpPath);

        CompareManager compareManager = new CompareManager();
        compareManager.compare(fromBuilds, toBuild, fromPath, toPath);

        if (isArchive) {
            Archiver.startRar(toPath);
        }

        if (isArchive && isPatch) {
            PatchMaker.make(toPath, toBuild, fromPath);
        }

        Log.println("UpdatesCreator finished");
    }

    private void parseParametersWithArgs(String[] args) {
        try {
            ArgsParser parser = new ArgsParser(args, options);

            fromBuilds = parser.getOptionValues("f");
            toBuild = parser.getOptionValue("t");
            fromPath = parser.getOptionValue("fp");
            toPath = parser.getOptionValue("tp");
            isArchive = parser.getBoolOptionValue("a");
            isPatch = parser.getBoolOptionValue("p");

            isFTP = parser.getBoolOptionValue("ftp");
            ftpServer = parser.getOptionValue("fs");
            ftpUser = parser.getOptionValue("fu");
            ftpPassword = parser.getOptionValue("fpwd");
            ftpPath = parser.getOptionValue("ftpp");

        } catch (ParseException exception) {
            exception.printStackTrace();
            System.exit(1);
        }
    }

}
