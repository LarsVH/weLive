package edu.vub.welive;

import java.io.IOException;

import edu.vub.at.IAT;
import edu.vub.at.android.util.IATAndroid;
import edu.vub.at.exceptions.InterpreterException;
import edu.vub.welive.R;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity implements JWeLive {
	private GridView 	grid;
	private IAT 		iat;
	public static Handler 	mHandler;
	private String TAG = "WLJava";

	private TextView atLogs;
	private String previousLog = new String("");

	//Message code send to looper thread
	public final int _MSG_TEST_ = 0;
	public final int _MSG_TOUCH_ = 1;
	public final int _MSG_ONOFF_ = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ViewGroup rootView = (ViewGroup) findViewById(R.id.rootlayout);        

		// DISCONNECT BUTTON
		Button disconnectButton = new Button(getApplicationContext());
		disconnectButton.setId(44);
		RelativeLayout.LayoutParams disconnectButtonParams =
				new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		disconnectButtonParams.addRule(RelativeLayout.ALIGN_TOP, RelativeLayout.TRUE);
		disconnectButtonParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
		disconnectButtonParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
		//Button disconnectButton = (Button) rootView.findViewById(R.id.disconnect);
		disconnectButton.setText("(Dis)connect");
		rootView.addView(disconnectButton, disconnectButtonParams);


		// GRID
		grid = new GridView(getApplicationContext(), this, 10, 10);
		grid.setId(55);
		grid.setBackgroundColor(Color.WHITE);
		RelativeLayout.LayoutParams gridParams =
				new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		//gridParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		//gridParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		gridParams.addRule(RelativeLayout.BELOW, disconnectButton.getId());

		//gridParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

		gridParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
		gridParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;

		//rootView.addView(grid);
		rootView.addView(grid, gridParams);
		//setContentView(grid);

		// LOGVIEW
		/*ScrollView atLogsScroll = new ScrollView(getApplicationContext());
        atLogsScroll.setId(66);
        RelativeLayout.LayoutParams atLogsScrollParams =
        		new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,0,1);
        atLogsScroll.setLayoutParams(scrollParams);
        atLogsScrollParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        atLogsScrollParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
       // atLogsScrollParams.addRule(ScrollView.SCROLLBAR_POSITION_DEFAULT);
        atLogsScrollParams.addRule(RelativeLayout.ALIGN_BOTTOM, grid.getId());
		 */
		atLogs = new TextView(getApplicationContext());
		RelativeLayout.LayoutParams atLogsParams =
				new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);       
		atLogsParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
		atLogsParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
		atLogsParams.addRule(RelativeLayout.ALIGN_BOTTOM, grid.getId());
		//atLogsScroll.addView(atLogs, atLogsParams);

		rootView.addView(atLogs, atLogsParams);

		//---------------------------------------------------------
		//Copy AmbientTalk files to the SD card
		Intent i = new Intent(this, weLiveAssetInstaller.class);
		startActivityForResult(i,0);
		//Start up the AmbientTalk code and eval weLive.at file
		new StartIATTask().execute((Void)null);
		//Spawn loop handling messages to AmbientTalk
		LooperThread lt = new LooperThread();
		lt.start();
		mHandler = lt.mHandler;
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// Starts up the AmbientTalk interpreter and interprets the code provided in assets/atlib/weLive/weLive.at
	public class StartIATTask extends AsyncTask<Void,String,Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				iat = IATAndroid.create(MainActivity.this);
				iat.evalAndPrint("import /.weLive.weLive.makeWeLive()", System.err);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterpreterException e) {
				Log.e("AmbientTalk","Could not start IAT",e);
			}
			return null;
		}

	}

	private static ATWeLive atwl;

	// Call the AmbientTalk methods in a separate thread to avoid blocking the UI.
	class LooperThread extends Thread {

		public Handler mHandler = new Handler() {
			public void handleMessage(Message msg) {
				if(null == atwl)
						return;
				switch (msg.what) {
				case _MSG_TEST_:{
					String arg = (String) msg.obj;
					Log.d(TAG, "mHandler -- calling callAT");
					atwl.callAT(arg);
					break;
				}
				case _MSG_TOUCH_:{
					int[] rowcol = (int[]) msg.obj;
					Log.d(TAG, "MGG_TOUCH -- row = " + rowcol[0] + " , col = " + rowcol[1]);
					//atwl.touchedCell(rowcol[0], rowcol[1]);
					atwl.callAT("zottterdezot");
					break;
				}
				case _MSG_ONOFF_:{
					//atwl.switchOnlineOffline();
					break;
				}			
				}

			}
		};


		public void run() {
			Looper.prepare();
			Looper.loop();
		}
	}

	@Override
	public JWeLive registerATApp(ATWeLive weLive) {
		Log.d(TAG, "registerATApp called");
		atwl = weLive;
		// XXX Testing Java <-> AT connection
		//mHandler.sendMessage(Message.obtain(mHandler,_MSG_TEST_, "TestArgument"));
		return this;	// --> we have to implement all methods AT can call in this class
	}


	public void callJava(String arg) {
		Log.d(TAG, "received callJava -- Argument: " + arg);
	}
	@Override
	public void fillCell(int row, int col, int ID) {
		grid.fillCell(row, col, ID);		
	}
	@Override
	public void clearCell(int row, int col) {
		grid.clearCell(row, col);		
	}
	@Override
	public void greyCell(int row, int col) {
		grid.greyCell(row, col);		
	}

	@Override
	public void displayLog(String log) {
		atLogs.setText("");
		atLogs.append(log + "\n");
		atLogs.append(previousLog);		
		previousLog = log;		
	}
}
