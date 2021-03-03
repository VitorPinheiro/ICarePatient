package br.pucrio.inf.lac.mhub.s2pa.technologies.ble.operations;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

public class BLEWrite extends BLEOperation {
    private final Object mObject;
    private final byte[] mValue;

    public BLEWrite( BluetoothGatt bluetoothGatt, Object o, byte[] value ) {
        super( bluetoothGatt );
        mObject = o;
        mValue = value;
    }

    @Override
    public boolean execute() {
        if( mObject instanceof BluetoothGattCharacteristic ) {
            BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) mObject;
            characteristic.setValue( mValue );
            mBluetoothGatt.writeCharacteristic( characteristic );
        }
        else if( mObject instanceof BluetoothGattDescriptor ) {
            BluetoothGattDescriptor descriptor = (BluetoothGattDescriptor) mObject;
            descriptor.setValue( mValue );
            mBluetoothGatt.writeDescriptor( descriptor );
        }
        return true;
    }
}
