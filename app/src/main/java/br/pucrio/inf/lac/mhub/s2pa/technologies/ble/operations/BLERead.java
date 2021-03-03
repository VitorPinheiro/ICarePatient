package br.pucrio.inf.lac.mhub.s2pa.technologies.ble.operations;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

public class BLERead extends BLEOperation {
    // The characteristic or descriptor
    private final Object mObject;

    public BLERead( BluetoothGatt bluetoothGatt, Object o ) {
        super( bluetoothGatt );
        mObject = o;
    }

    @Override
    public boolean execute() {
        if( mObject instanceof BluetoothGattCharacteristic )
            mBluetoothGatt.readCharacteristic( (BluetoothGattCharacteristic) mObject );
        else if( mObject instanceof BluetoothGattDescriptor )
            mBluetoothGatt.readDescriptor( (BluetoothGattDescriptor) mObject );
        return true;
    }
}
