package com.example.harshsaini.meme;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.harshsaini.meme.UI.CameraSourcePreview;
import com.example.harshsaini.meme.UI.GraphicsOverlay;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private CameraSourcePreview mcameraSourcePreview;
    private CameraSource mcameraSource;
    private String MAINLOG="MAIN SOURCE LOG";
    private ImageButton flipCamera;
    private  FaceDetector detector;
    private ImageButton captureImage;
    private ImageButton changePlate;

    private GraphicsOverlay mGraphicsOverlay;

    private static int cameraFacing=CameraSource.CAMERA_FACING_FRONT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mcameraSourcePreview=(CameraSourcePreview)findViewById(R.id.previewCamera);
        int cameraPer= ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        flipCamera=(ImageButton)findViewById(R.id.flipCamera);
        captureImage=(ImageButton)findViewById(R.id.cameraStore);
        mGraphicsOverlay=(GraphicsOverlay)findViewById(R.id.graphicOverlay);
        changePlate=(ImageButton)findViewById(R.id.changeFilters);

        flipCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int k=mcameraSource.getCameraFacing();

                changeCameraFacing(k);
            }
        });

        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(MAINLOG,"CLicK HOOH GAYA");
                onPictureTaken();
            }
        });

        changePlate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Filters.class);
                startActivity(intent);


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
                .setMode(FaceDetector.FAST_MODE)
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
                Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE
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
        Log.w(MAINLOG,"onResume");

        onStartCamera();
    }



    @Override
    protected void onRestart() {
        super.onRestart();
        Log.w(MAINLOG,"onRestart");
        onStartCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(MAINLOG,"onPause");
    }

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

        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, 421);
            dlg.show();
        }

        if(mcameraSource!=null)
        {
            mcameraSourcePreview.startCameraServices(mcameraSource,mGraphicsOverlay);

        }
        else {

            Log.e(MAINLOG, "Unable to start camera source.");
            mcameraSource.release();
            mcameraSource = null;
        }

    }




    public class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face>
    {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicTracker(mGraphicsOverlay);
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
            createCameraSource(getApplicationContext());
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



    public void onPictureTaken()
    {
        mcameraSource.takePicture(null, new CameraSource.PictureCallback() {
            private File imageFile;
            @Override
            public void onPictureTaken(byte[] bytes) {


                Bitmap loadedimage=mGraphicsOverlay.getDrawingCache(true);

                File enviorement=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"MYCAMERAAPP");
                if(!enviorement.exists())
                {
                    if(!enviorement.mkdirs())
                    {
                        Log.w(MAINLOG,"NOT ABLE TO CREATE DIRECTORY ");
                        return;
                    }
                }
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                        .format(new Date());
                imageFile=new File(enviorement.getPath()+File.separator+"IMG"+timeStamp+".jpg");
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(imageFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    fos.write(bytes);
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }



            }

        });
    }




    public class GraphicTracker extends Tracker<Face>
    {
        GraphicsOverlay graphicsOverlay;

        private FaceGraphics mFaceGraphic;

        GraphicTracker(GraphicsOverlay overlay) {
            graphicsOverlay = overlay;
            mFaceGraphic = new FaceGraphics(overlay);
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mGraphicsOverlay.add(mFaceGraphic);
            mFaceGraphic.update(face);
        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mGraphicsOverlay.remove(mFaceGraphic);
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            mGraphicsOverlay.remove(mFaceGraphic);
        }
    }
}



