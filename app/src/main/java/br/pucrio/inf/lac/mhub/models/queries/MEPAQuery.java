package br.pucrio.inf.lac.mhub.models.queries;

import android.util.JsonReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import br.pucrio.inf.lac.mhub.components.AppUtils;
import br.pucrio.inf.lac.mhub.models.base.QueryMessage;

/**
 * Created by luis on 1/07/15.
 * POJO that represents a query message
 * to the MEPA service
 */
public class MEPAQuery extends QueryMessage {
    /** DEBUG */
    public static final String TAG = "MEPAQuery";

    private ACTION type;
    private ITEM object;
    private ROUTE target;
    private String label;
    private String rule;
    private String actuation;
    private Map<String, Object> event;

    /** Getters */
    public ACTION getType() {
        return this.type;
    }

    public ITEM getObject() {
        return this.object;
    }

    public ROUTE getTarget() {
        return this.target;
    }

    public String getLabel() {
        return this.label;
    }

    public String getRule() {
        return this.rule;
    }

    public String getActuation() {
        return this.actuation;
    }

    public Map<String, Object> getEvent() {
        return this.event;
    }

    public void fromJSON( JsonReader reader ) throws IOException, IllegalArgumentException {
        reader.beginObject();
        while( reader.hasNext() ) {
            String name = reader.nextName();
            switch( name ) {
                case TYPE:
                    type = ACTION.fromString( reader.nextString() );
                    break;

                case OBJECT:
                    object = ITEM.fromString( reader.nextString() );
                    break;

                case TARGET:
                    target = ROUTE.fromString( reader.nextString() );
                    break;

                case LABEL:
                    label = reader.nextString();
                    break;

                case RULE:
                    rule = reader.nextString();
                    break;

                case EVENT:
                    event = new HashMap<>();

                    reader.beginArray();
                    while( reader.hasNext() ) {
                        reader.beginArray();
                        String var  = reader.nextString();
                        String type = reader.nextString();
                        event.put( var, type );
                        reader.endArray();
                    }
                    reader.endArray();
                    break;

                case ACTUATION:
                    actuation = reader.nextString();
                    break;

                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
    }

    @Override
    public String toString() {
        return TAG + " [type=" + type + ", label=" + AppUtils.valueOf( label )
                + ", rule=" + AppUtils.valueOf( rule ) + "]";
    }
}
