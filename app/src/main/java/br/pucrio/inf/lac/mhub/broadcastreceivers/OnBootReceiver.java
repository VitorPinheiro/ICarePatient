package br.pucrio.inf.lac.mhub.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import br.pucrio.inf.lac.mhub.components.AppUtils;
import br.pucrio.inf.lac.mhub.services.S2PAService;

/**
 * The On Boot Broadcast Receiver to start the service if the phone/tablet
 * is turned off, with the no need to open the application to start the service.
 * 
 * @author Luis Talavera
 *
 */
public class OnBootReceiver extends BroadcastReceiver {
	/** DEBUG */
	private final static String TAG = OnBootReceiver.class.getName();

	@Override
	public void onReceive(Context context, Intent intent) {
        // get the action
        String action = intent.getAction();

		if( action.equals( "android.intent.action.BOOT_COMPLETED" ) ) {
			if( AppUtils.getCurrentStartAtBoot( context ) ) {
				Intent pushIntent = new Intent( context, S2PAService.class );
				context.startService( pushIntent );
				
				AppUtils.logger( 'i', TAG, "/> OnBootReceiver >> Service Started" ); 
			} else {
				AppUtils.logger( 'i', TAG, "/> OnBootReceiver >> Service not Started" ); 
			}
		}
	}
}
