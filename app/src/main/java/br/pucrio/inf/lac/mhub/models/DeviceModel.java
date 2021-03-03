package br.pucrio.inf.lac.mhub.models;

import android.os.ResultReceiver;

/**
 * Created by luis on 1/07/15.
 * POJO for the device name to look for
 * its module
 */
public class DeviceModel {
    public final String name;
    public final ResultReceiver receiver;

    public DeviceModel( String name, ResultReceiver receiver ) {
        this.name = name;
        this.receiver = receiver;
    }
}
