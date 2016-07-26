package com.tianzun.clientview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import com.tele.control.R;

 
@SuppressLint("DrawAllocation")
public class SlipButton extends View implements OnTouchListener {
 
    private boolean NowChoose = true;
    private boolean OnSlip = false;
    private float DownX, NowX;
    private Rect Btn_On, Btn_Off;
    private boolean isChgLsnOn = true;
    private OnBChangedListener ChgLsn; 
    private Bitmap bg_on, bg_off, slip_btn; 
    public SlipButton(Context context) {
        super(context);
        init();
    }
    
    public boolean isChecked(){
    	return NowChoose;
    }
 
    public void setChecked(boolean check){
    	NowChoose = check;
    	invalidate();
    }
    
    public SlipButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
 
    private void init() {
        bg_on = BitmapFactory.decodeResource(getResources(),
                R.drawable.bss0);
        bg_off = BitmapFactory.decodeResource(getResources(),
                R.drawable.bss0);
        slip_btn = BitmapFactory.decodeResource(getResources(),R.drawable.bss1);
        Btn_On = new Rect(0, 0, slip_btn.getWidth(), slip_btn.getHeight());
        Btn_Off = new Rect(bg_off.getWidth() - slip_btn.getWidth(), 0,
                bg_off.getWidth(), slip_btn.getHeight());
        setOnTouchListener(this);
    }
 
    
    @SuppressLint("DrawAllocation")
	protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Matrix matrix = new Matrix();
        Paint paint = new Paint();
        float x;
        {
            if ((NowX < (bg_on.getWidth() / 2))&&!NowChoose)
            {
            	slip_btn = BitmapFactory.decodeResource(getResources(),R.drawable.bss1);
                canvas.drawBitmap(bg_off, matrix, paint);
            }
            else{
            	slip_btn = BitmapFactory.decodeResource(getResources(),R.drawable.bss2);
                canvas.drawBitmap(bg_on, matrix, paint);
            }
 
            if (OnSlip)
            {
                if (NowX >= bg_on.getWidth())
                    x = bg_on.getWidth() - slip_btn.getWidth() / 2;
                else
                    x = NowX - slip_btn.getWidth() / 2;
            } else {
                if (NowChoose)
                    x = Btn_Off.left;
                else
                    x = Btn_On.left;
            }
            if (x < 0)
                x = 0;
            else if (x > bg_on.getWidth() - slip_btn.getWidth())
                x = bg_on.getWidth() - slip_btn.getWidth();
            canvas.drawBitmap(slip_btn, x, 0, paint);
        }
    }
 
    
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction())
        {
        case MotionEvent.ACTION_MOVE:
            NowX = event.getX();
            break;
        case MotionEvent.ACTION_DOWN:
            if (event.getX() > bg_on.getWidth()
                    || event.getY() > bg_on.getHeight())
                return false;
            OnSlip = true;
            DownX = event.getX();
            NowX = DownX;
            break;
        case MotionEvent.ACTION_UP:
            OnSlip = false;
            boolean LastChoose = NowChoose;
            if (event.getX() >= (bg_on.getWidth() / 2))
                NowChoose = true;
            else
                NowChoose = false;
            if (isChgLsnOn && (LastChoose != NowChoose))
                ChgLsn.OnChanged(NowChoose);
            break;
        default:
 
        }
        invalidate();
        return true;
    }
 
    public void SetBOnChangedListener(OnBChangedListener ChgLsn){
    	
    	this.ChgLsn=ChgLsn;

    }
    public interface OnBChangedListener{
    	void OnChanged(boolean onoff);
    }

}
