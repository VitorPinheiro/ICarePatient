package br.pucrio.inf.lac.mhub.models.locals;

import com.google.gson.Gson;

import java.io.Serializable;

import br.pucrio.inf.lac.mhub.components.Time;

/**
 * Contains the sensor data received from the
 * Mobile Objects
 */
public class SensorDataExtended extends SensorData {
    /**
     * DEBUG
     */
    private static final String TAG = SensorDataExtended.class.getSimpleName();


    private Double sensorAccuracy;
    private Integer availableAttributes;
    private String [] availableAttributesList;
    private Double altitude;
    private Double speed;
    private Double locationAccuracy;
    private Long locationTimestamp;
    private Integer numericalResolution;
    private Serializable sensorObjectValue;
    private Long measurementTime = Time.getInstance().getCurrentTimestamp();

    public SensorDataExtended() {
        super(TAG);
    }

    public Serializable getSensorObjectValue() {
        return sensorObjectValue;
    }

    public void setSensorObjectValue(Serializable sensorObjectValue) {
        this.sensorObjectValue = sensorObjectValue;
    }

    public Double getSensorAccuracy() {
        return sensorAccuracy;
    }

    public void setSensorAccuracy(Double sensorAccuracy) {
        this.sensorAccuracy = sensorAccuracy;
    }

    public Integer getAvailableAttributes() {
        return availableAttributes;
    }

    public void setAvailableAttributes(Integer availableAttributes) {
        this.availableAttributes = availableAttributes;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public Integer getNumericalResolution() {
        return numericalResolution;
    }

    public void setNumericalResolution(Integer numericalResolution) {
        this.numericalResolution = numericalResolution;
    }

    public String[] getAvailableAttributesList() {
        if(availableAttributesList==null){
            availableAttributesList = new String[]{getSensorName()};
        }
        return availableAttributesList;
    }

    public void setAvailableAttributesList(String[] availableAttributesList) {
        this.availableAttributesList = availableAttributesList;
    }

    public Long getMeasurementTime() {
        return measurementTime;
    }

    public void setMeasurementTime(Long measurementTime) {
        this.measurementTime = measurementTime;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Double getLocationAccuracy() {
        return locationAccuracy;
    }

    public void setLocationAccuracy(Double locationAccuracy) {
        this.locationAccuracy = locationAccuracy;
    }

    public Long getLocationTimestamp() {
        return locationTimestamp;
    }

    public void setLocationTimestamp(Long locationTimestamp) {
        this.locationTimestamp = locationTimestamp;

    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }
}
