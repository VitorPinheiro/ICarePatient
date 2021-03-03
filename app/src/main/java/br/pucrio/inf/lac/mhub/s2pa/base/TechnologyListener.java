package br.pucrio.inf.lac.mhub.s2pa.base;

import java.util.List;

import br.pucrio.inf.lac.mhub.components.MOUUID;

/**
 * This interface receives the information of the M-OBJs
 * that are found under the implemented technologies
 */
public interface TechnologyListener {
	/**
	 * Called when a mobile object is found
	 * @param mobileObject The mobile object
	 * @param rssi The RSSI value for the mobile object
	 */
	void onMObjectFound( MOUUID mobileObject, Double rssi );
	
	/**
	 * Called when a mobile object is connected
	 * @param mobileObject The mobile object
	 */
	void onMObjectConnected( MOUUID mobileObject );
	
	/**
	 * Called when a mobile object is disconnected
	 * @param mobileObject The mobile object
	 * @param services Services of the mobile object 
	 */
	void onMObjectDisconnected( MOUUID mobileObject, List<String> services );
	
	/**
	 * Called when all the services of a mobile object are discovered
	 * @param mobileObject The mobile object
	 * @param services Services provided by the mobile object e.g. Temperature
	 */
	void onMObjectServicesDiscovered( MOUUID mobileObject, List<String> services );
	
	/**
	 * Called when a mobile object reads a value of its sensors
	 * @param mobileObject The mobile object UUID
	 * @param rssi The RSSI value for the mobile object
	 * @param serviceName The service name e.g. Temperature
	 * @param values The values read for the service
	 */
	void onMObjectValueRead( MOUUID mobileObject, Double rssi, String serviceName, Double[] values );
}
