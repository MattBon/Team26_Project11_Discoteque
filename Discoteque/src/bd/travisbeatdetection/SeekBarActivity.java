package bd.travisbeatdetection;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.util.Log;
import android.widget.SeekBar;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import bd.travisbeatdetection.R;

/** 
 * SeekBarActivity - Class used to create a Seek Bar that allows the user to choose a static color for all lights. 
 * Colors' scale is defined from 0 to 65534. Seek Bar is intended to work progressively, so that colors
 * are changed accordingly to the value indicated on the spectrum.
 * 
 * @authors Mattia Bonomi, Federico Zanetti
 * 
 */
public class SeekBarActivity extends Activity {

	/** 
	 * Declaring Hue Lights SDK.
	 * 
	 */
	private PHHueSDK phHueSDK;
    private static final int MAX_HUE=65535;
    public int checkstop = 1;
	private static final String TAG = "Discoteque";
		    	
	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {	
		
		super.onCreate(savedInstanceState);
		setTitle(R.string.app_name);
        setContentView(R.layout.activity_main_3);

        phHueSDK = PHHueSDK.create();
		PHBridge bridge = phHueSDK.getSelectedBridge();
		List<PHLight> allLights = bridge.getResourceCache().getAllLights();
		for(PHLight light : allLights){
			PHLightState lightState = new PHLightState();	  	
			lightState.setTransitionTime(0);
			lightState.setX((float) 0.703);
			lightState.setY((float) 0.296);
			   bridge.updateLightState(light, lightState, listener);
			}
        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar1); 
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){ 

        @Override 
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { 
   
        	PHBridge bridge = phHueSDK.getSelectedBridge();
        	List<PHLight> allLights = bridge.getResourceCache().getAllLights();	
        	for (PHLight light : allLights) {
        		PHLightState lightState = new PHLightState();   
        		lightState.setTransitionTime(0);
        		lightState.setHue(progress);         
        		bridge.updateLightState(light, lightState, listener);	
        		}   
    
        };

        @Override 
        public void onStartTrackingTouch(SeekBar seekBar) { 
     
        	} 

        @Override 
        public void onStopTrackingTouch(SeekBar seekBar) { 
     
        	} 
        });
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
        //System.exit(0);
		super.onDestroy();
		
    }
}

