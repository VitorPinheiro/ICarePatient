package br.pucrio.inf.lac.mhub.models.locals;

/**
 * Created by bertodetacio on 07/06/17.
 */

public class StartLocationSensorMessage {

    private long interval = 1000;

    public StartLocationSensorMessage() {
    }

    public StartLocationSensorMessage(long interval) {
        this.interval = interval;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}
