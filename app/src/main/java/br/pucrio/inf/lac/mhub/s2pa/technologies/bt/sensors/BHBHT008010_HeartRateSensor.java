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
public class BHBHT008010_HeartRateSensor extends BHBHT008010_AbstractSensor  {


    private static BHBHT008010_HeartRateSensor instance;

    /**
     *
     */
    public BHBHT008010_HeartRateSensor() {

    }

    public static BHBHT008010_HeartRateSensor getInstance() {
        if (instance == null) {
            instance = new BHBHT008010_HeartRateSensor();
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
        double heartRate = getGeneralPacketInfo().GetHeartRate(bytes);
        Double[] values = new Double[]{heartRate};
        return values;
    }

    public SensorData convertToSensorData(byte[] bytes) throws UnsupportedOperationException {
        double heartRate = getGeneralPacketInfo().GetHeartRate(bytes);
        Double[] values = new Double[]{heartRate};
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
