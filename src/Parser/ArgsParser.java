package Parser;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.cli.*;

/**
 * Created by a.peresypkin on 15.11.2017.
 */
public class ArgsParser {

    private CommandLine cmdLine;

    public ArgsParser(String[] args, Options options) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        cmdLine = parser.parse(options, args);
    }

    public String[] getOptionValues(String opt) {
        String[] values = cmdLine.getOptionValues(opt);
        return values;
    }

    public String getOptionValue(String opt) {
        String value = cmdLine.getOptionValue(opt);
        return value;
    }

    public boolean getBoolOptionValue(String op) {
        String value = cmdLine.getOptionValue(op);
        if (value.equals("false")) {
            return false;
        } else {
            return true;
        }
    }

}
