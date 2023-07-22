import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.JViewport;

import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.util.ArrayList;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

/**
 * A session window for text, file transfer and recording.
 * 
 * @author a-lives
 * @className DropPanel
 * @version 1.0
 * @date 2023-7-22
 */

public class DropPanel extends JPanel {

    private String user;
    private JScrollPane _DialogArea;
    private JTextPane _inputArea;
    private JButton _sendButton;
    private ArrayList<String> _HTMLContent;

    public DropPanel(String UItype) {
        user = UItype;
        JTextPane DialogInit = new JTextPane();
        DialogInit.setEditable(false);
        _inputArea = new JTextPane();
        _inputArea.setEditable(true);
        _sendButton = new JButton("send");

        GridBagLayout gbl = new GridBagLayout();
        gbl.columnWidths = new int[] { 0, 0 };
        gbl.rowHeights = new int[] { 0, 0 };
        gbl.columnWeights = new double[] { 0.9, 0.1 };
        gbl.rowWeights = new double[] { 0.9, 0.1 };
        setLayout(gbl);

        // style
        DialogInit.setContentType("text/html");
        _HTMLContent = new ArrayList<>();
        _HTMLContent.add("<i>let's chat!</i><br>");
        DialogInit.setText(bulidHTML());

        _sendButton.addActionListener(e -> {
            sendText();
        });

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.BOTH;
        _DialogArea = new JScrollPane(DialogInit);
        add(_DialogArea, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.BOTH;
        add(_inputArea, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.BOTH;
        add(_sendButton, constraints);

    }

    private static String escapeHTML(String html) {
        return html.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;")
                .replace("\n", "<br>");
    }

    private String bulidHTML() {
        String html = "";
        html += "<html><body>";
        for (String cont : _HTMLContent) {
            html += cont;
        }
        html += "</html></body>";
        return html;
    }

    /**
     * Add text to DialogArea.
     * 
     * @param content
     */
    public void addText(String content) {

        _HTMLContent.add("<p><b>" + user + ": </b>" + escapeHTML(content) + "</p>");
        JViewport jv = _DialogArea.getViewport();
        JTextPane jtp = (JTextPane) jv.getView();
        jtp.setText(bulidHTML());
        JScrollBar vScrollBar = _DialogArea.getVerticalScrollBar();
        vScrollBar.setValue(vScrollBar.getMaximum());
    }

    /**
     * Add img to DialogArea.
     * 
     * @param url
     */
    public void addImg(String url) {

    }

    /**
     * Send text to the other end
     * 
     */
    public void sendText() {
        addText(_inputArea.getText());

    }

    /**
     * Send file to the other end
     * 
     */
    public void sendFile() {

    }
}
