package br.pucrio.inf.lac.mhub.s2pa.base;

import java.util.List;

import br.pucrio.inf.lac.mhub.models.locals.SensorData;

/**
 * Represents a Mobile Object
 */
public interface TechnologyDeviceExtended extends TechnologyDevice {

    List<TechnologySensor> getServices();

    List<SensorData> convertToSensorDataList(byte[] bytes);


}
