/**
 *
 */
package br.pucrio.inf.lac.mhub.s2pa.technologies.bt;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.pucrio.inf.lac.mhub.components.MOUUID;
import br.pucrio.inf.lac.mhub.components.Time;
import br.pucrio.inf.lac.mhub.models.locals.SensorData;
import br.pucrio.inf.lac.mhub.models.locals.SensorDataExtended;
import br.pucrio.inf.lac.mhub.s2pa.base.TechnologyDeviceExtended;
import br.pucrio.inf.lac.mhub.s2pa.base.TechnologyListener;
import br.pucrio.inf.lac.mhub.s2pa.base.TechnologyListenerExtended;

/**
 * @author bertodetacio
 */
@SuppressLint("NewApi")
public abstract class BTMobileObject implements TechnologyDeviceExtended {


    private MOUUID mouuid = null;

    private String deviceName = null;

    private double rssi = 0;

    private BluetoothSocket bluetoothSocket = null;

    private InputStream inputStream = null;

    private OutputStream outpuStream = null;

    private long refreshRate = 0;

    private TechnologyListener btTechnologyListener = null;

    private SendMessageThread sendMessageThread = new SendMessageThread();

    private ReceiveMessageThread receiveMessageThread = new ReceiveMessageThread();

    private ArrayList<byte[]> messagesToSend = new ArrayList<byte[]>();

    private boolean connected = false;

    private HashMap<String,SensorData> sensorValues = new HashMap<String,SensorData>();


     /**
     * Constuctor
     */

    public BTMobileObject() {
        sendMessageThread.start();
        receiveMessageThread.start();
    }

    public synchronized void onConnected(BluetoothSocket bluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket;
        try {
            inputStream = bluetoothSocket.getInputStream();
            outpuStream = bluetoothSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setConnected(true);
        btTechnologyListener = BTTechnology.getInstance().getTechnologyListener();
        if (btTechnologyListener != null) {
            btTechnologyListener.onMObjectConnected(getMouuid());
        }
        notifyAll();
    }

    public synchronized void onDisconnected() {
        if (isConnected()) {
            setConnected(false);
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            bluetoothSocket = null;
            inputStream = null;
            outpuStream = null;
            if (btTechnologyListener != null) {
                btTechnologyListener.onMObjectDisconnected(getMouuid(), getServiceNames());
            }
        }
    }


    protected void onServiceDiscovered(List<String> services) {
        if (isConnected()) {
            btTechnologyListener = BTTechnology.getInstance().getTechnologyListener();
            if (btTechnologyListener != null) {
                btTechnologyListener.onMObjectServicesDiscovered(getMouuid(), services);
            }
        }
    }

    protected synchronized void write(byte[] bytes) {
        messagesToSend.add(bytes);
        notifyAll();
    }



    private void read() {
        byte[] buffer = null;
        byte[] arrayData = null;
        try {
            int numberBytesRead = 0;
            // Reads bytes from this stream into buffer.
            if (inputStream != null) {
                buffer = new byte[inputStream.available() * 2];
                numberBytesRead = inputStream.read(buffer);
                if (numberBytesRead > 0) {
                    arrayData = new byte[numberBytesRead];
                    System.arraycopy(buffer, 0, arrayData, 0, numberBytesRead);
                }
            }
            if (arrayData != null) {
               Long measurementTime = Time.getInstance().getCurrentTimestamp() ;
               List<SensorData> sensorDataList = convertToSensorDataList(arrayData);
               for(SensorData sensorData: sensorDataList){
                   if(sensorData instanceof  SensorDataExtended){
                       ((SensorDataExtended) sensorData).setMeasurementTime(measurementTime);
                   }
                   sensorValues.put(sensorData.getSensorName(),sensorData);
                    if(btTechnologyListener instanceof  TechnologyListenerExtended){
                       TechnologyListenerExtended technologyListenerExtended = (TechnologyListenerExtended) btTechnologyListener;
                       technologyListenerExtended.onMObjectValueRead(getMouuid(),sensorData);
                   }
                   else{
                       btTechnologyListener.onMObjectValueRead(getMouuid(),getRssi(),sensorData.getSensorName(),sensorData.getSensorValue());
                   }
               }
            }
        } catch (Exception e) {
            Log.e("Log", "Error reading data from " + getMouuid().toString(), e);
            e.printStackTrace();
            if (isConnected()) {
                onDisconnected();
            }
        }
    }

    private void write() {
        if (isConnected()) {
            try {
                outpuStream.write(messagesToSend.remove(0));
            } catch (IOException e) {
                Log.e("Log", "Error writing data from " + getMouuid().toString(), e);
                e.printStackTrace();
            }
        }
    }


    private synchronized void waiting() {
        try {
            wait();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void readSensorValue(String sensorName){
        SensorData sensorData = sensorValues.get(sensorName);
        if(btTechnologyListener instanceof  TechnologyListenerExtended){
            TechnologyListenerExtended technologyListenerExtended = (TechnologyListenerExtended) btTechnologyListener;
            technologyListenerExtended.onMObjectValueRead(getMouuid(),sensorData);
        }
        else{
            btTechnologyListener.onMObjectValueRead(getMouuid(),getRssi(),sensorData.getSensorName(),sensorData.getSensorValue());
        }


    }

    public abstract List<String> getServiceNames();

    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    public MOUUID getMouuid() {
        return mouuid;
    }

    public void setMouuid(MOUUID mouuid) {
        this.mouuid = mouuid;
    }

    public double getRssi() {
        return rssi;
    }

    public void setRssi(double rssi) {
        this.rssi = rssi;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public long getRefreshRate() {
        return refreshRate;
    }

    public void setRefreshRate(long refreshRate) {
        this.refreshRate = refreshRate;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }


    private class ReceiveMessageThread extends Thread {

        public ReceiveMessageThread() {
            super();
        }


        @Override
        public void run() {
            while (true) {
                if (isConnected()) {
                    read();
                } else {
                    waiting();
                }
                try {
                    Thread.sleep(refreshRate);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }


    }

    private class SendMessageThread extends Thread {

        public SendMessageThread() {
            super();
        }

        @Override
        public void run() {
            while (true) {
                if (isConnected() && !messagesToSend.isEmpty()) {
                    write();
                } else {
                    waiting();
                }
            }
        }

    }


}
