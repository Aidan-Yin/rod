import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.JTextArea;

import java.awt.GridBagLayout;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.awt.Color;
import java.awt.GridBagConstraints;

/**
 * A remote command prompt that connects directly to the server.
 * 
 * @author a-lives
 * @className RemoteCMD
 * @version 1.1
 * @date 2023-7-29
 */

public class RemoteCMD extends JPanel {

    private JTextField _inputField;
    private JTextArea _outputArea;
    private JScrollPane _outputPane;

    private SecureSocket _sendSocket;
    private SecureSocket _recvSocket;

    public RemoteCMD(PrivateKey privateKey, String serverIP, int sendPort, int recvPort) throws Exception {
        _inputField = new JTextField();
        _inputField.setEditable(true);
        _outputArea = new JTextArea();
        _outputArea.setEditable(false);

        // set style
        _inputField.setBackground(Color.BLACK);
        _outputArea.setBackground(Color.BLACK);
        _inputField.setForeground(Color.WHITE);
        _outputArea.setForeground(Color.WHITE);
        _inputField.setCaretColor(Color.WHITE);

        _inputField.addActionListener(e -> {
            try {
                sendCommand();
                _inputField.setText("");
            } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException
                    | InvalidAlgorithmParameterException | IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });

        GridBagLayout gbl = new GridBagLayout();
        gbl.columnWidths = new int[] { 0 };
        gbl.columnWeights = new double[] { 1.0 };
        gbl.rowHeights = new int[] { 0, 0 };
        gbl.rowWeights = new double[] { 0.9, 0.1 };
        setLayout(gbl);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.BOTH;
        _outputPane = new JScrollPane(_outputArea);
        add(_outputPane, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.BOTH;
        add(_inputField, constraints);

        // bulid sockets
        _sendSocket = new SecureSocket(privateKey, serverIP, sendPort);
        Log.log("connected: cmd-send");
        _recvSocket = new SecureSocket(privateKey, serverIP, recvPort);
        Log.log("connected: cmd-recv");

        addMsgRecviver();
    }

    /**
     * send command
     * 
     * @param command
     * @throws IOException
     * @throws InvalidAlgorithmParameterException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     */
    private void sendCommand() throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
            InvalidAlgorithmParameterException, IOException {
        _sendSocket.sendall(_inputField.getText().getBytes());
    }

    public void addText(String content) {
        JViewport jv = _outputPane.getViewport();
        JTextArea jtp = (JTextArea) jv.getView();
        jtp.setText(jtp.getText() + "\n" + content);
        JScrollBar vScrollBar = _outputPane.getVerticalScrollBar();
        vScrollBar.setValue(vScrollBar.getMaximum());
    }

    private void addMsgRecviver() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(20);
                        if (_recvSocket.isClosed())
                            break;
                        byte[] data = _recvSocket.recvall();
                        addText(new String(data));
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }

        }).start();
        // socket.close();
    }
}
