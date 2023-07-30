import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

/**
 * ServerSocket with RSA handshake and AES encrypt transmission.
 * 
 * @author Yin
 * @className SecureServerSocket
 * @date 2023-7-30
 */
public class SecureServerSocket {
    public ServerSocket serverSocket;
    public PrivateKey _selfPrivateKey;
    public PublicKey _selfPublicKey;
    public int TIMEOUT_PERIOD = 1000; // milliseconds

    /**
     * Creates an unbound SecureServerSocket.
     * 
     * @param selfPublicKey
     * @param selfPrivateKey
     * @throws IOException
     */
    public SecureServerSocket(PublicKey selfPublicKey, PrivateKey selfPrivateKey) throws IOException {
        _selfPrivateKey = selfPrivateKey;
        _selfPublicKey = selfPublicKey;
        serverSocket = new ServerSocket();
    }

    /**
     * Creates a SecureServerSocket, bound to the specified port.
     * 
     * @param selfPublicKey
     * @param selfPrivateKey
     * @param port
     * @throws IOException
     */
    public SecureServerSocket(PublicKey selfPublicKey, PrivateKey selfPrivateKey, int port) throws IOException {
        _selfPrivateKey = selfPrivateKey;
        _selfPublicKey = selfPublicKey;
        serverSocket = new ServerSocket(port);
    }

    /**
     * Creates a SecureServerSocket and binds it to the specified local port number,
     * with the specified backlog.
     * 
     * @param selfPublicKey
     * @param selfPrivateKey
     * @param port
     * @param backlog
     * @throws IOException
     */
    public SecureServerSocket(PublicKey selfPublicKey, PrivateKey selfPrivateKey, int port, int backlog)
            throws IOException {
        _selfPrivateKey = selfPrivateKey;
        _selfPublicKey = selfPublicKey;
        serverSocket = new ServerSocket(port, backlog);
    }

    /**
     * Create a SecureServerSocket with the specified port, listen backlog, and
     * local IP address to bind to.
     * 
     * @param selfPublicKey
     * @param selfPrivateKey
     * @param port
     * @param backlog
     * @param bindAddr
     * @throws IOException
     */
    public SecureServerSocket(PublicKey selfPublicKey, PrivateKey selfPrivateKey, int port, int backlog,
            InetAddress bindAddr)
            throws IOException {
        _selfPrivateKey = selfPrivateKey;
        _selfPublicKey = selfPublicKey;
        serverSocket = new ServerSocket(port, backlog, bindAddr);
    }

    /**
     * Binds the ServerSocket to a specific address (IP address and port number).
     * 
     * @param endpoint The IP address and port number to bind to.
     * @throws IOException
     */
    public void bind(SocketAddress endpoint) throws IOException {
        serverSocket.bind(endpoint);
    }

    /**
     * Listens for a connection to be made to this socket and accepts it. The method
     * blocks until a connection is made.
     * (Old version, use GCM mode only)
     * 
     * @param arrayOfValidClientRSAkey only the signature by PrivateKey whose paired
     *                                 PublicKey in the array will be accepted.
     * @return the SecureSocket to communicate with the client.
     * @throws IOException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeySpecException
     * @throws SignatureException
     */
    public SecureSocket accept(String[] arrayOfValidClientRSAkey) throws IOException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException, SignatureException, InvalidKeySpecException {
        /*
         * connect and handshake
         * 1. server wait for connection (accept)
         * 2. client connect server
         * 3. server send its public key to client
         * 4. client send AES key encrypted by remoteKey and signed by selfKey
         * 5. server verify the signature and set AES key
         */

        // step 1
        Socket client = serverSocket.accept();
        InputStream cis = client.getInputStream();
        OutputStream cos = client.getOutputStream();

        // step 3
        cos.write(_selfPublicKey.getEncoded());

        byte[] buf = new byte[1024]; // use RSA-4096
        SocketUtils.recvall(cis, buf);
        long timestamp = System.currentTimeMillis();

        byte[] AESkeyCiphertext = Arrays.copyOfRange(buf, 0, 512);
        byte[] signature = Arrays.copyOfRange(buf, 512, 1024);
        byte[] temp = RSA.decrypt(AESkeyCiphertext, _selfPrivateKey);
        byte[] AESkey = Arrays.copyOfRange(temp, 0, 16);

        // verify time stamp
        if (ArrayUtils.bytesToLong(Arrays.copyOfRange(temp, 16, 24)) - timestamp > TIMEOUT_PERIOD) {
            throw new RuntimeException("Timeout!");
        }

        // step 5
        PublicKey remoteKey;
        for (int i = 0; i < arrayOfValidClientRSAkey.length; i++) {
            remoteKey = RSA.getPublicKeyFromBase64(arrayOfValidClientRSAkey[i]);
            if (RSA.verify(temp, signature, remoteKey)) {
                // use GCM mode by default
                return new SecureSocket(AESkey, cis, cos, client, "GCM");
            }
        }
        return null;
    }

    /**
     * Listens for a connection to be made to this socket and accepts it. The method
     * blocks until a connection is made.
     * 
     * @param arrayOfValidClientRSAkey only the signature by PrivateKey whose paired
     *                                 PublicKey in the array will be accepted.
     * @param encryptionMode           Encryption modes that can be used.
     * @return the SecureSocket to communicate with the client.
     * @throws IOException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public SecureSocket accept(String[] arrayOfValidClientRSAkey, String[] encryptionMode)
            throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException,
            InvalidKeyException, SignatureException {
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

        // step 1
        Socket client = serverSocket.accept();
        InputStream cis = client.getInputStream();
        OutputStream cos = client.getOutputStream();

        // step 3
        cos.write(_selfPublicKey.getEncoded());

        // step 4
        byte[] buf = new byte[1024]; // use RSA-4096
        SocketUtils.recvall(cis, buf);

        long timestamp = System.currentTimeMillis();
        byte[] AESkeyCiphertext = Arrays.copyOfRange(buf, 0, 512);
        byte[] signature = Arrays.copyOfRange(buf, 512, 1024);
        byte[] temp = RSA.decrypt(AESkeyCiphertext, _selfPrivateKey);
        String clientEncryptionMode = new String(temp, 0, 3);
        byte[] AESkey = Arrays.copyOfRange(temp, 3, 19);

        // verify time stamp
        if (ArrayUtils.bytesToLong(Arrays.copyOfRange(temp, 19, 27)) - timestamp > TIMEOUT_PERIOD) {
            cis.close();
            cos.close();
            client.close();
            throw new RuntimeException("Timeout!");
        }

        // step 6
        PublicKey remoteKey;
        for (int i = 0; i < arrayOfValidClientRSAkey.length; i++) {
            remoteKey = RSA.getPublicKeyFromBase64(arrayOfValidClientRSAkey[i]);
            if (RSA.verify(temp, signature, remoteKey)) {
                if (Arrays.asList(encryptionMode).contains(clientEncryptionMode)) {
                    cos.write("ok".getBytes());
                    return new SecureSocket(AESkey, cis, cos, client, clientEncryptionMode);
                } else {
                    cis.close();
                    cos.close();
                    client.close();
                }
            }
        }
        return null;
    }
}
