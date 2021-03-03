package br.pucrio.inf.lac.mhub.models;

/**
 * Created by luis on 11/11/15.
 * POJO that describes the connection state
 */
public class ConnectionData {
    /** Message Data */
    private String mState;

    /** Connection States */
    public static final String CONNECTED    = "Connected";
    public static final String DISCONNECTED = "Disconnected";

    public String getState() {
        return mState;
    }

    public void setState( String state ) {
        this.mState = state;
    }

    @Override
    public String toString() {
        return getState();
    }
}
