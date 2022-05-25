package com.amnitamn.camstream.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.amnitamn.camstream.R;
import com.amnitamn.camstream.service.CameraClass;
import com.amnitamn.camstream.service.ConnectionThread;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    EditText editPort, editTextAddress;
    Button buttonConnect, changeAspectRatioBtn, changeCameraBtn;
    boolean is3_4 = true, is0=true;


    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private CameraClass camera;
    public TextureView textureView;
    ConnectionThread imageThread = null;
    float as = 3/4f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (NullPointerException ignored){}
        setContentView(R.layout.activity_main);

        editPort = findViewById(R.id.portInput);
        editTextAddress = findViewById(R.id.ipAddressInput);
        buttonConnect = findViewById(R.id.connectButton);
        changeAspectRatioBtn = findViewById(R.id.changeAspectRatioBtn);
        changeCameraBtn = findViewById(R.id.changeCameraBtn);


        buttonConnect.setOnClickListener(buttonConnectOnClickListener);
        changeAspectRatioBtn.setOnClickListener(changeAspectRatioOnClickListener);
        changeCameraBtn.setOnClickListener(changeCameraOnclickListener);


        textureView = findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(mSurfaceTextureListener);

        camera = new CameraClass(MainActivity.this, textureView, "0", as);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        camera.startBackgroundThread();
        if (textureView.isAvailable()){
            camera.openCamera(textureView.getWidth(), textureView.getHeight());
        } else {
            textureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }

    }


    View.OnClickListener buttonConnectOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            String port = editPort.getText().toString();
            if (port.equals("")) {
                Toast.makeText(MainActivity.this, "Enter Port Number",
                        Toast.LENGTH_LONG).show();
                return;
            }

            String textAddress = editTextAddress.getText().toString();
            if (textAddress.equals("")) {
                Toast.makeText(MainActivity.this, "Enter Address",
                        Toast.LENGTH_LONG).show();
                return;
            }

            imageThread = new ConnectionThread(textAddress, Integer.parseInt(port), MainActivity.this);
            imageThread.start();

        }
    };

    View.OnClickListener changeAspectRatioOnClickListener = view -> {
        if(is3_4){
            changeAspectRatio(9/16f);
            is3_4 = false;
        }
        else{
            changeAspectRatio(3/4f);
            is3_4 = true;
        }
    };

    View.OnClickListener changeCameraOnclickListener = view -> {
        if(is0){
            changeCamera("4");
            is0 = false;
        }
        else{
            changeCamera("0");
            is0 = true;
        }
    };


    @Override
    public void onStart() {
        super.onStart();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        camera.closeCamera();
        camera.stopBackgroundThread();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camera.closeCamera();
        camera.stopBackgroundThread();
    }

    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            textureView.setLayoutParams(new RelativeLayout.LayoutParams(textureView.getWidth(), (int) (textureView.getWidth()/as)));
            camera.openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            //camera.configureTransform(textureView.getWidth(), textureView.getHeight());
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    public void changeAspectRatio(float aspectRatio) {
        camera.closeCamera();
        camera.stopBackgroundThread();

        camera.setAspectRatio(aspectRatio);

        camera.startBackgroundThread();
        if (textureView.isAvailable()){
            camera.openCamera(textureView.getWidth(), textureView.getHeight());
        } else {
            textureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    public void changeCamera(String cameraId) {
        camera.closeCamera();
        camera.stopBackgroundThread();

        camera.setCameraId(cameraId);

        camera.startBackgroundThread();
        if (textureView.isAvailable()){
            camera.openCamera(textureView.getWidth(), textureView.getHeight());
        } else {
            textureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "ERROR: Camera permissions not granted", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}