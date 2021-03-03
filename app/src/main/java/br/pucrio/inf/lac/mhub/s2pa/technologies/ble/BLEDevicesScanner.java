package br.pucrio.inf.lac.mhub.s2pa.technologies.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Looper;

import br.pucrio.inf.lac.mhub.components.AppUtils;

class BLEDevicesScanner implements Runnable, BluetoothAdapter.LeScanCallback {
	private static final String TAG = BLEDevicesScanner.class.getSimpleName();
	
	private final BluetoothAdapter bluetoothAdapter;
	private final Handler mainThreadHandler = new Handler( Looper.getMainLooper() );
	private final LeScansPoster leScansPoster;
	private Thread scanThread;
	
	BLEDevicesScanner( BluetoothAdapter adapter, BluetoothAdapter.LeScanCallback callback ) {
        bluetoothAdapter = adapter;
        leScansPoster    = new LeScansPoster( callback );
    }
	
	private boolean isScanning() {
        return scanThread != null && scanThread.isAlive();
    }
	
	public synchronized void start() {
        if( isScanning() )
            return;

        AppUtils.logger( 'i', TAG, "Start Scan" );
        if( scanThread != null ) {
            scanThread.interrupt();
            scanThread = null;
        }
        
        scanThread = new Thread( this );
        scanThread.setName( TAG );
        scanThread.start();
    }
	
	synchronized void stop() {
		AppUtils.logger( 'i', TAG, "Stop Scan" );
        if( scanThread != null ) {
            scanThread.interrupt();
            scanThread = null;
        }
        
        bluetoothAdapter.stopLeScan( this );
    }
	
	@Override
	public void run() {
		synchronized( this ) {
			bluetoothAdapter.startLeScan( this );
		}
	}
	
	@Override
	public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
		synchronized(leScansPoster) {
            leScansPoster.set(device, rssi, scanRecord);
            mainThreadHandler.post(leScansPoster);
        }
	}
	
	private static class LeScansPoster implements Runnable {
        private final BluetoothAdapter.LeScanCallback leScanCallback;

        private BluetoothDevice device;
        private int rssi;
        private byte[] scanRecord;

        private LeScansPoster(BluetoothAdapter.LeScanCallback leScanCallback) {
            this.leScanCallback = leScanCallback;
        }

        public void set(BluetoothDevice device, int rssi, byte[] scanRecord) {
            this.device = device;
            this.rssi = rssi;
            this.scanRecord = scanRecord;
        }

        @Override
        public void run() {
            leScanCallback.onLeScan(device, rssi, scanRecord);
        }
    }
}
