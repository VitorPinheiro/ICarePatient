package br.pucrio.inf.lac.mhub.models.locals;

import org.json.JSONException;
import org.json.JSONObject;

import br.pucrio.inf.lac.mhub.models.base.LocalMessage;

/**
 * Represent the events, their label and its data
 */
public class EventData extends LocalMessage {
    /** DEBUG */
    private static final String TAG = EventData.class.getSimpleName();

    /** Message Data */
    private String label;
    private String data;

	public EventData() {
		super( TAG );
	}

    /** Getters */
    public String getLabel() {
        return this.label;
    }

    public String getData() {
        return this.data;
    }
    /** Getters */

    /** Setters */
    public void setLabel(String label) {
        this.label = label;
    }

    public void setData(String data) {
        this.data = data;
    }
    /** Setters */

    @Override
    public String getID() {
        return null;
    }

    @Override
	public String toJSON() throws JSONException {
        JSONObject data = new JSONObject();

        data.put( UUID,  getUuid() );
        data.put( LABEL, getLabel() );
        data.put( DATA,  getData() );

        if( getLatitude() != null && getLongitude() != null) {
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
        return TAG + " [label=" + label + ", data=" + data
                + "]";
    }
}
