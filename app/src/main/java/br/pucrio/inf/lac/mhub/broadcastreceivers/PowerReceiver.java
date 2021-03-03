package br.pucrio.inf.lac.mhub.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.v4.content.LocalBroadcastManager;

import br.pucrio.inf.lac.mhub.components.AppConfig;
import br.pucrio.inf.lac.mhub.components.AppUtils;

/**
 * The Power Broadcast Receiver, it will monitor when the device is connected
 * to an external power source or disconnected. This connection and
 * disconnection will determine the rate for the interval updates.
 * @author Luis Talavera
 */
public class PowerReceiver extends BroadcastReceiver {
	/** DEBUG */
	private static final String TAG = PowerReceiver.class.getName();

    @Override
	public void onReceive(Context c, Intent i) {
		// get local broadcast
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(c);
		// declare the intents location and connection
		Intent iLoc  = new Intent( BroadcastMessage.ACTION_CHANGE_LOCATION_INTERVAL )
		     , iConn = new Intent( BroadcastMessage.ACTION_CHANGE_MESSAGES_INTERVAL )
		     , iScan = new Intent( BroadcastMessage.ACTION_CHANGE_SCAN_INTERVAL );
		// get the action
		String action = i.getAction();
		
		/* The device is connected to an external power source */
		/* *************************************************** */
		if( action.equals( "android.intent.action.ACTION_POWER_CONNECTED" ) ) {
			AppUtils.logger( 'i', TAG, ">> ACTION_POWER_CONNECTED" );
			// changing the location interval
			// get the HIGH value from the location SPREF
			Integer locHigh = AppUtils.getLocationInterval( c, AppConfig.SPREF_LOCATION_INTERVAL_HIGH );
			iLoc.putExtra( BroadcastMessage.EXTRA_CHANGE_LOCATION_INTERVAL, locHigh );
			
			// changing the connection interval
			// get the HIGH value from the connection SPREF
			Integer connHigh = AppUtils.getSendSignalsInterval( c, AppConfig.SPREF_MESSAGES_INTERVAL_HIGH );
			iConn.putExtra( BroadcastMessage.EXTRA_CHANGE_MESSAGES_INTERVAL, connHigh );
			
			// changing the scan interval
			// get the HIGH value from the scan SPREF
			Integer scanHigh = AppUtils.getScanInterval( c, AppConfig.SPREF_SCAN_INTERVAL_HIGH );
			iScan.putExtra( BroadcastMessage.EXTRA_CHANGE_SCAN_INTERVAL, scanHigh );
		}
		
		/* The device is not connected to an external power source */
		/* ******************************************************* */
		if( action.equals( "android.intent.action.ACTION_POWER_DISCONNECTED" ) ) {
			AppUtils.logger( 'i', TAG, ">> PowerReceiver >> ACTION_POWER_DISCONNECTED" );
			// check for the battery life
			IntentFilter battFilter = new IntentFilter( Intent.ACTION_BATTERY_CHANGED );
			Intent iBatt = c.registerReceiver( null, battFilter );

            // No battery present
            if( iBatt == null ) {
                AppUtils.logger( 'e', TAG, "No Battery Present");
                return;
            }

			int level       = iBatt.getIntExtra( BatteryManager.EXTRA_LEVEL, -1 );
			float scale     = iBatt.getIntExtra( BatteryManager.EXTRA_SCALE, -1 );
			int battPercent = (int) ( ( level / scale ) * 100 );

			/* - - - HIGH - - - */
			if( battPercent >= AppConfig.DEFAULT_HIGH_VALUE ) {
				// get the HIGH value from the location SPREF
				Integer locHigh = AppUtils.getLocationInterval( c, AppConfig.SPREF_LOCATION_INTERVAL_HIGH );
				iLoc.putExtra( BroadcastMessage.EXTRA_CHANGE_LOCATION_INTERVAL, locHigh );
				
				// get the HIGH value from the connection SPREF
				Integer connHigh = AppUtils.getSendSignalsInterval( c, AppConfig.SPREF_MESSAGES_INTERVAL_HIGH );
				iConn.putExtra( BroadcastMessage.EXTRA_CHANGE_MESSAGES_INTERVAL, connHigh );
				
				// get the HIGH value from the scan SPREF
				Integer scanHigh = AppUtils.getScanInterval( c, AppConfig.SPREF_SCAN_INTERVAL_HIGH );
				iScan.putExtra( BroadcastMessage.EXTRA_CHANGE_SCAN_INTERVAL, scanHigh );
			}
			/* - - - MEDIUM - - - */
			else if( battPercent < AppConfig.DEFAULT_HIGH_VALUE && battPercent >= AppConfig.DEFAULT_MEDIUM_VALUE ) {
				// get the MEDIUM value from the location SPREF
				Integer locMedium = AppUtils.getLocationInterval( c, AppConfig.SPREF_LOCATION_INTERVAL_MEDIUM );
				iLoc.putExtra( BroadcastMessage.EXTRA_CHANGE_LOCATION_INTERVAL, locMedium );
				
				// get the MEDIUM value from the connection SPREF
				Integer connMedium = AppUtils.getSendSignalsInterval( c, AppConfig.SPREF_MESSAGES_INTERVAL_MEDIUM );
				iConn.putExtra( BroadcastMessage.EXTRA_CHANGE_MESSAGES_INTERVAL, connMedium );
				
				// get the MEDIUM value from the scan SPREF
				Integer scanMedium = AppUtils.getScanInterval( c, AppConfig.SPREF_SCAN_INTERVAL_MEDIUM );
				iScan.putExtra( BroadcastMessage.EXTRA_CHANGE_SCAN_INTERVAL, scanMedium );
			}
			/* - - - LOW - - - */
			else if( battPercent < AppConfig.DEFAULT_MEDIUM_VALUE ) {
				// get the LOW value from the location SPREF
				Integer locLow = AppUtils.getLocationInterval( c, AppConfig.SPREF_LOCATION_INTERVAL_LOW );
				iLoc.putExtra( BroadcastMessage.EXTRA_CHANGE_LOCATION_INTERVAL, locLow );
				
				// get the LOW value from the connection SPREF
				Integer connLow = AppUtils.getSendSignalsInterval( c, AppConfig.SPREF_MESSAGES_INTERVAL_LOW );
				iConn.putExtra( BroadcastMessage.EXTRA_CHANGE_MESSAGES_INTERVAL, connLow );
				
				// get the LOW value from the scan SPREF
				Integer scanLow = AppUtils.getScanInterval( c, AppConfig.SPREF_SCAN_INTERVAL_LOW );
				iScan.putExtra( BroadcastMessage.EXTRA_CHANGE_SCAN_INTERVAL, scanLow );					
			}
		}
		
		// send three broadcasts
		lbm.sendBroadcast( iLoc );
		lbm.sendBroadcast( iConn );
		lbm.sendBroadcast( iScan );
	}
}
