package game.misc;

import game.data.nbt.NBTElement;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class StaticUtils {
    public static Vector3i getChunkPos(Vector3f worldPos){
        return new Vector3i((int)(worldPos.x/18.4752086141), (int)(worldPos.y/32.), (int)(worldPos.z/32.));
    }

    public static Vector3f getWorldPos(Vector3i chunkPos){
        return new Vector3f(chunkPos.x*18.4752086141f, chunkPos.y*32f, chunkPos.z*32f);
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

    public static ByteBuffer getNBTSerialData(NBTElement element, byte[] valueBytes){
        byte[] name = element.getName().getBytes(StandardCharsets.UTF_8);
        int size = 4 + 1 + name.length+1 + valueBytes.length;
        return ByteBuffer.allocate(size).
                putInt(size). //the length of the element in bytes (4 bytes)
                put(element.getType()). //the type of the element (1 byte)
                put(name).put((byte) 0). //the name of the element and the null terminator (name.length+1 bytes)
                put(valueBytes); //the value of the element (valueBytes.length bytes)
    }

    public static ByteBuffer getNBTSerialData(NBTElement element, ByteBuffer valueBytes){
        byte[] name = element.getName().getBytes(StandardCharsets.UTF_8);
        int size = 4 + 1 + name.length+1 + valueBytes.capacity();
        return ByteBuffer.allocate(size).
                putInt(size). //the length of the element in bytes (4 bytes)
                        put(element.getType()). //the type of the element (1 byte)
                        put(name).put((byte) 0). //the name of the element and the null terminator (name.length+1 bytes)
                        put(valueBytes.flip()); //the value of the element (valueBytes.length bytes)
    }
    public static double getDistance(Vector3f a, Vector3f b){
        return Math.sqrt(
                ((a.x - b.x)*(a.x - b.x))
                +((a.y - b.y)*(a.y - b.y))
                +((a.z - b.z)*(a.z - b.z))
                );
    }

    public static String betterVectorToString(Vector3f vec){
        int stringLength = 5;
        StringBuilder b = new StringBuilder();
        String x = Float.toString(vec.x);
        String y = Float.toString(vec.y);
        String z = Float.toString(vec.z);
        if(x.length() > stringLength){
            b.append(x.substring(0, stringLength));
        } else {
            b.append(x);
        }
        b.append(", ");
        if(y.length() > stringLength){
            b.append(y.substring(0, stringLength));
        } else {
            b.append(y);
        }
        b.append(", ");
        if(z.length() > stringLength){
            b.append(z.substring(0, stringLength));
        } else {
            b.append(z);
        }
        return b.toString();

    }

    public static String betterFloatToString(float f, int sigFigs){
        int ceilLogf = (int)Math.ceil(Math.log10(f));
        if(ceilLogf > sigFigs) { //if the number of integer digits is greater than the number of significant digits
            //scientific notation
            int mantissa = (int) Math.round(f / Math.pow(10, (ceilLogf-sigFigs))); //get the first sigFigs digits
            // by shifting it by log(f) - sigFigs digits to the right, then casting to an integer
            String mantissaString = Integer.toString(mantissa);
            return mantissaString.charAt(0) + "." + mantissaString.substring(1, sigFigs) + "E" + ceilLogf;
        } else {
            //decimal notation
            return Float.toString(f); //todo: actually implement this
        }
    }

    public static void main(String[] args) {
        System.out.println(betterFloatToString(297565.565f, 5));
        System.out.println(betterFloatToString(29756172855.565f, 5));
        System.out.println(betterFloatToString(297565.565f, 2));
        System.out.println(betterFloatToString(2975.565f, 1));

    }
}
