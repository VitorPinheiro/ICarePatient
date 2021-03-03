package br.pucrio.inf.lac.mhub.models.locals;

/**
 * Created by bertodetacio on 07/06/17.
 */

public class StartBatterySensorMessage {

    private long interval = 1000;

    public StartBatterySensorMessage() {
    }

    public StartBatterySensorMessage(long interval) {
        this.interval = interval;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}
