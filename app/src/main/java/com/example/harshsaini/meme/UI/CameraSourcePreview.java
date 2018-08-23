package com.example.harshsaini.meme.UI;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.harshsaini.meme.MainActivity;
import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;

import java.io.IOException;

public class CameraSourcePreview extends ViewGroup {

    private String LOG="CAMERA SOURCE PREVIEW";
    private CameraSource mcameraSource;
    private SurfaceView msurfaceView;
    private Context mcontext;
    private boolean requestForCamera =false;
    private boolean surfaceAvilable=false;
    private GraphicsOverlay graphicsOverlay;


    public CameraSourcePreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        mcontext=context;
        msurfaceView=new SurfaceView(context);
        msurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                surfaceAvilable=true;
                try{
                        startIfReady();
                }
                catch (Exception e)
                {
                    Log.e(LOG ,"Camera Could not Find CameraSource in CameraSourcePreview in Constructor");
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                surfaceAvilable=false;
            }
        });

        addView(msurfaceView);


    }


    public void startCameraServices(CameraSource cameraSource)
    {
        if(cameraSource==null)
        {
            Log.e(LOG,"Camera Source is null in startCameraSource ");
            stop();

        }
        else
        {
            mcameraSource=cameraSource;
            requestForCamera=true;
            Log.w(LOG,requestForCamera+"  "+mcameraSource+"  "+" "+surfaceAvilable);
            startIfReady();

        }
    }

    public void startCameraServices(CameraSource cameraSource,GraphicsOverlay graphicsOverlay)
    {
        this.graphicsOverlay=graphicsOverlay;
        startCameraServices(cameraSource);
    }


    public void startIfReady()
    {
        if(surfaceAvilable && requestForCamera)
        {
            try {
                mcameraSource.start(msurfaceView.getHolder());
                Log.w(LOG,"Camera Source Preview  is Available");

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (graphicsOverlay != null) {
                Size size = mcameraSource.getPreviewSize();
                int min = Math.min(size.getWidth(), size.getHeight());
                int max = Math.max(size.getWidth(), size.getHeight());
                if (mcontext.getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT) {
                    // Swap width and height sizes when in portrait, since it will be rotated by
                    // 90 degrees
                    graphicsOverlay.setCameraInfo(min, max, mcameraSource.getCameraFacing());
                } else {
                    graphicsOverlay.setCameraInfo(max, min, mcameraSource.getCameraFacing());
                }
                graphicsOverlay.clear();
            }



            requestForCamera=false;

        }
    }


    public void stop() {
        if (mcameraSource != null) {
            mcameraSource.stop();
        }
    }

    public void release() {
        if (mcameraSource != null) {
            mcameraSource.release();
            mcameraSource = null;
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int previewWidth=320,previewHeight=240;
        if(mcameraSource!=null)
        {
            Size size=mcameraSource.getPreviewSize();
            if(size!=null)
            {
                previewHeight=size.getHeight();
                previewWidth=size.getWidth();

            }
        }

        if(mcontext.getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT)
        {
            int tmp = previewWidth;
            previewWidth = previewHeight;
            previewHeight = tmp;

            final int viewWidth = r - l;
            final int viewHeight = b- t;

            int childWidth;
            int childHeight;
            int childXOffset = 0;
            int childYOffset = 0;
            float widthRatio = (float) viewWidth / (float) previewWidth;
            float heightRatio = (float) viewHeight / (float) previewHeight;
            if (widthRatio > heightRatio) {
                childWidth = viewWidth;
                childHeight = (int) ((float) previewHeight * widthRatio);
                childYOffset = (childHeight - viewHeight) / 2;
            } else {
                childWidth = (int) ((float) previewWidth * heightRatio);
                childHeight = viewHeight;
                childXOffset = (childWidth - viewWidth) / 2;
            }

            for (int i = 0; i < getChildCount(); ++i) {
                // One dimension will be cropped.  We shift child over or up by this offset and adjust
                // the size to maintain the proper aspect ratio.
                getChildAt(i).layout(
                        0 * childXOffset, 0 * childYOffset,
                        childWidth - childXOffset, childHeight - childYOffset);
            }



            startIfReady();
        }

    }
}
