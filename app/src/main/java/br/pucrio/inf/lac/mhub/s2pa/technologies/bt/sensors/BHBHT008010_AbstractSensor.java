package br.pucrio.inf.lac.mhub.s2pa.technologies.bt.sensors;

import java.util.UUID;

import br.pucrio.inf.lac.mhub.s2pa.base.TechnologySensorExtended;

public abstract class BHBHT008010_AbstractSensor implements TechnologySensorExtended {

    private GeneralPacketInfo generalPacketInfo = null;

    private AccelerometerPacketInfo accelerometerPacketInfo = null;

    private BreathingPacketInfo breathingPacketInfo = null;

    private SummaryPacketInfo summaryPacketInfo = null;

    private ECGPacketInfo ecgPacketInfo = null;

    private RtoRPacketInfo rtoRPacketInfo = null;

    protected final int GP_MSG_ID = 0x20;

    protected final int BREATHING_MSG_ID = 0x21;

    protected final int ECG_MSG_ID = 0x22;

    protected final int RtoR_MSG_ID = 0x24;

    protected final int ACCEL_100mg_MSG_ID = 0x2A;

    protected final int SUMMARY_MSG_ID = 0x2B;

    private String name = null;


    public BHBHT008010_AbstractSensor() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public String getName() {
        if (name == null) {
            return getClass().getSimpleName();
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public UUID getService() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UUID getData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UUID getConfig() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public byte getEnableSensorCode() {
        // TODO Auto-generated method stub
        return 0;
    }

    public abstract int getPacketMsgID();

    @Override
    public abstract Double[] convert(byte[] value);


    protected GeneralPacketInfo getGeneralPacketInfo() {
        if (generalPacketInfo == null) {
            generalPacketInfo = new GeneralPacketInfo();
        }
        return generalPacketInfo;
    }

    protected AccelerometerPacketInfo getAccelerometerPacketInfo() {
        if (accelerometerPacketInfo == null) {
            accelerometerPacketInfo = new AccelerometerPacketInfo();
        }
        return accelerometerPacketInfo;
    }

    protected BreathingPacketInfo getBreathingPacketInfo() {
        if (breathingPacketInfo == null) {
            breathingPacketInfo = new BreathingPacketInfo();
        }
        return breathingPacketInfo;
    }

    protected ECGPacketInfo getECGPacketInfo() {
        if (ecgPacketInfo == null) {
            ecgPacketInfo = new ECGPacketInfo();
        }
        return ecgPacketInfo;
    }

    protected RtoRPacketInfo getRtoRPacketInfo() {
        if (rtoRPacketInfo == null) {
            rtoRPacketInfo = new RtoRPacketInfo();
        }
        return rtoRPacketInfo;
    }

    protected SummaryPacketInfo getSummaryPacketInfo() {
        if (summaryPacketInfo == null) {
            summaryPacketInfo = new SummaryPacketInfo();
        }
        return summaryPacketInfo;
    }


    public class GeneralPacketInfo {
        private byte _SequenceNum;
        private int _TSYear;
        private byte _TSMonth;
        private byte _TSDay;
        private long _MsOfDay;
        private int _HeartRate;
        private double _RespirationRate;
        private double _SkinTemperature;
        private int _Posture;
        private double _VMU;
        private double _PeakAcceleration;
        private double _BatteryVoltage;
        private double _BreathingWaveAmpl;
        private double _ECGAmplitude;
        private double _ECGNoise;
        private double _XAxis_Accn_Min;
        private double _XAxis_Accn_Peak;
        private double _YAxis_Accn_Min;
        private double _YAxis_Accn_Peak;
        private double _ZAxis_Accn_Min;
        private double _ZAxis_Accn_Peak;
        private int _ZephyrSysChan;
        private int _GSR;
        private byte _ROGStatus;
        private byte _AlarmSts;
        private byte _WornStatus;
        private byte _UserIntfBtnStatus;
        private byte _BHSigLowStatus;
        private byte _BHSensConnStatus;
        private byte _BatteryStatus;


        public byte GetSeqNum(byte[] Payload) {
            _SequenceNum = (byte) (Payload[0] & 0xFF);
            return _SequenceNum;
        }

        public int GetTSYear(byte[] Payload) {
            _TSYear = (Payload[1]) & (0xFF);
            _TSYear = _TSYear | (((Payload[2]) & 0xFF) << 8);
            return _TSYear;
        }

        public byte GetTSMonth(byte[] Payload) {
            _TSMonth = Payload[3];
            return _TSMonth;
        }

        public byte GetTSDay(byte[] Payload) {
            _TSDay = Payload[4];
            return _TSDay;
        }

        public long GetMsofDay(byte[] Payload) {
            _MsOfDay = 0;
            _MsOfDay = (Payload[5]) & (0xFF);
            _MsOfDay = _MsOfDay | (((Payload[6]) & (0xFF)) << 8);
            _MsOfDay = _MsOfDay | (((Payload[7]) & (0xFF)) << 16);
            _MsOfDay = _MsOfDay | (((Payload[8]) & (0xFF)) << 24);
            return _MsOfDay;
        }

        public int GetHeartRate(byte[] Payload) {
            _HeartRate = (int) (Payload[9] & 0xFF);
            return _HeartRate;
        }

        public double GetRespirationRate(byte[] Payload) {
            short RespRate = 0;
            RespRate = (short) (((Payload[12]) & 0xFF) << 8);
            short RespRate1 = 0;
            RespRate1 = (short) ((Payload[11]) & 0xFF);
            _RespirationRate = ((double) ((RespRate) | (RespRate1)) / 10);
            return _RespirationRate;
        }

        public double GetSkinTemperature(byte[] Payload) {
            short SkinTemp = 0;
            SkinTemp = (short) (((Payload[14]) & 0xFF) << 8);
            SkinTemp = (short) (SkinTemp | ((Payload[13]) & 0xFF));
            _SkinTemperature = (double) SkinTemp / 10;
            return _SkinTemperature;
        }

        public int GetPosture(byte[] Payload) {
            short Posture = 0;
            Posture = (short) ((Payload[15]) & 0xFF);
            Posture = (short) ((Posture) | (((Payload[16]) & (0xFF)) << 8));
            _Posture = (int) Posture;
            return _Posture;
        }

        public double GetVMU(byte[] Payload) {
            short VMUTemp = 0;
            VMUTemp = (short) ((Payload[17]) & 0xFF);
            VMUTemp = (short) ((VMUTemp) | (((Payload[18]) & (0xFF)) << 8));
            _VMU = ((double) VMUTemp / 100);
            return _VMU;
        }

        public double GetPeakAcceleration(byte[] Payload) {
            short PeakAcc = 0;
            PeakAcc = (short) ((Payload[19]) & 0xFF);
            PeakAcc = (short) ((PeakAcc) | (((Payload[20]) & (0xFF)) << 8));
            _PeakAcceleration = ((double) PeakAcc / 100);

            return _PeakAcceleration;
        }

        public double GetBatteryVoltage(byte[] Payload) {
            short BatteryVoltageTemp = 0;
            BatteryVoltageTemp = (short) ((Payload[21]) & 0xFF);
            BatteryVoltageTemp = (short) ((BatteryVoltageTemp) | (((Payload[22]) & (0xFF)) << 8));
            _BatteryVoltage = ((double) BatteryVoltageTemp / 1000);

            return _BatteryVoltage;
        }

        public double GetBreathingWaveAmplitude(byte[] Payload) {
            short BreathingWaveAmplTemp = 0;
            BreathingWaveAmplTemp = (short) ((Payload[23]) & 0xFF);
            BreathingWaveAmplTemp = (short) ((BreathingWaveAmplTemp) | (((Payload[24]) & (0xFF)) << 8));
            _BreathingWaveAmpl = ((double) BreathingWaveAmplTemp / 1000);

            return _BreathingWaveAmpl;
        }

        public double GetECGAmplitude(byte[] Payload) {
            short ECGAmplitudeTemp = 0;
            ECGAmplitudeTemp = (short) ((Payload[25]) & 0xFF);
            ECGAmplitudeTemp = (short) ((ECGAmplitudeTemp) | (((Payload[26]) & (0xFF)) << 8));
            _ECGAmplitude = ((double) ECGAmplitudeTemp / 1000000);

            return _ECGAmplitude;
        }

        public double GetECGNoise(byte[] Payload) {
            short ECGNoiseTemp = 0;
            ECGNoiseTemp = (short) ((Payload[27]) & 0xFF);
            ECGNoiseTemp = (short) ((ECGNoiseTemp) | (((Payload[28]) & (0xFF)) << 8));
            _ECGNoise = ((double) ECGNoiseTemp / 1000000);

            return _ECGNoise;
        }

        public double GetX_AxisAccnMin(byte[] Payload) {
            short XAxis_Accn_MinTemp = 0;
            XAxis_Accn_MinTemp = (short) ((Payload[29]) & 0xFF);
            XAxis_Accn_MinTemp = (short) ((XAxis_Accn_MinTemp) | (((Payload[30]) & (0xFF)) << 8));
            _XAxis_Accn_Min = ((double) XAxis_Accn_MinTemp / 100);

            return _XAxis_Accn_Min;
        }

        public double GetX_AxisAccnPeak(byte[] Payload) {
            short XAxis_Accn_PeakTemp = 0;
            XAxis_Accn_PeakTemp = (short) ((Payload[31]) & 0xFF);
            XAxis_Accn_PeakTemp = (short) ((XAxis_Accn_PeakTemp) | (((Payload[32]) & (0xFF)) << 8));
            _XAxis_Accn_Peak = ((double) XAxis_Accn_PeakTemp / 100);

            return _XAxis_Accn_Peak;
        }

        public double GetY_AxisAccnMin(byte[] Payload) {
            short YAxis_Accn_MinTemp = 0;
            YAxis_Accn_MinTemp = (short) ((Payload[33]) & 0xFF);
            YAxis_Accn_MinTemp = (short) ((YAxis_Accn_MinTemp) | (((Payload[34]) & (0xFF)) << 8));
            _YAxis_Accn_Min = ((double) YAxis_Accn_MinTemp / 100);

            return _YAxis_Accn_Min;
        }

        public double GetY_AxisAccnPeak(byte[] Payload) {
            short YAxis_Accn_PeakTemp = 0;
            YAxis_Accn_PeakTemp = (short) ((Payload[35]) & 0xFF);
            YAxis_Accn_PeakTemp = (short) ((YAxis_Accn_PeakTemp) | (((Payload[36]) & (0xFF)) << 8));
            _YAxis_Accn_Peak = ((double) YAxis_Accn_PeakTemp / 100);

            return _YAxis_Accn_Peak;
        }

        public double GetZ_AxisAccnMin(byte[] Payload) {
            short ZAxis_Accn_MinTemp = 0;
            ZAxis_Accn_MinTemp = (short) ((Payload[37]) & 0xFF);
            ZAxis_Accn_MinTemp = (short) ((ZAxis_Accn_MinTemp) | (((Payload[38]) & (0xFF)) << 8));
            _ZAxis_Accn_Min = ((double) ZAxis_Accn_MinTemp / 100);

            return _ZAxis_Accn_Min;
        }

        public double GetZ_AxisAccnPeak(byte[] Payload) {
            short ZAxis_Accn_PeakTemp = 0;
            ZAxis_Accn_PeakTemp = (short) ((Payload[39]) & 0xFF);
            ZAxis_Accn_PeakTemp = (short) ((ZAxis_Accn_PeakTemp) | (((Payload[40]) & (0xFF)) << 8));
            _ZAxis_Accn_Peak = ((double) ZAxis_Accn_PeakTemp / 100);

            return _ZAxis_Accn_Peak;
        }

        public int GetZephyrSysChan(byte[] Payload) {
            short ZephyrSysChanTemp = 0;
            ZephyrSysChanTemp = (short) ((Payload[41]) & 0xFF);
            ZephyrSysChanTemp = (short) ((ZephyrSysChanTemp) | (((Payload[42]) & (0xFF)) << 8));
            _ZephyrSysChan = (int) ZephyrSysChanTemp;
            return _ZephyrSysChan;
        }

        public int GetGSR(byte[] Payload) {
            short GSRTemp = 0;
            GSRTemp = (short) ((Payload[43]) & 0xFF);
            GSRTemp = (short) ((GSRTemp) | (((Payload[44]) & (0xFF)) << 8));
            _GSR = (int) GSRTemp;

            return _GSR;
        }

        public byte GetROGStatus(byte[] Payload) {
            _ROGStatus = (byte) ((Payload[49]) & 0xFF);
            return _ROGStatus;
        }

        public byte GetAlarmStatus(byte[] Payload) {

            _AlarmSts = (byte) ((Payload[50]) & 0xFF);
            return _AlarmSts;
        }

        public byte GetBatteryStatus(byte[] Payload) {
            _BatteryStatus = (byte) (Payload[51] & (0x7F));
            return _BatteryStatus;
        }

        public byte GetBHSensConnStatus(byte[] Payload) {
            _BHSensConnStatus = (byte) ((Payload[52] & 0x10) >> 4);
            return _BHSensConnStatus;
        }

        public byte _GetBHSigLowStatus(byte[] Payload) {
            _BHSigLowStatus = (byte) ((Payload[52] & 0x20) >> 5);
            return _BHSigLowStatus;
        }

        public byte GetUserIntfBtnStatus(byte[] Payload) {
            _UserIntfBtnStatus = (byte) ((Payload[52] & 0x40) >> 6);
            return _UserIntfBtnStatus;
        }

        public byte GetWornStatus(byte[] Payload) {
            _WornStatus = (byte) ((Payload[52] & 0x80) >> 7);

            return _WornStatus;
        }
    }

    public class ECGPacketInfo {
        private byte _SequenceNum;

        private int _TSYear;
        private byte _TSMonth;
        private byte _TSDay;
        private long _MsOfDay;
        public final short NUM_ECG_SAMPLES_PER_PACKET = 63;
        private Double[] _ECGSamples;

        public ECGPacketInfo() {
            _ECGSamples = new Double[NUM_ECG_SAMPLES_PER_PACKET];
        }

        public byte GetSeqNum(byte[] Payload) {
            _SequenceNum = (byte) (Payload[0] & 0xFF);
            return _SequenceNum;
        }

        public int GetTSYear(byte[] Payload) {
            _TSYear = (Payload[1]) & (0xFF);
            _TSYear = _TSYear | (((Payload[2]) & 0xFF) << 8);
            return _TSYear;
        }

        public byte GetTSMonth(byte[] Payload) {
            _TSMonth = Payload[3];
            return _TSMonth;
        }

        public byte GetTSDay(byte[] Payload) {
            _TSDay = Payload[4];
            return _TSDay;
        }

        public long GetMsofDay(byte[] Payload) {
            _MsOfDay = 0;
            _MsOfDay = (Payload[5]) & (0xFF);
            _MsOfDay = _MsOfDay | (((Payload[6]) & (0xFF)) << 8);
            _MsOfDay = _MsOfDay | (((Payload[7]) & (0xFF)) << 16);
            _MsOfDay = _MsOfDay | (((Payload[8]) & (0xFF)) << 24);
            return _MsOfDay;
        }

        public Double[] GetECGSamples(byte[] Payload) {
            long PackedData;
            short EcgSampleindex, i, j;
            short NumECGSamplesPer5bytes = 4;
            short PayloadIndex = 0;
            short NumBitsPerECGSample = 10;

            short NumIterations4ECGdata = (short) (NUM_ECG_SAMPLES_PER_PACKET / NumECGSamplesPer5bytes);
            short NumECGSamplesLeftover = (short) (NUM_ECG_SAMPLES_PER_PACKET % NumECGSamplesPer5bytes);
            EcgSampleindex = 0;
            for (i = 0; i < NumIterations4ECGdata; i++) {

                PackedData = 0;
                /*Extracting 4 ECG samples from 5 bytes into one variable*/
                PackedData = (long) (Payload[9 + PayloadIndex]) & (0xFF);
                PayloadIndex++;
                PackedData = (long) (PackedData) | ((Payload[9 + PayloadIndex] & (0xFF)) << 8);
                PayloadIndex++;
                PackedData = (long) (PackedData) | ((Payload[9 + PayloadIndex] & (0xFF)) << 16);
                PayloadIndex++;
                PackedData = (long) (PackedData) | ((Payload[9 + PayloadIndex] & (0xFF)) << 24);
                PayloadIndex++;
				/* Fixing the Sign Changing issue */
                PackedData = PackedData & (0x00000000FFFFFFFFL);
                long temp = (long) ((Payload[9 + PayloadIndex] & (0xFF)) << 24);
                temp = temp << 8;
                PackedData = (long) (PackedData | temp);
                PayloadIndex++;
                for (j = 0; j < NumECGSamplesPer5bytes; j++) {
                    _ECGSamples[EcgSampleindex] = Double.valueOf(((PackedData) & (0x3FF)));
                    EcgSampleindex++;
                    PackedData = PackedData >> NumBitsPerECGSample;
                }
            }
            PackedData = 0;
			/*Extracting the remaining ECG samples from 5 bytes into one variable*/
            PackedData = (Payload[9 + PayloadIndex]) & (0xFF);
            PayloadIndex++;
            PackedData = PackedData | ((Payload[9 + PayloadIndex] & (0xFF)) << 8);
            PayloadIndex++;
            PackedData = PackedData | ((Payload[9 + PayloadIndex] & (0xFF)) << 16);
            PayloadIndex++;
            PackedData = PackedData | ((Payload[9 + PayloadIndex] & (0xFF)) << 24);
            for (j = 0; j < NumECGSamplesLeftover; j++) {
                _ECGSamples[EcgSampleindex] = Double.valueOf(((PackedData) & (0x3FF)));
                EcgSampleindex++;
                PackedData = PackedData >> NumBitsPerECGSample;
            }
            return _ECGSamples;
        }
    }

    public class BreathingPacketInfo {
        private byte _SequenceNum;

        private int _TSYear;
        private byte _TSMonth;
        private byte _TSDay;
        private long _MsOfDay;
        public final short NUM_BREATHING_SAMPLES_PER_PACKET = 18;
        private Double[] _BreathingSamples;

        public BreathingPacketInfo() {
            _BreathingSamples = new Double[NUM_BREATHING_SAMPLES_PER_PACKET];
        }

        public byte GetSeqNum(byte[] Payload) {
            _SequenceNum = (byte) (Payload[0] & 0xFF);
            return _SequenceNum;
        }

        public int GetTSYear(byte[] Payload) {
            _TSYear = (Payload[1]) & (0xFF);
            _TSYear = _TSYear | (((Payload[2]) & 0xFF) << 8);
            return _TSYear;
        }

        public byte GetTSMonth(byte[] Payload) {
            _TSMonth = Payload[3];
            return _TSMonth;
        }

        public byte GetTSDay(byte[] Payload) {
            _TSDay = Payload[4];
            return _TSDay;
        }

        public long GetMsofDay(byte[] Payload) {
            _MsOfDay = 0;
            _MsOfDay = (Payload[5]) & (0xFF);
            _MsOfDay = _MsOfDay | (((Payload[6]) & (0xFF)) << 8);
            _MsOfDay = _MsOfDay | (((Payload[7]) & (0xFF)) << 16);
            _MsOfDay = _MsOfDay | (((Payload[8]) & (0xFF)) << 24);
            return _MsOfDay;
        }

        public Double[] GetBreathingSamples(byte[] Payload) {
            long PackedData;
            short BreathingSampleindex, i, j;
            short NumBreathingSamplesPer5bytes = 4;
            short PayloadIndex = 0;
            short NumBitsPerBreathingSample = 10;

            short NumIterations4Breathingdata = (short) (NUM_BREATHING_SAMPLES_PER_PACKET / NumBreathingSamplesPer5bytes);
            short NumBreathingSamplesLeftover = (short) (NUM_BREATHING_SAMPLES_PER_PACKET % NumBreathingSamplesPer5bytes);
            BreathingSampleindex = 0;
            for (i = 0; i < NumIterations4Breathingdata; i++) {

                PackedData = 0;
				/*Extracting 4 Breathing samples from 5 bytes into one variable*/
                PackedData = (Payload[9 + PayloadIndex]) & (0xFF);
                PayloadIndex++;
                PackedData = PackedData | ((Payload[9 + PayloadIndex] & (0xFF)) << 8);
                PayloadIndex++;
                PackedData = PackedData | ((Payload[9 + PayloadIndex] & (0xFF)) << 16);
                PayloadIndex++;
                PackedData = PackedData | ((Payload[9 + PayloadIndex] & (0xFF)) << 24);
                PayloadIndex++;
				/* Fixing the Sign Changing issue */
                PackedData = PackedData & (0x00000000FFFFFFFFL);
                long temp = (long) ((Payload[9 + PayloadIndex] & (0xFF)) << 24);
                temp = temp << 8;
                PackedData = PackedData | temp;
                PayloadIndex++;
                for (j = 0; j < NumBreathingSamplesPer5bytes; j++) {
                    _BreathingSamples[BreathingSampleindex] = Double.valueOf(((PackedData) & (0x3FF)));
                    BreathingSampleindex++;
                    PackedData = PackedData >> NumBitsPerBreathingSample;
                }
            }

            PackedData = 0;
			/*Extracting the remaining Breathing samples from 5 bytes into one variable*/
            PackedData = (Payload[9 + PayloadIndex]) & (0xFF);
            PayloadIndex++;
            PackedData = PackedData | ((Payload[9 + PayloadIndex] & (0xFF)) << 8);
            PayloadIndex++;
            PackedData = PackedData | ((Payload[9 + PayloadIndex] & (0xFF)) << 16);

            for (j = 0; j < NumBreathingSamplesLeftover; j++) {
                _BreathingSamples[BreathingSampleindex] = Double.valueOf(((PackedData) & (0x3FF)));
                BreathingSampleindex++;
                PackedData = PackedData >> NumBitsPerBreathingSample;
            }
            return _BreathingSamples;
        }

    }

    public class RtoRPacketInfo {
        private byte _SequenceNum;

        private int _TSYear;
        private byte _TSMonth;
        private byte _TSDay;
        private long _MsOfDay;
        public final short NUM_RtoR_SAMPLES_PER_PACKET = 18;
        private int[] _RtoRSamples;

        public RtoRPacketInfo() {
            _RtoRSamples = new int[NUM_RtoR_SAMPLES_PER_PACKET];
        }

        public byte GetSeqNum(byte[] Payload) {
            _SequenceNum = (byte) (Payload[0] & 0xFF);
            return _SequenceNum;
        }

        public int GetTSYear(byte[] Payload) {
            _TSYear = (Payload[1]) & (0xFF);
            _TSYear = _TSYear | (((Payload[2]) & 0xFF) << 8);
            return _TSYear;
        }

        public byte GetTSMonth(byte[] Payload) {
            _TSMonth = Payload[3];
            return _TSMonth;
        }

        public byte GetTSDay(byte[] Payload) {
            _TSDay = Payload[4];
            return _TSDay;
        }

        public long GetMsofDay(byte[] Payload) {
            _MsOfDay = 0;
            _MsOfDay = (Payload[5]) & (0xFF);
            _MsOfDay = _MsOfDay | (((Payload[6]) & (0xFF)) << 8);
            _MsOfDay = _MsOfDay | (((Payload[7]) & (0xFF)) << 16);
            _MsOfDay = _MsOfDay | (((Payload[8]) & (0xFF)) << 24);
            return _MsOfDay;
        }

        public int[] GetRtoRSamples(byte[] Payload) {
            short i, index;
            index = 0;
            for (i = 0; i < NUM_RtoR_SAMPLES_PER_PACKET; i++) {
                _RtoRSamples[i] = 0;
                _RtoRSamples[i] = ((Payload[9 + index]) & (0xFF));
                index++;
                _RtoRSamples[i] = (_RtoRSamples[i] | (((Payload[9 + index]) & (0xFF)) << 8));
                index++;
            }
            return _RtoRSamples;
        }
    }


    public class AccelerometerPacketInfo {
        private byte _SequenceNum;

        private int _TSYear;
        private byte _TSMonth;
        private byte _TSDay;
        private long _MsOfDay;

        public short NUM_ACCN_SAMPLES = 20;
        public XYZ_AccelerationData XYZ_AccnDataSamples;

        public AccelerometerPacketInfo() {
            XYZ_AccnDataSamples = new XYZ_AccelerationData();
        }

        public byte GetSeqNum(byte[] Payload) {
            _SequenceNum = (byte) (Payload[0] & 0xFF);
            return _SequenceNum;
        }

        public int GetTSYear(byte[] Payload) {
            _TSYear = (Payload[1]) & (0xFF);
            _TSYear = _TSYear | (((Payload[2]) & 0xFF) << 8);
            return _TSYear;
        }

        public byte GetTSMonth(byte[] Payload) {
            _TSMonth = Payload[3];
            return _TSMonth;
        }

        public byte GetTSDay(byte[] Payload) {
            _TSDay = Payload[4];
            return _TSDay;
        }

        public long GetMsofDay(byte[] Payload) {
            _MsOfDay = 0;
            _MsOfDay = (Payload[5]) & (0xFF);
            _MsOfDay = _MsOfDay | (((Payload[6]) & (0xFF)) << 8);
            _MsOfDay = _MsOfDay | (((Payload[7]) & (0xFF)) << 16);
            _MsOfDay = _MsOfDay | (((Payload[8]) & (0xFF)) << 24);
            return _MsOfDay;
        }

        public Double[] GetX_axisAccnData() {
            return XYZ_AccnDataSamples.X_axisAccnData;
        }

        public Double[] GetY_axisAccnData() {
            return XYZ_AccnDataSamples.Y_axisAccnData;
        }

        public Double[] GetZ_axisAccnData() {
            return XYZ_AccnDataSamples.Z_axisAccnData;
        }

        public void UnpackAccelerationData(byte[] Payload)

        {
            short i, j;
            long PackedData;
            short PayloadIndex = 0;
            short NumBytesinPackingPattern = 15;
            short SizeAccelerometerDataPayload = 75;
            short NumBitsPerAccelerationSample = 10;
            short NumIterationsPer15Bytes = (short) (SizeAccelerometerDataPayload / NumBytesinPackingPattern);
            short NumAccelerationSamplesPer5bytes = 4;
            short X_axis_SampleIndex = 0;
            short Y_axis_SampleIndex = 0;
            short Z_axis_SampleIndex = 0;
            short SIGN_CONV_FACTR = (0x1) << 9;
            short SIGN_CONV_FACTR_SIGNED = (0x1) << 10;
            int SignBit;
            for (i = 0; i < NumIterationsPer15Bytes; i++) {
                PackedData = 0;
				/*Extracting 4 Acceleration Samples from 5 bytes into one variable*/
                PackedData = (Payload[9 + PayloadIndex]) & (0xFF);
                PayloadIndex++;
                PackedData = PackedData | ((Payload[9 + PayloadIndex] & (0xFF)) << 8);
                PayloadIndex++;
                PackedData = PackedData | ((Payload[9 + PayloadIndex] & (0xFF)) << 16);
                PayloadIndex++;
                PackedData = PackedData | ((Payload[9 + PayloadIndex] & (0xFF)) << 24);
                PayloadIndex++;
				/* Fixing the Sign Changing issue */
                PackedData = PackedData & (0x00000000FFFFFFFFL);
                long temp = (long) ((Payload[9 + PayloadIndex] & (0xFF)) << 24);
                temp = temp << 8;
                PackedData = PackedData | temp;
                //temp = 1099511627775L;//Equivalent of 0xFFFFFFFFFF
                //PackedData = PackedData & temp;//Retaining the relevant 40 bits
                PayloadIndex++;

                for (j = 0; j < NumAccelerationSamplesPer5bytes; j++) {
                    switch (j) {
                        case 0:
                        case 3:
                            short tempAccn = (short) ((PackedData) & (0x3FF));
                            SignBit = (tempAccn >> 9);
                            if (1 == SignBit) {
                                tempAccn = (short) (tempAccn - SIGN_CONV_FACTR_SIGNED);
                            }
                            XYZ_AccnDataSamples.X_axisAccnData[X_axis_SampleIndex] = ((double) tempAccn / 10);
                            X_axis_SampleIndex++;
                            PackedData = PackedData >> NumBitsPerAccelerationSample;
                            break;

                        case 1:
                            tempAccn = (short) ((PackedData) & (0x3FF));
                            SignBit = (tempAccn >> 9);
                            if (1 == SignBit) {

                                tempAccn = (short) (tempAccn - SIGN_CONV_FACTR_SIGNED);
                            }
                            XYZ_AccnDataSamples.Y_axisAccnData[Y_axis_SampleIndex] = ((double) tempAccn / 10);
                            Y_axis_SampleIndex++;
                            PackedData = PackedData >> NumBitsPerAccelerationSample;
                            break;

                        case 2:
                            tempAccn = (short) ((PackedData) & (0x3FF));
                            SignBit = (tempAccn >> 9);
                            if (1 == SignBit) {

                                tempAccn = (short) (tempAccn - SIGN_CONV_FACTR_SIGNED);
                            }
                            XYZ_AccnDataSamples.Z_axisAccnData[Z_axis_SampleIndex] = ((double) tempAccn / 10);
                            Z_axis_SampleIndex++;
                            PackedData = PackedData >> NumBitsPerAccelerationSample;
                            break;

                    }
                }
                PackedData = 0;
				/*Extracting the next 4 Acceleration Samples from 5 bytes into one variable*/
                PackedData = (Payload[9 + PayloadIndex]) & (0xFF);
                PayloadIndex++;
                PackedData = PackedData | ((Payload[9 + PayloadIndex] & (0xFF)) << 8);
                PayloadIndex++;
                PackedData = PackedData | ((Payload[9 + PayloadIndex] & (0xFF)) << 16);
                PayloadIndex++;
                PackedData = PackedData | ((Payload[9 + PayloadIndex] & (0xFF)) << 24);
                PayloadIndex++;
				/* Fixing the Sign Changing issue */
                PackedData = PackedData & (0x00000000FFFFFFFFL);
                temp = (long) ((Payload[9 + PayloadIndex] & (0xFF)) << 24);
                temp = temp << 8;
                PackedData = PackedData | temp;

                PayloadIndex++;
				/* Extracting individual X, Y, Z samples */
                for (j = 0; j < NumAccelerationSamplesPer5bytes; j++) {
                    switch (j) {
                        case 0:
                        case 3:
                            short tempAccn = (short) ((PackedData) & (0x3FF));
                            SignBit = (tempAccn >> 9);
                            if (1 == SignBit) {

                                tempAccn = (short) (tempAccn - SIGN_CONV_FACTR_SIGNED);
                            }
                            XYZ_AccnDataSamples.Y_axisAccnData[Y_axis_SampleIndex] = ((double) tempAccn / 10);
                            Y_axis_SampleIndex++;
                            PackedData = PackedData >> NumBitsPerAccelerationSample;
                            break;

                        case 1:
                            tempAccn = (short) ((PackedData) & (0x3FF));
                            SignBit = (tempAccn >> 9);
                            if (1 == SignBit) {

                                tempAccn = (short) (tempAccn - SIGN_CONV_FACTR_SIGNED);
                            }
                            XYZ_AccnDataSamples.Z_axisAccnData[Z_axis_SampleIndex] = ((double) tempAccn / 10);
                            Z_axis_SampleIndex++;
                            PackedData = PackedData >> NumBitsPerAccelerationSample;
                            break;

                        case 2:
                            tempAccn = (short) ((PackedData) & (0x3FF));
                            SignBit = (tempAccn >> 9);
                            if (1 == SignBit) {

                                tempAccn = (short) (tempAccn - SIGN_CONV_FACTR_SIGNED);
                            }
                            XYZ_AccnDataSamples.X_axisAccnData[X_axis_SampleIndex] = ((double) tempAccn / 10);
                            X_axis_SampleIndex++;
                            PackedData = PackedData >> NumBitsPerAccelerationSample;
                            break;

                    }
                }

                PackedData = 0;
				/*Extracting the next 4 Acceleration Samples from 5 bytes into one variable*/
                PackedData = (Payload[9 + PayloadIndex]) & (0xFF);
                PayloadIndex++;
                PackedData = PackedData | ((Payload[9 + PayloadIndex] & (0xFF)) << 8);
                PayloadIndex++;
                PackedData = PackedData | ((Payload[9 + PayloadIndex] & (0xFF)) << 16);
                PayloadIndex++;
                PackedData = PackedData | ((Payload[9 + PayloadIndex] & (0xFF)) << 24);
                PayloadIndex++;
				/* Fixing the Sign Changing issue */
                PackedData = PackedData & (0x00000000FFFFFFFFL);
                temp = (long) ((Payload[9 + PayloadIndex] & (0xFF)) << 24);
                temp = temp << 8;
                PackedData = PackedData | temp;

                PayloadIndex++;

                for (j = 0; j < NumAccelerationSamplesPer5bytes; j++) {
                    switch (j) {
                        case 0:
                        case 3:
                            short tempAccn = (short) ((PackedData) & (0x3FF));
                            SignBit = (tempAccn >> 9);
                            if (1 == SignBit) {

                                tempAccn = (short) (tempAccn - SIGN_CONV_FACTR_SIGNED);
                            }
                            XYZ_AccnDataSamples.Z_axisAccnData[Z_axis_SampleIndex] = ((double) tempAccn / 10);
                            Z_axis_SampleIndex++;
                            PackedData = PackedData >> NumBitsPerAccelerationSample;
                            break;

                        case 1:
                            tempAccn = (short) ((PackedData) & (0x3FF));
                            SignBit = (tempAccn >> 9);
                            if (1 == SignBit) {

                                tempAccn = (short) (tempAccn - SIGN_CONV_FACTR_SIGNED);
                            }
                            XYZ_AccnDataSamples.X_axisAccnData[X_axis_SampleIndex] = ((double) tempAccn / 10);
                            X_axis_SampleIndex++;
                            PackedData = PackedData >> NumBitsPerAccelerationSample;
                            break;

                        case 2:
                            tempAccn = (short) ((PackedData) & (0x3FF));
                            SignBit = (tempAccn >> 9);
                            if (1 == SignBit) {

                                tempAccn = (short) (tempAccn - SIGN_CONV_FACTR_SIGNED);
                            }
                            XYZ_AccnDataSamples.Y_axisAccnData[Y_axis_SampleIndex] = ((double) tempAccn / 10);
                            Y_axis_SampleIndex++;
                            PackedData = PackedData >> NumBitsPerAccelerationSample;
                            break;

                    }
                }


            }
			/* For Debugging */
            j = 1;


        }

        public class XYZ_AccelerationData {
            Double[] X_axisAccnData;
            Double[] Y_axisAccnData;
            Double[] Z_axisAccnData;

            public XYZ_AccelerationData() {
                X_axisAccnData = new Double[NUM_ACCN_SAMPLES];
                Y_axisAccnData = new Double[NUM_ACCN_SAMPLES];
                Z_axisAccnData = new Double[NUM_ACCN_SAMPLES];
            }
        }
    }

    @SuppressWarnings({"unused", "unused"})
    public class SummaryPacketInfo {
        private byte _SequenceNum;

        private int _TSYear;
        private byte _TSMonth;
        private byte _TSDay;
        private long _MsOfDay;
        private final byte VERSION_ONE = 1;
        private final byte VERSION_TWO = 2;
        private final double INVALID_CORE_TEMPERATURE = 6553.5;
        private byte _VersionNumber;
        private int _HeartRate;
        private double _RespirationRate;
        private double _SkinTemperature;
        private int _Posture;
        private double _Activity;
        private double _coreTemperature;
        private double _PeakAcceleration;
        private double _BatteryVoltage;
        private byte _BatteryStatus;
        private double _BreathingWaveAmpl;
        private double _BreathingWaveNoise;
        private byte _BreathingRateConfidence;
        private double _ECGAmplitude;
        private double _ECGNoise;
        private byte _HeartRateConfidence;
        private int _HRV;
        private byte _SystemConfidence;
        private int _GSR;
        private byte _ROGStatus;
        private short _ROGTime;
        private double _Vertical_AxisAccnMin;
        private double _Vertical_AxisAccnPeak;
        private double _Lateral_AxisAccnMin;
        private double _Lateral_AxisAccnPeak;
        private double _Sagittal_AxisAccnMin;
        private double _Sagittal_AxisAccnPeak;
        private double _Device_Internal_Temperature;
        private byte _Status_Worn_Det_Level;
        private byte _Status_Button_Press_Det_Flag;
        private byte _Status_Fitted_to_Garment_Flag;
        private byte _Status_Heart_Rate_Unreliable_Flag;
        private short _LinkQuality;
        private byte _RSSI;
        private short _TxPower;

        public byte GetSeqNum(byte[] Payload) {
            _SequenceNum = (byte) (Payload[0] & 0xFF);
            return _SequenceNum;
        }

        public int GetTSYear(byte[] Payload) {
            _TSYear = (Payload[1]) & (0xFF);
            _TSYear = _TSYear | (((Payload[2]) & 0xFF) << 8);
            return _TSYear;
        }

        public byte GetTSMonth(byte[] Payload) {
            _TSMonth = Payload[3];
            return _TSMonth;
        }

        public byte GetTSDay(byte[] Payload) {
            _TSDay = Payload[4];
            return _TSDay;
        }

        public long GetMsofDay(byte[] Payload) {
            _MsOfDay = 0;
            _MsOfDay = (Payload[5]) & (0xFF);
            _MsOfDay = _MsOfDay | (((Payload[6]) & (0xFF)) << 8);
            _MsOfDay = _MsOfDay | (((Payload[7]) & (0xFF)) << 16);
            _MsOfDay = _MsOfDay | (((Payload[8]) & (0xFF)) << 24);
            return _MsOfDay;
        }

        public byte GetVersionNumber(byte[] Payload) {
            _VersionNumber = (byte) ((Payload[9]) & (0xFF));
            return _VersionNumber;
        }

        public int GetHeartRate(byte[] Payload) {
            _HeartRate = (int) (Payload[10] & 0xFF);
            return _HeartRate;
        }

        public double GetRespirationRate(byte[] Payload) {
            short RespRate = 0;
            RespRate = (short) (((Payload[13]) & 0xFF) << 8);
            short RespRate1 = 0;
            RespRate1 = (short) ((Payload[12]) & 0xFF);
            _RespirationRate = ((double) ((RespRate) | (RespRate1)) / 10);
            return _RespirationRate;
        }

        public double GetSkinTemperature(byte[] Payload) {
            short SkinTemp = 0;
            SkinTemp = (short) (((Payload[15]) & 0xFF) << 8);
            SkinTemp = (short) (SkinTemp | ((Payload[14]) & 0xFF));
            _SkinTemperature = (double) SkinTemp / 10;
            return _SkinTemperature;
        }

        public int GetPosture(byte[] Payload) {
            short Posture = 0;
            Posture = (short) ((Payload[16]) & 0xFF);
            Posture = (short) ((Posture) | (((Payload[17]) & (0xFF)) << 8));
            _Posture = (int) Posture;
            return _Posture;
        }

        public double GetActivity(byte[] Payload) {
            short ActivityTemp = 0;
            ActivityTemp = (short) ((Payload[18]) & 0xFF);
            ActivityTemp = (short) ((ActivityTemp) | (((Payload[19]) & (0xFF)) << 8));
            _Activity = ((double) ActivityTemp / 100);
            return _Activity;
        }

        public double GetPeakAcceleration(byte[] Payload) {
            short PeakAcc = 0;
            PeakAcc = (short) ((Payload[20]) & 0xFF);
            PeakAcc = (short) ((PeakAcc) | (((Payload[21]) & (0xFF)) << 8));
            _PeakAcceleration = ((double) PeakAcc / 100);

            return _PeakAcceleration;
        }

        public double GetBatteryVoltage(byte[] Payload) {
            short BatteryVoltageTemp = 0;
            BatteryVoltageTemp = (short) ((Payload[22]) & 0xFF);
            BatteryVoltageTemp = (short) ((BatteryVoltageTemp) | (((Payload[23]) & (0xFF)) << 8));
            _BatteryVoltage = ((double) BatteryVoltageTemp / 1000);

            return _BatteryVoltage;
        }

        public byte GetBatteryLevel(byte[] Payload) {
            _BatteryStatus = (byte) (Payload[24] & (0x7F));
            return _BatteryStatus;
        }

        public double GetBreathingWaveAmplitude(byte[] Payload) {
            short BreathingWaveAmplTemp = 0;
            BreathingWaveAmplTemp = (short) ((Payload[25]) & 0xFF);
            BreathingWaveAmplTemp = (short) ((BreathingWaveAmplTemp) | (((Payload[26]) & (0xFF)) << 8));
            _BreathingWaveAmpl = ((double) BreathingWaveAmplTemp / 1000);

            return _BreathingWaveAmpl;
        }

        public double GetBreathingWaveAmpNoise(byte[] Payload) {
            short BreathingWaveNoiseTemp = 0;
            BreathingWaveNoiseTemp = (short) ((Payload[27]) & 0xFF);
            BreathingWaveNoiseTemp = (short) ((BreathingWaveNoiseTemp) | (((Payload[28]) & (0xFF)) << 8));
            _BreathingWaveNoise = ((double) BreathingWaveNoiseTemp / 1000);

            return _BreathingWaveNoise;
        }

        public byte GetBreathingRateConfidence(byte[] Payload) {
            _BreathingRateConfidence = (byte) (Payload[29] & 0xFF);
            return _BreathingRateConfidence;
        }

        public double GetECGAmplitude(byte[] Payload) {
            short ECGAmplitudeTemp = 0;
            ECGAmplitudeTemp = (short) ((Payload[30]) & 0xFF);
            ECGAmplitudeTemp = (short) ((ECGAmplitudeTemp) | (((Payload[31]) & (0xFF)) << 8));
            _ECGAmplitude = ((double) ECGAmplitudeTemp / 1000000);

            return _ECGAmplitude;
        }

        public double GetECGNoise(byte[] Payload) {
            short ECGNoiseTemp = 0;
            ECGNoiseTemp = (short) ((Payload[32]) & 0xFF);
            ECGNoiseTemp = (short) ((ECGNoiseTemp) | (((Payload[33]) & (0xFF)) << 8));
            _ECGNoise = ((double) ECGNoiseTemp / 1000000);

            return _ECGNoise;
        }

        public byte GetHeartRateRateConfidence(byte[] Payload) {
            _HeartRateConfidence = (byte) (Payload[34] & 0xFF);
            return _HeartRateConfidence;
        }

        public int GetHearRateVariability(byte[] Payload) {
            int HRVTemp = 0;
            HRVTemp = ((Payload[35]) & 0xFF);
            HRVTemp = ((HRVTemp) | (((Payload[36]) & (0xFF)) << 8));
            _HRV = HRVTemp;

            return _HRV;
        }

        public byte GetSystemConfidence(byte[] Payload) {
            _SystemConfidence = (byte) (Payload[37] & 0xFF);
            return _SystemConfidence;
        }

        public int GetGSR(byte[] Payload) {
            short GSRTemp = 0;
            GSRTemp = (short) ((Payload[38]) & 0xFF);
            GSRTemp = (short) ((GSRTemp) | (((Payload[39]) & (0xFF)) << 8));
            _GSR = (int) GSRTemp;

            return _GSR;
        }

        public byte GetROGStatus(byte[] Payload) {
            short ROGStatusTemp = 0;
            ROGStatusTemp = (short) ((Payload[40]) & 0xFF);
            ROGStatusTemp = (short) ((ROGStatusTemp) | (((Payload[41]) & (0xFF)) << 8));
            _ROGStatus = (byte) (ROGStatusTemp & 0x7);

            return _ROGStatus;
        }

        public int GetROGTime(byte[] Payload) {
            short ROGStatusTemp = 0;
            ROGStatusTemp = (short) ((Payload[40]) & 0xFF);
            ROGStatusTemp = (short) ((ROGStatusTemp) | (((Payload[41]) & (0xFF)) << 8));
            _ROGTime = (short) ((ROGStatusTemp & 0xFFF8) >> 3);

            return _ROGTime;
        }

        public double GetVertical_AxisAccnMin(byte[] Payload) {
            short Vertical_AxisAccnMinTemp = 0;
            Vertical_AxisAccnMinTemp = (short) ((Payload[42]) & 0xFF);
            Vertical_AxisAccnMinTemp = (short) ((Vertical_AxisAccnMinTemp) | (((Payload[43]) & (0xFF)) << 8));
            _Vertical_AxisAccnMin = ((double) Vertical_AxisAccnMinTemp / 100);

            return _Vertical_AxisAccnMin;
        }

        public double GetVertical_AxisAccnPeak(byte[] Payload) {
            short Vertical_AxisAccnPeakTemp = 0;
            Vertical_AxisAccnPeakTemp = (short) ((Payload[44]) & 0xFF);
            Vertical_AxisAccnPeakTemp = (short) ((Vertical_AxisAccnPeakTemp) | (((Payload[45]) & (0xFF)) << 8));
            _Vertical_AxisAccnPeak = ((double) Vertical_AxisAccnPeakTemp / 100);

            return _Vertical_AxisAccnPeak;
        }

        public double GetLateral_AxisAccnMin(byte[] Payload) {
            short Lateral_AxisAccnMinTemp = 0;
            Lateral_AxisAccnMinTemp = (short) ((Payload[46]) & 0xFF);
            Lateral_AxisAccnMinTemp = (short) ((Lateral_AxisAccnMinTemp) | (((Payload[47]) & (0xFF)) << 8));
            _Lateral_AxisAccnMin = ((double) Lateral_AxisAccnMinTemp / 100);

            return _Lateral_AxisAccnMin;
        }

        public double GetLateral_AxisAccnPeak(byte[] Payload) {
            short Lateral_AxisAccnPeakTemp = 0;
            Lateral_AxisAccnPeakTemp = (short) ((Payload[48]) & 0xFF);
            Lateral_AxisAccnPeakTemp = (short) ((Lateral_AxisAccnPeakTemp) | (((Payload[49]) & (0xFF)) << 8));
            _Lateral_AxisAccnPeak = ((double) Lateral_AxisAccnPeakTemp / 100);

            return _Lateral_AxisAccnPeak;
        }

        public double GetSagittal_AxisAccnMin(byte[] Payload) {
            short Sagittal_AxisAccnMinTemp = 0;
            Sagittal_AxisAccnMinTemp = (short) ((Payload[50]) & 0xFF);
            Sagittal_AxisAccnMinTemp = (short) ((Sagittal_AxisAccnMinTemp) | (((Payload[51]) & (0xFF)) << 8));
            _Sagittal_AxisAccnMin = ((double) Sagittal_AxisAccnMinTemp / 100);

            return _Sagittal_AxisAccnMin;
        }

        public double GetSagittal_AxisAccnPeak(byte[] Payload) {
            short Sagittal_AxisAccnPeakTemp = 0;
            Sagittal_AxisAccnPeakTemp = (short) ((Payload[52]) & 0xFF);
            Sagittal_AxisAccnPeakTemp = (short) ((Sagittal_AxisAccnPeakTemp) | (((Payload[53]) & (0xFF)) << 8));
            _Sagittal_AxisAccnPeak = ((double) Sagittal_AxisAccnPeakTemp / 100);

            return _Sagittal_AxisAccnPeak;
        }

        public double GetDevice_Internal_Temperature(byte[] Payload) {
            short Device_Internal_TemperatureTemp = 0;
            Device_Internal_TemperatureTemp = (short) ((Payload[54]) & 0xFF);
            Device_Internal_TemperatureTemp = (short) ((Device_Internal_TemperatureTemp) | (((Payload[55]) & (0xFF)) << 8));
            _Device_Internal_Temperature = ((double) Device_Internal_TemperatureTemp / 10);

            return _Device_Internal_Temperature;
        }

        private byte Get_Status_WornDet_Level(byte[] Payload) {

            _Status_Worn_Det_Level = (byte) ((Payload[56]) & 0x3);
            return _Status_Worn_Det_Level;
        }

        private byte GetStatus_Button_Press_Det_Flag(byte[] Payload) {
            _Status_Button_Press_Det_Flag = (byte) (((Payload[56]) & 0x4) >> 2);
            return _Status_Button_Press_Det_Flag;
        }

        private byte GetStatus_Fitted_to_Garment_Flag(byte[] Payload) {
            _Status_Fitted_to_Garment_Flag = (byte) (((Payload[56]) & 0x8) >> 3);
            return _Status_Fitted_to_Garment_Flag;
        }

        private byte GetStatus_Heart_Rate_Unreliable_Flag(byte[] Payload) {
            _Status_Heart_Rate_Unreliable_Flag = (byte) (((Payload[56]) & 0x10) >> 4);
            return _Status_Heart_Rate_Unreliable_Flag;
        }

        private short GetLinkQuality(byte[] Payload) {
            _LinkQuality = (short) (Payload[58] & 0xFF);
            return _LinkQuality;
        }

        private byte GetRSSI(byte[] Payload) {
            _RSSI = (byte) (Payload[59] & 0xFF);
            return _RSSI;
        }

        private short GetTxPower(byte[] Payload) {
            _TxPower = (short) (Payload[60] & 0xFF);
            return _TxPower;
        }

        public double GetCoreTemperature(byte[] Payload) {
            byte versionNum = GetVersionNumber(Payload);
            if (versionNum > VERSION_ONE) {
                short coreTemp = 0;
                coreTemp = (short) ((Payload[61]) & 0xFF);
                coreTemp = (short) ((coreTemp) | (((Payload[62]) & (0xFF)) << 8));
                _coreTemperature = (double) ((coreTemp) / 10);

            } else {
                _coreTemperature = INVALID_CORE_TEMPERATURE;
            }
            return _coreTemperature;
        }

    }

    public class EventPacketInfo {
        private byte _SequenceNum;

        private int _TSYear;
        private byte _TSMonth;
        private byte _TSDay;
        private long _MsOfDay;
        private short _EventCode;
        private byte[] _EventSpecificData;

        public EventPacketInfo(byte NumBytesEventSpData) {
            _EventSpecificData = new byte[NumBytesEventSpData];
        }

        public byte GetSeqNum(byte[] Payload) {
            _SequenceNum = (byte) (Payload[0] & 0xFF);
            return _SequenceNum;
        }

        public int GetTSYear(byte[] Payload) {
            _TSYear = (Payload[1]) & (0xFF);
            _TSYear = _TSYear | (((Payload[2]) & 0xFF) << 8);
            return _TSYear;
        }

        public byte GetTSMonth(byte[] Payload) {
            _TSMonth = Payload[3];
            return _TSMonth;
        }

        public byte GetTSDay(byte[] Payload) {
            _TSDay = Payload[4];
            return _TSDay;
        }

        public long GetMsofDay(byte[] Payload) {
            _MsOfDay = 0;
            _MsOfDay = (Payload[5]) & (0xFF);
            _MsOfDay = _MsOfDay | (((Payload[6]) & (0xFF)) << 8);
            _MsOfDay = _MsOfDay | (((Payload[7]) & (0xFF)) << 16);
            _MsOfDay = _MsOfDay | (((Payload[8]) & (0xFF)) << 24);
            return _MsOfDay;
        }

        public short GetEventCode(byte[] Payload) {
            short EventCodeTemp = 0;
            EventCodeTemp = (short) ((Payload[9]) & 0xFF);
            EventCodeTemp = (short) ((EventCodeTemp) | (((Payload[10]) & (0xFF)) << 8));
            _EventCode = EventCodeTemp;
            return _EventCode;
        }

        public byte[] GetEventSpecificData(byte[] Payload) {

            System.arraycopy(Payload, 11, _EventSpecificData, 0, _EventSpecificData.length);
            return _EventSpecificData;
        }
    }

}
