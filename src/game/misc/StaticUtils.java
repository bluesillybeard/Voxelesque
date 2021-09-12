package game.misc;

import game.data.nbt.NBTElement;
import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class StaticUtils {
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
}
