package br.pucrio.inf.lac.mhub.models.locals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import br.pucrio.inf.lac.mhub.components.AppUtils;
import br.pucrio.inf.lac.mhub.models.base.LocalMessage;

/**
 * Contains the sensor data received from the
 * Mobile Objects
 */
public class SensorData extends LocalMessage {
    /** DEBUG */
    private static final String TAG = SensorData.class.getSimpleName();

    /** Message Data */
	private String   mouuid;
    private Double   signal;
    private String   action;
	private String   sensorName;
	private Double[] sensorValue;

    /** M-OBJ Actions */
	public static final String FOUND        = "found";
	public static final String CONNECTED    = "connected";
	public static final String DISCONNECTED = "disconnected";
	public static final String READ         = "read";
	//public static final String WRITE        = "write";

    public SensorData() {
        super( TAG );
    }

    public SensorData(String tag) {
        super(tag);
    }

    /** Getters */
	public String getMouuid() {
	    return this.mouuid;
	}

    public Double getSignal() {
        return this.signal;
    }

    public String getAction() {
        return this.action;
    }

	public String getSensorName() {
		return this.sensorName;
	}
	
	public Double[] getSensorValue() {
		return this.sensorValue;
	}
	/** Getters */
	
	/** Setters */
	public void setMouuid( String mouuid ) {
	    this.mouuid = mouuid;
	}

    public void setSignal( Double signal ) {
        this.signal = signal;
    }

    public void setAction( String action ) {
        this.action = action;
    }

	public void setSensorName( String sensorName ) {
		this.sensorName = sensorName;
	}
	
	public void setSensorValue( Double[] sensorValue ) {
		this.sensorValue = sensorValue;
	}
	/** Setters */

    @Override
    public String getID() {
        String key = getMouuid() + SEPARATOR + getAction();
        if( getSensorName() != null )
            key += SEPARATOR + getSensorName();

        return key;
    }

    @Override
	public String toJSON() throws JSONException {
		JSONObject data = new JSONObject();

        data.put( UUID,   getUuid() );
        data.put( SOURCE, getMouuid() );
        data.put( ACTION, getAction() );

        if( signal != null )
            data.put( SIGNAL, getSignal() );

        if( sensorName != null && sensorValue != null ) {
            data.put( SERVICE, getSensorName() );
            data.put( VALUE, new JSONArray( Arrays.asList( getSensorValue() ) ) );
        }

        if( getLatitude() != null && getLongitude() != null ) {
            data.put( LATITUDE,  getLatitude() );
            data.put( LONGITUDE, getLongitude() );
        }

        // Parent
        data.put( FUNCTION,  getTag() );
        data.put( TIMESTAMP, getTimestamp() );
		
		return data.toString();
	}
	
	@Override
	public String toString() {
        return TAG + " [uuid=" + getUuid() + ", source=" + mouuid
                + ", signal=" + AppUtils.valueOf( signal ) + ", action=" + action
                + ", sensor=" + AppUtils.valueOf( sensorName ) + ", value=" + Arrays.toString( sensorValue )
                + "]";
	}
}
