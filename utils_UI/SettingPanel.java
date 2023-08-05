import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.Box;
import javax.swing.BoxLayout;

import java.awt.Component;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.Properties;

/**
 * Setting panel used to change setting.
 * It is a frame.
 * 
 * @author a-lives
 * @className SettingPanel
 * @version 1.2
 * @date 2023-8-2
 */
public class SettingPanel extends JPanel {

    public static final double XPRO = 0.4;
    public static final double YPRO = 0.72;
    protected Properties _prop;
    protected Font _firstTitleFont;
    protected Font _secondTitleFont;
    protected Font _contFont;
    protected Color _backgroundColor = new Color(47, 54, 64);
    protected Color _textColor = new Color(220, 221, 225);
    protected int _width;
    protected int _height;
    protected int _spaceBetweenGroup;
    protected int _spaceBetweenSets;

    /**
     * Initalization
     * 
     * @param parent a UIFrame
     * @throws Exception
     */
    public SettingPanel(UIFrame parent) throws Exception {
        _width = parent.getWidth();
        _height = parent.getHeight();
        _firstTitleFont = new Font(null, Font.BOLD, (int) (_height * 0.03));
        _secondTitleFont = new Font(null, Font.PLAIN, (int) (_height * 0.024));
        _contFont = new Font(null, Font.PLAIN, (int) (_height * 0.022));
        _spaceBetweenGroup = (int) (_height * 0.02);
        _spaceBetweenSets = (int) (_height * 0.015);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(_backgroundColor);
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    /**
     * Initalization
     * 
     * @param width  panel width
     * @param height panel height
     * @throws Exception
     */
    public SettingPanel(int width, int height) throws Exception {
        _width = width;
        _height = height;
        _firstTitleFont = new Font(null, Font.BOLD, (int) (_height * 0.03));
        _secondTitleFont = new Font(null, Font.PLAIN, (int) (_height * 0.024));
        _contFont = new Font(null, Font.PLAIN, (int) (_height * 0.022));
        _spaceBetweenGroup = (int) (_height * 0.02);
        _spaceBetweenSets = (int) (_height * 0.015);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(_backgroundColor);
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    /**
     * Setting group!
     */
    public class SettingGroup extends JPanel {

        /**
         * a setting group.
         * 
         * @param title title
         */
        public SettingGroup(String title) {
            this.setBackground(_backgroundColor);
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel titleLabel = new JLabel(title);
            titleLabel.setForeground(_textColor);
            titleLabel.setFont(_firstTitleFont);
            this.add(titleLabel);
            titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        /**
         * 
         * @param key   the label of the check box
         * @param value if <code>true</code>, is checked
         */
        public void addCheckBox(String key, boolean value) {
            JCheckBox cb = new JCheckBox(key, value);
            cb.setBackground(_backgroundColor);
            cb.setForeground(_textColor);
            cb.setFont(_contFont);
            cb.addActionListener(e -> {
                changeSetting(key, String.valueOf(cb.isSelected()));
            });
            this.add(cb);
            cb.setAlignmentX(Component.LEFT_ALIGNMENT);
            this.add(Box.createVerticalStrut(_spaceBetweenSets));
        }

        /**
         * 
         * @param key    the label of the group
         * @param values the values can be chosen
         * @param value  the value witch has been chosen
         */
        public void addRadioButtonGroup(String key, String[] values, String value) {
            ButtonGroup bg = new ButtonGroup();
            JLabel l = new JLabel(key + ":");
            l.setForeground(_textColor);
            l.setFont(_secondTitleFont);
            this.add(l);
            for (String v : values) {
                JRadioButton rb = new JRadioButton(v);
                if (v.equals(value))
                    rb.setSelected(true);
                ;
                rb.setBackground(_backgroundColor);
                rb.setForeground(_textColor);
                rb.setFont(_contFont);
                rb.addActionListener(e -> {
                    changeSetting(key, e.getActionCommand());
                });
                bg.add(rb);
                this.add(rb);
                rb.setAlignmentX(Component.LEFT_ALIGNMENT);
            }
            this.add(Box.createVerticalStrut(_spaceBetweenSets));
        }

        /**
         * 
         */
        public void addComboBox() {
            // reserve
        }

        /**
         * 
         * @param key   the label of the text field
         * @param value default value
         */
        public void addTextField(String key, String value) {
            JLabel l = new JLabel(key + ": ");
            l.setForeground(_textColor);
            l.setFont(_secondTitleFont);
            JTextField ta = new JTextField(value);
            ta.setMaximumSize(new Dimension((int) (_width * 0.9), (int) (_height * 0.024)));
            ta.addActionListener(e -> {
                changeSetting(key, ta.getText());
            });
            Box b = Box.createHorizontalBox();
            b.add(l);
            b.add(ta);
            this.add(b);
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            this.add(Box.createVerticalStrut(_spaceBetweenSets));
        }

        public void addKeyPairGenerator() {
            JButton getButton = new JButton("get");
            getButton.setSize((int) (_width * 0.03), (int) (_height * 9.3));
            getButton.setBackground(_textColor);
            getButton.setForeground(_backgroundColor);
            JLabel priLab = new JLabel("privateKey: ");
            JLabel pubLab = new JLabel("publicKey: ");
            priLab.setForeground(_textColor);
            priLab.setFont(_secondTitleFont);
            pubLab.setForeground(_textColor);
            pubLab.setFont(_secondTitleFont);
            JTextField priKey = new JTextField();
            JTextField pubKey = new JTextField();
            priKey.setEditable(false);
            pubKey.setEditable(false);
            priKey.setMaximumSize(new Dimension((int) (_width * 0.9), (int) (_height * 0.024)));
            pubKey.setMaximumSize(new Dimension((int) (_width * 0.9), (int) (_height * 0.024)));
            getButton.addActionListener(e -> {
                RSA rsa = new RSA(4096);
                priKey.setText(rsa.getBase64PrivateKey());
                pubKey.setText(rsa.getBase64PublicKey());
            });
            Box b1 = Box.createHorizontalBox();
            Box b2 = Box.createHorizontalBox();
            b1.add(priLab);
            b1.add(priKey);
            b2.add(pubLab);
            b2.add(pubKey);
            this.add(b1);
            this.add(b2);
            this.add(getButton);
            b1.setAlignmentX(Component.LEFT_ALIGNMENT);
            b2.setAlignmentX(Component.LEFT_ALIGNMENT);
            getButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            this.add(Box.createVerticalStrut(_spaceBetweenSets));
        }
    }

    /**
     * 
     * @param title
     * @return a class of {@link SettingGroup}, you can add some setting utils in
     *         it.
     */
    public SettingGroup addSettingGroup(String title) {
        SettingGroup sg = new SettingGroup(title);
        add(sg);
        add(Box.createVerticalStrut(_spaceBetweenGroup));
        return sg;
    }

    /**
     * add a <code>JButton</code> as save button. Extremely important, must be
     * called once!
     */
    public JButton addSaveButton(String path) {
        JButton sb = new JButton("save");
        sb.setSize((int) (_width * 0.03), (int) (_height * 9.3));
        sb.setBackground(_textColor);
        sb.setForeground(_backgroundColor);
        sb.setAlignmentX(Component.LEFT_ALIGNMENT);
        sb.addActionListener(e -> {
            try {
                saveSetting(path);
            } catch (FileNotFoundException exp) {
                exp.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        add(sb);
        add(Box.createVerticalStrut(_spaceBetweenGroup));
        return sb;
    }

    /**
     * use to change setting
     * 
     * @param key
     * @param value
     */
    private void changeSetting(String key, String value) {
        Log.log("Changed: " + key + " is " + value);
        _prop.setProperty(key, value);
    }

    /**
     * use to save setting to properties file
     * 
     * @param path file path
     * @throws IOException
     * @throws FileNotFoundException
     */
    private void saveSetting(String path) throws IOException, FileNotFoundException {
        FileOutputStream fos = new FileOutputStream(path);
        _prop.store(fos, "setting");
        fos.close();
        Log.log("Setting saved in " + path);
    }

    /**
     * use to load setting from properties file
     * 
     * @param path file path
     * @return a <code>Properties</code>
     * @throws IOException
     * @throws FileNotFoundException
     */
    public Properties loadSetting(String path) throws IOException, FileNotFoundException {
        Properties prop = new Properties();
        File file = new File(path);
        if (file.exists()) {
            prop.load(new FileInputStream(path));
            Log.log("Loaded settings: Panel");
        } else {
            Log.log("Not found setting file, loaded default values: Panel");
        }
        return prop;
    }
}
