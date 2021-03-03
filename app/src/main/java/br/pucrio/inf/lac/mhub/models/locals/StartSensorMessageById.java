package br.pucrio.inf.lac.mhub.models.locals;

import android.hardware.SensorManager;

/**
 * Created by bertodetacio on 07/06/17.
 */

public class StartSensorMessageById {

    private int id;

    private int rate = SensorManager.SENSOR_DELAY_NORMAL;

    public StartSensorMessageById() {
    }

    public StartSensorMessageById(int id, int rate) {
        this.id = id;
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
