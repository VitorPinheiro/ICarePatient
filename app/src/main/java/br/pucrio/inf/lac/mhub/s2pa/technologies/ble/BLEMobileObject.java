package br.pucrio.inf.lac.mhub.s2pa.technologies.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import br.pucrio.inf.lac.mhub.components.AppUtils;
import br.pucrio.inf.lac.mhub.components.MOUUID;
import br.pucrio.inf.lac.mhub.s2pa.base.TechnologySensor;

/**
 * Created by luis on 28/09/15.
 * Representation of a Mobile Object in BLE
 */
public class BLEMobileObject extends BluetoothGattCallback {
    /** DEBUG */
    private static final String TAG = BLEMobileObject.class.getSimpleName();

    public static abstract class ServiceAction {
        public enum ActionType {
            READ,
            NOTIFY,
            WRITE
        }

        private final ActionType type;

        public ServiceAction( ActionType type ) {
            this.type = type;
        }

        public ActionType getType() {
            return type;
        }

        /***
         * Executes action.
         * @param bluetoothGatt The GATT of BLE
         * @return true - if action was executed instantly.
         *         false if action is waiting for feedback.
         */
        public abstract boolean execute(BluetoothGatt bluetoothGatt);
    }

    /** Gatt of the Mobile Object (Only when correctly connected) */
    private BluetoothGatt mGatt;

    /** Bluetooth Device */
    private BluetoothDevice mDevice;

    /** Mobile Object Unique Identifier */
    private MOUUID mMOUUID;

    /** Current RSSI */
    private Double mRSSI;

    /** Services provided */
    private List<String> mServices;

    /** Queue and flag to handle operations */
    private final Queue<Object> sWriteQueue = new ConcurrentLinkedQueue<>();
    private boolean sIsWriting = false;

    /** M-Object Configuration Descriptor */
    private final UUID CONFIG_DESCRIPTOR = UUID.fromString( "00002902-0000-1000-8000-00805f9b34fb" );

    public BLEMobileObject( BluetoothDevice device ) {
        final String macAddress = device.getAddress();

        mDevice   = device;
        mServices = new ArrayList<>();
        mMOUUID   = new MOUUID( BLETechnology.ID, macAddress );
    }

    @Override
    public void onConnectionStateChange( BluetoothGatt gatt, int status, int newState ) {
        final String macAddress = mDevice.getAddress();

        AppUtils.logger( 'i', TAG,
                macAddress + ": " + "Connection State Change: " +
                        AppUtils.gattState( status ) + " -> " +
                        AppUtils.connectionState( newState )
        );

            /* Successfully connected */
        if( status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED ) {
            AppUtils.logger( 'i', TAG, "=>Connected: " + macAddress );
            // Get the gatt object
            mGatt = gatt;
            // Add to connected devices
            /*mConnectedDevices.put( macAddress, this );
            // Inform to the S2PA Service
            listener.onMObjectConnected( mMOUUID );
            // Discovering services
            mGatt.discoverServices();*/
        }
            /* Disconnected */
        else if( newState == BluetoothProfile.STATE_DISCONNECTED ) {
            AppUtils.logger( 'i', TAG, "=>Disconnected: " + macAddress );
            // Close the gatt
            gatt.close();
            // Remove from connected devices
            /*mConnectedDevices.remove( macAddress );
            // Inform to the S2PA Service
            listener.onMObjectDisconnected( mMOUUID, mServices );
            // Start with next operation
            setCurrentOperation( null );
            doOperation();*/
        }
            /* If there is a failure at any stage */
        else if( status != BluetoothGatt.GATT_SUCCESS ) {
            AppUtils.logger( 'e', TAG, "=>Gatt Failed: " + macAddress );
            // Refresh the Gatt
            refreshDeviceCache( gatt );
            // Close the gatt
            gatt.close();
            // Remove from connected devices
            /*mConnectedDevices.remove( macAddress );
            // Inform to the S2PA Service
            listener.onMObjectDisconnected( mMOUUID, mServices );*/
        }
        // Remove the device from the waiting list (to connect)
        //sOperationsQueue.remove( mDevice );
    }

    @Override
    public void onServicesDiscovered( BluetoothGatt gatt, int status ) {
        final String macAddress = gatt.getDevice().getAddress();
        final String deviceName = gatt.getDevice().getName();

            /* Services discovered successfully */
        if( status == BluetoothGatt.GATT_SUCCESS ) {
            AppUtils.logger( 'i', TAG, "=>Services Discovered: " + macAddress );
            // Get the module for the device
            /*final TechnologyDevice device = mDeviceModules.get( deviceName );
            // Loop through the services
            for( BluetoothGattService temp : gatt.getServices() ) {
                // Get the sensor(s) of a service by its UUID
                List<TechnologySensor> sensors = device.getServiceByUUID( temp.getUuid() );
                // Enable the sensors and add to the sensor's list
                if( !sensors.isEmpty() )
                    subscribe( gatt, sensors );
            }*/
            // Inform to the S2PA Service
            /*listener.onMObjectServicesDiscovered( mMOUUID, mServices );
            // Continue with the next operation
            write( sCurrentOperation );*/
        }
            /* If there is a failure at any stage */
        else {
            AppUtils.logger( 'e', TAG, "=>Services Discovery Failed: " + macAddress );
            // Refresh the Gatt
            refreshDeviceCache( gatt );
            // Close the gatt
            gatt.close();
            // Remove from connected devices
            //mConnectedDevices.remove( macAddress );
            // Inform to the S2PA Service
            //listener.onMObjectDisconnected( mMOUUID, mServices );
            // Start with next operation
            /*setCurrentOperation( null );
            doOperation();*/
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        // Continue with the next operation
        /*setCurrentOperation( null );
        doOperation();*/
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        // Continue with the next operation
        /*setCurrentOperation( null );
        doOperation();*/
    }

    @Override
    public void onCharacteristicWrite( BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        // Read finished
        sIsWriting = false;
        // Continue with the next write
        nextWrite();
    }

    @Override
    public void onDescriptorWrite( BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status ) {
        // Read finished
        sIsWriting = false;
        // Continue with the next write
        nextWrite();
    }

    @Override
    public void onReadRemoteRssi( BluetoothGatt gatt, int rssi, int status ) {
            /* RSSI obtained successfully */
        if( status == BluetoothGatt.GATT_SUCCESS ) {
            mRSSI = (double) rssi;
            // If RSSI is lower than the allowed, disconnect
            /*allowedRssi = AppUtils.getCurrentSignalAllowedMO( ac );
            if( mRSSI < allowedRssi )
                queueOperation( new BLEDisconnect( mGatt ) );*/
        }
        // Continue with the next operation
        /*setCurrentOperation( null );
        doOperation();*/
    }

    @Override
    public void onCharacteristicChanged( BluetoothGatt gatt, BluetoothGattCharacteristic characteristic ) {
        final String deviceName = gatt.getDevice().getName();

        // Get the module for the device
        /*final TechnologyDevice device = mDeviceModules.get( deviceName );
        // Get sensor by UUID
        final TechnologySensor sensor = device.getCharacteristicByUUID( characteristic.getUuid() );
        //final TechnologySensor sensor = device.getServiceByUUID( characteristic.getService().getUuid() );
        // Transform raw data to an array of doubles
        final Double[] data = sensor.convert( characteristic.getValue() );
        // Inform to the S2PA Service
        listener.onMObjectValueRead( mMOUUID, mRSSI, sensor.getName(), data );*/
    }

    /**
     * Enables sensor and notifications of a service
     * @param gatt The Gatt connection in BLE
     * @param sensors The representation of the sensors
     */
    private void  subscribe( final BluetoothGatt gatt, final List<TechnologySensor> sensors ) {
        for( TechnologySensor sensor : sensors ) {
            AppUtils.logger( 'i', TAG, "=>Enabling " + sensor.getName() );
            mServices.add( sensor.getName() );
            // Get enabler code
            byte[] ENABLE_SENSOR = new byte[ 1 ];
            ENABLE_SENSOR[ 0 ] = sensor.getEnableSensorCode();
            // Get UUIDS of the service, data and configurations
            final UUID UUID_SERVICE = sensor.getService();
            final UUID UUID_DATA = sensor.getData();
            final UUID UUID_CONF = sensor.getConfig();
            // Get the service and characteristics by UUID
            BluetoothGattService serv = gatt.getService( UUID_SERVICE );
            BluetoothGattCharacteristic characteristic = serv.getCharacteristic( UUID_DATA );
            // Get configuration descriptor
            BluetoothGattDescriptor config = characteristic.getDescriptor( CONFIG_DESCRIPTOR );
            gatt.setCharacteristicNotification( characteristic, true );
            // Enable the service
            if( UUID_CONF != null ) {
                BluetoothGattCharacteristic configuration = serv.getCharacteristic( UUID_CONF );
                configuration.setValue( ENABLE_SENSOR );
                gatt.writeCharacteristic( configuration );
                write( configuration );
            }
            // Enable the notifications for the service
            config.setValue( BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE );
            gatt.writeDescriptor( config );
            // Write or add to waiting list
            write( config );
        }
    }

    /**
     * Refresh the cache information of the GATT
     * @param gatt the GATT desired to refresh
     * @return If it was successful
     */
    private boolean refreshDeviceCache( BluetoothGatt gatt ){
        try {
            // Get method by reflection
            Method localMethod = gatt.getClass().getMethod( "refresh", new Class[0] );
            if( localMethod != null ) {
                return (Boolean) localMethod.invoke( gatt, new Object[0] );
            }
        } catch( Exception ex ) {
            AppUtils.logger( 'e', TAG, "An exception occurred while refreshing device: " + ex.getMessage() );
        }
        return false;
    }

    /**
     * Allow only one write at a time
     * @param o The object to write, can be a characteristic, descriptor or Bluetooth Device
     */
    private synchronized void write( final Object o ) {
        // It is okay to write
        if( sWriteQueue.isEmpty() && !sIsWriting )
            doWrite( o );
            // Add to waiting list
        else sWriteQueue.add( o );
    }

    /**
     * Try to start with the next write
     */
    private synchronized void nextWrite() {
        // It is okay to write
        if( !sWriteQueue.isEmpty() && !sIsWriting )
            doWrite( sWriteQueue.poll() );
    }

    /**
     * Writes the object
     * @param o Object to be write
     */
    private synchronized void doWrite( final Object o ) {
        if( o instanceof BluetoothGattCharacteristic ) {
            sIsWriting = true;
            mGatt.writeCharacteristic( (BluetoothGattCharacteristic) o );
        } else if( o instanceof BluetoothGattDescriptor ) {
            sIsWriting = true;
            mGatt.writeDescriptor( (BluetoothGattDescriptor) o );
        } else if( o instanceof BluetoothDevice ) {
            /*setCurrentOperation( null );
            doOperation();*/
        } else {
            nextWrite();
        }
    }
}
