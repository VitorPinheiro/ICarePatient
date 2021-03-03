package br.pucrio.inf.lac.mhub.components;

import android.app.ActivityManager;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.pucrio.inf.lac.mhub.models.base.LocalMessage;
import br.pucrio.inf.lac.mhub.models.locals.MessageData;
import br.pucrio.inf.lac.mhub.services.ConnectionService;
import de.greenrobot.event.EventBus;

/**
 * The utilities used by the application.
 * 
 * @author Luis Talavera
 */
public class AppUtils {
    /** DEBUG */
    private static final String TAG = AppUtils.class.getSimpleName();

    /** Property separator, used to separate the route tag */
	private static final String PROP_SEP = "\\|";
	
	/**
	 * It checks if is a valid IP address.
	 * 
	 * @param s The IP address string.
	 * @return true  If the pattern is valid.
	 *         false If the pattern fails.
	 */
	public static Boolean isValidIp(String s) {
		String PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		                  "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		                  "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		                  "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		Pattern pattern = Pattern.compile( PATTERN );
	    Matcher matcher = pattern.matcher( s );
	    return matcher.matches();
	}
	
	/**
	 * It checks if is a valid port range between 1 and 65535.
	 * 
	 * @param s The gateway port string.
	 * @return true  A valid port.
	 *         false A not valid port.
	 */
	public static Boolean isValidPort(String s) {
		if( !isNumber( s ) )
			return false;
		else {
			int port = Integer.parseInt( s );
			if( port >= 1 && port <= 65535 )
				return true;
		}
		return false;
	}
	
	/**
	 * A simple check to see if a string is a valid number before inserting
	 * into the shared preferences.
	 * 
	 * @param s The number to be checked.
	 * @return true  It is a number.
	 *         false It is not a number.
	 */
	public static Boolean isNumber(String s) {
		try {
            Integer.parseInt( s );
        }
		catch(NumberFormatException e) {
			return false;			
		}
		return true;
	}

    /**
     * A simple check to see if a string is a valid double before inserting
     * into the shared preferences.
     *
     * @param s The number to be checked.
     * @return true  It is a number.
     *         false It is not a number.
     */
    public static Boolean isDouble(String s) {
        try {
            Double.parseDouble( s );
        }
        catch(NumberFormatException e) {
            return false;
        }
        return true;
    }
	
	/**
	 * A helper class just o obtain the config file for the Shared Preferences
	 * using the default values for this Shared Preferences app.
	 * 
	 * @param c The Context of the Android system.
	 * @return Returns the shared preferences with the default values.
	 */
	private static SharedPreferences getSPrefConfig(Context c) {
		return c.getSharedPreferences( AppConfig.SHARED_PREF_FILE, Context.MODE_PRIVATE );
	}

    /**
     * Clears the Preferences
     * @param c The Context of the Android system.
     * @return If the operation was successful
     */
    public static boolean trimPreferences(Context c) {
        return getSPrefConfig( c ).edit().clear().commit();
    }
	
	/**
	 * It saves the IP address inside the shared preferences.
	 * 
	 * @param c The Context of the Android system.
	 * @param s The gateway IP address.
	 * @return true  It returns true if is a valid IP and it was saved.
	 *         false the IP is not valid and it was not saved.
	 */
	public static Boolean saveIpAddress(Context c, String s) {
		if( isValidIp( s ) ) {
			SharedPreferences config = getSPrefConfig( c );
			SharedPreferences.Editor writer = config.edit();
			writer.putString( AppConfig.SPREF_GATEWAY_IP_ADDRESS, s );
			return writer.commit();
		}
		return false;
	}
	
	/**
	 * It gets the IP address from the Shared Preferences.
	 * 
	 * @param c The Context of the Android system.
	 * @return String A valid IP address.
	 *         null   If there is not a valid IP address or the key has no
	 *                   value.
	 */
	public static String getIpAddress(Context c) {
		SharedPreferences config = getSPrefConfig( c );
		return config.getString( AppConfig.SPREF_GATEWAY_IP_ADDRESS, AppConfig.DEFAULT_SDDL_IP_ADDRESS );
	}
	
	/**
	 * It saves the port of the gateway to the Shared Preferences.
	 * 
	 * @param c The Context of the Android system.
	 * @param n The gateway port.
	 * @return true  If it is a valid port and it was saved.
	 *         false It is not a valid port and it was not saved.
	 */
	public static Boolean saveGatewayPort (Context c, Integer n) {
		if( isValidPort( n.toString() ) ) {
			SharedPreferences config = getSPrefConfig( c );
			SharedPreferences.Editor writer = config.edit();
			writer.putString( AppConfig.SPREF_GATEWAY_PORT, n.toString() );
			
			return writer.commit();
		}
		return false;
	}
	
	/**
	 * It gets the gateway port.
	 * 
	 * @param c The Context of the Android system.
	 * @return Integer It returns the gateway port as an Integer.
	 *         null    It has no value saved or an invalid port.
	 */
	public static Integer getGatewayPort(Context c) {
		SharedPreferences config = getSPrefConfig( c );
		String port = config.getString( AppConfig.SPREF_GATEWAY_PORT, AppConfig.DEFAULT_SDDL_PORT + "" );
		
		if( !isNumber( port ) || port.equals( "" ) )
			return null;
		return Integer.parseInt( port );
	}
	
	/**
	 * It generates a random UUID for the Android device.
	 * 
	 * @return It returns the generated UUID.
	 */
	private static UUID generateUuid() {
		return UUID.randomUUID();
	}
	
	/**
	 * It creates a new random UUID to be used on the device and saves it to
	 * Shared Preferences to be used again as an ID.
	 * 
	 * @param c The Context of the Android system.
	 * @return true  It was saved successfully.
	 *         false It was not saved successfully.
	 */
	public static Boolean createSaveUuid(Context c) {
		String strUuid = generateUuid().toString();
		SharedPreferences config = getSPrefConfig( c );
		SharedPreferences.Editor writer = config.edit();
		writer.putString( AppConfig.SPREF_USER_UUID, strUuid );
		
		return writer.commit();
	}
	
	/**
	 * It gets the UUID saved inside the Shared Preferences.
	 * 
	 * @param c The Context of the Android system.
	 * @return UUID It returns the UUID saved.
	 *         null There is no UUID save inside the Shared Preferences.
	 */
	public static UUID getUuid(Context c) {
		SharedPreferences config = getSPrefConfig( c );
		String strUuid = config.getString( AppConfig.SPREF_USER_UUID, "" );
		
		if( strUuid.equals( "" ) )
			return null;
		return UUID.fromString( strUuid );
	}
	
	/**
	 * It saves the status of the connection.
	 * 
	 * @param c The Context of the Android system.
	 * @param flag The status of the connection.
	 * @return true  The flag was saved successfully.
	 *         false The flag was not saved successfully.
	 */
	public static Boolean saveIsConnected(Context c, Boolean flag) {
		SharedPreferences config = getSPrefConfig( c );
		SharedPreferences.Editor writer = config.edit();
		writer.putBoolean( AppConfig.SPREF_IS_CONNECTED, flag );
		
		return writer.commit();
	}
	
	/**
	 * It gets the status of the connection.
	 * 
	 * @param c The Context of the Android system.
	 * @return true  It has a live connection with the gateway.
	 *         false It does not have a live connection with the gateway.
	 */
	public static Boolean isConnected(Context c) {
		SharedPreferences config = getSPrefConfig( c );
        return config.getBoolean( AppConfig.SPREF_IS_CONNECTED, false );
	}
	
	/**
	 * It saves the current interval between messages to be send to the Gateway.
	 * 
	 * @param c The Context of the Android system.
	 * @param n The value between messages to be send.
	 * @return true  If the param was saved.
	 *         false If the param was not saved.
	 */
	public static Boolean saveCurrentSendMessagesInterval(Context c, Integer n) {
		SharedPreferences config = getSPrefConfig( c );
		SharedPreferences.Editor writer = config.edit();
		writer.putString( AppConfig.SPREF_CURRENT_MESSAGES_INTERVAL, n.toString() );
		
		return writer.commit();
	}
	
	/**
	 * It gets the interval between messages.
	 * 
	 * @param c The Context of the Android system.
	 * @return Integer The value.
	 *         null    If it was not possible to get the value.
	 */
	public static Integer getCurrentSendMessagesInterval(Context c) {
		SharedPreferences config = getSPrefConfig( c );
		String s = config.getString( AppConfig.SPREF_CURRENT_MESSAGES_INTERVAL, "" );
		
		if( s.equals( "" ) || !isNumber( s ) )
			return null;
		return Integer.parseInt( s );
	}
	
	/**
	 * It saves the interval between messages to be send to the Gateway.
	 * 
	 * @param c The Context of the Android system.
	 * @param n The value between messages to be send.
	 * @param flag The choice between the three HIGH, MEDIUM or LOW
	 * @return true  If the param was saved.
	 *         false If the param was not saved.
	 */
	public static Boolean saveSendSignalsInterval(Context c, Integer n, String flag) {
		SharedPreferences config = getSPrefConfig( c );
		SharedPreferences.Editor writer = config.edit();

        switch( flag ) {
            /* HIGH value */
            case AppConfig.SPREF_MESSAGES_INTERVAL_HIGH:
                writer.putString( AppConfig.SPREF_MESSAGES_INTERVAL_HIGH, n.toString() );
                break;

		    /* MEDIUM value */
            case AppConfig.SPREF_MESSAGES_INTERVAL_MEDIUM:
                writer.putString( AppConfig.SPREF_MESSAGES_INTERVAL_MEDIUM, n.toString() );
                break;

		    /* LOW value */
            case AppConfig.SPREF_MESSAGES_INTERVAL_LOW:
                writer.putString( AppConfig.SPREF_MESSAGES_INTERVAL_LOW, n.toString() );
                break;
        }
		
		return writer.commit();
	}
	
	/**
	 * It gets the interval values between messages from the choices HIGH, MEDIUM or LOW.
	 * 
	 * @param c The Context of the Android system.
	 * @param flag The choice between the three HIGH, MEDIUM or LOW
	 * @return Integer The value.
	 *         null    If it was not possible to get the value.
	 */
	public static Integer getSendSignalsInterval(Context c, String flag) {
		SharedPreferences config = getSPrefConfig(c);
		String s = "";

        switch( flag ) {
            /* HIGH value */
            case AppConfig.SPREF_MESSAGES_INTERVAL_HIGH:
                s = config.getString( AppConfig.SPREF_MESSAGES_INTERVAL_HIGH, AppConfig.DEFAULT_MESSAGES_INTERVAL_HIGH + "");
                break;

		    /* MEDIUM value */
            case AppConfig.SPREF_MESSAGES_INTERVAL_MEDIUM:
                s = config.getString( AppConfig.SPREF_MESSAGES_INTERVAL_MEDIUM, AppConfig.DEFAULT_MESSAGES_INTERVAL_MEDIUM + "");
                break;

		    /* LOW value */
            case AppConfig.SPREF_MESSAGES_INTERVAL_LOW:
                s = config.getString( AppConfig.SPREF_MESSAGES_INTERVAL_LOW, AppConfig.DEFAULT_MESSAGES_INTERVAL_LOW + "");
                break;
        }
		
		if( s.equals( "" ) || !isNumber( s ) )
			return null;
		return Integer.parseInt( s );
	}
	
	/**
	 * It saves the location min interval.
	 * 
	 * @param c The Context of the Android system.
	 * @param n The new min value.
	 * @return true If it was saved.
	 *         false If it was not saved.
	 */
	public static Boolean saveCurrentLocationInterval(Context c, Integer n) {
		SharedPreferences config = getSPrefConfig( c );
		SharedPreferences.Editor writer = config.edit();
		writer.putString( AppConfig.SPREF_CURRENT_LOCATION_INTERVAL, n.toString() );
		return writer.commit();
	}
	
	/**
	 * It gets the location min interval value.
	 * 
	 * @param c The Context of the Android system.
	 * @return Integer The value.
	 *         null    If it was not possible to get the value.
	 */
	public static Integer getCurrentLocationInterval(Context c) {
		SharedPreferences config = getSPrefConfig( c );
		String s = config.getString( AppConfig.SPREF_CURRENT_LOCATION_INTERVAL, "" );
		
		if( s.equals( "" ) || !isNumber( s ) )
			return null;
		return Integer.parseInt( s );
	}
	
	/**
	 * It saves the scan min interval.
	 * 
	 * @param c The Context of the Android system.
	 * @param n The new min value.
	 * @return true If it was saved.
	 *         false If it was not saved.
	 */
	public static Boolean saveCurrentScanInterval(Context c, Integer n) {
		SharedPreferences config = getSPrefConfig( c );
		SharedPreferences.Editor writer = config.edit();
		writer.putString( AppConfig.SPREF_CURRENT_SCAN_INTERVAL, n.toString() );
		return writer.commit();
	}
	
	/**
	 * It gets the current scan interval value.
	 * 
	 * @param c The Context of the Android system.
	 * @return Integer The value.
	 *         null    If it was not possible to get the value.
	 */
	public static Integer getCurrentScanInterval(Context c) {
		SharedPreferences config = getSPrefConfig( c );
		String s = config.getString( AppConfig.SPREF_CURRENT_SCAN_INTERVAL, "" );
		
		if( s.equals( "" ) || !isNumber( s ) )
			return null;
		return Integer.parseInt( s );
	}
	
	/**
	 * It saves the location min interval for the three values, HIGH, MEDIUM or LOW.
	 * 
	 * @param c The Context of the Android system.
	 * @param n The new min value.
	 * @param flag The choice between the three HIGH, MEDIUM or LOW
	 * @return true If it was saved.
	 *         false If it was not saved.
	 */
	public static Boolean saveLocationInterval(Context c, Integer n, String flag) {
		SharedPreferences config = getSPrefConfig( c );
		SharedPreferences.Editor writer = config.edit();

        switch( flag ) {
            /* HIGH value */
            case AppConfig.SPREF_LOCATION_INTERVAL_HIGH:
                writer.putString( AppConfig.SPREF_LOCATION_INTERVAL_HIGH, n.toString() );
                break;

		    /* MEDIUM value */
            case AppConfig.SPREF_LOCATION_INTERVAL_MEDIUM:
                writer.putString( AppConfig.SPREF_LOCATION_INTERVAL_MEDIUM, n.toString() );
                break;

		    /* LOW value */
            case AppConfig.SPREF_LOCATION_INTERVAL_LOW:
                writer.putString( AppConfig.SPREF_LOCATION_INTERVAL_LOW, n.toString() );
                break;
        }
		
		return writer.commit();
	}
	
	/**
	 * It gets the location min interval value for the three options HIGH, MEDIUM or LOW.
	 * 
	 * @param c The Context of the Android system.
	 * @param flag The choice between the three HIGH, MEDIUM or LOW
	 * @return Integer The value.
	 *         null    If it was not possible to get the value.
	 */
	public static Integer getLocationInterval(Context c, String flag) {
		SharedPreferences config = getSPrefConfig( c );
		String s = "";
		
        switch (flag) {
            /* HIGH value*/
            case AppConfig.SPREF_LOCATION_INTERVAL_HIGH:
                s = config.getString( AppConfig.SPREF_LOCATION_INTERVAL_HIGH, AppConfig.DEFAULT_LOCATION_INTERVAL_HIGH + "" );
                break;

		    /* MEDIUM value */
            case AppConfig.SPREF_LOCATION_INTERVAL_MEDIUM:
                s = config.getString( AppConfig.SPREF_LOCATION_INTERVAL_MEDIUM, AppConfig.DEFAULT_LOCATION_INTERVAL_MEDIUM + "" );
                break;

		    /* LOW value */
            case AppConfig.SPREF_LOCATION_INTERVAL_LOW:
                s = config.getString( AppConfig.SPREF_LOCATION_INTERVAL_LOW, AppConfig.DEFAULT_LOCATION_INTERVAL_LOW + "" );
                break;
        }
		
		if( s.equals( "" ) || !isNumber( s ) )
			return null;
		return Integer.parseInt( s );
	}
	
	/**
	 * It saves the scan min interval for the three values, HIGH, MEDIUM or LOW.
	 * 
	 * @param c The Context of the Android system.
	 * @param n The new min value.
	 * @param flag The choice between the three HIGH, MEDIUM or LOW
	 * @return true If it was saved.
	 *         false If it was not saved.
	 */
	public static Boolean saveScanInterval(Context c, Integer n, String flag) {
		SharedPreferences config = getSPrefConfig( c );
		SharedPreferences.Editor writer = config.edit();

        switch( flag ) {
            /* HIGH value */
            case AppConfig.SPREF_SCAN_INTERVAL_HIGH:
                writer.putString( AppConfig.SPREF_SCAN_INTERVAL_HIGH, n.toString() );
                break;

		    /* MEDIUM value */
            case AppConfig.SPREF_SCAN_INTERVAL_MEDIUM:
                writer.putString( AppConfig.SPREF_SCAN_INTERVAL_MEDIUM, n.toString() );
                break;

		    /* LOW value */
            case AppConfig.SPREF_SCAN_INTERVAL_LOW:
                writer.putString( AppConfig.SPREF_SCAN_INTERVAL_LOW, n.toString() );
                break;
        }
		
		return writer.commit();
	}
	
	/**
	 * It gets the scan min interval value for the three options HIGH, MEDIUM or LOW.
	 * 
	 * @param c The Context of the Android system.
	 * @param flag The choice between the three HIGH, MEDIUM or LOW
	 * @return Integer The value.
	 *         null    If it was not possible to get the value.
	 */
	public static Integer getScanInterval(Context c, String flag) {
		SharedPreferences config = getSPrefConfig( c );
		String s = "";

        switch( flag ) {
            /* HIGH value*/
            case AppConfig.SPREF_SCAN_INTERVAL_HIGH:
                s = config.getString( AppConfig.SPREF_SCAN_INTERVAL_HIGH, AppConfig.DEFAULT_SCAN_INTERVAL_HIGH + "" );
                break;

		    /* MEDIUM value */
            case AppConfig.SPREF_SCAN_INTERVAL_MEDIUM:
                s = config.getString( AppConfig.SPREF_SCAN_INTERVAL_MEDIUM, AppConfig.DEFAULT_SCAN_INTERVAL_MEDIUM + "" );
                break;

		    /* LOW value */
            case AppConfig.SPREF_SCAN_INTERVAL_LOW:
                s = config.getString( AppConfig.SPREF_SCAN_INTERVAL_LOW, AppConfig.DEFAULT_SCAN_INTERVAL_LOW + "" );
                break;
        }
		
		if( s.equals( "" ) || !isNumber( s ) )
			return null;
		return Integer.parseInt( s );
	}
	
	/**
	 * It gets the current start at boot state.
	 * 
	 * @param c The Context of the Android system.
	 * @return Boolean The value.
	 */
	public static Boolean getCurrentStartAtBoot(Context c) {
		SharedPreferences config = getSPrefConfig( c );
        return config.getBoolean( AppConfig.SPREF_START_BOOT, AppConfig.DEFAULT_START_BOOT );
	}

    /**
     * It saves if the start at boot is enabled or disable
     * @param c The Context of the Android system.
     * @param flag The status of the start at boot.
     * @return true If it was saved.
     *         false If it was not saved.
     */
    public static Boolean saveStartAtBoot(Context c, Boolean flag) {
        SharedPreferences config = getSPrefConfig( c );
        SharedPreferences.Editor writer = config.edit();
        writer.putBoolean( AppConfig.SPREF_START_BOOT, flag );
        return writer.commit();
    }

    /**
     * It gets the current energy manager state.
     *
     * @param c The Context of the Android system.
     * @return Boolean The value.
     */
    public static Boolean getCurrentEnergyManager(Context c) {
        SharedPreferences config = getSPrefConfig( c );
        return config.getBoolean( AppConfig.SPREF_ENERGY_MANAGER, AppConfig.DEFAULT_ENERGY_MANAGER );
    }

    /**
     * It saves if the energy manager is enabled or disable
     * @param c The Context of the Android system.
     * @param flag The status of the energy manager.
     * @return true If it was saved.
     *         false If it was not saved.
     */
    public static boolean saveEnergyManager(Context c, Boolean flag) {
        SharedPreferences config = getSPrefConfig( c );
        SharedPreferences.Editor writer = config.edit();
        writer.putBoolean( AppConfig.SPREF_ENERGY_MANAGER, flag );
        return writer.commit();
    }

    /**
     * It gets the current MEPA service state.
     *
     * @param c The Context of the Android system.
     * @return Boolean The value.
     */
    public static Boolean getCurrentMEPAService(Context c) {
        SharedPreferences config = getSPrefConfig( c );
        return config.getBoolean( AppConfig.SPREF_MEPA_SERVICE, AppConfig.DEFAULT_MEPA_SERVICE );
    }

    /**
     * It gets the current location service state.
     *
     * @param c The Context of the Android system.
     * @return Boolean The value.
     */
    public static Boolean getCurrentLocationService(Context c) {
        SharedPreferences config = getSPrefConfig( c );
        return config.getBoolean( AppConfig.SPREF_LOCATION_SERVICE, AppConfig.DEFAULT_LOCATION_SERVICE );
    }

    /**
     * It gets the current latitude.
     *
     * @param c The Context of the Android system.
     * @return Double The value.
     */
    public static Double getLocationLatitude(Context c) {
        SharedPreferences config = getSPrefConfig( c );
        String s = config.getString( AppConfig.SPREF_LOCATION_LATITUDE, "" );

        if( s.equals( "" ) || !isDouble( s ) )
            return null;
        return Double.parseDouble( s );
    }

    /**
     * It gets the current latitude.
     *
     * @param c The Context of the Android system.
     * @return Double The value.
     */
    public static Double getLocationLongitude(Context c) {
        SharedPreferences config = getSPrefConfig( c );
        String s = config.getString( AppConfig.SPREF_LOCATION_LONGITUDE, "" );

        if( s.equals( "" ) || !isDouble( s ) )
            return null;
        return Double.parseDouble( s );
    }
	
	/**
	 * It gets the current auto connect to mobile objects state.
	 * 
	 * @param c The Context of the Android system.
	 * @return Boolean The value.
	 */
	public static Boolean getCurrentAutoconnectMO(Context c) {
        SharedPreferences config = getSPrefConfig( c );
        return config.getBoolean( AppConfig.SPREF_AUTOCONNECT_MO, AppConfig.DEFAULT_AUTO_CONNECT_MO );
    }
	
	/**
	 * It gets the current minimum range accepted to connect to mobile objects.
	 * 
	 * @param c The Context of the Android system.
	 * @return The value.
	 */
	public static Integer getCurrentSignalAllowedMO(Context c) {
		SharedPreferences config = getSPrefConfig( c );
        return config.getInt( AppConfig.SPREF_SINGAL_RANGE_MO, AppConfig.DEFAULT_SIGNAL_RANGE_MO );
	}
	
	/**
	 * Look for the service tag in the property
	 * @param serviceName The service name to check
	 * @param property The allowed services
	 * @return If the service is allowed
	 */
	public static boolean isInRoute(String serviceName, String property) {
		String[] parts = property.split( PROP_SEP );
		
		for( String service : parts ) {
			if( service.equals( serviceName ) )
				return true;
		}
		
		return false;
	}
	
	/**
     * Verify if a service is running
     * @param c The Context of the Android system.
     * @param serviceName The name of the service.
     * @return Boolean true if is running otherwise false
     */
    public static boolean isMyServiceRunning(Context c, String serviceName) {
        ActivityManager manager = (ActivityManager) c.getSystemService( Context.ACTIVITY_SERVICE );
        for( ActivityManager.RunningServiceInfo service : manager.getRunningServices( Integer.MAX_VALUE ) )  {
            if( serviceName.equals( service.service.getClassName() ) )
                return true;
        }
        return false;
    }

    /**
     * Sends an error message to the Connection Service to be send to the cloud
     * @param tag The component that triggered the error (String)
     * @param message The error message (String)
     */
    public static void sendErrorMessage( final String tag, final String message ) {
		sendMessage( MessageData.TYPE.ERROR, tag, message );
    }

	/**
	 * Sends an message to the Connection Service to be send to the cloud
	 * @param tag The component that triggered the error (String)
	 * @param message The message (String)
	 */
	public static void sendMessage( MessageData.TYPE type, final String tag, final String message ) {
		MessageData msg = new MessageData( type );
		msg.setComponent( tag );
		msg.setMessage( message );

		msg.setRoute( ConnectionService.ROUTE_TAG );
		msg.setPriority( LocalMessage.HIGH );

		EventBus.getDefault().post( msg );
		AppUtils.logger( 'e', TAG, msg.toString() );
	}

    /**
     * Connection states for BLE as String
     * @param status The integer state
     * @return The state as String
     */
    public static String connectionState(final int status) {
        switch( status ) {
            case BluetoothProfile.STATE_CONNECTED:
                return "Connected";
            case BluetoothProfile.STATE_DISCONNECTED:
                return "Disconnected";
            case BluetoothProfile.STATE_CONNECTING:
                return "Connecting";
            case BluetoothProfile.STATE_DISCONNECTING:
                return "Disconnecting";
            default:
                return String.valueOf( status );
        }
    }

    /**
     * Gatt states as String
     * @param status The integer state
     * @return The state as String
     */
    public static String gattState(final int status) {
        switch( status ) {
            case BluetoothGatt.GATT_SUCCESS:
                return "GATT Success";
            case BluetoothGatt.GATT_FAILURE:
                return "GATT Failure";
            default:
                return String.valueOf( status );
        }
    }

    /**
     * Clears the cache
     * @param context The Context of the Android system.
     */
    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if( dir != null && dir.isDirectory() ) {
                AppUtils.logger( 'd', TAG, dir.getPath() );
                deleteDir( dir );
            }

             dir = context.getExternalCacheDir();
            if( dir != null && dir.isDirectory() ) {
                AppUtils.logger( 'd', TAG, dir.getPath() );
                deleteDir( dir );
            }
        } catch(Exception e) {
            AppUtils.logger( 'e', TAG, e.getMessage() );
        }
    }

    /**
     * Deletes a directory
     * @param dir The directory to delete
     * @return If it was deleted successfully
     */
    public static boolean deleteDir(File dir) {
        if( dir != null && dir.isDirectory() ) {
            String[] children = dir.list();
            for( String aChildren : children ) {
                boolean success = deleteDir( new File( dir, aChildren ) );
                if( !success )
                    return false;
            }

            return dir.delete();
        }
        return false;
    }

    /**
     * Gets an element in its String representation
     * @param obj The object to be transformed
     * @return The String representation
     */
    public static String valueOf(Object obj) {
        return (obj == null) ? "null" : obj.toString();
    }

	/**
	 * Transforms a Map to a JSONArray structure
	 * @param data the Map with the data
	 * @return a JSONArray with the values of the data (key - value)
	 * @throws NullPointerException
	 */
	public static JSONArray mapToJSONArray( Map<?, ?> data ) throws NullPointerException {
		JSONArray properties = new JSONArray();
		for( Map.Entry<?, ?> entry : data.entrySet() ) {
			JSONArray property = new JSONArray();

			String key = (String) entry.getKey();
			Object value = entry.getValue();

			if( key == null ) throw new NullPointerException( "key == null" );

			property.put( key );
			if( value instanceof String )
				property.put( value );
			else if( value instanceof Class )
				property.put( ((Class) value).getName() );

			properties.put( property );
		}
		return properties;
	}

	/**
	 * Gyroscope, Magnetometer, Barometer, IR temperature all store 16 bit two's complement values in the awkward format LSB MSB, which cannot be directly parsed
	 * as getIntValue(FORMAT_SINT16, offset) because the bytes are stored in the "wrong" direction.
	 *
	 * This function extracts these 16 bit two's complement values.
	 **/
	public static Integer shortSignedAtOffset( byte[] c, int offset ) {
		Integer lowerByte = (int) c[offset] & 0xFF;
		Integer upperByte = (int) c[offset + 1]; // // Interpret MSB as signed
		return (upperByte << 8) + lowerByte;
	}

	public static Integer shortUnsignedAtOffset( byte[] c, int offset ) {
		Integer lowerByte = (int) c[offset] & 0xFF;
		Integer upperByte = (int) c[offset + 1] & 0xFF; // // Interpret MSB as signed
		return (upperByte << 8) + lowerByte;
	}

	public static Integer twentyFourBitUnsignedAtOffset( byte[] c, int offset ) {
		Integer lowerByte = (int) c[offset] & 0xFF;
		Integer mediumByte = (int) c[offset+1] & 0xFF;
		Integer upperByte = (int) c[offset + 2] & 0xFF;
		return (upperByte << 16) + (mediumByte << 8) + lowerByte;
	}

	/**
	 * Transforms bytes to String
	 * @param data bytes
	 * @return String
	 */
	public static String bytesToHex(byte[] data) {
		if( data == null )
			return null;

		String str = "";
		for( byte aData : data ) {
			if( ( aData & 0xFF ) < 16 )
				str = str + "0" + Integer.toHexString( aData & 0xFF );
			else
				str = str + Integer.toHexString( aData & 0xFF );
		}
		return str;
	}

    /**
	 * Logger for the service, depending on the flag DEBUG
	 * @param type The char type of the log
	 * @param TAG The String used to know to which class the log belongs
	 * @param text The String to output on the log
	 */
	public static void logger(char type, final String TAG, final String text) {
		if( text == null ) {
            Log.e( TAG, "NULL Message" );
            return;
        }
			
    	if( AppConfig.DEBUG ) {
    		switch( type ) {
    			case 'i': // Information
    				Log.i( TAG, text );
    				break;
    				
    			case 'w': // Warning
    				Log.w( TAG, text );
    				break;
    				
    			case 'e': // Error
    				Log.e( TAG, text );
    				break;
    				
    			case 'd': // Debug
    				Log.d( TAG, text );
    				break;
    				
    			default:
    				Log.e( TAG, text );
    				break;
    		}
    	}
    }
}
