public class testingstuff {
    public static void main(String[] args) {
        byte[] fourBytes = getFourBytes(1.0f);
        System.out.println(getFloatFromFourBytes(fourBytes[0], fourBytes[1], fourBytes[2], fourBytes[3]));
    }
    public static int getIntFromFourBytes(byte b0, byte b1, byte b2, byte b3){
        return Byte.toUnsignedInt(b0) + (Byte.toUnsignedInt(b1) * 256) + (Byte.toUnsignedInt(b2) * 65536) + (Byte.toUnsignedInt(b3) * 16777216);
    }
    public static float getFloatFromFourBytes(byte b0, byte b1, byte b2, byte b3){
        return Float.intBitsToFloat(getIntFromFourBytes(b0, b1, b2, b3));
    }
    private static byte[] getFourBytes(float f){
        int floatInt = Float.floatToIntBits(f);
        return getFourBytes(floatInt);
    }
    private static byte[] getFourBytes(int i){
        return new byte[]{(byte)i, (byte)(i>>8), (byte)(i>>16), (byte)(i>>24)};
    }
}
