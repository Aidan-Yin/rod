import javax.swing.JFrame;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Server and Client UI implement.
 * 
 * @author a-lives
 * @version 1.0
 * @date 7-23
 */

public class UIFrame extends JFrame {

    private final Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();

    /**
     * The window is centered. If you need to adjust the position, use the xbias and
     * ybias parameters of the other constructor
     * 
     * @param title title
     * @param xpro  The proportion of the horizontal window to the screen
     * @param ypro  The proportion of the vertical window to the screen
     * @throws Exception
     */
    public UIFrame(String title, double xpro, double ypro) throws Exception {
        super(title);

        int screenHeight = screensize.height;
        int screenWidth = screensize.width;

        setLocation((int) (screenWidth * (1 -xpro)*0.5), (int) (screenHeight * (1 -ypro)*0.5));
        setSize((int) (screenWidth * xpro), (int) (screenHeight * ypro));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * If the bias parameter is zero, the window is centered
     * 
     * @param title title
     * @param xpro  The proportion of the horizontal window to the screen
     * @param ypro  The proportion of the vertical window to the screen
     * @param xbias Percentage of the window's horizontal downward shift
     * @param ybias Percentage of the window's vertica downward shift
     * @throws Exception
     */
    public UIFrame(String title, double xpro, double ypro, double xbias, double ybias) throws Exception {
        super(title);

        int screenHeight = screensize.height;
        int screenWidth = screensize.width;

        setLocation((int) (screenWidth * (0.5 - xpro*0.5 + xbias)), (int) (screenHeight * (0.5 - ypro*0.5 + ybias)));
        setSize((int) (screenWidth * xpro), (int) (screenHeight * ypro));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}
