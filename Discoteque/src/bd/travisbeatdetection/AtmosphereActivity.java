package bd.travisbeatdetection;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.puredata.android.io.PdAudio;
import org.puredata.android.service.PdPreferences;
import org.puredata.android.service.PdService;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;
import org.puredata.core.utils.PdDispatcher;
import org.puredata.core.utils.PdListener;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.Button;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import bd.travisbeatdetection.R;

/**
 * AtmosphereActivity - This class is used to include Beat Detection and Lights Control.
 * Once the connection between the mobile phone and the bridge is done, it
 * is necessary to process audio samples to extract BPM values. This parameter
 * is used to tweak lights, changing them colors accordingly to the song's
 * tempo.  Bulbs' color are changed rhythmically using a specific function to
 * choose colors: six different scenarios are implemented.
 *
 * 1. Party   -  Random
 * 2. Forest  -  Greenish
 * 3. Sea     -  Bluish
 * 4. Summer  -  Yellowish
 * 5. Winter  -  Whitish
 * 6. Inferno -  Reddish
 *
 * @authors Mattia Bonomi, Federico Zanetti
 *
 */
public class AtmosphereActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {

	/**
	 * Declaring Hue Lights SDK & Beat Detection instances.
	 *
	 */
	private static final String TAG = "Discoteque";
	private PHHueSDK phHueSDK;
    private static final int MAX_HUE=65535;
    public int checkstop = 1;

	private PowerManager.WakeLock mWakeLock;
	TextView tv = null;
	private PdService pdService = null;
	public float tempo;
	Boolean waitingForGetTempo = false;
	Boolean mediaPlayerStopped = true;
	TravisAudioPlayback audioPlayback;
	private Toast toast = null;
	public int selectedOption = 0;
	public int alterno = 0;
	float lowB  = 110;
	float highB = 160;
	/**
	 * Using thread to command lights. A boolean variable is used to choose between different scenarios.
	 *
	 */
	class Task implements Runnable{
    	@Override
    	public void run(){
    		while(checkstop == 0){
    			long timewait = Math.round(60000 / tempo );
    			timewait = Math.round(timewait);
    			PHBridge bridge = phHueSDK.getSelectedBridge();
    			List<PHLight> allLights = bridge.getResourceCache().getAllLights();
    		//Party
                if(selectedOption == 0){
                	Random rand = new Random();
                	for (PHLight light : allLights) {
                	    PHLightState lightState = new PHLightState();
                	    lightState.setTransitionTime(0);
                	    lightState.setHue(rand.nextInt(MAX_HUE));
                	    bridge.updateLightState(light, lightState, listener);
                	    }
                	}
              //Forest
    			if(selectedOption == 1){
    				switch (alterno) {
    				  case 0:
    					  for (PHLight light : allLights) {
  	    					PHLightState lightState = new PHLightState();
  	    					lightState.setTransitionTime(0);
  	    					lightState.setX((float) 0.41);
  	    					lightState.setY((float) 0.51721);
  	    				    bridge.updateLightState(light, lightState, listener);
  	    				    }
    				        break;
    				  case 1:
    					  for (PHLight light : allLights) {
	  	    					PHLightState lightState = new PHLightState();
	  	    					lightState.setTransitionTime(0);
	  	    					lightState.setX((float) 0.6121);
	  	    					lightState.setY((float) 0.3584);
	  	    				    bridge.updateLightState(light, lightState, listener);
	  	    				    }
    				        break;
    				}
    			}

    			//Sea
    			if(selectedOption == 2){
    				switch (alterno) {
    				  case 0:
    					  for (PHLight light : allLights) {
  	    					PHLightState lightState = new PHLightState();
  	    					lightState.setTransitionTime(0);
  	    					lightState.setX((float) 0.1748);
  	    					lightState.setY((float) 0.3805);
  	    				    bridge.updateLightState(light, lightState, listener);
  	    				    }
    				        break;
    				  case 1:
    					  for (PHLight light : allLights) {
	  	    					PHLightState lightState = new PHLightState();
	  	    					lightState.setTransitionTime(0);
	  	    					lightState.setX((float) 0.139);
	  	    					lightState.setY((float) 0.081);
	  	    				    bridge.updateLightState(light, lightState, listener);
	  	    				    }
    				        break;
    				}
    			}
    			//Summer
    			if(selectedOption == 3){
    				switch (alterno) {
    				  case 0:
    					  for (PHLight light : allLights) {
  	    					PHLightState lightState = new PHLightState();
  	    					lightState.setTransitionTime(0);
  	    					lightState.setX((float) 0.8);
  	    					lightState.setY((float) 0.1);
  	    				    bridge.updateLightState(light, lightState, listener);
  	    				    }
    				        break;
    				  case 1:
    					  for (PHLight light : allLights) {
	  	    					PHLightState lightState = new PHLightState();
	  	    					lightState.setTransitionTime(0);
	  	    					lightState.setX((float) 0.4);
	  	    					lightState.setY((float) 0.5);
	  	    				    bridge.updateLightState(light, lightState, listener);
	  	    				    }
    				        break;
    				}
    			}
    			//Winter
    			if(selectedOption == 4){
    				switch (alterno) {
    				  case 0:
    					  for (PHLight light : allLights) {
  	    					PHLightState lightState = new PHLightState();
  	    					lightState.setTransitionTime(0);
  	    					lightState.setX((float) 0.3393);
  	    					lightState.setY((float) 0.3607);
  	    				    bridge.updateLightState(light, lightState, listener);
  	    				    }
    				        break;
    				  case 1:
    					  for (PHLight light : allLights) {
	  	    					PHLightState lightState = new PHLightState();
	  	    					lightState.setTransitionTime(0);
	  	    					lightState.setX((float) 0.2083);
	  	    					lightState.setY((float) 0.2741);
	  	    				    bridge.updateLightState(light, lightState, listener);
	  	    				    }
    				        break;
    				}
    			}
    			//Inferno
    			if(selectedOption == 5){
    				switch (alterno) {
    				  case 0:
    					  for (PHLight light : allLights) {
  	    					PHLightState lightState = new PHLightState();
  	    					lightState.setTransitionTime(0);
  	    					lightState.setX((float) 0.703);
  	    					lightState.setY((float) 0.296);
  	    				    bridge.updateLightState(light, lightState, listener);
  	    				    }
    				        break;
    				  case 1:
    					  for (PHLight light : allLights) {
	  	    					PHLightState lightState = new PHLightState();
	  	    					lightState.setTransitionTime(0);
	  	    					lightState.setX((float) 0.4);
	  	    					lightState.setY((float) 0.5);
	  	    				    bridge.updateLightState(light, lightState, listener);
	  	    				    }
    				        break;
    				}
    			    }
    	        try{
    	            Thread.sleep(timewait);
    	        }catch(InterruptedException e){
    	            System.out.println("got interrupted!");
    	        }
    	        if (alterno == 0){alterno = 1;}else {alterno = 0;}
    		}
    	}
    }

	private void toast(final String msg) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (toast == null) {
					toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
				}
				toast.setText(TAG + ": " + msg);
				toast.show();
			}
		});
	}

	private final PdDispatcher myDispatcher = new PdDispatcher() {
		  @Override
		  public void print(String s) {
		    Log.i("Pd print", s);
		  }
		};

		private final PdListener myListener = new PdListener() {
	  @Override
	  public void receiveMessage(String symbol, Object... args) {
	    Log.i("receiveMessage symbol:", symbol);
	    for (Object arg: args) {
	      Log.i("receiveMessage atom:", arg.toString());
	    }
	  }


	  @Override
	  public void receiveList(final Object... args) {
	      Log.i("receiveList atoms:", args[0].toString() + args[1].toString());
	      /*if(Float.valueOf(args[0].toString()) == -1.0) {
	    	  //play song
	    	  if(waitingForGetTempo.booleanValue()){
	    	  runOnUiThread(new Runnable() {
		  			@Override
		  			public void run() {
					try {
						Thread.sleep((long)(60/tempo * 1000 - 300));  //I deserve to be maimed for this.
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		  			audioPlayback.playSong();
		  			tv.append("\nnow playing");
		  			mediaPlayerStopped = Boolean.FALSE;
		  			}
		  		});
	    	  waitingForGetTempo = false;
	    	  }
	      }
	      else {*/

	  	    runOnUiThread(new Runnable() {
	  			@Override
	  			public void run() {
	  			tempo = Float.valueOf(args[0].toString());
	  			tv = (TextView) findViewById(R.id.textview);
	  			tv.setText("tempo: " + tempo );
	  			PdBase.sendFloat("startAudio", 0);
	  			 new Thread(new Task()).start();
	  			}
	  		});
	     }

	  @Override public void receiveSymbol(String symbol) {
	    Log.i("receiveSymbol", symbol);
	  }

	  @Override public void receiveFloat(final float x) {
	    Log.i("receiveFloat", String.valueOf(x));
	  }

	  @Override public void receiveBang() {
	    Log.i("receiveBang", "bang!");
	  }
	};

	private final ServiceConnection pdConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			pdService = ((PdService.PdBinder)service).getService();
			initPd();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}
	};

	/** OnCreate() implements Buttons to choose a specific scenario.
	 *
	 */
	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		super.onCreate(savedInstanceState);
		setTitle(R.string.app_name);
        setContentView(R.layout.activity_main_2_smartphone);
        PdPreferences.initPreferences(getApplicationContext());
		PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
		bindService(new Intent(this,PdService.class), pdConnection, BIND_AUTO_CREATE);
		audioPlayback = new TravisAudioPlayback(this);

        phHueSDK = PHHueSDK.create();

        Button BPMButton;
        BPMButton = (Button) findViewById(R.id.GetTempo);
        BPMButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	startAudio();
    			waitingForGetTempo = Boolean.TRUE;
    			PdBase.sendFloat("startAudio", 1);
    			checkstop = 0;
            }
        });

        Button stopTempo;
        stopTempo = (Button) findViewById(R.id.StopTempo);
        stopTempo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	mediaPlayerStopped = Boolean.TRUE;
        		checkstop = 1;
        		PdAudio.stopAudio();
        		Thread.currentThread();
        		Thread.interrupted();
            }
        });

        Button button0;
        button0 = (Button) findViewById(R.id.button0);
        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	selectedOption = 0;
            }
        });

        Button button1;
        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	selectedOption = 1;
            }
        });

        Button button2;
        button2 = (Button) findViewById(R.id.button3);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	selectedOption = 2;
            }
        });

        Button button3;
        button3 = (Button) findViewById(R.id.button4);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	selectedOption = 3;
            }
        });

        Button button4;
        button4 = (Button) findViewById(R.id.button5);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	selectedOption = 4;
            }
        });

        Button button5;
        button5 = (Button) findViewById(R.id.button6);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	selectedOption = 5;
            }
        });

	}

	/**
	 * Function devoted to the addition of random lights.
	 */

	 PHLightListener listener = new PHLightListener() {

	        @Override
	        public void onSuccess() {
	        }

	        @Override
	        public void onStateUpdate(Map<String, String> arg0, List<PHHueError> arg1) {
	           Log.w(TAG, "Light has updated");
	        }

	        @Override
	        public void onError(int arg0, String arg1) {}

	        @Override
	        public void onReceivingLightDetails(PHLight arg0) {}

	        @Override
	        public void onReceivingLights(List<PHBridgeResource> arg0) {}

	        @Override
	        public void onSearchComplete() {}
	    };

	@Override
	protected void onDestroy() {
		PHBridge bridge = phHueSDK.getSelectedBridge();
        if (bridge != null) {

            if (phHueSDK.isHeartbeatEnabled(bridge)) {
                phHueSDK.disableHeartbeat(bridge);
            }

            phHueSDK.disconnect(bridge);
        	cleanup();
        	System.exit(0);
        }
        cleanup();
        System.exit(0);
       	super.onDestroy();
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		startAudio();
	}

	/** Instantiating PdCore Libraries
	 *
	 */
	private void initPd() {
		Resources res = getResources();
		File patchFile = null;
		try {
			PdBase.setReceiver(myDispatcher);
			myDispatcher.addListener("tempo", myListener);
			myDispatcher.addListener("testbang", myListener);
			PdBase.subscribe("android");
			File libDir = getFilesDir();
			try {
				IoUtils.extractZipResource(res.openRawResource(R.raw.externals), libDir, true);}
				catch (IOException e) {
					Log.e("Scene Player", e.toString());
				}
			PdBase.addToSearchPath(libDir.getAbsolutePath());
			InputStream in = res.openRawResource(R.raw.androidbeatdetection);
			patchFile = IoUtils.extractResource(in, "androidbeatdetection.pd", getCacheDir());
			PdBase.openPatch(patchFile);
			startAudio();
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			finish();
		} finally {
			if (patchFile != null) patchFile.delete();
		}
	}

	/**
	 * Starting audio listening using PdCore libs.
	 */
	private void startAudio() {
		String name = getResources().getString(R.string.app_name);
		try {
			pdService.initAudio(-1, -1, -1, -1);
			pdService.startAudio(new Intent(this, AtmosphereActivity.class), R.drawable.icon, name, "Return to " + name + ".");
		} catch (IOException e) {
			toast(e.toString());
		}
	}

	private void cleanup(){
		PdBase.release();
			try {
			unbindService(pdConnection);
		} catch (IllegalArgumentException e) {
			pdService = null;
		}
			audioPlayback.stopSong();

	}
}
