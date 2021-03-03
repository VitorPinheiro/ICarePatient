/**
 *
 */
package br.pucrio.inf.lac.mhub.s2pa.technologies.bt.sensors;

import java.util.UUID;

import br.pucrio.inf.lac.mhub.models.locals.SensorData;
import br.pucrio.inf.lac.mhub.models.locals.SensorDataExtended;

/**
 * @author bertodetacio
 */
public class BHBHT008010_PostureSensor extends BHBHT008010_AbstractSensor {


    private static BHBHT008010_PostureSensor instance;

    /**
     *
     */
    public BHBHT008010_PostureSensor() {
        // TODO Auto-generated constructor stub
    }

    public static BHBHT008010_PostureSensor getInstance() {
        if (instance == null) {
            instance = new BHBHT008010_PostureSensor();
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
    public Double[] convert(byte[] bytes) {
        double posture = getGeneralPacketInfo().GetPosture(bytes);
        Double[] values = new Double[]{posture};
        return values;
    }

    public SensorData convertToSensorData(byte[] bytes) throws UnsupportedOperationException {
        double posture = getGeneralPacketInfo().GetPosture(bytes);
        Double[] values = new Double[]{posture};
        SensorDataExtended sensorDataExtended = new SensorDataExtended();
        sensorDataExtended.setSensorName(getName());
        sensorDataExtended.setSensorValue(values);
        sensorDataExtended.setSensorObjectValue(values);
        return sensorDataExtended;
    }


    @Override
    public int getPacketMsgID() {
        // TODO Auto-generated method stub
        return GP_MSG_ID;
    }

}
