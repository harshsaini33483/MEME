package com.example.harshsaini.meme.UI;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

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
        }
        else
        {
            mcameraSource=cameraSource;
            requestForCamera=true;
            startIfReady();
        }
    }


    public void startIfReady()
    {
        if(surfaceAvilable && requestForCamera)
        {
            try {
                mcameraSource.start(msurfaceView.getHolder());

            } catch (IOException e) {
                e.printStackTrace();
            }
            requestForCamera=false;

        }
    }



    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int previewWidth=480,previewHeight=640;
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
                        -1 * childXOffset, -1 * childYOffset,
                        childWidth - childXOffset, childHeight - childYOffset);
            }

            startIfReady();
        }

    }
}
