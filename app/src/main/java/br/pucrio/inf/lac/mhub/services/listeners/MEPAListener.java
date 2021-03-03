package br.pucrio.inf.lac.mhub.services.listeners;

import android.content.Context;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import br.pucrio.inf.lac.mhub.MobileHub;
import br.pucrio.inf.lac.mhub.components.AppUtils;
import br.pucrio.inf.lac.mhub.components.actions.contract.Action;
import br.pucrio.inf.lac.mhub.models.base.LocalMessage;
import br.pucrio.inf.lac.mhub.models.base.QueryMessage;
import br.pucrio.inf.lac.mhub.models.locals.EventData;
import br.pucrio.inf.lac.mhub.services.ConnectionService;
import br.pucrio.inf.lac.mhub.services.MEPAService;
import de.greenrobot.event.EventBus;

/**
 * Created by luis on 7/04/15.
 * Listener for the MEPA events
 */
public class MEPAListener implements UpdateListener {
    /** DEBUG */
    private final static String TAG = MEPAListener.class.getSimpleName();

    /** Package of the device structure */
    private static final String ACTUATION_PACKAGE = "br.pucrio.inf.lac.mhub.components.actions.";

    /** Application context */
    @Inject
    Context ac;

    /** Listener ID */
    private String label;
    /** Target of the event data */
    private QueryMessage.ROUTE target;
    /** Defines an action after a complex event is detected */
    private Action actuation;

    /**
     * Constructor
     * @param label ID to identify the listener
     */
    public MEPAListener( String label, QueryMessage.ROUTE target ) {
        this.label = label;
        this.target = target;
    }

    /**
     * Constructor
     * @param label ID to identify the listener
     * @param actuation The string that identifies the actuation class
     */
    public MEPAListener( String label, QueryMessage.ROUTE target, String actuation ) {
        this( label, target );

        // Injection
        MobileHub.getComponent().inject( this );

        try {
            final String componentClass = ACTUATION_PACKAGE + actuation.replaceAll( "\\s+", "" );
            Class<?> c = Class.forName( componentClass );
            this.actuation= (Action) c.newInstance();
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * Get the listener label
     * @return The label (String)
     */
    public String getLabel() {
        return label;
    }

    /**
     * Transform an event to JSON
     * @param event The EventBean object
     * @return A String representation of the event in JSON
     * @throws JSONException
     */
    private String eventToJSON( EventBean event ) throws JSONException {
        String[] properties = event.getEventType().getPropertyNames();
        JSONObject data = new JSONObject();

        for( String property : properties )
            data.put( property, event.get( property ) );

        return data.toString();
    }

    @Override
    public void update( EventBean[] newData, EventBean[] oldData ) {
        for( EventBean event : newData ) {
            AppUtils.logger( 'i', TAG, "Event received: " + event.getUnderlying() );

            // Get the data of the event and send it as a broadcast to the
            // connection service
            try {
                EventData eventData = new EventData();
                eventData.setLabel( getLabel() );
                eventData.setData( eventToJSON( event ) );

                eventData.setPriority( LocalMessage.HIGH );
                if( target.equals( QueryMessage.ROUTE.LOCAL ) )
                    eventData.setRoute( MEPAService.ROUTE_TAG );
                else
                    eventData.setRoute( ConnectionService.ROUTE_TAG );

                EventBus.getDefault().post( eventData );

                if( actuation != null )
                    actuation.execute( ac );
            } catch( JSONException e ) {
                e.printStackTrace();
            }
        }
    }
}
