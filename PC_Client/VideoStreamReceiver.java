import javax.swing.JPanel;
import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;



public class VideoStreamReceiver extends JPanel{
    public String _serverIP;
    public int _serverPort;

    private BufferedImage image;

    public VideoStreamReceiver(){
        setBackground(Color.BLACK);
    } 
    public VideoStreamReceiver(String serverIP, int serverPort) {
        _serverIP = serverIP;
        _serverPort = serverPort;   

        setBackground(Color.BLACK);
    }

    public void setImageFromBytes(byte[] imageData) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
            image = ImageIO.read(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        }
    }

    public void connect() {
        try {
            // 这里开始接收数据并转换为视频流播放
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

