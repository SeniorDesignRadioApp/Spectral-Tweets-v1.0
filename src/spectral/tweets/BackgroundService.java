package spectral.tweets;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class BackgroundService extends Service implements LocationListener
{
	private static Timer repeater = new Timer();
	private static LocationManager lm;
	
	private static final int MIN_TIME_MILLISECONDS = 0;
	private static final int MIN_DIST_METERS = 0;
	private static final int frequency = 30 * 1000;
	private double temp_long = 0.0;
	private double temp_lat = 0.0;
	private int temp_count = 0;
	

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void onCreate()
	{
		super.onCreate();
		lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_MILLISECONDS, MIN_DIST_METERS, this);
		Toast.makeText(getApplicationContext(), "Location display is on", Toast.LENGTH_SHORT).show();
		startService();
	}
	
	public void onDestroy()
	{
		repeater.cancel();
		lm.removeUpdates(this);
		Toast.makeText(getApplicationContext(), "Location display is off", Toast.LENGTH_SHORT).show();
    	temp_lat = 0;
		temp_long = 0;
    	temp_count = 0;
	}
	
	private void startService()
	{
		repeater.scheduleAtFixedRate(new getInfoAndTweet(), 0, frequency);
	}
	
	/* this class will contain all of the GPS and WIFI classes so that none of that stuff clogs up the main thread */
	private class getInfoAndTweet extends TimerTask
	{
		WifiManager wifi;
		BroadcastReceiver receiver;
		
		DecimalFormat lat = new DecimalFormat("00.000000");
		DecimalFormat lon = new DecimalFormat("000.000000");
		String gps_info;
		String wifi_info;
		String final_string;
		private final String hashtag = "#ajd7v-34 ";
		int count = 0;
		
		private class WIFIscanner extends BroadcastReceiver
		{
			
			private final ArrayList<Integer> channel_numbers = new ArrayList<Integer> (Arrays.asList(0, 2412, 2417, 2422, 2427, 2432, 2437, 2442, 2447, 2452, 2457, 2462));
			List <ScanResult> results;
			Map<Integer, String> levels = new HashMap<Integer, String>();
			String empty_channel = "__________";		// 10 spaces
			
			public WIFIscanner()
			{
				init_levels();
			}
			
			public void init_levels()
			{
				levels.put(-20, "1");
				levels.put(-21, "2");
				levels.put(-22, "3");
				levels.put(-23, "4");
				levels.put(-24, "5");
				levels.put(-25, "6");
				levels.put(-26, "7");
				levels.put(-27, "8");
				levels.put(-28, "9");
				levels.put(-29, "a");
				levels.put(-30, "b");
				levels.put(-31, "c");
				levels.put(-32, "d");
				levels.put(-33, "e");
				levels.put(-34, "f");
				levels.put(-35, "g");
				levels.put(-36, "h");
				levels.put(-37, "i");
				levels.put(-38, "j");
				levels.put(-39, "k");
				levels.put(-40, "l");
				levels.put(-41, "m");
				levels.put(-42, "n");
				levels.put(-43, "o");
				levels.put(-44, "p");
				levels.put(-45, "q");
				levels.put(-46, "r");
				levels.put(-47, "s");
				levels.put(-48, "t");
				levels.put(-49, "u");
				levels.put(-50, "v");
				levels.put(-51, "w");
				levels.put(-52, "x");
				levels.put(-53, "y");
				levels.put(-54, "z");
				levels.put(-55, "A");
				levels.put(-56, "B");
				levels.put(-57, "C");
				levels.put(-58, "D");
				levels.put(-59, "E");
				levels.put(-60, "F");
				levels.put(-61, "G");
				levels.put(-62, "H");
				levels.put(-63, "I");
				levels.put(-64, "J");
				levels.put(-65, "K");
				levels.put(-66, "L");
				levels.put(-67, "M");
				levels.put(-68, "N");
				levels.put(-69, "O");
				levels.put(-70, "P");
				levels.put(-71, "Q");
				levels.put(-71, "R");
				levels.put(-73, "S");
				levels.put(-74, "T");
				levels.put(-75, "U");
				levels.put(-76, "V");
				levels.put(-77, "W");
				levels.put(-78, "X");
				levels.put(-79, "Y");
				levels.put(-80, "Z");
			}

			@Override
			public void onReceive(Context context, Intent intent)
			{
				wifi_info = "";
				results = wifi.getScanResults();
				ScanResult sr;
				Iterator<ScanResult> it = results.iterator();
				ScanResult channel_info[] = new ScanResult[12];
				
				for (int i = 1; i < 12; i++)
				{
					channel_info[i] = null;
				}
				
				while (it.hasNext())
				{
					sr = it.next();
					int channel = channel_numbers.indexOf(Integer.valueOf(sr.frequency));

					if (channel_info[channel] == null)
					{
						channel_info[channel] = sr;
					}
					else
					{
						if (channel_info[channel].level < sr.level)
						{
							channel_info[channel] = sr;
						}
					}
				}
				
				for (int i = 1; i < 12; i++)
				{
					if (channel_info[i] != null)
					{
						wifi_info += (levels.get(channel_info[i].level) == null ? "0" : levels.get(channel_info[i].level))  + channel_info[i].BSSID.replace(":", "").substring(2, 11);
					}
					else
					{
						wifi_info += empty_channel;
					}
				}
				
				final_string = hashtag + wifi_info + gps_info;
		    	if(Twitter_Test_AppActivity.twitter != null) {
		    		Twitter_Test_AppActivity.twitter.setStatus(final_string);
		    		Twitter_Test_AppActivity.changeText("Auto Tweet Sent: " + count + "\t" + final_string);
		    	} else {
		    		Twitter_Test_AppActivity.changeText("Tweet not sent");
		    	}
			}
			
		}
		
		public void run()
		{
	        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	        if (receiver == null)
	        {
	        	receiver = new WIFIscanner();
	        }
	        registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	        
	    	String latitude = lat.format(temp_lat / temp_count).replace(".",  "");
	    	String longitude = lon.format(temp_long / temp_count).replace(".",  "");
	    	gps_info = ((temp_lat / temp_count) > 0 ? "+" : "") + latitude + ((temp_long / temp_count) > 0 ? "+" : "") + longitude;
	    	
	    	wifi.startScan();
	    	count++;
		}
	}

	public void onLocationChanged(Location location) {
		temp_lat += location.getLatitude();
		temp_long += location.getLongitude();
		temp_count ++;
	}

	public void onProviderDisabled(String provider) {
		Toast.makeText(getApplicationContext(), "GPS disabled", Toast.LENGTH_SHORT).show();
		
	}

	public void onProviderEnabled(String provider) {
		Toast.makeText(getApplicationContext(), "GPS enabled", Toast.LENGTH_SHORT).show();
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {}

}
