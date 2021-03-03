package br.pucrio.inf.lac.mhub.models.locals;

/**
 * Created by bertodetacio on 07/06/17.
 */

public class StopSensorMessageBySimpleName {

    private String name = "ALL";

    public StopSensorMessageBySimpleName() {
    }

    public StopSensorMessageBySimpleName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
