package com.tianzun.clientview;
import com.tele.control.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CpView extends View {
        private Context context;
        private int bigCircle; 
        private int rudeRadius; 
        private int centerColor; 
        private Bitmap bitmapBack;
        private Paint mPaint; 
        private Paint mCenterPaint;
        private Point centerPoint;
        private Point mRockPosition;
        private OnColorChangedListener listener; 
        private int length;
        private boolean switch_on=true;
        
        public void setOn(){
        	switch_on = true;
        }
        public void setOff(){
        	switch_on = false;
        }

        public CpView(Context context) {
                super(context);
        }

        public CpView(Context context, AttributeSet attrs, int defStyleAttr) {
                super(context, attrs, defStyleAttr);
                this.context = context;
                init(attrs);
        }

        public CpView(Context context, AttributeSet attrs) {
                super(context, attrs);
                this.context = context;
                init(attrs);
        }

        public void setOnColorChangedListener(OnColorChangedListener listener) {
                this.listener = listener;
        }


        private void init(AttributeSet attrs) {
                TypedArray types = context.obtainStyledAttributes(attrs,
                                R.styleable.color_picker);
                try {
                        bigCircle = types.getDimensionPixelOffset(
                                        R.styleable.color_picker_circle_radius, 125);//定义半径
                        rudeRadius = types.getDimensionPixelOffset(
                                        R.styleable.color_picker_center_radius, 10);//定义半径
                        centerColor = types.getColor(R.styleable.color_picker_center_color,
                                        Color.WHITE);
                } finally {
                        types.recycle(); 
                }
                bitmapBack = BitmapFactory.decodeResource(getResources(),
                                R.drawable.color_range);
                bitmapBack = Bitmap.createScaledBitmap(bitmapBack, bigCircle * 2,
                                bigCircle * 2, false);
                centerPoint = new Point(bigCircle, bigCircle);
                mRockPosition = new Point(centerPoint);
                mPaint = new Paint();
                mPaint.setAntiAlias(true);
                mCenterPaint = new Paint();
                mCenterPaint.setColor(centerColor);
        }

        @Override
        protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                canvas.drawBitmap(bitmapBack, 0, 0, mPaint);
                canvas.drawCircle(mRockPosition.x, mRockPosition.y, rudeRadius, mCenterPaint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
        	    if(!switch_on)return false;
        	    
                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: 
                        length = getLength(event.getX(), event.getY(), centerPoint.x,
                                        centerPoint.y);
                        if (length > bigCircle - rudeRadius) {
                                return true;
                        }
                        break;
                case MotionEvent.ACTION_MOVE:
                        length = getLength(event.getX(), event.getY(), centerPoint.x,
                                        centerPoint.y);
                        if (length <= bigCircle - rudeRadius) {
                        	       mRockPosition.set((int) event.getX(), (int) event.getY());
                        } else {
                        }
                        listener.onColorChange(bitmapBack.getPixel(mRockPosition.x, mRockPosition.y));
                        break;
                case MotionEvent.ACTION_UP:
                		listener.onStop();
                        break;
                        
              

                default:
                        break;
                }
                invalidate(); 
                return true;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                setMeasuredDimension(bigCircle * 2, bigCircle * 2);
        }


        public static int getLength(float x1, float y1, float x2, float y2) {
                return (int) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        }


        public static Point getBorderPoint(Point a, Point b, int cutRadius) {
                float radian = getRadian(a, b);
                return new Point(a.x + (int) (cutRadius * Math.cos(radian)), a.x
                                + (int) (cutRadius * Math.sin(radian)));
        }


        public static float getRadian(Point a, Point b) {
                float lenA = b.x - a.x;
                float lenB = b.y - a.y;
                float lenC = (float) Math.sqrt(lenA * lenA + lenB * lenB);
                float ang = (float) Math.acos(lenA / lenC);
                ang = ang * (b.y < a.y ? -1 : 1);
                return ang;
        }

        public interface OnColorChangedListener {
                void onColorChange(int color);
                void onStop();
        }
}
