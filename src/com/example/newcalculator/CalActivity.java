package com.example.newcalculator;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CalActivity extends Activity implements OnClickListener, OnCompletionListener {
	
	MediaPlayer mp;
	LinearLayout l;
	private TextView mCalculatorDisplay;
	private Boolean userIsInTheMiddleOfTypingANumber = false;
	private CalculatorBrain mCalculatorBrain;
	private static final String DIGITS = "0123456789.";
	int num;
	DecimalFormat df = new DecimalFormat("@###########");
	ArrayList<String> songs;    
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cal);
		l = (LinearLayout) findViewById(R.id.lay);
		//l.setBackgroundColor(Color.GRAY);
		
		mCalculatorBrain = new CalculatorBrain();
		mCalculatorDisplay = (TextView) findViewById(R.id.textView1);

		df.setMinimumFractionDigits(0);
		df.setMinimumIntegerDigits(1);
		df.setMaximumIntegerDigits(8);
				
		findViewById(R.id.button0).setOnClickListener(this);
		findViewById(R.id.button1).setOnClickListener(this);
		findViewById(R.id.button2).setOnClickListener(this);
		findViewById(R.id.button3).setOnClickListener(this);
		findViewById(R.id.button4).setOnClickListener(this);
		findViewById(R.id.button5).setOnClickListener(this);
		findViewById(R.id.button6).setOnClickListener(this);
		findViewById(R.id.button7).setOnClickListener(this);
		findViewById(R.id.button8).setOnClickListener(this);
		findViewById(R.id.button9).setOnClickListener(this);

		findViewById(R.id.buttonAdd).setOnClickListener(this);
		findViewById(R.id.buttonSubtract).setOnClickListener(this);
		findViewById(R.id.buttonMultiply).setOnClickListener(this);
		findViewById(R.id.buttonDivide).setOnClickListener(this);
		findViewById(R.id.buttonToggleSign).setOnClickListener(this);
		findViewById(R.id.buttonDecimalPoint).setOnClickListener(this);
		findViewById(R.id.buttonEquals).setOnClickListener(this);
		findViewById(R.id.buttonClear).setOnClickListener(this);
		findViewById(R.id.buttonClearMemory).setOnClickListener(this);
		findViewById(R.id.buttonAddToMemory).setOnClickListener(this);
		findViewById(R.id.buttonSubtractFromMemory).setOnClickListener(this);
		findViewById(R.id.buttonRecallMemory).setOnClickListener(this);

		// The following buttons only exist in layout-land (Landscape mode)
		if (findViewById(R.id.buttonSquareRoot) != null) {
			findViewById(R.id.buttonSquareRoot).setOnClickListener(this);
		}
		if (findViewById(R.id.buttonSquared) != null) {
			findViewById(R.id.buttonSquared).setOnClickListener(this);
		}
		if (findViewById(R.id.buttonInvert) != null) {
			findViewById(R.id.buttonInvert).setOnClickListener(this);
		}
		if (findViewById(R.id.buttonSine) != null) {
			findViewById(R.id.buttonSine).setOnClickListener(this);
		}
		if (findViewById(R.id.buttonCosine) != null) {
			findViewById(R.id.buttonCosine).setOnClickListener(this);
		}
		if (findViewById(R.id.buttonTangent) != null) {
			findViewById(R.id.buttonTangent).setOnClickListener(this);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.cal, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getItemId() == R.id.shuffle)
		{
			mp.reset();
			if(!songs.isEmpty())
			{
				int l = songs.size();
				mp = MediaPlayer.create(this, Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/Music/" + songs.get((int)(Math.random()*l))));
			}
			else
			{
				Toast.makeText(this, "No music found on sd card, playing default music", Toast.LENGTH_LONG).show();
				mp = MediaPlayer.create(this, R.raw.m);
			}
			
			mp.start();
		}
		else if(item.getItemId() == R.id.toggle)
		{
			if(mp.isPlaying())
				mp.pause();
			else
				mp.start();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
	
		String buttonPressed = ((Button) v).getText().toString();

		if (DIGITS.contains(buttonPressed)) {

			// digit was pressed
			if (userIsInTheMiddleOfTypingANumber)
			{

				if (buttonPressed.equals(".") && mCalculatorDisplay.getText().toString().contains("."))
				{}
				else
				{
					mCalculatorDisplay.append(buttonPressed);
				}

			} 
			else
			{
				if (buttonPressed.equals("."))
				{
					mCalculatorDisplay.setText(0 + buttonPressed);
				} 
				else
				{
					mCalculatorDisplay.setText(buttonPressed);
				}
				userIsInTheMiddleOfTypingANumber = true;
			}
		}
		else
		{
			if (userIsInTheMiddleOfTypingANumber)
			{
				mCalculatorBrain.setOperand(Double.parseDouble(mCalculatorDisplay.getText().toString()));
				userIsInTheMiddleOfTypingANumber = false;
			}

			mCalculatorBrain.performOperation(buttonPressed);
			mCalculatorDisplay.setText(df.format(mCalculatorBrain.getResult()));
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		// Save variables on screen orientation change
		outState.putDouble("OPERAND", mCalculatorBrain.getResult());
		outState.putDouble("MEMORY", mCalculatorBrain.getMemory());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		// Restore variables on screen orientation change
		mCalculatorBrain.setOperand(savedInstanceState.getDouble("OPERAND"));
		mCalculatorBrain.setMemory(savedInstanceState.getDouble("MEMORY"));
		mCalculatorDisplay.setText(df.format(mCalculatorBrain.getResult()));
	}

	public ArrayList<String> getMP3Files(String directory)
	{
	    ArrayList<String> files = new ArrayList<String>();
	    File folder = new File(directory);
	    for (File file : folder.listFiles())
	        if (file.isFile())
	            if (file.getName().endsWith(".mp3") || file.getName().endsWith(".MP3"))
	                files.add(file.getName());
	    return files;
	}
	
	@Override
	public void onCompletion(MediaPlayer m)
	{
		int l = songs.size();
		mp.reset();
		mp = MediaPlayer.create(this, Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/Music/" + songs.get((int)(Math.random()*l))));
		mp.start();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		SharedPreferences sp = getSharedPreferences("mem", Context.MODE_PRIVATE);
		CalculatorBrain.mCalculatorMemory = sp.getFloat("mr", 0.0f); 
		songs = new ArrayList<String>();
		songs = getMP3Files(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music");
		if(!songs.isEmpty())
		{
			int l = songs.size();
			mp = MediaPlayer.create(this, Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/Music/" + songs.get((int)(Math.random()*l))));
		}
		else
		{
			Toast.makeText(this, "No music found on sd card, playing default music", Toast.LENGTH_LONG).show();
			mp = MediaPlayer.create(this, R.raw.m);
		}
		
		mp.start();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		mp.stop();
		mp.reset();
		SharedPreferences sp = getSharedPreferences("mem", Context.MODE_PRIVATE);
		SharedPreferences.Editor ed = sp.edit();
		ed.putFloat("mr", (float)CalculatorBrain.mCalculatorMemory);
		ed.commit();
	}	
}