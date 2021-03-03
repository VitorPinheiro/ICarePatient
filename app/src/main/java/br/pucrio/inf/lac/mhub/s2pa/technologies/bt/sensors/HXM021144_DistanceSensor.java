package br.pucrio.inf.lac.mhub.s2pa.technologies.bt.sensors;


import java.util.UUID;

import br.pucrio.inf.lac.mhub.models.locals.SensorData;
import br.pucrio.inf.lac.mhub.models.locals.SensorDataExtended;

public class HXM021144_DistanceSensor extends HXM021144_AbstractSensor {

    private static HXM021144_DistanceSensor instance;

    private HXM021144_DistanceSensor() {
        // TODO Auto-generated constructor stub
    }

    public static HXM021144_DistanceSensor getInstance() {
        if (instance == null) {
            instance = new HXM021144_DistanceSensor();
        }
        return instance;
    }

    @Override
    public UUID getCalibration() {
        return null;
    }

    @Override
    public void setCalibrationData(byte[] value) throws UnsupportedOperationException {

    }

    @Override
    public synchronized Double[] convert(byte[] bytes) {
        // TODO Auto-generated method stub
        double distance = getHRSpeedDistPacketInfo().GetDistance(bytes);
        return new Double[]{distance};
    }

    public SensorData convertToSensorData(byte[] bytes) throws UnsupportedOperationException {
        Double[] values = convert(bytes);
        SensorDataExtended sensorDataExtended = new SensorDataExtended();
        sensorDataExtended.setSensorName(getName());
        sensorDataExtended.setSensorValue(values);
        sensorDataExtended.setSensorObjectValue(values);
        return sensorDataExtended;
    }

    @Override
    public int getPacketMsgID() {
        // TODO Auto-generated method stub
        return HR_SPD_DIST_PACKET;
    }


}
