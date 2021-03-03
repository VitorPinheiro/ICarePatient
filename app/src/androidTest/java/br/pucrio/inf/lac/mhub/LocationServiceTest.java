package br.pucrio.inf.lac.mhub;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import java.util.concurrent.Semaphore;

import br.pucrio.inf.lac.mhub.broadcastreceivers.BroadcastMessage;
import br.pucrio.inf.lac.mhub.components.AppUtils;
import br.pucrio.inf.lac.mhub.services.LocationService;

/**
 * Created by luis on 18/03/15.
 * Tests for the location service
 */
public class LocationServiceTest extends ServiceTestCase<LocationService> {
    /** DEBUG */
    private final static String TAG = LocationServiceTest.class.getSimpleName();

    /** The context object */
    private Context ac;

    /** The Local Broadcast Manager */
    private LocalBroadcastManager lbm;

    public LocationServiceTest() {
        super(LocationService.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        AppUtils.logger( 'd', TAG, "Unit Tests" );
        // get the context
        ac = getContext();
        // get local broadcast
        lbm = LocalBroadcastManager.getInstance( ac );
        Intent iLoc = new Intent( ac, LocationService.class );
        startService( iLoc );
    }

    /**
     * Basic test for the startup of the Service
     * @throws Exception
     */
    public void test() throws Exception {
        assertNotNull( lbm );
        assertNotNull( getService() );
    }

    /**
     * Tests the change of interval time for the request of
     * time updates
     * @throws Exception
     */
    public void testChangeInterval() throws Exception {
        final Integer newLocation = 50000;
        final Boolean energy = AppUtils.getCurrentEnergyManager( ac );
        final Integer current = AppUtils.getCurrentLocationInterval( ac );

        AppUtils.saveEnergyManager( ac, true );

        Intent i = new Intent( BroadcastMessage.ACTION_CHANGE_LOCATION_INTERVAL );
        i.putExtra( BroadcastMessage.EXTRA_CHANGE_LOCATION_INTERVAL, newLocation );
        lbm.sendBroadcast( i );

        // we wait a second for the location service
        try {
            Thread.sleep( 1500 );
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        assertEquals( newLocation, AppUtils.getCurrentLocationInterval( ac ) );

        AppUtils.saveEnergyManager( ac, energy );
        AppUtils.saveCurrentLocationInterval( ac, current );
    }
}
