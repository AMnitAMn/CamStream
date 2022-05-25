package com.amnitamn.camstream.service;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.TextureView;
import androidx.annotation.RequiresApi;
import com.amnitamn.camstream.activity.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ConnectionThread extends Thread{
    TextureView tv;
    String dstAddress;
    int dstPort;
    Socket socket;
    Activity activity;
    boolean goOut = false;



    public ConnectionThread(String address, int port, Activity activity) {
        this.activity = activity;
        this.dstAddress = address;
        this.dstPort = port;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void run() {
        try {
            socket = new Socket(dstAddress, dstPort);
            socket.setKeepAlive(true);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            tv = ((MainActivity)activity).textureView;
            while (true) {
                Bitmap bmp = tv.getBitmap();
                bmp =  Bitmap.createScaledBitmap(bmp, bmp.getWidth()/2, bmp.getHeight()/2, true);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                dos.writeUTF(String.valueOf(String.valueOf(stream.size()).length()));
                dos.writeUTF(String.valueOf(stream.size()));
                dos.write(stream.toByteArray());
                dos.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

