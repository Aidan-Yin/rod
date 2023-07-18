import java.io.InputStream;
import java.net.Socket;
import javax.swing.JPanel;

public class VideoStreamReceiver {
    private static String _serverIP;
    private static int _serverPort;
    
    private static JPanel _panel;

    public VideoStreamReceiver(String serverIP, int serverPort, JPanel panel) {
        _serverIP = serverIP;
        _serverPort = serverPort;
        _panel = panel;
    }

    public void start() {
        try {
            Socket socket = new Socket(_serverIP, _serverPort);
            InputStream inputStream = socket.getInputStream();
            byte[] buffer = new byte[4096];   //这个可能需要其他数据调整
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                //  这里想办法把视频流转化为图显示
            }


            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

