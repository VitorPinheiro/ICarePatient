package br.pucrio.inf.lac.mhub.s2pa.base;

import br.pucrio.inf.lac.mhub.components.MOUUID;
import br.pucrio.inf.lac.mhub.models.locals.SensorData;

/**
 * This interface receives the information of the M-OBJs
 * that are found under the implemented technologies
 */
public interface TechnologyListenerExtended extends TechnologyListener {

    void onMObjectValueRead(MOUUID mobileObject, SensorData sensorData);
}
