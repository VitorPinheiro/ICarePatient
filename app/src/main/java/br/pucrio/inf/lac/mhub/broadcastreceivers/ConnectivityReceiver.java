package br.pucrio.inf.lac.mhub.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;

import br.pucrio.inf.lac.mhub.components.AppUtils;

/**
 * The connectivity receiver, it will monitor the device connection.
 * @author Luis Talavera
 */
public class ConnectivityReceiver extends BroadcastReceiver {
	/** DEBUG */
	private static final String TAG = ConnectivityReceiver.class.getName();

    @Override
	public void onReceive(Context c, Intent i) {
		// get local broadcast
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(c);
		// check for the network status
		ConnectivityManager cm = (ConnectivityManager)c.getSystemService( Context.CONNECTIVITY_SERVICE );
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

		if( activeNetwork != null ) {
			Intent iConn = new Intent( BroadcastMessage.ACTION_CONNECTIVITY_CHANGED );
		    boolean isConnected = activeNetwork.isConnected();
		    boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
		    boolean is3G   = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
		    
		    if( isConnected && isWiFi ) {
		    	iConn.putExtra( BroadcastMessage.EXTRA_CONNECTIVITY_CHANGED, BroadcastMessage.INFO_CONNECTIVITY_WIFI );
		    	AppUtils.logger( 'i', TAG, ">> ConnectivityReceiver >> WiFi" );
		    }
		    else if( isConnected && is3G ) {
		    	iConn.putExtra( BroadcastMessage.EXTRA_CONNECTIVITY_CHANGED, BroadcastMessage.INFO_CONNECTIVITY_3G );
		    	AppUtils.logger( 'i', TAG, ">> ConnectivityReceiver >> 3G" );
		    }
		    else if( !isConnected ) {
		    	iConn.putExtra( BroadcastMessage.EXTRA_CONNECTIVITY_CHANGED, BroadcastMessage.INFO_CONNECTIVITY_NO_CONNECTION );
		    	AppUtils.logger( 'i', TAG, ">> ConnectivityReceiver >> Not Connected" );
		    }

		    lbm.sendBroadcast( iConn );
		}
	}
}
