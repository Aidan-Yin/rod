import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Some utils for array.
 * 
 * @author Yin
 * @className ArrayUtils
 * @date 2023-8-7
 */
public class ArrayUtils {
    public static byte[] arrayConcat(byte[]... bytes) {
        if (bytes.length<2){
            throw new IllegalArgumentException("arrayConcat needs at least 2 arrays");
        }
        int allLength = 0;
        for (byte[] a : bytes){
            allLength += a.length;
        }
        byte[] result = new byte[allLength];
        int offset = 0;
        for (byte[] a : bytes){
            System.arraycopy(a, 0, result, offset, a.length);
            offset += a.length;
        }
        return result;
    }

    public static byte[] longToBytes(long num){
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        byteBuffer.putLong(num);
        return byteBuffer.array();
    }
    
    public static long bytesToLong(byte[] bytes){
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        return byteBuffer.getLong();
    }

    public static byte[] intToBytes(int num){
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        byteBuffer.putInt(num);
        return byteBuffer.array();
    }
    
    public static int bytesToInt(byte[] bytes){
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        return byteBuffer.getInt();
    }
}
