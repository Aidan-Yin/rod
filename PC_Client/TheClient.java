import javax.swing.JFrame;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

/**
 * Client UI implement
 * 
 * @author a-lives
 * @className TheClient
 * @version 1.0
 * @date 2023-7-22
 */

public class TheClient extends UI {

    private RemoteCMD _rcmd;
    private VideoStreamReceiver _vsr;

    /**
     * Initialization
     * 
     * @throws Exception
     */
    public TheClient() throws Exception {
        super(UI.CLIENT);

        _rcmd = new RemoteCMD();
        _vsr = new VideoStreamReceiver();
        _dropPanel = new DropPanel(UI.CLIENT);

        // initalize GridBagLayout
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.7, 0.3 };
        gridBagLayout.rowWeights = new double[] { 0.8, 0.2 };
        setLayout(gridBagLayout);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 2;
        constraints.fill = GridBagConstraints.BOTH;
        add(_dropPanel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.BOTH;
        add(_vsr, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.BOTH;
        add(_rcmd, constraints);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        try {
            TheClient theClient = new TheClient();
            theClient.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
