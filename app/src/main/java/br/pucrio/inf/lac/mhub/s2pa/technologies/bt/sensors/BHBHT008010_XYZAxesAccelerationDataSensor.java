/**
 *
 */
package br.pucrio.inf.lac.mhub.s2pa.technologies.bt.sensors;

import java.util.ArrayList;
import java.util.UUID;

import br.pucrio.inf.lac.mhub.models.locals.SensorData;
import br.pucrio.inf.lac.mhub.models.locals.SensorDataExtended;

/**
 * @author bertodetacio
 */
public class BHBHT008010_XYZAxesAccelerationDataSensor extends BHBHT008010_AbstractSensor {


    private static BHBHT008010_XYZAxesAccelerationDataSensor instance;

    /**
     *
     */
    public BHBHT008010_XYZAxesAccelerationDataSensor() {
        // TODO Auto-generated constructor stub
    }

    public static BHBHT008010_XYZAxesAccelerationDataSensor getInstance() {
        if (instance == null) {
            instance = new BHBHT008010_XYZAxesAccelerationDataSensor();
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

        AccelerometerPacketInfo accelerometerPacketInfo = getAccelerometerPacketInfo();

        accelerometerPacketInfo.UnpackAccelerationData(bytes);

        ArrayList<Double> values = new ArrayList<Double>();

        Double[] valuesXYZ = new Double[accelerometerPacketInfo.NUM_ACCN_SAMPLES * 3];

        Double[] X_AxisAccelerationData = accelerometerPacketInfo.GetX_axisAccnData();
        Double[] Y_AxisAccelerationData = accelerometerPacketInfo.GetY_axisAccnData();
        Double[] Z_AxisAccelerationData = accelerometerPacketInfo.GetZ_axisAccnData();

        for (int i = 0; i < accelerometerPacketInfo.NUM_ACCN_SAMPLES; i++) {
            Double valueX = X_AxisAccelerationData[i];
            values.add(valueX);
        }

        for (int i = 0; i < accelerometerPacketInfo.NUM_ACCN_SAMPLES; i++) {
            Double valueY = Y_AxisAccelerationData[i];
            values.add(valueY);
        }
        for (int i = 0; i < accelerometerPacketInfo.NUM_ACCN_SAMPLES; i++) {
            Double valueZ = Z_AxisAccelerationData[i];
            values.add(valueZ);
        }

        return values.toArray(valuesXYZ);
    }

    @Override
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
        return ACCEL_100mg_MSG_ID;
    }

}
