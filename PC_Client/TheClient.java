import javax.swing.JFrame;

import java.awt.GridBagLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;
import java.awt.GridBagConstraints;

/**
 * Client UI implement, extends from {@link UIFrame}.
 * 
 * @author a-lives
 * @className TheClient
 * @version 1.2
 * @date 2023-8-12
 */

public class TheClient extends UIFrame {

    public static boolean _debug = true;

    public RemoteCMD _rcmd;
    public VideoPlayer _vsr;
    public ClientSettingPanel _csp;
    // private DropPanel _dropPanel;

    public PrivateKey _privateKey;
    public String _serverIP;
    public int _serverPort_video;
    public int _serverPort_mouse;
    public int _serverPort_cmd_send;
    public int _serverPort_cmd_recv;

    public String _settingPath = "ClientSetting.properties";

    /**
     * Initialization
     * 
     * @throws Exception
     */
    public TheClient() throws Exception {
        super("Rod-Client", 0.8, 0.8);

        loadSettingFromProp(_settingPath);

        _rcmd = new RemoteCMD(_debug, _privateKey, _serverIP, _serverPort_cmd_send, _serverPort_cmd_recv);
        _vsr = new VideoPlayer(_debug, _privateKey, _serverIP, _serverPort_video, _serverPort_mouse);
        _csp = new ClientSettingPanel((int) (getWidth() * 0.2), getHeight(), _settingPath, this);
        // _dropPanel = new DropPanel("Rod-Client");

        // initalize GridBagLayout
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.8, 0.2 };
        gridBagLayout.rowWeights = new double[] { 0.8, 0.2 };
        setLayout(gridBagLayout);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 2;
        constraints.fill = GridBagConstraints.BOTH;
        add(_csp, constraints);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.BOTH;
        add(_vsr, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.BOTH;
        add(_rcmd, constraints);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * load settings from a properties file, if not, load default values.
     * 
     * @param path setting file path
     * @throws FileNotFoundException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public void loadSettingFromProp(String path)
            throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Properties prop = new Properties();
        File file = new File(path);
        if (file.exists()) {
            prop.load(new FileInputStream(path));
            Log.log("Loaded settings: Client");
        } else {
            Log.log("Not found setting file, loaded default values: Client");
        }
        // load basic args
        _privateKey = RSA.getPrivateKeyFromBase64(prop.getProperty("privateKey",
                "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"));
        _serverIP = prop.getProperty("serverIP", "127.0.0.1");
        _serverPort_video = Integer.parseInt(prop.getProperty("serverPort_video", "8080"));
        _serverPort_mouse = Integer.parseInt(prop.getProperty("serverPort_mouse", "8081"));
        _serverPort_cmd_send = Integer.parseInt(prop.getProperty("serverPort_cmd_input", "8082"));
        _serverPort_cmd_recv = Integer.parseInt(prop.getProperty("serverPort_cmd_output", "8083"));
    }

    public static void main(String[] args) {
        try {
            TheClient theClient = new TheClient();
            theClient.setVisible(true);
        } catch (Exception e) {
            if (TheClient._debug)
                e.printStackTrace();
        }
    }
}
