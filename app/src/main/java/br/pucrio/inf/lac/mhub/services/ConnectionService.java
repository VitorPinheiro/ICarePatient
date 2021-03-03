package br.pucrio.inf.lac.mhub.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import br.pucrio.inf.lac.mhub.broadcastreceivers.BroadcastMessage;
import br.pucrio.inf.lac.mhub.components.AppConfig;
import br.pucrio.inf.lac.mhub.components.AppUtils;
import br.pucrio.inf.lac.mhub.models.base.LocalMessage;
import br.pucrio.inf.lac.mhub.models.locals.EventData;
import br.pucrio.inf.lac.mhub.models.locals.LocationData;
import br.pucrio.inf.lac.mhub.models.locals.MessageData;
import br.pucrio.inf.lac.mhub.models.locals.SensorData;
import br.pucrio.inf.lac.mhub.services.listeners.ConnectionListener;
import de.greenrobot.event.EventBus;
import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.ClientLibProtocol.PayloadSerialization;
import lac.cnclib.sddl.message.Message;

public class ConnectionService extends Service {
	/** DEBUG */
	private static final String TAG = ConnectionService.class.getSimpleName();

    /** Tag used to route the message */
    public static final String ROUTE_TAG = "CONN";

	/** The context object */
	private Context ac;

	/** SDDL IP address */
	private String ipAddress;
	
	/** SDDL connection port */
	private Integer port;

    /** The UUID of the device */
    private UUID uuid;
	
	/** The node connection to the SDDL gateway */
	//private static NodeConnection connection;
	private NodeConnection connection;
	
	/** The connection listener for the node connection */
	private ConnectionListener listener;
	
	/** The MrUDP socket connection */
	private SocketAddress socket;
	
	/** The last location object */
	private LocationData lastLocation;
	
	/** The keep running flag to indicate if the service is running, used internally */
	private volatile Boolean keepRunning;
	
	/** The is connected flag to indicate if the connection is active, used internally */
	private volatile Boolean isConnected;

    /** The interval time between messages to be sent */
    private Integer sendAllMsgsInterval;

	/** A list of messages to be sent to the gateway */
	//private final LinkedHashMap<String, Message> lstMsg = new LinkedHashMap<>();
	private final ConcurrentHashMap<String, Message> lstMsg = new ConcurrentHashMap<>();
	//private final ConcurrentLinkedQueue<Message> lstMsg = new ConcurrentLinkedQueue<>();
	
	/** The Local Broadcast Manager */
	private LocalBroadcastManager lbm;
	
	/**
	 * The device connectivity, not related to the MrUDP connection, there are
	 * 3 types.
	 * - No Connection
	 * - 3G
	 * - WiFi
	 */
	private String deviceTypeConnectivity;

	final Object lock = new Object();

	@Override
	public void onCreate() {
		super.onCreate();
		// initialize the flags
		keepRunning = true;
		isConnected = false;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
        AppUtils.logger( 'i', TAG, ">> Started" );
		// get the context 
		ac = ConnectionService.this;
        // register to event bus
        EventBus.getDefault().register( this );
		// get local broadcast 
		lbm = LocalBroadcastManager.getInstance( ac );
		// register broadcasts
		registerBroadcasts();
		// if it is not connected, create a new thread resetting previous threads
		if( !isConnected ) {
			// call the bootstrap to initialize all the variables
			bootstrap();
			// start thread connection 
			startThread();
		}
		// If we get killed, after returning from here, restart 
		return START_STICKY;
	}
	
	@Override
	public IBinder onBind( Intent i ) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
        AppUtils.logger( 'i', TAG, ">> Destroyed" );
		// not connected
		isConnected = false;
		// unregister broadcasts 
		unregisterBroadcasts();
        // unregister from event bus
        EventBus.getDefault().unregister( this );

		if( sendAllMsgsInterval <= 0 ) {
			synchronized( lock ) {
				lock.notify();
			}
		}
	}
	
	/**
	 * The bootstrap for this service, it will start and get all the default
	 * values from the SharedPreferences to start the service without any
	 * problem.
	 */
	private void bootstrap() {
		Boolean saved;
		// create the UUID for this device if there is not one
		if( AppUtils.getUuid( ac ) == null ) {
			saved = AppUtils.createSaveUuid( ac );
			if( !saved )
				AppUtils.logger( 'e', TAG, ">> UUID not saved to SharedPrefs" );
		}
		uuid = AppUtils.getUuid( ac );

		// set ip address 
		ipAddress = AppUtils.getIpAddress( ac );
		if( ipAddress == null )
			ipAddress = AppConfig.DEFAULT_SDDL_IP_ADDRESS;
		// save the ip address to SPREF 
		AppUtils.saveIpAddress( ac, ipAddress );

		// set port 
		port = AppUtils.getGatewayPort( ac );
		if( port == null )
			port = AppConfig.DEFAULT_SDDL_PORT;
		// save port to SPREF 
		AppUtils.saveGatewayPort( ac, port );

		// set the interval time between messages 
		sendAllMsgsInterval = AppUtils.getCurrentSendMessagesInterval( ac );
		if( sendAllMsgsInterval == null )
			sendAllMsgsInterval = AppConfig.DEFAULT_MESSAGES_INTERVAL_HIGH;
		AppUtils.saveCurrentSendMessagesInterval( ac, sendAllMsgsInterval );

		// start the listener here to be on another Thread 
		listener = ConnectionListener.getInstance( ac );
		//listener = new ConnectionListener( ac );
		
		// set all the default values for the options HIGH, MEDIUM and LOW on SPREF 
		if( AppUtils.getSendSignalsInterval( ac, AppConfig.SPREF_MESSAGES_INTERVAL_HIGH ) == null )
			AppUtils.saveSendSignalsInterval( ac,
					AppConfig.DEFAULT_MESSAGES_INTERVAL_HIGH,
					AppConfig.SPREF_MESSAGES_INTERVAL_HIGH );
		
		if( AppUtils.getSendSignalsInterval( ac, AppConfig.SPREF_MESSAGES_INTERVAL_MEDIUM ) == null )
			AppUtils.saveSendSignalsInterval( ac,
					AppConfig.DEFAULT_MESSAGES_INTERVAL_MEDIUM,
					AppConfig.SPREF_MESSAGES_INTERVAL_MEDIUM );
		
		if( AppUtils.getSendSignalsInterval( ac, AppConfig.SPREF_MESSAGES_INTERVAL_LOW ) == null )
			AppUtils.saveSendSignalsInterval( ac,
					AppConfig.DEFAULT_MESSAGES_INTERVAL_LOW,
					AppConfig.SPREF_MESSAGES_INTERVAL_LOW );
		
		// check for the network status 
		ConnectivityManager cm = (ConnectivityManager) ac.getSystemService( Context.CONNECTIVITY_SERVICE );
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		
		if( activeNetwork != null ) {
		    boolean isConnected = activeNetwork.isConnected();
		    boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
		    boolean is3G   = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
		    
		    if( isConnected && isWiFi )
		    	deviceTypeConnectivity = BroadcastMessage.INFO_CONNECTIVITY_WIFI;
		    else if( isConnected && is3G )
		    	deviceTypeConnectivity = BroadcastMessage.INFO_CONNECTIVITY_3G;
		    else if( !isConnected )
		    	deviceTypeConnectivity = BroadcastMessage.INFO_CONNECTIVITY_NO_CONNECTION;
		}
	}

    /**
     * Creates the MR-UDP connection
     * @return the connection
     * @throws IOException
     */
    /*public static NodeConnection getConnection() throws IOException {
        if( connection == null )
            connection = new MrUdpNodeConnection();
        return connection;
    }*/
	
	/**
	 * It starts the connection thread, it creates the connection and everything
	 * related to the connection to the gateway.
	 */
	private void startThread() {
		Thread t = new Thread( new Runnable() {
			public void run () {
				try {
					AppUtils.logger( 'i', TAG, "Thread created!! -- " + ipAddress + ":" + port );

                    //connection = getConnection();
					connection = new MrUdpNodeConnection();
					connection.addNodeConnectionListener( listener );
					socket = new InetSocketAddress( ipAddress, port );
					connection.connect( socket );
					isConnected = true;
					// set the service is running flag and is connected
                    Boolean saved = AppUtils.saveIsConnected( ac, true );
					if( !saved )
						AppUtils.logger( 'e', TAG, ">> isConnected flag not saved" );

					// loop forever while the service is running
					while( keepRunning ) {
						// kill connection
						if( !isConnected ) {
							keepRunning = false;
							connection.disconnect();
							stopThread();
						}

						// send messages if we have connection with the device
						if( isConnected && sendAllMsgsInterval > 0 ) {
                            AppUtils.logger( 'i', TAG, ">> Sending Messages(" + lstMsg.size() + ")" );
							Iterator<Map.Entry<String, Message>> it = lstMsg.entrySet().iterator();
							//Iterator<Message> it = lstMsg.iterator();

							synchronized( lstMsg ) {
								while( it.hasNext() ) {
									Map.Entry<String, Message> currentMessage = it.next();
									//Message currentMessage = it.next();
									connection.sendMessage( currentMessage.getValue() );
									//connection.sendMessage( currentMessage );
									it.remove();

								}
							}
							// This has to be changed. The disconnection will wait until the thread is wake up
							// Use handlers instead (Pendant)
							synchronized( this ) {
								Thread.sleep( sendAllMsgsInterval );
							}
						} else if( isConnected ) {
							synchronized( lock ) {
								lock.wait();
							}
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
	}
	
	/**
	 * It stops the connection thread.
	 */
	private synchronized void stopThread() {
		Boolean saved = AppUtils.saveIsConnected( ac, false );
		if( !saved )
			AppUtils.logger( 'e', TAG, ">> isConnected flag not saved" );
	}

    /**
     * Creates an application message to send to the cloud in JSON
     * It includes the current location if exists to the message
     * Depending on the priority it will send the message immediately
     * or group it to be sent in an interval of time
     * @param s The Mobile Hub Message structure
     * @param sender The UUID of the Mobile Hub
     */
    private void createAndQueueMsg(LocalMessage s, UUID sender) {
        s.setUuid( sender.toString() );
        Double latitude = null, longitude = null;

        // If location service not activated, set location
        if( !AppUtils.getCurrentLocationService( ac ) ) {
            latitude = AppUtils.getLocationLatitude( ac );
            longitude = AppUtils.getLocationLongitude( ac );
        }
        // The last known location
        else if( lastLocation != null ) {
            latitude = lastLocation.getLatitude();
            longitude = lastLocation.getLongitude();
        }

        if( latitude != null && longitude != null ) {
            s.setLatitude( latitude );
            s.setLongitude( longitude );
        }

        try {
            ApplicationMessage am = new ApplicationMessage();
            am.setPayloadType( PayloadSerialization.JSON );
            am.setContentObject( s.toJSON() );
            am.setTagList( new ArrayList<String>() );
            am.setSenderID( sender );

            if( s.getPriority().equals( LocalMessage.HIGH ) ) {
                connection.sendMessage( am );
            } else {
                synchronized( lstMsg ) {
                    lstMsg.put( s.getID(), am );
					//lstMsg.add( am );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * Register/Unregister the broadcast receiver.
	 */
	private void registerBroadcasts() {
		IntentFilter filter = new IntentFilter();
		filter.addAction( BroadcastMessage.ACTION_CHANGE_MESSAGES_INTERVAL );
		filter.addAction( BroadcastMessage.ACTION_CONNECTIVITY_CHANGED );
		
		lbm.registerReceiver( mConnBroadcastReceiver, filter );
	}
	
	private void unregisterBroadcasts() {
        if( lbm != null )
		    lbm.unregisterReceiver( mConnBroadcastReceiver );
	}

    @SuppressWarnings("unused") // it's actually used to receive events from the Location Service
    public void onEvent( LocationData locData ) {
        if( locData != null && AppUtils.isInRoute( ROUTE_TAG, locData.getRoute() ) ) {
			AppUtils.logger( 'i', TAG, ">> NEW_LOCATION_MSG" );
            //create the message
            locData.setConnectionType( deviceTypeConnectivity );
            // set the battery status
            IntentFilter battFilter = new IntentFilter( Intent.ACTION_BATTERY_CHANGED );
            Intent iBatt = ac.registerReceiver( null, battFilter );

            // No battery present
            if( iBatt == null ) {
                AppUtils.logger( 'e', TAG, "No Battery Present" );
            } else {
                int level = iBatt.getIntExtra( BatteryManager.EXTRA_LEVEL, -1 );
                float scale = iBatt.getIntExtra( BatteryManager.EXTRA_SCALE, -1 );
                int battLevel = (int) ( ( level / scale ) * 100 );
                locData.setBatteryPercent( battLevel );
                // set the battery charging
                int charging = iBatt.getIntExtra( BatteryManager.EXTRA_STATUS, -1 );
                locData.setCharging( charging == BatteryManager.BATTERY_STATUS_CHARGING );
            }

            // save the last location (used when a new msg is send)
            lastLocation = locData;
            // add the message to the queue
            createAndQueueMsg( locData, uuid );
        }
    }

    @SuppressWarnings("unused") // it's actually used to receive events from the S2PA Service
    public void onEvent( SensorData sensorData ) {
        // Look if the message is for this service
        if( sensorData != null && AppUtils.isInRoute( ROUTE_TAG, sensorData.getRoute() ) )
			createAndQueueMsg( sensorData, uuid );
    }

    @SuppressWarnings("unused") // it's actually used to receive events from the MEPA Service
    public void onEvent( EventData eventData ) {
        // Look if the message is for this service
        if( eventData != null && AppUtils.isInRoute( ROUTE_TAG, eventData.getRoute() ) )
			createAndQueueMsg( eventData, uuid );
    }

    @SuppressWarnings("unused") // it's actually used to receive error events
    public void onEvent( MessageData messageData ) {
        createAndQueueMsg( messageData, uuid );
    }

    /**
     * The broadcast receiver for all the services, it will receive all the
     * updates from the location/mepa/s2pa service and send it to the gateway.
     */
    private BroadcastReceiver mConnBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent i) {
            String action = i.getAction();

			/* Broadcast: ACTION_CHANGE_SEND_MESSAGES_INTERVAL */
			/* *********************************************** */
            if( action.equals( BroadcastMessage.ACTION_CHANGE_MESSAGES_INTERVAL ) ) {
                if( !AppUtils.getCurrentEnergyManager( ac ) )
                    return;

                sendAllMsgsInterval = i.getIntExtra( BroadcastMessage.EXTRA_CHANGE_MESSAGES_INTERVAL, -1 );
                // problem getting the value from the extra, set the default value
                if( sendAllMsgsInterval < 0 )
                    sendAllMsgsInterval = AppConfig.DEFAULT_MESSAGES_INTERVAL_HIGH;
                // save the preferences with the new value
                AppUtils.saveCurrentSendMessagesInterval( ac, sendAllMsgsInterval );
            }
			/* Broadcast: ACTION_CONNECTIVITY_CHANGED */
			/* ************************************** */
            else if( action.equals( BroadcastMessage.ACTION_CONNECTIVITY_CHANGED ) ) {
                deviceTypeConnectivity = i.getStringExtra( BroadcastMessage.EXTRA_CONNECTIVITY_CHANGED );
            }
        }
    };
}
