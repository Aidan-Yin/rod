import javax.swing.JFrame;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Server and Client UI implement.
 * 
 * @author a-lives
 * @version 1.0
 * @date 7-21
 */

public class UI extends JFrame {

    public static final String SERVER = "Rod-Server";
    public static final String CLIENT = "Rod-Client";
    public DropPanel _dropPanel;
    private final Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();

    public UI(String UItype) throws Exception {
        super(UItype);

        int screenHeight = screensize.height;
        int screenWidth = screensize.width;

        if (UItype == SERVER) {
            setLocation((int) (screenWidth * 0.3), (int) (screenHeight * 0.15));
            setSize((int) (screenWidth * 0.4), (int) (screenHeight * 0.7));

        } else if (UItype == CLIENT) {
            setLocation((int) (screenWidth * 0.18), (int) (screenHeight * 0.15));
            setSize((int) (screenWidth * 0.64), (int) (screenHeight * 0.7));

        } else {
            throw new Exception("not a vaild UI type");
        }
    }
}
