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
    private Paint mPaint, mFillPaint, mHollowPaint, mPaintToUse, mPaintFillBorder;
    private int shape_mode = R.id.shape_line;
    private boolean isFilling = false;

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
                shape_mode = R.id.shape_line;
                break;
            case R.id.shape_circle:
                shape_mode = R.id.shape_circle;
                break;
            case R.id.shape_rect:
                shape_mode = R.id.shape_rect;
                break;
            case R.id.shape_triangle:
                shape_mode = R.id.shape_triangle;
                break;

            case R.id.only_border:
                isFilling = false;
                mPaintToUse = mHollowPaint;
                break;
            case R.id.color_blue:
                isFilling = true;
                mFillPaint.setColor(Color.BLUE);
                mPaintToUse = mFillPaint;
                break;
            case R.id.color_green:
                isFilling = true;
                mFillPaint.setColor(Color.GREEN);
                mPaintToUse = mFillPaint;
                break;
            case R.id.color_red:
                isFilling = true;
                mFillPaint.setColor(Color.RED);
                mPaintToUse = mFillPaint;
                break;
            case R.id.color_yellow:
                isFilling = true;
                mFillPaint.setColor(Color.YELLOW);
                mPaintToUse = mFillPaint;
                break;
            case R.id.color_cyan:
                isFilling = true;
                mFillPaint.setColor(Color.CYAN);
                mPaintToUse = mFillPaint;
                break;
            case R.id.color_dark_gray:
                isFilling = true;
                mFillPaint.setColor(Color.DKGRAY);
                mPaintToUse = mFillPaint;
                break;
            case R.id.color_gray:
                isFilling = true;
                mFillPaint.setColor(Color.GRAY);
                mPaintToUse = mFillPaint;
                break;
            case R.id.color_light_gray:
                isFilling = true;
                mFillPaint.setColor(Color.LTGRAY);
                mPaintToUse = mFillPaint;
                break;
            case R.id.color_magenta:
                isFilling = true;
                mFillPaint.setColor(Color.MAGENTA);
                mPaintToUse = mFillPaint;
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

        mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFillPaint.setStrokeWidth(5);
        mFillPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mFillPaint.setAntiAlias(true);

        mPaintFillBorder = new Paint();
        mPaintFillBorder.setAntiAlias(true);
        mPaintFillBorder.setDither(true);
        mPaintFillBorder.setStyle(Paint.Style.STROKE);
        mPaintFillBorder.setStrokeJoin(Paint.Join.ROUND);
        mPaintFillBorder.setStrokeCap(Paint.Cap.SQUARE);
        mPaintFillBorder.setStrokeWidth(5);
        mPaintFillBorder.setColor(Color.BLACK);

        mHollowPaint = new Paint();
        mHollowPaint.setAntiAlias(true);
        mHollowPaint.setDither(true);
        mHollowPaint.setStyle(Paint.Style.STROKE);
        mHollowPaint.setStrokeJoin(Paint.Join.ROUND);
        mHollowPaint.setStrokeCap(Paint.Cap.SQUARE);
        mHollowPaint.setStrokeWidth(15);
        mHollowPaint.setColor(Color.BLACK);

        mPaintToUse = mHollowPaint;    // by default the hollow object will be created
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

        private float xmin = -1000, xmax = -1000, ymin = -1000, ymax = -1000;
        float xstart = 0, ystart = 0, xend = 0, yend = 0;

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
            if (shape_mode == R.id.shape_rect) {
                mCanvas.drawRect(xmin, ymin, xmax, ymax, mPaintToUse);
                if (isFilling) {
                    mCanvas.drawRect(xmin, ymin, xmax, ymax, mPaintFillBorder);
                }
            } else if (shape_mode == R.id.shape_circle) {
                mCanvas.drawCircle((xmax + xmin) / 2, (ymax + ymin) / 2, ((xmax - xmin) + (ymax - ymin)) / 4, mPaintToUse);
                if (isFilling) {
                    mCanvas.drawCircle((xmax + xmin) / 2, (ymax + ymin) / 2, ((xmax - xmin) + (ymax - ymin)) / 4, mPaintFillBorder);
                }
            } else if (shape_mode == R.id.shape_triangle) {
                Path path = new Path();
                path.setFillType(Path.FillType.EVEN_ODD);
                path.moveTo((xmax + xmin) / 2, ymin);
                path.lineTo(xmin, ymax);
                path.lineTo(xmax, ymax);
                path.lineTo((xmax + xmin) / 2, ymin);
                path.close();
                mCanvas.drawPath(path, mPaintToUse);
                if (isFilling) {
                    mCanvas.drawPath(path, mPaintFillBorder);
                }
            } else if (shape_mode == R.id.shape_line) {
                mCanvas.drawLine(xstart, ystart, xend, yend, mPaintToUse);
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
                    drawShape();
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
