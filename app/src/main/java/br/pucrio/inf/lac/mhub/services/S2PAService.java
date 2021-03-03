package br.pucrio.inf.lac.mhub.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.SparseArray;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import br.pucrio.inf.lac.mhub.R;
import br.pucrio.inf.lac.mhub.broadcastreceivers.BatteryReceiver;
import br.pucrio.inf.lac.mhub.broadcastreceivers.BroadcastMessage;
import br.pucrio.inf.lac.mhub.components.AppConfig;
import br.pucrio.inf.lac.mhub.components.AppUtils;
import br.pucrio.inf.lac.mhub.components.MOUUID;
import br.pucrio.inf.lac.mhub.models.locals.SensorData;
import br.pucrio.inf.lac.mhub.s2pa.technologies.bt.BTTechnology;
import br.pucrio.inf.lac.mhub.ui.MHubSettings;
import br.pucrio.inf.lac.mhub.models.base.LocalMessage;
import br.pucrio.inf.lac.mhub.models.queries.S2PAQuery;
import br.pucrio.inf.lac.mhub.s2pa.base.Technology;
import br.pucrio.inf.lac.mhub.s2pa.base.TechnologyListener;
import br.pucrio.inf.lac.mhub.s2pa.technologies.ble.BLETechnology;
import de.greenrobot.event.EventBus;

public class S2PAService extends Service implements TechnologyListener {
	/** DEBUG */
	private static final String TAG = S2PAService.class.getSimpleName();

    /** Tag used to route the message */
    public static final String ROUTE_TAG = "S2PA";

	/** The context object */
	private Context ac;
	
	/** Notifications */
	private NotificationManager mNManager;
	
	/** Unique Identification Number for the Notification */
    private int NOTIFICATION = R.string.service_started;

    /** SparseArray of technologies */
    private SparseArray<Technology> technologies;

 	/** Time for the scans */
 	private Integer currentTime;
 	
 	/** The Local Broadcast Manager */
 	private LocalBroadcastManager lbm;

    /** Alarm Manager to check for battery status */
    private AlarmManager alarmMngr;

    /** Alarm pending intent */
    private PendingIntent piAlarm;
 	
 	/** Handlers */
 	private Handler mTimerHandler;
 	private Handler mStopperHandler;
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AppUtils.logger( 'i', TAG, ">> Started" );
		// Get the context
		ac = S2PAService.this;
        // register to event bus
        EventBus.getDefault().register( this );
		// get local broadcast 
		lbm = LocalBroadcastManager.getInstance( ac );
		// register broadcast 
		registerBroadcasts();
        // clean everything before begin
        cleanUp();
		// Restart the services
		setAllRunningServices( true );
        // configurations
        bootstrap();
        // Start the routing from the technologies
        startRouting();
		// Notifications
		showNotification();
		// if the service is killed by Android, service starts again

        // Simulating Zephyr sensor
        new Thread(new Runnable() {
            @Override
            public void run() {
                simulatedSensor();
            }
        }).start();

        return START_STICKY;
    }
	
	@Override
	public void onDestroy() {
		super.onDestroy();
        AppUtils.logger( 'i', TAG, ">> Destroyed" );
        // unregister from event bus
        EventBus.getDefault().unregister( this );
		// unregister broadcast 
		unregisterBroadcasts();
        // stop the Alarm Manager
        if( piAlarm != null )
            alarmMngr.cancel( piAlarm );
        // stop receiving the information from technologies
        stopRouting();
        // Destroy technologies
        if( technologies != null ) {
            for( int i = 0; i < technologies.size(); i++ ) {
                Technology temp = technologies.valueAt( i );
                temp.destroy();
            }
            technologies.clear();
        }
		// Stops all the services
		setAllRunningServices( false );
		// Cancels the notification
        if( mNManager != null )
		    mNManager.cancel( NOTIFICATION );
	}
	
	@Override
	public IBinder onBind( Intent intent ) {
		return null;
	}

    private void bootstrap() {
        // Initialize components
        technologies     = new SparseArray<>();
        mStopperHandler  = new Handler();
        mTimerHandler    = new Handler();

        Technology temp = new BLETechnology( ac );
        boolean exist = temp.initialize();
        if( exist ) {
            technologies.append( BLETechnology.ID, temp );
            temp.setListener( this );
            temp.enable();
        }

        Technology temp2 = BTTechnology.getInstance( ac );
        boolean exist2 = temp.initialize();
        if( exist2 ) {
            technologies.append( BLETechnology.ID, temp );
            temp.setListener( this );
            temp.enable();
        }


        // set all the default values for the options HIGH, MEDIUM and LOW
        if( AppUtils.getScanInterval( ac, AppConfig.SPREF_SCAN_INTERVAL_HIGH ) == null )
            AppUtils.saveScanInterval( ac
                    , AppConfig.DEFAULT_SCAN_INTERVAL_HIGH
                    , AppConfig.SPREF_SCAN_INTERVAL_HIGH
            );

        if( AppUtils.getScanInterval( ac, AppConfig.SPREF_SCAN_INTERVAL_MEDIUM ) == null )
            AppUtils.saveScanInterval( ac
                    , AppConfig.DEFAULT_SCAN_INTERVAL_MEDIUM
                    , AppConfig.SPREF_SCAN_INTERVAL_MEDIUM
            );

        if( AppUtils.getScanInterval( ac, AppConfig.SPREF_SCAN_INTERVAL_LOW ) == null )
            AppUtils.saveScanInterval( ac
                    , AppConfig.DEFAULT_SCAN_INTERVAL_LOW
                    , AppConfig.SPREF_SCAN_INTERVAL_LOW
            );

        // start and AlarmManager to check for battery
        Intent iAlarm = new Intent( ac, BatteryReceiver.class );
        iAlarm.setAction( BroadcastMessage.ACTION_CHECK_BATTERY_LEVEL );
        piAlarm = PendingIntent.getBroadcast( ac, 0, iAlarm, PendingIntent.FLAG_CANCEL_CURRENT );
        Calendar current = Calendar.getInstance();
        current.setTimeInMillis( System.currentTimeMillis() );
        alarmMngr = (AlarmManager) getSystemService( Context.ALARM_SERVICE );

        alarmMngr.setInexactRepeating( AlarmManager.RTC_WAKEUP,
                ( current.getTimeInMillis() + 1000 * 60 ),
                AlarmManager.INTERVAL_HALF_HOUR, // Repeat
                piAlarm
        );
    }

    /**
     * It cleans up everything it needs.
     */
    private void cleanUp() {
        Boolean saved = AppUtils.saveIsConnected( ac, false );
        if( !saved )
            AppUtils.logger( 'e', TAG, ">> isConnected flag not saved" );
    }
	
	/**
	 * Stops or Restarts all services
	 */
	private void setAllRunningServices( boolean start ) {
        if( ac == null ) {
            AppUtils.logger( 'e', TAG, "Null Context" );
            return;
        }

        Intent iConn = new Intent( ac, ConnectionService.class );
        Intent iLoc  = new Intent( ac, LocationService.class );
        Intent iAdap = new Intent( ac, AdaptationService.class );
        Intent iMepa = new Intent( ac, MEPAService.class );

		// stop all services
        if( AppUtils.isMyServiceRunning( ac, ConnectionService.class.getName() ) )
            stopService( iConn );

        if( AppUtils.isMyServiceRunning( ac, AdaptationService.class.getName() ) )
            stopService( iAdap );

        if( AppUtils.isMyServiceRunning( ac, LocationService.class.getName()) )
            stopService( iLoc );

        if( AppUtils.isMyServiceRunning( ac, MEPAService.class.getName() ) )
            stopService( iMepa );
		
		// start services
		if( start ) {
            startService( iConn );

            if( AppUtils.getCurrentLocationService( ac ) )
                startService( iLoc );

            startService( iAdap );

            if( AppUtils.getCurrentMEPAService( ac ) )
                startService( iMepa );
		}
	}
    
    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
    	mNManager = (NotificationManager) getSystemService( NOTIFICATION_SERVICE );
    	
    	CharSequence title = getText( R.string.service_name );
        CharSequence text  = getText( R.string.service_started );
        
        Intent notificationIntent = new Intent( S2PAService.this, MHubSettings.class );
        notificationIntent.setAction( Intent.ACTION_MAIN );
        notificationIntent.addCategory( Intent.CATEGORY_LAUNCHER );
        PendingIntent intent = PendingIntent.getActivity( this, 0, notificationIntent, 0 );
        
        Notification notification = new Notification.Builder( ac )
        					.setContentTitle( title )
        					.setContentText( text )
        					.setWhen( System.currentTimeMillis() )
        					.setSmallIcon( R.drawable.ic_launcher ).build();
        
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.contentIntent = intent;
        
        mNManager.notify( NOTIFICATION, notification );
    }

    /**
     * Starts the routing by schedule a scan on some periods
     * @return if there are technologies to scan
     */
    public boolean startRouting() {
        if( technologies.size() > 0 ) {
            // check for the current value
            currentTime = AppUtils.getCurrentScanInterval( ac );
            if( currentTime == null ) // if null get the default
                currentTime = AppConfig.DEFAULT_SCAN_INTERVAL_HIGH;
            // save the current scan interval to SPREF
            AppUtils.saveCurrentScanInterval( ac, currentTime );
            // Start the timer
            mTimerHandler.postDelayed( mDoScan, AppConfig.DEFAULT_DELAY_SCAN_PERIOD );

            return true;
        }
        return false;
    }

    private void stopRouting() {
        if( mTimerHandler != null )
            mTimerHandler.removeCallbacks( mDoScan );
    }

    // Runnable that takes care of start the scans
    private Runnable mDoScan = new Runnable() {
        @Override
        public void run() {
            Boolean autoconnect = AppUtils.getCurrentAutoconnectMO( ac );

            for(int i = 0; i < technologies.size(); i++) {
                Technology temp = technologies.valueAt( i );
                temp.startScan( autoconnect );
            }
            // Stops the scan after the default time
            mStopperHandler.postDelayed( mStopScan, AppConfig.DEFAULT_SCAN_PERIOD );
        }
    };

    // Runnable that takes care of stopping the scans
    private Runnable mStopScan = new Runnable() {
        @Override
        public void run() {
            for(int i = 0; i < technologies.size(); i++) {
                Technology temp = technologies.valueAt(i);
                temp.stopScan();
            }
            // Starts a new scan
            mTimerHandler.postDelayed( mDoScan, currentTime );
        }
    };

    @Override
    public void onMObjectFound( MOUUID mobileObject, Double rssi ) {
        AppUtils.logger( 'i', TAG, ">> MObject Found: " + mobileObject.toString() );

        SensorData sensorData = new SensorData();
        sensorData.setMouuid( mobileObject.toString() );
        sensorData.setSignal( rssi );
        sensorData.setAction( SensorData.FOUND );

        sensorData.setPriority( LocalMessage.HIGH );
        sensorData.setRoute( ConnectionService.ROUTE_TAG );

        EventBus.getDefault().post( sensorData );
    }

    @Override
    public void onMObjectConnected( MOUUID mobileObject ) {
        AppUtils.logger( 'i', TAG, ">> MObject Connected: " + mobileObject.toString() );

        SensorData sensorData = new SensorData();
        sensorData.setMouuid( mobileObject.toString() );
        sensorData.setAction( SensorData.CONNECTED );

        sensorData.setPriority( LocalMessage.HIGH );
        sensorData.setRoute( ConnectionService.ROUTE_TAG );

        EventBus.getDefault().post( sensorData );
    }

    @Override
    public void onMObjectDisconnected( MOUUID mobileObject, List<String> services ) {
            AppUtils.logger('i', TAG, ">> MObject Disconnected: " + mobileObject.toString());

            SensorData sensorData = new SensorData();
            sensorData.setMouuid(mobileObject.toString());
            sensorData.setAction(SensorData.DISCONNECTED);

            sensorData.setPriority(LocalMessage.HIGH);
            sensorData.setRoute(ConnectionService.ROUTE_TAG);

            EventBus.getDefault().post(sensorData);

    }

    @Override
    public void onMObjectServicesDiscovered( MOUUID mobileObject, List<String> services ) {
        AppUtils.logger( 'i', TAG, ">> MObject Services Discovered: " + mobileObject.toString() );
    }

    @Override
    public void onMObjectValueRead( MOUUID mobileObject, Double rssi, String serviceName, Double[] values ) {
        AppUtils.logger( 'i', TAG, ">> MObject: " + mobileObject.toString() + " Service: " + serviceName + " - Value: " + Arrays.toString( values ) );

        SensorData sensorData = new SensorData();
        sensorData.setMouuid( mobileObject.toString() );
        sensorData.setSignal( rssi );
        sensorData.setSensorName( serviceName );
        sensorData.setSensorValue( values );
        sensorData.setAction( SensorData.READ );

        sensorData.setPriority( LocalMessage.LOW );
        // "|" => MHubMessage.SEPARATOR
        sensorData.setRoute( ConnectionService.ROUTE_TAG + "|" + MEPAService.ROUTE_TAG );

        EventBus.getDefault().post( sensorData );
    }

    public void simulatedSensor()
    {
        while(true) {
            AppUtils.logger('i', TAG, ">> MObject: ");

            SensorData sensorData = new SensorData();
            sensorData.setMouuid("00000000-0451-4000-b000-000000000023");
            sensorData.setSignal(-29.0);
            sensorData.setSensorName("zephyrvitor");
            sensorData.setSensorValue(new Double[]{20.0});
            sensorData.setAction(SensorData.READ);

            sensorData.setPriority(LocalMessage.LOW);
            // "|" => MHubMessage.SEPARATOR
            sensorData.setRoute(ConnectionService.ROUTE_TAG + "|" + MEPAService.ROUTE_TAG);

            EventBus.getDefault().post(sensorData);

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Register/Unregister the Broadcast Receivers.
     */
    private void registerBroadcasts() {
        IntentFilter filter = new IntentFilter();
        filter.addAction( BroadcastMessage.ACTION_CHANGE_SCAN_INTERVAL );
        lbm.registerReceiver( mS2PABroadcastReceiver, filter );
    }

    private void unregisterBroadcasts() {
        if( lbm != null )
            lbm.unregisterReceiver( mS2PABroadcastReceiver );
    }

    @SuppressWarnings("unused") // it's actually used to receive s2pa query events
    public void onEvent( S2PAQuery query ) {
        switch( query.getType() ) {
            case ADD:
                String target = query.getTarget();
                List<String> devices = query.getDevices();

                try {
                    if( target.equals( "black" ) ) {
                        for( String temp : devices ) {
                            MOUUID device = MOUUID.fromString( temp );
                            Technology technology = technologies.get( device.getTechnologyID() );
                            technology.addToBlackList( device.getAddress() );
                        }
                    } else if( target.equals( "white" ) ) {
                        for (String temp : devices) {
                            MOUUID device = MOUUID.fromString( temp );
                            Technology technology = technologies.get( device.getTechnologyID() );
                            technology.addToWhiteList( device.getAddress() );
                        }
                    }
                } catch( StringIndexOutOfBoundsException ex ) {
                    AppUtils.sendErrorMessage( ROUTE_TAG, ex.getMessage() );
                }
                break;

            case REMOVE:
                break;
        }
    }

    /**
     * The broadcast receiver.
     */
    public BroadcastReceiver mS2PABroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent i) {
            String action = i.getAction();
			/* Broadcast: ACTION_CHANGE_SCAN_INTERVAL */
			/* ****************************************** */
            if( action.equals( BroadcastMessage.ACTION_CHANGE_SCAN_INTERVAL ) ) {
                if( !AppUtils.getCurrentEnergyManager( ac ) )
                    return;

                // obtain the new value from the EXTRA
                currentTime = i.getIntExtra( BroadcastMessage.EXTRA_CHANGE_SCAN_INTERVAL, -1 );
                // problem getting the value, set the default value
                if( currentTime < 0 )
                    currentTime = AppConfig.DEFAULT_SCAN_INTERVAL_HIGH;
                // save the preferences with the new value
                AppUtils.saveCurrentScanInterval( ac, currentTime );
                AppUtils.logger( 'd', TAG, currentTime + "" );
            }
        }
    };
}