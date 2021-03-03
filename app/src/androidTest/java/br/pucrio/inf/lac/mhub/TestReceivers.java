package br.pucrio.inf.lac.mhub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.test.AndroidTestCase;

import java.util.concurrent.Semaphore;

import br.pucrio.inf.lac.mhub.broadcastreceivers.BatteryReceiver;
import br.pucrio.inf.lac.mhub.broadcastreceivers.BroadcastMessage;
import br.pucrio.inf.lac.mhub.broadcastreceivers.OnBootReceiver;
import br.pucrio.inf.lac.mhub.broadcastreceivers.PowerReceiver;
import br.pucrio.inf.lac.mhub.components.AppConfig;
import br.pucrio.inf.lac.mhub.components.AppUtils;
import br.pucrio.inf.lac.mhub.services.S2PAService;

/**
 * Created by luis on 17/06/15.
 * Test for the broadcast receivers
 */
public class TestReceivers extends AndroidTestCase {
    /** DEBUG */
    private final static String TAG = TestReceivers.class.getSimpleName();

    /** The context object */
    private Context ac;

    /** Verifies connection by cable */
    private PowerReceiver mPReceiver;

    /** Verifies battery level */
    private BatteryReceiver mBReceiver;

    /** Starts the S2PA at boot */
    private OnBootReceiver mOBReceiver;

    /** The Local Broadcast Manager */
    private LocalBroadcastManager lbm;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        AppUtils.logger( 'd', TAG, "Unit Tests" );
        // get the context
        ac = getContext();
        // get local broadcast
        lbm = LocalBroadcastManager.getInstance( ac );
        // creates the receivers
        mPReceiver  = new PowerReceiver();
        mBReceiver  = new BatteryReceiver();
        mOBReceiver = new OnBootReceiver();
    }

    /**
     * Register/Unregister the broadcast receiver.
     */
    private void registerBroadcasts(BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter();
        filter.addAction( BroadcastMessage.ACTION_CHANGE_LOCATION_INTERVAL );
        filter.addAction( BroadcastMessage.ACTION_CHANGE_MESSAGES_INTERVAL );
        filter.addAction( BroadcastMessage.ACTION_CHANGE_SCAN_INTERVAL );
        lbm.registerReceiver( receiver, filter );
    }

    /**
     * Test the power receiver for energy connected and disconnected
     * @throws Exception
     */
    public void testPowerReceiver() throws Exception {
        Intent iConn = new Intent( Intent.ACTION_POWER_CONNECTED );
        Intent iDisc = new Intent( Intent.ACTION_POWER_DISCONNECTED );

        Integer locHigh = AppUtils.getLocationInterval( ac, AppConfig.SPREF_LOCATION_INTERVAL_HIGH);
        Integer connHigh = AppUtils.getSendSignalsInterval( ac, AppConfig.SPREF_MESSAGES_INTERVAL_HIGH );
        Integer scanHigh = AppUtils.getScanInterval( ac, AppConfig.SPREF_SCAN_INTERVAL_HIGH );

        final Integer[] response = new Integer[3];
        final Semaphore semaphore = new Semaphore( 0 );

        BroadcastReceiver mTestReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {
                String action = i.getAction();

                if( action.equals( BroadcastMessage.ACTION_CHANGE_LOCATION_INTERVAL ) )
                    response[0] = i.getIntExtra( BroadcastMessage.EXTRA_CHANGE_LOCATION_INTERVAL, -1 );
                else if( action.equals( BroadcastMessage.ACTION_CHANGE_MESSAGES_INTERVAL ) )
                    response[1] = i.getIntExtra( BroadcastMessage.EXTRA_CHANGE_MESSAGES_INTERVAL, -1 );
                else if( action.equals( BroadcastMessage.ACTION_CHANGE_SCAN_INTERVAL ) )
                    response[2] = i.getIntExtra( BroadcastMessage.EXTRA_CHANGE_SCAN_INTERVAL, -1 );

                if( response[0] != null && response[1] != null && response[2] != null )
                    semaphore.release();
            }
        };
        registerBroadcasts( mTestReceiver );

        mPReceiver.onReceive( ac, iConn );
        semaphore.acquire();

        assertNotNull( response[0] );
        assertNotNull( response[1] );
        assertNotNull( response[2] );
        assertEquals( locHigh, response[0] );
        assertEquals( connHigh, response[1] );
        assertEquals( scanHigh, response[2] );

        mPReceiver.onReceive( mContext, iDisc );
        semaphore.acquire();

        assertNotNull( response[0] );
        assertNotNull( response[1] );
        assertNotNull( response[2] );
    }

    /**
     * Test the battery receiver, to modify the services
     * behavior depending on the current battery level
     * @throws Exception
     */
    public void testBatteryReceiver() throws Exception {
        Intent intent = new Intent( BroadcastMessage.ACTION_CHECK_BATTERY_LEVEL );

        final Integer[] response = new Integer[4];
        final Semaphore semaphore = new Semaphore( 0 );
        response[3] = 0;

        BroadcastReceiver mTestReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {
                String action = i.getAction();

                if( action.equals( BroadcastMessage.ACTION_CHANGE_LOCATION_INTERVAL ) ) {
                    ++response[3];
                    response[0] = i.getIntExtra( BroadcastMessage.EXTRA_CHANGE_LOCATION_INTERVAL, -1 );
                }
                else if( action.equals( BroadcastMessage.ACTION_CHANGE_MESSAGES_INTERVAL ) ) {
                    ++response[3];
                    response[1] = i.getIntExtra( BroadcastMessage.EXTRA_CHANGE_MESSAGES_INTERVAL, -1 );
                }
                else if( action.equals( BroadcastMessage.ACTION_CHANGE_SCAN_INTERVAL ) ) {
                    ++response[3];
                    response[2] = i.getIntExtra( BroadcastMessage.EXTRA_CHANGE_SCAN_INTERVAL, -1 );
                }

                if( response[3] == 3  )
                    semaphore.release();
            }
        };
        registerBroadcasts( mTestReceiver );

        mBReceiver.onReceive( ac, intent );
        semaphore.acquire();

        assertNotNull( response[0] );
        assertNotNull( response[1] );
        assertNotNull( response[2] );

        assertTrue( ( response[0] == AppConfig.DEFAULT_LOCATION_INTERVAL_HIGH ) ||
                    ( response[0] == AppConfig.DEFAULT_LOCATION_INTERVAL_MEDIUM ) ||
                    ( response[0] == AppConfig.DEFAULT_LOCATION_INTERVAL_LOW )
        );

        assertTrue( ( response[1] == AppConfig.DEFAULT_MESSAGES_INTERVAL_HIGH ) ||
                    ( response[1] == AppConfig.DEFAULT_MESSAGES_INTERVAL_MEDIUM ) ||
                    ( response[1] == AppConfig.DEFAULT_MESSAGES_INTERVAL_LOW )
        );

        assertTrue( ( response[2] == AppConfig.DEFAULT_SCAN_INTERVAL_HIGH ) ||
                    ( response[2] == AppConfig.DEFAULT_SCAN_INTERVAL_MEDIUM ) ||
                    ( response[2] == AppConfig.DEFAULT_SCAN_INTERVAL_LOW )
        );
    }

    /**
     * Tests the on boot receiver, to start the s2pa service
     * at the boot of the device
     * @throws Exception
     */
    public void testBootReceiver() throws Exception {
        Intent intent = new Intent( "android.intent.action.BOOT_COMPLETED" );

        boolean atBoot = AppUtils.getCurrentStartAtBoot( ac );
        AppUtils.saveStartAtBoot( ac, true );

        mOBReceiver.onReceive( ac, intent );

        // we wait a second for the s2pa service
        try {
            Thread.sleep( 1500 );
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        assertTrue( AppUtils.isMyServiceRunning( ac, S2PAService.class.getName() ) );

        AppUtils.saveStartAtBoot( ac, atBoot );
    }
}
