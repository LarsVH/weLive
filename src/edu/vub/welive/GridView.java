package edu.vub.welive;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class GridView extends View {
	private String TAG = "WLJava";
	
	private Context mContext;
	private Paint mPaint;
	private int mHeight;
	private int mWidth;
	private int mSize;	
	private JCell [][] cells = new JCell[mHeight][mWidth];
	
	public class JCell {
		public Color color;
		
		public JCell(Color color){
			this.color = color;
		}
	}
	
	public GridView(Context mContext,int height,int width){
		super(mContext);
		this.mContext 	= mContext;
		mPaint 			= new Paint(Color.BLACK);
		mHeight			= height;
		mWidth			= width;
		mSize 			= mHeight * mWidth;
	}
	
	@Override
	 public boolean onTouchEvent(MotionEvent event) {
	        float x = event.getX();
	        float y = event.getY();
	        	        
	        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            	Log.d(TAG, "GridView -- onTouch: x=" + x + " - y=" + y);
            	// TODO: create a callback
            	getCell((int)x,(int)y);
                invalidate();
                break;	        
	        }
	        return true;        
	 }	 
	 
	public void getCell(int xCo, int yCo){
		int row;
		int col;
		
		// Determine col
		for(int i = 0; i < mWidth; i++){
			int left = i * (mSize + 5);
			int right = left + mSize;
			if(xCo <= right){
				if(xCo >= left){
					col = i;
					Log.d(TAG, "getCell(row,col) -- touched col:" + (col+1));
					break;
				}
				else
					Log.d(TAG, "getCell(row,col) -- touched vertical seperator");
			}
		}
		// Determine row
		for(int j = 0; j < mHeight; j++){
			int top = j * (mSize + 5);
			int bottom = top + mSize;
			if(yCo <= bottom){
				if(yCo >= top){
					row = j;
					Log.d(TAG, "getCell(row,col) -- touched row:" + (row+1));
					break;
				}
				else
					Log.d(TAG, "getCell(row,col) -- touched horizontal seperator");
			}
		}
	// TODO: return JCell from cells array
	}
	
	//XXX !!!: communicatie AT -> Java arrays beginnen vanaf 0 (AT vanaf 1!)
	
	@Override
	public 	void onDraw(Canvas mCanvas){
		for(int i = 0; i < mWidth; i++) {
		    for(int j = 0; j < mHeight; j++) {
		    	int left = i * (mSize + 5);
		    	int top = j * (mSize + 5);
		    	int right = left + mSize;
		    	int bottom = top + mSize;
		    	mCanvas.drawRect(new Rect(left, top, right, bottom), mPaint);
		    	// TODO: vul cells array op met zwarte cellen
		    }
		}
	}

}
