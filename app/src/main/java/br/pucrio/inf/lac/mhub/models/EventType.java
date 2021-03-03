package br.pucrio.inf.lac.mhub.models;

/**
 * Created by luis on 9/09/15.
 * Describes event types for the CEP engine
 */
public class EventType {
    /** Attributes */
    private long id;
    private String label;
    private String properties;
    private String created;

    /** Getters */
    public long getId() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    public String getProperties() {
        return properties;
    }

    public String getCreated() {
        return created;
    }

    /** Setters */
    public void setId( long id ) {
        this.id = id;
    }

    public void setLabel( String label ) {
        this.label = label;
    }

    public void setProperties( String properties ) {
        this.properties = properties;
    }

    public void setCreated( String created ) {
        this.created = created;
    }
}
