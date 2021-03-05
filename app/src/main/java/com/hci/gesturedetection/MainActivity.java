package com.hci.gesturedetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    DrawingView dv;
    private Paint mPaint, mFillPaint;
    private int mode = R.id.shape_line;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pallete_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shape_line:
                mode = R.id.shape_line;
                break;
            case R.id.shape_circle:
                mode = R.id.shape_circle;
                break;
            case R.id.shape_rect:
                mode = R.id.shape_rect;
                break;
            case R.id.shape_triangle:
                mode = R.id.shape_triangle;
                break;
            case R.id.color_blue:
                mode = R.id.color_blue;
                mFillPaint.setColor(Color.BLUE);
                break;
            case R.id.color_green:
                mode = R.id.color_green;
                mFillPaint.setColor(Color.GREEN);
                break;
            case R.id.color_red:
                mode = R.id.color_red;
                mFillPaint.setColor(Color.RED);
                break;
            case R.id.color_yellow:
                mode = R.id.color_yellow;
                mFillPaint.setColor(Color.YELLOW);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        dv = new DrawingView(this);
        dv.setBackgroundColor(0xFFFFFFFF);
        setContentView(dv);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(15);

        mFillPaint = new Paint();
        mFillPaint.setAntiAlias(true);
        mFillPaint.setDither(true);
        mFillPaint.setStyle(Paint.Style.STROKE);
        mFillPaint.setStrokeJoin(Paint.Join.ROUND);
        mFillPaint.setStrokeCap(Paint.Cap.SQUARE);
        mFillPaint.setStrokeWidth(10);
        mFillPaint.setColor(Color.RED); // by default filling color will be red
    }

    public class DrawingView extends View {

        public int width;
        public int height;
        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path mPath;
        private Paint mBitmapPaint;
        Context context;
        private Paint circlePaint;
        private Path circlePath;

        private float xmin=-1000, xmax=-1000, ymin=-1000, ymax=-1000;
        float xstart=0, ystart=0, xend=0, yend=0;

        public DrawingView(Context c) {
            super(c);
            context = c;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);

        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(mPath, mPaint);
            canvas.drawPath(circlePath, circlePaint);
            this.setDrawingCacheEnabled(true);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;

                circlePath.reset();
                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
            circlePath.reset();
            // commit the path to our offscreen
//            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }

        private void drawShape() {
            if (mode == R.id.shape_rect) {
                mCanvas.drawRect(xmin, ymin, xmax, ymax, mPaint);
            }
            else if (mode == R.id.shape_circle) {
                mCanvas.drawCircle((xmax+xmin)/2, (ymax+ymin)/2, ((xmax-xmin) + (ymax-ymin)) / 4, mPaint);
            }
            else if (mode == R.id.shape_triangle) {
                mCanvas.drawLine((xmax+xmin)/2, ymin, xmin, ymax, mPaint);
                mCanvas.drawLine(xmin, ymax, xmax, ymax, mPaint);
                mCanvas.drawLine(xmax, ymax, (xmax+xmin)/2, ymin, mPaint);
            }
            else if (mode == R.id.shape_line) {
                mCanvas.drawLine(xstart, ystart, xend, yend, mPaint);
            }
        }

        private void fillColor(float x, float y) {
            if (x < 0 || y < 0 || y >= mBitmap.getHeight() || x >= mBitmap.getWidth()) {
                return;
            }
            int currentPixel = mBitmap.getPixel((int) x, (int) y);
            System.out.println("" + Color.alpha(currentPixel) + " " + Color.red(mBitmap.getPixel((int) x, (int) y)) + " " + Color.green(mBitmap.getPixel((int) x, (int) y)) + " " + Color.blue(mBitmap.getPixel((int) x, (int) y)));
//            System.out.println("" + currentPixel);
            if (!(Color.alpha(currentPixel) == 255 && Color.red(currentPixel) == 0 && Color.green(currentPixel) == 0 && Color.blue(currentPixel) == 0)) {
//            if (Color.alpha()) {
                mCanvas.drawPoint(x, y, mFillPaint);
                fillColor(x, y-10);
                fillColor(x-10, y);
                fillColor(x, y+10);
                fillColor(x+10, y);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            if ((xmin == -1000) || (xmin > x)) {
                xmin = x;
            }
            if ((xmax == -1000) || (xmax < x)) {
                xmax = x;
            }
            if ((ymin == -1000) || (ymin > y)) {
                ymin = y;
            }
            if ((ymax == -1000) || (ymax < y)) {
                ymax = y;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    xstart = x;
                    ystart = y;
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    xend = x;
                    yend = y;
                    if (mode == R.id.shape_line || mode == R.id.shape_rect || mode == R.id.shape_circle || mode == R.id.shape_triangle) {
                        drawShape();
                        System.out.println("" + Color.alpha(mBitmap.getPixel((int) x, (int) y)) + " " + Color.red(mBitmap.getPixel((int) x, (int) y)) + " " + Color.green(mBitmap.getPixel((int) x, (int) y)) + " " + Color.blue(mBitmap.getPixel((int) x, (int) y)));
                    }
                    else {
                        fillColor(x, y);
                    }
                    xmin = -1000;
                    xmax = -1000;
                    ymin = -1000;
                    ymax = -1000;
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }
}
