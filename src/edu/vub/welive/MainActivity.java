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


public class MainActivity extends ActionBarActivity implements JWeLive {
	private GridView 	grid;
	private IAT 		iat;
	private Handler 	mHandler;
	private String TAG = "WLJava";
	
	//Message code send to looper thread
	private final int _MSG_TEST_ = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        grid = new GridView(getApplicationContext(),10,10);
        grid.setBackgroundColor(Color.WHITE);
        setContentView(grid);
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
	private class LooperThread extends Thread {

		public Handler mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case _MSG_TEST_:{
						String arg = (String) msg.obj;
						Log.d(TAG, "mHandler -- calling callAT");
						atwl.callAT(arg);
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
		mHandler.sendMessage(Message.obtain(mHandler,_MSG_TEST_, "TestArgument"));
		return this;	// --> we have to implement all methods AT can call in this class
	}


	@Override
	public JWeLive callJava(String arg) {
		Log.d(TAG, "received callJava -- Argument: " + arg);
		return null;
	}
}
