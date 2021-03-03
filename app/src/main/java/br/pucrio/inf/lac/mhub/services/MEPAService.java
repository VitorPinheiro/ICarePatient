package br.pucrio.inf.lac.mhub.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.JsonReader;
import android.util.JsonToken;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EPStatementException;

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import br.pucrio.inf.lac.mhub.components.AppUtils;
import br.pucrio.inf.lac.mhub.databases.CEPRulesDataSource;
import br.pucrio.inf.lac.mhub.databases.EventTypesDataSource;
import br.pucrio.inf.lac.mhub.managers.LocalRouteManager;
import br.pucrio.inf.lac.mhub.models.CEPRule;
import br.pucrio.inf.lac.mhub.models.EventType;
import br.pucrio.inf.lac.mhub.models.base.QueryMessage;
import br.pucrio.inf.lac.mhub.models.locals.EventData;
import br.pucrio.inf.lac.mhub.models.locals.MessageData;
import br.pucrio.inf.lac.mhub.models.locals.SensorData;
import br.pucrio.inf.lac.mhub.models.queries.MEPAQuery;
import br.pucrio.inf.lac.mhub.services.listeners.MEPAListener;
import de.greenrobot.event.EventBus;

/**
 * Service that contains the CEP engine (Asper)
 * to process the sensor data, it also handles a database
 * for the active CEP rules
 */
public class MEPAService extends Service {
    /** DEBUG */
    private static final String TAG = MEPAService.class.getSimpleName();

    /** Tag used to route the message */
    public static final String ROUTE_TAG = "MEPA";

    /** The CEPRules helper to handle its database table */
    private CEPRulesDataSource rulesDS;
    /** The EventType helper to handle its database table */
    private EventTypesDataSource eventDS;

    /** Asper Configurations */
    private static EPServiceProvider cep;
    private static EPRuntime cepRT;
    private static EPAdministrator cepAdm;

    /** Collection of queries / EPL statements */
    private static ConcurrentHashMap<String, CEPRule> mQueries;

    /** Collection of events */
    private static ConcurrentHashMap<String, EventType> mEvents;

    /** This is the object that receives interactions from clients */
    private final IBinder mBinder = new LocalBinder();

    /**
     * Class for clients to access. Because we know this service always runs in
     * the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public MEPAService getService() {
            return MEPAService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AppUtils.logger( 'i', TAG, ">> START" );
        // get the context
        Context ac = MEPAService.this;
        // register to event bus
        EventBus.getDefault().register( this );
        // get the data source for CEPRules
        rulesDS = CEPRulesDataSource.getInstance( ac );
        // get the data source for EventTypes
        eventDS = EventTypesDataSource.getInstance( ac );
        // call the bootstrap to initialize all the variables
        bootstrap();
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppUtils.logger( 'i', TAG, ">> DESTROYED" );
        // unregister from event bus
        EventBus.getDefault().unregister( this );
        // Destroy CEP
        cep.removeAllStatementStateListeners();
        cep.removeAllServiceStateListeners();

        cepAdm.stopAllStatements();
        cepAdm.destroyAllStatements();

        mQueries.clear();
        mEvents.clear();
        cep.destroy();

        if( rulesDS != null )
            rulesDS.close();

        if( eventDS != null )
            eventDS.close();

        System.gc();
    }

    /**
     * The bootstrap for this service, it will start and get all the default
     * values from the SharedPreferences to start the service without any
     * problem.
     */
    private void bootstrap() {
        Configuration cepConfig = new Configuration();
        cepConfig.addEventType( "SensorData", SensorData.class.getName() );

        cep = EPServiceProviderManager.getDefaultProvider( cepConfig );
        cepAdm = cep.getEPAdministrator();
        cepRT  = cep.getEPRuntime();

        mQueries = new ConcurrentHashMap<>();
        mEvents  = new ConcurrentHashMap<>();

        try {
            eventDS.open();
            List<EventType> eventObjs = eventDS.getAllEventTypes();
            for( EventType temp : eventObjs )
                addEvent( temp );

            rulesDS.open();
            List<CEPRule> ruleObjs = rulesDS.getAllCEPRules();
            for( CEPRule temp : ruleObjs )
                addRule( temp );
        } catch( SQLException e ) {
            e.printStackTrace();
        }

        /*String data =
            "[" +
                "{" +
                    "\"MEPAQuery\":" + "{" +
                        "\"type\":\"add\"," +
                        "\"label\":\"AVGTemp\"," +
                        "\"object\":\"rule\"," +
                        "\"rule\":\"SELECT avg(sensorValue[1]) as value FROM SensorData(sensorName='Temperature').win:time_batch(10 sec)\"," +
                        "\"target\":\"local\"," +
                        "\"actuation\":\"SoundAction\"" +
                    "}" +
                "}" +
            "]";

        LocalRouteManager cm = LocalRouteManager.getInstance();
        cm.routeMessage( data );*/
    }

    /**
     * Register a new EPL compliant query.
     * @param ruleObj The rule structure
     */
    private void addRule( CEPRule ruleObj ) {
        final String label = ruleObj.getLabel();
        EPStatement statement = cepAdm.createEPL( ruleObj.getRule(), label );
        // If the state is inactive then stop the rule
        if( ruleObj.getState() == CEPRule.INACTIVE )
            statement.stop();
        // Otherwise, we add a listener
        else {
            MEPAListener listener = new MEPAListener( label, ruleObj.getTarget() );
            statement.addListener( listener );
        }
        // Since the statement cannot be save in the DB, lets set it again
        ruleObj.setStatement( statement );
        mQueries.put( label, ruleObj );
    }

    /**
     * Register a new EPL compliant statement.
     * @param label The label for the statements
     * @param rule The string for the EPL
     * @param target The target of the data, either global (cloud), local (mepa)
     * @return true if no error presented
     */
    private boolean addRule( String label, String rule, QueryMessage.ROUTE target, String actuation ) {
        if( mQueries.containsKey( label ) ) {
            AppUtils.sendErrorMessage( ROUTE_TAG, String.format( MessageData.ERROR.ER06.toString(), "Rule", label ) );
            return false;
        }

        try {
            EPStatement statement = cepAdm.createEPL( rule, label );
            MEPAListener listener;

            if( actuation != null )
                listener = new MEPAListener( label, target, actuation );
            else
                listener = new MEPAListener( label, target );

            statement.addListener( listener );

            if( target.equals( QueryMessage.ROUTE.LOCAL ) ) {
                final com.espertech.esper.client.EventType eventType = statement.getEventType();
                final String[] propertyNames = eventType.getPropertyNames();
                final Map<String, Object> properties = new HashMap<>();

                for( String propertyName : propertyNames )
                    properties.put( propertyName, eventType.getPropertyType( propertyName ) );

                addEvent( label, properties );
            }

            CEPRule ruleObj = rulesDS.createCEPRule( label, rule, target, CEPRule.LOW, CEPRule.ACTIVE );
            ruleObj.setStatement( statement );
            mQueries.put( label, ruleObj );
        } catch( EPStatementException ex ) {
            AppUtils.sendErrorMessage( ROUTE_TAG, ex.getMessage() );
            return false;
        }
        return true;
    }

    /**
     * Starts CEP statements with a specific label
     * @param label The label for the statements
     */
    private void startRule( String label ) {
        CEPRule rule = mQueries.get( label );
        EPStatement statement = rule.getStatement();
        if( statement.isStopped() ) {
            MEPAListener listener = new MEPAListener( label, rule.getTarget() );
            statement.addListener( listener );
            statement.start();
            rule.setState( CEPRule.ACTIVE );
            rulesDS.updateCEPRule( rule );
        }
    }

    /**
     * Stops CEP statements with a specific label
     * @param label The label for the statements
     */
    private void stopRule( String label ) {
        CEPRule rule = mQueries.get( label );
        EPStatement statement = rule.getStatement();
        if( statement.isStarted() ) {
            statement.removeAllListeners();
            statement.stop();
            rule.setState( CEPRule.INACTIVE );
            rulesDS.updateCEPRule( rule );
        }
    }

    /**
     * Destroys CEP statements with a specific label
     * @param label The name of the statements
     */
    private void removeRule( String label ) {
        CEPRule rule = mQueries.remove( label );
        if( rule != null ) {
            EPStatement statement = rule.getStatement();
            statement.removeAllListeners();
            statement.destroy();
            rulesDS.deleteCEPRule( rule );
        } else {
            AppUtils.sendErrorMessage( ROUTE_TAG, String.format( MessageData.ERROR.ER02.toString(), label ) );
        }
    }

    /**
     * Removes all the queries
     */
    private void clearRules() {
        for( String label : mQueries.keySet() )
            removeRule( label );
    }

    /**
     * Add an event to the CEP engine from the structure (DB model)
     * @param eventObj The event structure
     */
    private void addEvent( EventType eventObj ) {
        final String label = eventObj.getLabel();
        final String data = eventObj.getProperties();
        final Map<String, Object> properties = new HashMap<>();

        JsonReader reader = new JsonReader( new StringReader( data ) );
        try {
            reader.beginArray();
            while( reader.hasNext() ) {
                reader.beginArray();
                String var  = reader.nextString();
                String type = reader.nextString();
                properties.put( var, Class.forName( type ) );
                reader.endArray();
            }
            reader.endArray();

            cepAdm.getConfiguration().addEventType(
                    label,
                    properties
            );
            mEvents.put( label, eventObj );
        } catch( IOException | ClassNotFoundException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Add an event to the CEP engine where the properties are a Map
     * @param label The name of the event type
     * @param properties The properties of the event
     */
    private void addEvent( String label, Map<String, Object> properties ) {
        if( mEvents.containsKey( label ) ) {
            AppUtils.sendErrorMessage( ROUTE_TAG, String.format( MessageData.ERROR.ER06.toString(), "Event", label ) );
            return;
        }

        cepAdm.getConfiguration().addEventType(
                label,
                properties
        );
        final EventType eventType = eventDS.createEventType( label, properties );
        mEvents.put( label, eventType );
    }

    /**
     * Removes an event type.
     * @param label The name of the event type
     * @return boolean If the remove was a success or not
     */
    private boolean removeEvent( String label ) {
        // First remove all the rules
        Set<String> ruleLabels = cepAdm.getConfiguration().getEventTypeNameUsedBy( label );
        for( String ruleLabel : ruleLabels )
            removeRule( ruleLabel );

        // Try to remove the event
        boolean removed = cepAdm.getConfiguration().removeEventType( label, false );
        if( removed ) {
            final EventType eventType = mEvents.remove( label );
            eventDS.deleteCEPRule( eventType );
        }
        return removed;
    }

    /**
     * Removes all the events
     */
    private void clearEvents() {
        for( String label : mEvents.keySet() )
            removeEvent( label );
    }

    @SuppressWarnings("unused") // it's actually used to receive events from the S2PA Service
    public void onEvent( SensorData sensorData ) {
        // Look if the message is for this service
        if( sensorData == null || !AppUtils.isInRoute( ROUTE_TAG, sensorData.getRoute() ) )
            return;

        if( sensorData.getSensorValue() != null )
            cepRT.sendEvent( sensorData );
    }

    @SuppressWarnings("unused") // it's actually used to receive events from the MEPA Service
    public void onEvent( EventData eventData ) {
        // Look if the message is for this service
        if( eventData == null || !AppUtils.isInRoute( ROUTE_TAG, eventData.getRoute() ) )
            return;

        // Event description
        Map<String, Object> payload = new HashMap<>();
        // Event data
        final String label = eventData.getLabel();
        final String data = eventData.getData();
        JsonReader reader = new JsonReader( new StringReader( data ) );

        try {
            reader.beginObject();
            while( reader.hasNext() ) {
                final String name  = reader.nextName();
                final JsonToken token = reader.peek();
                Object value = null;

                if( token.equals( JsonToken.STRING ) )
                    value = reader.nextString();
                else if( token.equals( JsonToken.NUMBER ) )
                    value = reader.nextDouble();

                payload.put( name, value );
            }
            reader.endObject();

            if( mEvents.containsKey( label ) )
                cepRT.sendEvent( payload, label );
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     * This callback generates error (StackOverFlow) at the deploy if it is used as background
     * * Note: only generates the errors with complex queries like Math.sqrt(Math.pow(avg...
     * @param query The query received from the LocalRouteManager
     */
    @SuppressWarnings("unused")
    public void onEventMainThread( MEPAQuery query ) {
        // Get the query values
        final QueryMessage.ITEM object  = query.getObject();
        final QueryMessage.ROUTE target = query.getTarget() != null ? query.getTarget() : QueryMessage.ROUTE.GLOBAL;
        final String label = query.getLabel();
        final String rule = query.getRule();
        final String actuation = query.getActuation();
        final Map<String, Object> event = query.getEvent();
        // Depending on the type it will execute an action
        switch( query.getType() ) {
            case ADD:
                // Verify if label (identifier) exists
                if( label == null )
                    AppUtils.sendErrorMessage( ROUTE_TAG, MessageData.ERROR.ER03.toString() );
                // Verify if there is a rule or event to deploy
                else if( rule == null && event == null )
                    AppUtils.sendErrorMessage( ROUTE_TAG, MessageData.ERROR.ER05.toString() );
                // Deploy a rule and/or event
                else {
                    if( rule != null ) addRule( label, rule, target, actuation );

                    if( event != null ) addEvent( label, event );
                }
                break;

            case REMOVE:
                // Verify if label (identifier) exists
                if( label == null )
                    AppUtils.sendErrorMessage( ROUTE_TAG, MessageData.ERROR.ER03.toString() );
                // Verify if there is a rule or event to remove
                else if( object == null )
                    AppUtils.sendErrorMessage( ROUTE_TAG, MessageData.ERROR.ER05.toString() );
                // Remove a rule or event
                else {
                    if( object.equals( QueryMessage.ITEM.RULE ) )
                        removeRule( label );
                    else
                        removeEvent( label );
                }
                break;

            case START:
                // Verify if label (identifier) exists
                if( label == null )
                    AppUtils.sendErrorMessage( ROUTE_TAG, MessageData.ERROR.ER03.toString() );
                // Start the rule
                else
                    startRule( label );
                break;

            case STOP:
                // Verify if label (identifier) exists
                if( label == null )
                    AppUtils.sendErrorMessage( ROUTE_TAG, MessageData.ERROR.ER03.toString() );
                // Stop the rule
                else
                    stopRule( label );
                break;

            case CLEAR:
                if( object.equals( QueryMessage.ITEM.RULE ) )
                    clearRules();
                else if( object.equals( QueryMessage.ITEM.EVENT ) )
                    clearEvents();
                else {
                    clearEvents();
                    clearRules();
                }
                break;

            case GET:
                String message = "Events: " + Collections.singletonList( mEvents.keySet() ).toString() + "\n" +
                                 "Queries:" + Collections.singletonList( mQueries.keySet() ).toString();

                AppUtils.sendMessage(
                        MessageData.TYPE.REPLY,
                        TAG,
                        message
                );

                AppUtils.logger( 'd', TAG, message );
                break;

            default:
                AppUtils.sendErrorMessage( ROUTE_TAG, MessageData.ERROR.ER01.toString() );
                break;
        }
    }
}
