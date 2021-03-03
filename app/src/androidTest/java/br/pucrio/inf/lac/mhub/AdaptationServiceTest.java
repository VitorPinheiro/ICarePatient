package br.pucrio.inf.lac.mhub;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.content.LocalBroadcastManager;
import android.test.ServiceTestCase;

import java.util.concurrent.Semaphore;

import br.pucrio.inf.lac.mhub.broadcastreceivers.BroadcastMessage;
import br.pucrio.inf.lac.mhub.components.AppUtils;
import br.pucrio.inf.lac.mhub.s2pa.base.TechnologyDevice;
import br.pucrio.inf.lac.mhub.services.AdaptationService;

/**
 * Created by luis on 18/03/15.
 * Tests for the location service
 */
public class AdaptationServiceTest extends ServiceTestCase<AdaptationService> {
    /** DEBUG */
    private final static String TAG = AdaptationServiceTest.class.getSimpleName();

    /** Semaphore to wait for the result */
    private Semaphore semaphore;

    /** Data received from the adaptation service*/
    private int code;
    private Bundle data;

    /** The Local Broadcast Manager */
    private LocalBroadcastManager lbm;

    public AdaptationServiceTest() {
        super(AdaptationService.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        AppUtils.logger( 'd', TAG, "Unit Tests" );
        // get the context
        Context ac = getContext();
        // get local broadcast
        lbm = LocalBroadcastManager.getInstance( ac );
        // create semaphore
        semaphore = new Semaphore( 0 );
        // start service
        Intent iAdp = new Intent( ac, AdaptationService.class );
        startService( iAdp );
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
     * Test a request for a device structure
     * with one that exists
     * @throws Exception
     */
    public void testCorrectRequestStructure() throws Exception {
        Intent i = new Intent( BroadcastMessage.ACTION_NEW_DEVICE );
        i.putExtra( BroadcastMessage.EXTRA_NEW_DEVICE, "SensorTag" );
        i.putExtra( BroadcastMessage.EXTRA_RESULT_RECEIVER, mTestReceiver );
        lbm.sendBroadcast( i );
        semaphore.acquire();

        assertNotNull( data );
        assertEquals( AdaptationService.SUCCESS, code );
        assertTrue( data.getSerializable( AdaptationService.EXTRA_RESULT ) instanceof TechnologyDevice );
    }

    /**
     * Test a request for a device structure
     * with one that don't exist
     * @throws Exception
     */
    public void testWrongRequestStructure() throws Exception {
         Intent i = new Intent( BroadcastMessage.ACTION_NEW_DEVICE );
        i.putExtra( BroadcastMessage.EXTRA_NEW_DEVICE, "" );
        i.putExtra( BroadcastMessage.EXTRA_RESULT_RECEIVER, mTestReceiver );
        lbm.sendBroadcast( i );
        semaphore.acquire();

        assertNull( data );
        assertEquals( AdaptationService.FAILED, code );
    }

    private ResultReceiver mTestReceiver = new ResultReceiver( new Handler() ) {
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            code = resultCode;
            data = resultData;
            semaphore.release();
        }
    };
}
