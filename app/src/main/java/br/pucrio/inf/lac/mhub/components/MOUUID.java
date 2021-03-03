package br.pucrio.inf.lac.mhub.components;

import java.util.Locale;
import java.util.UUID;

/**
 * Unique identifier for the M-OBJs
 */
public class MOUUID {
	/** Identifier of the technology */
	private int technologyID;

	/** MAC address of the mobile object */
	private String mObjectAddress;

	/** ICON for the device */
	private String mIcon;
	
	public MOUUID( int TechnologyID, String mObjectAddress ) {
		this.technologyID   = TechnologyID;
		this.mObjectAddress = mObjectAddress.replace( ":", "" );
	}
	
	public int getTechnologyID() {
		return this.technologyID;
	}
	
	public String getAddress() {
		return this.mObjectAddress;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other == null)
			return false;
		
		if(!(other instanceof MOUUID))
			return false;
		
		if(other == this)
			return true;
		
		MOUUID temp = (MOUUID) other;
        return temp.getTechnologyID() == technologyID && temp.getAddress().equals( mObjectAddress );
    }
	
	@Override
	public String toString() {
		return this.technologyID + "-" + this.mObjectAddress;
	}
	
	/**
	 * Transform the MOUUD to a UUID
	 * @return UUID 
	 */
	public UUID toUUID() {
		String techID = String.format( Locale.US, "%05d", this.technologyID );
		return UUID.fromString( "00000000-0000-0000-" + techID + "-" + this.mObjectAddress );
	}
	
	/**
	 * Creates a MOUUD from a UUID
	 * @param uuid The UUID
	 * @return MOUUID
	 */
	public static MOUUID fromUUID( UUID uuid ) {
        String[] parts = uuid.toString().split( "-" );

        String techID = parts[3];
        String MAC    = parts[4];

        /*StringBuilder bAddress = new StringBuilder( MAC );
        int idx = bAddress.length() - 2;

        while (idx > 0) {
            bAddress.insert( idx, ":" );
            idx = idx - 2;
        }*/

        return new MOUUID( Integer.valueOf( techID ), MAC );
	}

    /**
     * Creates a MOUUD from a UUID
     * @param uuid The UUID
     * @return MOUUID
     */
    public static MOUUID fromString( String uuid ) throws StringIndexOutOfBoundsException {
        String[] parts = uuid.split( "-" );

        String techID = parts[0];
        String MAC    = parts[1];

        /*StringBuilder bAddress = new StringBuilder( MAC );
        int idx = bAddress.length() - 2;

        while (idx > 0) {
            bAddress.insert( idx, ":" );
            idx = idx - 2;
        }*/

        return new MOUUID( Integer.valueOf( techID ), MAC );
    }
}
