package com.rod;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ControlPanel extends AppCompatActivity {
    private static final String TAG = "ControlPanel";
    private String host = "";
    private int port1 = 0;
    private int port2 = 0;
    private String useKeyName = "";
    private final ConcurrentLinkedQueue<String> Actions = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Integer> X = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Integer> Y = new ConcurrentLinkedQueue<>();
    private int moveCounter = 0;
    private final Bitmap[] screen = new Bitmap[1];

    private Bitmap scaleBitmap(Bitmap src, int targetWidth, int targetHeight){
        float scaleWidth = ((float) targetWidth) / src.getWidth();
        float scaleHeight = ((float) targetHeight) / src.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),src.getHeight(),matrix,true);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_control_panel);
        Intent intent = getIntent();
        host = intent.getStringExtra("host");
        port1 = intent.getIntExtra("port1",8080);
        port2 = intent.getIntExtra("port2",8081);
        useKeyName = intent.getStringExtra("useKeyName");
        if (savedInstanceState!=null){
            host = savedInstanceState.getString("host");
            port1 = savedInstanceState.getInt("port1");
            port2 = savedInstanceState.getInt("port2");
            useKeyName = savedInstanceState.getString("useKeyName");
        }



        final PrivateKey[] privateKey = new PrivateKey[1];
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openFileInput("RSAKeys")));
            JSONObject jsonObject = new JSONObject(bufferedReader.readLine());
            Log.i(TAG, "onCreate: "+jsonObject.getString(useKeyName));
            privateKey[0] = RSA.getPrivateKeyFromBase64(jsonObject.getString(useKeyName).split(",")[1]);
        } catch (JSONException | IOException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

        final SecureSocket[] socket = new SecureSocket[2];
        ImageView imageView = findViewById(R.id.imageView);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        Handler handler = new Handler();

        imageView.setOnTouchListener((view, motionEvent) -> {
            switch(motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    Actions.offer("P");
                    Log.i(TAG, "onCreate: P"+"-"+motionEvent.getX()+"-"+motionEvent.getY());
                    X.offer((int) motionEvent.getX());
                    Y.offer((int) motionEvent.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    Actions.offer("R");
                    X.offer((int) motionEvent.getX());
                    Y.offer((int) motionEvent.getY());
                    Log.i(TAG, "onCreate: R"+"-"+motionEvent.getX()+"-"+motionEvent.getY());
                    break;
                case MotionEvent.ACTION_MOVE:

                    Log.i(TAG, "onCreate: D"+"-"+motionEvent.getX()+"-"+motionEvent.getY());
                    if (moveCounter>10){
                        Actions.offer("D");
                        X.offer((int) motionEvent.getX());
                        Y.offer((int) motionEvent.getY());
                        moveCounter = 0;
                    }else{
                        moveCounter += 1;
                    }
                    break;
            }
            return false;
        });

        // video receiving part
        Thread threadVideo = new Thread(() -> {
            while(true){
                Log.i(TAG, "onCreate: start Video");
                byte[] img;
                try {
                    socket[0] = new SecureSocket(privateKey[0], host, port1, "OFB");
                    img = socket[0].recvall();
                    screen[0] = BitmapFactory.decodeByteArray(img, 0, img.length);
                    double screenAspectRatio = ((double) displayMetrics.widthPixels) / displayMetrics.heightPixels;
                    double imageAspectRatio = ((double) screen[0].getWidth()) / screen[0].getHeight();
                    handler.post(() -> {
                        if (screenAspectRatio<imageAspectRatio) {
                            layoutParams.height = displayMetrics.widthPixels * screen[0].getHeight() / screen[0].getWidth();
                            layoutParams.width = displayMetrics.widthPixels;
                        }else { // screenAspectRatio>=imageAspectRatio

                            layoutParams.width = displayMetrics.heightPixels * screen[0].getWidth() / screen[0].getHeight();
                            layoutParams.height = displayMetrics.heightPixels;
                        }
                        imageView.setImageBitmap(screen[0]);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                while (true) {
                    try {
                        img = socket[0].recvall();
                        screen[0] = scaleBitmap(BitmapFactory.decodeByteArray(img, 0, img.length),layoutParams.width,layoutParams.height);
                        handler.post(() -> imageView.setImageBitmap(screen[0]));
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });

//        // video refresh part
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(()->{
//                    imageView.setImageBitmap(screen[0]);
//                });
//            }
//        },20,50);

        //mouse part
        Thread threadMouse = new Thread(() -> {
            while (true) {
                try {
                    socket[1] = new SecureSocket(privateKey[0], host, port2, "GCM");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                while (true) {
                    if (!(Actions.isEmpty() || X.isEmpty() || Y.isEmpty())) {
                        try {
                            byte[] signal = (X.poll() + "," + Y.poll() + "," + layoutParams.width + "," + layoutParams.height + "," + "L" + "," + Actions.poll()).getBytes();
                            socket[1].sendall(signal);
                        } catch (Exception e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            }
        });
        threadVideo.start();
        threadMouse.start();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState: "+host+port1+port2);
        outState.putString("host", host);
        outState.putInt("port1", port1);
        outState.putInt("port2", port2);
        outState.putString("useKeyName", useKeyName);
    }

}