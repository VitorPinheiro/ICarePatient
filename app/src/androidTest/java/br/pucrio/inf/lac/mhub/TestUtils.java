package br.pucrio.inf.lac.mhub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.test.AndroidTestCase;

import java.util.UUID;
import java.util.concurrent.Semaphore;

import br.pucrio.inf.lac.mhub.broadcastreceivers.BroadcastMessage;
import br.pucrio.inf.lac.mhub.components.AppUtils;
import br.pucrio.inf.lac.mhub.components.MOUUID;
import br.pucrio.inf.lac.mhub.managers.LocalRouteManager;
import br.pucrio.inf.lac.mhub.models.locals.MessageData;
import br.pucrio.inf.lac.mhub.models.base.QueryMessage;

/**
 * Created by luis on 17/06/15.
 * Test the utils and other components
 */
public class TestUtils extends AndroidTestCase {
    /** DEBUG */
    private final static String TAG = TestReceivers.class.getSimpleName();

    /** The context object */
    private Context ac;

    /** The Local Broadcast Manager */
    private LocalBroadcastManager lbm;

    /** Test globals */
    private static final String correctIP = "192.168.1.10";
    private static final String correctPort = "5500";

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        AppUtils.logger( 'd', TAG, "Unit Tests" );
        // get the context
        ac = getContext();
        // get local broadcast
        lbm = LocalBroadcastManager.getInstance( ac );
    }

    /**
     * Register/Unregister the broadcast receiver.
     */
    /*private void registerBroadcasts(BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter();
        filter.addAction( BroadcastMessage.ACTION_NEW_ERROR_MSG );
        filter.addAction( BroadcastMessage.ACTION_NEW_QUERY_MSG );
        lbm.registerReceiver( receiver, filter );
    }

    public void testValidIp() throws Exception {
        String wrongIP1  = "192.168.1";
        String wrongIP2  = "192168110";
        String wrongIP3  = "Wrong";
        String wrongIP4  = "1922.1681.1.10";

        assertTrue(AppUtils.isValidIp( correctIP ) );
        assertFalse(AppUtils.isValidIp( wrongIP1 ) );
        assertFalse(AppUtils.isValidIp( wrongIP2 ) );
        assertFalse(AppUtils.isValidIp( wrongIP3 ) );
        assertFalse(AppUtils.isValidIp( wrongIP4 ) );
    }*/

    public void testValidPort() throws Exception {
        String wrongPort1  = "192.168.1";
        String wrongPort2  = "0";
        String wrongPort3  = "Wrong";
        String wrongPort4  = "65536";

        assertTrue( AppUtils.isValidPort( correctPort ) );
        assertFalse( AppUtils.isValidPort( wrongPort1 ) );
        assertFalse( AppUtils.isValidPort( wrongPort2 ) );
        assertFalse( AppUtils.isValidPort( wrongPort3 ) );
        assertFalse( AppUtils.isValidPort( wrongPort4 ) );
    }

    public void testPreference() throws Exception {
        // Test get Ip Address
        String ip = AppUtils.getIpAddress( ac );
        assertNotNull( ip );
        AppUtils.saveIpAddress( ac, correctIP );
        assertEquals( correctIP, AppUtils.getIpAddress( ac ) );
        AppUtils.saveIpAddress( ac, ip );

        // Test get Port
        Integer port = AppUtils.getGatewayPort( ac );
        assertNotNull( port );
        AppUtils.saveGatewayPort( ac, Integer.parseInt( correctPort ) );
        assertEquals( correctPort, AppUtils.getGatewayPort( ac ).toString() );
        AppUtils.saveGatewayPort( ac, port );
    }

    public void testMOUUID() throws Exception {
        int technology = 1;
        String address = "00a0c91e6bf6";
        UUID uuid = UUID.fromString( "00000000-0000-0000-000" + technology + "-" + address );
        MOUUID mouuid1 = MOUUID.fromUUID(uuid);
        assertNotNull( mouuid1 );
        assertEquals( technology, mouuid1.getTechnologyID() );
        assertEquals( address, mouuid1.getAddress() );

        MOUUID mouuid2 = new MOUUID( technology, address );
        assertNotNull( mouuid2 );
        assertEquals( technology, mouuid2.getTechnologyID() );
        assertEquals( address, mouuid2.getAddress() );

        assertEquals( mouuid1, mouuid2 );
        assertNotNull( mouuid1.toString() );
        assertNotNull( mouuid1.toUUID() );
        assertEquals( mouuid1.toUUID(), uuid );

        MOUUID mouuid = MOUUID.fromString( technology + "-" + address );
        assertNotNull( mouuid );
        assertEquals( technology, mouuid2.getTechnologyID() );
        assertEquals( address, mouuid2.getAddress() );
    }

    /*public void testLocalRouteManager() throws Exception {
        final MessageData[] response = new MessageData[1];
        final QueryMessage[] query    = new QueryMessage[1];
        final Semaphore semaphore  = new Semaphore( 0 );
        final LocalRouteManager cm = LocalRouteManager.getInstance( ac );

        BroadcastReceiver mTestReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {
                String action = i.getAction();

                if( action.equals( BroadcastMessage.ACTION_NEW_ERROR_MSG ) ) {
                    response[0] = (MessageData) i.getSerializableExtra( BroadcastMessage.EXTRA_NEW_ERROR_MSG );
                    semaphore.release();
                }
                else if( action.equals( BroadcastMessage.ACTION_NEW_QUERY_MSG ) ) {
                    query[0] = (QueryMessage) i.getSerializableExtra( BroadcastMessage.EXTRA_NEW_QUERY_MSG );
                    semaphore.release();
                }
            }
        };
        registerBroadcasts( mTestReceiver );

        assertFalse( cm.routeMessage( null ) );
        semaphore.acquire();

        assertNotNull( response[0] );
        assertNotNull( response[0].getComponent());
        assertEquals( MessageData.ERROR.ER04.toString(), response[0].getMessage() );
        assertFalse( cm.routeMessage( "[]" ) );

        assertTrue( cm.routeMessage( "{}" ) );
    }*/
}
