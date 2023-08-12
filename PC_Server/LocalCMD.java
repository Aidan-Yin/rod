import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * LocalCMD, use to bulid a cmd process and interact with remote client
 * treminal.
 * 
 * @author a-lives
 * @className LocalCMD
 * @version 1.2
 * @date 2023-8-12
 */
public class LocalCMD {

    public boolean _debug;
    public Process _process;
    public OutputStream _outputStream;
    public BufferedReader _inputStreamReader;
    public BufferedReader _errorStreamReader;

    private PrivateKey _privateKey;
    private PublicKey _publicKey;
    private String[] _vaildClients;

    private SecureServerSocket _serverSocket_input;
    private SecureSocket _secureSocket_input;
    private int _port_input;

    private SecureServerSocket _serverSocket_output;
    private SecureSocket _secureSocket_output;
    private int _port_output;

    private final ConcurrentLinkedQueue<String> _output_cache = new ConcurrentLinkedQueue<>();

    /**
     * 
     * @param privateKey
     * @param port_input
     * @param port_output
     * @throws IOException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws SignatureException
     * @throws InvalidKeySpecException
     */
    public LocalCMD(boolean debug, PrivateKey privateKey, PublicKey publicKey, String[] vaildClients, int port_input,
            int port_output)
            throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, SignatureException, InvalidKeySpecException {
        _debug = debug;
        _privateKey = privateKey;
        _publicKey = publicKey;
        _vaildClients = vaildClients;
        _port_input = port_input;
        _port_output = port_output;
        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe");
        _process = processBuilder.start();
        Log.log("Built cmd process.");
        _outputStream = _process.getOutputStream();
        _inputStreamReader = new BufferedReader(new InputStreamReader(_process.getInputStream(), "GBK"));
        _errorStreamReader = new BufferedReader(new InputStreamReader(_process.getErrorStream(), "GBK"));
    }

    /**
     * 
     * @throws IOException
     */
    public void addInputSocket() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    _serverSocket_input = new SecureServerSocket(_publicKey, _privateKey, _port_input);
                    Log.log("Built socket,waitting connection...: cmd-input;Port: " + _port_input);
                } catch (IOException e) {
                    if (_debug)
                        e.printStackTrace();
                }
                while (true) {
                    try {
                        _secureSocket_input = _serverSocket_input.accept(_vaildClients, new String[] { "GCM" });
                        Log.log("Connected to Client: cmd-input");
                        new Thread(() -> {
                            while (true) {
                                try {
                                    if (_secureSocket_input.isClosed())
                                        break;
                                    byte[] signal = _secureSocket_input.recvall();
                                    _outputStream.write((new String(signal) + "\n").getBytes());
                                    _outputStream.flush();
                                    Log.log("Received command: " + new String(signal));
                                } catch (IllegalBlockSizeException | IOException e) {
                                    if (_debug)
                                        e.printStackTrace();
                                    Log.log("Connection close: cmd-input");
                                    break;
                                }
                            }
                        }).start();
                    } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException
                            | InvalidKeySpecException
                            | SignatureException
                            | IOException e) {
                        if (_debug)
                            e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void addOutputSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    _serverSocket_output = new SecureServerSocket(_publicKey, _privateKey, _port_output);
                    Log.log("Built socket,waitting connection...: cmd-output; Port: " + _port_output);
                } catch (IOException e) {
                    if (_debug)
                        e.printStackTrace();
                }
                while (true) {
                    try {
                        _secureSocket_output = _serverSocket_output.accept(_vaildClients, new String[] { "GCM" });
                        Log.log("Connected to Client: cmd-output");
                        new Thread(() -> {
                            while (true) {
                                try {
                                    if (_secureSocket_output.isClosed())
                                        break;
                                    if (!_output_cache.isEmpty()) {
                                        _secureSocket_output.sendall(_output_cache.poll().getBytes());
                                        // Log.log("sended msg");
                                        Thread.sleep(20);
                                    }
                                } catch (IllegalBlockSizeException | IOException | InterruptedException e) {
                                    if (_debug)
                                        e.printStackTrace();
                                    Log.log("Connection close: cmd-output");
                                    break;
                                }
                            }
                        }).start();
                        ;
                    } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException
                            | InvalidKeySpecException
                            | SignatureException
                            | IOException e) {
                        if (_debug)
                            e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * get msg from cmd process and store in a output cache
     */
    public void addMsgGeter() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String line_msg;
                // String line_error;
                for (;;) {
                    try {
                        Thread.sleep(20);
                        if ((line_msg = _inputStreamReader.readLine()) != null) {
                            System.out.println("msg: " + line_msg);
                            _output_cache.offer(line_msg);
                        }
                    } catch (IOException | InterruptedException e) {
                        if (_debug)
                            e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * get error from cmd process and store in a msg cache
     */
    public void addErrGeter() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String line_msg;
                // String line_error;
                for (;;) {
                    try {
                        Thread.sleep(20);
                        if ((line_msg = _errorStreamReader.readLine()) != null) {
                            System.out.println("err: " + line_msg);
                            _output_cache.offer(line_msg);
                        }
                    } catch (IOException | InterruptedException e) {
                        if (_debug)
                            e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * exit
     * 
     * @return
     * @throws InterruptedException
     */
    public int exit() throws InterruptedException {
        return _process.waitFor();
    }
}
