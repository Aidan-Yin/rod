import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

/**
 * Socket with RSA handshake and AES encrypt transmission.
 * 
 * @author Yin
 * @className SecureSocket
 * @date 2023-7-30
 */
public class SecureSocket {
    public Socket _socket;
    public InputStream _inputStream;
    public OutputStream _outputStream;
    private byte[] _AESkey;
    private AES aes;

    /**
     * (Old version constructor, use GCM mode only)
     * 
     * @param selfPrivateKey local machine's RSA PrivateKey, and it's paired
     *                       PublicKey should be uploaded to the server before use
     *                       this class.
     * @param host           the host name, or null for the loopback address.
     * @param port           port the port number.
     * @throws IOException
     * @throws UnknownHostException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public SecureSocket(PrivateKey selfPrivateKey, String host, int port) throws UnknownHostException, IOException, InvalidKeySpecException, InvalidKeyException, SignatureException{
        /*
         * connect and handshake
         * 1. server wait for connection (accept)
         * 2. client connect server
         * 3. server send its public key to client
         * 4. client send AES key encrypted by remoteKey and signed by selfKey
         * 5. server verify the signature and set AES key
         */
        // step 2
        _socket = new Socket(host, port);
        _inputStream = _socket.getInputStream();
        _outputStream = _socket.getOutputStream();

        // step 3
        // the length of publickey(4096 bit).getEncoded() is 550.
        PublicKey remoteKey = RSA.getPublicKeyFromByte(SocketUtils.recvall(_inputStream, 550));

        // step 4
        // generate random key
        _AESkey = new byte[16]; // Use AES-128, it's enough so far.
        SecureRandom random = new SecureRandom();
        random.nextBytes(_AESkey);

        // encrypt key and sign (add timestamp to prevent replay attack)
        byte[] timeBytes = ArrayUtils.longToBytes(System.currentTimeMillis());
        byte[] temp = ArrayUtils.arrayConcat(_AESkey, timeBytes);
        byte[] cipherKey = RSA.encrypt(temp, remoteKey);
        byte[] signature = RSA.sign(temp, selfPrivateKey);
        byte[] toSend = ArrayUtils.arrayConcat(cipherKey, signature);
        _outputStream.write(toSend);

        // use GCM mode by default
        aes = new AES("GCM", _AESkey);
    }

    /**
     * 
     * @param selfPrivateKey local machine's RSA PrivateKey, and it's paired
     *                       PublicKey should be uploaded to the server before use
     *                       this class.
     * @param host           the host name, or null for the loopback address.
     * @param port           port the port number.
     * @param encryptionMode The encryption mode to use.
     * @throws UnknownHostException
     * @throws IOException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public SecureSocket(PrivateKey selfPrivateKey, String host, int port, String encryptionMode)
            throws UnknownHostException, IOException, InvalidKeySpecException, InvalidKeyException, SignatureException {

        // valid the parameter
        if (selfPrivateKey == null) {
            throw new IllegalArgumentException("selfPrivateKey is null");
        }

        if (!(encryptionMode.equals("GCM") || encryptionMode.equals("OFB") || encryptionMode.equals("CBC"))) {
            throw new IllegalArgumentException(encryptionMode + " mode is not supported");
        }

        /*
         * connect and handshake
         * 1. server wait for connection (accept)
         * 2. client connect server
         * 3. server send its public key to client
         * 4. client send AES mode & key encrypted by remoteKey and signed by selfKey
         * 5. server verify if the mode in the list ? send "ok" : close the
         * connection(Client try to receive "ok" signal)
         * 6. server verify the signature and set AES key
         */

        // step 2
        _socket = new Socket(host, port);
        _inputStream = _socket.getInputStream();
        _outputStream = _socket.getOutputStream();

        // step 3
        // the length of publickey(4096 bit).getEncoded() is 550.
        PublicKey remoteKey = RSA.getPublicKeyFromByte(SocketUtils.recvall(_inputStream, 550));

        // step 4
        // generate random key
        _AESkey = new byte[16]; // Use AES-128, it's enough so far.
        SecureRandom random = new SecureRandom();
        random.nextBytes(_AESkey);

        // encrypt key and sign (add timestamp to prevent replay attack)
        byte[] timeBytes = ArrayUtils.longToBytes(System.currentTimeMillis());
        byte[] temp = ArrayUtils.arrayConcat(encryptionMode.getBytes(), _AESkey, timeBytes);
        byte[] cipherKey = RSA.encrypt(temp, remoteKey);
        byte[] signature = RSA.sign(temp, selfPrivateKey);
        byte[] toSend = ArrayUtils.arrayConcat(cipherKey, signature);
        _outputStream.write(toSend);

        try {
            byte[] return_signal = new byte[2];
            SocketUtils.recvall(_inputStream, return_signal);
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("The server doesn't accept " + encryptionMode + " mode");
        }

        // step 6
        aes = new AES(encryptionMode, _AESkey);
    }

    /**
     * It's only used as the return value in SecureServerSocket.accept()
     * 
     * @param AESkey
     * @param inputStream
     * @param outputStream
     * @param socket
     * @param encryptionMode
     */
    public SecureSocket(byte[] AESkey, InputStream inputStream, OutputStream outputStream, Socket socket,
            String encryptionMode) {
        _AESkey = AESkey;
        _inputStream = inputStream;
        _outputStream = outputStream;
        _socket = socket;
        aes = new AES(encryptionMode, _AESkey);
    }

    /**
     * send all data securely
     * 
     * @param data data you want to send
     * @throws IllegalBlockSizeException
     * @throws IOException
     */
    public void sendall(byte[] data) throws IllegalBlockSizeException, IOException {
        SecureRandom random = new SecureRandom();
        byte[] iv;
        byte[] ciphertext;
        try {
            switch (aes.getMode()) {
                case "GCM":
                    iv = new byte[96];
                    byte[] aad = new byte[32];
                    random.nextBytes(iv);
                    random.nextBytes(aad);
                    ciphertext = aes.encrypt(data, iv, aad);
                    _outputStream.write(ArrayUtils.intToBytes(iv.length + aad.length + ciphertext.length));
                    _outputStream.write(iv);
                    _outputStream.write(aad);
                    _outputStream.write(ciphertext);
                    break;
                case "OFB":
                    iv = new byte[16];
                    random.nextBytes(iv);
                    ciphertext = aes.encrypt(data, iv);
                    _outputStream.write(ArrayUtils.intToBytes(iv.length + ciphertext.length));
                    _outputStream.write(iv);
                    _outputStream.write(ciphertext);
                    break;
                case "CBC":
                    iv = new byte[16];
                    random.nextBytes(iv);
                    ciphertext = aes.encrypt(data, iv);
                    _outputStream.write(ArrayUtils.intToBytes(iv.length + ciphertext.length));
                    _outputStream.write(iv);
                    _outputStream.write(ciphertext);
                    break;
                default:
                    break;
            }
        } catch (InvalidKeyException | BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

    }

    /**
     * Receive all encrypted data
     * 
     * @return the data received (plaintext).
     * @throws IOException
     * @throws IllegalBlockSizeException
     */
    public byte[] recvall() throws IOException, IllegalBlockSizeException {
        byte[] iv;
        int length;
        byte[] rawData;
        byte[] ciphertext;
        try {
            switch (aes.getMode()) {
                case "GCM":
                    iv = new byte[96];
                    byte[] aad = new byte[32];
                    length = ArrayUtils.bytesToInt(SocketUtils.recvall(_inputStream, 4));
                    rawData = SocketUtils.recvall(_inputStream, length);
                    iv = Arrays.copyOfRange(rawData, 0, 96);
                    aad = Arrays.copyOfRange(rawData, 96, 128);
                    ciphertext = Arrays.copyOfRange(rawData, 128, length);
                    return aes.decrypt(ciphertext, iv, aad);
                case "OFB":
                    iv = new byte[16];
                    length = ArrayUtils.bytesToInt(SocketUtils.recvall(_inputStream, 4));
                    rawData = SocketUtils.recvall(_inputStream, length);
                    iv = Arrays.copyOfRange(rawData, 0, 16);
                    ciphertext = Arrays.copyOfRange(rawData, 16, length);
                    return aes.decrypt(ciphertext, iv);
                case "CBC":
                    iv = new byte[16];
                    length = ArrayUtils.bytesToInt(SocketUtils.recvall(_inputStream, 4));
                    rawData = SocketUtils.recvall(_inputStream, length);
                    iv = Arrays.copyOfRange(rawData, 0, 16);
                    ciphertext = Arrays.copyOfRange(rawData, 16, length);
                    return aes.decrypt(ciphertext, iv);
                default:
                    return null;
            }
        } catch (InvalidKeyException | BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Send file with secure socket, support large file
     * 
     * @param filePath the path of the file you want to send
     * @throws IOException
     * @throws IllegalBlockSizeException
     */
    public void sendFile(String filePath) throws IllegalBlockSizeException, IOException {
        File file = new File(filePath);
        FileInputStream inputStream = new FileInputStream(file);
        long size = file.length();
        if (size <= 128 * 1024 * 1024) { // less than 128MB: small file
            // send file size
            sendall(ArrayUtils.longToBytes(size));

            // read file and send it
            byte[] data = new byte[(int) size];
            inputStream.read(data);
            sendall(data);
        } else { // more than 128MB: large file
                 // send file size
            sendall(ArrayUtils.longToBytes(size));
            // read file and send it
            byte[] buffer = new byte[128 * 1024 * 1024]; // 128MB
            int read = inputStream.read(buffer);
            while (read == 128 * 1024 * 1024) { // haven't to the end
                sendall(buffer);
                read = inputStream.read(buffer);
            }
            byte[] lastBuffer = new byte[read];
            System.arraycopy(buffer, 0, lastBuffer, 0, read);
            sendall(lastBuffer);
        }
        inputStream.close();
    }

    /**
     * receive file send by sendFile(), support large file
     * 
     * @param savePath the path you want to save the file
     * @throws IllegalBlockSizeException
     * @throws IOException
     */
    public void recvFile(String savePath) throws IllegalBlockSizeException, IOException {
        File file = new File(savePath);
        FileOutputStream outputStream = new FileOutputStream(file);
        long size = ArrayUtils.bytesToLong(recvall());
        if (size <= 128 * 1024 * 1024) { // less than 128MB: small file
            outputStream.write(recvall());
        } else { // more than 128MB: large file
            int loops = (int) size / (128 * 1024 * 1024) + 1;
            for (int i = 0; i < loops; i++) {
                outputStream.write(recvall());
            }
        }
        outputStream.close();
    }

    /**
     * Releases system resources.
     * 
     * @throws IOException
     */
    public void close() throws IOException {
        _inputStream.close();
        _outputStream.close();
        _socket.close();
    }

    /**
     * Returns the connection state of the socket.
     * <p>
     * Note: Closing a socket doesn't clear its connection state, which means
     * this method will return {@code true} for a closed socket
     * (see {@link #isClosed()}) if it was successfully connected prior
     * to being closed.
     *
     * @return true if the socket was successfully connected to a server
     */
    public boolean isConnected() {
        return _socket.isConnected();
    }

    /**
     * Returns the closed state of the socket.
     * 
     * @return true if the socket has been closed
     */
    public boolean isClosed() {
        return _socket.isClosed();
    }
}
