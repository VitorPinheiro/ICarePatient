package br.pucrio.inf.lac.mhub.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import br.pucrio.inf.lac.mhub.components.AppUtils;
import br.pucrio.inf.lac.mhub.models.EventType;

/**
 * Created by luis on 11/09/15.
 * Handles the functions to add, remove or edit data from the database for events
 */
public class EventTypesDataSource {
    /** DEBUG */
    private final static String TAG = EventTypesDataSource.class.getSimpleName();

    /** Date format */
    private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /** Database fields */
    private SQLiteDatabase mDatabase;
    private MHubSQLiteHelper mDBHelper;
    private static final String[] allColumns = {
            MHubSQLiteHelper.COLUMN_ID,
            MHubSQLiteHelper.COLUMN_LABEL,
            MHubSQLiteHelper.COLUMN_PROPERTIES,
            MHubSQLiteHelper.COLUMN_CREATED
    };

    /** Instance for the singleton */
    private static EventTypesDataSource instance = null;

    private EventTypesDataSource( Context context ) {
        mDBHelper = new MHubSQLiteHelper( context );
    }

    /**
     * Get the EventTypeDataSource instance
     * @param ac The context of the application
     * @return The instance
     */
    public static EventTypesDataSource getInstance( Context ac ) {
        if( instance == null )
            instance = new EventTypesDataSource( ac );
        return instance;
    }

    public void open() throws SQLException {
        mDatabase = mDBHelper.getWritableDatabase();
    }

    public void close() {
        if( mDBHelper != null )
            mDBHelper.close();
    }

    /**
     * Creates a new EventType from the basic data, and returns it as a EventType structure
     * @param label The label of the EventType
     * @param properties The properties of the event
     * @return The EventType structure
     */
    public EventType createEventType( String label, Map<String, Object> properties ) {
        SimpleDateFormat date = new SimpleDateFormat( DATE_FORMAT, java.util.Locale.getDefault() );

        ContentValues values = new ContentValues();
        values.put( MHubSQLiteHelper.COLUMN_LABEL,      label );
        values.put( MHubSQLiteHelper.COLUMN_PROPERTIES, AppUtils.mapToJSONArray( properties ).toString() );
        values.put( MHubSQLiteHelper.COLUMN_CREATED,    date.format( new Date() ) );

        long insertId = mDatabase.insert( MHubSQLiteHelper.TABLE_EVENT_TYPES, null, values );
        Cursor cursor = mDatabase.query( MHubSQLiteHelper.TABLE_EVENT_TYPES, allColumns,
                MHubSQLiteHelper.COLUMN_ID + " = " + insertId,
                null, null, null, null );
        cursor.moveToFirst();
        EventType newType = cursorToEventType( cursor );
        cursor.close();

        return newType;
    }

    /**
     * Delete a EventType
     * @param eventType the EventType to be deleted
     */
    public void deleteCEPRule( EventType eventType ) {
        long id = eventType.getId();
        AppUtils.logger( 'i', TAG, "EventType deleted with id: " + id );
        mDatabase.delete( MHubSQLiteHelper.TABLE_EVENT_TYPES,
                MHubSQLiteHelper.COLUMN_ID + " = " + id, null
        );
    }

    /**
     * Get all the EventTypes stored
     * @return A List of EventTypes
     */
    public List<EventType> getAllEventTypes() {
        List<EventType> eventTypes = new ArrayList<>();
        Cursor cursor = mDatabase.query( MHubSQLiteHelper.TABLE_EVENT_TYPES,
                allColumns, null, null, null, null, null
        );

        cursor.moveToFirst();
        while( !cursor.isAfterLast() ) {
            EventType eventType = cursorToEventType( cursor );
            eventTypes.add( eventType );
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return eventTypes;
    }

    /**
     * Transforms the cursor to an EventType
     * @param cursor The current cursor
     * @return The cursor as a EventType structure
     */
    private EventType cursorToEventType( Cursor cursor ) {
        EventType eventType = new EventType();
        eventType.setId( cursor.getLong( 0 ) );
        eventType.setLabel( cursor.getString( 1 ) );
        eventType.setProperties( cursor.getString( 2 ) );
        eventType.setCreated( cursor.getString( 3 ) );

        return eventType;
    }
}
