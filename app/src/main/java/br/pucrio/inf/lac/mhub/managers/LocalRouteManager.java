package br.pucrio.inf.lac.mhub.managers;

import android.util.JsonReader;

import java.io.IOException;
import java.io.StringReader;

import br.pucrio.inf.lac.mhub.components.AppUtils;
import br.pucrio.inf.lac.mhub.models.locals.MessageData;
import br.pucrio.inf.lac.mhub.models.queries.MEPAQuery;
import br.pucrio.inf.lac.mhub.models.queries.S2PAQuery;
import de.greenrobot.event.EventBus;

/**
 * Created by luis on 2/05/15.
 * Handle incoming JSON messages which are commands
 * to the Mobile Hub
 */
public class LocalRouteManager {
    /** DEBUG */
    private final static String TAG = LocalRouteManager.class.getSimpleName();

    /** Instance for the singleton */
    private static LocalRouteManager instance = new LocalRouteManager();

    private LocalRouteManager() {
    }

    public static LocalRouteManager getInstance() {
        return instance;
    }

    /**
     * It receives the String message (JSON), parses and sends it to their
     * destination service
     * @param data The JSON as a String
     * @return true  if everything was Ok
     *         false if it failed
     */
    public boolean routeMessage( String data ) {
        if( data == null ) {
            AppUtils.sendErrorMessage( TAG, MessageData.ERROR.ER04.toString() );
            return false;
        }

        AppUtils.logger( 'e', TAG, data );

        JsonReader reader = new JsonReader( new StringReader( data ) );
        try {
            reader.beginArray();
            while( reader.hasNext() ) {
                reader.beginObject();
                while( reader.hasNext() ) {
                    String name = reader.nextName();
                    switch( name ) {
                        case MEPAQuery.TAG:
                            MEPAQuery mepa = new MEPAQuery();
                            mepa.fromJSON( reader );
                            EventBus.getDefault().post( mepa );
                            break;

                        case S2PAQuery.TAG:
                            S2PAQuery s2pa = new S2PAQuery();
                            s2pa.fromJSON( reader );
                            EventBus.getDefault().post( s2pa );
                            break;

                        default:
                            reader.skipValue();
                            break;
                    }
                }
                reader.endObject();
            }
            reader.endArray();
            return true;
        } catch( IOException | IllegalArgumentException | IllegalStateException e ) {
            AppUtils.sendErrorMessage( TAG, e.getMessage() );
        }
        return false;
    }
}
