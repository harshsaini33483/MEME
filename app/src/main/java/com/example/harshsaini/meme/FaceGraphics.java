package com.example.harshsaini.meme;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.example.harshsaini.meme.UI.GraphicsOverlay;
import com.example.harshsaini.meme.UI.GraphicsOverlay.Graphics;
import com.google.android.gms.vision.face.Face;

public class FaceGraphics extends Graphics {

    private volatile Face mFace;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final int COLOR_CHOICES[] = {
            Color.BLUE,
            Color.CYAN,
            Color.GREEN,
            Color.MAGENTA,
            Color.RED,
            Color.WHITE,
            Color.YELLOW
    };
    private static int mCurrentColorIndex = 0;
    private static Bitmap bitmap;
    private static Bitmap op;
    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;
    private int mFaceId;
    public static int ide=R.drawable.five;
    private static final float BOX_STROKE_WIDTH = .0f;


    public FaceGraphics(GraphicsOverlay graphicsOverlay) {
        super(graphicsOverlay);
        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];
        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);
        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
        changeFilters();

    }


    public void changeFilters()
    {
        bitmap=BitmapFactory.decodeResource(getOverlay().getContext().getResources(),ide);
        op = bitmap;
    }




    void setId(int id)
    {
        mFaceId=id;
    }

    void update(Face face)
    {
        mFace = face;
        op = Bitmap.createScaledBitmap(bitmap, (int) scaleX(face.getWidth()),
                (int) scaleY(((bitmap.getHeight() * face.getWidth()) / bitmap.getWidth())), false);
        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        if (face == null) {
            return;
        }

        // Draws a circle at the position of the detected face, with the face's track id below.
        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
       // canvas.drawRect(left, top, right, bottom, mBoxPaint);
        canvas.drawBitmap(op, left-50, top+100, new Paint());
    }


}
