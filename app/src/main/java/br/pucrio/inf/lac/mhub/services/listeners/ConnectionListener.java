package br.pucrio.inf.lac.mhub.services.listeners;

import android.content.Context;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.pucrio.inf.lac.mhub.components.AppUtils;
import br.pucrio.inf.lac.mhub.managers.LocalRouteManager;
import br.pucrio.inf.lac.mhub.models.ConnectionData;
import de.greenrobot.event.EventBus;
import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.Message;

/**
 * Receives the messages from the cloud, is the listener for
 * the connection service
 */
public class ConnectionListener implements NodeConnectionListener {
	/** DEBUG */
	private static final String TAG = ConnectionListener.class.getSimpleName();

    /** The UUID for this device */
    private final UUID uuid;

    /** The Command Manager */
    private final LocalRouteManager cm;

    private static ConnectionListener instance = null;

	private ConnectionListener( Context ac ) {
        // get UUID
        uuid = AppUtils.getUuid( ac );
        // get the command manager
        cm = LocalRouteManager.getInstance();
	}

    public static ConnectionListener getInstance( Context ac ) {
        if( instance == null )
            instance = new ConnectionListener( ac );
        return instance;
    }

	@Override
	public void connected( NodeConnection nc ) {
		AppUtils.logger( 'i', TAG, "Connected and Identified..." );
		publishState( ConnectionData.CONNECTED );
        sendACKMessage( nc );
	}

	@Override
	public void reconnected( NodeConnection nc, SocketAddress s, boolean b1, boolean b2 ) {
		AppUtils.logger( 'i', TAG, "Reconnected..." );
		publishState( ConnectionData.CONNECTED );
		sendACKMessage( nc );
	}

	@Override
	public void disconnected( NodeConnection nc ) {
		AppUtils.logger( 'i', TAG, "Disconnected..." );
		publishState( ConnectionData.DISCONNECTED );
	}

	@Override
	public void internalException( NodeConnection nc, Exception e ) {
		AppUtils.logger( 'i', TAG, "InternalException... " + e.getMessage() );
	}

	@Override
	public void newMessageReceived( NodeConnection nc, Message m ) {
		AppUtils.logger( 'i', TAG, "NewMessageReceived..." );

		if( m.getContentObject() instanceof String ) {
			String str = m.getContentObject().toString();
			AppUtils.logger( 'i', TAG, str );
            cm.routeMessage( str );
		}
	}

	@Override
	public void unsentMessages( NodeConnection nc, List<Message> mList ) {
		AppUtils.logger( 'd', TAG, "UnsetMessages..." );
	}

    /**
     * Sends an ACK message to the cloud and the local services
     * to let them know that the connection (connection or reconnection)
     * is established
     * @param nc The connection
     */
    private void sendACKMessage( NodeConnection nc ) {
        // Send a message once we are reconnected
        ApplicationMessage am = new ApplicationMessage();
        am.setContentObject( "ack" );
        am.setTagList( new ArrayList<String>() );
        am.setSenderID( uuid );

        try {
            nc.sendMessage( am );
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

	/**
	 * Publish the connection state to the subscribers
	 * @param state The state connected or disconnected
	 */
	private void publishState( String state ) {
		ConnectionData data = new ConnectionData();
		data.setState( state );
		// Post the Connection object for subscribers
		EventBus.getDefault().postSticky( data );
	}
}
