package br.pucrio.inf.lac.mhub.managers;

import android.content.Context;
import android.os.BatteryManager;

/**
 * Created by luis on 29/05/15.
 * Handles the behavior of the Services based in some
 * variables like current battery level, power consumption,
 * mobility, etc.
 */
public class EnergyManager {
    /** Instance for the singleton */
    private static EnergyManager instance;

    private EnergyManager(Context ac) {
        BatteryManager mBatteryManager = (BatteryManager) ac.getSystemService( Context.BATTERY_SERVICE );
        //Long energy = mBatteryManager.getLongProperty( BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER );
    }

    public static EnergyManager getInstance(Context ac) {
        if( instance == null )
            instance = new EnergyManager( ac );
        return instance;
    }
}
