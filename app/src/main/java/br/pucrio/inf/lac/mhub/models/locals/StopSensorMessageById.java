package br.pucrio.inf.lac.mhub.models.locals;

import android.hardware.SensorManager;

/**
 * Created by bertodetacio on 07/06/17.
 */

public class StopSensorMessageById {

    private int id;

    public StopSensorMessageById() {
    }

    public StopSensorMessageById(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
