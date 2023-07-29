package com.rod;

import java.nio.ByteBuffer;

public class ArrayUtils {
    public static byte[] arrayConcat(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    public static byte[] longToBytes(long num){
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.putLong(num);
        return byteBuffer.array();
    }
    
    public static long bytesToLong(byte[] bytes){
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        return byteBuffer.getLong();
    }

    public static byte[] intToBytes(int num){
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.putInt(num);
        return byteBuffer.array();
    }
    
    public static int bytesToInt(byte[] bytes){
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        return byteBuffer.getInt();
    }

}
