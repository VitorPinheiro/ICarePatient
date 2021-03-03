package br.pucrio.inf.lac.mhub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.test.ServiceTestCase;

import java.util.concurrent.Semaphore;

import br.pucrio.inf.lac.mhub.broadcastreceivers.BroadcastMessage;
import br.pucrio.inf.lac.mhub.components.AppUtils;
import br.pucrio.inf.lac.mhub.components.MOUUID;
import br.pucrio.inf.lac.mhub.models.locals.SensorData;
import br.pucrio.inf.lac.mhub.services.AdaptationService;
import br.pucrio.inf.lac.mhub.services.ConnectionService;
import br.pucrio.inf.lac.mhub.services.LocationService;
import br.pucrio.inf.lac.mhub.services.MEPAService;
import br.pucrio.inf.lac.mhub.services.S2PAService;

/**
 * Created by luis on 18/03/15.
 * Tests for the location service
 */
public class S2PAServiceTest extends ServiceTestCase<S2PAService> {
    /** DEBUG */
    private final static String TAG = S2PAServiceTest.class.getSimpleName();

    /** The context object */
    private Context ac;

    /** The Local Broadcast Manager */
    private LocalBroadcastManager lbm;

    /** Mock Data */
    private final static Double rssi = 50.0;
    private final static MOUUID mouuid = new MOUUID( 1, "test" );
    private final static String sensorName = "Temperature";
    private final static Double[] sensorValue = { 10.00, 11.00 };

    public S2PAServiceTest() {
        super(S2PAService.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        AppUtils.logger( 'd', TAG, "Unit Tests" );
        // get the context
        ac = getContext();
        // get local broadcast
        lbm = LocalBroadcastManager.getInstance( ac );
        Intent iS2PA = new Intent( ac, S2PAService.class );
        startService( iS2PA );
    }

    /**
     * Register/Unregister the broadcast receiver.
     */
    /*private void registerBroadcasts(BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter();
        filter.addAction( BroadcastMessage.ACTION_NEW_SENSOR_MSG );
        lbm.registerReceiver( receiver, filter );
    }*/

    /**
     * Basic test for the startup of the Service
     * @throws Exception
     */
    /*public void test() throws Exception {
        assertNotNull( lbm );
        assertNotNull( getService() );

        assertTrue( AppUtils.isMyServiceRunning( ac, ConnectionService.class.getName() ) );
        assertTrue( AppUtils.isMyServiceRunning( ac, AdaptationService.class.getName() ) );
        assertTrue( AppUtils.isMyServiceRunning( ac, LocationService.class.getName() ) );
        assertTrue( AppUtils.isMyServiceRunning( ac, MEPAService.class.getName() ) );
    }*/

    /**
     * Tests whenever a M-OBJ is found by any technology
     * @throws Exception
     */
    /*public void testFoundMOBJ() throws Exception {
        final SensorData[] response = new SensorData[1];
        final Semaphore semaphore = new Semaphore( 0 );

        BroadcastReceiver mTestReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {
                String action = i.getAction();

                if( action.equals( BroadcastMessage.ACTION_NEW_SENSOR_MSG ) ) {
                    response[0] = (SensorData) i.getSerializableExtra( BroadcastMessage.EXTRA_NEW_SENSOR_MSG );
                    semaphore.release();
                }
            }
        };
        registerBroadcasts( mTestReceiver );

        getService().onMObjectFound( mouuid, rssi );
        semaphore.acquire();

        assertNotNull( response[0] );
        assertEquals( SensorData.FOUND, response[0].getAction() );
        assertEquals( mouuid.toString(), response[0].getMouuid() );
        assertEquals( rssi, response[0].getSignal() );
    }*/

    /**
     * Test whenever a M-OBJ connects to the M-Hub
     * @throws Exception
     */
    /*public void testConnectedMOBJ() throws Exception {
        final SensorData[] response = new SensorData[1];
        final Semaphore semaphore = new Semaphore( 0 );

        BroadcastReceiver mTestReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {
                String action = i.getAction();

                if( action.equals( BroadcastMessage.ACTION_NEW_SENSOR_MSG ) ) {
                    response[0] = (SensorData) i.getSerializableExtra( BroadcastMessage.EXTRA_NEW_SENSOR_MSG );
                    semaphore.release();
                }
            }
        };
        registerBroadcasts( mTestReceiver );

        getService().onMObjectConnected( mouuid );
        semaphore.acquire();

        assertNotNull( response[0] );
        assertEquals( SensorData.CONNECTED, response[0].getAction() );
        assertEquals( mouuid.toString(), response[0].getMouuid() );
    }*/

    /**
     * Test the disconnection of M-OBJs
     * @throws Exception
     */
    /*public void testDisconnectedMOBJ() throws Exception {
        final SensorData[] response = new SensorData[1];
        final Semaphore semaphore = new Semaphore( 0 );

        BroadcastReceiver mTestReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {
                String action = i.getAction();

                if( action.equals( BroadcastMessage.ACTION_NEW_SENSOR_MSG ) ) {
                    response[0] = (SensorData) i.getSerializableExtra( BroadcastMessage.EXTRA_NEW_SENSOR_MSG );
                    semaphore.release();
                }
            }
        };
        registerBroadcasts( mTestReceiver );

        getService().onMObjectDisconnected( mouuid, null );
        semaphore.acquire();

        assertNotNull( response[0] );
        assertEquals( SensorData.DISCONNECTED, response[0].getAction() );
        assertEquals( mouuid.toString(), response[0].getMouuid() );
    }*/

    /**
     * Test whenever a value is read from a M-OBJ
     * @throws Exception
     */
    /*public void testValueReadMOBJ() throws Exception {
        final SensorData[] response = new SensorData[1];
        final Semaphore semaphore = new Semaphore( 0 );

        BroadcastReceiver mTestReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {
                String action = i.getAction();

                if( action.equals( BroadcastMessage.ACTION_NEW_SENSOR_MSG ) ) {
                    response[0] = (SensorData) i.getSerializableExtra( BroadcastMessage.EXTRA_NEW_SENSOR_MSG );
                    semaphore.release();
                }
            }
        };
        registerBroadcasts( mTestReceiver );

        getService().onMObjectValueRead( mouuid, rssi, sensorName, sensorValue );
        semaphore.acquire();

        assertNotNull( response[0] );
        assertEquals( SensorData.READ, response[0].getAction() );
        assertEquals( mouuid.toString(), response[0].getMouuid() );
        assertEquals( sensorName, response[0].getSensorName() );
        assertEquals( sensorValue, response[0].getSensorValue() );
    }*/

    /**
     * Tests the change of interval time for the scans
     * @throws Exception
     */
    public void testChangeInterval() throws Exception {
        final Integer newScan = 50000;
        final Boolean energy = AppUtils.getCurrentEnergyManager( ac );
        final Integer current = AppUtils.getCurrentScanInterval( ac );

        AppUtils.saveEnergyManager( ac, true );

        Intent i = new Intent( BroadcastMessage.ACTION_CHANGE_SCAN_INTERVAL );
        i.putExtra( BroadcastMessage.EXTRA_CHANGE_SCAN_INTERVAL, newScan );
        lbm.sendBroadcast( i );

        // we wait a second for the s2pa service
        try {
            Thread.sleep( 1500 );
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        assertEquals( newScan, AppUtils.getCurrentScanInterval( ac ) );

        AppUtils.saveEnergyManager( ac, energy );
        AppUtils.saveCurrentScanInterval(ac, current);
    }
}
