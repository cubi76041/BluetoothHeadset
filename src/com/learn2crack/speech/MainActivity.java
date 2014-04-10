package com.learn2crack.speech;

import java.util.ArrayList;
import java.util.HashMap;

import root.gast.speech.activation.SpeechActivationService;

import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final int REQUEST_CODE = 1234;
	Button Start;
	TextView Speech;
	Dialog match_text_dialog;
	ListView textlist;
	ArrayList<String> matches_text;
	Context context = this;
	BluetoothHelper mBluetoothHelper;
	AudioManager am;
	Bluetooth bt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mBluetoothHelper = new BluetoothHelper(this);
		Start = (Button) findViewById(R.id.start_reg);
//		Speech = (TextView) findViewById(R.id.speech);
//		
//		 if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
//		    	System.out.println("Gingerboard");
//		        bt = new Gingerbread();
//		    } else {
//		    	System.out.println("Honeycomb");
//		        bt = new Honeycomb();
//		    }
//		 
//		 bt.setContext(this);
//
//		    try {
//		        bt.obtainProxy();
//		    } catch (Exception e) {
//		        e.printStackTrace();
//		    }
//		    IntentFilter filter = new IntentFilter(Intent.ACTION_ALL_APPS);
//		    registerReceiver((Honeycomb) bt, filter);
//		    
//		    am = (AudioManager) getSystemService("audio");
//			 am.setBluetoothScoOn(true);
//	            am.startBluetoothSco();

//		Start.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (isConnected()) {
//
//					 if(bt.isAvailable()) {
//						 System.out.println("BT is available");
//				            bt.startVoiceRecognition();
//				        }
//					 
//					 System.out.println("Voice State:" + ((Honeycomb) bt).getVoiceState());
//					 System.out.println("Voice State 2: " + ((Honeycomb) bt).getVoiceState());
//					Intent intent = new Intent(
//							RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//					intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//							RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//					startActivityForResult(intent, REQUEST_CODE);
//				} else {
//					Toast.makeText(getApplicationContext(),
//							"Plese Connect to Internet", Toast.LENGTH_LONG)
//							.show();
//				}
////				startService(new Intent(context, MyService.class));
//			}
//
//		});
	}
	
	@Override
	protected void onResume()
	{ 
		super.onResume();
		System.out.println("Starting bluetooth helper ... ");
	    mBluetoothHelper.start();
		
//        Intent i = DirectToSpeechService.makeStartServiceIntent(this,
//                "Speak Hello");
//        startService(i);
	}
//
	@Override
	protected void onPause()
	{
		super.onPause();
		System.out.println("Stopping bluetooth helper ... ");
	    mBluetoothHelper.stop();
	}
	
	private class BluetoothHelper extends BluetoothHeadsetUtils
	{
	    public BluetoothHelper(Context context)
	    {
	        super(context);
	    }

	    @Override
	    public void onScoAudioDisconnected()
	    {
	        System.out.println("Cancel speech recognizer if desired");
	    }

	    @Override
	    public void onScoAudioConnected()
	    {           
	        System.out.println("Should start speech recognition here if not already started");  
	        Start.performClick();
	    }

	    @Override
	    public void onHeadsetDisconnected()
	    {

	    }

	    @Override
	    public void onHeadsetConnected()
	    {
	    	System.out.println("Should Start speech here");
			Intent intent = new Intent(
			RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
			RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	startActivityForResult(intent, REQUEST_CODE);
	    }
	    
	    protected void speak(String text)
	    {

	        HashMap<String, String> myHashRender = new HashMap<String, String>();

	        if (mBluetoothHelper.isOnHeadsetSco())
	        {
	            myHashRender.put(TextToSpeech.Engine.KEY_PARAM_STREAM, 
	                String.valueOf(AudioManager.STREAM_VOICE_CALL));
	        }
//	        mTts.speak(text, TextToSpeech.QUEUE_FLUSH, myHashRender);
	    }
	}

	public boolean isConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo net = cm.getActiveNetworkInfo();
		if (net != null && net.isAvailable() && net.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 if(bt.isAvailable()) {
			 System.out.println("BT is available");
	            bt.stopVoiceRecognition();
	        }
//		 am.setBluetoothScoOn(false);
//         am.stopBluetoothSco();
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

			match_text_dialog = new Dialog(MainActivity.this);
			match_text_dialog.setContentView(R.layout.dialog_matches_frag);
			match_text_dialog.setTitle("Select Matching Text");
			textlist = (ListView) match_text_dialog.findViewById(R.id.list);
			matches_text = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, matches_text);
			textlist.setAdapter(adapter);
			textlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Speech.setText("You have said "
							+ matches_text.get(position));
					match_text_dialog.hide();
				}
			});
			match_text_dialog.show();

		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}