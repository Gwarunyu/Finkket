package com.nuttwarunyu.finkket;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.lang.reflect.Type;

/**
 * Created by Dell-NB on 27/10/2558.
 */
public class DrawingView extends View {

    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private int paintColor = 0xFFFFFFFF;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private float brushSize, lastBrushSize;
    private boolean erase = false;
    private int paintAlpha = 255;



    public int getPaintAlpha() {
        return Math.round((float) paintAlpha / 255 * 100);
    }

    public void setPaintAlpha(int newPaintAlpha) {
        paintAlpha = Math.round((float) paintAlpha / 255 * 100);
        drawPaint.setColor(0xFFFFFFFF);
        drawPaint.setAlpha(paintAlpha);
    }


    public DrawingView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setupDrawing();
        Log.d("DrawingView", "Process");
        setBackgroundResource(R.drawable.note);
/*        MainActivity mainActivity = new MainActivity();
        Bitmap bitmapBG = mainActivity.setBG();
        Drawable drawable = new BitmapDrawable(getResources(),bitmapBG);
        setBackground(drawable);*/
    }

    private void setupDrawing() {

        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;

        drawPath = new Path();
        drawPaint = new Paint();

        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);

    }

    public void setBrushSize(float newSize) {
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, getResources().getDisplayMetrics());
        brushSize = pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        //Bitmap currentBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.note);
        //canvasBitmap = currentBitmap.copy(Bitmap.Config.ARGB_8888,true);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void setLastBrushSize(float lastSize) {
        lastBrushSize = lastSize;
    }

    public float getLastBrushSize() {
        return lastBrushSize;
    }

   /* public void setColor(String newColor) {
        invalidate();

        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }*/

    public void setErase(boolean isErase) {
        erase = isErase;

        if (erase) drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else drawPaint.setXfermode(null);
    }

    public void startNew() {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public void setPattern(String newPattern) {
        invalidate();

        int patterID = getResources().getIdentifier(newPattern, "drawable", "com.nuttwarunyu.finkket");
        Bitmap patternBitmap = BitmapFactory.decodeResource(getResources(), patterID);
        BitmapShader patternBitmapShader = new BitmapShader(patternBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

        drawPaint.setColor(0xFFFFFFFF);
        drawPaint.setShader(patternBitmapShader);

    }

}
