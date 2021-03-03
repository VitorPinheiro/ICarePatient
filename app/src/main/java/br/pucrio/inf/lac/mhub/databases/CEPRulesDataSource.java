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

import br.pucrio.inf.lac.mhub.components.AppUtils;
import br.pucrio.inf.lac.mhub.models.CEPRule;
import br.pucrio.inf.lac.mhub.models.base.QueryMessage;

/**
 * Created by luis on 28/05/15.
 * Handles the functions to add, remove or edit data from the database
 */
public class CEPRulesDataSource {
    /** DEBUG */
    private final static String TAG = CEPRulesDataSource.class.getSimpleName();

    /** Date format */
    private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /** Database fields */
    private SQLiteDatabase mDatabase;
    private MHubSQLiteHelper mDBHelper;
    private static final String[] allColumns = {
            MHubSQLiteHelper.COLUMN_ID,
            MHubSQLiteHelper.COLUMN_LABEL,
            MHubSQLiteHelper.COLUMN_RULE,
            MHubSQLiteHelper.COLUMN_TARGET,
            MHubSQLiteHelper.COLUMN_PRIORITY,
            MHubSQLiteHelper.COLUMN_STATE,
            MHubSQLiteHelper.COLUMN_CREATED
    };

    /** Instance for the singleton */
    private static CEPRulesDataSource instance = null;

    private CEPRulesDataSource(Context context) {
        mDBHelper = new MHubSQLiteHelper( context );
    }

    /**
     * Get the CEPRulesDataSource instance
     * @param ac The context of the application
     * @return The instance
     */
    public static CEPRulesDataSource getInstance( Context ac ) {
        if( instance == null )
            instance = new CEPRulesDataSource( ac );
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
     * Creates a new CEPRule from the basic data, and returns it as a CEPRule structure
     * @param label The label of the CEPRule
     * @param rule The entire CEP Rule statement
     * @param priority The priority of the CEPRule
     * @param state The current state, like if it is currently running or if it is stopped
     * @return The CEPRule structure
     */
    public CEPRule createCEPRule( String label, String rule, QueryMessage.ROUTE target, int priority, int state ) {
        SimpleDateFormat date = new SimpleDateFormat( DATE_FORMAT, java.util.Locale.getDefault() );

        ContentValues values = new ContentValues();
        values.put( MHubSQLiteHelper.COLUMN_LABEL,    label );
        values.put( MHubSQLiteHelper.COLUMN_RULE,     rule );
        values.put( MHubSQLiteHelper.COLUMN_TARGET,   target.toString() );
        values.put( MHubSQLiteHelper.COLUMN_PRIORITY, priority );
        values.put( MHubSQLiteHelper.COLUMN_STATE,    state );
        values.put( MHubSQLiteHelper.COLUMN_CREATED,  date.format( new Date() ) );

        long insertId = mDatabase.insert( MHubSQLiteHelper.TABLE_CEP_RULES, null, values );
        Cursor cursor = mDatabase.query( MHubSQLiteHelper.TABLE_CEP_RULES, allColumns,
                MHubSQLiteHelper.COLUMN_ID + " = " + insertId,
                null, null, null, null );
        cursor.moveToFirst();
        CEPRule newRule = cursorToCEPRule( cursor );
        cursor.close();

        return newRule;
    }

    /**
     * Updates a CEP Rule (only priority or state)
     * @param cepRule The CEP Rule
     */
    public void updateCEPRule( CEPRule cepRule ) {
        ContentValues values = new ContentValues();
        values.put( MHubSQLiteHelper.COLUMN_PRIORITY, cepRule.getPriority() );
        values.put( MHubSQLiteHelper.COLUMN_STATE, cepRule.getState() );

        mDatabase.update( MHubSQLiteHelper.TABLE_CEP_RULES,
                values,
                MHubSQLiteHelper.COLUMN_ID + "=" + cepRule.getId(),
                null );
    }

    /**
     * Delete a CEPRule
     * @param cepRule the CEPRule to be deleted
     */
    public void deleteCEPRule( CEPRule cepRule ) {
        long id = cepRule.getId();
        AppUtils.logger( 'i', TAG, "CEPRule deleted with id: " + id );
        mDatabase.delete( MHubSQLiteHelper.TABLE_CEP_RULES,
                MHubSQLiteHelper.COLUMN_ID + " = " + id, null
        );
    }

    /**
     * Get all the CEPRules stored
     * @return A List of CEPRules
     */
    public List<CEPRule> getAllCEPRules() {
        List<CEPRule> CEPRules = new ArrayList<>();
        Cursor cursor = mDatabase.query( MHubSQLiteHelper.TABLE_CEP_RULES,
                allColumns, null, null, null, null, null
        );

        cursor.moveToFirst();
        while( !cursor.isAfterLast() ) {
            CEPRule CEPRule = cursorToCEPRule( cursor );
            CEPRules.add( CEPRule );
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return CEPRules;
    }

    /**
     * Transforms the cursor to a CEPRule
     * @param cursor The current cursor
     * @return The cursor as a CEPRule structure
     */
    private CEPRule cursorToCEPRule(Cursor cursor) {
        CEPRule cepRule = new CEPRule();
        cepRule.setId( cursor.getLong( 0 ) );
        cepRule.setLabel( cursor.getString( 1 ) );
        cepRule.setRule( cursor.getString( 2 ) );
        cepRule.setTarget( cursor.getString( 3 ) );
        cepRule.setPriority( cursor.getInt( 4 ) );
        cepRule.setState( cursor.getInt( 5 ) );
        cepRule.setCreated( cursor.getString( 6 ) );

        return cepRule;
    }
}
