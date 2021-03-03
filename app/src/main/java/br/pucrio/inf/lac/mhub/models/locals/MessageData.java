package br.pucrio.inf.lac.mhub.models.locals;

import org.json.JSONException;
import org.json.JSONObject;

import br.pucrio.inf.lac.mhub.models.base.LocalMessage;

/**
 * Created by luis on 14/05/15.
 * Representation of an Error from M-Hub
 */
public class MessageData extends LocalMessage {
    /** DEBUG */
    private static final String TAG = MessageData.class.getSimpleName();

    /** Message Types */
    public enum TYPE {
        ERROR ( "ErrorData" ),
        REPLY ( "ReplyData" );

        private String value;

        TYPE( String value ) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /** Message Data */
    private String component;
    private String message;

    /** Error Types */
    public enum ERROR {
        ER01   ( "Action not supported." ),
        ER02   ( "Query %s not found." ),
        ER03   ( "Label not defined." ),
        ER04   ( "Null pointer Exception." ),
        ER05   ( "Rule and Event not defined." ),
        ER06   ( "%s: Label %s already used." );

        private String value;

        ERROR(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public MessageData( TYPE tag ) {
        super( tag.toString() );
    }

    /** Getters */
    public String getComponent() {
        return component;
    }

    public String getMessage() {
        return message;
    }
    /** Getters */

    /** Setters */
    public void setComponent(String component) {
        this.component = component;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    /** Setters */

    @Override
    public String getID() {
        return null;
    }

    @Override
    public String toJSON() throws JSONException {
        JSONObject data = new JSONObject();

        data.put( UUID,      getUuid() );
        data.put( COMPONENT, getComponent() );
        data.put( MESSAGE,   getMessage() );

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
        return TAG + " [uuid=" + getUuid()
                + ", component=" + getComponent()
                + ", message=" + getMessage()
                + "]";
    }
}
