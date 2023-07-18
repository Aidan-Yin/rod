import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

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
        setTitle("Rod-Client");


        // layout
        GridBagLayout gridBagLayout = new GridBagLayout();
        setLayout(gridBagLayout);
        
        videoPanel = new JPanel();  // Video stream show

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.fill = GridBagConstraints.BOTH;

        add(videoPanel,constraints);


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }

    public JPanel getVideoPanel() {
        return videoPanel;
    }

    public static void main(String[] args) {
        try{
            App app = new App();
            app.setVisible(true);
            // VideoStreamReceiver vsr = new VideoStreamReceiver("127.0.0.1", 6969, app.getVideoPanel());
            // vsr.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
