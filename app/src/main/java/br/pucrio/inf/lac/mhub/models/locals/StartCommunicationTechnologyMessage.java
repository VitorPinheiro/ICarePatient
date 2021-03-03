package br.pucrio.inf.lac.mhub.models.locals;

/**
 * Created by bertodetacio on 06/06/17.
 */

public class StartCommunicationTechnologyMessage {

    private int technology;

    public StartCommunicationTechnologyMessage(int technology)
    {
        this.technology = technology;
    }

    public int getTechnology() {
        return technology;
    }

    public void setTechnology(int technology) {
        this.technology = technology;
    }

}
