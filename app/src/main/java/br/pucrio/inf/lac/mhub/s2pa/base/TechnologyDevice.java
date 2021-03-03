package br.pucrio.inf.lac.mhub.s2pa.base;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import component.IComponent;

/**
 * Represents a Mobile Object
 */
public interface TechnologyDevice extends Serializable, IComponent<Object, Object> {
    /**
     * Get the TechnologySensor or service by the Name
     * @param serviceName The service name
     * @return The service
     */
    TechnologySensor getServiceByName( String serviceName );

    /**
     * Get the TechnologySensor or service by the UUID
     * @param uuid The UUID of the service
     * @return The service
     */
    List<TechnologySensor> getServiceByUUID( UUID uuid );

    /**
     * Get the TechnologySensor by the UUID
     * @param uuid The UUID of the characteristic
     * @return The service
     */
    TechnologySensor getCharacteristicByUUID( UUID uuid );
}
