package br.pucrio.inf.lac.mhub.models.locals;

import android.hardware.SensorManager;

/**
 * Created by bertodetacio on 07/06/17.
 */

public class StartAllSensorsMessage {

    private int rate = SensorManager.SENSOR_DELAY_NORMAL;

    public StartAllSensorsMessage() { }

    public StartAllSensorsMessage(int rate) {
        this.rate = rate;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
