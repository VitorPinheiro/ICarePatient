package br.pucrio.inf.lac.mhub.s2pa.technologies.bt;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.ArrayList;

import br.pucrio.inf.lac.mhub.components.AppUtils;
import br.pucrio.inf.lac.mhub.components.MOUUID;
import br.pucrio.inf.lac.mhub.s2pa.base.TechnologyListener;

@SuppressLint("NewApi")
public class BTDevicesScanner {

    // Adaptador Genérico para a Tecnologia Bluetooth
    private BluetoothAdapter bluetoothAdapter = null;

    // Única instancia desta tecnologia. Visível por todos os componentes da
    // aplicação.
    private static BTDevicesScanner instance = null;

    // flag de parada do scanner
    private boolean scanStoped = true;

    // flag de repetição do scan
    private boolean repeatScan = false;

    // /indica que deve autonectar quando encontrar um objeto móvel
    private boolean autoconnect = false;

    // indica que deve autonectar apenas com dispositivos pareados
    private boolean ignoreNotPairedDevices = true;

    private boolean initialized = false;

    private long timeBetweenScans = 3000;

    // Tag utilizada por todos os Mobile Hubs
    private static final String MOBILE_HUB_DEVICE = "MHub";

    // Lista de dispositivos procurados
    // Se a lista estiver vazia, entende-se que deve-se buscar todos e qualquer
    // dispostivo
    private ArrayList<String> specificDevices = new ArrayList<>();

    // Broadcast Receiver
    private BTBroadcastReceiver btBroadcastReceiver = null;

    private BTDevicesScanner() {
        super();
    }


       public static BTDevicesScanner getisntance() {
        if (instance == null) {
            instance = new BTDevicesScanner();
        }
        return instance;
    }

    public void registerReceiver(Context context) {
        if (btBroadcastReceiver == null) {
            btBroadcastReceiver = BTBroadcastReceiver.getInstance();
            btBroadcastReceiver.registerReceviver(context);
        }
    }

    public void unRegisterReceiver(Context context) {
        if (btBroadcastReceiver != null) {
            btBroadcastReceiver.unregisterReceviver(context);
        }
    }

    public boolean initialize() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // adaptador nulo ou sem endereço implica em impossibilidade de
        // inicializar a tecnologia

        if (bluetoothAdapter == null
                || bluetoothAdapter.getAddress() == null) {
            setInitialized(false);
            Log.i("Log", "Can't Initialze Scan. Bluetooth not avaliable");
            setInitialized(false);

            return false;

        } else {
            setInitialized(true);
            Log.i("Log", "Bluetooth Technology Initialized");
            return true;
        }
    }

    public synchronized boolean enable() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.enable()) {
            Log.i("Log", "Bluetooth Techonology Not Enable");
        } else {
            Log.i("Log", "Bluetooth Technology Enable");
        }
        return bluetoothAdapter.isEnabled();
    }

    public boolean isEnable() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return !(bluetoothAdapter == null || !bluetoothAdapter.isEnabled());
    }

    public boolean isScanning() {
        return isEnable() && bluetoothAdapter.isDiscovering();
    }

    public synchronized boolean startScan(boolean autoconnect) {
        setAutoconnect(autoconnect);
        return startScan();
    }

    public synchronized boolean startScan() {

        if (isEnable()) {
            Log.i("Log", "Start Scan");
            scanStoped = false;

            return bluetoothAdapter.startDiscovery();
        } else {
            Log.i("Log", "Can't Start Scan. Bluetooth disable");
            return false;
        }
    }

    public synchronized boolean stopScan() {
        if (isEnable()) {
            Log.i("Log", "Stop Scan");

            return bluetoothAdapter.cancelDiscovery();
        } else {
            Log.i("Log", "Can't Stop Scan. Bluetooth disable");
            return false;
        }
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public boolean isStopedScan() {
        return scanStoped;
    }

    public synchronized boolean addSearchDevice(String MACaddres) {
        return specificDevices.add(MACaddres);
    }

    public synchronized boolean removeSearchDevice(String MACAddres) {
        return specificDevices.remove(MACAddres);
    }

    public boolean isSearchForSpecificDevices() {
        return !specificDevices.isEmpty();
    }

    public boolean specificDevicesConstains(String MACAddres) {
        return specificDevices.contains(MACAddres);
    }

    public boolean isScanStoped() {
        return scanStoped;
    }

    public boolean isAutoconnect() {
        return autoconnect;
    }

    public void setAutoconnect(boolean autoconnect) {
        this.autoconnect = autoconnect;
    }

    public boolean isIgnoreNotPairedDevices() {
        return ignoreNotPairedDevices;
    }

    public void setIgnoreNotPairedDevices(boolean ignoreNotPairedDevices) {
        this.ignoreNotPairedDevices = ignoreNotPairedDevices;
    }

    public boolean isRepeatScan() {
        return repeatScan;
    }

    public void setRepeatScan(boolean repeatScan) {
        this.repeatScan = repeatScan;
    }

    public long getTimeBetweenScans() {
        return timeBetweenScans;
    }

    public void setTimeBetweenScans(long timeBetweenScans) {
        this.timeBetweenScans = timeBetweenScans;
    }

    public BluetoothDevice foundBTDevice(String macAddress) {

        boolean isMacAddressValid = BluetoothAdapter
                .checkBluetoothAddress(macAddress);
        if (!isMacAddressValid) {
            return null;
        } else {

            return BluetoothAdapter.getDefaultAdapter()
                    .getRemoteDevice(macAddress);

        }
    }

    public void turnVisible(long timeVisible) {
        BTTechnology btTechnology = BTTechnology.getInstance();
        if (isEnable() && btTechnology != null) {
            bluetoothAdapter.setName(MOBILE_HUB_DEVICE);
            Context context = btTechnology.getContext();
            if (context != null) {
                Intent discoverableIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(
                        BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                        timeVisible);
                discoverableIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(discoverableIntent);
            }
        }
    }

    private static class BTBroadcastReceiver extends BroadcastReceiver {

             // instancia da Tecnologia Bluetooth Classic
        private BTTechnology btTechnology = BTTechnology.getInstance();

        // Instancia do Scan de Dispostivos do Bluetooth Classic
        private BTDevicesScanner btDevicesScanner = BTDevicesScanner
                .getisntance();

        // Instancia do gereniador de conexoes
        private BTMobileObjectsManager btMobileObjectsManager = BTMobileObjectsManager
                .getInstance();

        // Instancia do Listener de Eventos da Tecnologia Bluetooth Classic
        TechnologyListener btTechnologyListener = null;

        // Lista de ações de interesse dente Broacast Receiver
        private String[] actions = new String[]{BluetoothDevice.ACTION_FOUND,
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED};

        //
        private boolean registered = false;

        // Instancia única desta classe
        private static BTBroadcastReceiver instance = null;

        private BTBroadcastReceiver() {
        }

        public static BTBroadcastReceiver getInstance() {
            if (instance == null) {
                instance = new BTBroadcastReceiver();
            }
            return instance;
        }

        // Register the BroadcastReceiver
        public void registerReceviver(Context context) {

            if (instance != null && !isRegistered()) {
                for (String action : actions) {
                    IntentFilter intentFilter = new IntentFilter(action);
                    context.registerReceiver(this, intentFilter);
                }
                setRegistered(true);
            }
        }

        // Register the BroadcastReceiver
        public void unregisterReceviver(Context context) {
            context.unregisterReceiver(this);
            setRegistered(false);

        }

        @Override
        public void onReceive(Context context, Intent intent) {


            String action = intent.getAction();
            // Quando o round de descoberta (12 segundos) dos dispostivos
            // completa o ciclo.
            // Não quando forçado a parar.
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                onScanBluetoothDevicesFinished();
            }
            // Quando um dispositivo é encontrado
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                onBluetoothDeviceFound(intent);
            }


        }

        private void onBluetoothDeviceFound(Intent intent) {


            // Get the BluetoothDevice object from the Intent


            BluetoothDevice device = intent
                    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            // Get device address

            if (device != null) {

                final String macAddress = device.getAddress();
                // Get device deviceName
                final String deviceName = device.getName();
                // Get device RSSI
                double rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,
                        Short.MIN_VALUE);

                boolean paired = device.getBondState() == BluetoothDevice.BOND_BONDED;

                ParcelUuid[] uuids = device.getUuids();

                Log.i("Log", "Mobile Device " + deviceName
                        + " Found. MAc Addres: " + macAddress + ". RSSI: "
                        + rssi + ". Paired: " + paired + ".");

                if (btDevicesScanner.isSearchForSpecificDevices()
                        && !btDevicesScanner
                        .specificDevicesConstains(macAddress)) {
                    return;
                }

                if (btDevicesScanner.isIgnoreNotPairedDevices() && !(paired)) {
                    return;
                }

                MOUUID mouuid = new MOUUID(BTTechnology.ID, macAddress);

                BTMobileObject btMobileObject;

                if (!btMobileObjectsManager.containsBTMobileObject(mouuid)) {
                    btMobileObject = btMobileObjectsManager
                            .createBTMobileObject(mouuid, deviceName, rssi);
                } else {
                    btMobileObject = btMobileObjectsManager
                            .getBTMobileObjectByMOUUID(mouuid);
                    btMobileObject.setRssi(rssi);
                }

                if (btMobileObject != null) {

                    btTechnologyListener = btTechnology.getTechnologyListener();

                    if (btTechnologyListener != null) {
                        // Comunica o Listener que encotrou um objeto móvel
                        btTechnologyListener.onMObjectFound(mouuid, rssi);
                    }

                    // implementar os demais critérios para a autoconexão. Ex.:
                    // RSSI
                    if (btDevicesScanner.isAutoconnect()) {

                        if (!btMobileObject.isConnected()) {
                            btMobileObjectsManager.connect(btMobileObject);
                        }
                    }
                }

            }

        }

        private void onScanBluetoothDevicesFinished() {
            if (btDevicesScanner.isRepeatScan()) {
                try {
                    Thread.sleep(btDevicesScanner.getTimeBetweenScans());
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                btDevicesScanner.startScan();
            }
        }

        public boolean isRegistered() {
            return registered;
        }

        public void setRegistered(boolean registered) {
            this.registered = registered;
        }

    }

}
