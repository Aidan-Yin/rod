import javax.swing.JFrame;

/**
 * The server can receive files/commands from the client, access local files,
 * terminals, and pass the local screen to the client.
 * 
 * @author a-lives
 * @className TheServer
 * @version 1.0
 * @date 2023-7-22
 */

public class TheServer extends UI {

    /**
     * Initialization
     * 
     * @throws Exception
     */
    public TheServer() throws Exception {
        super(UI.SERVER);
        _dropPanel = new DropPanel(UI.SERVER);
        add(_dropPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        try {
            TheServer theServer = new TheServer();
            theServer.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
