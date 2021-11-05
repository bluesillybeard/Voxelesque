package game.misc;

import game.data.nbt.NBTElement;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class StaticUtils {
    public static Vector3i getChunkPos(Vector3f worldPos){
        return new Vector3i((int)Math.round(worldPos.x/18.4752086141-0.5), (int)Math.round(worldPos.y/32.-0.5), (int)Math.round(worldPos.z/32.-0.5));
    }

    public static Vector3f getWorldPos(Vector3i chunkPos){
        return new Vector3f(chunkPos.x*18.4752086141f, chunkPos.y*32f, chunkPos.z*32f+16f);
    }

    public static byte[] getFourBytes(int i){
        return new byte[]{(byte)i, (byte)(i>>8), (byte)(i>>16), (byte)(i>>24)};
    }

    public static byte[] getFourBytes(float f){
        int floatInt = Float.floatToIntBits(f);
        return getFourBytes(floatInt);
    }
    public static int getIntFromFourBytes(byte b0, byte b1, byte b2, byte b3){
        return Byte.toUnsignedInt(b0) + (Byte.toUnsignedInt(b1) << 8) + (Byte.toUnsignedInt(b2) << 16) + (Byte.toUnsignedInt(b3) << 24);
    }
    public static float getFloatFromFourBytes(byte b0, byte b1, byte b2, byte b3){
        return Float.intBitsToFloat(getIntFromFourBytes(b0, b1, b2, b3));
    }

    public static ByteBuffer getNBTSerialData(NBTElement element, byte[] valueBytes){ //todo: find a better name for this function
        byte[] name = element.getName().getBytes(StandardCharsets.UTF_8);
        int size = 4 + 1 + name.length+1 + valueBytes.length;
        return ByteBuffer.allocate(size).
                putInt(size). //the length of the element in bytes (4 bytes)
                put(element.getType()). //the type of the element (1 byte)
                put(name).put((byte) 0). //the name of the element and the null terminator (name.length+1 bytes)
                put(valueBytes); //the value of the element (valueBytes.length bytes)
    }

    public static ByteBuffer getNBTSerialData(NBTElement element, ByteBuffer valueBytes){//todo: find a better name for this function
        byte[] name = element.getName().getBytes(StandardCharsets.UTF_8);
        int size = 4 + 1 + name.length+1 + valueBytes.capacity();
        return ByteBuffer.allocate(size).
                putInt(size). //the length of the element in bytes (4 bytes)
                        put(element.getType()). //the type of the element (1 byte)
                        put(name).put((byte) 0). //the name of the element and the null terminator (name.length+1 bytes)
                        put(valueBytes.flip()); //the value of the element (valueBytes.length bytes)
    }

    public static String betterVectorToString(Vector3f vec, int sigFigs){
        String x = FloatToStringSigFigs(vec.x, sigFigs);
        String y = FloatToStringSigFigs(vec.y, sigFigs);
        String z = FloatToStringSigFigs(vec.z, sigFigs);
        return "(" + x + ", " + y + ", " + z + ")";

    }

    /**
     * Similar to Float.toString, except this method allows you to specific a number of digits.
     * @param f the float to create the string
     * @param sigFigs the number of significant figures or digits to use
     * @return a String that represents f
     */
    public static String FloatToStringSigFigs(float f, int sigFigs){
        //uses simple mathematical concepts way to convert a float into a string.
        int ceilLogf = (int)Math.ceil(Math.log10(f)); //the number of integer digits in the number.
        if(ceilLogf > sigFigs) { //if the number of integer digits is greater than the number of significant digits
            //scientific notation

            String mantissaString = Integer.toString((int) Math.round(f / Math.pow(10, (ceilLogf-sigFigs))));
            //get the first sigFigs digits of the number
            // by shifting it by log(f) - sigFigs digits to the right, then casting to an integer
            return mantissaString.charAt(0) + "." + mantissaString.substring(1, sigFigs) + "E" + (ceilLogf-1);
        } else {
            //decimal notation
            String intPartString = Integer.toString((int)f);
            return intPartString + "." + (int) Math.abs(Math.round(f % 1 * Math.pow(10, sigFigs - intPartString.length())));
        }
    }
}
