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
 * Module for the CC2650SensorTag, it is used when a communication
 * with a CC2650SensorTag is required
 */
public class CC2650SensorTag implements TechnologyDevice {
    private final static UUID
        UUID_DEVINFO_SERV  = fromString( "0000180a-0000-1000-8000-00805f9b34fb" ),
        UUID_DEVINFO_FWREV = fromString( "00002A26-0000-1000-8000-00805f9b34fb" ),

        UUID_IRT_SERV = fromString( "f000aa00-0451-4000-b000-000000000000" ),
        UUID_IRT_DATA = fromString( "f000aa01-0451-4000-b000-000000000000" ),
        UUID_IRT_CONF = fromString( "f000aa02-0451-4000-b000-000000000000" ), // 0: disable, 1: enable
        UUID_IRT_PERI = fromString( "f000aa03-0451-4000-b000-000000000000" ), // Period in tens of milliseconds

        UUID_HUM_SERV = fromString( "f000aa20-0451-4000-b000-000000000000" ),
        UUID_HUM_DATA = fromString( "f000aa21-0451-4000-b000-000000000000" ),
        UUID_HUM_CONF = fromString( "f000aa22-0451-4000-b000-000000000000" ), // 0: disable, 1: enable
        UUID_HUM_PERI = fromString( "f000aa23-0451-4000-b000-000000000000" ), // Period in tens of milliseconds

        UUID_OPT_SERV = fromString( "f000aa70-0451-4000-b000-000000000000" ),
        UUID_OPT_DATA = fromString( "f000aa71-0451-4000-b000-000000000000" ),
        UUID_OPT_CONF = fromString( "f000aa72-0451-4000-b000-000000000000" ), // 0: disable, 1: enable
        UUID_OPT_PERI = fromString( "f000aa73-0451-4000-b000-000000000000" ), // Period in tens of milliseconds

        UUID_BAR_SERV = fromString( "f000aa40-0451-4000-b000-000000000000" ),
        UUID_BAR_DATA = fromString( "f000aa41-0451-4000-b000-000000000000" ),
        UUID_BAR_CONF = fromString( "f000aa42-0451-4000-b000-000000000000" ), // 0: disable, 1: enable
        UUID_BAR_CALI = fromString( "f000aa43-0451-4000-b000-000000000000" ), // Calibration characteristic
        UUID_BAR_PERI = fromString( "f000aa44-0451-4000-b000-000000000000" ), // Period in tens of milliseconds

        UUID_MOV_SERV = fromString( "f000aa80-0451-4000-b000-000000000000" ),
        UUID_MOV_DATA = fromString( "f000aa81-0451-4000-b000-000000000000" ),
        UUID_MOV_CONF = fromString( "f000aa82-0451-4000-b000-000000000000" ), // 0: disable, bit 0: enable x, bit 1: enable y, bit 2: enable z
        UUID_MOV_PERI = fromString( "f000aa83-0451-4000-b000-000000000000" ), // Period in tens of milliseconds

        UUID_TST_SERV = fromString( "f000aa64-0451-4000-b000-000000000000" ),
        UUID_TST_DATA = fromString( "f000aa65-0451-4000-b000-000000000000" ),
        UUID_TST_CONF = fromString( "f000aa66-0451-4000-b000-000000000000" );

    private static final byte ENABLE_SENSOR_CODE = (byte) 0x01;

    private enum Sensor implements TechnologySensor {
        IR_TEMPERATURE( "Temperature", UUID_IRT_SERV, UUID_IRT_DATA, UUID_IRT_CONF, null ) {
            @Override
            public Double[] convert( final byte[] value ) {
                double ambient = extractAmbientTemperature( value );
                double target = extractTargetTemperature( value, ambient );
                double targetNewSensor = extractTargetTemperatureTMP007( value );

                return new Double[]{ ambient, target, targetNewSensor };
            }

            private double extractAmbientTemperature( byte[] v ) {
                int offset = 2;
                return AppUtils.shortUnsignedAtOffset( v, offset ) / 128.0;
            }

            private double extractTargetTemperature( byte[] v, double ambient ) {
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
                double S = S0 * ( 1 + a1 * ( Tdie - Tref ) + a2 * pow( ( Tdie - Tref ), 2 ) );
                double Vos = b0 + b1 * ( Tdie - Tref ) + b2 * pow( ( Tdie - Tref ), 2 );
                double fObj = ( Vobj2 - Vos ) + c2 * pow( ( Vobj2 - Vos ), 2 );
                double tObj = pow( pow( Tdie, 4 ) + ( fObj / S ), .25 );

                return tObj - 273.15;
            }

            private double extractTargetTemperatureTMP007( byte [] v ) {
                int offset = 0;
                return AppUtils.shortUnsignedAtOffset( v, offset ) / 128.0;
            }
        }/*,
        MOVEMENT_ACC( "Accelerometer", UUID_MOV_SERV, UUID_MOV_DATA, UUID_MOV_CONF, null, (byte) 0x7F ) {
            @Override
            public Double[] convert( final byte[] value ) {
                // Range 8G
                final float SCALE = (float) 4096.0;

                double x = (value[7]<<8) + value[6];
                double y = (value[9]<<8) + value[8];
                double z = (value[11]<<8) + value[10];
                return new Double[]{ (x / SCALE) * -1.0, y / SCALE, (z / SCALE) * -1.0 };
            }
        },
        MOVEMENT_GYRO( "Gyroscope", UUID_MOV_SERV, UUID_MOV_DATA, UUID_MOV_CONF, null, (byte) 0x7F ) {
            @Override
            public Double[] convert( final byte[] value ) {
                final float SCALE = (float) 128.0;

                double x = (value[1]<<8) + value[0];
                double y = (value[3]<<8) + value[2];
                double z = (value[5]<<8) + value[4];
                return new Double[]{ x / SCALE, y / SCALE, z / SCALE };
            }
        },
        MOVEMENT_MAG( "Magnetometer", UUID_MOV_SERV, UUID_MOV_DATA, UUID_MOV_CONF, null, (byte) 0x7F ) {
            @Override
            public Double[] convert( final byte[] value ) {
                final float SCALE = (float) (32768 / 4912);
                if( value.length >= 18 ) {
                    double x = (value[13]<<8) + value[12];
                    double y = (value[15]<<8) + value[14];
                    double z = (value[17]<<8) + value[16];
                    return new Double[]{ x / SCALE, y / SCALE, z / SCALE };
                }
                else
                    return new Double[]{ 0.0, 0.0, 0.0 };
            }
        }*/,
        HUMIDITY( "Humidity", UUID_HUM_SERV, UUID_HUM_DATA, UUID_HUM_CONF, null ) {
            @Override
            public Double[] convert( final byte[] value ) {
                int a = AppUtils.shortUnsignedAtOffset( value, 2 );
                a = a - ( a % 4 );

                return new Double[]{ ( double ) ( ( -6f ) + 125f * ( a / 65535f ) ) };
            }
        },
        LUXOMETER( "Luxometer", UUID_OPT_SERV, UUID_OPT_DATA, UUID_OPT_CONF, null ) {
            @Override
            public Double[] convert( final byte [] value ) {
                int mantissa;
                int exponent;
                Integer sfloat = AppUtils.shortUnsignedAtOffset( value, 0 );

                mantissa = sfloat & 0x0FFF;
                exponent = (sfloat >> 12) & 0xFF;

                double output;
                double magnitude = pow(2.0f, exponent);
                output = (mantissa * magnitude);

                return new Double[]{ output / 100.0f };
            }
        },/*
        BAROMETER( "Barometer", UUID_BAR_SERV, UUID_BAR_DATA, UUID_BAR_CONF, UUID_BAR_CALI ) {
            @Override
            public void setCalibrationData( byte[] value ) {
                this.coefficients = new int[ 8 ];
                for( int i = 0; i < 4; ++i ) {
                    this.coefficients[ i ] = AppUtils.shortUnsignedAtOffset( value, i * 2 );
                    this.coefficients[ i + 4 ] = AppUtils.shortSignedAtOffset( value, 8 + i * 2 );
                }
            }

            @Override
            public Double[] convert( final byte[] value ) {
                if( this.coefficients == null )
                    return new Double[]{ 0.0 };

                final Integer t_r; // Temperature raw value from sensor
                final Integer p_r; // Pressure raw value from sensor
                final Double S; // Interim value in calculation
                final Double O; // Interim value in calculation
                final Double p_a; // Pressure actual value in unit Pascal.

                t_r = AppUtils.shortSignedAtOffset( value, 0 );
                p_r = AppUtils.shortUnsignedAtOffset( value, 2 );

                S = this.coefficients[ 2 ] + this.coefficients[ 3 ] * t_r / pow( 2, 17 ) + ( ( this.coefficients[ 4 ] * t_r / pow( 2, 15 ) ) * t_r ) / pow( 2, 19 );
                O = this.coefficients[ 5 ] * pow( 2, 14 ) + this.coefficients[ 6 ] * t_r / pow( 2, 3 ) + ( ( this.coefficients[ 7 ] * t_r / pow( 2, 15 ) ) * t_r ) / pow( 2, 4 );
                p_a = ( S * p_r + O ) / pow( 2, 14 );

                return new Double[]{ p_a };
            }
        }*/;

        private String name;
        private final UUID service, data, config, calibration;
        private byte enableCode; // See getEnableSensorCode for explanation.
        public int[] coefficients; // Calibration coefficients

        /**
         * Constructor called by the Gyroscope because he needs a different enabler code.
         **/
        Sensor( String name, UUID service, UUID data, UUID config, UUID calibration, byte enableCode ) {
            this.name = name;
            this.service = service;
            this.data = data;
            this.config = config;
            this.enableCode = enableCode;
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