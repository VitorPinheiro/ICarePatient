package br.pucrio.inf.lac.mhub.s2pa.technologies.bt;

/**
 * @author bertodetacio
 */

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import br.pucrio.inf.lac.mhub.components.AppUtils;
import br.pucrio.inf.lac.mhub.s2pa.base.Technology;
import br.pucrio.inf.lac.mhub.s2pa.base.TechnologyListener;

@SuppressLint("NewApi")
public class BTTechnology implements Technology {

    // DEBUG
    public static final String TAG = BTTechnology.class.getName();

    // Technology ID
    public final static int ID = 0;

    //Contexto da Aplicação
    private Context context = null;

    //Única instancia desta tecnologia. Visível por todos os componentes da aplicação.
    private static BTTechnology instance = null;

    //Technology Listener
    private TechnologyListener technologyListener = null;

    //Scanner para a Tecnologia Bluetooth Classic
    private BTDevicesScanner btDevicesScanner = null;

    //Geerenciador de Objetos Móveis para Bluetooth Classic
    private BTMobileObjectsManager btMobileObjectsManager = null;

    private BTTechnology() {
        super();
    }


    public static BTTechnology getInstance(Context... contexts) {
        if (instance == null) {
            instance = new BTTechnology();
            if (contexts != null && contexts.length > 0 && contexts[0] != null) {
                instance.setContext(contexts[0]);
            }
        }
        return instance;
    }


    public synchronized void initialize(Context context) {
        setContext(context);
        initialize();
    }


    @Override
    public synchronized boolean initialize() {

       BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null){
            return false;
        }


        // Adapter available
       BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null){
            return false;
        }

        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)){
            return false;
        }


        btDevicesScanner = BTDevicesScanner.getisntance();
        btDevicesScanner.registerReceiver(context);
        btMobileObjectsManager = BTMobileObjectsManager.getInstance();


        return btDevicesScanner.initialize();
    }


    public boolean isInitialzed() {
        return btDevicesScanner.isInitialized();
    }


    @Override
    public synchronized void enable() {

        btDevicesScanner.enable();
    }


    public boolean isEnable() {
        return btDevicesScanner.isEnable();
    }


    @Override
    public synchronized void startScan(boolean autoconnect) {
        btDevicesScanner.setAutoconnect(autoconnect);
        btDevicesScanner.startScan(autoconnect);
    }

    public synchronized boolean startScan(boolean autoconnect, String MACaddres) {
        btDevicesScanner.addSearchDevice(MACaddres);
        return btDevicesScanner.startScan(autoconnect);

    }

    public synchronized boolean startScan() {
        return btDevicesScanner.startScan();
    }

    @Override
    public synchronized void stopScan() {
        btDevicesScanner.stopScan();
    }

    public TechnologyListener getTechnologyListener() {
        return technologyListener;
    }

    @Override
    public void setListener(TechnologyListener technologyListener) {
        this.technologyListener = technologyListener;
    }


    @Override
    public void readSensorValue(String macAddress, String serviceName) {
        btMobileObjectsManager.readSensorValue(macAddress, serviceName);
    }

    @Override
    public synchronized void writeSensorValue(String macAddress, String serviceName,
                                              Object value) {
        // TODO Auto-generated method stub

    }

    /**
     * Create a bluetooth connection with SerialPortServiceClass_UUID
     */
    @Override
    public synchronized boolean connect(String macAddress) {
        return btMobileObjectsManager.connect(macAddress);
    }

    @Override
    public synchronized boolean disconnect(String macAddress) {
        // TODO Auto-generated method stub
        return btMobileObjectsManager.disconnect(macAddress);
    }

    public void turnVisible(long timeVisible) {
        btDevicesScanner.turnVisible(timeVisible);
    }

    public void acceptConnections() {
        btMobileObjectsManager.acceptConnections();
    }

    public void noAcceptMoreConnections() {
        btMobileObjectsManager.noAcceptMoreConnections();
    }


    @Override
    public void destroy() {
        if (instance != null && btDevicesScanner != null) {
            btDevicesScanner.unRegisterReceiver(context);
        }
        if (btMobileObjectsManager != null) {
            btMobileObjectsManager.closeAllConnections();
        }
    }


    public Context getContext() {
        return context;
    }


    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void addToWhiteList(String macAddress) {

    }

    @Override
    public void addToBlackList(String macAddress) {

    }

    @Override
    public void clearWhiteList() {

    }

    @Override
    public boolean isInBlackList(String macAddress) {
        return false;
    }

    @Override
    public boolean removeFromWhiteList(String macAddress) {
        return false;
    }

    @Override
    public void clearBlackList() {

    }

    @Override
    public boolean isInWhiteList(String macAddress) {
        return false;
    }

    @Override
    public boolean removeFromBlackList(String macAddress) {
        return false;
    }


    public BTDevicesScanner getBtDevicesScanner() {
        return btDevicesScanner;
    }

    public void setBtDevicesScanner(BTDevicesScanner btDevicesScanner) {
        this.btDevicesScanner = btDevicesScanner;
    }
}

