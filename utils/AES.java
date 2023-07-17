import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 
 * @author Yin
 * @className AES
 * @description to use AES algorithm easily
 * @date 2023-7-16
 */
public class AES{

    private static Cipher cipher;
    private static String _mode;
    private static byte[] _key;
    private static byte[] _iv;
    private static boolean toEncrypt;

    /**
     * 
     * @param mode the AES mode you'd like to use 
     * @param key the secret key you'd like to use
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     */
    public AES(String mode, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException{
        // only mode ECB
        if (!mode.equals("ECB")){
            throw new IllegalArgumentException("not a valid AES mode parameter");
        }
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        _mode = mode;
        _key = key;
    }

    /**
     * 
     * @param mode the AES mode you are going to use 
     * @param key the secret key you are going to use
     * @param iv the Initialization Vector(IV) you are going to use
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public AES(String mode, byte[] key, byte[] iv) throws NoSuchAlgorithmException, NoSuchPaddingException{
        switch (mode){
            case "CBC":
                cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                break;
            case "OFB":
                cipher = Cipher.getInstance("AES/OFB/NoPadding");
                break;
            case "GCM":
                cipher = Cipher.getInstance("AES/GCM/NoPadding");
                break;
            default:
                throw new IllegalArgumentException("not a valid AES mode parameter");
        }
        _mode = mode;
        _key = key;
        _iv = iv;
    }

    /**
     * 
     * @description do some preperation
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     */
    public void prepareToEncrypt() throws InvalidKeyException, InvalidAlgorithmParameterException{
        switch (_mode){
            case "ECB":
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(_key, "AES"));
                break;
            case "CBC":
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(_key, "AES"), new IvParameterSpec(_iv));
                break;
            case "GCM":
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(_key, "AES"), new GCMParameterSpec(128, _iv));
                break;
            case "OFB":
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(_key, "AES"), new IvParameterSpec(_iv));
                break;
        }
        toEncrypt = true;
    }

    /**
     * 
     * @description do some preperation
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     */
    public void prepareToDecrypt() throws InvalidKeyException, InvalidAlgorithmParameterException{
        switch (_mode){
            case "ECB":
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(_key, "AES"));
                break;
            case "CBC":
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(_key, "AES"), new IvParameterSpec(_iv));
                break;
            case "GCM":
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(_key, "AES"), new GCMParameterSpec(128, _iv));
                break;
            case "OFB":
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(_key, "AES"), new IvParameterSpec(_iv));
                break;
        }
        toEncrypt = false;
    }

    /**
     * 
     * @param data the data you are going to encrypt
     * @return
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public byte[] encrypt(byte[] data) throws IllegalBlockSizeException, BadPaddingException{
        if (!toEncrypt){
            throw new UnsupportedOperationException("Please use AES.prepareToEncrypt() first before encrypt");
        }
        if (_mode.equals("GCM")){
            throw new IllegalArgumentException("Miss a parameter for GCM mode");
        }
        return cipher.doFinal(data);
    }

    /**
     * 
     * @param data the data you are going to encrypt
     * @param aad Additional Authenticated Data
     * @return
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public byte[] encrypt(byte[] data, byte[] aad) throws IllegalBlockSizeException, BadPaddingException{
        if (!toEncrypt){
            throw new UnsupportedOperationException("Please use AES.prepareToEncrypt() first before encrypt");
        }
        if (!_mode.equals("GCM")){
            throw new IllegalArgumentException("There is one redundant parameter(Only GCM mode need 2 parameter to encrypt data)");
        }
        cipher.updateAAD(aad);
        return cipher.doFinal(data);
    }

    /**
     * 
     * @param data the data you are going to decrypt
     * @return
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public byte[] decrypt(byte[] data) throws IllegalBlockSizeException, BadPaddingException{
        if (toEncrypt){
            throw new UnsupportedOperationException("Please use AES.prepareToDecrypt() first before decrypt");
        }
        if (_mode.equals("GCM")){
            throw new IllegalArgumentException("Miss a parameter for GCM mode");
        }
        return cipher.doFinal(data);
    }

    /**
     * 
     * @param data the data you are going to decrypt
     * @param add Additional Authenticated Data
     * @return
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public byte[] decrypt(byte[] data, byte[] aad) throws IllegalBlockSizeException, BadPaddingException{
        if (toEncrypt){
            throw new UnsupportedOperationException("Please use AES.prepareToDecrypt() first before decrypt");
        }
        if (!_mode.equals("GCM")){
            throw new IllegalArgumentException("There is one redundant parameter(Only GCM mode need 2 parameter to decrypt data)");
        }
        cipher.updateAAD(aad);
        return cipher.doFinal(data);
    }
}