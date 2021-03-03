package br.pucrio.inf.lac.mhub.s2pa.technologies.bt.sensors;

import java.util.UUID;

import br.pucrio.inf.lac.mhub.s2pa.base.TechnologySensorExtended;

public abstract class HXM021144_AbstractSensor implements TechnologySensorExtended {

    protected final int HR_SPD_DIST_PACKET = 0x26;

    private String name = null;

    private HRSpeedDistPacketInfo hRSpeedDistPacketInfo = null;

    /*     */
/*     */
    public HXM021144_AbstractSensor() {
    }

    public HXM021144_AbstractSensor(String name) {
        this.name = name;
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
    public abstract Double[] convert(byte[] bytes);


    protected HRSpeedDistPacketInfo getHRSpeedDistPacketInfo() {
        if (hRSpeedDistPacketInfo == null) {
            hRSpeedDistPacketInfo = new HRSpeedDistPacketInfo();
        }
        return hRSpeedDistPacketInfo;
    }


    protected class HRSpeedDistPacketInfo {

        private String FirmwareID;
        private String FirmwareVersion;
        private String HardwareID;
        private String HardwareVersion;
        private byte BatteryChargeInd;
        private byte HeartRate;
        private byte HeartBeatNum;


        /*     */
/* 113 */
        public String GetFirmwareID(byte[] DataPacket) {
            short FirmwareID_Temp;
/* 114 */
            FirmwareID_Temp = (short) (DataPacket[0] & 0xFF);
/* 115 */
            FirmwareID_Temp = (short) (FirmwareID_Temp | (DataPacket[1] & 0xFF) << 8);
/*     */       
/* 117 */
            this.FirmwareID = String.valueOf(FirmwareID_Temp);
/* 118 */
            return this.FirmwareID;
/*     */
        }

        /*     */
/*     */ 
/*     */
        public String GetFirmwareVersion(byte[] DataPacket)
/*     */ {
/* 124 */
            short FirmwareVersion_Temp = 0;
/* 125 */
            FirmwareVersion_Temp = (short) (DataPacket[2] & 0xFF);
/*     */       
/*     */ 
/*     */ 
/* 129 */
            char d = (char) DataPacket[2];
/* 130 */
            char e = (char) DataPacket[3];
/* 131 */
            StringBuilder s = new StringBuilder(2);
/* 132 */
            this.FirmwareVersion = String.valueOf((d + e));
/* 133 */
            return this.FirmwareVersion;
/*     */
        }

        /*     */
/*     */ 
/*     */
        public String GetHardwareID(byte[] DataPacket)
/*     */ {
/* 139 */
            short HardwareID_Temp;
/* 140 */
            HardwareID_Temp = (short) (DataPacket[4] & 0xFF);
/* 141 */
            HardwareID_Temp = (short) (HardwareID_Temp | (DataPacket[5] & 0xFF) << 8);
/*     */       
/* 143 */
            this.HardwareID = String.valueOf(HardwareID_Temp & 0xFF);
/* 144 */
            return this.HardwareID;
/*     */
        }

        /*     */
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */
        public String GetHardwareVersion(byte[] DataPacket)
/*     */ {
/* 154 */
            char d = (char) DataPacket[6];
/* 155 */
            char e = (char) DataPacket[7];
/* 156 */
            StringBuilder s = new StringBuilder(2);
/* 157 */
            this.HardwareVersion = String.valueOf((d + e));
/* 158 */
            return this.HardwareVersion;
/*     */
        }

        /*     */
/*     */
        public byte GetBatteryChargeInd(byte[] DataPacket)
/*     */ {
/* 163 */
            this.BatteryChargeInd = ((byte) (DataPacket[8] & 0xFF));
/* 164 */
            return this.BatteryChargeInd;
/*     */
        }

        /*     */
/*     */ 
/*     */
        public byte GetHeartRate(byte[] DataPacket)
/*     */ {

/* 170 */
            this.HeartRate = ((byte) (DataPacket[9] & 0xFF));
/* 171 */
            return this.HeartRate;
				
/*     */
        }

        /*     */
/*     */
        public byte GetHeartBeatNum(byte[] DataPacket)
/*     */ {
/* 176 */
            this.HeartBeatNum = ((byte) (DataPacket[10] & 0xFF));
/* 177 */
            return this.HeartBeatNum;
        }

        /*     */
/* 179 */     private int[] HeartBeatTS = new int[15];
        /*     */     private double Distance;

        /*     */
/* 182 */
        public int[] GetHeartBeatTS(byte[] DataPacket) {
            byte[] TempTS = new byte[30];
/* 183 */
            System.arraycopy(DataPacket, 11, TempTS, 0, 30);
/*     */       
/* 185 */
            int k = 0;
/* 186 */
            for (byte i = 0; i < this.HeartBeatTS.length; i = (byte) (i + 1)) {
/* 187 */
                int Temp = DataPacket[(11 + k)] & 0xFF;
/* 188 */
                Temp |= (DataPacket[(11 + k + 1)] & 0xFF) << 8;
/* 189 */
                this.HeartBeatTS[i] = Temp;
/* 190 */
                k += 2;
/*     */
            }
/* 192 */
            return this.HeartBeatTS;
/*     */
        }

        /*     */
/*     */     private double InstantSpeed;
        /*     */     private byte Strides;
        /*     */     private double Cadence;

        /*     */
        public double GetDistance(byte[] DataPacket) {
/* 199 */
            short Distance_Temp;
/* 200 */
            Distance_Temp = (short) (DataPacket[47] & 0xFF);
/* 201 */
            Distance_Temp = (short) (Distance_Temp | (DataPacket[48] & 0xFF) << 8);
/* 202 */
            this.Distance = (Distance_Temp / 16.0D);
/* 203 */
            return this.Distance;
/*     */
        }

        /*     */
/*     */ 
/*     */
        protected double GetInstantSpeed(byte[] DataPacket)
/*     */ {
/* 209 */
            short InstantSpeed_Temp;
/* 210 */
            InstantSpeed_Temp = (short) (DataPacket[49] & 0xFF);
/* 211 */
            InstantSpeed_Temp = (short) (InstantSpeed_Temp | (DataPacket[50] & 0xFF) << 8);
/* 212 */
            this.InstantSpeed = (InstantSpeed_Temp / 256.0D);
/* 213 */
            return this.InstantSpeed;
/*     */
        }

        /*     */
/*     */
        protected byte GetStrides(byte[] DataPacket)
/*     */ {
/* 218 */
            this.Strides = ((byte) (DataPacket[51] & 0xFF));
/* 219 */
            return this.Strides;
/*     */
        }

        /*     */
/*     */
        protected double GetCadence(byte[] DataPacket)
/*     */ {
/* 224 */
            short Cadence_Temp;
/* 225 */
            Cadence_Temp = (short) (DataPacket[53] & 0xFF);
/* 226 */
            Cadence_Temp = (short) (Cadence_Temp | (DataPacket[54] & 0xFF) << 8);
/* 227 */
            this.Cadence = (Cadence_Temp / 16.0D);
/* 228 */
            return this.Cadence;
/*     */
        }
/*     */
    }

}

/*     */ 
