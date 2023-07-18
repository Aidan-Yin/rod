import java.io.IOException;
import java.io.InputStream;

/**
 * Include recvall() to make sure all data are received
 * 
 * @author Yin
 * @className SocketUtils
 * @date 2023-7-18
 */
public class SocketUtils {
    /**
     * Make sure all data are received
     * 
     * @param is     the InputStream of the target socket
     * @param output the place you want store data in
     * @return
     * @throws IOException
     */
    public static byte[] recvall(InputStream is, byte[] output) throws IOException {
        int rec = is.read(output);
        int length = output.length;
        while (rec < length) {
            rec += is.read(output, rec, length - rec);
        }
        return output;
    }

    /**
     * Make sure all data are received
     * 
     * @param is     the InputStream of the target socket
     * @param length the length of data you are going to receive
     * @return
     * @throws IOException
     */
    public static byte[] recvall(InputStream is, int length) throws IOException {
        byte[] output = new byte[length];
        int rec = is.read(output);
        while (rec < length) {
            rec += is.read(output, rec, length - rec);
        }
        return output;
    }
}
