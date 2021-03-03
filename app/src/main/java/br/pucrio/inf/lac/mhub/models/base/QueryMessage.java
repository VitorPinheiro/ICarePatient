package br.pucrio.inf.lac.mhub.models.base;

import android.util.JsonReader;

import java.io.IOException;

/**
 * Created by luis on 13/05/15.
 * Query representation of an external message
 */
public abstract class QueryMessage {
    /** Types of queries */
    public enum ACTION {
        ADD   ( "add" ),
        REMOVE( "remove" ),
        START ( "start" ),
        STOP  ( "stop" ),
        CLEAR ( "clear" ),
        GET   ( "get" );

        private String value;

        ACTION(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static ACTION fromString(String text) {
            if( text != null ) {
                for( ACTION b : ACTION.values() ) {
                    if( text.equalsIgnoreCase( b.value ) )
                        return b;
                }
            }
            throw new IllegalArgumentException( "No constant with text " + text + " found" );
        }
    }

    /** Types of objects */
    public enum ITEM {
        RULE  ( "rule" ),
        EVENT ( "event" );

        private String value;

        ITEM( String value ) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static ITEM fromString(String text) {
            if( text != null ) {
                for( ITEM b : ITEM.values() ) {
                    if( text.equalsIgnoreCase( b.value ) )
                        return b;
                }
            }
            throw new IllegalArgumentException( "No constant with text " + text + " found" );
        }
    }

    /** Types of targets */
    public enum ROUTE {
        LOCAL  ( "local" ),
        GLOBAL ( "global" );

        private String value;

        ROUTE( String value ) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static ROUTE fromString( String text ) throws IllegalArgumentException {
            if( text != null ) {
                for( ROUTE b : ROUTE.values() ) {
                    if( text.equalsIgnoreCase( b.value ) )
                        return b;
                }
            }
            throw new IllegalArgumentException( "No constant with text " + text + " found" );
        }
    }

    /** JSON Keys */
    protected static final String TYPE      = "type";   // e.g. "add", "remove", "get", or "clear"
    protected static final String OBJECT    = "object"; // e.g. "rule", "event"
    protected static final String LABEL     = "label";  // e.g. "HighTemperature"
    protected static final String RULE      = "rule";   // e.g. [ "SELECT * FROM SensorData;", ... ]
    protected static final String EVENT     = "event";  // e.g. [ [ "var1", "double" ], [ "var2", "string" ] ]
    protected static final String DEVICE    = "device"; // [ "1-bc:6a:29:ac:68:04", ... ]
    protected static final String TARGET    = "target"; // "white", "black" (2PA) "local", "global" (MEPA)
    protected static final String ACTUATION = "actuation"; // The name of the action class (e.g. SoundAction)

    /**
     * Creates an object from a JSON structure
     * @param reader The Json Data
     */
    public abstract void fromJSON(JsonReader reader) throws IOException;
}
