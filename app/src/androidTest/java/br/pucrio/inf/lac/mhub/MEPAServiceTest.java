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
import br.pucrio.inf.lac.mhub.models.locals.MessageData;
import br.pucrio.inf.lac.mhub.models.locals.EventData;
import br.pucrio.inf.lac.mhub.models.base.LocalMessage;
import br.pucrio.inf.lac.mhub.models.base.QueryMessage;
import br.pucrio.inf.lac.mhub.models.locals.SensorData;
import br.pucrio.inf.lac.mhub.services.MEPAService;

/**
 * Created by luis on 18/03/15.
 * Tests for the MEPA service
 */
public class MEPAServiceTest extends ServiceTestCase<MEPAService> {
    /** DEBUG */
    private final static String TAG = MEPAServiceTest.class.getSimpleName();

    /** The Local Broadcast Manager */
    private LocalBroadcastManager lbm;

    public MEPAServiceTest() {
        super(MEPAService.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        AppUtils.logger( 'd', TAG, "Unit Tests" );
        // get the context
        Context ac = getContext();
        // get local broadcast
        lbm = LocalBroadcastManager.getInstance( ac );
        // start the service
        Intent iMEPA = new Intent( ac, MEPAService.class );
        startService( iMEPA );
    }

    /**
     * Register/Unregister the broadcast receiver.
     */
    /*private void registerBroadcasts(BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter();
        filter.addAction( BroadcastMessage.ACTION_NEW_ERROR_MSG );
        filter.addAction( BroadcastMessage.ACTION_NEW_EVENT_MSG );
        lbm.registerReceiver( receiver, filter );
    }*/

    /**
     * Clears all the CEP Rules from the MEPA Service
     */
    /*private void clearMEPAService() {
        QueryMessage temp = new QueryMessage();
        temp.setRoute( MEPAService.ROUTE_TAG );
        temp.setType( QueryMessage.ACTION.CLEAR );

        Intent i = new Intent( BroadcastMessage.ACTION_NEW_QUERY_MSG );
        i.putExtra( BroadcastMessage.EXTRA_NEW_QUERY_MSG, temp );
        lbm.sendBroadcast( i );

        temp.setType( QueryMessage.ACTION.GET );

        i = new Intent( BroadcastMessage.ACTION_NEW_QUERY_MSG );
        i.putExtra( BroadcastMessage.EXTRA_NEW_QUERY_MSG, temp );
        lbm.sendBroadcast( i );
    }*/

    /**
     * Basic test for the startup of the Service
     * @throws Exception
     */
    public void test() throws Exception {
        assertNotNull( lbm );
        assertNotNull( getService() );
    }

    /**
     * Test the deployment of a new CEP Rule
     * - Query without a type
     * @throws Exception
     */
    /*public void testQueryDeployment() throws Exception {
        final MessageData[] response = new MessageData[1];
        final Semaphore semaphore = new Semaphore( 0 );

        BroadcastReceiver mTestReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {
                String action = i.getAction();

                if( action.equals( BroadcastMessage.ACTION_NEW_ERROR_MSG ) ) {
                    response[0] = (MessageData) i.getSerializableExtra( BroadcastMessage.EXTRA_NEW_ERROR_MSG );
                    semaphore.release();
                }
            }
        };
        registerBroadcasts( mTestReceiver );

        // No Type
        QueryMessage temp = new QueryMessage();
        temp.setRoute( MEPAService.ROUTE_TAG );

        Intent i = new Intent( BroadcastMessage.ACTION_NEW_QUERY_MSG );
        i.putExtra( BroadcastMessage.EXTRA_NEW_QUERY_MSG, temp );
        lbm.sendBroadcast( i );
        semaphore.acquire();

        assertNotNull( response[0] );
        assertEquals( MEPAService.ROUTE_TAG, response[0].getComponent() );
        assertEquals( MessageData.ERROR.ER01.toString(), response[0].getMessage() );
    }*/

    /**
     * Tests the add of CEP Rules
     * - Without label
     * - Wrong syntax
     * @throws Exception
     */
    /*public void testAddWrongCEPRule() throws Exception {
        final MessageData[] response = new MessageData[1];
        final Semaphore semaphore = new Semaphore( 0 );

        BroadcastReceiver mTestReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {
                String action = i.getAction();

                if( action.equals( BroadcastMessage.ACTION_NEW_ERROR_MSG ) ) {
                    response[0] = (MessageData) i.getSerializableExtra( BroadcastMessage.EXTRA_NEW_ERROR_MSG );
                    semaphore.release();
                }
            }
        };
        registerBroadcasts( mTestReceiver );

        // Add a query without label
        QueryMessage temp = new QueryMessage();
        temp.setRoute( MEPAService.ROUTE_TAG );
        temp.setType( QueryMessage.ACTION.ADD );
        temp.addQuery( "EMPTY" );

        Intent i = new Intent( BroadcastMessage.ACTION_NEW_QUERY_MSG );
        i.putExtra( BroadcastMessage.EXTRA_NEW_QUERY_MSG, temp );
        lbm.sendBroadcast( i );
        semaphore.acquire();

        assertNotNull( response[0] );
        assertEquals( MEPAService.ROUTE_TAG, response[0].getComponent() );
        assertEquals( MessageData.ERROR.ER03.toString(), response[0].getMessage() );

        // Add a wrong query
        temp.setLabel( "HighTemperature" );
        i.putExtra( BroadcastMessage.EXTRA_NEW_QUERY_MSG, temp );
        lbm.sendBroadcast( i );
        semaphore.acquire();

        assertNotNull( response[0] );
        assertEquals( MEPAService.ROUTE_TAG, response[0].getComponent() );
        assertNotNull( response[0].getMessage() );
    }*/

    /**
     * Test the remove of CEP Rules
     * - Remove a wrong query
     * @throws Exception
     */
    /*public void testRemoveWrongCEPRule() throws Exception {
        final MessageData[] response = new MessageData[1];
        final Semaphore semaphore = new Semaphore( 0 );

        BroadcastReceiver mTestReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {
                String action = i.getAction();

                if( action.equals( BroadcastMessage.ACTION_NEW_ERROR_MSG ) ) {
                    response[0] = (MessageData) i.getSerializableExtra( BroadcastMessage.EXTRA_NEW_ERROR_MSG );
                    semaphore.release();
                }
            }
        };
        registerBroadcasts( mTestReceiver );

        // Remove a wrong query
        QueryMessage temp = new QueryMessage();
        temp.setRoute( MEPAService.ROUTE_TAG );
        temp.setType( QueryMessage.ACTION.REMOVE );
        temp.setLabel( "EMPTY" );

        Intent i = new Intent( BroadcastMessage.ACTION_NEW_QUERY_MSG );
        i.putExtra( BroadcastMessage.EXTRA_NEW_QUERY_MSG, temp );
        lbm.sendBroadcast( i );
        semaphore.acquire();

        assertNotNull( response[0] );
        assertEquals( MEPAService.ROUTE_TAG, response[0].getComponent() );
        assertEquals( MessageData.ERROR.ER02.toString(), response[0].getMessage() );
    }*/

    /**
     * Test the correct detection of events,
     * by adding a CEP Rule and sending sensor data
     * @throws Exception
     */
    /*public void testDetection() throws Exception {
        final EventData[] response = new EventData[1];
        final Semaphore semaphore = new Semaphore( 0 );

        BroadcastReceiver mTestReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {
                String action = i.getAction();

                if( action.equals( BroadcastMessage.ACTION_NEW_EVENT_MSG ) ) {
                    response[0] = (EventData) i.getSerializableExtra( BroadcastMessage.EXTRA_NEW_EVENT_MSG );
                    semaphore.release();
                }
            }
        };
        registerBroadcasts( mTestReceiver );

        clearMEPAService();

        final String label = "HighTemperature";

        QueryMessage temp = new QueryMessage();
        temp.setRoute( MEPAService.ROUTE_TAG );
        temp.setType( QueryMessage.ACTION.ADD );
        temp.setLabel( label );
        temp.addQuery( "SELECT * FROM SensorData(sensorName='Temperature') WHERE sensorValue[0] >= 30" );

        Intent i = new Intent( BroadcastMessage.ACTION_NEW_QUERY_MSG );
        i.putExtra( BroadcastMessage.EXTRA_NEW_QUERY_MSG, temp );
        lbm.sendBroadcast( i );

        SensorData sensorData = new SensorData();
        sensorData.setMouuid( "1-mock" );
        sensorData.setSignal( 50.0 );
        sensorData.setSensorName( "Temperature" );
        sensorData.setSensorValue( new Double[]{ 30.0, 30.0 } );
        sensorData.setAction( SensorData.READ );

        sensorData.setPriority( LocalMessage.LOW );
        sensorData.setRoute( MEPAService.ROUTE_TAG );

        i = new Intent( BroadcastMessage.ACTION_NEW_SENSOR_MSG );
        i.putExtra( BroadcastMessage.EXTRA_NEW_SENSOR_MSG, sensorData );
        lbm.sendBroadcast( i );
        semaphore.acquire();

        assertNotNull( response[0] );
        assertEquals( label, response[0].getLabel() );
        assertNotNull( response[0].getData() );

        clearMEPAService();
    }*/
}
