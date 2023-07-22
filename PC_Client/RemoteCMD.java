import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;


public class RemoteCMD extends JPanel{
    
    private JTextField _inputField;
    private JTextArea _outputArea;

    public RemoteCMD(){
        _inputField = new JTextField();
        _inputField.setEditable(true);
        _outputArea = new JTextArea();
        _outputArea.setEditable(false);

        _outputArea.setText("This is the remote cmd");

        _inputField.addActionListener(e->{
            sendCommand(_inputField.getText());
        });

        GridBagLayout gbl = new GridBagLayout();
        gbl.columnWidths = new int[] {0};
        gbl.columnWeights = new double[] {1.0};
        gbl.rowHeights = new int[] {0,0};
        gbl.rowWeights = new double[] {0.9,0.1};
        setLayout(gbl);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.BOTH;
        add(_outputArea,constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.BOTH;
        add(_inputField,constraints);
    }

    private void sendCommand(String command){
        // 这里写命令发出的事件。
    }

}
