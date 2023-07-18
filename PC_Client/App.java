import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.FlowLayout;

/**
 * Sever UI implement
 * 
 * @author a-lives
 * @className App
 * @version 1.0
 * @date 2023-7-18
 */

public class App extends JFrame {

    private JPanel videoPanel;
    /** 
     *Initialization
     *  
     * @throws UnsupportedLookAndFeelException
     */
    public App() throws UnsupportedLookAndFeelException{
        setTitle("Rod-Server");
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Video stream show
        videoPanel = new JPanel();
        add(videoPanel);

        pack();
    }

    public JPanel getVideoPanel() {
        return videoPanel;
    }

    public static void main(String[] args) {
        try{
            App app = new App();
            app.setVisible(true);
            VideoStreamReceiver vsr = new VideoStreamReceiver("127.0.0.1", 6969, app.getVideoPanel());
            vsr.start();
        }catch (UnsupportedLookAndFeelException e){
            e.printStackTrace();
        }
        
    }
}
