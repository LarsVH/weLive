package edu.vub.welive;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class GridView extends View {
	private String TAG = "WLJava";

	private Context mContext;
	private Canvas mCanvas;
	private Handler mHandler;
	private final int _MSG_TOUCH_ = 1;
	private final int _MSG_ONOFF_ = 2;
	private int defaultColor;
	private Paint mPaint;
	private int mHeight;
	private int mWidth;
	private int mSize;	
	private JCell [][] cells;
	private ArrayList<Integer> colors;
	private Iterator<Integer> colorsIter;
	private Hashtable<Integer,Integer> colorTable;	// key = userID, value = color


	public GridView(Context mContext, Handler mHandler, int height,int width){
		super(mContext);
		this.mContext 	= mContext;
		this.mHandler	= mHandler;
		defaultColor 	= Color.BLACK;		
		mPaint 			= new Paint();
		mHeight			= height;
		mWidth			= width;
		mSize 			= mHeight * mWidth;
		cells = new JCell[mHeight][mWidth];	// [Rows][Cols]
		// Initialize cell array
		for(int i = 0; i < mHeight; i++){
			for(int j = 0; j < mWidth; j++){
				cells[i][j] = new JCell(defaultColor);
			}
		}

		colors = new ArrayList<Integer>();
		colors.add(Color.RED);
		colors.add(Color.GREEN);
		colors.add(Color.BLUE);
		colors.add(Color.YELLOW);
		colors.add(Color.CYAN);
		colors.add(Color.MAGENTA);
		colorsIter = colors.iterator();

		colorTable = new Hashtable<Integer,Integer>();

	}

	public class JCell {
		public int color;
		public boolean greyed;

		public JCell(int color){
			this.color = color;
			this.greyed = false;
		}
		public JCell(){
			color = -1;
			greyed = false;
		};

		public boolean isValid(){
			if(!(color == -1))
				return true;
			else
				return false;
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.d(TAG, "GridView -- onTouch: x=" + x + " - y=" + y);
			int[] co = getCellRowCol((int)x,(int)y);
			mHandler.sendMessage(Message.obtain(mHandler,_MSG_TOUCH_, co));
			//TODO create a callback to AT: callAT(co[0],co[1])
			
			/* if(co != null && cells[co[0]][co[1]].color == defaultColor){
				Random rand = new Random();
				fillCell(co[0], co[1], rand.nextInt(20));					
			}
			else
				if(co != null && cells[co[0]][co[1]].color != defaultColor && !cells[co[0]][co[1]].greyed){
					greyCell(co[0], co[1]);
				}
				else if(co != null && cells[co[0]][co[1]].color != defaultColor && cells[co[0]][co[1]].greyed){
					unGreyCell(co[0], co[1]);
				}
				*/
			invalidate();
			break;	        
		}
		// DEBUG
		return true;        
	}	 

	public int[] getCellRowCol(int xCo, int yCo){
		int row = -1;
		int col = -1;

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
		if(row == -1 || col == -1){
			return null;
		}
		else {
			int[] result = new int[2];
			result[0] = row;
			result[1] = col;
			return result;
		}
	}

	public int[] getCellCoordinates(int row, int col){
		int [] result = new int[4];
		int left = col * (mSize + 5);
		int top = row * (mSize + 5);
		int right = left + mSize;
		int bottom = top + mSize;

		result[0] = left;
		result[1] = top;
		result[2] = right;
		result[3] = bottom;
		return result;
	}
	
	// Generate a random color
	public int generateColor(){
		Random rand = new Random();		
		return Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
	}

	public int nextColor(){
		if(colorsIter.hasNext())
			return colorsIter.next();
		else
			return generateColor();
	};
	
	public void fillCell(int row, int col, int ID) {
		int color;		
		if(colorTable.containsKey(ID))
			color = colorTable.get(ID);			
		else {
			color = nextColor();
			colorTable.put(ID, color);
		}
		cells[row][col].color = color;

		draw(mCanvas);
	}

	public void clearCell(int row, int col) {
		cells[row][col].color = defaultColor;
		draw(mCanvas);		
	}
	
	public void greyCell(int row, int col) {
		int currentColor = cells[row][col].color;
		clearCell(row, col);
		
		cells[row][col].color = currentColor;
		cells[row][col].greyed = true;
		draw(mCanvas);
	}
	
	public void unGreyCell(int row, int col){
		cells[row][col].greyed = false;		
		draw(mCanvas);
	}


	//XXX !!!: communicatie AT -> Java arrays beginnen vanaf 0 (AT vanaf 1!)

	@Override
	public void onDraw(Canvas mCanvas){
		this.mCanvas = mCanvas;
		for(int i = 0; i < mWidth; i++) {		// !! iterates first on columns, then on rows
			for(int j = 0; j < mHeight; j++) {
				int left = i * (mSize + 5);
				int top = j * (mSize + 5);
				int right = left + mSize;
				int bottom = top + mSize;
				mPaint.setColor(cells[j][i].color);
				mPaint.setStyle(Paint.Style.FILL);
				if(cells[j][i].greyed){
					mPaint.setAlpha(100);
				}
				mCanvas.drawRect(new Rect(left, top, right, bottom), mPaint);
			}
		}
	}

}