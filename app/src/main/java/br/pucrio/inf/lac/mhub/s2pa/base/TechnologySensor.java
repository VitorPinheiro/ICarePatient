package br.pucrio.inf.lac.mhub.s2pa.base;

import java.io.Serializable;
import java.util.UUID;

/**
 * Each technology device possess technology sensors
 * e.g. The SensorTag (implements TechnologyDevice) possess 6 sensors
 */
public interface TechnologySensor extends Serializable {
    /**
     * Gets the name of the Sensor
     * @return A String which is the sensor's name (e.g. Temperature)
     */
	String getName();

    /**
     * Gets the service UUID
     * @return UUID, to get the service object
     * e.g. Temperature: f000aa00-0451-4000-b000-000000000000
     */
	UUID getService();

    /**
     * Gets the data UUID
     * @return UUID, the data characteristic of the service
     * e.g. Temperature: f000aa01-0451-4000-b000-000000000000
     */
	UUID getData();

    /**
     * Gets the configuration UUID for the service
     * @return UUID, to enable it or the notifications
     * e.g. Temperature: f000aa02-0451-4000-b000-000000000000
     */
	UUID getConfig();

	/**
	 * Gets the calibration UUID for the service
	 * @return UUID, to calibrate the service
	 * e.g. Pressure: f000aa43-0451-4000-b000-000000000000
	 */
	UUID getCalibration();
	
	/**
     * Gets the code to enable the sensor
	 * @return the code which, when written to the configuration characteristic, turns on the sensor.
	 **/
	byte getEnableSensorCode();

	/**
	 * Functions to transform the calibration data
	 * @param value The raw data
	 */
	void setCalibrationData( byte[] value ) throws UnsupportedOperationException;

    /**
     * Functions to transform the data obtained from the sensor to an array of doubles
     * @param value The raw data
     * @return The representation of the raw data in Double
     */
	Double[] convert( byte[] value ) throws UnsupportedOperationException;
}
