package com.tianzun.clientview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class RoundProgressView extends View {
	
	/**最外围的颜色值*/
	private int mOutRoundColor = Color.argb(100, 0, 135, 209);
	/**中心圆的颜色值*/
	private int mCenterRoundColor = Color.argb(255, 255, 255, 255);
	
	/**进度的颜色*/
	private int mProgressRoundColor = Color.argb(255,245, 191, 143);
	/**进度的背景颜色*/
	private int mProgressRoundBgColor = Color.argb(50, 255, 255, 255);
	/**进度条的宽度*/
	private int mProgressWidth = 5;
	

	/***字体颜色 #0087d1*/
	private int mTextColor = Color.rgb(255, 255, 255);
	//private int mTextColor = Color.rgb(0, 135, 209);
	private float mPencentTextSize = 32;
	
	private int mWidth,mHeight;
	private int mPaddingX;
	
	private float mProgress = 0.5f;
	private float mMax = 1.0f;
	
	private String text = "配置";


	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;

	}

	private Paint mPaint = new Paint();

	public RoundProgressView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public RoundProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RoundProgressView(Context context) {
		super(context);
		init();
	}
	
	public void init(){
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mWidth = getWidth();
		mHeight = getHeight();
		
		if(mWidth > mHeight){
			mPaddingX = (mWidth-mHeight)/2;
			mWidth = mHeight;
		}
		mPaint.setAntiAlias(true); // 消除锯齿
		mPaint.setStyle(Style.FILL);
		mPaint.setColor(mOutRoundColor);
		RectF oval = new RectF(new Rect(mPaddingX, 0, mWidth+mPaddingX, mHeight));
		canvas.drawArc(oval, 0, 360, true, mPaint);
		
		int halfWidth = mWidth/6;
		mPaint.setStrokeWidth(mProgressWidth);
		mPaint.setColor(mProgressRoundBgColor);
		mPaint.setStyle(Style.STROKE);
		oval = new RectF(new Rect(halfWidth+mPaddingX, halfWidth, halfWidth*5+mPaddingX, halfWidth*5));
		canvas.drawArc(oval, 0, 360, false, mPaint);
		
		mPaint.setColor(mProgressRoundColor);
		canvas.drawArc(oval, 90, 360*mProgress/mMax, false, mPaint);
		
		
		halfWidth = mWidth/4;
		mPaint.setStyle(Style.FILL);
		mPaint.setColor(mCenterRoundColor);
		oval = new RectF(new Rect(halfWidth+mPaddingX, halfWidth, halfWidth*3+mPaddingX, halfWidth*3));		
		mPaint.reset();
		mPaint.setTextSize(mPencentTextSize);
		mPaint.setColor(mTextColor);
		mPaint.setStyle(Style.FILL);
		mPaint.setTextAlign(Align.CENTER);
		canvas.drawText(text, mWidth/2+mPaddingX, mHeight/2+mPencentTextSize/3, mPaint);

	}
	
	public void setMax(float mMax) {
		this.mMax = mMax;
	}
	
	public void setProgress(float mProgress) {
		this.mProgress = mProgress;
		invalidate();
	}
	
	public float getMax() {
		return mMax;
	}
	
	public float getProgress() {
		return mProgress;
	}
	
}
