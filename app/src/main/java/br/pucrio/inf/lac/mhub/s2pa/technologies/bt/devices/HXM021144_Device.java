/**
 *
 */
package br.pucrio.inf.lac.mhub.s2pa.technologies.bt.devices;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Vector;

import br.pucrio.inf.lac.mhub.models.locals.SensorData;
import br.pucrio.inf.lac.mhub.s2pa.base.TechnologySensor;
import br.pucrio.inf.lac.mhub.s2pa.technologies.bt.BTMobileObject;
import br.pucrio.inf.lac.mhub.s2pa.technologies.bt.sensors.HXM021144_AbstractSensor;
import br.pucrio.inf.lac.mhub.s2pa.technologies.bt.sensors.HXM021144_CadenceSensor;
import br.pucrio.inf.lac.mhub.s2pa.technologies.bt.sensors.HXM021144_DistanceSensor;
import br.pucrio.inf.lac.mhub.s2pa.technologies.bt.sensors.HXM021144_StridesSensor;
import br.pucrio.inf.lac.mhub.s2pa.technologies.bt.sensors.HXM021144_HeartBeatNumSensor;
import br.pucrio.inf.lac.mhub.s2pa.technologies.bt.sensors.HXM021144_HeartRateSensor;
import br.pucrio.inf.lac.mhub.s2pa.technologies.bt.sensors.HXM021144_InstantSpeedSensor;
import zephyr.android.HxMBT.ZephyrPacket;
import zephyr.android.HxMBT.ZephyrPacketArgs;

/**
 * @author bertodetacio
 */
public class HXM021144_Device extends BTMobileObject {


    //faz o mapeamento de um sensor para cada serviço
    private Hashtable<String, HXM021144_AbstractSensor> sensors = new Hashtable<>();

    //Timer Android
    private Timer timer = new Timer();

    //Descobre os serviços e comunica o listener periodicamente
    private TimerTaskDiscoveryServices timerTaskDiscoveryServices = new TimerTaskDiscoveryServices();

    //tempo entre uma divulgação e outra de serviços descobertos
    private long timeDiscoveryServices = 30000;

    //serializador e parser de dados do zephyr
    private ZephyrPacket zephyrPacket = new ZephyrPacket();

      /**
     *
     */
    public HXM021144_Device() {
        super();
        init();
    }


    private void init() {
        inicializeSensors();
        timer.scheduleAtFixedRate(timerTaskDiscoveryServices, timeDiscoveryServices, timeDiscoveryServices);
    }


    @Override
    public TechnologySensor getServiceByName(String serviceName) {
        return sensors.get(serviceName);
    }

    @Override
    public List<TechnologySensor> getServiceByUUID(UUID uuid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TechnologySensor getCharacteristicByUUID(UUID uuid) {
        return null;
    }

    public void addSensor(HXM021144_AbstractSensor sensor) {
        sensors.put(sensor.getName(), sensor);
    }

    public void removeSensor(TechnologySensor sensor) {
        sensors.remove(sensor);
    }

    public void removeSensor(String name) {
        sensors.remove(name);
    }

    @Override
    public List<String> getServiceNames() {
        return new ArrayList<>(sensors.keySet());
    }


    @Override
    public List<SensorData> convertToSensorDataList(byte[] bytes) {

        ArrayList<SensorData> sensorDataList = new ArrayList<>();

        Vector<byte[]> serializedDataVector = serialize(bytes);
        for (byte[] serializedData : serializedDataVector) {
            ZephyrPacketArgs zephyrPacketArgs = parser(serializedData);
            if (validate(zephyrPacketArgs)) {
                byte[] data = zephyrPacketArgs.getBytes();
                for (HXM021144_AbstractSensor sensor : sensors.values()) {
                   int packetMsgID = sensor.getPacketMsgID();
                    if (zephyrPacketArgs.getMsgID() == packetMsgID) {
                        SensorData sensorData = sensor.convertToSensorData(data);
                        sensorDataList.add(sensorData);
                    }
                }
            }
        }

        return sensorDataList;
    }

    private Vector<byte[]> serialize(byte[] data) {
        return zephyrPacket.Serialize(data);
    }

    private ZephyrPacketArgs parser(byte[] serializedData) {
        ZephyrPacketArgs zephyrPacketArgs = null;
        try {
            zephyrPacketArgs = zephyrPacket.Parse(serializedData);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return zephyrPacketArgs;
    }

    private boolean validate(ZephyrPacketArgs zephyrPacketArgs) {
        return zephyrPacketArgs != null && zephyrPacketArgs.getNumRvcdBytes() > 1;
    }

    private void inicializeSensors() {
        addSensor(HXM021144_DistanceSensor.getInstance());
        addSensor(HXM021144_HeartRateSensor.getInstance());
        addSensor(HXM021144_InstantSpeedSensor.getInstance());
        addSensor(HXM021144_CadenceSensor.getInstance());
        addSensor(HXM021144_HeartBeatNumSensor.getInstance());
        addSensor(HXM021144_StridesSensor.getInstance());
    }



    @Override
    public List<TechnologySensor> getServices() {
        ArrayList<TechnologySensor> services = new ArrayList<>();
        services.addAll(sensors.values());
        return services;
    }


    private class TimerTaskDiscoveryServices extends TimerTask {
        @Override
        public void run() {
            if (isConnected()) {
                List<String> services = getServiceNames();
                onServiceDiscovered(services);
            }
        }
    }


    public long getTimeDiscoveryServices() {
        return timeDiscoveryServices;
    }

    public void setTimeDiscoveryServices(long timeDiscoveryServices) {
        this.timeDiscoveryServices = timeDiscoveryServices;
        if (timer != null && timerTaskDiscoveryServices != null) {
            timer.cancel();
            timer.scheduleAtFixedRate(timerTaskDiscoveryServices, timeDiscoveryServices, timeDiscoveryServices);
        }
    }


    @Override
    public boolean initialize(Object o) {
        return false;
    }

    @Override
    public boolean loadState(Object o) {
        return false;
    }

    @Override
    public Object getState() {
        return null;
    }

    @Override
    public String getVersion() {
        return "0.1";
    }


}
