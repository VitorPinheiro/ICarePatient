package br.pucrio.inf.lac.mhub.models;

import com.espertech.esper.client.EPStatement;

import br.pucrio.inf.lac.mhub.models.base.QueryMessage;

/**
 * Created by luis on 28/05/15.
 * POJO for the CEP Rules, so they can be stored in the database
 */
public class CEPRule {
    /** Attributes */
    private long id;
    private String label;
    private String rule;
    private String target;
    private int priority;
    private int state;
    private String created;

    /** CEP statement */
    private EPStatement statement;

    /** Priority options */
    public static final int HIGH   = 2;
    public static final int MEDIUM = 1;
    public static final int LOW    = 0;

    /** State options */
    public static final int ACTIVE   = 1;
    public static final int INACTIVE = 0;

    /** Getters */
    public long getId() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    public String getRule() {
        return this.rule;
    }

    public QueryMessage.ROUTE getTarget() {
        return QueryMessage.ROUTE.fromString( this.target );
    }

    public int getPriority() {
        return this.priority;
    }

    public int getState() {
        return this.state;
    }

    public String getCreated() {
        return this.created;
    }

    public EPStatement getStatement() {
        return this.statement;
    }
    /** Getters */

    /** Setters */
    public void setId( long id ) {
        this.id = id;
    }

    public void setLabel( String label ) {
        this.label = label;
    }

    public void setRule( String rule ) {
        this.rule = rule;
    }

    public void setTarget( String target ) {
        this.target = target;
    }

    public void setPriority( int priority ) {
        this.priority = priority;
    }

    public void setState( int state ) {
        this.state = state;
    }

    public void setCreated( String created ) {
        this.created = created;
    }

    public void setStatement( EPStatement statement ) {
        this.statement = statement;
    }
    /** Setters */
}
