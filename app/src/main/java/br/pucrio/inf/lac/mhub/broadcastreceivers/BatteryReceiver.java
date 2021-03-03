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
 * It will check for the battery status, this broadcast receiver is set inside
 * the s2pa service with an AlarmManager. The modifications are sent through a broadcast
 * 
 * @author Luis Talavera
 */
public class BatteryReceiver extends BroadcastReceiver {
	/** DEBUG */
	private final static String TAG = BatteryReceiver.class.getName();

    @Override
	public void onReceive(Context c, Intent i) {
		// get local broadcast
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance( c );
		// get the action 
		String action = i.getAction();
		
		/* Broadcast: ACTION_CHECK_BATTERY_LEVEL */
		/* ************************************* */
		if( action.equals( BroadcastMessage.ACTION_CHECK_BATTERY_LEVEL ) ) {
			AppUtils.logger( 'i', TAG, ">> ACTION_CHECK_BATTERY_LEVEL" );

			// check for the battery life 
			IntentFilter battFilter = new IntentFilter( Intent.ACTION_BATTERY_CHANGED );
			Intent iBatt = c.getApplicationContext().registerReceiver( null, battFilter );

            // No battery present
            if( iBatt == null ) {
                AppUtils.logger( 'e', TAG, "No Battery Present" );
                return;
            }

            int level       = iBatt.getIntExtra( BatteryManager.EXTRA_LEVEL, -1 );
			float scale     = iBatt.getIntExtra( BatteryManager.EXTRA_SCALE, -1 );
			int battPercent = (int) ( ( level / scale ) * 100 );
			
			// get the current value from the location and connection services 
			Integer locCurrent  = AppUtils.getCurrentLocationInterval( c );
            Integer connCurrent = AppUtils.getCurrentSendMessagesInterval( c );
            Integer scanCurrent = AppUtils.getCurrentScanInterval( c );
			
			// get all the defaults from the SPREF 
			int locHigh   = AppUtils.getLocationInterval( c, AppConfig.SPREF_LOCATION_INTERVAL_HIGH );
			int locMedium = AppUtils.getLocationInterval( c, AppConfig.SPREF_LOCATION_INTERVAL_MEDIUM );
			int locLow    = AppUtils.getLocationInterval( c, AppConfig.SPREF_LOCATION_INTERVAL_LOW );
			
			int connHigh   = AppUtils.getSendSignalsInterval( c, AppConfig.SPREF_MESSAGES_INTERVAL_HIGH );
			int connMedium = AppUtils.getSendSignalsInterval( c, AppConfig.SPREF_MESSAGES_INTERVAL_MEDIUM );
			int connLow    = AppUtils.getSendSignalsInterval( c, AppConfig.SPREF_MESSAGES_INTERVAL_LOW );
			
			int scanHigh   = AppUtils.getScanInterval( c, AppConfig.SPREF_SCAN_INTERVAL_HIGH );
			int scanMedium = AppUtils.getScanInterval( c, AppConfig.SPREF_SCAN_INTERVAL_MEDIUM );
			int scanLow    = AppUtils.getScanInterval( c, AppConfig.SPREF_SCAN_INTERVAL_LOW );
			
			/* - - - HIGH - - - */
			if( battPercent >= AppConfig.DEFAULT_HIGH_VALUE ) {
				AppUtils.logger( 'i', TAG, ">> BatteryReceiver >> Battery HIGH" );
				
				// if location is not HIGH or connection is not HIGH or scan is not HIGH, change to HIGH 
				if( ( locCurrent  != null && locCurrent  != locHigh  ) ||
                    ( connCurrent != null && connCurrent != connHigh ) ||
                    ( scanCurrent != null && scanCurrent != scanHigh ) ) {

					Intent iLocHigh = new Intent( BroadcastMessage.ACTION_CHANGE_LOCATION_INTERVAL );
					iLocHigh.putExtra( BroadcastMessage.EXTRA_CHANGE_LOCATION_INTERVAL, locHigh );
					lbm.sendBroadcast( iLocHigh );
					
					Intent iConnHigh = new Intent( BroadcastMessage.ACTION_CHANGE_MESSAGES_INTERVAL );
					iConnHigh.putExtra( BroadcastMessage.EXTRA_CHANGE_MESSAGES_INTERVAL, connHigh );
					lbm.sendBroadcast( iConnHigh );
					
					Intent iScanHigh = new Intent( BroadcastMessage.ACTION_CHANGE_SCAN_INTERVAL );
					iScanHigh.putExtra( BroadcastMessage.EXTRA_CHANGE_SCAN_INTERVAL, scanHigh );
					lbm.sendBroadcast( iScanHigh );
				}
			}
			
			/* - - - MEDIUM - - - */
			else if( battPercent < AppConfig.DEFAULT_HIGH_VALUE && battPercent >= AppConfig.DEFAULT_MEDIUM_VALUE ) {
				AppUtils.logger( 'i', TAG, ">> BatteryReceiver >> Battery MEDIUM" );
				
				// if location is not MEDIUM or connection is not MEDIUM or scan is not MEDIUM, change to MEDIUM 
				if( ( locCurrent  != null && locCurrent  != locMedium  ) ||
                    ( connCurrent != null && connCurrent != connMedium ) ||
                    ( scanCurrent != null && scanCurrent != scanMedium ) ) {

					Intent iLocMedium = new Intent( BroadcastMessage.ACTION_CHANGE_LOCATION_INTERVAL );
					iLocMedium.putExtra( BroadcastMessage.EXTRA_CHANGE_LOCATION_INTERVAL, locMedium );
					lbm.sendBroadcast( iLocMedium );
					
					Intent iConnMedium = new Intent( BroadcastMessage.ACTION_CHANGE_MESSAGES_INTERVAL );
					iConnMedium.putExtra( BroadcastMessage.EXTRA_CHANGE_MESSAGES_INTERVAL, connMedium );
					lbm.sendBroadcast( iConnMedium );
					
					Intent iScanMedium = new Intent( BroadcastMessage.ACTION_CHANGE_SCAN_INTERVAL );
					iScanMedium.putExtra( BroadcastMessage.EXTRA_CHANGE_SCAN_INTERVAL, scanMedium );
					lbm.sendBroadcast( iScanMedium );
				}
			}
			
			/* - - - LOW - - - */
			else if( battPercent < AppConfig.DEFAULT_MEDIUM_VALUE ) {
				AppUtils.logger( 'i', TAG, ">> BatteryReceiver >> Battery LOW" );
				
				// if location is not LOW or connection is not LOW or scan is not LOW, change to LOW 
				if( ( locCurrent  != null && locCurrent  != locLow  ) ||
                    ( connCurrent != null && connCurrent != connLow ) ||
                    ( scanCurrent != null && scanCurrent != scanLow ) ) {

					Intent iLocLow = new Intent( BroadcastMessage.ACTION_CHANGE_LOCATION_INTERVAL );
					iLocLow.putExtra( BroadcastMessage.EXTRA_CHANGE_LOCATION_INTERVAL, locLow );
					lbm.sendBroadcast( iLocLow );
					
					Intent iConnLow = new Intent( BroadcastMessage.ACTION_CHANGE_MESSAGES_INTERVAL );
					iConnLow.putExtra( BroadcastMessage.EXTRA_CHANGE_MESSAGES_INTERVAL, connLow );
					lbm.sendBroadcast( iConnLow );
					
					Intent iScanLow = new Intent( BroadcastMessage.ACTION_CHANGE_SCAN_INTERVAL );
					iScanLow.putExtra( BroadcastMessage.EXTRA_CHANGE_SCAN_INTERVAL, scanLow );
					lbm.sendBroadcast( iScanLow );
				}
			}
		}
	}
}
