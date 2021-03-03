package br.pucrio.inf.lac.mhub.s2pa.base;

import br.pucrio.inf.lac.mhub.models.locals.SensorData;

public interface TechnologySensorExtended extends TechnologySensor{

   SensorData convertToSensorData(byte[] value) throws UnsupportedOperationException;



}
