package bd.travisbeatdetection;

import java.util.List;
import java.util.Map;
import java.util.Random;

import android.util.Log;
import android.view.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.View.OnClickListener;
import android.widget.Button;
import bd.travisbeatdetection.R;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

/** 
 * HomeDiscotequeActivity - This class is used to give the user the opportunity to choose between three activities:
 * 1. Beat Detection and Lights tweaking accordingly to song's tempo and using random colors.
 * 2. Beat Detection and Lights tweaking accordingly to song's tempo and using user's pre-defined colors.
 * 3. Seek Bar allows to chose between colors and to set the desired static atmosphere.
 * 
 * @authors Mattia Bonomi, Federico Zanetti
 * 
 */
public class HomeDiscotequeActivity extends Activity {
	
	/** 
	 * Declaring Hue Lights SDK
	 * 
	 */
	private static final String TAG = "Discoteque";
	private PHHueSDK phHueSDK;
    private static final int MAX_HUE=65535;
    public int checkstop = 1;
	
    /**
     * OnCreate() implements Buttons to switch between different activities.
     */
	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {	
		
		super.onCreate(savedInstanceState);
		setTitle(R.string.app_name);
        setContentView(R.layout.homediscoteque);
        
        phHueSDK = PHHueSDK.create();
        randomLights();
        
        Button roundID;
		roundID = (Button) findViewById(R.id.roundID);
		roundID.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				randomLights();
			}
		});
		
		Button beat_detection;
        beat_detection = (Button) findViewById(R.id.beat_detection);
        beat_detection.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {            	
            	startFirstActivity();
            }
        });

        Button atmosphere;
        atmosphere = (Button) findViewById(R.id.atmosphere);
        atmosphere.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	startSecondActivity();
            }
        });

         Button seek_bar;
         seek_bar = (Button) findViewById(R.id.seek_bar);
         seek_bar.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View v) {            	
            	startThirdActivity();
             }
         });  
        
        }

	/**
	 * Function devoted to the addition of random lights.
	 */
	 public void randomLights() {
	        PHBridge bridge = phHueSDK.getSelectedBridge();
	        List<PHLight> allLights = bridge.getResourceCache().getAllLights();
	        Random rand = new Random();
	        for (PHLight light : allLights) {
	            PHLightState lightState = new PHLightState();
	            lightState.setTransitionTime(0);
	            lightState.setHue(rand.nextInt(MAX_HUE));
	            bridge.updateLightState(light, lightState, listener);
	            }
	    }
	
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
	    
	/** 
	 * Intent to call first sub-activity. This is done for starting Beat Detection Activity and 
	 * for tweaking lights accordingly with random colors.
	 * */    
	public void startFirstActivity() {   
	        Intent intent = new Intent(getApplicationContext(), BeatDetectionActivity.class);
	        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
	            //intent.addFlags(0x8000); 
	        	startActivity(intent);
	        	}
	
	/** 
	 * Intent to call second sub-activity. This is done for starting Atmosphere Beat Detection Activity and 
	 * for tweaking lights accordingly with pre-defined colors.
	 * */ 	
	public void startSecondActivity() {   
        Intent intent = new Intent(getApplicationContext(), AtmosphereActivity.class);
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            //intent.addFlags(0x8000); 
        	startActivity(intent);
    }
	
	/** 
	 * Intent to call third sub-activity. This is done for starting Seek Bar Activity and 
	 * for setting all lights on the same color chosen within a spectrum.
	 * */ 
	public void startThirdActivity() {   
        Intent intent = new Intent(getApplicationContext(), SeekBarActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            intent.addFlags(0x8000); 
            startActivity(intent);
    }

	@Override
	protected void onDestroy() {
		PHBridge bridge = phHueSDK.getSelectedBridge();
        if (bridge != null) {
            
            if (phHueSDK.isHeartbeatEnabled(bridge)) {
                phHueSDK.disableHeartbeat(bridge);
            }
            phHueSDK.disconnect(bridge);
            super.onDestroy();
        }
       System.exit(0);
       super.onDestroy();
    }

	@Override
	public void finish() {
		super.finish();
	}

}