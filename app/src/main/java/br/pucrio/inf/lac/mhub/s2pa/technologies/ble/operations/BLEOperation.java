package br.pucrio.inf.lac.mhub.s2pa.technologies.ble.operations;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

public abstract class BLEOperation {
	protected BluetoothGatt mBluetoothGatt;

	/**
	 * Constructor, sets gatt
	 * @param bluetoothGatt The bluetooth gatt connection
	 */
	public BLEOperation( BluetoothGatt bluetoothGatt ) {
		mBluetoothGatt = bluetoothGatt;
	}

	/**
	 * Gets the device for the gatt connection
	 * @return The Bluetooth device representation
	 */
	public BluetoothDevice getDevice() {
		return mBluetoothGatt.getDevice();
	}

	/***
	 * Executes action.
	 * @return true - if action is waiting for a callback.
	 *         false - if action was executed instantly.
	 */
	public abstract boolean execute();
}
