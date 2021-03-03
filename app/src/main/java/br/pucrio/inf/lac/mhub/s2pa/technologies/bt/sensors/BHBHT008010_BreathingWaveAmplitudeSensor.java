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
public class BHBHT008010_BreathingWaveAmplitudeSensor extends BHBHT008010_AbstractSensor {


    private static BHBHT008010_BreathingWaveAmplitudeSensor instance;

    /**
     *
     */
    public BHBHT008010_BreathingWaveAmplitudeSensor() {
        // TODO Auto-generated constructor stub
    }

    public static BHBHT008010_BreathingWaveAmplitudeSensor getInstance() {
        if (instance == null) {
            instance = new BHBHT008010_BreathingWaveAmplitudeSensor();
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
        double value = getGeneralPacketInfo().GetBreathingWaveAmplitude(bytes);
        Double[] values = new Double[]{value};
        return values;
    }

    public SensorData convertToSensorData(byte[] bytes) throws UnsupportedOperationException {
        double value = getGeneralPacketInfo().GetBreathingWaveAmplitude(bytes);
        Double[] values = new Double[]{value};
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
