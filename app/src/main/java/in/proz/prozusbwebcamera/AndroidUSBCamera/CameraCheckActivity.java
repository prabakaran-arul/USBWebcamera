package in.proz.prozusbwebcamera.AndroidUSBCamera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import in.proz.prozusbwebcamera.R;

public class CameraCheckActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private Camera camera;
    private boolean isRecording = false;
    private MediaRecorder mediaRecorder;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSurfaceView = findViewById(R.id.textureView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        if(checkCameraHardware(CameraCheckActivity.this)){
            Log.d("Keerthiga"," camera present");
            if(prepareVideoRecorder()){
                Log.d("Keerthiga","prepate video recording");
                startRecording();
             }else{
                Log.d("Keerthiga"," not prep video reco");
            }
        }else{
            Log.d("Keerthiga"," no camera presn");
        }

        //mSurfaceHolder.addCallback(this);
    }
    private boolean prepareVideoRecorder() {
        camera = Camera.open(); // Open the default rear-facing camera
        // Configure camera parameters
        Camera.Parameters params = camera.getParameters();
        camera.setParameters(params);

        // Set the orientation of the camera preview
        camera.setDisplayOrientation(90); // Adjust as needed based on camera orientation

        // Start the preview
        camera.startPreview();
        camera.setErrorCallback(new Camera.ErrorCallback() {
            @Override
            public void onError(int error, Camera camera) {
                Log.d("Keerthiga","error "+error+" came "+camera.toString());
            }
        });



        // Return true if successful
        return true;
    }

    private boolean startRecording() {
        if (prepareVideoRecorder()) {
            Log.d("Keerthiga"," pre vide ");
            mediaRecorder = new MediaRecorder();
            camera.unlock();
            mediaRecorder.setCamera(camera);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);

            // Step 3: Set output file path
            File output = getOutputMediaFile();
            mediaRecorder.setOutputFile(output.toString());
            Log.d("Keerthiga"," try "+mSurfaceHolder.getSurface()
            +"   "+mSurfaceHolder.getSurfaceFrame());
            try {
                mediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
                mediaRecorder.prepare();
                mediaRecorder.start();
                isRecording = true;
                return true;
            } catch (IOException e) {
                Log.d("Keerthiga"," error "+e.getMessage());
                releaseMediaRecorder();
                return false;
            }
        } else {
            Log.d("Keerthiga"," release res");
            releaseMediaRecorder();
            return false;
        }
    }
    private File getOutputMediaFile() {
        // To be implemented based on your requirements
        // Example: Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "prozusbwebcamera");

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("Keerthiga", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String fileName = "VID_" + timeStamp + ".mp4";
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                fileName);
        Log.d("Keerthiga"," media file "+mediaFile);
        return mediaFile;
    }
    private void stopRecording() {
        if (isRecording) {
            // Stop recording and release resources
            try {
                Log.d("Keerthiga"," stop recording");
                mediaRecorder.stop();
            } catch (RuntimeException e) {
                Log.d("Keerthiga"," errro1 "+e.getMessage());
                // Handle this as needed
            } finally {
                releaseMediaRecorder();
                isRecording = false;
            }
        }
    }
    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            Log.d("Keerthiga"," release media recorder ");
            mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            camera.lock(); // lock camera for later use
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Keerthiga"," destroy camera");
        releaseCamera();
    }

    private void releaseCamera() {
        if (camera != null) {
            Log.d("Keerthiga"," release camera");
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (isRecording) {
            Log.d("Keerthiga"," on pause ");
            stopRecording();
        }
        releaseCamera();
    }
    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        Log.d("Keerthiga"," on created");
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        try {
            Log.d("Keerthiga"," try block ");
            camera.setPreviewDisplay(holder);
            camera.startPreview();
            Camera.Parameters params = camera.getParameters();
            camera.setParameters(params);

            // Set the orientation of the camera preview
            camera.setDisplayOrientation(90); // Adjust as needed based on camera orientation

            // Start the preview
            camera.startPreview();
            prepareVideoRecorder();
            startRecording();
        } catch (IOException e) {
            Log.d("Keerthiga"," ere "+e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        if (mSurfaceHolder.getSurface() == null) {
            // Preview surface does not exist
            return;
        }

        try {
            Log.d("Keerthiga"," surfacec  che ");
            camera.stopPreview();
        } catch (Exception e) {
            Log.d("Keerthiga"," erml "+e.getMessage());
            // Ignore: tried to stop a non-existent preview
        }
        try {
            Log.d("Keerthiga","dlsdv ");
            camera.setPreviewDisplay(mSurfaceHolder);
            camera.startPreview();
            startRecording();
        } catch (Exception e) {
            Log.d("Keerthiga"," errrt "+e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        Log.d("Keerthiga"," on deas ");
        releaseCamera();
        releaseMediaRecorder();
        camera = null;
    }
}
