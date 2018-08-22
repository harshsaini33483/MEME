package com.example.harshsaini.meme;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;

import com.example.harshsaini.meme.UI.CameraSourcePreview;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class MainActivity extends AppCompatActivity {

    private CameraSourcePreview mcameraSourcePreview;
    private CameraSource mcameraSource;
    private String MAINLOG="MAIN SOURCE LOG";
    private ImageButton flipCamera;
    private  FaceDetector detector;

    private static int cameraFacing=CameraSource.CAMERA_FACING_FRONT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mcameraSourcePreview=(CameraSourcePreview)findViewById(R.id.previewCamera);
        int cameraPer= ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        flipCamera=(ImageButton)findViewById(R.id.flipCamera);


        flipCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int k=mcameraSource.getCameraFacing();

                changeCameraFacing(k);
            }
        });



        if(PackageManager.PERMISSION_GRANTED==cameraPer)
        {
            createCameraSource(getApplicationContext());
        }
        else
        {
            requestCameraPermissions();
        }


    }

    public void changeCameraFacing(int facing)
    {
        if(cameraFacing==CameraSource.CAMERA_FACING_BACK)
        {
            cameraFacing=CameraSource.CAMERA_FACING_FRONT;
        }
        else
        {
            cameraFacing=CameraSource.CAMERA_FACING_BACK;
        }

        mcameraSource.release();
        mcameraSource=null;
        createCameraSource(getApplicationContext());
        onStartCamera();
    }


    public void createCameraSource(final Context context)
    {

        detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setLandmarkType(FaceDetector.NO_LANDMARKS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

            if(!detector.isOperational())
            {
                Log.w(MAINLOG,"Detector is in OPerational if");
            }

            cameraSourceObjectFacing(context,detector);


    }



    public void cameraSourceObjectFacing(Context context,FaceDetector face)
    {
        mcameraSource=new CameraSource.Builder(context,face).setRequestedFps(60.0f)
                .setRequestedPreviewSize(2048,1024)
                .setFacing(cameraFacing)
                .setAutoFocusEnabled(true).build();

    }






    public void requestCameraPermissions()
    {
        Log.w(MAINLOG,"Requesting Camera Permission Please Grant IT");
        final String permission[]=new String[]{
                Manifest.permission.CAMERA
        };

        if(!ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA))
        {
            ActivityCompat.requestPermissions(this,permission,421);
            return;

        }
        final Activity thisActivity = this;

        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(thisActivity,permission,421);
            }
        };


    }

    @Override
    protected void onResume() {
        super.onResume();

        onStartCamera();
    }

    /**
     * Stops the camera.
     */


    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mcameraSource != null) {
            mcameraSource.release();
        }
    }


    public void onStartCamera()
    {

        if(mcameraSource!=null)
        {
            mcameraSourcePreview.startCameraServices(mcameraSource);
        }
        else {
            Log.w(MAINLOG,"Camera Source is null in onStartCamera ");
        }

    }




    public class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face>
    {


        @Override
        public Tracker<Face> create(Face face) {
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != 421) {
            Log.d(MAINLOG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(MAINLOG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
           // createCameraSource();
            return;
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Tracker sample")
                .setMessage("There is NO CAMERA SO PERMISSION IS DENIED")
                .setPositiveButton("OK", listener)
                .show();


    }
}
