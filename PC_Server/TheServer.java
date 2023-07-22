import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

/**
 * The server can receive files/commands from the client, access local files, terminals, and pass the local screen to the client.
 * 
 * 
 * 
 */


public class TheServer extends UI{

    public TheServer() throws Exception{
        super(UI.SERVER);
        _dropPanel = new DropPanel(UI.SERVER);
        add(_dropPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args){
        try{
            TheServer theServer = new TheServer();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
