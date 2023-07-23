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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.Properties;

/**
 * Setting panel used to change setting
 * 
 * @author a-lives
 * @version 1.0
 * @date 7-23
 */
public class SettingPanel extends JPanel {

    public static final double XPRO = 0.4;
    public static final double YPRO = 0.72;
    private Properties _prop;
    private Font _firstTitleFont;
    private Font _secondTitleFont;
    private Font _contFont;
    private Color _backgroundColor = new Color(47, 54, 64);
    private Color _textColor = new Color(220, 221, 225);
    private int _width;
    private int _height;
    private int _spaceBetweenGroup;
    private int _spaceBetweenSets;

    /**
     * Initalization
     * 
     * @param title
     * @param parent a UIFrame
     * @throws Exception
     */
    public SettingPanel(String title, UIFrame parent) throws Exception {
        _prop = loadSetting("setting.prop");
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
     * Setting group!
     * 
     * 
     */
    public class SettingGroup extends JPanel {

        /**
         * 
         * @param title
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
         * @param key
         * @param value
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
         * @param key
         * @param values
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
         * @param key
         * @param value
         */
        public void addTextArea(String key, String value) {
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

    }

    /**
     * 
     * @param title
     * @return a class of SettingGroup, you can add some setting utils in it.
     */
    public SettingGroup addSettingGroup(String title) {
        SettingGroup sg = new SettingGroup(title);
        add(sg);
        add(Box.createVerticalStrut(_spaceBetweenGroup));
        return sg;
    }

    /**
     * add the save button.extremely important,must be called once
     */
    public void addSaveButton() {
        JButton sb = new JButton("save");
        sb.setSize((int) (_width * 0.03), (int) (_height * 9.3));
        sb.setBackground(_textColor);
        sb.setForeground(_backgroundColor);
        sb.setAlignmentX(LEFT_ALIGNMENT);
        sb.addActionListener(e -> {
            try {
                saveSetting("setting.prop");
            } catch (FileNotFoundException exp) {
                exp.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        add(sb);
    }

    /**
     * use to change setting
     * 
     * @param key
     * @param value
     */
    private void changeSetting(String key, String value) {
        System.out.println(key + " is " + value);
        _prop.setProperty(key, value);
    }

    /**
     * user to save setting to properties file
     * 
     * @param path
     * @throws IOException
     * @throws FileNotFoundException
     */
    private void saveSetting(String path) throws IOException, FileNotFoundException {
        FileOutputStream fos = new FileOutputStream(path);
        _prop.store(fos, "setting");
        fos.close();
    }

    /**
     * use to load setting from properties file
     * 
     * @param path
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     */
    public Properties loadSetting(String path) throws IOException, FileNotFoundException {
        Properties prop = new Properties();
        prop.load(new FileInputStream(path));
        return prop;
    }
}
