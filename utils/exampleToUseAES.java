import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * 
 * @author Yin
 * @className exampleToUseAES
 * @description to show how to use the AES class (uncommented a block to see the
 *              effect)
 * @date 2023-7-16
 */
public class exampleToUseAES {
    public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        byte[] key = "0123456789abcdef".getBytes();
        byte[] iv = "0123456789abcdef".getBytes();
        byte[] data = "Hello, world!".getBytes();
        byte[] aad = "Hello again!".getBytes();

        /**
         * ECB mode
         */

        // // encrypt
        // AES aes = new AES("ECB", key);
        // aes.prepareToEncrypt();
        // byte[] ciphertext = aes.encrypt(data);
        // System.out.println(new String(ciphertext));
        // // decrypt
        // aes.prepareToDecrypt();
        // byte[] plaintext = aes.decrypt(ciphertext);
        // System.out.println(new String(plaintext));

        /**
         * CBC mode
         */

        // // encrypt
        // AES aes = new AES("CBC", key, iv);
        // aes.prepareToEncrypt();
        // byte[] ciphertext = aes.encrypt(data);
        // System.out.println(new String(ciphertext));
        // // decrypt
        // aes.prepareToDecrypt();
        // byte[] plaintext = aes.decrypt(ciphertext);
        // System.out.println(new String(plaintext));

        /**
         * GCM mode
         */

        // // encrypt
        // AES aes = new AES("GCM", key, iv);
        // aes.prepareToEncrypt();
        // byte[] ciphertext = aes.encrypt(data, aad);
        // System.out.println(new String(ciphertext));
        // // decrypt
        // aes.prepareToDecrypt();
        // byte[] plaintext = aes.decrypt(ciphertext, aad);
        // System.out.println(new String(plaintext));

        /**
         * OFB mode
         */

        // // encrypt
        // AES aes = new AES("OFB", key, iv);
        // aes.prepareToEncrypt();
        // byte[] ciphertext = aes.encrypt(data);
        // System.out.println(new String(ciphertext));
        // // decrypt
        // aes.prepareToDecrypt();
        // byte[] plaintext = aes.decrypt(ciphertext);
        // System.out.println(new String(plaintext));

    }
}
