package br.pucrio.inf.lac.mhub.s2pa.technologies.bt;

/**
 * @author bertodetacio
 */

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.UUID;

import br.pucrio.inf.lac.mhub.components.MOUUID;
import br.pucrio.inf.lac.mhub.s2pa.base.TechnologyDevice;
import br.pucrio.inf.lac.mhub.s2pa.base.TechnologyListener;


@SuppressLint("NewApi")
public class BTMobileObjectsManager {

    private BTDevicesScanner btDevicesScanner = BTDevicesScanner
            .getisntance();

    private BluetoothAdapter bluetoothAdapter = null;

    // UUID para conexões com dispostivos Android
    private static final UUID UUID_ANDROID_DEVICE = UUID
            .fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");


    // UUID para conexões com outros dispositivos
    private static final UUID UUID_OTHER_DEVICE = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Instãncia única desta classe (padrão singleton)
    private static BTMobileObjectsManager instance = null;

    //Tabela Hash de Objeto Móveis para a Tecnologia Bluetooth Classic
    private Hashtable<String, BTMobileObject> btMobileObjects = new Hashtable<String, BTMobileObject>();

    //mapeia o nome do device com o nome da classe que contem o driver
    private Hashtable<String, String> devices = new Hashtable<String, String>();

    //instancia do listener da tecnologia bluetooth classic
    private BTTechnology btTechnology = null;

    private TechnologyListener btTechnologyListener = null;

    private AcceptThread acceptThread = null;





    private BTMobileObjectsManager() {
        super();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btTechnology = BTTechnology.getInstance();
    }

    // recupera a instancia desta classe
    public static BTMobileObjectsManager getInstance() {
        if (instance == null) {
            instance = new BTMobileObjectsManager();
        }
        return instance;
    }


    public boolean connect(MOUUID mouuid) {
        return connect(mouuid.getAddress());
    }


    public boolean connect(String macAddress) {

        if (!btDevicesScanner.isEnable()) {
            return false;
        }

        BTMobileObject btMobileObject = getBTMobileObjectByMacAddress(macAddress);

        if (btMobileObject != null) {
            return connect(btMobileObject);
        } else {
            BluetoothDevice btDevice = btDevicesScanner
                    .foundBTDevice(macAddress);
            if (btDevice != null) {

                String deviceName = btDevice.getName();
                MOUUID mouuid = new MOUUID(BTTechnology.ID, macAddress);
                btMobileObject = createBTMobileObject(mouuid, deviceName, 0);

                if (btMobileObject != null) {

                    if (btMobileObject.isConnected()) {
                        return true;
                    } else if (btDevicesScanner.isIgnoreNotPairedDevices()
                            && !(btDevice.getBondState() == BluetoothDevice.BOND_BONDED)) {
                        return false;
                    }
                    return connect(btMobileObject);
                }
            }
            return false;
        }
    }


    public boolean connect(BTMobileObject btMobileObject) {

        if (!btDevicesScanner.isEnable()) {
            return false;
        }

        if (btMobileObject == null) {
            return false;
        }

        if (btMobileObject.isConnected()) {
            return true;
        }

        BluetoothDevice bluetoothDevice = null;
        BluetoothSocket bluetoothSocket = null;
        String deviceName = null;
        Log.i("Log", "Try connect to " + btMobileObject.getMouuid().getAddress());
        // Inicia uma conexão somente se o adaptador bluetooth estiver ativo
        if (!btDevicesScanner.isEnable()) {
            Log.i("Log", "Could not connect to device. MAC address" + btMobileObject.getMouuid().getAddress());
            Log.i("Log", "Bluetooth Not Enable");
            return false;
        } else {
            boolean isMacAddressValid = BluetoothAdapter
                    .checkBluetoothAddress(btMobileObject.getMouuid().getAddress());
            if (!isMacAddressValid) {
                Log.i("Log", "Could not connect to device. MAC address " + btMobileObject.getMouuid().getAddress());
                Log.i("Log", "Mac Address is Not Valid");
                return false;
            } else {
                bluetoothDevice = bluetoothAdapter
                        .getRemoteDevice(btMobileObject.getMouuid().getAddress());

                if (bluetoothDevice == null) {

                    Log.i("Log", "Could not connect to device. MAC address " + btMobileObject.getMouuid().getAddress());
                    return false;
                } else {
                    deviceName = bluetoothDevice.getName();
                    try {
                        bluetoothSocket = bluetoothDevice
                                .createRfcommSocketToServiceRecord(UUID_OTHER_DEVICE);
                        bluetoothSocket.connect();
                        btMobileObject.onConnected(bluetoothSocket);
                        return true;

                    } catch (IOException e) {
                        Log.e("Log", e.getMessage());
                        // try another method for connection, this should work
                        // on the HTC desire, credits to Michael Biermann
                        try {
                            Method mMethod = bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                            bluetoothSocket = (BluetoothSocket) mMethod.invoke(bluetoothDevice, Integer.valueOf(1));
                            bluetoothSocket.connect();
                            btMobileObject.onConnected(bluetoothSocket);
                            return true;
                        } catch (Exception ex) {
                            Log.e("Log", "Could not connect to the Device " + deviceName + ". MAC address " + btMobileObject.getMouuid().getAddress());
                            return false;
                        }
                    }
                }
            }
        }
    }

    public boolean disconnect(String macAddress) {
        BTMobileObject btMobileObject = getBTMobileObjectByMacAddress(macAddress);
        if (btMobileObject != null) {
            return disconnect(btMobileObject);
        } else {
            return false;
        }
    }

    public boolean disconnect(MOUUID mouuid) {
        BTMobileObject btMobileObject = getBTMobileObjectByMOUUID(mouuid);
        if (btMobileObject != null) {
            return disconnect(btMobileObject);
        } else {
            return false;
        }
    }

    public boolean disconnect(BTMobileObject btMobileObject) {
        if (btMobileObject != null) {
            try {
                btMobileObject.onDisconnected();
                return !btMobileObject.isConnected();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;
    }


    public BTMobileObject createBTMobileObject(MOUUID mouuid, String deviceName, double rssi) {

        BTMobileObject btMobileObject = null;

        TechnologyDevice technologyDevice = null;

        String className = null;

        Class<?> clazz;

        if(deviceName!=null) {

            if (btMobileObjects.contains(mouuid)) {
                btMobileObject = getBTMobileObjectByMOUUID(mouuid);
            } else {
                if (devices.containsKey(deviceName)) {
                    className = devices.get(deviceName);
                } else {

                    deviceName = deviceName.replace(" ", "").trim();

                    className = "br.pucrio.inf.lac.mhub.s2pa.technologies.bt.devices." + deviceName + "_Device";
                }

                try {
                    clazz = Class.forName(className);
                    technologyDevice = (TechnologyDevice) clazz.newInstance();
                    btMobileObject = (BTMobileObject) technologyDevice;
                    btMobileObject.setMouuid(mouuid);
                    btMobileObject.setDeviceName(deviceName);
                    btMobileObject.setRssi(rssi);
                    btMobileObjects.put(mouuid.toString(), btMobileObject);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return btMobileObject;

    }

    public void readSensorValue(String macAddress, String serviceName){
        MOUUID mouuid = new MOUUID(BTTechnology.ID,macAddress);
        BTMobileObject btMobileObject = btMobileObjects.get(mouuid);
        btMobileObject.readSensorValue(serviceName);
    }

    public BTMobileObject getBTMobileObjectByMacAddress(String macAddress) {
        MOUUID mouuid = new MOUUID(BTTechnology.ID, macAddress);
        return getBTMobileObjectByMOUUID(mouuid);
    }


    public BTMobileObject getBTMobileObjectByMOUUID(MOUUID mouuid) {
        return btMobileObjects.get(mouuid.toString());
    }

    public boolean containsBTMobileObject(MOUUID mouuid) {
        return btMobileObjects.containsKey(mouuid.toString());
    }

    public boolean containsMobileObject(String macAddress) {
        MOUUID mouuid = new MOUUID(BTTechnology.ID, macAddress);
        return containsBTMobileObject(mouuid);
    }

    public void registerDeviceDriver(String device, String clazz) {
        if (!devices.containsKey(device)) {
            devices.put(device, clazz);
        }
    }

    public void acceptConnections() {
        if (acceptThread == null) {
            acceptThread = new AcceptThread();
        }
    }

    public void acceptConnections(int timeout) {
        if (acceptThread == null) {
            acceptThread = new AcceptThread(timeout);
        }
    }

    public void noAcceptMoreConnections() {
        if (acceptThread != null) {
            acceptThread.close();
            acceptThread = null;
        }
    }

    public void closeAllConnections() {
        if (acceptThread != null) {
            acceptThread.close();
            acceptThread = null;
        }
        for (BTMobileObject btMobileObject : btMobileObjects.values()) {
            disconnect(btMobileObject);
        }
        btMobileObjects.clear();
    }




    private class AcceptThread extends Thread {

        private int timeout = 0;

        BluetoothAdapter bluetoothAdapter = null;
        BluetoothServerSocket bluetoothServerSocket = null;

        BluetoothSocket bluetoothSocket = null;

        private boolean finished = false;

        public AcceptThread() {
            start();
        }

        public AcceptThread(int timeout) {
            this.timeout = timeout;
            new AcceptThread();
        }


        public void run() {
            init();
            while (!finished) {
                accept();
                createBTMobileObject();
            }
        }

        private void init() {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                try {
                    bluetoothServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("MHub", UUID_OTHER_DEVICE);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        private boolean accept() {
            try {
                if (timeout <= 0) {
                    bluetoothSocket = bluetoothServerSocket.accept();
                } else {
                    bluetoothSocket = bluetoothServerSocket.accept(timeout);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
            return true;
        }

        private void createBTMobileObject() {

            String deviceName = null;

            String macAddress = null;

            double rssi = 0;

            if (!isConnected()) {

                BluetoothDevice btDevice = bluetoothSocket.getRemoteDevice();

                btDevice.fetchUuidsWithSdp();

                ParcelUuid[] uuids = btDevice.getUuids();

                for (ParcelUuid uuid : uuids) {
                    Log.i("Log", "UUID = " + uuid.toString());
                }

                deviceName = btDevice.getName();
                macAddress = btDevice.getAddress();
                boolean paired = btDevice.getBondState() == BluetoothDevice.BOND_BONDED;

                Log.i("Log", "Mobile Device " + deviceName + " Found. MAc Addres: " + macAddress + ". RSSI: " + rssi + ". Paired: " + paired + ".");

                MOUUID mouuid = new MOUUID(BTTechnology.ID, macAddress);

                BTMobileObject btMobileObject = null;

                if (!instance.containsBTMobileObject(mouuid)) {
                    btMobileObject = instance.createBTMobileObject(mouuid, deviceName, rssi);
                } else {
                    btMobileObject = instance.getBTMobileObjectByMOUUID(mouuid);
                }

                if (btMobileObject != null) {
                    btTechnologyListener = btTechnology.getTechnologyListener();
                    if (btTechnologyListener != null) {
                        //Comunica o Listener que encotrou um objeto móvel
                        btTechnologyListener.onMObjectFound(mouuid, rssi);
                        btMobileObject.onConnected(bluetoothSocket);
                    }
                }

            }
        }



        private boolean isConnected() {
            if (bluetoothSocket == null || !bluetoothSocket.isConnected()) {
                return false;
            } else {
                return true;
            }
        }


        public synchronized void close() {
            if (bluetoothServerSocket != null) {
                try {
                    bluetoothServerSocket.close();
                    bluetoothServerSocket = null;
                    bluetoothSocket = null;
                    finished = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }


}
