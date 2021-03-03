package br.pucrio.inf.lac.mhub.s2pa.technologies.ble.operations;

import android.bluetooth.BluetoothGatt;

public class BLEReadRssi extends BLEOperation {
	public BLEReadRssi(BluetoothGatt bluetoothGatt) {
		super(bluetoothGatt);
	}
	
	@Override
	public boolean execute() {
		mBluetoothGatt.readRemoteRssi();
		return true;
	}
}
