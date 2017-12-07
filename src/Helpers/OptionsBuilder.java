package Helpers;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * Created by a.peresypkin on 15.11.2017.
 */
public class OptionsBuilder {

    public static void setupOptions(Options options) {
        options.addOption(Option.builder("f")
                .longOpt("from")
                .desc("Билды с которых необходимо подготовить обновления. Обязательный параметр")
                .argName("build list")
                .required(true)
                .hasArgs()
                .build());

        options.addOption(Option.builder("t")
                .longOpt("to")
                .desc("Билд на который необходимо подготовить обновления. Обязательный параметр")
                .argName("build")
                .required(true)
                .hasArg()
                .build());

        options.addOption(Option.builder("fp")
                .longOpt("fromPath")
                .desc("Удаленная папка, где хранятся все билды на патч")
                .argName("path")
                .required(true)
                .hasArg()
                .build());

        options.addOption(Option.builder("tp")
                .longOpt("toPath")
                .desc("Локальный путь, где необходимо собрать обновления")
                .argName("path")
                .required(true)
                .hasArg()
                .build());

        options.addOption(Option.builder("a")
                .longOpt("isArchive")
                .desc("Архивировать ли обновления?")
                .argName("bool")
                .required(true)
                .hasArg()
                .build());

        options.addOption(Option.builder("p")
                .longOpt("isPatch")
                .desc("Собрать папки для патча?")
                .argName("bool")
                .required(true)
                .hasArg()
                .build());

        options.addOption(Option.builder("fs")
                .longOpt("ftpServer")
                .desc("Сервер FTP")
                .required(true)
                .hasArg()
                .build());

        options.addOption(Option.builder("fu")
                .longOpt("ftpUser")
                .desc("Пользователь FTP")
                .required(true)
                .hasArg()
                .build());

        options.addOption(Option.builder("fpwd")
                .longOpt("ftpPassword")
                .desc("Пароль пользователя FTP")
                .required(true)
                .hasArg()
                .build());

        options.addOption(Option.builder("ftpp")
                .longOpt("ftpPath")
                .desc("Путь к билдам на FTP")
                .required(true)
                .hasArg()
                .build());

        options.addOption(Option.builder("ftp")
                .longOpt("isFTP")
                .desc("Включает скачивание билдов с FTP")
                .argName("bool")
                .required(true)
                .hasArg()
                .build());

//        HelpFormatter formatter = new HelpFormatter();
//        formatter.printHelp("ant", options);
    }

}
