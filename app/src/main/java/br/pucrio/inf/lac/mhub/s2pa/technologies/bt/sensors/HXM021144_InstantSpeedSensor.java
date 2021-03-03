package br.pucrio.inf.lac.mhub.s2pa.technologies.bt.sensors;


import java.util.UUID;

import br.pucrio.inf.lac.mhub.models.locals.SensorData;
import br.pucrio.inf.lac.mhub.models.locals.SensorDataExtended;

public class HXM021144_InstantSpeedSensor extends HXM021144_AbstractSensor {

    private static HXM021144_InstantSpeedSensor instance;

    private HXM021144_InstantSpeedSensor() {
        // TODO Auto-generated constructor stub
    }

    public static HXM021144_InstantSpeedSensor getInstance() {
        if (instance == null) {
            instance = new HXM021144_InstantSpeedSensor();
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
        double instanteSpeed = getHRSpeedDistPacketInfo().GetInstantSpeed(bytes);
        return new Double[]{instanteSpeed};
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
