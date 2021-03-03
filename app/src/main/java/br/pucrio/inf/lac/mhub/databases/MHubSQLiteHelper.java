package br.pucrio.inf.lac.mhub.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by luis on 28/05/15.
 * Database helper for the SQLite
 */
public class MHubSQLiteHelper extends SQLiteOpenHelper {
    /** Table CEP Rules attributes names */
    public static final String TABLE_CEP_RULES = "CEPRules";
    public static final String COLUMN_ID       = "id";
    public static final String COLUMN_LABEL    = "label";
    public static final String COLUMN_RULE     = "rule";
    public static final String COLUMN_TARGET   = "target";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_STATE    = "state";
    public static final String COLUMN_CREATED  = "created";

    /** Table CEP Events attributes names */
    public static final String TABLE_EVENT_TYPES = "EventTypes";
    public static final String COLUMN_PROPERTIES = "properties";

    /** Database basics */
    private static final String DATABASE_NAME = "mobile_hub.db";
    private static final int DATABASE_VERSION = 5;

    /** SQL-sentence to create the table CEPRules */
    private static final String sqlCreateCEPRules = "CREATE TABLE " + TABLE_CEP_RULES + " (" +
            COLUMN_ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, "  +
            COLUMN_LABEL    + " TEXT NOT NULL, "    + COLUMN_RULE     + " TEXT NOT NULL, " +
            COLUMN_TARGET   + " TEXT NOT NULL, "    + COLUMN_PRIORITY + " INTEGER NOT NULL, " +
            COLUMN_STATE    + " INTEGER NOT NULL, " + COLUMN_CREATED  + " DATETIME NOT NULL)";

    /** SQL-sentence to create the table CEPRules */
    private static final String sqlCreateEventType = "CREATE TABLE " + TABLE_EVENT_TYPES + " (" +
            COLUMN_ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, "    +
            COLUMN_LABEL    + " TEXT NOT NULL, "    + COLUMN_PROPERTIES + " TEXT NOT NULL, " +
            COLUMN_CREATED  + " DATETIME NOT NULL)";

    public MHubSQLiteHelper( Context context ) {
        super( context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate( SQLiteDatabase db ) {
        // Creates the database tables
        db.execSQL( sqlCreateCEPRules );
        db.execSQL( sqlCreateEventType );
    }

    @Override
    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
        if( newVersion > oldVersion ) {
            // Deletes the previous tables
            db.execSQL( "DROP TABLE IF EXISTS " + TABLE_CEP_RULES );
            db.execSQL( "DROP TABLE IF EXISTS " + TABLE_EVENT_TYPES );
            // Creates a new table
            onCreate( db );
        }
    }
}
