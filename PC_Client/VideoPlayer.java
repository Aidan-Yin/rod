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

    private BufferedImage image;
    private SecureSocket _socket;

    /**
     * Initialization
     * 
     * @throws Exception
     */
    public VideoPlayer(PrivateKey privateKey, String serverIP, int serverPort) throws Exception {
        setBackground(Color.BLACK);
        _socket = new SecureSocket(privateKey, serverIP, serverPort);
        addMouseTracker();
    }

    /**
     * Add mousetracker to track mouseEvents and send signal to client.
     * 
     */
    public void addMouseTracker(){
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
                            _socket.sendall(signal.getBytes());
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
                            _socket.sendall(signal.getBytes());
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
                            _socket.sendall(signal.getBytes());
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
