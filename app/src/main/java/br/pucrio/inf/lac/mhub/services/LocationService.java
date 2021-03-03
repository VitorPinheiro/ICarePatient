package br.pucrio.inf.lac.mhub.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import java.util.Date;

import br.pucrio.inf.lac.mhub.broadcastreceivers.BroadcastMessage;
import br.pucrio.inf.lac.mhub.components.AppConfig;
import br.pucrio.inf.lac.mhub.components.AppUtils;
import br.pucrio.inf.lac.mhub.models.locals.LocationData;
import br.pucrio.inf.lac.mhub.models.base.LocalMessage;
import de.greenrobot.event.EventBus;

/**
 * Service to obtain the location of the device.
 * @author Luis Talavera
 */
public class LocationService extends Service implements LocationListener {
	/** DEBUG */
	private static final String TAG = LocationService.class.getSimpleName();
	
	/** The context object */
	private Context ac;

	/** The location manager */
	private LocationManager lm;

    /** Current location update interval */
    private Integer currentInterval;

	/** The two providers that we care */
	private String gpsProvider;
    private String networkProvider;

    /** The Local Broadcast Manager */
    private LocalBroadcastManager lbm;

    /** Last location saved */
    private Location lastRegisteredLocation;

    /** GPS rate, since it consumes more battery than network */
    private static final int GPS_RATE = 4;

    /** Time difference threshold set for two minutes */
    private static final int TIME_DIFFERENCE_THRESHOLD = 1 * 60 * 1000;

    /** This is the object that receives interactions from clients */
    private final IBinder mBinder = new LocalBinder();

    /**
     * Class for clients to access. Because we know this service always runs in
     * the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

	/**
	 * It gets called when the service is started.
	 * 
	 * @param i The intent received.
	 * @param flags Additional data about this start request.
	 * @param startId A unique integer representing this specific request to start.
	 * @return Using START_STICKY the service will run again if got killed by
	 * the service.
	 */
	@Override
	public int onStartCommand(Intent i, int flags, int startId) {
        AppUtils.logger( 'i', TAG, ">> STARTED" );
		// get the context 
		ac = LocationService.this;
        // get local broadcast
        lbm = LocalBroadcastManager.getInstance( ac );
        // register broadcast
        registerBroadcasts();
		// get location manager 
		lm = (LocationManager) getSystemService( LOCATION_SERVICE );
		// Configurations
		bootstrap();
		// if the service is killed by Android, service starts again 
		return START_STICKY;
	}
	
	/**
	 * When the service get destroyed by Android or manually.
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
        AppUtils.logger( 'i', TAG, ">> DESTROYED" );
        // unregister broadcast
        unregisterBroadcasts();
		// remove the listener
        if( lm != null )
		    lm.removeUpdates( this );
	}

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
	
	/**
	 * The method used to obtain the new location.
	 * @param location The new location object.
	 */
	@Override
	public void onLocationChanged(Location location) {
        // Wait until we get a good enough location
        if( isBetterLocation( lastRegisteredLocation, location ) ) {
            AppUtils.logger( 'i', TAG, ">> NEW_LOCATION_SENT" );

            LocationData locData = new LocationData();
            locData.setLatitude( location.getLatitude() );
            locData.setLongitude( location.getLongitude() );
            locData.setAccuracy( location.getAccuracy() );
            locData.setDatetime( new Date() );
            locData.setBearing( location.getBearing() );
            locData.setProvider( location.getProvider() );
            locData.setSpeed( location.getSpeed() );
            locData.setAltitude( location.getAltitude() );

            locData.setPriority( LocalMessage.LOW );
            locData.setRoute( ConnectionService.ROUTE_TAG );

            // post the Location object for subscribers
            EventBus.getDefault().postSticky( locData );

            // save the location as last registered
            lastRegisteredLocation = location;
        }
	}
	
	@Override
	public void onProviderDisabled(String provider) {
        AppUtils.logger( 'i', TAG, "provider disabled: " + provider );

        // If it's a provider we care about, we set it as null
        if( provider.equals( LocationManager.GPS_PROVIDER ) )
            gpsProvider = null;
        else if( provider.equals( LocationManager.NETWORK_PROVIDER ) )
            networkProvider = null;
    }

	@Override
	public void onProviderEnabled(String provider) {
        AppUtils.logger( 'i', TAG, "provider enabled: " + provider );

        // If it's a provider we care about, we start listening
        if( provider.equals( LocationManager.GPS_PROVIDER ) ) {
            gpsProvider = LocationManager.GPS_PROVIDER;
            lm.requestLocationUpdates( gpsProvider,
                    currentInterval * GPS_RATE,
                    AppConfig.DEFAULT_LOCATION_MIN_DISTANCE,
                    this );
        }
        else if( provider.equals( LocationManager.NETWORK_PROVIDER ) ) {
            networkProvider = LocationManager.NETWORK_PROVIDER;
            lm.requestLocationUpdates( networkProvider,
                    currentInterval,
                    AppConfig.DEFAULT_LOCATION_MIN_DISTANCE,
                    this );
        }
    }

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
        String statusAsString = "Available";
        if( status == LocationProvider.OUT_OF_SERVICE )
            statusAsString = "Out of service";
        else if( status == LocationProvider.TEMPORARILY_UNAVAILABLE )
            statusAsString = "Temporarily Unavailable";

        // Log any information about the status of the providers
        AppUtils.logger( 'i', TAG, provider + " provider status has changed: [" + statusAsString + "]" );
    }
	
	/**
	 * The bootstrap for the location service
	 */
	private void bootstrap() {
        // check for the current value
        currentInterval = AppUtils.getCurrentLocationInterval( ac );
        if( currentInterval == null ) // if null get the default
            currentInterval = AppConfig.DEFAULT_LOCATION_INTERVAL_HIGH;
        // save the current location interval to SPREF
        AppUtils.saveCurrentLocationInterval( ac, currentInterval );

        // Start listening location updated from gps and network, if enabled
        if( lm.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            gpsProvider = LocationManager.GPS_PROVIDER;
            lm.requestLocationUpdates( gpsProvider,
                    currentInterval * GPS_RATE,
                    AppConfig.DEFAULT_LOCATION_MIN_DISTANCE,
                    this );

            AppUtils.logger( 'i', TAG, "GPS Location provider has been started" );
        }

        // 4x faster refreshing rate since this provider doesn't consume much battery.
        if( lm.isProviderEnabled( LocationManager.NETWORK_PROVIDER ) ) {
            networkProvider = LocationManager.NETWORK_PROVIDER;
            lm.requestLocationUpdates( networkProvider,
                    currentInterval,
                    AppConfig.DEFAULT_LOCATION_MIN_DISTANCE,
                    this );

            AppUtils.logger( 'i', TAG, "NETWORK Location provider has been started" );
        }

		if( gpsProvider == null && networkProvider == null )
			AppUtils.logger( 'e', TAG, "No providers available" );

        // set all the default values for the options HIGH, MEDIUM and LOW
		if( AppUtils.getLocationInterval( ac, AppConfig.SPREF_LOCATION_INTERVAL_HIGH ) == null )
			AppUtils.saveLocationInterval( ac,
					AppConfig.DEFAULT_LOCATION_INTERVAL_HIGH,
					AppConfig.SPREF_LOCATION_INTERVAL_HIGH );
		
		if( AppUtils.getLocationInterval( ac, AppConfig.SPREF_LOCATION_INTERVAL_MEDIUM ) == null )
			AppUtils.saveLocationInterval( ac,
					AppConfig.DEFAULT_LOCATION_INTERVAL_MEDIUM,
					AppConfig.SPREF_LOCATION_INTERVAL_MEDIUM );
		
		if( AppUtils.getLocationInterval( ac, AppConfig.SPREF_LOCATION_INTERVAL_LOW ) == null )
			AppUtils.saveLocationInterval( ac,
					AppConfig.DEFAULT_LOCATION_INTERVAL_LOW,
					AppConfig.SPREF_LOCATION_INTERVAL_LOW );
	}

    /**
     * Decide if new location is better than older by following some basic criteria.
     *
     * @param oldLocation Old location used for comparison.
     * @param newLocation Newly acquired location compared to old one.
     * @return If new location is more accurate and suits your criteria more than the old one.
     */
    private boolean isBetterLocation( Location oldLocation, Location newLocation ) {
        // If there is no old location, the new location is better
        if( oldLocation == null )
            return true;

        // Check if new location is newer in time
        long timeDelta = newLocation.getTime() - oldLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TIME_DIFFERENCE_THRESHOLD;
        boolean isSignificantlyOlder = timeDelta < -TIME_DIFFERENCE_THRESHOLD;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if( isSignificantlyNewer )
            return true;
        // If the new location is more than two minutes older, it must be worse
        else if( isSignificantlyOlder )
            return false;

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) ( newLocation.getAccuracy() - oldLocation.getAccuracy() );
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider( newLocation.getProvider(), oldLocation.getProvider() );

        // Determine location quality using a combination of timeliness and accuracy
        if( isMoreAccurate )
            return true;
        else if( isNewer && !isLessAccurate )
            return true;
        else if( isNewer && !isSignificantlyLessAccurate && isFromSameProvider )
            return true;

        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if( provider1 == null )
            return provider2 == null;
        return provider1.equals( provider2 );
    }

	/**
	 * Register/Unregister the Broadcast Receivers.
	 */
	private void registerBroadcasts() {
		IntentFilter filter = new IntentFilter();
		filter.addAction( BroadcastMessage.ACTION_CHANGE_LOCATION_INTERVAL );
		lbm.registerReceiver( mLocBroadcastReceiver, filter );
	}
	
	private void unregisterBroadcasts() {
        if( lbm != null )
		    lbm.unregisterReceiver( mLocBroadcastReceiver );
	}

    /**
	 * The broadcast receiver.
	 */
	private BroadcastReceiver mLocBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context c, Intent i) {
			String action = i.getAction();
			/* Broadcast: ACTION_CHANGE_LOCATION_INTERVAL */
			/* ****************************************** */
			if( action.equals( BroadcastMessage.ACTION_CHANGE_LOCATION_INTERVAL ) ) {
                // Verify if the energy manager is being used
                if( !AppUtils.getCurrentEnergyManager( ac ) )
                    return;

                // unregister the listener
				lm.removeUpdates( LocationService.this );
				// obtain the new value from the EXTRA 
				Integer newInterval = i.getIntExtra( BroadcastMessage.EXTRA_CHANGE_LOCATION_INTERVAL, -1 );
				// problem getting the value, set the default value 
				if( newInterval < 0 )
                    newInterval = AppConfig.DEFAULT_LOCATION_INTERVAL_HIGH;

                if( gpsProvider != null )
                    lm.requestLocationUpdates( gpsProvider,
                            newInterval * GPS_RATE,
                            AppConfig.DEFAULT_LOCATION_MIN_DISTANCE,
                            LocationService.this
                    );

                if( networkProvider != null )
                    lm.requestLocationUpdates( networkProvider,
                            newInterval,
                            AppConfig.DEFAULT_LOCATION_MIN_DISTANCE,
                            LocationService.this
                    );

                // Sets the new interval as the current one
                currentInterval = newInterval;
				// save the preferences with the new value 
				AppUtils.saveCurrentLocationInterval( ac, newInterval );
			}
		}
	};
}
