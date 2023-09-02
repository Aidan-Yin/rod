import java.awt.Component;
// import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.swing.Box;
import javax.swing.JButton;
// import javax.swing.JDialog;
// import javax.swing.JLabel;

/**
 * The client setting panel. Extends from the {@link SettingPanel}
 * 
 * @author a-lives
 * @className ClientSettingPanel
 * @version 1.2
 * @date 2023-8-5
 */

public class ClientSettingPanel extends SettingPanel {

    private String _settingPath;

    /**
     * Initalization
     * 
     * @param width  The width of the given space
     * @param height The height of the given space
     * @throws Exception
     */
    public ClientSettingPanel(int width, int height, String settingPath, TheClient parent) throws Exception {
        super(width, height);
        _settingPath = settingPath;
        _prop = loadSetting(_settingPath);

        // add SettingGroups
        SettingPanel.SettingGroup key_group = addSettingGroup("Key(RSA)");
        key_group.addTextField("privateKey", _prop.getProperty("privateKey"));

        SettingPanel.SettingGroup server_group = addSettingGroup("Server");
        server_group.addTextField("serverIP", _prop.getProperty("serverIP", "127.0.0.1"));
        server_group.addTextField("serverPort:video", _prop.getProperty("serverPort_video", "8080"));
        server_group.addTextField("serverPort:mouse", _prop.getProperty("serverPort_mouse", "8081"));
        server_group.addTextField("serverPort:cmdInput", _prop.getProperty("serverPort_cmd_input", "8082"));
        server_group.addTextField("serverPort:cmdOutput", _prop.getProperty("serverPort_cmd_output", "8083"));

        // add key pair generator
        SettingPanel.SettingGroup rsa_gen = addSettingGroup("NewKeyPair(RSA)");
        rsa_gen.addKeyPairGenerator();

        // add savebutton and alert dialog.
        JButton sb = addSaveButton(_settingPath);
        sb.addActionListener(e -> {
            // method 1
            
            try {
                parent.loadSettingFromProp(_settingPath);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e1) {
                e1.printStackTrace();
            } 

            // method2

            // JDialog dialog = new JDialog(parent, "!");
            // dialog.add(new JLabel("     Restart to apply the changes!"));
            // Rectangle rect = parent.getBounds();
            // dialog.setBounds((int) (rect.getCenterX() - rect.getWidth() * 0.1),
            //         (int) (rect.getCenterY() - rect.getHeight() * 0.08), (int) (rect.getWidth() * 0.2),
            //         (int) (rect.getHeight() * 0.16));
            // dialog.setVisible(true);
        });

        // add reload buttons
        addReloadButton("Reload:VideoPlayer", e -> {
            parent._vsr.reload(parent._privateKey, parent._serverIP, parent._serverPort_video,
                    parent._serverPort_mouse);
        });
        addReloadButton("Reload:cmd", e -> {
            parent._rcmd.reload(parent._privateKey, parent._serverIP, parent._serverPort_cmd_send,
                    parent._serverPort_cmd_recv);
        });
    }

    public void addReloadButton(String name, ActionListener actionListener) {
        JButton sb = new JButton(name);
        sb.setSize((int) (_width * 0.03), (int) (_height * 9.3));
        sb.setBackground(_textColor);
        sb.setForeground(_backgroundColor);
        sb.setAlignmentX(Component.LEFT_ALIGNMENT);
        sb.addActionListener(e -> {
            actionListener.actionPerformed(e);
        });
        add(sb);
        add(Box.createVerticalStrut(_spaceBetweenGroup));
    }
}
