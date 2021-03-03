package br.pucrio.inf.lac.mhub.models.locals;

import br.pucrio.inf.lac.mhub.components.MOUUID;

/**
 * Created by bertodetacio on 11/06/17.
 */

public class S2PASensorData {

    private SensorData sensorData;

    private MOUUID mouuid;

    public SensorData getSensorData() {
        return sensorData;
    }

    public void setSensorData(SensorData sensorData) {
        this.sensorData = sensorData;
    }

    public MOUUID getMouuid() {
        return mouuid;
    }

    public void setMouuid(MOUUID mouuid) {
        this.mouuid = mouuid;
    }
}


