import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Mutifunctional log.
 * 
 * @author a-lives
 * @className Log
 * @version 1.1
 * @date 2023-8-12
 */
public class Log {

    /**
     * make log
     * 
     * @param msg
     */
    public static void log(String msg) {
        Date date = new Date();
        System.out.println("[" + date.toString() + "] " + msg);
    }

    /**
     * make log when debug is <code>true</code>
     * 
     * @param debug
     * @param msg
     */
    public static void debug(boolean debug, String msg) {
        if (debug) {
            log(msg);
        }
    }

    /**
     * make log and write in file
     * 
     * @param path
     * @param msg
     * @throws IOException
     */
    public static void log(String path, String msg) throws IOException {
        Date date = new Date();
        String logContent = "[" + date.toString() + "] " + msg;
        System.out.println(logContent);
        FileWriter fileWriter = new FileWriter(path);
        fileWriter.append(logContent);
        fileWriter.close();
    }

    /**
     * make log and write in file when debug is <code>true</code>
     * 
     * @param debug
     * @param msg
     * @throws IOException
     */
    public static void debug(boolean debug, String path, String msg) throws IOException {
        if (debug) {
            log(path, msg);
        }
    }
}
