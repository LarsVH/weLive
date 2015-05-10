package edu.vub.welive;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class GridView extends View {
	private Context mContext;
	private Paint mPaint;
	private int mHeight;
	private int mWidth;
	private int mSize;
	
	public GridView(Context mContext,int height,int width){
		super(mContext);
		this.mContext 	= mContext;
		mPaint 			= new Paint(Color.BLACK);
		mHeight			= height;
		mWidth			= width;
		mSize 			= mHeight * mWidth;
	}
	
	@Override
	public 	void onDraw(Canvas mCanvas){
		for(int i = 0; i < mWidth; i++) {
		    for(int j = 0; j < mHeight; j++) {
		    	int left = i * (mSize + 5);
		    	int top = j * (mSize + 5);
		    	int right = left + mSize;
		    	int bottom = top + mSize;
		    	mCanvas.drawRect(new Rect(left, top, right, bottom), mPaint);
		    }
		}
	}

}
