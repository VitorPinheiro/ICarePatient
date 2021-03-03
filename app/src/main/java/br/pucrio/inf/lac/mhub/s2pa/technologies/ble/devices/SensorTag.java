package br.pucrio.inf.lac.mhub.s2pa.technologies.ble.devices;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.pucrio.inf.lac.mhub.components.AppUtils;
import br.pucrio.inf.lac.mhub.s2pa.base.TechnologyDevice;
import br.pucrio.inf.lac.mhub.s2pa.base.TechnologySensor;

import static java.lang.Math.pow;
import static java.util.UUID.fromString;

/**
 * Module for the SensorTag, it is used when a communication
 * with a SensorTag is required
 */
public class SensorTag implements TechnologyDevice {
	public final static UUID
	    UUID_IRT_SERV = fromString("f000aa00-0451-4000-b000-000000000000"),
	    UUID_IRT_DATA = fromString("f000aa01-0451-4000-b000-000000000000"),
	    UUID_IRT_CONF = fromString("f000aa02-0451-4000-b000-000000000000"), // 0: disable, 1: enable
	
	    UUID_ACC_SERV = fromString("f000aa10-0451-4000-b000-000000000000"),
	    UUID_ACC_DATA = fromString("f000aa11-0451-4000-b000-000000000000"),
	    UUID_ACC_CONF = fromString("f000aa12-0451-4000-b000-000000000000"), // 0: disable, 1: enable
	
	    UUID_HUM_SERV = fromString("f000aa20-0451-4000-b000-000000000000"),
	    UUID_HUM_DATA = fromString("f000aa21-0451-4000-b000-000000000000"),
	    UUID_HUM_CONF = fromString("f000aa22-0451-4000-b000-000000000000"), // 0: disable, 1: enable
	
	    UUID_MAG_SERV = fromString("f000aa30-0451-4000-b000-000000000000"),
	    UUID_MAG_DATA = fromString("f000aa31-0451-4000-b000-000000000000"),
	    UUID_MAG_CONF = fromString("f000aa32-0451-4000-b000-000000000000"), // 0: disable, 1: enable
	
	    UUID_BAR_SERV = fromString("f000aa40-0451-4000-b000-000000000000"), 
	    UUID_BAR_DATA = fromString("f000aa41-0451-4000-b000-000000000000"),
	    UUID_BAR_CONF = fromString("f000aa42-0451-4000-b000-000000000000"), // 0: disable, 1: enable
	    UUID_BAR_CALI = fromString("f000aa43-0451-4000-b000-000000000000"), // Calibration characteristic
	
	    UUID_GYR_SERV = fromString("f000aa50-0451-4000-b000-000000000000"), 
	    UUID_GYR_DATA = fromString("f000aa51-0451-4000-b000-000000000000"),
	    UUID_GYR_CONF = fromString("f000aa52-0451-4000-b000-000000000000"); // 0: disable, bit 0: enable x, bit 1: enable y, bit 2: enable z;

	public static final byte ENABLE_SENSOR_CODE = 1;

    private enum Sensor implements TechnologySensor {
    	IR_TEMPERATURE( "Temperature", UUID_IRT_SERV, UUID_IRT_DATA, UUID_IRT_CONF, null ) {
			@Override
    	    public Double[] convert( final byte [] value ) {
    			double ambient = extractAmbientTemperature( value );
    	    	double target = extractTargetTemperature( value, ambient );
    	    	
    	    	return new Double[]{ ambient, target };
    	    }

    	    private double extractAmbientTemperature( byte [] v ) {
    	    	int offset = 2;
    	    	return AppUtils.shortUnsignedAtOffset( v, offset ) / 128.0;
    	    }

    	    private double extractTargetTemperature(byte [] v, double ambient) {
    	    	Integer twoByteValue = AppUtils.shortSignedAtOffset( v, 0 );

    	    	double Vobj2 = twoByteValue.doubleValue();
    	    	Vobj2 *= 0.00000015625;

    	    	double Tdie = ambient + 273.15;

    	    	double S0 = 5.593E-14; // Calibration factor
    	    	double a1 = 1.75E-3;
    	    	double a2 = -1.678E-5;
    	    	double b0 = -2.94E-5;
    	    	double b1 = -5.7E-7;
    	    	double b2 = 4.63E-9;
    	    	double c2 = 13.4;
    	    	double Tref = 298.15;
    	    	double S = S0 * (1 + a1 * (Tdie - Tref) + a2 * pow((Tdie - Tref), 2));
    	    	double Vos = b0 + b1 * (Tdie - Tref) + b2 * pow((Tdie - Tref), 2);
    	    	double fObj = (Vobj2 - Vos) + c2 * pow((Vobj2 - Vos), 2);
    	    	double tObj = pow(pow(Tdie, 4) + (fObj / S), .25);

    	    	return tObj - 273.15;
    	    }
    	},
    	ACCELEROMETER( "Accelerometer", UUID_ACC_SERV, UUID_ACC_DATA, UUID_ACC_CONF, null ) {
    		@Override
    	  	public Double[] convert(final byte[] value) {
	    		Integer x = (int) value[0];
    	  		Integer y = (int) value[1];
    	  		Integer z = (int) value[2] * -1;
    	    	
    	  		return new Double[]{x / 64.0, y / 64.0, z / 64.0};
    	  	}
    	},
    	HUMIDITY( "Humidity", UUID_HUM_SERV, UUID_HUM_DATA, UUID_HUM_CONF, null ) {
    	    @Override
    	    public Double[] convert(final byte[] value) {	
    			int a = AppUtils.shortUnsignedAtOffset( value, 2 );
    	    	a = a - (a % 4);	   
    	      
  	    		return new Double[]{(double) ((-6f) + 125f * (a / 65535f))};
    	    }
    	},
    	MAGNETOMETER( "Magnetometer", UUID_MAG_SERV, UUID_MAG_DATA, UUID_MAG_CONF, null ) {
    	    @Override
    	    public Double[] convert(final byte [] value) {
	    		double x = AppUtils.shortSignedAtOffset( value, 0 ) * (2000f / 65536f) * -1;
	    		double y = AppUtils.shortSignedAtOffset( value, 2 ) * (2000f / 65536f) * -1;
	    		double z = AppUtils.shortSignedAtOffset( value, 4 ) * (2000f / 65536f);
    	      
    	    	return new Double[]{x, y, z};
    	    }
    	},
    	BAROMETER( "Barometer", UUID_BAR_SERV, UUID_BAR_DATA, UUID_BAR_CONF, UUID_BAR_CALI ) {
			@Override
			public void setCalibrationData( byte[] value ) {
				this.coefficients = new int[8];
				for( int i = 0; i < 4; ++i ) {
					this.coefficients[i] = AppUtils.shortUnsignedAtOffset( value, i * 2 );
					this.coefficients[i + 4] = AppUtils.shortSignedAtOffset( value, 8 + i * 2 );
				}
			}

    		@Override
    	    public Double[] convert( final byte [] value ) {
				if( this.coefficients == null )
					return new Double[]{ 0.0 };

				final Integer t_r; // Temperature raw value from sensor
				final Integer p_r; // Pressure raw value from sensor
				final Double S; // Interim value in calculation
				final Double O; // Interim value in calculation
				final Double p_a; // Pressure actual value in unit Pascal.

				t_r = AppUtils.shortSignedAtOffset( value, 0 );
				p_r = AppUtils.shortUnsignedAtOffset( value, 2 );

				S = this.coefficients[2] + this.coefficients[3] * t_r / pow(2, 17) + ((this.coefficients[4] * t_r / pow(2, 15)) * t_r) / pow(2, 19);
				O = this.coefficients[5] * pow(2, 14) + this.coefficients[6] * t_r / pow(2, 3) + ((this.coefficients[7] * t_r / pow(2, 15)) * t_r) / pow(2, 4);
				p_a = (S * p_r + O) / pow(2, 14);

    			return new Double[]{p_a};
    	    }
    	},
    	GYROSCOPE( "Gyroscope", UUID_GYR_SERV, UUID_GYR_DATA, UUID_GYR_CONF, null, (byte)7 ) {
    	    @Override
    	    public Double[] convert(final byte [] value) {
    			double y = AppUtils.shortSignedAtOffset( value, 0 ) * (500f / 65536f) * -1;
    			double x = AppUtils.shortSignedAtOffset( value, 2 ) * (500f / 65536f);
    			double z = AppUtils.shortSignedAtOffset( value, 4 ) * (500f / 65536f);
    	      
  	    		return new Double[]{x, y, z};
    	    }
    	};
		
		private String name;
		private final UUID service, data, config, calibration;
		private byte enableCode; // See getEnableSensorCode for explanation.
		public int[] coefficients; // Calibration coefficients

		/**
		 * Constructor called by the Gyroscope because he needs a different enabler code.
		 **/
		Sensor( String name, UUID service, UUID data, UUID config, UUID calibration, byte enableCode ) {
			this.name    = name;
			this.service = service;
			this.data    = data;
			this.config  = config;
			this.enableCode  = enableCode;
			this.calibration = calibration;
		}

		/**
		 * Constructor called by all the sensors except Gyroscope
		 **/
		Sensor( String name, UUID service, UUID data, UUID config, UUID calibration ) {
			// This is the sensor enable code for all sensors except the gyroscope
			this( name, service, data, config, calibration, ENABLE_SENSOR_CODE );
		}
		
		@Override
		public String getName() {
			return name;
		}

		@Override
		public UUID getService() {
			return service;
		}

		@Override
		public UUID getData() {
			return data;
		}

		@Override
		public UUID getConfig() {
			return config;
		}

		@Override
		public UUID getCalibration() {
			return calibration;
		}
		
		@Override
		public byte getEnableSensorCode() {
			return enableCode;
		}

		@Override
		public void setCalibrationData( byte[] value ) throws UnsupportedOperationException {
			throw new UnsupportedOperationException( "Programmer error, the individual enum classes are supposed to override this method." );
		}

		@Override
		public Double[] convert( byte[] value ) throws UnsupportedOperationException {
			throw new UnsupportedOperationException( "Programmer error, the individual enum classes are supposed to override this method." );
		}
    }

    @Override
    public TechnologySensor getServiceByName( String serviceName ) {
		for( Sensor s : Sensor.values() ) {
			if( s.getName().equals( serviceName ) )
				return s;
		}
		return null;
    }

    @Override
    public List<TechnologySensor> getServiceByUUID( UUID uuid ) {
		List<TechnologySensor> temp = new ArrayList<>();
		for( Sensor s : Sensor.values() ) {
			if( s.getService().equals( uuid ) )
				temp.add( s );
		}
		return temp;
    }

    @Override
    public TechnologySensor getCharacteristicByUUID( UUID uuid ) {
		for( Sensor s : Sensor.values() ) {
			if( s.getData().equals( uuid ) )
				return s;
		}
		return null;
    }

    @Override
    public boolean initialize( Object o ) {
        return false;
    }

    @Override
    public boolean loadState( Object o ) {
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

