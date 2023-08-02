/**
 * The client setting panel. Extends from the {@link SettingPanel}
 * 
 * @author a-lives
 * @className ClientSettingPanel
 * @version 1.0
 * @date 2023-8-2
 */

public class ClientSettingPanel extends SettingPanel {

    private String _settingPath = "ClientSetting.properties";

    /**
     * Initalization
     * 
     * @param width  The width of the given space
     * @param height The height of the given space
     * @throws Exception
     */
    public ClientSettingPanel(int width, int height) throws Exception {
        super(width, height);
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

        SettingPanel.SettingGroup rsa_gen = addSettingGroup("GetKeyPair(RSA)");
        rsa_gen.addKeyPairGenerator();

        addSaveButton(_settingPath);
    }
}
