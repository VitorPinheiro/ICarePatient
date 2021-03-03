package br.pucrio.inf.lac.mhub.broadcastreceivers;

/**
 * All the messages that the broadcast receivers exchange.
 * @author Luis Talavera
 */
public class BroadcastMessage {
	/**
	 * It is the ID of the application (the package) used in all the broadcast
	 * messages, to prevent conflict problems from others broadcasts.
	 */
	private static final String BROADCAST_APPID
		= BroadcastMessage.class.getClass().getPackage().getName() + ".";
	
	/**
	 * It is used to tell the location service to change the update rate.
	 * EXTRA - The new location min time.
	 */
	public static final String ACTION_CHANGE_LOCATION_INTERVAL
		= BROADCAST_APPID + "ActionChangeLocationInterval";
	public static final String EXTRA_CHANGE_LOCATION_INTERVAL
		= BROADCAST_APPID + "ExtraChangeLocationInterval";
	
	/**
	 * It is used to tell the scan to change the update rate.
	 * EXTRA - The new scan min time.
	 */
	public static final String ACTION_CHANGE_SCAN_INTERVAL
		= BROADCAST_APPID + "ActionChangeScanInterval";
	public static final String EXTRA_CHANGE_SCAN_INTERVAL
		= BROADCAST_APPID + "ExtraChangeScanInterval";
	
	/**
	 * It is used to tell the connection service to change the interval
	 * between messages to be sent.
	 * EXTRA - The new send message interval.
	 */
	public static final String ACTION_CHANGE_MESSAGES_INTERVAL
		= BROADCAST_APPID + "ActionChangeMessagesInterval";
	public static final String EXTRA_CHANGE_MESSAGES_INTERVAL
		= BROADCAST_APPID + "ExtraChangeMessagesInterval";
	
	/**
	 * It is used to inform the connection service that the connectivity has
	 * changed from 3g to wifi or vice-versa.
	 * EXTRA - The type of the connection.
	 */
	public static final String ACTION_CONNECTIVITY_CHANGED
		= BROADCAST_APPID + "ActionConnectivityChanged";
	public static final String EXTRA_CONNECTIVITY_CHANGED
		= BROADCAST_APPID + "ExtraConnectivityChanged";
	
	/** The info types */
	public static final String INFO_CONNECTIVITY_NO_CONNECTION
	= BROADCAST_APPID + "InfoConnectivityNoConnection";
	public static final String INFO_CONNECTIVITY_3G
	= BROADCAST_APPID + "InfoConnectivity3G";
	public static final String INFO_CONNECTIVITY_WIFI
	= BROADCAST_APPID + "InfoConnectivityWifi";
	
	/**
	 * It is used by the battery receiver to check for battery level.
	 */
	public static final String ACTION_CHECK_BATTERY_LEVEL
		= BROADCAST_APPID + "ActionCheckBatteryLevel";

    /**
     * It is used to send the name of a device to the adaptation service.
     * EXTRA - Contains the result receiver to respond to the technology.
     * EXTRA - Contains the string name of the device.
     */
    public static final String ACTION_NEW_DEVICE
            = BROADCAST_APPID + "ActionNewDevice";
    public static final String EXTRA_RESULT_RECEIVER
            = BROADCAST_APPID + "ExtraResultReceiver";
    public static final String EXTRA_NEW_DEVICE
            = BROADCAST_APPID + "ExtraNewDevice";
}
