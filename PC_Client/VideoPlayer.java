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
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A control that is responsible for playing the server screen and do mouse
 * events.
 * 
 * @author a-lives
 * @className VideoPlayer
 * @version 1.1
 * @date 2023-8-2
 */

public class VideoPlayer extends JPanel {

    private BufferedImage image;
    private SecureSocket _socketMouse;
    private SecureSocket _socketVideo;
    private ConcurrentLinkedQueue<String> _mouseQueue;

    /**
     * Initialization
     * 
     * @throws Exception
     */
    public VideoPlayer(PrivateKey privateKey, String serverIP, int serverPortVideo, int serverPortMouse)
            throws Exception {
        setBackground(Color.BLACK);
        addVideoSocket(privateKey, serverIP, serverPortVideo);
        addMouseSocket(privateKey, serverIP, serverPortMouse);
    }

    public void addMouseSocket(PrivateKey privateKey, String serverIP, int port) {
        try {
            _socketMouse = new SecureSocket(privateKey, serverIP, port, "GCM");
            Log.log("connected: mouse");
            _mouseQueue = new ConcurrentLinkedQueue<>();
            addMouseTracker();
            addMouseEventSender();
        } catch (Exception e) {
            Log.log("connection failed: mouse");
        }
    }

    /**
     * Add mousetracker to track mouseEvents and send signal to client.
     * 
     */
    public void addMouseTracker() {
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
                _mouseQueue.offer(signal);
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
                _mouseQueue.offer(signal);
            }
        });

        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
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
                _mouseQueue.offer(signal);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });
    }

    public void addMouseEventSender() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10);
                        if (!_mouseQueue.isEmpty()) {
                            String signal = _mouseQueue.poll();
                            _socketMouse.sendall(signal.getBytes());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    public void addVideoSocket(PrivateKey privateKey, String serverIP, int port) {
        try {
            _socketVideo = new SecureSocket(privateKey, serverIP, port, "OFB");
            Log.log("connected: video");
            play();
        } catch (Exception e) {
            Log.log("connection failed: video");
        }
    }

    public void play() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5);
                        if (_socketVideo.isClosed())
                            break;
                        byte[] data = _socketVideo.recvall();
                        setImageFromBytes(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }

        }).start();
        // socket.close();
    }

    public void reload(PrivateKey privateKey, String serverIP, int serverPortVideo, int serverPortMouse) {
        addVideoSocket(privateKey, serverIP, serverPortVideo);
        try {
            _socketMouse = new SecureSocket(privateKey, serverIP, serverPortMouse, "GCM");
            Log.log("connected: mouse");
            _mouseQueue = new ConcurrentLinkedQueue<>();
        } catch (Exception e) {
            Log.log("connection failed: mouse");
        }
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
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
