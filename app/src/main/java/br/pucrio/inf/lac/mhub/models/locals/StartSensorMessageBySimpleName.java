package br.pucrio.inf.lac.mhub.models.locals;

import android.hardware.Sensor;
import android.hardware.SensorManager;

import br.pucrio.inf.lac.mhub.s2pa.technologies.ble.devices.SensorTag;

/**
 * Created by bertodetacio on 07/06/17.
 */

public class StartSensorMessageBySimpleName {

    private String name = "ALL";

    private int rate = SensorManager.SENSOR_DELAY_NORMAL;

    public StartSensorMessageBySimpleName() {
    }

    public StartSensorMessageBySimpleName(String name, int rate) {
        this.name = name;
        this.rate = rate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
