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
public class BHBHT008010_XYZAxesPeakAccelerationSensor extends BHBHT008010_AbstractSensor {


    private static BHBHT008010_XYZAxesPeakAccelerationSensor instance;

    /**
     *
     */
    public BHBHT008010_XYZAxesPeakAccelerationSensor() {
        // TODO Auto-generated constructor stub
    }

    public static BHBHT008010_XYZAxesPeakAccelerationSensor getInstance() {
        if (instance == null) {
            instance = new BHBHT008010_XYZAxesPeakAccelerationSensor();
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
        double X_AxisPeakAcceleration = getGeneralPacketInfo().GetX_AxisAccnPeak(bytes);
        double Y_AxisPeakAcceleration = getGeneralPacketInfo().GetY_AxisAccnPeak(bytes);
        double Z_AxisPeakAcceleration = getGeneralPacketInfo().GetZ_AxisAccnPeak(bytes);

        Double[] values = new Double[]{X_AxisPeakAcceleration, Y_AxisPeakAcceleration, Z_AxisPeakAcceleration};
        return values;
    }

    public SensorData convertToSensorData(byte[] bytes) throws UnsupportedOperationException {
        Double[] values = convert(bytes);
        SensorDataExtended sensorDataExtended = new SensorDataExtended();
        sensorDataExtended.setSensorName(getName());
        sensorDataExtended.setSensorValue(values);
        sensorDataExtended.setSensorObjectValue(values);
        String[] avaliableSttributes = new String[]{"X","Y","Z"};
        sensorDataExtended.setAvailableAttributesList(avaliableSttributes);
        return sensorDataExtended;
    }

    @Override
    public int getPacketMsgID() {
        // TODO Auto-generated method stub
        return GP_MSG_ID;
    }

}
