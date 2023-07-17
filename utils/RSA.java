import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * @author Yin
 * @className RSA
 * @description To generate and read different form of rsa key easily.<br>
 *              To encrypt and decrypt data with rsa algorithm easily.<br>
 *              TO sign and verify with rsa easily.
 * @date 2023-7-17
 */
public class RSA {
    public static PublicKey publicKey;
    public static PrivateKey privateKey;

    /**
     * 
     * @param nbit the keysize
     * @throws NoSuchAlgorithmException
     */
    public RSA(int nbit) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(nbit);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }

    public byte[] getBytePublicKey() {
        return publicKey.getEncoded();
    }

    public byte[] getBytePrivateKey() {
        return privateKey.getEncoded();
    }

    public String getBase64PublicKey() {
        return Base64.getEncoder().encodeToString(getBytePublicKey());
    }

    public String getBase64PrivateKey() {
        return Base64.getEncoder().encodeToString(getBytePrivateKey());
    }

    /**
     * 
     * @return something likes:<br>
     *         -----BEGIN RSA PUBLIC KEY-----<br>
     *         MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJp1qfpak723HCoRyhZkoPTrkggtrkbQ<br>
     *         NJzTM3eX+kBSVnD6uKUa7S5/MueQcf1f3km5tohapcNhpErlG2XE/0ECAwEAAQ==<br>
     *         -----END RSA PUBLIC KEY-----
     */
    public String getFormalPublicKey() {
        StringBuilder stringBuilder = new StringBuilder();
        String base64Key = getBase64PublicKey();
        stringBuilder.append("-----BEGIN RSA PUBLIC KEY-----\n");
        for (int i = 0; i * 64 < base64Key.length(); i++) {
            stringBuilder.append(base64Key, i * 64, Math.min((i + 1) * 64, base64Key.length()));
            stringBuilder.append("\n");
        }
        stringBuilder.append("-----END RSA PUBLIC KEY-----");
        return stringBuilder.toString();
    }

    /**
     * 
     * @return something likes:<br>
     *         -----BEGIN RSA PRIVATE KEY-----<br>
     *         MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAmnWp+lqTvbccKhHK<br>
     *         FmSg9OuSCC2uRtA0nNMzd5f6QFJWcPq4pRrtLn8y55Bx/V/eSbm2iFqlw2GkSuUb<br>
     *         ZcT/QQIDAQABAkA63O9/tKgMCy3FDI1+ti+u1t2Kl1oMq4j5YPWCtJzOiNqlWB/R<br>
     *         34usn/bl1wpkRQUnGOz67SJ2JQchWp6hVmrXAiEA0FQo3dgiJIPFf5jczOw+G7Lj<br>
     *         82B/3A3bG8sxHtROa+sCIQC9zd2yal91RAId46/QZAO/r+5y4kvxx12nOCz6cRfS<br>
     *         gwIgTvVG0+Yh8qL6zMScExMK6yafHNQbQcUCoYMep+ehnWcCIQCWxd4CSHWj1W5J<br>
     *         CAE1bP19W+fy4sipO1Gt7/Xqy8O8uQIgXut92n6oH9dNdecOtiZpCaEboOzLUZ9v<br>
     *         YRYeuc9LPXs=<br>
     *         -----END RSA PRIVATE KEY-----
     */
    public String getFormalPrivateKey() {
        StringBuilder stringBuilder = new StringBuilder();
        String base64Key = getBase64PrivateKey();
        stringBuilder.append("-----BEGIN RSA PRIVATE KEY-----\n");
        for (int i = 0; i * 64 < base64Key.length(); i++) {
            stringBuilder.append(base64Key, i * 64, Math.min((i + 1) * 64, base64Key.length()));
            stringBuilder.append("\n");
        }
        stringBuilder.append("-----END RSA PRIVATE KEY-----");
        return stringBuilder.toString();
    }

    /**
     * 
     * @param _publicKey publicKey
     * @return something likes:<br>
     *         -----BEGIN RSA PUBLIC KEY-----<br>
     *         MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJp1qfpak723HCoRyhZkoPTrkggtrkbQ<br>
     *         NJzTM3eX+kBSVnD6uKUa7S5/MueQcf1f3km5tohapcNhpErlG2XE/0ECAwEAAQ==<br>
     *         -----END RSA PUBLIC KEY-----
     */
    public static String getFormalPublicKey(PublicKey publicKey) {
        StringBuilder stringBuilder = new StringBuilder();
        String base64Key = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        stringBuilder.append("-----BEGIN RSA PUBLIC KEY-----\n");
        for (int i = 0; i * 64 < base64Key.length(); i++) {
            stringBuilder.append(base64Key, i * 64, Math.min((i + 1) * 64, base64Key.length()));
            stringBuilder.append("\n");
        }
        stringBuilder.append("-----END RSA PUBLIC KEY-----");
        return stringBuilder.toString();
    }

    /**
     * 
     * @param privateKey
     * @return something likes:<br>
     *         -----BEGIN RSA PRIVATE KEY-----<br>
     *         MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAmnWp+lqTvbccKhHK<br>
     *         FmSg9OuSCC2uRtA0nNMzd5f6QFJWcPq4pRrtLn8y55Bx/V/eSbm2iFqlw2GkSuUb<br>
     *         ZcT/QQIDAQABAkA63O9/tKgMCy3FDI1+ti+u1t2Kl1oMq4j5YPWCtJzOiNqlWB/R<br>
     *         34usn/bl1wpkRQUnGOz67SJ2JQchWp6hVmrXAiEA0FQo3dgiJIPFf5jczOw+G7Lj<br>
     *         82B/3A3bG8sxHtROa+sCIQC9zd2yal91RAId46/QZAO/r+5y4kvxx12nOCz6cRfS<br>
     *         gwIgTvVG0+Yh8qL6zMScExMK6yafHNQbQcUCoYMep+ehnWcCIQCWxd4CSHWj1W5J<br>
     *         CAE1bP19W+fy4sipO1Gt7/Xqy8O8uQIgXut92n6oH9dNdecOtiZpCaEboOzLUZ9v<br>
     *         YRYeuc9LPXs=<br>
     *         -----END RSA PRIVATE KEY-----
     */
    public static String getFormalPrivateKey(PrivateKey privateKey) {

        StringBuilder stringBuilder = new StringBuilder();
        String base64Key = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        stringBuilder.append("-----BEGIN RSA PRIVATE KEY-----\n");
        for (int i = 0; i * 64 < base64Key.length(); i++) {
            stringBuilder.append(base64Key, i * 64, Math.min((i + 1) * 64, base64Key.length()));
            stringBuilder.append("\n");
        }
        stringBuilder.append("-----END RSA PRIVATE KEY-----");
        return stringBuilder.toString();
    }

    /**
     * 
     * @param byteKey You can get it from PublicKey.getEncoded()
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PublicKey getPublicKeyFromByte(byte[] byteKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(byteKey);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        return publicKey;
    }

    /**
     * 
     * @param byteKey You can get it from PrivateKey.getEncoded()
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PrivateKey getPrivateKeyFromByte(byte[] byteKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(byteKey);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        return privateKey;
    }

    public static PublicKey getPublicKeyFromBase64(String base64Key)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] byteKey = Base64.getDecoder().decode(base64Key);
        return RSA.getPublicKeyFromByte(byteKey);
    }

    public static PrivateKey getPrivateKeyFromBase64(String base64Key)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] byteKey = Base64.getDecoder().decode(base64Key);
        return RSA.getPrivateKeyFromByte(byteKey);
    }

    /**
     * 
     * @param formal something likes:<br>
     *               -----BEGIN RSA PUBLIC KEY-----<br>
     *               MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJp1qfpak723HCoRyhZkoPTrkggtrkbQ<br>
     *               NJzTM3eX+kBSVnD6uKUa7S5/MueQcf1f3km5tohapcNhpErlG2XE/0ECAwEAAQ==<br>
     *               -----END RSA PUBLIC KEY-----
     * @return PublicKey
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PublicKey getPublicKeyFromFormal(String formal)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] buffer = formal.split("\n");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < (buffer.length - 1); i++) {
            stringBuilder.append(buffer[i]);
        }
        return RSA.getPublicKeyFromBase64(stringBuilder.toString());
    }

    /**
     * 
     * @param formal something likes:<br>
     *               -----BEGIN RSA PRIVATE KEY-----<br>
     *               MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAmnWp+lqTvbccKhHK<br>
     *               FmSg9OuSCC2uRtA0nNMzd5f6QFJWcPq4pRrtLn8y55Bx/V/eSbm2iFqlw2GkSuUb<br>
     *               ZcT/QQIDAQABAkA63O9/tKgMCy3FDI1+ti+u1t2Kl1oMq4j5YPWCtJzOiNqlWB/R<br>
     *               34usn/bl1wpkRQUnGOz67SJ2JQchWp6hVmrXAiEA0FQo3dgiJIPFf5jczOw+G7Lj<br>
     *               82B/3A3bG8sxHtROa+sCIQC9zd2yal91RAId46/QZAO/r+5y4kvxx12nOCz6cRfS<br>
     *               gwIgTvVG0+Yh8qL6zMScExMK6yafHNQbQcUCoYMep+ehnWcCIQCWxd4CSHWj1W5J<br>
     *               CAE1bP19W+fy4sipO1Gt7/Xqy8O8uQIgXut92n6oH9dNdecOtiZpCaEboOzLUZ9v<br>
     *               YRYeuc9LPXs=<br>
     *               -----END RSA PRIVATE KEY-----
     * @return PrivateKey
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PrivateKey getPrivateKeyFromFormal(String formal)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] buffer = formal.split("\n");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < (buffer.length - 1); i++) {
            stringBuilder.append(buffer[i]);
        }
        return RSA.getPrivateKeyFromBase64(stringBuilder.toString());
    }

    /**
     * 
     * @param data      plaintext
     * @param publicKey
     * @return ciphertext
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static byte[] encrypt(byte[] data, PublicKey publicKey) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] result = cipher.doFinal(data);
        return result;
    }

    /**
     * 
     * @param data       ciphertext
     * @param privateKey
     * @return plaintext
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static byte[] decrypt(byte[] data, PrivateKey privateKey) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] result = cipher.doFinal(data);
        return result;
    }

    /**
     * 
     * @param data       data to sign
     * @param privateKey
     * @return signed data
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public static byte[] sign(byte[] data, PrivateKey privateKey)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    /**
     * 
     * @param data       data to verify
     * @param signedData signed data
     * @param publicKey
     * @return is the signature valid or not
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public static boolean verify(byte[] data, byte[] signedData, PublicKey publicKey)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(signedData);
    }
}
