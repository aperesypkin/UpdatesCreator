package Helpers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by a.peresypkin on 18.11.2017.
 */
public class Log {

    public static void println(String message) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("[" + dtf.format(now) + "]" + " " + message);
    }

}
