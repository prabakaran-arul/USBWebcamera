package in.proz.prozusbwebcamera.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraControl;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import in.proz.prozusbwebcamera.R;


import in.proz.prozusbwebcamera.webcam.WebcamPreview;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CAMERA_PERMISSION = 200;

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCaptureSession;
    private CaptureRequest.Builder mCaptureRequestBuilder;
    private MediaRecorder mMediaRecorder;
    private String mCameraId;
    private Size mVideoSize;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        if(!checkCameraPermission()){
            requestCameraPermission();
        }
        mSurfaceView = findViewById(R.id.textureView);
        mSurfaceHolder = mSurfaceView.getHolder();

        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d("Keerthiga"," surface created");
                setupCamera();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.d("Keerthiga"," surface changed");

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d("Keerthiga"," surface destroyed");

                closeCamera();
            }
        });
    }
    private void requestCameraPermission() {
        Log.d("Keerthiga"," request permission");

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 100:
                Log.d("Keerthiga","permission granted ");

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with camera operation
                    // e.g., startCamera();
                } else {
                    // Permission denied, handle accordingly
                    // e.g., display an explanation or disable camera features
                }
                break;
        }
    }
    private boolean checkCameraPermission() {
        boolean result =  ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean result1 =  ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        return (result&&result1);
    }


    private void setupCamera() {
        Log.d("Keerthiga"," setup camera called ");

        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            Log.d("Keerthiga"," setup try block ");

            String[] cameraIds = cameraManager.getCameraIdList();
            for (String cameraId : cameraIds) {
                mCameraId = cameraId;
                break;
            }
            Log.d("Keerthiga"," camera id "+mCameraId);

            if (mCameraId != null) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(mCameraId);
                StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                            REQUEST_CAMERA_PERMISSION);
                    return;
                }
                cameraManager.openCamera(mCameraId, new CameraDevice.StateCallback() {
                    @Override
                    public void onOpened(@NonNull CameraDevice camera) {
                        mCameraDevice = camera;
                        startPreview();
                        Log.d("Keerthiga"," open camera ");

                    }

                    @Override
                    public void onDisconnected(@NonNull CameraDevice camera) {
                        camera.close();
                        mCameraDevice = null;
                        Log.d("Keerthiga"," disconnect camera ");

                    }

                    @Override
                    public void onError(@NonNull CameraDevice camera, int error) {
                        camera.close();
                        Log.d("Keerthiga"," camera error ");

                        mCameraDevice = null;
                    }
                }, null);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private Size chooseVideoSize(Size[] choices) {
        // Implement your logic to choose video size here (e.g., select the largest size).
        return choices[0]; // Default implementation, choose the first available size.
    }

    private void startPreview() {
        try {
            Log.d("Keerthiga"," tart previce");

            Surface surface = mSurfaceHolder.getSurface();
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCaptureRequestBuilder.addTarget(surface);
            mCameraDevice.createCaptureSession(Collections.singletonList(surface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            mCaptureSession = session;
                            Log.d("Keerthiga"," capture session ");
                            updatePreview();
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            Toast.makeText(CameraActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }, null);
        } catch (CameraAccessException e) {
            Log.d("Keerthiga","  preview faile "+e.getMessage());

            e.printStackTrace();
        }
    }

    private void updatePreview() {
        mCaptureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            Log.d("Keerthiga"," update preive ");

            mCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(), null, null);
        } catch (CameraAccessException e) {
            Log.d("Keerthiga"," update preview erro "+e.getMessage());

            e.printStackTrace();
        }
    }

    private void closeCamera() {
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeCamera();
    }
}
