package game.misc;

import game.data.nbt.NBTElement;

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
                        put(valueBytes); //the value of the element (valueBytes.length bytes)
    }
}
