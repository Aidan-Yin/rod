import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Mutifunctional log.
 * 
 * @author a-lives
 * @className Log
 * @version 1.0
 * @date 2023-7-27
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
     * make log and write in file
     * 
     * @param path
     * @param msg
     * @throws IOException
     */
    public static void record(String path, String msg) throws IOException {
        Date date = new Date();
        String logContent = "[" + date.toString() + "] " + msg;
        System.out.println(logContent);
        FileWriter fileWriter = new FileWriter(path);
        fileWriter.append(logContent);
        fileWriter.close();
    }
}
