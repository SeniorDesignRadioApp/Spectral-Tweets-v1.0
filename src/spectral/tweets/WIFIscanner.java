package spectral.tweets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;

public class WIFIscanner extends BroadcastReceiver {

	Twitter_Test_AppActivity main;
	private final static ArrayList<Integer> channel_numbers = new ArrayList<Integer> (Arrays.asList(0, 2412, 2417, 2422, 2427, 2432, 2437, 2442, 2447, 2452, 2457, 2462));
	List <ScanResult> results;
	Map<Integer, String> levels = new HashMap<Integer, String>();
	String empty_channel = "__________";		// 10 spaces
	
	public WIFIscanner(Twitter_Test_AppActivity main)
	{
		super();
		this.main = main;
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
		results = main.wifi.getScanResults();
		String str = "";
		String tmp = "";
		ScanResult sr;
		Iterator<ScanResult> it = results.iterator();
		ScanResult channel_info[] = new ScanResult[12];
		
		for (int i = 1; i < 12; i++)
		{
			channel_info[i] = null;
		}
		
//		str += "all channels\n";
		
		while (it.hasNext())
		{
			sr = it.next();
//			str += channel_numbers.indexOf(Integer.valueOf(sr.frequency)) + " " + sr.level + " " + sr.BSSID + "\n";
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
		
//		str += "\nfinal results\n";
		
		for (int i = 1; i < 12; i++)
		{
//			str += "channel " + i + "\n";
			if (channel_info[i] != null)
			{
//				tmp = channel_numbers.indexOf(Integer.valueOf(channel_info[i].frequency)) + " " + channel_info[i].level + " " + channel_info[i].BSSID.replace(":", "").substring(3, 11) + "\n";
				tmp = (levels.get(channel_info[i].level) == null ? "0" : levels.get(channel_info[i].level))  + channel_info[i].BSSID.replace(":", "").substring(2, 11);
				str += tmp;
			}
			else
			{
//				tmp = "no info\n";
				tmp = empty_channel;
				str += tmp;
			}
		}
		
		/* set the flag so GPSscanner knows it can update the display */
		Twitter_Test_AppActivity.wifi_info = str;
		Twitter_Test_AppActivity.ready_flag = true;
	}
}