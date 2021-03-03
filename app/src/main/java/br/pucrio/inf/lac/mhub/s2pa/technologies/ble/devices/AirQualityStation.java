package br.pucrio.inf.lac.mhub.s2pa.technologies.ble.devices;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.pucrio.inf.lac.mhub.s2pa.base.TechnologyDevice;
import br.pucrio.inf.lac.mhub.s2pa.base.TechnologySensor;

import static java.util.UUID.fromString;

/**
 * Created by luis on 10/08/15.
 * Module for the AirQualityStation, it is used when a communication
 * with a AirQualityStation is required
 */
public class AirQualityStation implements TechnologyDevice {
    public final static UUID
            UUID_AQS_SERV = fromString("01010101-0101-0101-0101-010101010101"), // Service

            UUID_IRT_DATA = fromString("01010101-0101-0101-0166-616465524740"), // Temperature Sensor
            UUID_HUM_DATA = fromString("01010101-0101-0101-0166-616465524741"), // Humidity Sensor
            UUID_MQ3_DATA = fromString("01010101-0101-0101-0101-010101524743"), // MQ3 Gas Sensor
            UUID_MQ4_DATA = fromString("01010101-0101-0101-0101-010101524744"), // MQ4 Gas Sensor
            UUID_MQ6_DATA = fromString("01010101-0101-0101-0101-010101524745"), // MQ6 Gas Sensor
            UUID_MQ7_DATA = fromString("01010101-0101-0101-0101-010101524746"); // MQ7 Gas Sensor

    private static final byte ENABLE_SENSOR_CODE = 1;

    private enum Sensor implements TechnologySensor {
        IR_TEMPERATURE( "Temperature", UUID_AQS_SERV, UUID_IRT_DATA, null ) {
            @Override
            public Double[] convert( final byte [] value ) {
                return new Double[]{ parseData( value ) };
            }
        },
        HUMIDITY( "Humidity", UUID_AQS_SERV, UUID_HUM_DATA, null ) {
            @Override
            public Double[] convert(final byte[] value) {
                return new Double[]{ parseData( value ) };
            }
        },
        MQ3( "MQ3", UUID_AQS_SERV, UUID_MQ3_DATA, null ) {
            @Override
            public Double[] convert(final byte[] value) {
                return new Double[]{ parseData( value ) };
            }
        },
        MQ4( "MQ4", UUID_AQS_SERV, UUID_MQ4_DATA, null ) {
            @Override
            public Double[] convert(final byte [] value) {
                return new Double[]{ parseData( value ) };
            }
        },
        MQ6( "MQ6", UUID_AQS_SERV, UUID_MQ6_DATA, null ) {
            @Override
            public Double[] convert(final byte [] value) {
                return new Double[]{ parseData( value ) };
            }
        },
        MQ7( "MQ7", UUID_AQS_SERV, UUID_MQ7_DATA, null ) {
            @Override
            public Double[] convert(final byte [] value) {
                return new Double[]{ parseData( value ) };
            }
        };

        private String name;
        private final UUID service, data, config;
        private byte enableCode; // See getEnableSensorCode for explanation.

        /**
         * Constructor called by all the sensors except Gyroscope
         **/
        Sensor( String name, UUID service, UUID data, UUID config ) {
            this.name    = name;
            this.service = service;
            this.data    = data;
            this.config  = config;
            this.enableCode = ENABLE_SENSOR_CODE;
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
            return null;
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
        public Double[] convert(byte[] value) {
            throw new UnsupportedOperationException( "Programmer error, the individual enum classes are supposed to override this method." );
        }

        private static Double parseData( final byte[] value ) {
            short lsb = (short) (value[0] & 0xff);
            String str = String.valueOf( lsb );
            return Double.parseDouble( str );
            /*String str = AppUtils.bytesToHex( value );
            Long i = Long.parseLong( str, 16 );
            return Double.longBitsToDouble( i );*/
        }

        public static Sensor getFromServiceName(String serviceName) {
            for( Sensor s : Sensor.values() ) {
                if( s.getName().equals( serviceName ) ) {
                    return s;
                }
            }
            return null;
        }

        public static List<TechnologySensor> getFromServiceUuid(UUID uuid) {
            List<TechnologySensor> temp = new ArrayList<>();
            for( Sensor s : Sensor.values() ) {
                if( s.getService().equals( uuid ) ) {
                    temp.add( s );
                }
            }
            return temp;
        }

        public static Sensor getFromCharacteristicUuid(UUID uuid) {
            for( Sensor s : Sensor.values() ) {
                if( s.getData().equals( uuid ) ) {
                    return s;
                }
            }
            return null;
        }
    }

    @Override
    public TechnologySensor getServiceByName( String serviceName ) {
        return Sensor.getFromServiceName( serviceName );
    }

    @Override
    public List<TechnologySensor> getServiceByUUID( UUID uuid ) {
        return Sensor.getFromServiceUuid( uuid );
    }

    @Override
    public TechnologySensor getCharacteristicByUUID( UUID uuid ) {
        return Sensor.getFromCharacteristicUuid( uuid );
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
