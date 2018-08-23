package com.example.harshsaini.meme.UI;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.vision.CameraSource;

import java.util.HashSet;
import java.util.Set;

public class GraphicsOverlay extends View {

    private final Object mLock = new Object();
    int mFacing= CameraSource.CAMERA_FACING_FRONT;
    private int mPreviewWidth;
    private float mWidthScaleFactor = 1.0f;
    private int mPreviewHeight;
    private float mHeightScaleFactor = 1.0f;
    Set<Graphics>mOverlay=new HashSet<Graphics>();

    public GraphicsOverlay(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }


    public static abstract class Graphics {
        private GraphicsOverlay mGraphicsOverlay;
        public Graphics(GraphicsOverlay graphicsOverlay)
        {
            mGraphicsOverlay=graphicsOverlay;
        }
        public abstract void draw(Canvas canvas);

        public float scaleX(float horizontal) {
            return (horizontal * mGraphicsOverlay.mWidthScaleFactor);
        }
        public float scaleY(float vertical) {
            return vertical * mGraphicsOverlay.mHeightScaleFactor;
        }

        public float translateY(float y) {
            return scaleY(y);
        }
        public float translateX(float x)
        {
            if (mGraphicsOverlay.mFacing == CameraSource.CAMERA_FACING_FRONT) {
                return mGraphicsOverlay.getWidth() - scaleX(x);
            } else {
                return scaleX(x);
            }
        }
        public void postInvalidate()
        {
            mGraphicsOverlay.postInvalidate();
        }
        public GraphicsOverlay getOverlay()
        {
            return mGraphicsOverlay;
        }


    }



    public void clear()
    {
        synchronized (mLock)
        {
            mOverlay.clear();
        }
        postInvalidate();
    }

    public void add(Graphics graphic)
    {
        synchronized (mLock) {
            mOverlay.add(graphic);
        }
        postInvalidate();
    }
    public void remove(Graphics graphic)
    {

        synchronized (mLock) {
            mOverlay.remove(graphic);
        }
        postInvalidate();
    }
    public void setCameraInfo(int previewWidth, int previewHeight, int facing)
    {
        synchronized (mLock) {
            mPreviewWidth = previewWidth;
            mPreviewHeight = previewHeight;
            mFacing = facing;
        }
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        synchronized (mLock) {
            if ((mPreviewWidth != 0) && (mPreviewHeight != 0)) {
                mWidthScaleFactor = (float) canvas.getWidth() / (float) mPreviewWidth;
                mHeightScaleFactor = (float) canvas.getHeight() / (float) mPreviewHeight;
            }

            for (Graphics graphic : mOverlay) {
                graphic.draw(canvas);
            }
        }
    }
}

