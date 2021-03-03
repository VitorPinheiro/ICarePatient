package br.pucrio.inf.lac.mhub.s2pa.technologies.ble.operations;

import android.bluetooth.BluetoothGatt;

public class BLEDisconnect extends BLEOperation {
	public BLEDisconnect(BluetoothGatt bluetoothGatt) {
		super(bluetoothGatt);
	}
	
	@Override
	public boolean execute() {
		mBluetoothGatt.disconnect();
		return true;
	}
}
