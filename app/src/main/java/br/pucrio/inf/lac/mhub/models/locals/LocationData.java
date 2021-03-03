package br.pucrio.inf.lac.mhub.models.locals;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import br.pucrio.inf.lac.mhub.models.base.LocalMessage;

/**
 * POJO of the location, which is sent to the cloud
 */
public class LocationData extends LocalMessage {
    /** DEBUG */
    private static final String TAG = LocationData.class.getSimpleName();

    /** Message Data */
	private String  uuid;
	private Date    datetime;
	private float   accuracy;
	private String  provider;
	private float   speed;
	private float   bearing;
	private double  altitude;
	private String  connectionType;
	private int     batteryPercent;
	private boolean isCharging;

    public LocationData() {
        super( TAG );
    }

    /** Getters */
    public String getUuid() {
        return uuid;
    }

    public Date getDatetime() {
        return datetime;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public String getProvider() {
        return provider;
    }

    public float getSpeed() {
        return speed;
    }

    public float getBearing() {
        return bearing;
    }

    public double getAltitude() {
        return altitude;
    }

    public String getConnectionType() {
        return connectionType;
    }

	public int getBatteryPercent() {
		return batteryPercent;
	}

    public boolean isCharging() {
        return isCharging;
    }
	/** Getters */

    /** Setters */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public void setBatteryPercent(int batteryPercent) {
        this.batteryPercent = batteryPercent;
    }

    public void setCharging(boolean isCharging) {
        this.isCharging = isCharging;
    }
    /** Setters */

    @Override
    public String getID() {
        return getRoute();
    }

    @Override
	public String toJSON() throws JSONException {
		JSONObject data = new JSONObject();

        data.put( UUID,       getUuid() );
        data.put( DATE,       getDatetime() );
        data.put( ACCURACY,   getAccuracy() );
        data.put( PROVIDER,   getProvider() );
        data.put( SPEED,      getSpeed() );
        data.put( BEARING,    getBearing() );
        data.put( ALTITUDE,   getAltitude() );
        data.put( CONNECTION, getConnectionType() );
        data.put( BATTERY,    getBatteryPercent() );
        data.put( CHARGING,   isCharging() );
        // Parent
        data.put( FUNCTION,   getTag() );
        data.put( LATITUDE,   getLatitude() );
        data.put( LONGITUDE,  getLongitude() );
        data.put( TIMESTAMP,  getTimestamp() );
		
		return data.toString();
	}

    @Override
    public String toString() {
        return TAG + " [uuid=" + uuid + ", latitude=" + getLatitude()
                + ", longitude=" + getLongitude() + ", datetime=" + datetime
                + ", accuracy=" + accuracy + ", provider=" + provider
                + ", speed=" + speed + ", bearing=" + bearing
                + ", altitude=" + altitude + ", connectionType=" + connectionType
                + ", batteryPercent=" + batteryPercent + ", isCharging=" + isCharging
                + "]";
    }
}
