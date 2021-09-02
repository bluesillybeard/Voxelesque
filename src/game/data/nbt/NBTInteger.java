package game.data.nbt;

import game.misc.StaticUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class NBTInteger implements NBTElement{
    private int value;
    private String name;

    public NBTInteger(String name, int value){
        this.name = name;
        this.value = value;
    }
    public NBTInteger(byte[] serializedData) throws InstantiationException {
        if(serializedData[4] != NBT_ELEMENT_TYPE_INT)
            throw new InstantiationException("Cannot use data for type " + serializedData[4] + " to create type " + NBT_ELEMENT_TYPE_INT + ".");
        int size = StaticUtils.getIntFromFourBytes(serializedData[3], serializedData[2], serializedData[1], serializedData[0]);

        this.name = new String(serializedData, 5, size-10);
        this.value = StaticUtils.getIntFromFourBytes(serializedData[size-4], serializedData[size-3], serializedData[size-2], serializedData[size-1]);
    }
    @Override
    public byte getType(){
        return NBT_ELEMENT_TYPE_INT;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Object getAsObject() {
        return value;
    }

    @Override
    public byte[] serialize() {
        byte[] valueBytes = StaticUtils.getFourBytes(value);
        ByteBuffer out = StaticUtils.getNBTSerialData(this, valueBytes);
        if(out.hasArray()) {
            return out.array();
        } else {
            throw new IllegalStateException("ByteBuffer.allocate() returned a non-array buffer! Try using a different JRE.");
        }    }

    public int getValue(){
        return value;
    }

    public void setValue(int value){
        this.value = value;
    }

    public String toString(){
        return name + ":" + value;
    }
}
