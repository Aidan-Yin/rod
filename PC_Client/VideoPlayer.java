import javax.swing.JPanel;
import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.PrivateKey;

/**
 * A control that is responsible for playing the server screen.
 * 
 * @author a-lives
 * @className RemoteCMD
 * @version 1.0
 * @date 2023-7-22
 */

public class VideoPlayer extends JPanel {

    public String _serverIP;
    public int _serverPort;
    private BufferedImage image;

    /**
     * Initialization
     * 
     * @throws Exception
     */
    public VideoPlayer() throws Exception {
        setBackground(Color.BLACK);
        PrivateKey privateKey = RSA.getPrivateKeyFromBase64(
                "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");
        SecureSocket socket = new SecureSocket(privateKey, "127.0.0.1", 8081);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                int h = VideoPlayer.this.getHeight();
                int w = VideoPlayer.this.getWidth();
                String button = switch (e.getButton()) {
                    case MouseEvent.BUTTON1 -> "L";
                    case MouseEvent.BUTTON2 -> "M";
                    case MouseEvent.BUTTON3 -> "R";
                    default -> "L";
                };
                String signal = "" + x + "," + y + "," + w + "," + h + "," + button + "," + "P";
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            socket.sendall(signal.getBytes());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }).start();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                int h = VideoPlayer.this.getHeight();
                int w = VideoPlayer.this.getWidth();
                String button = switch (e.getButton()) {
                    case MouseEvent.BUTTON1 -> "L";
                    case MouseEvent.BUTTON2 -> "M";
                    case MouseEvent.BUTTON3 -> "R";
                    default -> "L";
                };
                String signal = "" + x + "," + y + "," + w + "," + h + "," + button + "," + "R";
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            socket.sendall(signal.getBytes());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }).start();
            }
        });
        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e){
                int b = e.getButton();
                String button = switch (b) {
                    case MouseEvent.BUTTON1 -> "L";
                    case MouseEvent.BUTTON2 -> "M";
                    case MouseEvent.BUTTON3 -> "R";
                    default -> "L";
                };
                int x = e.getX();
                int y = e.getY();
                int h = VideoPlayer.this.getHeight();
                int w = VideoPlayer.this.getWidth();
                String signal = "" + x + "," + y + "," + w + "," + h + "," + button + "," + "M";
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            socket.sendall(signal.getBytes());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }).start();
            }
            @Override
            public void mouseMoved(MouseEvent e) {
                
            }
        });
    }

    /**
     * Your own watch
     * 
     * @param imageData
     * @throws IOException
     */
    public void setImageFromBytes(byte[] imageData) throws IOException {
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
}
