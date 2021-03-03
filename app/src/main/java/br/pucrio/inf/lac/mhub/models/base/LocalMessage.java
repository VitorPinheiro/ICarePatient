package br.pucrio.inf.lac.mhub.models.base;

import org.json.JSONException;

import java.io.Serializable;

/**
 * Abstract class which all local messages have to extend
 */
public abstract class LocalMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /** UUID of the Mobile Hub */
    private String mUuid;
    /** Services that will receive the message */
    private String mRoute;
    /** Message priority */
    private String mPriority;
    /** Message location */
    private Double mLatitude;
    private Double mLongitude;
    /** Timestamp generated in the constructor */
    private Long mTimestamp;
    /** Extra information - TAG */
    private String mTag;

    /** Priority options */
    public static final String HIGH = "HIGH";
    public static final String LOW  = "LOW";

    /** JSON Keys */
    // General
    protected static final String UUID      = "uuid";
    protected static final String FUNCTION  = "tag";
    protected static final String LATITUDE  = "latitude";
    protected static final String LONGITUDE = "longitude";
    protected static final String TIMESTAMP = "timestamp";
    // LocationData
    protected static final String DATE       = "date";
    protected static final String ACCURACY   = "accuracy";
    protected static final String PROVIDER   = "provider";
    protected static final String SPEED      = "speed";
    protected static final String BEARING    = "bearing";
    protected static final String ALTITUDE   = "altitude";
    protected static final String CONNECTION = "connection";
    protected static final String BATTERY    = "battery";
    protected static final String CHARGING   = "charging";
    // SensorData
    protected static final String SOURCE  = "source";
    protected static final String SIGNAL  = "signal";
    protected static final String ACTION  = "action";
    protected static final String SERVICE = "sensor_name";
    protected static final String VALUE   = "sensor_value";
    // EventData
    protected static final String LABEL = "label";
    protected static final String DATA  = "data";
    // ErrorData
    protected static final String COMPONENT = "component";
    protected static final String MESSAGE   = "message";

    /** ID Separator */
    public static final String SEPARATOR = "-";

    /**
     * Default Constructor
     * - Initializes some variables
     * - Gets the current time
     */
    public LocalMessage( String tag ) {
        this.mTag       = tag;
        this.mRoute     = "";
        this.mPriority  = "";
        this.mTimestamp = System.currentTimeMillis() / 1000;
    }

    /** Getters */
    public String getUuid() {
        return mUuid;
    }

    public String getRoute() {
        return this.mRoute;
    }

    public String getPriority() {
        return this.mPriority;
    }

    public Double getLatitude() {
        return this.mLatitude;
    }

    public Double getLongitude() {
        return this.mLongitude;
    }

    public Long getTimestamp() {
        return this.mTimestamp;
    }

    public String getTag() {
        return this.mTag;
    }
    /** Getters */

    /** Setters */
    public void setUuid(String uuid) {
        this.mUuid = uuid;
    }

    public void setRoute(String route) {
        this.mRoute = route;
    }

    public void setPriority(String priority) {
        this.mPriority = priority;
    }

    public void setLatitude(Double latitude) {
        this.mLatitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.mLongitude = longitude;
    }
    /** Setters */

    /**
     * Creates an identifier for a message
     * @return String representation of the ID
     */
    public abstract String getID();

    /**
     * Transform the object to a JSON structure
     * @return The JSON String
     */
    public abstract String toJSON() throws JSONException;
}
