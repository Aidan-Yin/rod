import javax.swing.JPanel;
import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * A control that is responsible for playing the server screen.
 * 
 * @author a-lives
 * @className RemoteCMD
 * @version 1.0
 * @date 2023-7-22
 */

public class VideoStreamReceiver extends JPanel {

    public String _serverIP;
    public int _serverPort;
    private BufferedImage image;

    /**
     * Initialization
     * 
     * @throws Exception
     */
    public VideoStreamReceiver() throws Exception {
        setBackground(Color.BLACK);
    }

    /**
     * Your own watch
     * 
     * @param imageData
     * @throws IOException
     */
    private void setImageFromBytes(byte[] imageData) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
        image = ImageIO.read(inputStream);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        }
    }

    /**
     * Your own watch
     * 
     * @throws Exception
     */
    public void connect() throws Exception {
        // 这里开始接收数据并转换为视频流播放
    }
}
