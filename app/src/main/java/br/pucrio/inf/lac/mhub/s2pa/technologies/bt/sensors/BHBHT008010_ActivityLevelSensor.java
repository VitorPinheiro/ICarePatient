/**
 *
 */
package br.pucrio.inf.lac.mhub.s2pa.technologies.bt.sensors;

import java.util.UUID;

import br.pucrio.inf.lac.mhub.components.Time;
import br.pucrio.inf.lac.mhub.models.locals.SensorData;
import br.pucrio.inf.lac.mhub.models.locals.SensorDataExtended;

/**
 * @author bertodetacio
 */
public class BHBHT008010_ActivityLevelSensor extends BHBHT008010_AbstractSensor {


    private static BHBHT008010_ActivityLevelSensor instance;

    /**
     *
     */
    public BHBHT008010_ActivityLevelSensor() {
        // TODO Auto-generated constructor stub
    }

    public static BHBHT008010_ActivityLevelSensor getInstance() {
        if (instance == null) {
            instance = new BHBHT008010_ActivityLevelSensor();
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
        double activity = getSummaryPacketInfo().GetActivity(bytes);
        Double[] values = new Double[]{activity};
        return values;
    }

    @Override
    public SensorData convertToSensorData(byte[] bytes) throws UnsupportedOperationException {
        double activity = getSummaryPacketInfo().GetActivity(bytes);
        Double[] values = new Double[]{activity};
        SensorDataExtended sensorDataExtended = new SensorDataExtended();
        sensorDataExtended.setSensorName(getName());
        sensorDataExtended.setSensorValue(values);
        sensorDataExtended.setSensorObjectValue(values);
        sensorDataExtended.setMeasurementTime(Time.getInstance().getCurrentTimestamp());
        return sensorDataExtended;
    }

    @Override
    public int getPacketMsgID() {
        // TODO Auto-generated method stub
        return SUMMARY_MSG_ID;
    }

}
