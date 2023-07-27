import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * LocalCMD, use to bulid a cmd process.
 * 
 * @author a-lives
 * @className LocalCMD
 * @version 1.0
 * @date 2023-7-25
 */
public class LocalCMD {

    public Process _process;
    public OutputStream _outputStream;
    public BufferedReader _inputStreamReader;
    public BufferedReader _errorStreamReader;

    /**
     * 
     * @throws IOException
     */
    public LocalCMD() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe");
        _process = processBuilder.start();
        _outputStream = _process.getOutputStream();
        _inputStreamReader = new BufferedReader(new InputStreamReader(_process.getInputStream()));
        _errorStreamReader = new BufferedReader(new InputStreamReader(_process.getErrorStream()));
    }

    /**
     * input command
     * 
     * @param command
     * @throws IOException
     */
    public void Input(String command) throws IOException {
        _outputStream.write((command + "\n").getBytes());
        _outputStream.flush();
    }

    /**
     * get output
     * 
     * @throws IOException
     */
    public void Output() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String line;
                try {
                    while ((line = _inputStreamReader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * get Error
     * 
     * @throws IOException
     */
    public void getError() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String line;
                try {
                    while ((line = _errorStreamReader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * exit
     * 
     * @return
     * @throws InterruptedException
     */
    public int exit() throws InterruptedException {
        return _process.waitFor();
    }
}
