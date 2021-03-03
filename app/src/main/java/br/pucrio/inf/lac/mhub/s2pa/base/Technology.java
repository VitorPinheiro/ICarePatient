package br.pucrio.inf.lac.mhub.s2pa.base;

/**
 * Each technology must implement this interface to
 * communicate with the S2PA Service that implements
 * the TechnologyListener
 */
public interface Technology {
	/**
	 * Initialize the resources and verifies if the technology is available in the device
	 * @return If its possible to initialize
	 */
	boolean initialize();
	
	/**
	 * Tries to enable the technology
     * @throws NullPointerException
	 */
	void enable() throws NullPointerException;
	
	/**
	 * Set a listener for the technology
	 * @param listener The listener for the technology
	 */
	void setListener(TechnologyListener listener);
	
	/**
	 * Look for near mobile objects
	 * @param autoConnect if after find devices a connection should be realized
     * @throws NullPointerException
	 */
	void startScan(boolean autoConnect) throws NullPointerException;;
	
	/**
	 * Stops the scan started by the startScan function
     * @throws NullPointerException
	 */
	void stopScan() throws NullPointerException;
	
	/**
	 * Read a value of a service in a mobile object 
	 * @param macAddress Address of the device in the technology
	 * @param serviceName Service Name e.g. Temperature
	 */
	void readSensorValue( String macAddress, String serviceName );
	
	/**
	 * Writes a value of a service in a mobile object
	 * @param macAddress Address of the device in the technology
	 * @param serviceName Name of the service
	 * @param value New value to be set on the service
	 */
	void writeSensorValue( String macAddress, String serviceName, Object value );
	
	/**
	 * Attempt to connect to a device if it was found or not connected already
	 * @param macAddress Mobile Object
	 * @return	If its possible the attempt to connect
	 */
	boolean connect(String macAddress);
	
	/**
	 * Attempt to disconnect from a device if it is connected already
	 * @param macAddress Mobile Object
	 * @return	If its possible the disconnect
	 */
	boolean disconnect(String macAddress);
	
	/**
	 * Includes a Mobile Object into the black list
	 * @param macAddress The mobile object address
	 */
	void addToBlackList(String macAddress);
	
	/**
	 * Remove a Mobile Object from the back list
	 * @param macAddress The mobile object address
	 */
	boolean removeFromBlackList(String macAddress);
	
	/**
	 * Clears the black list
	 */
	void clearBlackList();
	
	/**
	 * Verifies if a mobile object is in the black list
	 * @param macAddress The mobile object address
	 * @return true if it is on the list, otherwise false
	 */
	boolean isInBlackList(String macAddress);

    /**
     * Includes a Mobile Object into the black list
     * @param macAddress The mobile object address
     */
    void addToWhiteList(String macAddress);

    /**
     * Remove a Mobile Object from the back list
     * @param macAddress The mobile object address
     */
    boolean removeFromWhiteList(String macAddress);

    /**
     * Clears the black list
     */
    void clearWhiteList();

    /**
     * Verifies if a mobile object is in the black list
     * @param macAddress The mobile object address
     * @return true if it is on the list, otherwise false
     */
    boolean isInWhiteList(String macAddress);
	
	/**
	 * Release the resources of the technology
	 */
	void destroy();
}
