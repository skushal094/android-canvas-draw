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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DrawingView dv;
    private Paint mPaint, mFillPaint, mHollowPaint, mPaintToUse, mPaintFillBorder;
    private int shape_mode = R.id.shape_line;
    private boolean isFilling = false;

    // for convex hull
    List<Float> points;


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
            case R.id.item_free_hand:
                shape_mode = 0; // zero for free hand
                isFilling = false;
                mPaintToUse = mHollowPaint;
                this.setTitle("Free Hand");
                break;

            case R.id.shape_line:
                shape_mode = R.id.shape_line;
                this.setTitle("Line");
                break;
            case R.id.shape_circle:
                shape_mode = R.id.shape_circle;
                this.setTitle("Circle");
                break;
            case R.id.shape_rect:
                shape_mode = R.id.shape_rect;
                this.setTitle("Rectangle");
                break;
            case R.id.shape_triangle:
                shape_mode = R.id.shape_triangle;
                this.setTitle("Triangle");
                break;

            case R.id.only_border:
                isFilling = false;
                mPaintToUse = mHollowPaint;
                break;
            case R.id.color_white:
                isFilling = true;
                mFillPaint.setColor(Color.WHITE);
                mPaintToUse = mFillPaint;
                break;
            case R.id.color_black:
                isFilling = true;
                mFillPaint.setColor(Color.BLACK);
                mPaintToUse = mFillPaint;
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

            case R.id.item_clear:
                dv = new DrawingView(this);
                dv.setBackgroundColor(0xFFFFFFFF);
                setContentView(dv);
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
        mPaint.setStrokeWidth(5);

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
        mHollowPaint.setStrokeWidth(5);
        mHollowPaint.setColor(Color.BLACK);

        mPaintToUse = mHollowPaint;    // by default the hollow object will be created
        this.setTitle("Line");
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

        private float x_min = -1000, x_max = -1000, y_min = -1000, y_max = -1000;
        float x_start = 0, y_start = 0, x_end = 0, y_end = 0;
        float x1, y1, x2, y2, x3, y3;
        float x1_f, y1_f, x2_f, y2_f, x3_f, y3_f;
        double res = 0.0;

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
            points = new ArrayList<>();
            points.add(x);
            points.add(y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                points.add(x);
                points.add(y);
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
            if (shape_mode == 0) {
                mCanvas.drawPath(mPath, mPaintToUse);
            }
            // kill this so we don't double draw
            mPath.reset();
        }

        private void drawShape() {
            if (shape_mode == R.id.shape_rect) {
                mCanvas.drawRect(x_min, y_min, x_max, y_max, mPaintToUse);
                if (isFilling) {
                    mCanvas.drawRect(x_min, y_min, x_max, y_max, mPaintFillBorder);
                }
            } else if (shape_mode == R.id.shape_circle) {
                mCanvas.drawCircle((x_max + x_min) / 2, (y_max + y_min) / 2, ((x_max - x_min) + (y_max - y_min)) / 4, mPaintToUse);
                if (isFilling) {
                    mCanvas.drawCircle((x_max + x_min) / 2, (y_max + y_min) / 2, ((x_max - x_min) + (y_max - y_min)) / 4, mPaintFillBorder);
                }
            } else if (shape_mode == R.id.shape_triangle) {
                Path path = new Path();
                path.setFillType(Path.FillType.EVEN_ODD);

                // for convex
                try {
                    List<Float> hullPoints = ConvexHull.main(points);    // This gives points of convex hull
                    int hullSize = hullPoints.size();
                    if (hullSize > 1) {
                        res = 0;
                        int total_points = hullSize / 2;

                        if (total_points > 3) {
                            for (int i = 0; i < hullSize - 4; i = i + 2) {
                                for (int j = i + 2; j < hullSize - 2; j = j + 2) {
                                    for (int k = i + 4; k < hullSize; k = k + 2) {
                                        x1 = hullPoints.get(i);
                                        y1 = hullPoints.get(i + 1);
                                        x2 = hullPoints.get(j);
                                        y2 = hullPoints.get(j + 1);
                                        x3 = hullPoints.get(k);
                                        y3 = hullPoints.get(k + 1);

                                        double areaTemp = (0.5 * Math.abs((x1 * (y2 - y3)) + (x2 * (y3 - y1)) + (x3 * (y1 - y2))));
                                        if (res < areaTemp) {
                                            res = areaTemp;
                                            x1_f = x1;
                                            x2_f = x2;
                                            x3_f = x3;
                                            y1_f = y1;
                                            y2_f = y2;
                                            y3_f = y3;
                                        }
                                    }
                                }
                            }

                            path.moveTo(x1_f, y1_f);
                            path.lineTo(x2_f, y2_f);
                            path.lineTo(x3_f, y3_f);
                            path.close();
                            mCanvas.drawPath(path, mPaintToUse);
                        } else {
                            path.moveTo(hullPoints.get(0), hullPoints.get(1));
                            for (int i = 2; i < hullPoints.size(); i = i + 2) {
                                path.lineTo(hullPoints.get(i), hullPoints.get(i + 1));
                            }

                            path.close();
                            mCanvas.drawPath(path, mPaintToUse);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (isFilling) {
                    mCanvas.drawPath(path, mPaintFillBorder);
                }
            } else if (shape_mode == R.id.shape_line) {
                mCanvas.drawLine(x_start, y_start, x_end, y_end, mPaintToUse);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            if ((x_min == -1000) || (x_min > x)) {
                x_min = x;
            }
            if ((x_max == -1000) || (x_max < x)) {
                x_max = x;
            }
            if ((y_min == -1000) || (y_min > y)) {
                y_min = y;
            }
            if ((y_max == -1000) || (y_max < y)) {
                y_max = y;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x_start = x;
                    y_start = y;
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    x_end = x;
                    y_end = y;
                    drawShape();
                    x_min = -1000;
                    x_max = -1000;
                    y_min = -1000;
                    y_max = -1000;
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }
}
