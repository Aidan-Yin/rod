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
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Socket with RSA handshake and AES-GCM encrypt transmission.
 * 
 * @author Yin
 * @className SecureSocket
 * @date 2023-7-23
 */
public class SecureSocket {
    public Socket _socket;
    public InputStream _inputStream;
    public OutputStream _outputStream;
    private byte[] _AESkey;
    private AES aes;

    /**
     * 
     * @param selfPrivateKey local machine's RSA PrivateKey, and it's paired
     *                       PublicKey should be uploaded to the server before use
     *                       this class.
     * @param host           the host name, or null for the loopback address.
     * @param port           port the port number.
     * @throws UnknownHostException
     * @throws IOException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws SignatureException
     * @throws InvalidKeySpecException
     */
    public SecureSocket(PrivateKey selfPrivateKey, String host, int port)
            throws UnknownHostException, IOException, InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, SignatureException,
            InvalidKeySpecException {
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
        Random random = new Random();
        random.nextBytes(_AESkey);

        // encrypt key and sign (add timestamp to prevent replay attack)
        byte[] timeBytes = ArrayUtils.longToBytes(System.currentTimeMillis());
        byte[] temp = ArrayUtils.arrayConcat(_AESkey, timeBytes);
        byte[] cipherKey = RSA.encrypt(temp, remoteKey);
        byte[] signature = RSA.sign(temp, selfPrivateKey);
        byte[] toSend = ArrayUtils.arrayConcat(cipherKey, signature);
        _outputStream.write(toSend);

        aes = new AES("GCM", _AESkey);
    }

    /**
     * It's only used as the return value in SecureServerSocket.accept()
     * 
     * @param AESkey
     * @param inputStream
     * @param outputStream
     * @param socket
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public SecureSocket(byte[] AESkey, InputStream inputStream, OutputStream outputStream, Socket socket)
            throws NoSuchAlgorithmException, NoSuchPaddingException {
        _AESkey = AESkey;
        _inputStream = inputStream;
        _outputStream = outputStream;
        _socket = socket;
        aes = new AES("GCM", _AESkey);
    }

    /**
     * send all data securely
     * 
     * @param data data you want to send
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws IOException
     */
    public void sendall(byte[] data) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
            InvalidAlgorithmParameterException, IOException {
        Random random = new Random();
        byte[] iv = new byte[96];
        byte[] aad = new byte[32];
        random.nextBytes(iv);
        random.nextBytes(aad);
        byte[] ciphertext = aes.encrypt(data, iv, aad);
        _outputStream.write(ArrayUtils.intToBytes(iv.length + aad.length + ciphertext.length));
        _outputStream.write(iv);
        _outputStream.write(aad);
        _outputStream.write(ciphertext);
    }

    /**
     * Receive all encrypted data
     * 
     * @return the data received.
     * @throws IOException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidAlgorithmParameterException
     */
    public byte[] recvall() throws IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
            InvalidAlgorithmParameterException {
        byte[] iv = new byte[96];
        byte[] aad = new byte[32];
        int length = ArrayUtils.bytesToInt(SocketUtils.recvall(_inputStream, 4));
        byte[] rawData = SocketUtils.recvall(_inputStream, length);
        iv = Arrays.copyOfRange(rawData, 0, 96);
        aad = Arrays.copyOfRange(rawData, 96, 128);
        byte[] ciphertext = Arrays.copyOfRange(rawData, 128, length);
        return aes.decrypt(ciphertext, iv, aad);
    }

    /**
     * Send file with secure socket, support large file
     * 
     * @param filePath the path of the file you want to send
     * @throws IOException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidAlgorithmParameterException
     */
    public void sendFile(String filePath) throws IOException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, InvalidAlgorithmParameterException {
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
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws IOException
     */
    public void recvFile(String savePath) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
            InvalidAlgorithmParameterException, IOException {
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
     * 
     * @return
     */
    public boolean isConnected() {
        return _socket.isConnected();
    }

    /**
     * Returns the closed state of the socket.
     * 
     * @return
     */
    public boolean isClosed() {
        return _socket.isClosed();
    }
}
