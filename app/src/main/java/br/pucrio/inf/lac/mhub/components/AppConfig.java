package br.pucrio.inf.lac.mhub.components;

/**
 * Configuration class with the default values for the project.
 * @author Luis Talavera
 */
public class AppConfig {
	/* DEBUG flag */ 
	public static final boolean DEBUG = true;

	/* ID of the shared preferences file. */
	public static final String SHARED_PREF_FILE = "MobileHubSharedPref";
	
	/**
	 * Keys used with the Shared Preferences (SP) and default values.
	 * {{ ======================================================================
	 */
	
	/* IP Address of the SDDL gateway
	 * type -- String
	 */
	public static final String SPREF_GATEWAY_IP_ADDRESS = "SPGatewayIpAddress";
	
	/* Port of the SDDL gateway
	 * type -- String
	 */
	public static final String SPREF_GATEWAY_PORT = "SPGatewayPort";
	
	/* The UUID of the user-device
	 * type -- String
	 */
	public static final String SPREF_USER_UUID = "SPUserUuid";
	
	/* The current interval used by the connection service to send messages.
	 * type -- Integer
	 */
	public static final String SPREF_CURRENT_MESSAGES_INTERVAL = "SPCurrentMessagesInterval";
	
	/* The interval values used by the connection service, this parameters are
	 * set manually from the application configuration.
	 */
	public static final String SPREF_MESSAGES_INTERVAL_HIGH   = "SPMessagesIntervalHigh";
	public static final String SPREF_MESSAGES_INTERVAL_MEDIUM = "SPMessagesIntervalMedium";
	public static final String SPREF_MESSAGES_INTERVAL_LOW    = "SPMessagesIntervalLow";
	
	/* The current interval used by the location service to send new locations.
	 * type -- Integer
	 */
	public static final String SPREF_CURRENT_LOCATION_INTERVAL = "SPCurrentLocationInterval";
	
	/* The interval values used by the location service, this parameters are
	 * set manually from the application configuration.
	 * type -- Integer
	 */
	public static final String SPREF_LOCATION_INTERVAL_HIGH   = "SPLocationIntervalHigh";
	public static final String SPREF_LOCATION_INTERVAL_MEDIUM = "SPLocationIntervalMedium";
	public static final String SPREF_LOCATION_INTERVAL_LOW    = "SPLocationIntervalLow";

    /* The current interval used by the S2PA service to start a scan for M-Objects.
	 * type -- Integer
	 */
    public static final String SPREF_CURRENT_SCAN_INTERVAL = "SPCurrentScanInterval";

	/* The interval values used by the location service, this parameters are
	 * set manually from the application configuration.
	 * type -- Integer
	 */
	public static final String SPREF_SCAN_INTERVAL_HIGH   = "SPScanIntervalHigh";
	public static final String SPREF_SCAN_INTERVAL_MEDIUM = "SPScanIntervalMedium";
	public static final String SPREF_SCAN_INTERVAL_LOW    = "SPScanIntervalLow";
	
	/* Connection status with the gateway.
	 * type -- Boolean
	 * 
	 * __Values__
	 * true  -- Connected 
	 * false -- Not connected
	 */
	public static final String SPREF_IS_CONNECTED = "SPIsConnected";
	
	/*
	 * If the M-Hub application should be started at boot
	 * type -- Boolean
	 */
	public static final String SPREF_START_BOOT = "SPStartBoot";

    /*
	 * Enable or disable the Energy Manager
	 * type -- Boolean
	 */
    public static final String SPREF_ENERGY_MANAGER = "SPEnergyManager";

    /*
	 * Enable or disable the MEPA Service
	 * type -- Boolean
	 */
    public static final String SPREF_MEPA_SERVICE = "SPMEPAService";

    /*
	 * Enable or disable the Location Service
	 * type -- Boolean
	 */
    public static final String SPREF_LOCATION_SERVICE = "SPLocationService";

    /*
	 * Mobile Hub Latitude, value for a fix location
	 * type -- Double
	 */
    public static final String SPREF_LOCATION_LATITUDE = "SPLocationLatitude";

    /*
	 * Mobile Hub Longitude, value for a fix location
	 * type -- Boolean
	 */
    public static final String SPREF_LOCATION_LONGITUDE = "SPLocationLongitude";
	
	/*
	 * Auto connection with the mobile objects after they are found
	 * type -- Boolean
	 */
	public static final String SPREF_AUTOCONNECT_MO = "SPAutoConnectMO";
	
	/* Threshold of the range (RSSI) accepted for M-Objects
	 * type -- Integer
	 * */
	public static final String SPREF_SINGAL_RANGE_MO = "SPSignalRangeMO";
	
	/**
	 * }} ======================================================================
	 */
	
	/**
	 * Default values
	 * {{ ======================================================================
	 */

    /* The range used by the HIGH, MEDIUM and LOW values. this is by default
	 * the battery life.
	 *
	 * HIGH   :: between 100% and 70%
	 * MEDIUM :: between 70% and 40%
	 * LOW    :: between 40% and 0% // Not needed (else)
	 */
    public static final int DEFAULT_HIGH_VALUE   = 70;
    public static final int DEFAULT_MEDIUM_VALUE = 40;
	
	/* Default interval values to scan mobile objects (milliseconds), it is used by
	 * the s2pa service.
	 * 
	 * HIGH   :: 20 seconds
	 * MEDIUM :: 40 seconds
	 * LOW    :: 1 minute
	 */
	public static final int DEFAULT_SCAN_INTERVAL_HIGH   = 1000 * 20;
	public static final int DEFAULT_SCAN_INTERVAL_MEDIUM = 1000 * 40;
	public static final int DEFAULT_SCAN_INTERVAL_LOW    = 1000 * 60;
	
	/* Default interval values to send messages (milliseconds), it is used by
	 * the connection service.
	 * 
	 * HIGH   ::  30 seconds
	 * MEDIUM ::  1 minute
	 * LOW    ::  2 minutes
	 */
	public static final int DEFAULT_MESSAGES_INTERVAL_HIGH   = 1000 * 30;
	public static final int DEFAULT_MESSAGES_INTERVAL_MEDIUM = 1000 * 60;
	public static final int DEFAULT_MESSAGES_INTERVAL_LOW    = 1000 * 60 * 2;
	
	/*
	 * Default interval values to get a new location (milliseconds), it is used by
	 * the location service.
	 * 
	 * HIGH   ::   1 minute
	 * MEDIUM ::   4 minutes
	 * LOW    ::  10 minutes
	 */
	public static final int DEFAULT_LOCATION_INTERVAL_HIGH   = 1000 * 60;
	public static final int DEFAULT_LOCATION_INTERVAL_MEDIUM = 1000 * 60 * 4;
	public static final int DEFAULT_LOCATION_INTERVAL_LOW    = 1000 * 60 * 10;
	
	/*
	 * Default value of delay to start the first scan (milliseconds)
	 * 
	 * Default: 1 second
	 */
	public static final Integer DEFAULT_DELAY_SCAN_PERIOD = 1000;
	
	/*
	 * Default value of scan period (milliseconds)
	 * 
	 * Default: 2 seconds
	 */
	public static final Integer DEFAULT_SCAN_PERIOD = 1000 * 2;
	
	/*
	 * Default value to get a new location when the distance is greater than
	 * this value. ( 0 = disabled )
	 * 
	 * Default: 0 meters
	 */
	public static final Integer DEFAULT_LOCATION_MIN_DISTANCE = 0;
	
	/*
	 * Default value to start at boot
	 * 
	 * Default: false
	 */
	public static final Boolean DEFAULT_START_BOOT = false;

    /*
	 * Default use of energy manager
	 *
	 * Default: false
	 */
    public static final Boolean DEFAULT_ENERGY_MANAGER = false;

    /*
	 * Default use of MEPA service
	 *
	 * Default: false
	 */
    public static final Boolean DEFAULT_MEPA_SERVICE = false;

    /*
	 * Default use of location service
	 *
	 * Default: false
	 */
    public static final Boolean DEFAULT_LOCATION_SERVICE = false;

	/*
	 * Default value for the auto connect to mobile objects
	 * 
	 * Default: true
	 */
	public static final Boolean DEFAULT_AUTO_CONNECT_MO = true;
	
	/*
	 * Default value for the signal allowed to connect to mobile objects
	 * 
	 * Default: -90 (high range, worst signal = -100)
	 */
	public static final Integer DEFAULT_SIGNAL_RANGE_MO = -90;
	
	/*
	 * Default value for the ip address, the first address that the device
	 * will connect to.
	 * 
	 * Default: onDevelopment --// not set //--
	 */
	public static final String DEFAULT_SDDL_IP_ADDRESS = "139.82.100.71";
	
	/*
	 * Default value for the port used by the SDDL.
	 * 
	 * Default: 5500
	 */
	public static final Integer DEFAULT_SDDL_PORT = 5500;
	
	/**
	 * }} ======================================================================
	 */
}
