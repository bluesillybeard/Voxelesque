package game.misc;

import game.data.nbt.NBTElement;
import game.world.World;
import org.joml.Vector3f;
import org.joml.Vector3i;


import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;

public class StaticUtils {

    //if I were using C or C++ I could use stack allocated objects, but Java has no such thing so in order to save memory I gotta do this madness.
    //I'm using Java because it's a much better programming experience - soooo much easier than C or C++.
    private static final Vector3f[] tempsf = new Vector3f[]{new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f(), };
    private static final Vector3i[] tempsi = new Vector3i[]{new Vector3i(), new Vector3i(), new Vector3i(), new Vector3i(), new Vector3i(), new Vector3i(), new Vector3i(), new Vector3i(), };
    private static int tempfIndex;
    private static int tempiIndex;

    /**
     * converts a world position into a chunk position.
     * @param worldPos the world pos to get the chunk pos of.
     * @return the chunk pos. Note: this value may change unexpectedly, it is recommended to create a new copy of it if it will be needed for something later.
     */
    public static Vector3i getChunkPos(Vector3f worldPos){
        return tempsi[tempiIndex = (tempiIndex+1)%tempsi.length].set((int)Math.round(worldPos.x/(World.CHUNK_SIZE*0.288675134595)-0.5), (int)Math.round(worldPos.y/(World.CHUNK_SIZE*0.5)-0.5), (int)Math.round(worldPos.z/(World.CHUNK_SIZE*0.5)-0.5));
    }

    /**
     * gets the world pos of the center of a chunk, given its pos.
     * @param chunkPos the chunk pos to find the world pos of.
     * @return the world pos of the chunk. Note: this value may change unexpectedly, it is recommended to create a new copy of it if it will be needed for something later.
     */
    public static Vector3f getChunkWorldPos(Vector3i chunkPos){
        return tempsf[tempfIndex = (tempfIndex+1)%tempsf.length].set((chunkPos.x+0.5f)*(World.CHUNK_SIZE*0.288675134595f), (chunkPos.y+0.5f)*(World.CHUNK_SIZE*0.5f), (chunkPos.z+0.5f)*(World.CHUNK_SIZE*0.5f));
    }

    /**
     * gets the world pos of the center of a chunk, given its pos.
     * @return the world pos of the chunk. Note: this value may change unexpectedly, it is recommended to create a new copy of it if it will be needed for something later.
     */
    public static Vector3f getChunkWorldPos(int x, int y, int z){
        return tempsf[tempfIndex = (tempfIndex+1)%tempsf.length].set((x+0.5f)*(World.CHUNK_SIZE*0.288675134595f), (y+0.5f)*(World.CHUNK_SIZE*0.5f), (z+0.5f)*(World.CHUNK_SIZE*0.5f));
    }

    /**
     * gets the block position given a world position.
     * @param worldPos the world position to get the block position from
     * @return the block position. Note: this value may change unexpectedly, it is recommended to create a new copy of it if it will be needed for something later.
     */
    public static Vector3i getBlockPos(Vector3f worldPos){
        return tempsi[tempiIndex = (tempiIndex+1)%tempsi.length].set((int)Math.round(worldPos.x/0.288675134595-0.5), (int)Math.round(worldPos.y/0.5-0.5), (int)Math.round(worldPos.z/0.5-0.5));
    }

    /**
     * gets the world position of the center of a block, given its block position.
     * @param blockPos the block position
     * @return the world position of that block. Note: this value may change unexpectedly, it is recommended to create a new copy of it if it will be needed for something later.
     */
    public static Vector3f getBlockWorldPos(Vector3i blockPos){
        return tempsf[tempfIndex = (tempfIndex+1)%tempsf.length].set((blockPos.x+0.5f)*0.288675134595f, (blockPos.y+0.5f)*0.5f, (blockPos.z+0.5f)*0.5f);
    }

    /**
     * gets the world position of the center of a block, given its block position.
     * @return the world position of that block. Note: this value may change unexpectedly, it is recommended to create a new copy of it if it will be needed for something later.
     */
    public static Vector3f getBlockWorldPos(int x, int y, int z){
        return tempsf[tempfIndex = (tempfIndex+1)%tempsf.length].set((x+0.5f)*0.288675134595f, (y+0.5f)*0.5f, (z+0.5f)*0.5f);
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

        /*
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
            double log10 = Math.log10(f%1);
            long decimal = Math.abs(Math.round(f % 1 * Math.pow(10, sigFigs - intPartString.length())));
            if(log10 < 1) {
                return intPartString + "." + decimal;
            } else {
                int zeros = (int)(-log10)+1;
                return intPartString + "." + "0".repeat(zeros) + decimal; //add the required number of 0s
            }
        }

         */
        //todo (unimportant) re-implement this function to actually work. Old code is commented above.

        //As a "temporary" solution, use a NumberFormat. Doesn't use scientific notation, but it works for now.
        int digits = (int)Math.ceil(Math.log10(f)); //the number of integer digits in the number.

        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(sigFigs-digits);
        return format.format(f);
    }
}
