package com.rod;

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
 * To use AES algorithm easily
 * 
 * @author Yin
 * @className AES
 * @version 1.1
 * @date 2023-7-17
 */
public class AES {

    private Cipher _cipher;
    private String _mode;
    private byte[] _key;

    /**
     * Initialization
     * 
     * @param mode the AES mode you are going to use
     * @param key  the secret key you are going to use
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public AES(String mode, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException {
        switch (mode) {
            case "ECB":
                _cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                break;
            case "CBC":
                _cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                break;
            case "OFB":
                _cipher = Cipher.getInstance("AES/OFB/NoPadding");
                break;
            case "GCM":
                _cipher = Cipher.getInstance("AES/GCM/NoPadding");
                break;
            default:
                throw new IllegalArgumentException("not a valid AES mode parameter");
        }
        _mode = mode;
        _key = key;
    }

    /**
     * Encrypt data
     * 
     * @param data the data you are going to encrypt(plaintext)
     * @return ciphertext
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
    public byte[] encrypt(byte[] data) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        // verify parameter
        if (_mode.equals("GCM")) {
            throw new IllegalArgumentException("Miss iv and aad for GCM mode");
        } else if (!_mode.equals("ECB")) {
            throw new IllegalArgumentException("Miss iv for " + _mode + " mode");
        }
        _cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(_key, "AES"));
        return _cipher.doFinal(data);
    }

    /**
     * Encrypt data
     * 
     * @param data the data you are going to encrypt(plaintext)
     * @param iv   the Initialization Vector(IV) you are going to use
     * @return ciphertext
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     */
    public byte[] encrypt(byte[] data, byte[] iv) throws IllegalBlockSizeException, BadPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException {
        // verify parameter
        if (_mode.equals("GCM")) {
            throw new IllegalArgumentException("Miss aad for GCM mode");
        } else if (_mode.equals("ECB")) {
            throw new IllegalArgumentException("One redundant parameter");
        }
        switch (_mode) {
            case "CBC":
                _cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(_key, "AES"), new IvParameterSpec(iv));
                break;
            case "OFB":
                _cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(_key, "AES"), new IvParameterSpec(iv));
                break;
        }
        return _cipher.doFinal(data);
    }

    /**
     * Encrypt data
     * 
     * @param data the data you are going to encrypt(plaintext)
     * @param iv   the Initialization Vector(IV) you are going to use
     * @param aad  Additional Authenticated Data
     * @return ciphertext
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     */
    public byte[] encrypt(byte[] data, byte[] iv, byte[] aad) throws IllegalBlockSizeException, BadPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException {
        // verify parameter
        if (!_mode.equals("GCM")) {
            throw new IllegalArgumentException("There is(are) redundant parameter(s)");
        }
        _cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(_key, "AES"), new GCMParameterSpec(128, iv));
        _cipher.updateAAD(aad);
        return _cipher.doFinal(data);
    }

    /**
     * Decrypt data
     * 
     * @param data the data you are going to decrypt(ciphertext)
     * @return plaintext
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
    public byte[] decrypt(byte[] data) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        // verify parameter
        if (_mode.equals("GCM")) {
            throw new IllegalArgumentException("Miss iv and aad for GCM mode");
        } else if (!_mode.equals("ECB")) {
            throw new IllegalArgumentException("Miss iv for " + _mode + " mode");
        }
        _cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(_key, "AES"));
        return _cipher.doFinal(data);
    }

    /**
     * Decrypt data
     * 
     * @param data the data you are going to decrypt(ciphertext)
     * @param iv   the Initialization Vector(IV) used when encrypt
     * @return plaintext
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     */
    public byte[] decrypt(byte[] data, byte[] iv) throws IllegalBlockSizeException, BadPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException {
        // verify parameter
        if (_mode.equals("GCM")) {
            throw new IllegalArgumentException("Miss aad for GCM mode");
        } else if (_mode.equals("ECB")) {
            throw new IllegalArgumentException("One redundant parameter");
        }
        switch (_mode) {
            case "CBC":
                _cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(_key, "AES"), new IvParameterSpec(iv));
                break;
            case "OFB":
                _cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(_key, "AES"), new IvParameterSpec(iv));
                break;
        }
        return _cipher.doFinal(data);
    }

    /**
     * Decrypt data
     * 
     * @param data the data you are going to decrypt(ciphertext)
     * @param iv   the Initialization Vector(IV) used when encrypt
     * @param aad  Additional Authenticated Data
     * @return plaintext
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     */
    public byte[] decrypt(byte[] data, byte[] iv, byte[] aad) throws IllegalBlockSizeException, BadPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException {
        // verify parameter
        if (!_mode.equals("GCM")) {
            throw new IllegalArgumentException("There is(are) redundant parameter(s)");
        }
        _cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(_key, "AES"), new GCMParameterSpec(128, iv));
        _cipher.updateAAD(aad);
        return _cipher.doFinal(data);
    }
}