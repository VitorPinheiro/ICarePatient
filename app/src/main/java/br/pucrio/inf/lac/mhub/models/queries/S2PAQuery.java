package br.pucrio.inf.lac.mhub.models.queries;

import android.util.JsonReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.pucrio.inf.lac.mhub.models.base.QueryMessage;

/**
 * Created by luis on 1/07/15.
 * POJO that represents a query message
 * to the S2PA service
 */
public class S2PAQuery extends QueryMessage {
    /** DEBUG */
    public static final String TAG = "S2PAQuery";

    /** Message Data */
    private ACTION type;
    private String target;
    private final List<String> device = new ArrayList<>();

    /** Getters */
    public ACTION getType() {
        return this.type;
    }

    public String getTarget() {
        return this.target;
    }

    public List<String> getDevices() {
        return this.device;
    }

    @Override
    public void fromJSON(JsonReader reader) throws IOException, IllegalArgumentException {
        reader.beginObject();
        while( reader.hasNext() ) {
            String name = reader.nextName();
            switch( name ) {
                case TYPE:
                    type = ACTION.fromString( reader.nextString() );
                    break;

                case TARGET:
                    target = reader.nextString();
                    break;

                case DEVICE:
                    reader.beginArray();
                    while( reader.hasNext() )
                        device.add( reader.nextString() );
                    reader.endArray();
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
        return TAG + " [type=" + type + ", target=" + target
                + ", devices=" + Arrays.toString( device.toArray() )
                + "]";
    }
}
